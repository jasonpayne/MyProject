package com.xinchao.utils;

import com.xinchao.dao.entity.QuestionBank;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class Test {

    public static void main(String[] args) {
        List<String> result = new ArrayList<String>();
//        String source = "<a title=中国体育报 href=''>aaa</a><a title='北京日报' href=''>bbb</a>";
//        String source = "<input type=\"radio\" value=\"B\" name=\"666101071001\">";
        String source = "<td width='43' height='20'>20</td>";
//        Matcher m = compile("<td[^>]*>([^<]*)</td>").matcher(source);
        Matcher m = compile("(?<=<td>).*(?=</td>)").matcher(source);
        while (m.find()) {
            String r = m.group(1);
            result.add(r);
        }
        System.out.println(result);
    }

}
