package com.xinchao.controller;

import com.xinchao.dao.entity.ClazzUser;
import com.xinchao.dao.entity.TestUser;
import com.xinchao.dao.entity.User;
import com.xinchao.dao.mapper.AnswerMapper;
import com.xinchao.dao.mapper.ClazzUserMapper;
import com.xinchao.dao.mapper.TestUserMapper;
import com.xinchao.dao.mapper.UserMapper;
import com.xinchao.service.ClazzService;
import com.xinchao.service.QuestionService;
import com.xinchao.utils.HttpClient;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * QuestionController 测试
 * @author xinchao.pan 2020-02-04
 */
@RestController
@RequestMapping("/question")
public class ZhengZhouController {

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    TestUserMapper testUserMapper;

    @Autowired
    ClazzUserMapper clazzUserMapper;

    @Autowired
    QuestionService questionService;

    @Autowired
    ClazzService clazzService;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(@RequestBody User user) {
        try {
            //发送 POST 请求登陆,注册已知课程
            String ptopId = questionService.login(user);
            if(ptopId == null) {
                return "对不起，你输入的账号和密码未通过系统的验证";
            }else{
                User loginInfo = userMapper.login(user);
                if(null != loginInfo){
                    if(loginInfo.getIsClass() == 0 || loginInfo.getIsClass()==0){
                        user.setPtopId(ptopId);
                        userMapper.update(user);
//                        register0(user);
                        return "已经注册成功，正在操作，请稍等登陆查看";
                    }else {
                        return "已经注册成功，练习和答题已完成，请立即登陆查看";
                    }
                }else{
                    // 插入系统，并且初始化数据
                    user.setPtopId(ptopId);
                    user.setIsClass(0);
                    user.setIsTest(0);
                    userMapper.insert(user);
                    return "刚刚注册成功，正在操作，请稍等登陆查看。"+register0(user);
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String register0(User user) {

        try {
            String ptopId = null;
            // 打开学习主页
            String reqFinal = "http://171.8.225.133/vls5s/vls3isapi2.dll/getfirstpage?ptopid="+user.getPtopId();
            String allclass = HttpClient.sendGet(reqFinal, null);
            if(allclass.contains("你的登录信息已经失效")){
                ptopId = questionService.login(user);
                reqFinal = "http://171.8.225.133/vls5s/vls3isapi2.dll/getfirstpage?ptopid="+ptopId;
                allclass = HttpClient.sendGet(reqFinal, null);
            }
            System.out.println("===========================本学期所有需要学习课程==========================");
            List<String> keChengList = new ArrayList<String>();
            Pattern pattern = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
            if(allclass.contains("你应已修习")){
                allclass = allclass.substring(0,allclass.indexOf("你应已修习"));
            }
            Matcher matcher = pattern.matcher(allclass);
            while (matcher.find()) {
                String r = matcher.group(1).replace("\"", "");
                if (r.contains("lookonecourse")) {
                    String keId = r.substring(r.indexOf("keid=") + "keid=".length());
                    keChengList.add(keId);
                    // 听课
                    String KeChengDetailUrl = "http://171.8.225.133/vls5s/vls3isapi2.dll/lookonecourse?ptopid="+user.getPtopId()+"&keid="+keId;
                    String KeChengDetailHtml = HttpClient.sendGet(KeChengDetailUrl, null);
                    if(KeChengDetailHtml.contains("你的登录信息已经失效")){
                        ptopId = questionService.login(user);
                        KeChengDetailUrl = "http://171.8.225.133/vls5s/vls3isapi2.dll/lookonecourse?ptopid="+ptopId+"&keid="+keId;
                        KeChengDetailHtml = HttpClient.sendGet(KeChengDetailUrl, null);
                    }
                    Document document = Jsoup.parse(KeChengDetailHtml);
                    String text = document.body().text().trim();
                    if(!text.contains("共10分，你已取得10分") && !text.contains("因库中无有效课件，你直接取得10分")){
                        ClazzUser clazzUser = new ClazzUser();
                        clazzUser.setClzssId(keId);
                        clazzUser.setUid(user.getUid());
                        String score = text.substring(text.indexOf("共10分，你已取得")+"共10分，你已取得".length(), text.indexOf("共10分，你已取得")+"共10分，你已取得".length()+1);
                        clazzUser.setScore(Integer.valueOf(score));
                        clazzUser.setIsComplete(0);
                        clazzUserMapper.insert(clazzUser);
                    }
                }
            }
            System.out.println("===========================获取sid参数========================================");
            String sid = null;
            for(String keCheng : keChengList){
                String testUrl = "http://171.8.225.170/vls2s/vls3isapi.dll/mygetonetest?ptopid=" + user.getPtopId() + "&keid=" + keCheng;
                String testHtml = HttpClient.sendGet(testUrl, null);
                // 测试
                if(!testHtml.contains("有效自测题数量不足")){
                   sid = testHtml.substring(testHtml.indexOf("&sid=") + "&sid=".length(), testHtml.indexOf("&wheres="));
                   break;
                }
            }
            if(StringUtils.isBlank(sid)){
                return "没有需要测试的科目";
            }
            System.out.println("===========================添加本学期测试列表========================================");
            String needQuery = "http://171.8.225.170/vls2s/vls3isapi.dll/myviewdatalist";
            String needParam = "ptopid=" + user.getPtopId() + "&sid=" + sid;
            System.out.println(needQuery+"?"+needParam);
            List<String> testList = new ArrayList<>();
            int sum = 0;
            for (int i = 1; i <= 10; i++) {
                System.out.println(needQuery + "?" + needParam + "&pn=" + i);
                String testHtml = HttpClient.sendPost(needQuery, needParam + "&pn=" + i);
                if(testHtml.contains("你的登录信息已经失效")){
                    ptopId = questionService.login(user);
                    needParam = "ptopid=" + ptopId + "&sid=" + sid;
                    testHtml = HttpClient.sendGet(needQuery, needParam);
                }
                Pattern pattern4 = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
                Matcher matcher4 = pattern4.matcher(testHtml);
                while (matcher4.find()) {
                    String r = matcher4.group(1).replace("\"", "").replace("testonce0", "testonce");
                    String ZhangId = r.substring(r.indexOf("zhang=") + "zhang=".length());
                    String keId =  ZhangId.substring(0 , 4);
                    if(keChengList.contains(keId)){
                        TestUser testUser = new TestUser();
                        testUser.setZhangId(ZhangId);
                        testUser.setUid(user.getUid());
//                    testUser.setScore();//是多少就是多少！
                        // TODO
                        testUser.setIsComplete(0);
                        testUser.setIsSubmit(0);
                        testUserMapper.insert(testUser);
                        testList.add(r);
                    }
                }
                if (i == 1) {
                    sum = Integer.valueOf(testHtml.substring(testHtml.indexOf("共") + 1, testHtml.indexOf("条"))) / 25 + 1;
                }
                if (i == sum) {
                    break;
                }
            }
            if(null != testList && testList.size()>0){
                return "需要做"+testList.size()+"套测试题";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return null;
    }

    @RequestMapping(value = "/openTest", method = RequestMethod.GET)
    public String openTest() {
        try {
            return questionService.openTest();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/submitAnswer", method = RequestMethod.GET)
    public String submitAnswer() {
        try {
            return questionService.submitAnswer();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/listenClazz", method = RequestMethod.GET)
    public String listenClazz() {
        try {
            return clazzService.listenClazz();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }
}
