package com.xinchao.dao.entity;

import lombok.Data;

/**
 *  用户表
 * @author xinchao.pan 2020-02-07
 */
@Data
public class User {

    private static final long serialVersionUID = 1L;

    /**
     * 账户
     */
    private String uid;

    /**
     * 密码
     */
    private String pw;

    /**
     * ptopId
     */
    private String ptopId;

    /**
     * ruid
     */
    private String ruId;

    /**
     * 是否上课完成。0:未完成；1:已完成
     */
    private Integer isClazz;

    /**
     * 是否测试完成。0:未完成；1:已完成
     */
    private Integer isTest;

}
