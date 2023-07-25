package com.leyou.gateway.fallback;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 创建时间：2020/12/10
 * 统一熔断降级处理类
 * @author wpf
 */
@RestController
public class FallbackController {

    /**
     * 降级处理方法
     */
    @RequestMapping("/fallback")
    public String fallback(){
        return "服务器正忙，请您稍后再试";
    }
}


