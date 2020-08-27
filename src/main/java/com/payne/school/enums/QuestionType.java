package com.payne.school.enums;

/**
 * 设备状态
 *
 * @author xinchao.pan
 */
public enum QuestionType {

    JST("base-1", "计算题"),

    LST("base-10", "论述题"),

    XZT("base-11", "写作题"),

    SJT("base-12", "设计题"),

    YWT("base-13", "业务题"),

    ZCFY("base-14", "字词翻译"),

    HTT("base-2", "绘图题"),

    BCT("base-3", "编程题"),

    JCT("base-4", "纠错题"),

    JDT("base-5", "简答题"),

    MCJS("base-6", "名词解释"),

    DYFY("base-7", "短语翻译"),

    JZFY("base-8", "句子翻译"),

    DLFX("base-9", "段落翻译"),

    DANXT("danxuan-1", "单选题"),

    TL1("danxuan-2", "听力一"),

    YYDH("danxuan-3", "英语对话"),

    DUOXT("duoxuan-1", "多选题"),

    WDT("jianda-1", "问答题"),

    TKT("jianda-2", "填空题"),

    TLTKT("jianda-3", "听力填空题"),

    PDT("panduan-1", "判断题"),

    ZHT("zuhe-1", "组合题"),

    ALFX("zuhe-2", "案例分析"),

    YDLJ("zuhe-3", "阅读理解"),

    SXXWXTK("zuhe-4", "十选项完形填空"),

    TL2("zuhe-5", "听力二"),

    WXXWXTK("zuhe-6", "五选项完型填空"),

    SCT("shangchuan-1", "上传题"),

    SCTFZG("shangchuan-2", "上传题(非主观)");

    private final String code;

    private final String note;

    QuestionType(String code, String note) {
        this.code = code;
        this.note = note;
    }

    public String getCode() {
        return code;
    }

    public String getNote() {
        return note;
    }

    public static String getNote(String code) {
        for (QuestionType danXuan : QuestionType.values()) {
            if (danXuan.getCode() == code) {
                return danXuan.getNote();
            }
        }
        return null;
    }

    public static String getCode(String note) {
        for (QuestionType danXuan : QuestionType.values()) {
            if (danXuan.getNote().equals(note)) {
                return danXuan.getCode();
            }
        }
        return null;
    }
}
