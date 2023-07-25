package com.leyou.search.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.leyou.client.item.ItemClient;
import com.leyou.common.pojo.PageResult;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.HighlightUtils;
import com.leyou.dto.search.GoodsDto;
import com.leyou.dto.search.GoodsSearchResult;
import com.leyou.pojo.item.Brand;
import com.leyou.pojo.item.Category;
import com.leyou.pojo.item.SpecParam;
import com.leyou.pojo.search.GoodSearchRequest;
import com.leyou.pojo.search.GoodsSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 创建时间：2020/12/19
 *
 * @author wpf
 */
@Service
public class GoodDataSearchService {

    @Autowired
    private ElasticsearchTemplate searchTemplate;
    @Autowired
    private ItemClient itemClient;

    /**
     * 用关键词搜索商品并分页的业务类
     * @param goodSearchRequest 接收请求参数的对象，包括关键词和当前页码
     * @return 搜索到的结果对象
     *
     * 错误查询命令(只适用于单一条件查询)
     * GET goods-index/_search
     * {
     *   "query": {
     *     "bool": {
     *       "must": [
     *         {
     *           "match": {
     *             "allForSearch": "手机"
     *           }
     *         }
     *       ]
     *     }
     *   },
     *   "highlight": {
     *     "pre_tags": ["<font style='color:red'>"],
     *     "post_tags": ["</font>"],
     *     "fields": {
     *       "allForSearch": {}
     *     }
     *   }
     * }
     *
     * 真实查询指令
     * GET goods-index/_search
     * {
     *   "query": {
     *     "bool": {
     *       "must": [
     *         {
     *           "multi_match": {
     *             "query": "手机",
     *             "fields": [
     *               "allForSearch",
     *               "spuName"
     *               ]
     *           }
     *         }
     *       ]
     *     }
     *   },
     *   "highlight": {
     *     "pre_tags": ["<font style='color:red'>"],
     *     "post_tags": ["</font>"],
     *     "fields": {
     *       "allForSearch": {}
     *     }
     *   }
     * }
     *
     * 添加聚合查询后的真是查询指令：
     * GET goods-index/_search
     * {
     *   "query": {
     *     "bool": {
     *       "must": [
     *         {
     *           "multi_match": {
     *             "query": "手机",
     *             "fields": [
     *               "allForSearch",
     *               "spuName"
     *               ]
     *           }
     *         }
     *       ]
     *     }
     *   },
     *   "highlight": {
     *     "pre_tags": ["<font style='color:red'>"],
     *     "post_tags": ["</font>"],
     *     "fields": {
     *       "allForSearch": {}
     *     }
     *   },
     *   "aggs": {
     *     "categoryAgg": {
     *       "terms": {
     *         "field": "categoryId",
     *         "size": 10
     *       }
     *     },
     *     "brandAgg": {
     *       "terms": {
     *         "field": "brandId",
     *         "size": 10
     *       }
     *     }
     *     //这里后面还需要根据特有参数的值来动态增加过滤条件
     *   }
     * }
     *
     */
    public GoodsSearchResult<GoodsDto> searchGoodsByKeyWordAndPage(GoodSearchRequest goodSearchRequest) {
        //创建返回值的对象
        GoodsSearchResult<GoodsDto> searchResult = new GoodsSearchResult<>();

        //创建本地查询构建器
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        //获取构造商品分页列表查询参数后的构造器
        searchQueryBuilder = this.getGoodsPageQueryBuilder(goodSearchRequest, searchQueryBuilder);
//        PageResult<GoodsDto> pageResult = this.queryGoodsPage(goodSearchRequest, searchQueryBuilder);
        //定义品牌和类别的聚合查询别名
        String[] aggregationQueryName = {"categoryAgg", "brandAgg"};
        //获取构造过滤条件聚合查询参数后的构造器
        searchQueryBuilder = this.getFilterConditionsQueryBuilder(goodSearchRequest, searchQueryBuilder, aggregationQueryName);

        //获得查询结果(需要通过接口设置高亮字段)
        AggregatedPage<GoodsSearch> aggregatedPage =
                searchTemplate.queryForPage(searchQueryBuilder.build(), GoodsSearch.class,
                        HighlightUtils.highlightBody(GoodsSearch.class, "spuName"));
        List<GoodsSearch> goodsSearchList = aggregatedPage.getContent();

        //处理结果，拷贝对象的属性值
        List<GoodsDto> goodsDtoList = BeanHelper.copyWithCollection(goodsSearchList, GoodsDto.class);
        //根据分类和品牌的过滤结果来查询特有参数并进一步进行聚合查询，获取特有参数的分类map集合
        Map<String, Object> filterConditions = this.getFilterConditionsMapFromResult(
                aggregatedPage, aggregationQueryName, searchQueryBuilder);

        //构造分页对象封装数据
        PageResult<GoodsDto> pageResult = new PageResult<>();
        pageResult.setItems(goodsDtoList);
        pageResult.setTotal(aggregatedPage.getTotalElements());
        pageResult.setTotalPage(Long.valueOf(aggregatedPage.getTotalPages()));

        //封装返回值 (这里可以测试BeanUtils可否拷贝父类的属性值)
//        searchResult.setItems(pageResult.getItems());
//        searchResult.setTotal(pageResult.getTotal());
//        searchResult.setTotalPage(pageResult.getTotalPage());
        GoodsSearchResult<GoodsDto> goodsSearchResult = BeanHelper.copyProperties(pageResult, searchResult.getClass());

        goodsSearchResult.setFilterConditions(filterConditions);

        return goodsSearchResult;
    }

    /**
     * 获取添加了分页查询、布尔查询、高亮查询条件后的本地查询构造器
     * @param goodSearchRequest 前端传递的请求参数对象
     * @param searchQueryBuilder 处理前的本地查询构造器对象
     * @return 处理后的本地查询构造器对象
     */
    private NativeSearchQueryBuilder getGoodsPageQueryBuilder(GoodSearchRequest goodSearchRequest,
                                                NativeSearchQueryBuilder searchQueryBuilder) {
        //添加搜索结果的筛选条件
        searchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","spuName","subTitle","skuList"},null));

        /*
        创建布尔查询构建器
        各种查询构建器可以使用new的方法创建对象
        也可以使用QueryBuilders.xxxQuery或AggregationBuilders.xxxQuery的方式获取，其内部就是返回一个new出的对象，
        但使用QueryBuilders或AggregationBuilders方法创建对象会更加常用
         */
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        /*
        在布尔查询中追加must的查询条件
        QueryBuilders.matchQuery只适用于单一条件查询
        QueryBuilders.multiMatchQuery()才能进行多条件查询
         */
        boolQueryBuilder.must(QueryBuilders.multiMatchQuery(goodSearchRequest.getKey(),
                "allForSearch", "spuName"));

        //添加用户选择的过滤条件
        this.addFilterParmas(goodSearchRequest, boolQueryBuilder);

        //添加布尔查询条件
        searchQueryBuilder.withQuery(boolQueryBuilder);

        //添加分页查询条件
        searchQueryBuilder.withPageable(PageRequest.of(goodSearchRequest.getPage() - 1, goodSearchRequest.getPageSize()));

        //添加高亮查询条件
        HighlightUtils.highlightField(searchQueryBuilder, "spuName");


        return searchQueryBuilder;
    }

    /**
     * 向布尔查询构建器中添加过滤条件
     * @param goodSearchRequest 前端传递的请求参数对象
     * @param boolQueryBuilder 处理前的布尔查询构建器
     */
    private void addFilterParmas(GoodSearchRequest goodSearchRequest, BoolQueryBuilder boolQueryBuilder) {
        //获取过滤条件
        Map<String, Object> filterParams = goodSearchRequest.getFilterParams();
        //循环遍历循环条件的map集合
        filterParams.entrySet().forEach(entrySet -> {
            //根据分类、品牌和其他特有规格参数分别处理
            String key = entrySet.getKey();
            switch (key) {
                case "分类":
                    key = "categoryId";
                    break;
                case "品牌":
                    key = "brandId";
                    break;
                default:
                    key = "specParams." + key + ".keyword";
            }
            //添加过滤条件
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, entrySet.getValue()));
        } );
    }

    /**
     * 获取添加了聚合查询条件后的本地查询构造器
     * @param goodSearchRequest 前端传递的请求参数对象
     * @param searchQueryBuilder 处理前的本地查询构造器对象
     * @param aggregationQueryName 代表类别和品牌聚合查询别名的字符串数组
     * @return 处理后的本地查询构造器对象
     */
    private NativeSearchQueryBuilder getFilterConditionsQueryBuilder(GoodSearchRequest goodSearchRequest,
            NativeSearchQueryBuilder searchQueryBuilder, String[] aggregationQueryName) {

        //创建聚合查询构建对象
        TermsAggregationBuilder categoryTerms = AggregationBuilders.terms(aggregationQueryName[0]);
        TermsAggregationBuilder brandTerms = AggregationBuilders.terms(aggregationQueryName[1]);

        //向本地查询构建器中添加聚合查询条件
        searchQueryBuilder.addAggregation(categoryTerms.field("categoryId"));
        searchQueryBuilder.addAggregation(brandTerms.field("brandId"));

        return searchQueryBuilder;
    }

    /**
     * 根据分类过滤的结果获取特有参数，并进行进一步过滤，获取过滤后的特有参数map集合
     * @param aggregatedPage 查询的结果对象
     * @param aggregationQueryName 代表类别和品牌聚合查询别名的字符串数组
     * @param searchQueryBuilder 第一次查询过的本地查询构造器对象
     * @return 过滤条件的Map集合
     */
    private Map<String, Object> getFilterConditionsMapFromResult(
            AggregatedPage<GoodsSearch> aggregatedPage, String[] aggregationQueryName,
            NativeSearchQueryBuilder searchQueryBuilder) {
        //创建一个map集合，用于存放返回的结果
        Map<String, Object> filterConditionMap = new LinkedHashMap<>();

        //获取所有聚合条件的查询结果对象(并不是直接的结果)
        Aggregations aggregations = aggregatedPage.getAggregations();
        //根据别名获取对应的terms对象(真正的聚合查询结果)
//        Terms categoryAggregation = aggregations.get(aggregationQueryName[0]);
//        Terms brandAggregation = aggregations.get(aggregationQueryName[1]);

        //也可以直接根据别名获取对应的聚合查询结果对象(terms对象，Terms接口是Aggregation接口的子接口)
        Terms categoryAggregation = (Terms) aggregatedPage.getAggregation(aggregationQueryName[0]);
        Terms brandAggregation = (Terms) aggregatedPage.getAggregation(aggregationQueryName[1]);

        //获取bucket
        List<? extends Terms.Bucket> categoryBuckets = categoryAggregation.getBuckets();
        List<? extends Terms.Bucket> brandBuckets = brandAggregation.getBuckets();

        //利用stream流改造bucket对象，以获取需要的Id集合
        List<Long> categoryIds = categoryBuckets.stream().map(Terms.Bucket::getKeyAsNumber).map(Number::longValue).collect(Collectors.toList());
        List<Long> brandIds = brandBuckets.stream().map(Terms.Bucket::getKeyAsNumber).map(Number::longValue).collect(Collectors.toList());

        //调用商品服务的Feign接口，根据Id集合获取对应的Brand和Category集合
        List<Category> categoryList = itemClient.findCategoriesByIds(categoryIds);
        List<Brand> brandList = itemClient.findBrandsByIds(brandIds);

        //将数据存入map集合中
        filterConditionMap.put("分类", categoryList);
        filterConditionMap.put("品牌", brandList);

        //根据分类Id查询规格参数列表
        //遍历分类Id
        if (CollectionUtils.isNotEmpty(categoryIds)) {
            categoryIds.forEach(
                categoryId -> {
                    List<SpecParam> specParamList = itemClient.getSpecParam(null, categoryId, true);
                    //判空后往上一次查询过的本地查询构造器中加入聚合查询条件
                    if (CollectionUtils.isNotEmpty(specParamList)) {
                        specParamList.forEach(specParam -> {
                            searchQueryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName())
                            .field("specParams." + specParam.getName() + ".keyword"));
                        });
                    }
                }
            );
        }

        //第二次聚合查询，获取特有规格参数的聚合结果
        AggregatedPage<GoodsSearch> aggregatedPageForSpec = searchTemplate.queryForPage(searchQueryBuilder.build(), GoodsSearch.class);
        //获取聚合结果对象
        Aggregations aggregationsForSpec = aggregatedPageForSpec.getAggregations();

        //再次遍历分类Id，获取查询结果
        if (CollectionUtils.isNotEmpty(categoryIds)) {
            categoryIds.forEach(
                categoryId -> {
                    List<SpecParam> specParamList = itemClient.getSpecParam(null, categoryId, true);
                    //判空后根据规格参数名查询结果
                    if (CollectionUtils.isNotEmpty(specParamList)) {
                       specParamList.forEach(specParam -> {
                           Terms specTerms = aggregationsForSpec.get(specParam.getName());
                           //获得bucket
                           List<? extends Terms.Bucket> buckets = specTerms.getBuckets();
                           List<String> querySpecList = buckets.stream().
                                   map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());
                           //将查询后的集合放入到map中
                           filterConditionMap.put(specParam.getName(), querySpecList);
                       });
                    }
                }
            );
        }

        return filterConditionMap;
    }

    /**
     * 仅仅进行分页后内容查询，返回内容列表即可
     * @param goodSearchRequest 接收请求参数的对象，包括关键词和当前页码
     * @return 搜索到的结果对象
     */
    public List<GoodsDto> searchGoodsOnlyForPage(GoodSearchRequest goodSearchRequest) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        NativeSearchQueryBuilder goodsPageQueryBuilder = this.getGoodsPageQueryBuilder(goodSearchRequest,
                searchQueryBuilder);

        Page<GoodsSearch> goodsSearchPage = searchTemplate.queryForPage(goodsPageQueryBuilder.build(), GoodsSearch.class);

        List<GoodsDto> goodsDtoList = BeanHelper.copyWithCollection(goodsSearchPage.getContent(), GoodsDto.class);

        return goodsDtoList;
    }


}
