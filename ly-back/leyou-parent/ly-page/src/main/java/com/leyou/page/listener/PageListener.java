package com.leyou.page.listener;

import com.leyou.common.constants.MQConstants;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.page.service.StaticPageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建时间：2020/12/23
 * 静态页面微服务中RabbitMq的消费者类
 * @author wpf
 */
@Component
@Slf4j
public class PageListener {

    @Autowired
    StaticPageService staticPageService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = MQConstants.Queue.PAGE_ITEM_UP),
        exchange = @Exchange(name = MQConstants.Exchange.ITEM_EXCHANGE_NAME),
        key = MQConstants.RoutingKey.ITEM_UP_KEY))
    public void createIndexForSpu(Long spuId) {
        staticPageService.createStaticPage(spuId);
        log.info("【静态页同步】静态页创建成功，商品ID为："+spuId);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = MQConstants.Queue.PAGE_ITEM_DOWN),
        exchange = @Exchange(name = MQConstants.Exchange.ITEM_EXCHANGE_NAME),
        key = MQConstants.RoutingKey.ITEM_DOWN_KEY))
    public void deleteIndexForSpu(Long spuId) {
        staticPageService.deleteStaticPage(spuId);
        log.info("【静态页同步】静态页删除成功，商品ID为："+spuId);
    }

}
