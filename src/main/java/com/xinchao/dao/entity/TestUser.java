package com.xinchao.dao.entity;

import lombok.Data;

/**
 *  学生测试表
 * @author xinchao.pan 2020-02-08
 */
@Data
public class TestUser {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 所属章节
     */
    private String zhangId;

    /**
     * 所属学生
     */
    private String uid;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 是否完成; 0:表示未完成;1:表示完成
     */
    private Integer isComplete;

    /**
     * 是否能提交；0：不能，1表示能',
     */
    private Integer isSubmit;

}
