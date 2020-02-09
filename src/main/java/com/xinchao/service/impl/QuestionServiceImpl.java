package com.xinchao.service.impl;

import com.xinchao.dao.entity.Answer;
import com.xinchao.dao.entity.TestUser;
import com.xinchao.dao.entity.User;
import com.xinchao.dao.mapper.AnswerMapper;
import com.xinchao.dao.mapper.TestUserMapper;
import com.xinchao.dao.mapper.UserMapper;
import com.xinchao.enums.DanXuan;
import com.xinchao.enums.DuoXuan;
import com.xinchao.enums.PanDuan;
import com.xinchao.model.MajorTest;
import com.xinchao.service.QuestionService;
import com.xinchao.utils.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.compile;

/**
 * 实现通用方法
 * @author xinchao.pan
 * @date 2020/02/04
 * http://171.8.225.133/
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    TestUserMapper testUserMapper;

    private static final String denglu = "http://202.196.64.120/vls2s/zzjlogin.dll/login";

//    private static final String kecheng = "http://123.15.57.74/vls5s/vls3isapi2.dll/";
    private static final String kecheng = "http://171.8.225.133/vls5s/vls3isapi2.dll/";

    /**
     * [查询] 根据主键 id 查询
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    @Override
    public String login(User user){
        String loginHtml = HttpClient.sendPost(denglu, "uid="+user.getUid()+"&pw="+user.getPw());
        if(loginHtml.contains("你无法进入系统")) {
            return null;
        }else {
            String ptopId = loginHtml.substring(loginHtml.indexOf("ptopid=") + "ptopid=".length(), loginHtml.indexOf("&sid="));
            user.setPtopId(ptopId);
            userMapper.update(user);
            return ptopId;
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

    @Override
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
                    ptopId = login(nowUser);
                    testDetailUrl = "http://171.8.225.138/vls2s/vls3isapi.dll/testonce?ptopid=" + ptopId + "&zhang=" + model.getZhangId();
                    testDetailHtml = HttpClient.sendGet(testDetailUrl, null);
                }
                Matcher testDetailMatcher = compile("<input" + "[^<>]*?\\s" + "name=['\"]?(.*?)['\"]?(\\s.*?)?>").matcher(testDetailHtml);
                // 本章需要完成的全部题目
                TreeSet<String> questionSet = new TreeSet();
                // 章节zhang
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
            }
            if(null != testList && testList.size()>0){
                // 答题需要延迟2分钟提交
                int k = 1;
                while (k<5) {
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

    @Override
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
                Answer queryOld = new Answer();
                queryOld.setZhengId(model.getZhangId());
                List<Answer> answerOlds = answerMapper.selectForList(queryOld);
                List<Answer> answerOldList = answerOlds.stream().sorted(Comparator.comparing(Answer::getQuestId))
                        .collect(Collectors.toList());
                // 等待提交的答案
                Map<String,String> answerMap = new TreeMap<>();
                for (Answer answerOld : answerOldList) {
                    if(answerOld.getQuestId().contains(model.getZhangId()+1)) {
                        answerMap.put(answerOld.getQuestId(), answerOld.getAnswers());
                    }else if(answerOld.getQuestId().contains(model.getZhangId()+2)){
                        List<String> duoXuans = Arrays.asList(answerOld.getAnswers().split(","));
                        for(String duoXuan : duoXuans){
                            answerMap.put(answerOld.getQuestId()+duoXuan ,duoXuan);
                        }
                    }else if(answerOld.getQuestId().contains(model.getZhangId()+3)){
                        answerMap.put(answerOld.getQuestId(), answerOld.getAnswers());
                    }
                }
                User user = new User();
                user.setUid(model.getUid());
                User nowUser = userMapper.selectOne(user);
                ptopId = nowUser.getPtopId();
                String param = "submitpaper=submit&ptopid="+ptopId+"&paperid="+user.getUid()+model.getZhangId();
                for (Map.Entry<String, String> map : answerMap.entrySet()) {
                    param = param + "&" + map.getKey() + "=" + map.getValue();
                }
                String submitURL = "http://171.8.225.138/vls2s/vls3isapi.dll/smpaper";
                String submitHtml = HttpClient.sendPost(submitURL, param);
                if(submitHtml.contains("你的登录信息已经失效")){
                    ptopId = login(nowUser);
                    param = "submitpaper=submit&ptopid="+ptopId+"&paperid="+user.getUid()+model.getZhangId();
                    submitHtml = HttpClient.sendPost(submitURL, param);
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
                    Matcher matcher6 = compile("<font [^>]*>([^<]*)</font>").matcher(submitHtml);
                    List<String> resultList = new ArrayList<>();
                    while (matcher6.find()) {
                        String r = matcher6.group(1);
                        if(r.contains("[对]")){
                            resultList.add("正确");
                        }else if(r.contains("[错]")){
                            resultList.add("错误");
                        }
                    }
                    System.out.println("===========================更新答案库========================================");
                    for (Answer answerOld : answerOldList) {
                        for(int i=0;i<resultList.size();i++) {
                            if(resultList.get(i).equals("正确")){
                                if(answerOld.getIsCorrect() != 1){
                                    answerOld.setIsCorrect(1);
                                    answerMapper.update(answerOld);
                                }
                            }else if(resultList.get(i).equals("错误")){
                                // 单选
                                if(answerOld.getQuestId().contains(model.getZhangId()+1)){
                                    answerOld.setAnswers(DanXuan.getNote(DanXuan.getCode(answerOld.getAnswers())+1));
                                }
                                // 多选
                                else if(answerOld.getQuestId().contains(model.getZhangId()+2)){
                                    answerOld.setAnswers(DuoXuan.getNote(DuoXuan.getCode(answerOld.getAnswers())+1));
                                }
                                // 判断
                                else if(answerOld.getQuestId().contains(model.getZhangId()+3)){
                                    answerOld.setAnswers(PanDuan.getNote(PanDuan.getCode(answerOld.getAnswers())+1));
                                }
                                answerOld.setIsCorrect(0);
                                answerMapper.update(answerOld);
                            }
                            resultList.remove(i);
                            break;
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
