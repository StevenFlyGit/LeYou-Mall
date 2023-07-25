package com.leyou.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 创建时间：2020/12/13
 * 阿里云Oss相关配置参数读取类
 * 让@ConfigurationProperties注解生效两种方法：
 * 1）在启动类使用@EnableConfigurationProperties(OssProperties.class)开启配置读取
 * 2）在当前类上直接使用@Component注解
 * @author wpf
 */
@Data
@Component
@ConfigurationProperties(prefix = "ly.oss")
public class OssProperties {

    private String accessKeyId; //阿里云KeyId
    private String accessKeySecret; //阿里云KeySecret(秘钥)
    private String host; //访问oss的域名，很重要bucket + endpoint(访问图片的路径)
    private String endPoint; //服务的端点
    private String dir; //保存到Oss中某个bucket的子目录
    private Long expireTime; //链接超时时间，单位是S
    private Long maxFileSize; //上传的文件大小限制

}
