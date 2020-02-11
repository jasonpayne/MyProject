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
    A(12, "A"),
    B(13, "B"),
    C(14, "C"),
    D(15, "D"),
    // ==========================E===========================
    ABCDE(16, "A,B,C,D,E"),
    ABCE(17, "A,B,C,E"),
    ABDE(18, "A,B,D,E"),
    ACDE(19, "A,C,D,E"),
    BCDE(20, "B,C,D,E"),
    ABE(21, "A,B,E"),
    ACE(22, "A,C,E"),
    ADE(23, "A,D,E"),
    BCE(24, "B,C,E"),
    BDE(25, "B,D,E"),
    CDE(26, "C,D,E"),
    AE(27, "A,E"),
    BE(28, "B,E"),
    CE(29, "C,E"),
    DE(30, "D,E"),
    E(31, "E");
    // ==========================F===========================
    /*ABCDEF(27,"A,B,C,D,E,F"),
    ABCDF(28,"A,B,C,D,F"),
    ABCEF(29,"A,B,C,E,F"),
    ABDEF(30,"A,B,D,E,F"),
    ACDEF(31,"A,C,D,E,F"),
    BCDEF(32,"B,C,D,E,F"),
    ABCF(33,"A,B,C,F"),
    ABDF(34,"A,B,D,F"),
    ABEF(35,"A,B,E,F"),
    ACDF(36,"A,C,D,F"),
    ACEF(37,"A,C,E,F"),
    ADEF(38,"A,D,E,F"),
    CDEF(39,"C,D,E,F"),
    BDEF(40,"B,D,E,F"),
    BCEF(41,"B,C,E,F"),
    BCDF(42,"B,C,D,F"),
    ABF(43,"A,B,F"),
    ACF(44,"A,C,F"),
    ADF(45,"A,D,F"),
    AEF(46,"A,E,F"),
    BCF(47,"B,C,F"),
    BDF(48,"B,D,F"),
    BEF(49,"B,E,F"),
    CDF(50,"C,D,F"),
    CEF(51,"C,E,F"),
    DEF(52,"D,E,F"),
    AF(53,"A,F"),
    BF(54,"B,F"),
    CF(55,"C,F"),
    DF(56,"D,F"),
    EF(57,"E,F"),
    F(57,"F"),*/

    // ==========================G===========================
    /*ABCDEFG(58,"A,B,C,D,E,F,G"),
    ABCDEG(59,"A,B,C,D,E,G"),
    ABCDFG(60,"A,B,C,D,F,G"),
    ABCEFG(61,"A,B,C,E,F,G"),
    ABDEFG(62,"A,B,D,E,F,G"),
    ACDEFG(63,"A,C,D,E,F,G"),
    BCDEFG(64,"B,C,D,E,F,G"),

    CDEFG(65,"C,D,E,F,G"),
    BDEFG(66,"B,D,E,F,G"),
    BCEFG(67,"B,C,E,F,G"),
    BCDFG(68,"B,C,D,F,G"),
    BCDEG(69,"B,C,D,E,G"),
    ADEFG(70,"A,D,E,F,G"),
    ACEFG(71,"A,C,E,F,G"),
    ACDFG(72,"A,C,D,F,G"),
    ACDEG(73,"A,C,D,E,G"),
    ABEFG(74,"A,B,E,F,G"),
    ABDFG(75,"A,B,D,F,G"),
    ABDEG(76,"A,B,D,E,G"),
    ABCFG(77,"A,B,C,F,G"),
    ABCEG(78,"A,B,C,E,G"),
    ABCDG(79,"A,B,C,D,G"),

    ABCG(80,"A,B,C,G"),
    ABDG(81,"A,B,D,G"),
    ABEG(82,"A,B,E,G"),
    ABFG(83,"A,B,F,G"),
    ACDG(84,"A,C,D,G"),
    ACEG(85,"A,C,E,G"),
    ACFG(86,"A,C,F,G"),
    ADEG(87,"A,D,E,G"),
    ADFG(88,"A,D,F,G"),
    AEFG(89,"A,E,F,G"),
    BCDG(90,"B,C,D,G"),
    BCEG(91,"B,C,E,G"),
    BCFG(92,"B,C,F,G"),
    BDEG(93,"B,D,E,G"),
    BDFG(94,"B,D,F,G"),
    BEFG(95,"B,E,F,G"),
    CDEG(96,"C,D,E,G"),
    CDFG(97,"C,D,F,G"),
    CEFG(98,"C,E,F,G"),
    DEFG(99,"D,E,F,G");*/

    // ==========================H===========================

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
