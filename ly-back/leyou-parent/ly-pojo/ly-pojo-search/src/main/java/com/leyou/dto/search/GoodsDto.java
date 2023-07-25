package com.leyou.dto.search;

import lombok.Data;

/**
 * 创建时间：2020/12/19
 * 用于接收Elasticsearch中需要使用的商品数据
 * @author wpf
 */
@Data
public class GoodsDto {

    private Long id;
    private String spuName;
    private String skuList;
    private String subTitle;
}
