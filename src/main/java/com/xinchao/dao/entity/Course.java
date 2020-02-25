package com.xinchao.dao.entity;

import lombok.Data;

/**
 *  专业课表
 * @author xinhchao.pan 2020-02-25
 */
@Data
public class Course {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 专业课id
     */
    private String keId;

    /**
     * 专业课名称
     */
    private String keName;

    /**
     * 所属学生uid
     */
    private String uid;

    /**
     * 题库数量
     */
    private Integer amount;

    /**
     * 是否完成归档考试库; 0:表示未完成;1:表示完成
     */
    private Integer isComplete;

}
