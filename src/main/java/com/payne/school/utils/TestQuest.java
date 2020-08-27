package com.payne.school.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestQuest {

    /**
     * 假设你要给你的IDE写一个插件，查询某个方法所有的完整调用链。
     * 比如A被B,C,D调用，B又被E,F调用，C被G,H调用，那么其完整的调用链将会是棵树：
     * A - B
     *       - E
     *       - F
     *   - C
     *       - G
     *       - H
     *   - D
     *
     * IDE提供的api，只提供一个方法getCaller，用这个方法可以查询某个方法的直接调用者有哪些。
     * 比如方法A被B,C,D调用，那么getCaller(A)会返回调用列表，B, C, D。
     *
     * 请编写代码，查找某个方法的完整调用链（树状结构）。
     */

    /**
     * 方法一
     *
     */

    public static void main(String[] args) {
        List list = fun("A");
        System.out.println(list);
    }

    /**
     * idea内部类
     * @param fu
     * @return
     */
    public static List<String> getIDECaller0(String fu){
        List<String> list = new ArrayList<>();
        HashMap<String,String> map  = new HashMap<>();
        map.put("B","A");
        map.put("C","A");
        map.put("D","A");
        map.put("E","B");
        map.put("F","B");
        map.put("G","C");
        map.put("H","C");
        for (Map.Entry<String, String> m : map.entrySet()) {
            if(fu.equals(m.getValue())){
                list.add(m.getKey());
            }
        }
        return list;
    }

    public static List<String> fun(String funName){
        List<String> result = new ArrayList<>();
        List<String> all = setChildren(result, funName);
        List<String> mubiao = new ArrayList<>();
        mubiao.addAll(all);

        for(int i=0; i<mubiao.size() ; i++){
            for (String jihe : all){
                if(jihe.contains(mubiao.get(i))&& !jihe.equals(mubiao.get(i))){
                    mubiao.remove(i);
                }
            }
        }
        return mubiao ;
    }


    private static List<String> setChildren(List<String> result, String funA) {
        List<String> sonList = getIDECaller0(funA);
        for(String query : sonList){
            for(int i=0; i < result.size() ; i++){
                if(result.get(i).substring(result.get(i).length()-1).equals(funA)){
                    result.add(result.get(i)+"-->"+query);
                }
            }
            result.add(funA+"-->"+query);
            setChildren(result, query);
        }
        return result;
    }
}
