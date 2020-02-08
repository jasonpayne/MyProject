package com.xinchao.enums;

/**
 * 设备状态
 *
 * @author ju.wang@bitmain.com
 */
public enum PanDuan {

    Y(1, "Y"),
    N(2, "N");

    private final int code;

    private final String note;

    PanDuan(int code, String note) {
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
        for (PanDuan panDuan : PanDuan.values()) {
            if (panDuan.getCode() == code) {
                return panDuan.getNote();
            }
        }
        return null;
    }

    public static int getCode(String note) {
        for (PanDuan panDuan : PanDuan.values()) {
            if (panDuan.getNote().equals(note)) {
                return panDuan.getCode();
            }
        }
        return 0;
    }
}
