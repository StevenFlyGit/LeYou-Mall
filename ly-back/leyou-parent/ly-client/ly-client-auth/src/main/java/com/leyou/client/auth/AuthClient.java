package com.leyou.client.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 创建时间：2020/12/26
 * 授权中心微服务向外提供的服务调用接口
 * @author wpf
 */
@FeignClient("auth-service")
public interface AuthClient {

    /**
     * 给微服务分发token的方法
     * @param serviceName 服务名
     * @param secret 给服务分配的密码
     * @return token字符串
     */
    @GetMapping("/authorization")
    public String applicationAuthorize(@RequestParam("serviceName") String serviceName,
           @RequestParam("secret") String secret);


}
