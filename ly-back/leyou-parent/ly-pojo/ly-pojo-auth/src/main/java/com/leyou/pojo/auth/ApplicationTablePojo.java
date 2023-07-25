package com.leyou.pojo.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 创建时间：2020/12/26
 *
 * @author wpf
 */
@Data
@TableName("tb_application")
public class ApplicationTablePojo {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务密钥
     */
    private String secret;
    /**
     * 服务信息
     */
    private String info;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

}
