package com.leyou.search.listener;

import com.leyou.common.constants.MQConstants;
import com.leyou.search.service.GoodDataImportService;
import com.leyou.search.service.GoodDataSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建时间：2020/12/23
 * 搜索微服务中RabbitMq的消费者类
 * @author wpf
 */
@Component
@Slf4j
public class SearchListener {

    @Autowired
    GoodDataImportService goodDataImportService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = MQConstants.Queue.SEARCH_ITEM_UP),
        exchange = @Exchange(name = MQConstants.Exchange.ITEM_EXCHANGE_NAME),
        key = MQConstants.RoutingKey.ITEM_UP_KEY))
    public void createIndex(Long spuId) {
        goodDataImportService.createIndexForSpu(spuId);
        log.info("【索引同步】索引创建成功，商品ID为："+spuId);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = MQConstants.Queue.SEARCH_ITEM_DOWN),
            exchange = @Exchange(name = MQConstants.Exchange.ITEM_EXCHANGE_NAME),
            key = MQConstants.RoutingKey.ITEM_DOWN_KEY))
    public void deleteIndex(Long spuId) {
        goodDataImportService.deleteIndexForSpu(spuId);
        log.info("【索引同步】索引删除成功，商品ID为："+spuId);
    }


}
