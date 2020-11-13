package com.payne.school.model;

import lombok.Data;

/**
 * 用户表
 *
 * @author xinchao.pan 2020-02-07
 */
@Data
public class User {

    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    private String name;

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
     * 专业
     */
    private String major;

    /**
     * 年级（入学时间）
     */
    private String grade;

    /**
     * 是否上课完成。0:未完成；1:已完成
     */
    private Integer isClazz;

    /**
     * 是否测试完成。0:未完成；1:已完成
     */
    private Integer isTest;

}
