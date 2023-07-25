package com.leyou.order.controller;

import com.leyou.dto.order.OrderDto;
import com.leyou.order.service.OrderService;
import com.leyou.vo.order.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 创建时间：2020/12/30
 * 操作订单业务的控制器
 * @author wpf
 */
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/order")
    public ResponseEntity<Long> createOrder(@RequestBody OrderDto orderDto) {
        Long orderId = orderService.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }

    /**
     * 根据订单Id查询订单的vo对象返回
     */
    @GetMapping("/order/{id}")
    public ResponseEntity<OrderVo> getOrderById(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.findOrderVoById(orderId));
    }

}
