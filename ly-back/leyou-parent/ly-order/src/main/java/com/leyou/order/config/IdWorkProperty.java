package com.leyou.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 创建时间：2020/12/30
 * 读取雪花算法的服务器唯一编码的配置类
 * @author wpf
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "ly.worker")
public class IdWorkProperty {

    private Long workerId;
    private Long dataCenterId;

}
