package com.leyou.cart.config;

import com.leyou.common.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * 创建时间：2020/12/28
 * Jwt相关参数配置类
 * @author wpf
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperty {

    private String pubKeyPath;
    private PublicKey publicKey;
    private CookiePojo cookie = new CookiePojo(); //对于内部类，Spring并不会自定创建对象，因此需要手动new
    private Application application = new Application(); //对于内部类，Spring并不会自定创建对象，因此需要手动new

    @Data
    public class CookiePojo {
        private String cookieName;
    }

    @Data
    public class Application {
        private String serviceName;
        private String secret;
    }

    @PostConstruct
    public void initMethodF() throws Exception {
        if (pubKeyPath != null) {
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
        }
    }


}
