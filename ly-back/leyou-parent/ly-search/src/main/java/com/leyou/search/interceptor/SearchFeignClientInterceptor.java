package com.leyou.search.interceptor;

import com.leyou.common.constants.LyConstants;
import com.leyou.search.scheduler.GetServiceTokenSchedule;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建时间：2020/12/27
 * search服务调用其他服务的feign接口时的拦截器
 * @author wpf
 */
@Component
public class SearchFeignClientInterceptor implements RequestInterceptor {

    @Autowired
    private GetServiceTokenSchedule tokenSchedule;

    @Override
    public void apply(RequestTemplate template) {
        //在请求feign接口之前加入一条请求头信息
        template.header(LyConstants.APP_TOKEN_HEADER, tokenSchedule.getServiceToken());
    }
}
