package com.payne.shop.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单信息表
 *
 * @author panxinchao 2020-10-26
 */
@Data
public class LogisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    private Integer id;

    /**
     * yahoo订单号
     */
    private String orderCode;

    /**
     * OCS订单号
     */
    private String inTranCode;

    /**
     * 转运订单号
     */
    private String outTranCode;

    /**
     * 取件时间
     */
    private String obtainTime;

    /**
     * 运单详情
     */
    private String tranDetail;

    /**
     * 签收时间
     */
    private String signTime;

    /**
     * 快递状态
     */
    private Integer status;


    //映射用的
    /**
     * sku_id
     */
    private String skuId;

    /**
     * 数量
     */
    private String quantity;

}