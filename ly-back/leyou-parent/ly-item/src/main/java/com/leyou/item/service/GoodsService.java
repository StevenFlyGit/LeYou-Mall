package com.leyou.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyou.common.constants.MQConstants;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.dto.item.SpuDto;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.pojo.item.Category;
import com.leyou.pojo.item.Sku;
import com.leyou.pojo.item.Spu;
import com.leyou.pojo.item.SpuDetail;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 创建时间：2020/12/15
 * 处理商品数据的业务类
 * @author wpf
 */
@Service
public class GoodsService {

    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AmqpTemplate amqpTemplate; //RabbitMq模板类

    /**
     * 分页查询商品的Spu信息
     * @param keyWord 用户输入的关键词
     * @param page 当前页码数
     * @param rows 一页显示的行数
     * @param isSaleable 是否上架
     * @return 查询到的结果集合
     */
    public IPage<Spu> findSpuByPage(String keyWord, Integer page, Integer rows, Boolean isSaleable) {

        //构造分页查询对象
        IPage<Spu> spuIPage = new Page<>(page, rows);

        QueryWrapper<Spu> wrapper = Wrappers.query();
        /*
        需要查询sql语句为：
        select * from tb_spu where (name like %keyWord% or sub_title like %keyWord%)
        and saleable = isSaleable
        需要使用到QueryWrapper对象的and()方法，可以使用lambda表达式简化编写
         */
//        wrapper.and(new Function<QueryWrapper<Object>, QueryWrapper<Object>>() {
//            @Override
//            public QueryWrapper<Object> apply(QueryWrapper<Object> objectQueryWrapper) {
//                return objectQueryWrapper.like("name", keyWord).or().like("sub_title", keyWord);
//            }
//        });
        //上下两种写法等价
//        wrapper.and(
//            q -> {
//                return q.like("name", keyWord).or().like("sub_title", keyWord);
//            }
//        );
        //上下两种写法等价
        if (StringUtils.isNotEmpty(keyWord)) {
            wrapper.and(
                    q -> q.like("name", keyWord).or().like("sub_title", keyWord)
            );
        }

        if (isSaleable != null) {
            wrapper.eq("saleable", isSaleable);
        }
        return spuMapper.selectPage(spuIPage, wrapper);
    }

    public List<SpuDto> setCategoryNameAndBrandName(List<SpuDto> spuDtoList) {

        spuDtoList.forEach(spuDto -> {
            spuDto.setBrandName(brandService.findBrandById(spuDto.getBrandId()).getName());

            /*
            stream流式编程
             1）作用：简化集合的操作的语法
             2）核心方法：
                2.1 stream(): 把集合转换为流（流式编程的第一步）
                2.2 map(): 对集合的每个元素进行处理（例如 取出元素的某个属性，打印属性名，对属性值进行算术运算 等等）
                    map方法可以执行多次
                    map()方法支持lambada表达式
                       写法一： map(c->c.getName())
                       写法二： map(Category::getName)  推荐写法
                2.3 collect(): 对map方法处理后的结果进行归总（归集） （例如： 把每个对象的属性名称再次封装成一个List集合）
                    collet(Collectors.toList())  将map方法每个结果收集成新的List集合
                    collet(Collectors.toSet())  将map方法每个结果收集成新的Set集合
                    collet(Collectors.toMap())  将map方法每个结果收集成新的Map集合
                    collet(Collectors.join("分隔符"))  将map方法每个结果收集成以指定分隔符拼接成的字符串
                最后需要的格式：//手机通讯/手机/手机
             */
            List<Category> categoryList = categoryService.findCategoriesByIds
                    (Arrays.asList(spuDto.getCid1(), spuDto.getCid2(), spuDto.getCid3()));

//            Stream<String> objectStream = categoryList.stream().map(new Function<Category, Object>() {
//                @Override
//                public Object apply(Category category) {
//                    return category.getName();
//                }
//            });
            //上下两种写法等价
            Stream<String> objectStream = categoryList.stream().map(Category::getName);
            String categoryNames = objectStream.collect(Collectors.joining("/"));

            spuDto.setCategoryName(categoryNames);
        });

        return spuDtoList;
    }

    /**
     * 修改商品上下架，更新spu信息，同时需要更新sku
     * @param spuId spu商品的Id
     * @param isSaleable 是否上架
     */
    public void changeGoodsState(Long spuId, Boolean isSaleable) {
        try {
            //构建修改的Spu对象
            Spu spu = new Spu();
            spu.setId(spuId);
            spu.setSaleable(isSaleable);
            //调用mapper接口进行删除
            spuMapper.updateById(spu);

            //发送上下架的消息到消息队列，用于添加索引到elasticsearch和生成商品详情的静态页面、
            //通过上下架的布尔值来获得常量类中相应的路由key
            String routingKey = isSaleable ? MQConstants.RoutingKey.ITEM_UP_KEY : MQConstants.RoutingKey.ITEM_DOWN_KEY;
            /*
            参数1：指定交换机
            参数2：指定路由key
            参数3：指定发送的String字符串(字符串类型的消息)
             */
            amqpTemplate.convertAndSend(MQConstants.Exchange.ITEM_EXCHANGE_NAME, routingKey, spuId);

        } catch (Exception e) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }

    /**
     * 根据spuId查询spu信息
     */
    public List<Sku> findSkusBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);

        QueryWrapper<Sku> queryWrapper = Wrappers.query(sku);

        return skuMapper.selectList(queryWrapper);
    }

    /**
     * 根据spuId查询spuDetail
     */
    public SpuDetail findSpuDetailBySpuId(Long spuId) {
        return spuDetailMapper.selectById(spuId);
    }

    /**
     * 根据SpuId查询并封装SpuDto对象
     */
    public SpuDto findSpuDtoBySpuId(Long spuId) {
        //通过Dao层接口查询Spu对象
        Spu spu = spuMapper.selectById(spuId);
        //利用工具类拷贝数据
        SpuDto spuDto = BeanHelper.copyProperties(spu, SpuDto.class);

        //封装brandName
        spuDto.setBrandName(brandService.findBrandById(spuDto.getBrandId()).getName());
        //封装categoryName
        List<Category> categoryList = categoryService.findCategoriesByIds
                (Arrays.asList(spuDto.getCid1(), spuDto.getCid2(), spuDto.getCid3()));
        Stream<String> objectStream = categoryList.stream().map(Category::getName);
        String categoryNames = objectStream.collect(Collectors.joining("/"));
        spuDto.setCategoryName(categoryNames);

        //封装spuDetail和skuList
        spuDto.setSpuDetail(this.findSpuDetailBySpuId(spuId));
        spuDto.setSkuList(this.findSkusBySpuId(spuId));

        return spuDto;
    }

    /**
     * 根据skuId集合查询Sku对象集合
     */
    public List<Sku> findSkuListByIds(List<Long> skuIds) {
        List<Sku> skuList = skuMapper.selectBatchIds(skuIds);
        if (CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return skuList;
    }

    /**
     * 减少商品库存
     */
    public void minesStock(Map<Long, Integer> stockParamMap) {
        Set<Map.Entry<Long, Integer>> entrySet = stockParamMap.entrySet();
        Iterator<Map.Entry<Long, Integer>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Integer> next = iterator.next();
            //获取原来的库存
            Sku sku = skuMapper.selectById(next.getKey());
            //减少相应的库存
            sku.setStock(sku.getStock() - next.getValue());
            //将减少后的数据返回给数据库
            skuMapper.updateById(sku);
        }

    }

    /**
     * 增加商品库存
     */
    public void plusStock(Map<Long, Integer> stockParamMap) {
        Set<Map.Entry<Long, Integer>> entrySet = stockParamMap.entrySet();
        Iterator<Map.Entry<Long, Integer>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Integer> next = iterator.next();
            //获取原来的库存
            Sku sku = skuMapper.selectById(next.getKey());
            //减少相应的库存
            sku.setStock(sku.getStock() + next.getValue());
            //将减少后的数据返回给数据库
            skuMapper.updateById(sku);
        }
    }
}
