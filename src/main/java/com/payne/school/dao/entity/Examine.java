package com.payne.school.dao.entity;

import lombok.Data;

/**
 *  考题表
 * @author xinchao.pan 2020-02-22
 */
@Data
public class Examine {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 问题uuid
     */
    private String questId;

    /**
     * 所属课程（4位数）
     */
    private String keId;

    /**
     * 课程名称
     */
    private String keName;

    /**
     * 题目内容
     */
    private String questName;

    /**
     * 选项内容
     */
    private String answersName;

    /**
     * 客观答案
     */
    private String objAnswer;

    /**
     * 主观答案
     */
    private String subjAnswer;

    /**
     * 题目类型
     */
    private String questType;

    /**
     * 是否有答案。0：初始化；1：有答案(网站)；2：没有答案（需要去人工做题）；-1：错误题目
     */
    private Integer isReply;

    /**
     * 最后所属学生
     */
    private String uid;
}