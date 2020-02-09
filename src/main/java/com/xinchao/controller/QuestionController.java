package com.xinchao.controller;

import com.xinchao.dao.entity.Answer;
import com.xinchao.dao.entity.TestUser;
import com.xinchao.dao.entity.User;
import com.xinchao.dao.mapper.AnswerMapper;
import com.xinchao.dao.mapper.TestUserMapper;
import com.xinchao.dao.mapper.UserMapper;
import com.xinchao.enums.DanXuan;
import com.xinchao.enums.DuoXuan;
import com.xinchao.enums.PanDuan;
import com.xinchao.service.QuestionService;
import com.xinchao.utils.HttpClient;
import com.xinchao.utils.Test;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.compile;

/**
 * QuestionController
 * @author xinchao.pan 2020-02-04
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    TestUserMapper testUserMapper;

    @Autowired
    QuestionService questionService;

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
            String reqFinal = "http://171.8.225.133/vls5s/vls3isapi2.dll/getfirstpage?ptopid="+user.getPtopId();
            System.out.println(reqFinal);
            // 打开学习主页
            String allclass = HttpClient.sendGet(reqFinal, null);
            System.out.println("===========================本学期所有需要学习课程==========================");
            List<String> needList = new ArrayList<String>();
            Pattern pattern = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
            if(allclass.contains("你应已修习")){
                allclass = allclass.substring(0,allclass.indexOf("你应已修习"));
            }
            Matcher matcher = pattern.matcher(allclass);
            while (matcher.find()) {
                String r = matcher.group(1).replace("\"", "");
                if (r.contains("lookonecourse")) {
                    System.out.println(r);
                    needList.add(r.substring(r.indexOf("keid=") + "keid=".length()));
                }
            }
            System.out.println("===========================获取sid参数========================================");
            String sid = null;
            for(String needClass : needList){
                String need = "http://171.8.225.170/vls2s/vls3isapi.dll/mygetonetest?ptopid=" + user.getPtopId() + "&keid=" + needClass;
                String need0 = HttpClient.sendGet(need, null);
                if(need0.contains("有效自测题数量不足")){
                    continue;
                }else {
                    sid = need0.substring(need0.indexOf("&sid=") + "&sid=".length(), need0.indexOf("&wheres="));
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
                Pattern pattern4 = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
                Matcher matcher4 = pattern4.matcher(testHtml);
                while (matcher4.find()) {
                    String r = matcher4.group(1).replace("\"", "").replace("testonce0", "testonce");
                    TestUser testUser = new TestUser();
                    testUser.setZhangId(r.substring(r.indexOf("zhang=") + "zhang=".length()));
                    testUser.setUid(user.getUid());
//                    testUser.setScore();//是多少就是多少！
                    // TODO
                    testUser.setIsComplete(0);
                    testUser.setIsSubmit(0);
                    testUserMapper.insert(testUser);
                    testList.add(r);
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
            String ptopId = "";
            TestUser testUser = new TestUser();
            testUser.setIsComplete(0);
            testUser.setIsSubmit(0);
            List<TestUser> testList = testUserMapper.selectForList(testUser);
            if(null == testList ||testList.size() == 0){
                return "没有打开的题目";
            }

            for (TestUser model : testList) {
                User user = new User();
                user.setUid(model.getUid());
                User nowUser = userMapper.selectOne(user);
                ptopId = nowUser.getPtopId();
                String testDetailUrl = "http://171.8.225.138/vls2s/vls3isapi.dll/testonce?ptopid=" + ptopId + "&zhang=" + model.getZhangId();
                String testDetailHtml = HttpClient.sendGet(testDetailUrl, null);
                if(testDetailHtml.contains("你的登录信息已经失效")){
                    ptopId = questionService.login(nowUser);
                    testDetailUrl = "http://171.8.225.138/vls2s/vls3isapi.dll/testonce?ptopid=" + ptopId + "&zhang=" + model.getZhangId();
                    testDetailHtml = HttpClient.sendGet(testDetailUrl, null);
                }
                Matcher testDetailMatcher = compile("<input" + "[^<>]*?\\s" + "name=['\"]?(.*?)['\"]?(\\s.*?)?>").matcher(testDetailHtml);
                // 本章需要完成的题目
                TreeSet<String> questionSet = new TreeSet();
                while (testDetailMatcher.find()) {
                    String r = testDetailMatcher.group(1);
                    if(r.contains(model.getZhangId())){
                        if(r.contains("A") || r.contains("B") || r.contains("C") || r.contains("D") || r.contains("E")){
                            questionSet.add(r.substring(0,r.length()-1));
                        }else {
                            questionSet.add(r);
                        }
                    }
                }
                // 初始化答案
                for (String str : questionSet) {
                    Answer info = new Answer();
                    info.setQuestId(str);
                    List<Answer> quest = answerMapper.selectForList(info);
                    if(null == quest || quest.size() == 0){
                        Answer isNotAnswer = new Answer();
                        isNotAnswer.setQuestId(str);
                        isNotAnswer.setZhengId(model.getZhangId());
                        if(str.contains(model.getZhangId()+1)){
                            isNotAnswer.setAnswers("A");
                        }
                        else if(str.contains(model.getZhangId()+2)){
                            isNotAnswer.setAnswers("A,B,C,D");
                        }
                        else if(str.contains(model.getZhangId()+3)){
                            isNotAnswer.setAnswers("Y");
                        }
                        isNotAnswer.setIsCorrect(-1);
                        // 如果不存在初始答案则要初始化
                        answerMapper.insert(isNotAnswer);
                    }
                }
                StringBuilder sb = new StringBuilder ();
                for (String str : questionSet){
                    sb.append(str+",");
                }
                model.setQuests(sb.toString());
            }
            if(null != testList && testList.size()>0){
                // 答题需要延迟2分钟提交
                int k = 1;
                while (k < 5) {
                    Thread.sleep(30100);
                    System.out.println("等待中========================"+k);
                    k++;
                }
                for (TestUser model : testList) {
                    model.setIsSubmit(1);
                    testUserMapper.update(model);
                }
                return "已经打开"+testList.size()+"道题目";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return null;
    }

    @RequestMapping(value = "/submitAnswer", method = RequestMethod.GET)
    public String submitAnswer() {
        try {
            String ptopId = "";
            TestUser testUser = new TestUser();
            testUser.setIsComplete(0);
            testUser.setIsSubmit(1);
            List<TestUser> testList = testUserMapper.selectForList(testUser);
            if(null == testList ||testList.size() == 0) {
                return "没有需要提交的测试";
            }
            for (TestUser model : testList) {
                List<String> questList = Arrays.asList(model.getQuests().split(","));
                Answer queryOld = new Answer();
                queryOld.setZhengId(model.getZhangId());
                List<Answer> answerList = answerMapper.selectForList(queryOld);
                // 等待提交的答案
                Map<String,String> answerMap = new TreeMap<>();
                for(String quest : questList){
                    for (Answer answerOld : answerList) {
                        if(quest.equals(answerOld.getQuestId()) && quest.contains(model.getZhangId()+1)) {
                            answerMap.put(quest, answerOld.getAnswers());
                            continue;
                        }else if(quest.equals(answerOld.getQuestId()) && quest.contains(model.getZhangId()+2)){
                            List<String> duoXuans = Arrays.asList(answerOld.getAnswers().split(","));
                            for(String duoXuan : duoXuans){
                                answerMap.put(quest+duoXuan ,duoXuan);
                            }
                            continue;
                        }else if(quest.equals(answerOld.getQuestId()) && quest.contains(model.getZhangId()+3)){
                            answerMap.put(quest, answerOld.getAnswers());
                            continue;
                        }
                    }
                }

                User user = new User();
                user.setUid(model.getUid());
                User nowUser = userMapper.selectOne(user);
                ptopId = nowUser.getPtopId();
                String param = "submitpaper=submit&ptopid="+ptopId+"&paperid="+user.getUid()+model.getZhangId();
                String answerParam = "";
                for (Map.Entry<String, String> map : answerMap.entrySet()) {
                    answerParam = answerParam + "&" + map.getKey() + "=" + map.getValue();
                }
                String submitURL = "http://171.8.225.138/vls2s/vls3isapi.dll/smpaper";
                String submitHtml = HttpClient.sendPost(submitURL, param + answerParam);
                if(submitHtml.contains("你的登录信息已经失效")){
                    ptopId = questionService.login(nowUser);
                    param = "submitpaper=submit&ptopid="+ptopId+"&paperid="+user.getUid()+model.getZhangId();
                    submitHtml = HttpClient.sendPost(submitURL, param + answerParam);
                }
                Matcher matcherPoint = compile("<span [^>]*>([^<]*)</span>").matcher(submitHtml);
                Integer score = 0;
                while (matcherPoint.find()) {
                    String m = matcherPoint.group(1);
                    if(m.contains("分")){
                        score = Integer.valueOf(m.substring(0,m.indexOf("分")));
                    }
                }
                if(submitHtml.contains("抱歉")) {
                    // TODO
                }
                if(submitHtml.contains("交卷操作成功完成")) {
                    Matcher submitMatcher = compile("<font [^>]*>([^<]*)</font>").matcher(submitHtml);
                    List<String> resultList = new ArrayList<>();
                    while (submitMatcher.find()) {
                        String r = submitMatcher.group(1);
                        if(r.contains("[对]")){
                            resultList.add("正确");
                        }else if(r.contains("[错]")){
                            resultList.add("错误");
                        }
                    }
                    for (Map.Entry<String, String> map : answerMap.entrySet()) {
                        for (int i = 0 ;0 < resultList.size(); i++) {
                            map.setValue(resultList.get(i));
                            resultList.remove(i);
                        }
                    }
                    System.out.println("===========================更新答案库========================================");
                    for (Map.Entry<String, String> map : answerMap.entrySet()) {
                        for (Answer answerNew : answerList) {
                            if(map.getKey().equals(answerNew.getQuestId())) {
                                if(map.getValue().equals("正确")){
                                    if(answerNew.getIsCorrect() != 1){
                                        answerNew.setIsCorrect(1);
                                        answerMapper.update(answerNew);
                                    }
                                }else if(map.getValue().equals("错误")){
                                    // 单选
                                    if(map.getKey().contains(model.getZhangId()+1)){
                                        answerNew.setAnswers(DanXuan.getNote(DanXuan.getCode(answerNew.getAnswers())+1));
                                    }
                                    // 多选
                                    else if(map.getKey().contains(model.getZhangId()+2)){
                                        answerNew.setAnswers(DuoXuan.getNote(DuoXuan.getCode(answerNew.getAnswers())+1));
                                    }
                                    // 判断
                                    else if(map.getKey().contains(model.getZhangId()+3)){
                                        answerNew.setAnswers(PanDuan.getNote(PanDuan.getCode(answerNew.getAnswers())+1));
                                    }
                                    answerNew.setIsCorrect(0);
                                    answerMapper.update(answerNew);
                                }
                                break;
                            }
                        }
                    }
                    model.setScore(score);
                    model.setIsSubmit(0);
                    if(score == 20){
                        model.setIsComplete(1);
                        System.out.println("已经完成答题");
                    }else{
                        System.out.println("当前:"+ score +"分，需要继续答题");
                    }
                    testUserMapper.update(model);
                }
            }
            if(null != testList && testList.size() > 0) {
                return "已经提交了"+testList.size()+"道测试";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return null;
    }
}
