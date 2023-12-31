package com.leyou.pojo.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 创建时间：2020/12/30
 * 订单表实体类
 * @author wpf
 */
@Data
@TableName("tb_order")
public class Order {
    /**
     * 订单编号
     */
    //本项目使用的是雪花算法来生成分布式Id
    //@TableId(type = IdType.ID_WORKER_STR) 分布式全局唯一ID 字符串类型
    //@TableId(type = IdType.ID_WORKER) 分布式全局唯一ID 长整型类型
    @TableId(type = IdType.NONE) //表示无状态，即自己维护主键Id
    private Long orderId;
    /**
     * 商品金额
     */
    private Long totalFee;
    /**
     * 邮费
     */
    private Long postFee;
    /**
     * 实付金额
     */
    private Long actualFee;
    /**
     * 付款方式：1:在线支付, 2:货到付款
     */
    private Integer paymentType;
    /**
     * 优惠促销的活动id，
     */
    private String promotionIds;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 付款时间
     */
    private Date payTime;
    /**
     * 发货时间
     */
    private Date consignTime;
    /**
     * 确认收货时间
     */
    private Date endTime;
    /**
     * 交易关闭时间
     */
    private Date closeTime;
    /**
     * 评价时间
     */
    private Date commentTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 发票类型，0无发票，1普通发票，2电子发票，3增值税发票
     */
    private Integer invoiceType;
    /**
     *  订单来源 1:app端，2：pc端，3：微信端
     */
    private Integer sourceType;
}
