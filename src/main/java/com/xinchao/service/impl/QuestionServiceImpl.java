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
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.regex.Matcher;

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

    HttpClient HttpClient = new HttpClient();

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
            int count = 0;
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
                if(testDetailHtml.contains("测试题数量不够")){
                    model.setIsComplete(-1);
                    model.setQuests("测试题数量不够，不能进行在线测试");
                    continue;
                }
                // 题目
                Document document = Jsoup.parse(testDetailHtml);
                Elements questElements = document.select("td[width=100%]").select("td[bgcolor=#E6E6DF]").select("td[height=20]").
                        select("td:not(td[width=25%])").select("td:not(td[width=40%])").select("td:not(td[width=20%])");
                List<String> questNameList = new ArrayList<>();
                for(Element element : questElements){
                    String str = element.text();
                    if(!str.contains("本题空白")) {
                        questNameList.add(str);
                    }
                }
                // 答案
                List<String> answersNameList = new ArrayList<>();
                Elements answerElements = document.select("table[width=80%]").select("table:not(table[width=100%])").select("table:not(table[width=750])");
                for(Element element : answerElements){
                    answersNameList.add(element.text());
                }

                Matcher testDetailMatcher = compile("<input" + "[^<>]*?\\s" + "name=['\"]?(.*?)['\"]?(\\s.*?)?>").matcher(testDetailHtml);
                // 本章当前需要完成的题目
                TreeSet<String> questionSet = new TreeSet();
                Map<String,String> map = new HashMap<>();
                while (testDetailMatcher.find()) {
                    String r = testDetailMatcher.group(1);
                    if(r.contains(model.getZhangId())){
                        if(r.contains("A") || r.contains("B") || r.contains("C") || r.contains("D") || r.contains("E")
                                || r.contains("F")|| r.contains("G")|| r.contains("H")|| r.contains("i")|| r.contains("j")){
                            questionSet.add(r.substring(0,r.length()-1));
                            map.put(r.substring(0,r.length()-1),r.substring(r.length()-1));
                        }else {
                            questionSet.add(r);
                        }
                    }
                }
                // 初始化答案
                int index = 0;
                for (String str : questionSet) {
                    Answer quest = answerMapper.selectOne(str);
                    if(null == quest){
                        Answer isNotAnswer = new Answer();
                        isNotAnswer.setQuestId(str);
                        isNotAnswer.setZhangId(model.getZhangId());
                        if(str.contains(model.getZhangId()+1)){
                            isNotAnswer.setAnswers("A");
                        }
                        else if(str.contains(model.getZhangId()+2)){
                            isNotAnswer.setAnswers("A,B,C,D");
                            isNotAnswer.setAnswerSize(map.get(str));
                        }
                        else if(str.contains(model.getZhangId()+3)){
                            isNotAnswer.setAnswers("Y");
                        }
                        isNotAnswer.setQuestName(questNameList.get(index));
                        isNotAnswer.setAnswersName(answersNameList.get(index));
                        isNotAnswer.setIsCorrect(-1);
                        // 如果不存在初始答案则要初始化
                        answerMapper.insert(isNotAnswer);
                    }
                    index ++;
                }
                StringBuilder sb = new StringBuilder ();
                for (String str : questionSet){
                    sb.append(str+",");
                }
                model.setQuests(sb.toString());
                model.setIsSubmit(1);
                System.out.println("打开第"+ ++count +"测试章节");
            }
            if(null != testList && testList.size() > 0){
                // 答题需要延迟2分钟提交
                int k = 0;
                while (k < 2) {
                    Thread.sleep(60100);
                    System.out.println("==========等待第("+ ++k +")个60秒==========");
                }
                for (TestUser model : testList) {
                    testUserMapper.update(model);
                }
            }
            return "已经打开"+count+"章节的题目";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
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
            int count = 0;
            for (TestUser model : testList) {
                List<String> questList = new ArrayList(Arrays.asList(model.getQuests().split(",")));
                Answer zhangOld = new Answer();
                zhangOld.setZhangId(model.getZhangId());
                // 当前数据库中答案（全部这个章节下的答案）
                List<Answer> answerList = answerMapper.selectForList(zhangOld);
                // 等待提交的答案的格式
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
                    ptopId = login(nowUser);
                    param = "submitpaper=submit&ptopid="+ptopId+"&paperid="+user.getUid()+model.getZhangId();
                    submitHtml = HttpClient.sendPost(submitURL, param + answerParam);
                }
                Matcher matcherPoint = compile("<span [^>]*>([^<]*)</span>").matcher(submitHtml);
                int score = 0;
                while (matcherPoint.find()) {
                    String m = matcherPoint.group(1);
                    if(m.contains("分")){
                        score = Integer.valueOf(m.substring(0,m.indexOf("分")));
                    }
                }
                if(submitHtml.contains("抱歉")) {
                    continue;
                } else if(submitHtml.contains("交卷操作成功完成")) {
                    Matcher submitMatcher = compile("<font [^>]*>([^<]*)</font>").matcher(submitHtml);
                    Map<String,String> answerResultMap = new TreeMap<>();
                    while (submitMatcher.find()) {
                        for (int i = 0 ;i < questList.size(); i++) {
                            String r = submitMatcher.group(1);
                            if(r.contains("[对]")){
                                answerResultMap.put(questList.get(i),"正确");
                                questList.remove(i);
                                break;
                            }else if(r.contains("[错]")){
                                answerResultMap.put(questList.get(i),"错误");
                                questList.remove(i);
                                break;
                            }
                        }
                    }
                    System.out.println("===========================更新答案库========================================");
                    int noAnswers = 0;
                    for (Map.Entry<String, String> map : answerResultMap.entrySet()) {
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
                                        if(answerNew.getAnswers().equals("D")){
                                            answerNew.setIsCorrect(2);
                                            noAnswers = noAnswers + 1;
                                        }else{
                                            answerNew.setAnswers(DanXuan.getNote(DanXuan.getCode(answerNew.getAnswers())+1));
                                            answerNew.setIsCorrect(0);
                                        }
                                    }
                                    // 多选
                                    else if(map.getKey().contains(model.getZhangId()+2)){
                                        if(StringUtils.isNotBlank(answerNew.getAnswerSize()) && "FGHIJ".contains(answerNew.getAnswerSize())){
                                            answerNew.setIsCorrect(2);
                                            noAnswers = noAnswers + 2;
                                        }else{
                                            if(answerNew.getAnswers().equals("E")){
                                                answerNew.setIsCorrect(2);
                                                noAnswers = noAnswers + 2;
                                            }else{
                                                answerNew.setAnswers(DuoXuan.getNote(DuoXuan.getCode(answerNew.getAnswers())+1));
                                                answerNew.setIsCorrect(0);
                                            }
                                        }
                                    }
                                    // 判断
                                    else if(map.getKey().contains(model.getZhangId()+3)){
                                        if(answerNew.getAnswers().equals("N")){
                                            answerNew.setIsCorrect(2);
                                            noAnswers = noAnswers + 1;
                                        }else{
                                            answerNew.setAnswers(PanDuan.getNote(PanDuan.getCode(answerNew.getAnswers())+1));
                                            answerNew.setIsCorrect(0);
                                        }
                                    }
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
                        System.out.println("提交了第"+ ++count +"章节测试，已经完成答题");
                    }else {
                        if(score + noAnswers == 20){
                            model.setIsComplete(2);
                            System.out.println("提交了第"+ ++count +"章节测试，当前:"+ score +"分，存在没有正确答案和多选题F以上的题目，暂停答题");
                        }else {
                            System.out.println("提交了第"+ ++count +"章节测试，当前:"+ score +"分，需要继续答题");
                        }
                    }
                    testUserMapper.update(model);
                }
            }
            return "总计提交了"+ count +"章节的测试";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }
}
