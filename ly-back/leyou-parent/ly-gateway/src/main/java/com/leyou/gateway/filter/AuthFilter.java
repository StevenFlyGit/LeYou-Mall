package com.leyou.gateway.filter;

import com.leyou.client.auth.AuthClient;
import com.leyou.common.constants.LyConstants;
import com.leyou.common.pojo.Payload;
import com.leyou.common.utils.JwtUtils;
import com.leyou.gateway.config.FilterProperty;
import com.leyou.gateway.config.JwtProperty;
import com.leyou.gateway.scheduler.GetServiceTokenSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.swing.text.LayoutQueue;
import java.util.List;

/**
 * 创建时间：2020/12/26
 * 用户权限过滤器
 * @author wpf
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtProperty jwtProperty;
    @Autowired
    private FilterProperty filterProperty;
    @Autowired
    private GetServiceTokenSchedule tokenSchedule;

    /**
     * 权限过滤
     * @param exchange： 封装了request和response对象
     * @param chain 过滤器链，控制过滤器的流程。用于执行后面的过滤器或者执行目标资源
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest(); //Spring封装的请求对象(Api有部分与原生Servelet的请求对象不同)
        ServerHttpResponse response = exchange.getResponse(); //Spring封装的响应对象(Api有部分与原生Servelet的响应对象不同)

        //往请求头中加入网关服务的token
        request.mutate().header(LyConstants.APP_TOKEN_HEADER, tokenSchedule.getServiceToken());

        //检查请求是否包含在白名单目录里
        //两种API得到的结果相同，都是获得从域名后(包含端口)到参数前的内容
        String path1 = request.getURI().getPath();
//        String path2 = request.getPath().toString();
//        System.out.println("path1 = " + path1);
//        System.out.println("path2 = " + path2);
        List<String> allowPaths = filterProperty.getAllowPaths();
        for (String allowPath : allowPaths) {
            if (path1.contains(allowPath)) {
                //放行请求
                return chain.filter(exchange);
            }
        }

        Payload<Object> infoFromToken = null;

        try {
            //获取Cookie的Value(即token)，因为与原生API不同，因此不能使用工具类获取
            String token = request.getCookies().getFirst(jwtProperty.getCookie().getCookieName()).getValue();
            //利用工具类来解析token，若能解析成功，则代表合法，否则抛出异常
            infoFromToken = JwtUtils.getInfoFromToken(token, jwtProperty.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            //返回401状态码，一般代表无权限
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //终止请求
            return response.setComplete();
        }

        //查询当前用户拥有的权限（基于RBAC表查询得到），判断用户是否有足够权限访问(这一步没有做)

        //若顺利执行，则放行请求
        return chain.filter(exchange);
    }

    /**
     * 数字越小，越优先执行过滤逻辑
     * @return 代表优先等级的数字
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
