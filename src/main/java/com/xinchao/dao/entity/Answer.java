package com.xinchao.dao.entity;

import lombok.Data;

@Data
public class Answer {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 所属课程
     */
    private String questId;

    /**
     * 所属章节
     */
    private String zhangId;

    /**
     * 所属课程
     */
    private String keId;

    /**
     * 答案
     */
    private String answers;

    /**
     * 答案状态;-1表示初始化; 0:表示错误;1:表示正确
     */
    private Integer isCorrect;

}
