package com.xinchao.enums;

/**
 * 设备状态
 *
 * @author ju.wang@bitmain.com
 */
public enum DanXuan {

    A(1, "A"),
    B(2, "B"),
    C(3, "C"),
    D(4, "D");

    private final int code;

    private final String note;

    DanXuan(int code, String note) {
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
        for (DanXuan danXuan : DanXuan.values()) {
            if (danXuan.getCode() == code) {
                return danXuan.getNote();
            }
        }
        return null;
    }

    public static int getCode(String note) {
        for (DanXuan danXuan : DanXuan.values()) {
            if (danXuan.getNote().equals(note)) {
                return danXuan.getCode();
            }
        }
        return 0;
    }
}
