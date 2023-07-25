package com.leyou.sms.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建时间：2020/12/23
 * 用于生成发送短信中需要用到的阿里云短信API中的对象
 * @author wpf
 */
@Configuration
public class SmsConfig {

    @Autowired
    private SmsProperty smsProperty;

    @Bean
    public IAcsClient getIAcsClient() {
        DefaultProfile profile = DefaultProfile.getProfile
                (smsProperty.getRegionID(), smsProperty.getAccessKeyID(), smsProperty.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);
        return client;
    }

}
