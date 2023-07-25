package com.leyou.cart.config;

import com.leyou.cart.interceptor.ServiceAuthorizeInterceptor;
import com.leyou.cart.interceptor.UserAuthorizeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 创建时间：2020/12/27
 * 拦截器配置类
 * @author wpf
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    ServiceAuthorizeInterceptor serviceInterceptor;
    @Autowired
    UserAuthorizeInterceptor userInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //加入编写好的拦截器对象并配置拦截地址
        //.addPathPatterns() 添加拦截路径
        //.excludePathPatterns() 添加放行路径
        registry.addInterceptor(serviceInterceptor).addPathPatterns("/**");
        registry.addInterceptor(userInterceptor).addPathPatterns("/**");
    }
}
