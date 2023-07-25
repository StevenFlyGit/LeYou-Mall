package com.leyou.pojo.item;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 创建时间：2020/12/15
 * Spu商品详细信息表实体类
 * @author wpf
 */

@Data
@TableName("tb_spu_detail")
public class SpuDetail {
    @TableId(type = IdType.INPUT)
    private Long spuId;// 对应的SPU的id
    private String description;// 商品描述
    private String specialSpec;// 商品特殊规格的名称及可选值模板
    private String genericSpec;// 商品的全局规格属性
    private String packingList;// 包装清单
    private String afterService;// 售后服务
    private Date createTime;// 创建时间
    private Date updateTime;// 最后修改时间
}
