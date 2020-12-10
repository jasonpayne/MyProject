package com.payne.shop.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单信息表
 *
 * @author panxinchao 2020-10-26
 */
@Data
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * sku_id
     */
    private String skuId;

    /**
     * 数量
     */
    private String quantity;

    /**
     * 邮编
     */
    private String zipCode;

    /**
     * 地址
     */
    private String address;

    /**
     * 订单联系人
     */
    private String shipName;

    /**
     * 电话
     */
    private String phone;

    /**
     * 订单时间
     */
    private String orderTime;

    /**
     * 是否存在一起发货的订单（默认不存在）
     */
    private Boolean repeatFlag = false;

}