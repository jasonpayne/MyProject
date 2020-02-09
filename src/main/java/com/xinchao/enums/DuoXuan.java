package com.xinchao.enums;

/**
 * 设备状态
 *
 * @author ju.wang@bitmain.com
 */
public enum DuoXuan {

    ABCD(1, "A,B,C,D"),
    ABC(2, "A,B,C"),
    ABD(3, "A,B,D"),
    ACD(4, "A,C,D"),
    BCD(5, "B,C,D"),
    AB(6, "A,B"),
    AC(7, "A,C"),
    AD(8, "A,D"),
    BC(9, "B,C"),
    BD(10, "B,D"),
    CD(11, "C,D"),
    ABCDE(12, "A,B,C,D,E"),
    ABCE(13, "A,B,C,E"),
    ABDE(14, "A,B,D,E"),
    ACDE(15, "A,C,D,E"),
    BCDE(16, "B,C,D,E"),
    ABE(17, "A,B,E"),
    ACE(18, "A,C,E"),
    ADE(19, "A,D,E"),
    BCE(20, "B,C,E"),
    BDE(21, "B,D,E"),
    CDE(22, "C,D,E"),
    AE(23, "A,E"),
    BE(24, "B,E"),
    CE(25, "C,E"),
    DE(26, "D,E");

    private final int code;

    private final String note;

    DuoXuan(int code, String note) {
        this.code = code;
        this.note = note;
    }

    public int getCode() {
        return code;
    }

    public String getNote() {
        return note;
    }

    public static String getNote(int code) {
        for (DuoXuan duoXuan : DuoXuan.values()) {
            if (duoXuan.getCode() == code) {
                return duoXuan.getNote();
            }
        }
        return null;
    }

    public static int getCode(String note) {
        for (DuoXuan duoXuan : DuoXuan.values()) {
            if (duoXuan.getNote().equals(note)) {
                return duoXuan.getCode();
            }
        }
        return 0;
    }
}
