package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 创建时间：2020/12/23
 * 读取yml配置文件中的相关属性
 * @author wpf
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ly.sms")
public class SmsProperty {

    /**
     * 账号
     */
    String accessKeyID;
    /**
     * 密钥
     */
    String accessKeySecret;
    /**
     * 短信签名
     */
    String signName;
    /**
     * 短信模板
     */
    String verifyCodeTemplate;
    /**
     * 发送短信请求的域名
     */
    String domain;
    /**
     * API版本
     */
    String version;
    /**
     * API类型
     */
    String action;
    /**
     * 区域
     */
    String regionID;
    /**
     * 短信模板中验证码的占位符
     */
    String code;


//    private String accessKeyID;
//    private String accessKeySecret;
//    private String signName;
//    private String verifyCodeTemplate;
//    private String domain;
//    private String action;
//    private String version;
//    private String regionID;
//    private String code;

}
