package com.leyou.gateway.scheduler;

import com.leyou.client.auth.AuthClient;
import com.leyou.common.constants.LyConstants;
import com.leyou.gateway.config.JwtProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 创建时间：2020/12/27
 * 定时获取服务token
 * @author wpf
 */
@Component
@Slf4j
public class GetServiceTokenSchedule {

    @Autowired
    private AuthClient authClient;
    @Autowired
    private JwtProperty jwtProperty;

    private String serviceToken;

    @Scheduled(fixedRate = LyConstants.TOKEN_REFRESH_INTERVAL)
    public void getTokenForGateway() {

        while(true) {
            try {
                this.serviceToken = authClient.applicationAuthorize(jwtProperty.getApplication().getServiceName(),
                        jwtProperty.getApplication().getSecret());

                log.info("【服务获取token定时任务】- " + jwtProperty.getApplication().getServiceName() + "连接成功");
                break;
            } catch (Exception e) {
                try {
                    log.error("【服务获取token定时任务】- " + jwtProperty.getApplication().getServiceName() + "连接失败吗，" +
                            "10秒后重新连接");
                    Thread.sleep(LyConstants.TOKEN_RETRY_INTERVAL);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }

    }

    public String getServiceToken() {
        return serviceToken;
    }
}
