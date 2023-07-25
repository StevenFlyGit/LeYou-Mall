package com.leyou.dto.item;

import com.leyou.pojo.item.Sku;
import com.leyou.pojo.item.SpuDetail;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 创建时间：2020/12/15
 * 用于封装前后端交互的商品信息
 * @author wpf
 */
@Data
public class SpuDto {
    private Long id;
    private Long brandId;
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String name;// 商品名称
    private String subTitle;// 子标题
    private Boolean saleable;// 是否上架
    private Date createTime;// 创建时间
    private Date updateTime;// 最后修改时间

    /**
     * 表中的信息只包含Id，但是前端需要展示名称，因此需要封装名称信息
     * 从数据库根据Id查询到名称后再注入到该类的属性中
     */
    //分类名称 格式：手机通讯/手机/手机
    private String categoryName;
    //品牌名称
    private String brandName;

    //为封装商品详情页的静态模板数据，增加SpuDetail和List<Sku>两个属性
    private SpuDetail spuDetail;
    private List<Sku> skuList;

}
