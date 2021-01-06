package com.payne.shop.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单时间
 *
 * @author xinchao.pam 2020-12-10
 */
@Data
public class ShopOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    private Integer id;

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 订单时间
     */
    private String orderTime;

    /**
     * 付款时间
     */
    private String payTime;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 邮编
     */
    private String postCode;

    /**
     * 地址
     */
    private String address;

    /**
     * 发货人
     */
    private String consignee;

    /**
     * 发货时间
     */
    private String deliveTime;

    /**
     * 联系电话
     */
    private String iphone;

}