package com.leyou.order.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leyou.client.item.ItemClient;
import com.leyou.client.user.UserClient;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.IdWorker;
import com.leyou.common.utils.UserHolder;
import com.leyou.dto.order.CartDto;
import com.leyou.dto.order.OrderDto;
import com.leyou.order.config.IdWorkProperty;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderLogisticsMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.pojo.item.Sku;
import com.leyou.pojo.order.Order;
import com.leyou.pojo.order.OrderDetail;
import com.leyou.pojo.order.OrderLogistics;
import com.leyou.pojo.order.OrderStatusEnum;
import com.leyou.pojo.user.Address;
import com.leyou.vo.order.OrderVo;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 创建时间：2020/12/30
 * 订单的相关业务类
 * @author wpf
 */
@Service
@Transactional
public class OrderService {

    @Autowired
    private IdWorkProperty idWorkProperty;
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private UserClient userClient;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderLogisticsMapper orderLogisticsMapper;

    /**
     * 创建订单
     */
    public Long createOrder(OrderDto orderDto) {
        //获取用户信息
        UserInfo user = UserHolder.getUser();

        //添加订单信息
        Order order = new Order();
        //使用雪花算法生成分布式Id
        IdWorker idWorker = new IdWorker(idWorkProperty.getWorkerId(), idWorkProperty.getDataCenterId());
        order.setOrderId(idWorker.nextId());

        order.setPromotionIds("");//优惠活动
        order.setPaymentType(1);//支付类型
        order.setPostFee(20L);//邮费
        order.setUserId(user.getId());//下单用户
        order.setInvoiceType(0);//发票类型
        order.setSourceType(2);//订单来源
        order.setStatus(OrderStatusEnum.INIT.value());//订单状态

        //计算总金额
        //根据SkuId集合查询Sku集合
        List<Long> skuIdList = orderDto.getCarts().stream().map(CartDto::getSkuId).collect(Collectors.toList());
        List<Sku> skuList = itemClient.getSkuListByIds(skuIdList);
        //将cartDtoList转为map，map的key为商品Id，map的value为数量
        /*
        toMap()方法：将List集合元素转移到Map集合汇总
           参数一： 将List集合的对象的哪个属性作为Map的key
           参数二： 将List集合的对象的哪个属性作为Map的value
         */
        List<CartDto> cartDtoList = orderDto.getCarts();
        Map<Long, Integer> cartMap = cartDtoList.stream().
                collect(Collectors.toMap(CartDto::getSkuId, CartDto::getNum));

        /*
        mapToLong(): 取出集合元素进行运算，把运算结果转换为Long类型
        sum(): 把每个mapToLong()方法的运算结果进行加总。
         */
        long totalFee = skuList.stream().mapToLong(sku -> sku.getPrice() * cartMap.get(sku.getId())).sum();
        order.setTotalFee(totalFee);

        //因为暂时没有开发优惠券模块，因此直接加上运费即为实际支付的费用
        order.setActualFee(totalFee + order.getPostFee());
        //设置时间
        order.setCreateTime(new Date());

        //插入到数据库
        orderMapper.insert(order);

        //添加订单详情信息
        skuList.forEach(sku -> {
            OrderDetail orderDetail = BeanHelper.copyProperties(sku, OrderDetail.class);
            orderDetail.setId(idWorker.nextId());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setOrderId(order.getOrderId());
            orderDetail.setCreateTime(new Date());
            orderDetail.setUpdateTime(null);
            orderDetail.setNum(cartMap.get(sku.getId()));

            orderDetailMapper.insert(orderDetail);
        });

        //添加订单物流信息
        Address address = userClient.findAddressById(user.getId(), orderDto.getAddressId());
        //拷贝数据
        OrderLogistics orderLogistics = BeanHelper.copyProperties(address, OrderLogistics.class);
        /*
        必须设置订单编号
        此处应该采用webService技术来调用接口，获取其他物流系统的数据
         */
        orderLogistics.setOrderId(order.getOrderId());//订单编号
        orderLogistics.setLogisticsNumber("SF000001");//物流单号
        orderLogistics.setLogisticsCompany("顺丰物流");//物流公司名称

        orderLogisticsMapper.insert(orderLogistics);

        //扣减库存
        //此处需要解决分布式事务的问题。若在此步之后出错，订单、订单详情、订单物流表的数据会回滚，但订库存并不会回滚
        itemClient.minesStock(cartMap);

        return order.getOrderId();
    }

    /**
     * 根据订单Id查询订单的vo对象返回
     */
    public OrderVo findOrderVoById(Long orderId) {
        //查询订单
        Order order = orderMapper.selectById(orderId);
        //拷贝数据
        OrderVo orderVo = BeanHelper.copyProperties(order, OrderVo.class);

        //查询并封装详情数据
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(Wrappers.query(orderDetail));
        orderVo.setDetailList(orderDetailList);

        //查询并封装物流数据
//        OrderLogistics orderLogistics = new OrderLogistics();
//        orderLogistics.setOrderId(orderId);
//        OrderLogistics queryOrderLogistics = orderLogisticsMapper.selectOne(Wrappers.query(orderLogistics));

        orderVo.setLogistics(orderLogisticsMapper.selectById(orderId)); //订单表与订单物流表共用主键

        return orderVo;
    }
}
