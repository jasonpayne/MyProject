package com.xinchao.dao.entity;

import io.swagger.models.auth.In;
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
     * 答案长度(多选题)
     */
    private String answerSize;

    /**
     * 答案状态;-1表示初始化; 0:表示错误;1:表示正确;2:答案太多，暂时不枚举
     */
    private Integer isCorrect;

    /**
     * '题目内容'
     */
    private String questName;

    /**
     * '答案内容'
     */
    private String answersName;

}
