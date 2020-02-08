package com.xinchao.service.impl;

import com.xinchao.dao.entity.User;
import com.xinchao.model.MajorTest;
import com.xinchao.service.QuestionService;
import com.xinchao.utils.HttpClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * 实现通用方法
 * @author xinchao.pan
 * @date 2020/02/04
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    private static final String denglu = "http://202.196.64.120/vls2s/zzjlogin.dll/login";

    private static final String kecheng = "http://123.15.57.74/vls5s/vls3isapi2.dll/";

    /**
     * [查询] 根据主键 id 查询
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    @Override
    public boolean login(User user){
        String loginHtml = HttpClient.sendPost(denglu, "uid="+user.getUid()+"&pw="+user.getPw());
        if(loginHtml.contains("你无法进入系统")) {
            return false;
        }else {
            return true;
        }
    }

    /**
     * 专业课列表
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    @Override
    public List<String> majorList(User user){
        String homePageUrl = kecheng+"getfirstpage?ptopid="+user.getPtopId();
        String homePage = HttpClient.sendGet(homePageUrl,  null);
        if(homePage.contains("你的登录信息已经失效")){
            return null;
        }else {
            List<String> majorList = new ArrayList<String>();
            Matcher matcher = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>").matcher(homePage);
            while (matcher.find()) {
                String major = matcher.group(1).replace("\"", "");
                if(major.contains("lookonecourse") && !major.contains("0027") && !major.contains("9011")){
                    majorList.add(major);
                }
            }
            return majorList;
        }
    }

    /**
     * 专业课详情（测试方向）
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    @Override
    public MajorTest majorDetailToTest(String majorUrl){
        MajorTest model = new MajorTest();
        // 获取keId
        String keId = majorUrl.substring(majorUrl.indexOf("keid=")+"keid=".length());
        model.setKeId(keId);
        // 进入某个专业课的练习调用一
        String majorTestTodoHtml = HttpClient.sendGet(majorUrl,  null);
        String majorTestTodoUrl = null;
        Matcher majorTestTodoMatcher = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>").matcher(majorTestTodoHtml);
        while (majorTestTodoMatcher.find()) {
            String r = majorTestTodoMatcher.group(1).replace("\"", "");
            if(r.contains("mygetonetest")){
                majorTestTodoUrl = r;
                break;
            }
        }
        // 获取ruid
        String ruid = majorTestTodoUrl.substring(majorTestTodoUrl.indexOf("ruid=")+"ruid=".length(), majorTestTodoUrl.indexOf("&keid="));
        model.setRuid(ruid);
        // 进入某个专业课的练习调用二
        String majorTestHtml = HttpClient.sendGet(majorTestTodoUrl,  null);
        String majorTestUrl = null;
        Matcher majorTestMatcher = compile("url=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)").matcher(majorTestHtml);
        while (majorTestMatcher.find()) {
            String r = majorTestMatcher.group(1).replace("\'", "");
            System.out.println(r);
            majorTestUrl = r;
        }
        model.setTestListUrl(majorTestUrl);
        return model;
    }

    /**
     * 测试列表
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    @Override
    public List<Map<String,Object>> testList(String testListUrl){
        // Todo 分值没有获取。可用用来判断是否还需要答题
        List<Map<String,Object>> testList = new ArrayList<>();
        Map<String,Object> testMap = new HashMap<>();
        String testListHtml = HttpClient.sendGet(testListUrl,  null);
        Matcher testListMatcher = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>").matcher(testListHtml);
        while (testListMatcher.find()) {
            String r = testListMatcher.group(1).replace("\"", "").replace("testonce0","testonce");
            System.out.println(r);
            testMap.put(r,10);
            testList.add(testMap);
        }
        return testList;
    }

    /*@Override
    public MajorTest testDetail(String majorUrl){
        String xiangqing = needOneQuerylist.get(4);
        String needlianxilistdetail = HttpClient.sendGet(xiangqing,  null);
        Pattern pattern5 = compile("<input" + "[^<>]*?\\s" + "name=['\"]?(.*?)['\"]?(\\s.*?)?>");
        Matcher matcher5 = pattern5.matcher(needlianxilistdetail);
        // 本章需要完成的全部题目
        TreeSet<String> questionSet = new TreeSet();
        // 章节zhang
        String zhang = xiangqing.substring(xiangqing.indexOf("zhang=")+"zhang=".length());
        while (matcher5.find()) {
            String r = matcher5.group(1);
            if(r.contains(zhang)){
                if(r.contains("A") || r.contains("B") || r.contains("C") || r.contains("D")){
                    questionSet.add(r.substring(0,r.length()-1));
                }else {
                    questionSet.add(r);
                }
            }
        }
    }*/

    /**
     * [新增]
     * @author xinchao.pan
     * @date 2020/02/04
     **/
//    int insert(QuestionBank question);

    /**
     * 人像批量添加
     * @param list
     * @return
     */
//    int insertBatch(@Param("list") List<QuestionBank> list);

    /**
     * [更新]
     * @author xinchao.pan
     * @date 2020/02/04
     **/
//    int update(QuestionBank question);

}
