package com.leyou.order.interceptor;

import com.leyou.common.pojo.Payload;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.common.utils.UserHolder;
import com.leyou.order.config.JwtProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 创建时间：2020/12/28
 * 进入购物车前先获取用户登录信息的拦截器
 * @author wpf
 */
@Component
public class UserAuthorizeInterceptor implements HandlerInterceptor {

    @Autowired
    JwtProperty jwtProperty;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Payload<UserInfo> infoFromToken = null;
        //获取Cookie中的token
        try {
            String userToken = CookieUtils.getCookieValue(request, jwtProperty.getCookie().getCookieName());
            //从token中获取用户信息
            infoFromToken = JwtUtils.getInfoFromToken(userToken, jwtProperty.getPublicKey(), UserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        //利用工具类，将用户放入ThreadLocal对象中(一个线程对应一个对象)
        UserHolder.setUser(infoFromToken.getInfo());

        return true;
    }
}
