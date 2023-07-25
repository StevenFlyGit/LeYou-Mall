package com.leyou.pojo.item;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 创建时间：2020/12/14
 * 商品的规格组实体类
 * @author wpf
 */
@Data
@TableName("tb_spec_group")
public class SpecGroup {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long cid;
    private String name;
    private Date createTime;
    private Date updateTime;
}
