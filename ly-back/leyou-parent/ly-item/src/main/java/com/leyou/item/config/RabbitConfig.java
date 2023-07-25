package com.leyou.item.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建时间：2020/12/23
 * Rabbit相关配置类
 * @author wpf
 */
@Configuration
public class RabbitConfig {

    /**
     * 默认情况下，AMQP会使用JDK的序列化方式进行处理，传输数据比较大，效率太低。我们可以自定义消息转换器，使用JSON来处理
     * @return 转换器对象，会加入到Spring容器中
     */
    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

}
