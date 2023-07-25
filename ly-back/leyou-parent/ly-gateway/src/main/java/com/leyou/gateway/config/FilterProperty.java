package com.leyou.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 创建时间：2020/12/26
 * 配置请求拦截白名单
 * @author wpf
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ly.filter")
public class FilterProperty {

    private List<String> allowPaths;

}
