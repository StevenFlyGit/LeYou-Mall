package com.leyou.item.interceptor;

import com.leyou.common.constants.LyConstants;
import com.leyou.common.pojo.ApplicationInfo;
import com.leyou.common.pojo.Payload;
import com.leyou.common.utils.JwtUtils;
import com.leyou.item.config.JwtProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 创建时间：2020/12/27
 * 各个服务的权限校验拦截器
 * @author wpf
 */
@Component
@Slf4j
public class ServiceAuthorizeInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperty jwtProperty;

    @Override //该方法在控制器方法执行成功之后执行
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override //无论控制器的方法无论执行成功，该方法都会在之后执行
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    @Override //该方法在控制器方法执行之前执行
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Payload<ApplicationInfo> infoFromToken = new Payload<>();
        try {
            String serviceToken = request.getHeader(LyConstants.APP_TOKEN_HEADER);
            infoFromToken = JwtUtils.getInfoFromToken(serviceToken, jwtProperty.getPublicKey(), ApplicationInfo.class);
        } catch (Exception e) {
            log.error("【服务token验证】验证失败，原因："+e.getMessage());
            //返回false即代表拒绝访问
            return false;
        }

        List<String> targetList = infoFromToken.getInfo().getTargetList();
        //判断权限是否足够(校验调用方中token的目标服务名是否包含此服务的服务名)
        if (targetList.contains(jwtProperty.getApplication().getServiceName())) {
            //允许访问
            return true;
        } else {
            log.error("【服务token验证】验证失败，该服务不属于合法服务");
            //拒绝访问
            return false;
        }

    }
}
