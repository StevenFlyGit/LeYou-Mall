package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.client.item.ItemClient;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.dto.item.SpuDto;
import com.leyou.pojo.item.Sku;
import com.leyou.pojo.item.SpecParam;
import com.leyou.pojo.item.SpuDetail;
import com.leyou.pojo.search.GoodsSearch;
import com.leyou.search.repository.GoodSearchRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 创建时间：2020/12/18
 * 商品数据导入到elasticsearch的相关业务类
 * @author wpf
 */
@Service
public class GoodDataImportService {

    @Autowired
    private GoodSearchRepository goodSearchRepository;
//    @Autowired
//    private ElasticsearchTemplate searchTemplate;
    @Autowired
    private ItemClient itemClient;

    /**
     * 将Mysql中的商品数据导入到Elasticsearch中，以便后期搜索使用
     */
    public void importGoodsMagFromSqlToEs() {
        //使用item微服务中分页查询的方法，分批将mysql中的商品数据导入到elasticsearch中
        //定义分页的参数
        int pageSize = 100;
        int currentPage = 1;
        long totalPage = 0; //后续查出数据后再进行赋值

        do {
            PageResult<SpuDto> spuMsgByPage = itemClient.getSpuMsgByPage(null, currentPage, pageSize, true);//只导入上架的商品
            //获取总页数
            totalPage = spuMsgByPage.getTotalPage();
            //创建elasticsearch中需要存储的商品信息集合
            List<GoodsSearch> goodsList = new ArrayList<>();

            //使用stream流来编辑goodSearch对象
            goodsList = spuMsgByPage.getItems().stream().map(this::buildGoodsSearch).collect(Collectors.toList());

            //使用spring-date-elasticsearch中的方法来保存商品信息到es中
            goodSearchRepository.saveAll(goodsList);
            currentPage++;
        } while (currentPage <= totalPage);

    }

    /**
     * 将SpuDto对象转换为搜索所用的GoodsSearch对象
     * @param spuDto
     * @return
     */
    public GoodsSearch buildGoodsSearch(SpuDto spuDto) {
        GoodsSearch goodsSearch = new GoodsSearch();
        //为goods的各个属性赋值
        goodsSearch.setId(spuDto.getId());
        goodsSearch.setSpuName(spuDto.getName());
        goodsSearch.setSubTitle(spuDto.getSubTitle());

        //调用远程方法来获取spu下面的所有sku集合
        List<Sku> skuList = itemClient.getSkusBySpuId(spuDto.getId());

        //使用stream流来封装allForSearch属性的信息
        String allForSearch = spuDto.getName() + "," + spuDto.getSubTitle() + "," +
                skuList.stream().map(Sku::getTitle).collect(Collectors.joining(","));

        goodsSearch.setAllForSearch(allForSearch); //搜索的关键信息

        goodsSearch.setSkuList(JsonUtils.toString(skuList));
        goodsSearch.setBrandId(spuDto.getBrandId());
        goodsSearch.setCategoryId(spuDto.getCid3());

        Map<String, Object> specParams = this.buildSpecParamMap(spuDto);

        goodsSearch.setSpecParams(specParams); //搜索的关键信息

        goodsSearch.setCreateTime(spuDto.getCreateTime().getTime());

        //使用stream流来封装price属性的信息
        Set<Long> priceSet = skuList.stream().map(Sku::getPrice).collect(Collectors.toSet());

        goodsSearch.setPrices(priceSet);


        return goodsSearch;
    }

    private Map<String, Object> buildSpecParamMap(SpuDto spuDto) {
        Map<String, Object> specParamMap = new HashMap<>();

        //查询所有的参数
        List<SpecParam> specParamList = itemClient.getSpecParam(null, spuDto.getCid3(), true);
        //查询spuDetail表的信息
        SpuDetail spuDetail = itemClient.getSpuDetailBySpuId(spuDto.getId());

        //获得通用规格的集合(通过Json工具类来转换)
        Map<Long, Object> genericSpecMap = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, Object.class);
        //使用工具类中的nativeRead方法，其中使用了TypeReference，可以在转换Json字符串时解析多层集合嵌套
        Map<Long, List<Object>> specialSpecMap = JsonUtils.nativeRead(spuDetail.getSpecialSpec(),
                new TypeReference<Map<Long, List<Object>>>() {});

        specParamList.forEach(
            specParam -> {
                //判断该规格属于通用规格还是特有规格
                if (specParam.getGeneric()) {
                    //属于通用规格
                    if (specParam.getNumeric()) {
                        specParamMap.put(specParam.getName(),
                                chooseSegment(genericSpecMap.get(specParam.getId()), specParam));
                    } else {
                        specParamMap.put(specParam.getName(), genericSpecMap.get(specParam.getId()));
                    }
                } else {
                    if (specParam.getNumeric()) {
                        specParamMap.put(specParam.getName(),
                                chooseSegment(specialSpecMap.get(specParam.getId()), specParam));
                    } else {
                        specParamMap.put(specParam.getName(), specialSpecMap.get(specParam.getId()));
                    }
                }
            }
        );

        return specParamMap;
    }

    /**
     * 把一个具体数字转换为数字区间
     * @param value 转换后的区间
     * @param p 类别对象
     * @return 转换后的字符串
     */
    private String chooseSegment(Object value, SpecParam p) {
        final int maxValue = Integer.MAX_VALUE;
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public void createIndexForSpu(Long spuId) {
        try {
            SpuDto spuDto = itemClient.getSpuDtoBySpuId(spuId);
            GoodsSearch goodsSearch = this.buildGoodsSearch(spuDto);
            goodSearchRepository.save(goodsSearch);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.INDEX_CREATE_ERROR);
        }
    }

    public void deleteIndexForSpu(Long spuId) {
        try {
            goodSearchRepository.deleteById(spuId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.INDEX_DELETE_ERROR);
        }
    }

}
