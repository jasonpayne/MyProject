package com.xinchao.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinchao.dao.entity.Answer;
import com.xinchao.dao.entity.ClazzUser;
import com.xinchao.dao.entity.TestUser;
import com.xinchao.dao.entity.User;
import com.xinchao.dao.mapper.AnswerMapper;
import com.xinchao.dao.mapper.ClazzUserMapper;
import com.xinchao.dao.mapper.TestUserMapper;
import com.xinchao.dao.mapper.UserMapper;
import com.xinchao.model.QuestAnswer;
import com.xinchao.service.ClazzService;
import com.xinchao.service.QuestionService;
import com.xinchao.utils.HttpClient;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    HttpClient HttpClient = new HttpClient();

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
                    if(loginInfo.getIsTest() == 0 || loginInfo.getIsClazz()==0){
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
                    user.setIsClazz(0);
                    user.setIsTest(0);
                    userMapper.insert(user);
                    return "刚刚注册成功，正在操作，请稍等登陆查看。"+register0(user);
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/mytest", method = RequestMethod.GET)
    public void mytest() {
        List<User> select = userMapper.selectForList(new User());
        for (User user : select) {
            String ptopId = questionService.login(user);
        }
    }

    @RequestMapping(value = "/getExamineAll", method = RequestMethod.GET)
    public void getExamineAll() {
        List<User> users = userMapper.selectForList(new User());
        String ptopId = questionService.login(users.get(20));
        String cookieUrl = "http://222.22.63.178/student/wsdlLogin?ptopid="+ptopId;
        // 设置cookie
        HttpClient.sendGetNoRedirects(cookieUrl, null);
        /*String examinesUrl = "http://222.22.63.178/student/courseList";
        String examinesHtml = HttpClient.sendGet(examinesUrl, null);*/
        /*if(examinesHtml.contains("你的登录信息已经失效")){

        }*/

        /*Elements courseNameElements = new Elements();
        Elements courseIdElements = new Elements();
        Document examineDocument = Jsoup.parse(examinesHtml);
        courseNameElements = examineDocument.select("p[class=text_center class-name float-l]");
        courseIdElements = examineDocument.select("a[class=btn btn-sm btn-border]").select("a[onclick]");
        List<String> courseNameList = new ArrayList<>();
        for(Element element : courseNameElements){
            String str = element.text();
            System.out.println(str);
            courseNameList.add(str);
        }
        List<String> courseList = new ArrayList<>();
        for(Element element : courseIdElements){
            String str = element.toString().substring(element.toString().indexOf("('")+"('".length(), element.toString().indexOf("')"));
            String courseId = str.substring(str.length()-4);
            System.out.println(courseId);
            courseList.add(str);
            String examineDetailUrl = "http://222.22.63.178/student/courseSelect?studentCourseId="+courseId;
        }*/

        String examineDetailUrl = "http://222.22.63.178/student/exercise";
        String examineDetailHtml = HttpClient.sendGet(examineDetailUrl, null);

        Document document = Jsoup.parse(examineDetailHtml);
        Elements examineElements = document.select("script[type=text/javascript]");
        JSONArray jsonArray = new JSONArray();
        for(Element element : examineElements){
            if(element.html().contains("var questionsJson =")) {
                String str = element.html().replace("\n", ""); //这里是为了解决 无法多行匹配的问题
                Matcher matcher = Pattern.compile("var questionsJson = \\[(.*?)\\]").matcher(str);
                if(matcher.find()){
                    String questionsVar = matcher.group().replace("var questionsJson =", "");
                    jsonArray = JSONObject.parseArray(questionsVar);
                }
            }
        }
        for(int i = 0 ; i < jsonArray.size() ; i++) {
            jsonArray.getJSONObject(i).get("id");
            jsonArray.getJSONObject(i).get("type");
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
            /*if(allclass.contains("你应已修习")){
                allclass = allclass.substring(0,allclass.indexOf("你应已修习"));
            }*/
            Matcher matcher = pattern.matcher(allclass);
            while (matcher.find()) {
                String r = matcher.group(1).replace("\"", "");
                if (r.contains("lookonecourse") ) {
                    String keId = r.substring(r.indexOf("keid=") + "keid=".length());
                    if (!keId.equals("0027") && !keId.equals("9011")){
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
                        if(!text.contains("共10分，你已取得10分") && !text.contains("因库中无有效课件，你直接取得10分")
                                && !text.contains("因库中无有效课件，你直接取得10分") && !text.contains("点播课件不再计分")){
                            ClazzUser clazzUser = new ClazzUser();
                            clazzUser.setClzssId(keId);
                            clazzUser.setUid(user.getUid());
                            String score = text.substring(text.indexOf("共10分，你已取得")+"共10分，你已取得".length(), text.indexOf("共10分，你已取得")+"共10分，你已取得".length()+1);
                            System.out.println(user.getUid() + "==="+  user.getPw() + "==="+ keId+"========"+score);
                            clazzUser.setScore(Integer.valueOf(score));
                            clazzUser.setIsComplete(0);
                            clazzUserMapper.insert(clazzUser);
                        }
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

    @RequestMapping(value = "/getAnswers", method = RequestMethod.GET)
    public Object getAnswer(Answer answer) {
        try {
            List<Answer> answerList = answerMapper.selectForList(answer);
            List<QuestAnswer> questAnswerList = new ArrayList<>();
            for(Answer model : answerList){
                QuestAnswer questAnswer = new QuestAnswer();
                questAnswer.set题目(model.getQuestName());
                questAnswer.set选项(model.getAnswersName());
                questAnswer.set答案(model.getAnswers());
                questAnswerList.add(questAnswer);
            }
            return questAnswerList;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }
}
