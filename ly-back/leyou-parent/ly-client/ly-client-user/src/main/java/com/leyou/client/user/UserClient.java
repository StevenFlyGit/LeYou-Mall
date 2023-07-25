package com.leyou.client.user;

import com.leyou.pojo.user.Address;
import com.leyou.pojo.user.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 创建时间：2020/12/24
 * 用户微服务向外提供的服务调用接口
 * Feign接口中声明的返回值可以不使用ResponseEntity来封装
 * @author wpf
 */
@FeignClient("user-service")
public interface UserClient {

    /**
     * 根据用户名和密码查询用户
     */
    @GetMapping("/query")
    public User findUserByUsernameAndPassword(@RequestParam("username") String username,
        @RequestParam("password") String password);

    /**
     * 根据Id查找地址
     */
    @GetMapping("/address")
    public Address findAddressById(
            @RequestParam("userId") Long userId,
            @RequestParam("id") Long addressId
    );
}
