package com.leyou.auth.config;

import com.leyou.common.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 创建时间：2020/12/24
 * Jwt相关参数配置类
 * @author wpf
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperty {

    private String pubKeyPath;
    private String priKeyPath;
    private CookiePojo cookie = new CookiePojo(); //对于内部类，Spring并不会自定创建对象，因此需要手动new
    private ApplicationPojo application = new ApplicationPojo(); //对于内部类，Spring并不会自定创建对象，因此需要手动new

    @Data
    public class ApplicationPojo {
        private Integer expire;
    }

    @Data
    public class CookiePojo {
        private Integer expire;
        private Integer refresh;
        private String cookieName;
        private String cookieDomain;
    }

    //获取公钥和私有对象(利用工具类，在生成对象后对该两个值赋值)
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @PostConstruct //相当于传统spring项目中在xml文件里的Bean标签上配置的init-method(即Spring对象初始化的第8步)
    private void initMethod() throws Exception {
        if (this.priKeyPath != null) {
            privateKey = RsaUtils.getPrivateKey(priKeyPath);
        }
        if (this.pubKeyPath != null) {
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
        }
    }

    /**
     * 不能使用无参构造器对publicKey与privateKey两个属性赋值
     * 因为该类属于Spring的对象，在加载该构造器时，Spring还未从配置文件中读取并注入其他属性的值
     * Spring Bean在Spring Bean Factory Container中完成其整个生命周期：以下是完成其生命周期所需的各种内容：
     *
     * 1. Spring容器从XML文件或@Configuration中bean的定义中实例化bean(IOC)。
     * 2. Spring依据配置中指定的属性，为bean填充所有属性(DI)。
     * 3. 如果bean实现BeanNameAware接口，spring调用setBeanName()方法，并传递bean的id。
     * 4. 如果bean实现BeanFactoryAware接口，spring将调用setBeanFactory()方法，并把自己作为参数。
     * 5. 如果bean实现ApplicationContextAware接口，spring将调用setApplicationContext()方法，并把ApplicationContext实例作为参数。
     * 6. 如果存在与bean关联的任何BeanPostProcessors（后处理器），则调用preProcessBeforeInitialization()方法。比如Autowired等依赖注入功能是在此时完成。
     * 7. 如果Bean实现了InitializingBean接口，则调用bean的afterPropertiesSet()方法。
     * 8. 如果为bean指定了init-method，那么将调用bean的init方法。
     * 9. 最后，如果存在与bean关联的任何BeanPostProcessors，则将调用postProcessAfterInitialization（）方法。
     * @throws Exception
     */
//    public JwtProperty() throws Exception {
//        publicKey = RsaUtils.getPublicKey(pubKeyPath);
//        privateKey = RsaUtils.getPrivateKey(priKeyPath);
//    }

}
