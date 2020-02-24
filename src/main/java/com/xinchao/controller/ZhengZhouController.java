package com.xinchao.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinchao.dao.entity.*;
import com.xinchao.dao.mapper.*;
import com.xinchao.enums.QuestionType;
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

    @Autowired
    ExamineMapper examineMapper;

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

        try {
            List<User> users = userMapper.selectForList(new User());
            String ptopId = questionService.login(users.get(0));
            String cookieUrl = "http://222.22.63.178/student/wsdlLogin?ptopid="+ptopId;
            // 设置cookie
            HttpClient.sendGetNoRedirects(cookieUrl, null);

            System.out.println("------------------------------以下为需要测试功能代码---------------------------------");

            Examine examineQuery = new Examine();
//            examineQuery.setIsReply(0);
//            examineQuery.setQuestType("danxuan-1");
            List<Examine> examines = examineMapper.selectForList(examineQuery);

            Elements questElements = new Elements();
            Elements answersElements = new Elements();
            Elements answerElements = new Elements();
            int succeed = 0;
            int fail = 0;
            for(Examine model :examines){
                String quest = "";
                String answers = "";
                String answer = "";
                String examineDetailUrl = "http://222.22.63.178/student/getQuestion?isSimulate=1&qId=" + model.getQuestId();
                System.out.println(examineDetailUrl);
                String examineDetailHtml = HttpClient.sendGet(examineDetailUrl, null);
                if(StringUtils.isBlank(examineDetailHtml)){
                    fail++;
                    continue;
                }
                Document document = Jsoup.parse(examineDetailHtml);
                if(model.getQuestType().equals(QuestionType.JST.getCode()) // 计算题
                    || model.getQuestType().equals(QuestionType.LST.getCode()) // 论述题
                    || model.getQuestType().equals(QuestionType.XZT.getCode()) // 写作题
                    || model.getQuestType().equals(QuestionType.SJT.getCode())// 设计题
                    || model.getQuestType().equals(QuestionType.YWT.getCode()) // 业务题
                    || model.getQuestType().equals(QuestionType.ZCFY.getCode()) // 字词翻译
                    || model.getQuestType().equals(QuestionType.HTT.getCode()) // 绘图题
                    || model.getQuestType().equals(QuestionType.BCT.getCode()) // 编程题
                    || model.getQuestType().equals(QuestionType.JCT.getCode()) // 纠错题
                    || model.getQuestType().equals(QuestionType.JDT.getCode()) // 简答题
                    || model.getQuestType().equals(QuestionType.MCJS.getCode()) // 名词解释
                    || model.getQuestType().equals(QuestionType.DYFY.getCode()) // 短语翻译
                    || model.getQuestType().equals(QuestionType.JZFY.getCode()) // 句子翻译
                    || model.getQuestType().equals(QuestionType.DLFX.getCode()) // 段落翻译
                    || model.getQuestType().equals(QuestionType.WDT.getCode()) // 问答题
                    || model.getQuestType().equals(QuestionType.TKT.getCode()) // 填空题
                    || model.getQuestType().equals(QuestionType.TLTKT.getCode()) // 听力填空题
                    || model.getQuestType().equals(QuestionType.ZHT.getCode()) // 组合题
                    || model.getQuestType().equals(QuestionType.ALFX.getCode()) // 案例分析
                ){
                    // 问题
                    questElements = document.select("div[class=shiti-item cl q-item subjective]")
                            .select("div[data-q-id="+model.getQuestId()+"]").select("div > h4,div > h3,div > h2,div > h1");
                    if(null != questElements && questElements.size() > 0){
                        for(int i = 0 ; i < questElements.size() ; i++){
                            // 有图片
                            if(questElements.get(i).html().contains("img")){
                                Elements questImgElements = questElements.select("img");
                                for(Element  element : questImgElements){
                                    String src = "http://222.22.63.178" + element.attr("src").replace("\"","");
                                    if(StringUtils.isNotBlank(src)){
                                        quest = quest + src + ";";
                                    }
                                }
                            }
                            if(StringUtils.isNotBlank(questElements.get(i).text())){
                                quest = quest + questElements.get(i).text();
                            }
                        }
                    }
                    // 继续寻找题目
                    if(StringUtils.isBlank(quest)){
                        questElements = document.select("div[class=shiti-item cl q-item subjective]")
                                .select("div[data-q-id="+model.getQuestId()+"]");
                        if(null != questElements && questElements.size() > 0){
                            for(int i = 0 ; i < questElements.size() ; i++){
                                // 有图片
                                if(questElements.get(i).html().contains("img")){
                                    Elements questImgElements = questElements.select("img");
                                    for(Element  element : questImgElements){
                                        String src = "http://222.22.63.178" + element.attr("src").replace("\"","");
                                        if(StringUtils.isNotBlank(src)){
                                            quest = quest + src + ";";
                                        }
                                    }
                                }
                                if(StringUtils.isNotBlank(questElements.get(i).text())){
                                    quest = quest + questElements.get(i).text();
                                }
                            }
                        }
                        quest = quest.replace("http://222.22.63.178;","").replace(" 收藏 扫一扫上传图 预览","");
                    }
                    // 答案
                    String answerJsoup ="id=see-answer_"+model.getQuestId();
                    answerElements = document.select("div["+answerJsoup+"]");
                    if(null != answerElements && answerElements.size() > 0){
                        if(answerElements.get(0).html().contains("img")){
                            // 有图片
                            answerElements = answerElements.select("img");
                            for(Element  element : answerElements){
                                String src = "http://222.22.63.178"+ element.attr("src").replace("\"","");
                                answer = answer + src + ";";
                            }
                        }
                        answer = answer + answerElements.get(0).text();
                    }
                }else if(model.getQuestType().equals(QuestionType.DANXT.getCode()) // 单选题
                    || model.getQuestType().equals(QuestionType.TL1.getCode()) // 听力一
                    || model.getQuestType().equals(QuestionType.YYDH.getCode()) // 英语对话
                    || model.getQuestType().equals(QuestionType.DUOXT.getCode()) // 多选题
                    || model.getQuestType().equals(QuestionType.PDT.getCode()) // 判断题
                ){
                    // 题目
                    questElements = document.select("div[class=shiti-item cl q-item]")
                            .select("div[data-q-id="+model.getQuestId()+"]").select("div > h4,div > h3,div > h2,div > h1");
                    if(null != questElements && questElements.size() > 0){
                        for(int i = 0 ; i < questElements.size() ; i++){
                            // 有图片
                            if(questElements.get(i).html().contains("src")){
                                Elements questSrcElements = questElements.get(i).select("src");
                                for(Element questSrcElement : questSrcElements){
                                    if(null != questSrcElement.getElementsByTag("embed")){
                                        String embedSuffix = questSrcElement.getElementsByTag("embed").attr("src");
                                        if(StringUtils.isNotBlank(embedSuffix)){
                                            String embedSrc = "http://222.22.63.178" + embedSuffix.replace("\"","");
                                            if(StringUtils.isNotBlank(embedSrc)){
                                                quest = quest + embedSrc + ";";
                                            }
                                        }
                                    }
                                    if(null != questSrcElement.getElementsByTag("img")){
                                        String imgSuffix = questSrcElement.getElementsByTag("img").attr("src");
                                        if(StringUtils.isNotBlank(imgSuffix)){
                                            String imgSrc = "http://222.22.63.178" + imgSuffix.replace("\"","");
                                            if(StringUtils.isNotBlank(imgSrc)){
                                                quest = quest + imgSrc + ";";
                                            }
                                        }
                                    }
                                }
                            }
                            if(StringUtils.isNotBlank(questElements.get(i).text())){
                                quest = quest + questElements.get(i).text();
                            }
                        }
                    }
                    // 继续寻找题目
                    if(StringUtils.isBlank(quest)){
                        Element questElement = document.select("div[class=shiti-item cl q-item]").select("p").first();
                        if(null != questElement){
                            // 有图片
                            if(questElement.html().contains("src")){
                                Elements questSrcElements = questElement.select("embed,img");
                                for(Element questSrcElement : questSrcElements){
                                    if(null != questSrcElement.getElementsByTag("embed")){
                                        String embedSuffix = questSrcElement.getElementsByTag("embed").attr("src");
                                        if(StringUtils.isNotBlank(embedSuffix)){
                                            String embedSrc = "http://222.22.63.178" + embedSuffix.replace("\"","");
                                            if(StringUtils.isNotBlank(embedSrc)){
                                                quest = quest + embedSrc + ";";
                                            }
                                        }
                                    }
                                    if(null != questSrcElement.getElementsByTag("img")){
                                        String imgSuffix = questSrcElement.getElementsByTag("img").attr("src");
                                        if(StringUtils.isNotBlank(imgSuffix)){
                                            String imgSrc = "http://222.22.63.178" + imgSuffix.replace("\"","");
                                            if(StringUtils.isNotBlank(imgSrc)){
                                                quest = quest + imgSrc + ";";
                                            }
                                        }
                                    }
                                }
                            }
                            if(StringUtils.isNotBlank(questElement.text())){
                                quest = quest + questElements.text();
                            }
                        }
                        if(StringUtils.isNotBlank(quest)){
                            quest = quest.replace("http://222.22.63.178;","").replace(" 收藏 扫一扫上传图 预览","");
                        }
                    }

                    // 选项
                    answersElements = document.select("div[class=shiti-item-left]").select("p[class=answer],p+div");
                    if(answersElements.size()>0 && answersElements.size() % 2 == 0){
                        if(null != answersElements && answersElements.size() > 0){
                            for(int i = 0 ; i < answersElements.size() ; i = i+2) {
                                answers = answers + answersElements.get(i).text();
                                if(answersElements.get(i+1).html().contains("src")){
                                    // 有图片
                                    Elements answersImgElements = answersElements.get(i).select("src");
                                    for(Element element : answersImgElements){
                                        String src = "http://222.22.63.178"+element.attr("src").replace("\"","");
                                        answers = answers + src + ";";
                                    }
                                }
                                answers = answers + answersElements.get(i+1).text()+ ";";
                                if(answersElements.get(i).toString().contains("data-o-right-flag=\"1\"")){
                                    if(model.getQuestType().equals(QuestionType.PDT.getCode())){
                                        answer = answer + answersElements.get(i+1).text();
                                    }else {
                                        answer = answer + answersElements.get(i).text();
                                    }
                                }
                            }
                        }
                    }
                    // 答案
                    if(StringUtils.isBlank(answer)){
                        answerElements = document.select("p[class=dacuo]");
                        if(null != answerElements && answerElements.size() > 0){
                            answer = answer + answerElements.get(0).text();
                        }
                    }
                }else if(model.getQuestType().equals(QuestionType.YDLJ.getCode()) // 阅读理解
                    || model.getQuestType().equals(QuestionType.SXXWXTK.getCode()) // 十选项完形填空
                    || model.getQuestType().equals(QuestionType.TL2.getCode()) // 听力二
                    || model.getQuestType().equals(QuestionType.WXXWXTK.getCode()) // 五选项完型填空
                ){
                    // <p class="answer" data-o-id="0054010630011" data-o-right-flag="1"



                }else if(model.getQuestType().equals(QuestionType.SCT.getCode()) // 上传题
                    || model.getQuestType().equals(QuestionType.SCTFZG.getCode())){ // 上传题(非主观)
                }
                if(StringUtils.isNotBlank(quest)){
                    model.setQuestName(quest);
                }
                if(StringUtils.isNotBlank(answers) && !model.getQuestType().equals(QuestionType.PDT.getCode())){
                    model.setAnswersName(answers);
                }
                if(StringUtils.isNotBlank(answer) && !answer.contains("没有详解")){
                    model.setSubjAnswer(answer);
                    model.setIsReply(1);
                }else{
                    model.setIsReply(2);
                }
                succeed = succeed + examineMapper.update(model);
            }
            System.out.println("succeed:"+succeed);
            System.out.println("fail:"+fail);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @RequestMapping(value = "/getExamineAll", method = RequestMethod.GET)
    public int getExamineAll() {
        int succeed = 0;
        try {
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
            List<Examine> list = new ArrayList<>();
            for(int i = 0 ; i < jsonArray.size() ; i++) {
                Examine examine = new Examine();
                examine.setQuestId(jsonArray.getJSONObject(i).getString("id"));
                examine.setQuestType(jsonArray.getJSONObject(i).getString("type"));
                examine.setIsReply(0);
                list.add(examine);
                if (list.size() > 10000) {
                    succeed = succeed + examineMapper.insertBatch(list);
                    list.clear();
                }
            }
            if (list.size() > 0) {
                succeed = succeed + examineMapper.insertBatch(list);
            }
            return succeed;
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return succeed;
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
                            clazzUserMapper.insertNotExist(clazzUser);
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
                        testUserMapper.insertNotExist(testUser);
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
            String aa = questionService.openTest();
            String bb = questionService.submitAnswer();
            return aa+bb;
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
