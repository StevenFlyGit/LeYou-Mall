package com.leyou.pojo.item;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 创建时间：2020/12/12
 * 商品类别实体类
 * @author wpf
 */
@Data
@TableName("tb_category") //配置表名
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long parentId;
    private Boolean isParent;
    private Integer sort;
    private Date createTime;
    private Date updateTime;

}
