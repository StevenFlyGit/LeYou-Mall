package com.leyou.pojo.search;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;
import java.util.Set;

/**
 * 创建时间：2020/12/18
 * ElasticSearch的Field记录实体类
 * 用于进行门户网站的商品搜索
 * @author wpf
 */
@Data
@Document(indexName = "goods-index", type = "goods-doc")
public class GoodsSearch {

    @Id
    private Long id; //索引中的id，可以直接使用spu表的Id
    @Field(type = FieldType.Text, analyzer = "ik_max_word") //索引，分词
    private String spuName; //对应sup表中的name属性，用于搜索后高亮显示
    @Field(type = FieldType.Keyword, index = false) //不分词，不索引
    private String subTitle; //spu子标题(对应spu表送的spu_title字段)
    @Field(type = FieldType.Text, analyzer = "ik_max_word") //索引，分词
    private String allForSearch; //将所有需要搜索的字段封装到一个字符串中，降低后续处理的难度
    /*
    因为集合对象（map，list等）在es中默认为索引，不分词，但实际上sku的对象不需要索引和分词
    因此将spu下对应的sku集合转换为json字符串来存储，控制不索引不分词来提升es的执行效率
     */
    @Field(type = FieldType.Keyword, index = false) //不索引，不分词
    private String skuList;
    @Field(type = FieldType.Long) //因为要进行聚合搜索，因此需要索引
    private Long brandId; //商品品牌Id
    @Field(type = FieldType.Long) //因为要进行聚合搜索，因此需要索引
    private Long categoryId; //商品3级分类Id
    @Field(type = FieldType.Object) //map集合，索引不分词
    private Map<String, Object> specParams; //规格参数，因为参数的种类和每一种参数的个数都不确定，因此有map集合来存储

    @Field(type = FieldType.Long)
    private Long createTime; //商品的创建时间，后续用于排序操作。封装时间毫秒值便于排序
    @Field(type = FieldType.Long)
    private Set<Long> prices; //所有sku的价格，用set集合存储方便排序

}
