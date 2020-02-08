package com.xinchao.utils;

import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;

public class StringUtils {

    public static void main(String[] args) {
        boolean aa = isEmptyOrWhitespace("");
//        boolean aa = isEmptyOrWhitespace("");
//        boolean aa = isEmptyOrWhitespace("  ");
        System.out.println(aa);
        /*String str = "dsads ad";
        StringBuffer sb = new StringBuffer();
        for (int i = 1;i<=str.length();i++){
            sb.append(" ");
        }
        System.out.println("==="+sb+"===");*/
    }


    public static boolean isEmptyOrWhitespace(String str) {
        // TODO: 请补充此函数，如果一个字符串是空或者只包含空格则返回true，其他情况返回false
        Boolean flag = false;
        try{
            if(str == ""){
                flag = true;
            }else{
                StringBuffer sb = new StringBuffer();
                for (int i = 1;i<=str.length();i++){
                        sb.append(" ");
                }
                if(str.equals(sb.toString())){
                    flag = true;
                }
            }
            return flag;
        }catch (Exception e){
            System.out.println("传参Str异常为null"+e);
        }
        return flag;
    }
}
