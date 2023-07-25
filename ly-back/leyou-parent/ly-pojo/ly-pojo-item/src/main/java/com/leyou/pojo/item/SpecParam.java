package com.leyou.pojo.item;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 创建时间：2020/12/15
 * 商品规格实体类
 * @author wpf
 */
@Data
@TableName("tb_spec_param")
public class SpecParam {

    private Long id;
    private Long cid;
    private Long groupId;
    private String name;
    @TableField("`numeric`")
    private Boolean numeric;
    private String unit;
    private Boolean generic;
    private Boolean searching;
    private String segments;
    private Date createTime;
    private Date updateTime;

}
