package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 创建时间：2020/12/10
 * 网关的启动类
 * @author wpf
 */
//@SpringBootApplication //springboot启动注解
//@EnableDiscoveryClient //服务注册注解
//@EnableCircuitBreaker //开启熔断器注解
@SpringCloudApplication
@EnableFeignClients //开启Feign扫描
@EnableScheduling
public class LyGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyGatewayApplication.class, args);
    }
}
