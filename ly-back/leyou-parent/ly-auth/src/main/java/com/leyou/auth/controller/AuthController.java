package com.leyou.auth.controller;

import com.leyou.auth.service.AuthService;
import com.leyou.common.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 创建时间：2020/12/24
 * 用户权限相关业务的控制器
 * @author wpf
 */
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录方法(包含授权过程)
     * @param username 用户名
     * @param password 密码
     * @param response Http响应对象，用于写入Cookie
     */
    @PostMapping("/login")
    public ResponseEntity<Void> userLogin(@RequestParam("username") String username,
        @RequestParam("password") String password, HttpServletResponse response) {
        authService.login(username, password, response);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 校验用户信息(检查是否有相应的权限)
     * @return 用户信息
     */
    @GetMapping("/verify")
    public ResponseEntity<UserInfo> userVerify(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.verify(request, response));
    }

    /**
     * 用户注销账户方法
     * @param request Http请求对象，用于获取Cookie
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> userLogout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 给微服务分发token的方法
     * @param serviceName 服务名
     * @param secret 给服务分配的密码
     * @return token字符串
     */
    @GetMapping("/authorization")
    public ResponseEntity<String> applicationAuthorize(@RequestParam("serviceName") String serviceName,
        @RequestParam("secret") String secret) {
        return ResponseEntity.ok(authService.applicationAuthorize(serviceName, secret));
    }

}
