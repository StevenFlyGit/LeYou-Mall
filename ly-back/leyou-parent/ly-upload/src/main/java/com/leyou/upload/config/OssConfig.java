package com.leyou.upload.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSBuilder;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建时间：2020/12/13
 * 用于生成Oss上传文件中需要生成的工具类对象
 * @author wpf
 */
@Configuration
public class OssConfig {

    /**
     * 注意：在@Bean的方法的参数上面可以直接注入IOC容器的对象，不需要任何注解
     * 加上@Qualifier注解则代表是通过Id来获取对象(对象的默认Id是类名首字母小写)
     * @param ossProp
     * @return
     */
    @Bean
    public OSS createOss(@Qualifier("ossProperties") OssProperties ossProp) {
        return new OSSClientBuilder().build(ossProp.getEndPoint(), ossProp.getAccessKeyId(), ossProp.getAccessKeySecret());
    }

}
