package com.payne.school.model;

import lombok.Data;

/**
 * 学生听课表
 *
 * @author xinchao.pan 2020-02-08
 */
@Data
public class ClazzUser {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 所属课程
     */
    private String clzssId;

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

}
