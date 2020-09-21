package com.payne.school.controller;

import cn.wanghaomiao.xpath.model.JXDocument;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.payne.school.dao.entity.*;
import com.payne.school.dao.mapper.*;
import com.payne.school.enums.QuestionType;
import com.payne.school.model.QuestAnswer;
import com.payne.school.service.ClazzService;
import com.payne.school.utils.HttpClient;
import com.payne.school.dao.entity.*;
import com.payne.school.dao.mapper.*;
import com.payne.school.service.QuestionService;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * QuestionController 测试
 *
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

    @Autowired
    CourseMapper courseMapper;

    com.payne.school.utils.HttpClient HttpClient = new HttpClient();

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(@RequestBody User user) {
        try {
            //发送 POST 请求登陆,注册已知课程
            String ptopId = questionService.login(user);
            if (ptopId == null) {
                return "对不起，你输入的账号和密码未通过系统的验证";
            } else {
                User loginInfo = userMapper.login(user);
                if (null != loginInfo) {
                    user.setPtopId(ptopId);
                    userMapper.update(user);
                    return "已经注册成功，正在操作。" + register0(loginInfo);
                } else {
                    // 插入系统，并且初始化数据
                    user.setPtopId(ptopId);
                    user.setIsClazz(0);
                    user.setIsTest(0);
                    userMapper.insert(user);
                    return "刚刚注册成功，正在操作，请稍等登陆查看。" + register0(user);
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/register/batch", method = RequestMethod.GET)
    public List<String> registerBatch(@RequestBody List<String> users) {
        List<String> result = new ArrayList<>();
        for (String info : users) {
            User user = new User();
            String[] arr = info.split(":");
            user.setName(arr[0]);
            user.setUid(arr[1]);
            user.setPw(arr[2]);
            try {
                //发送 POST 请求登陆,注册已知课程
                String ptopId = questionService.login(user);
                if (ptopId == null) {
                    result.add(info + "==>对不起，你输入的账号和密码未通过系统的验证。");
                } else {
                    User loginInfo = userMapper.login(user);
                    if (null != loginInfo) {
                        user.setPtopId(ptopId);
                        userMapper.update(user);
                        result.add(info + "==>已经注册成功，正在操作。" + register0(loginInfo));
                    } else {
                        // 插入系统，并且初始化数据
                        user.setPtopId(ptopId);
                        user.setIsClazz(0);
                        user.setIsTest(0);
                        userMapper.insert(user);
                        result.add(info + "==>刚刚注册成功，正在操作，请稍等登陆查看。" + register0(user));
                    }
                }
            } catch (Exception e) {
                result.add(info + "==>注册失败，异常原因。" + e.getMessage());
            }
        }
        return result;
    }

    /**
     * 一次性获取所有网考题（慎用）
     */
    @RequestMapping(value = "/getExamineAll", method = RequestMethod.GET)
    public int getExamineAll() {
        int succeed = 0;
        try {
            List<User> users = userMapper.selectForList(new User());
            String ptopId = questionService.login(users.get(20));
            String cookieUrl = "http://222.22.63.178/student/wsdlLogin?ptopid=" + ptopId;
            // 设置cookie
            HttpClient.sendGetNoRedirects(cookieUrl, null);

            String examineDetailUrl = "http://222.22.63.178/student/exercise";
            String examineDetailHtml = HttpClient.sendGet(examineDetailUrl, null);
            Document document = Jsoup.parse(examineDetailHtml);
            Elements examineElements = document.select("script[type=text/javascript]");
            JSONArray jsonArray = new JSONArray();
            for (Element element : examineElements) {
                if (element.html().contains("var questionsJson =")) {
                    String str = element.html().replace("\n", ""); //这里是为了解决 无法多行匹配的问题
                    Matcher matcher = Pattern.compile("var questionsJson = \\[(.*?)\\]").matcher(str);
                    if (matcher.find()) {
                        String questionsVar = matcher.group().replace("var questionsJson =", "");
                        jsonArray = JSONObject.parseArray(questionsVar);
                    }
                }
            }
            List<Examine> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                String questId = jsonArray.getJSONObject(i).getString("id");
                Examine examineQuery = examineMapper.selectOne(questId);
                if (null != examineQuery) {
                    continue;
                } else {
                    Examine examine = new Examine();
                    examine.setQuestId(questId);
                    examine.setQuestType(jsonArray.getJSONObject(i).getString("type"));
                    examine.setIsReply(0);
                    list.add(examine);
                    if (list.size() > 10000) {
                        succeed = succeed + examineMapper.insertBatch(list);
                        list.clear();
                    }
                }
            }
            if (list.size() > 0) {
                succeed = succeed + examineMapper.insertBatch(list);
            }
            return succeed;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return succeed;
    }

    /**
     * 一次性获取所有网考题答案，按照题目类型分类获取答案（慎用）
     */
    @RequestMapping(value = "/getExamineAnswer", method = RequestMethod.GET)
    public void getExamineAnswer() {
        try {
            List<User> users = userMapper.selectForList(new User());
            String ptopId = questionService.login(users.get(0));
            String cookieUrl = "http://222.22.63.178/student/wsdlLogin?ptopid=" + ptopId;
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
            for (Examine model : examines) {
                String quest = "";
                String answers = "";
                String answer = "";
                String examineDetailUrl = "http://222.22.63.178/student/getQuestion?isSimulate=1&qId=" + model.getQuestId();
                System.out.println(examineDetailUrl);
                String examineDetailHtml = HttpClient.sendGet(examineDetailUrl, null);
                if (StringUtils.isBlank(examineDetailHtml)) {
                    fail++;
                    continue;
                }
                Document document = Jsoup.parse(examineDetailHtml);
                if (model.getQuestType().equals(QuestionType.JST.getCode()) // 计算题
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
                ) {
                    // 问题
                    questElements = document.select("div[class=shiti-item cl q-item subjective]")
                            .select("div[data-q-id=" + model.getQuestId() + "]").select("div > h4,div > h3,div > h2,div > h1");
                    if (null != questElements && questElements.size() > 0) {
                        for (int i = 0; i < questElements.size(); i++) {
                            // 有图片
                            if (questElements.get(i).html().contains("img")) {
                                Elements questImgElements = questElements.select("img");
                                for (Element element : questImgElements) {
                                    String src = "http://222.22.63.178" + element.attr("src").replace("\"", "");
                                    if (StringUtils.isNotBlank(src)) {
                                        quest = quest + src + ";";
                                    }
                                }
                            }
                            if (StringUtils.isNotBlank(questElements.get(i).text())) {
                                quest = quest + questElements.get(i).text();
                            }
                        }
                    }
                    // 继续寻找题目
                    if (StringUtils.isBlank(quest)) {
                        questElements = document.select("div[class=shiti-item cl q-item subjective]")
                                .select("div[data-q-id=" + model.getQuestId() + "]");
                        if (null != questElements && questElements.size() > 0) {
                            for (int i = 0; i < questElements.size(); i++) {
                                // 有图片
                                if (questElements.get(i).html().contains("img")) {
                                    Elements questImgElements = questElements.select("img");
                                    for (Element element : questImgElements) {
                                        String src = "http://222.22.63.178" + element.attr("src").replace("\"", "");
                                        if (StringUtils.isNotBlank(src)) {
                                            quest = quest + src + ";";
                                        }
                                    }
                                }
                                if (StringUtils.isNotBlank(questElements.get(i).text())) {
                                    quest = quest + questElements.get(i).text();
                                }
                            }
                        }
                        quest = quest.replace("http://222.22.63.178;", "").replace(" 收藏 扫一扫上传图 预览", "");
                    }
                    // 答案
                    String answerJsoup = "id=see-answer_" + model.getQuestId();
                    answerElements = document.select("div[" + answerJsoup + "]");
                    if (null != answerElements && answerElements.size() > 0) {
                        if (answerElements.get(0).html().contains("img")) {
                            // 有图片
                            answerElements = answerElements.select("img");
                            for (Element element : answerElements) {
                                String src = "http://222.22.63.178" + element.attr("src").replace("\"", "");
                                answer = answer + src + ";";
                            }
                        }
                        answer = answer + answerElements.get(0).text();
                    }
                } else if (model.getQuestType().equals(QuestionType.DANXT.getCode()) // 单选题
                        || model.getQuestType().equals(QuestionType.TL1.getCode()) // 听力一
                        || model.getQuestType().equals(QuestionType.YYDH.getCode()) // 英语对话
                        || model.getQuestType().equals(QuestionType.DUOXT.getCode()) // 多选题
                        || model.getQuestType().equals(QuestionType.PDT.getCode()) // 判断题
                ) {
                    // 题目
                    questElements = document.select("div[class=shiti-item cl q-item]")
                            .select("div[data-q-id=" + model.getQuestId() + "]").select("div > h4,div > h3,div > h2,div > h1");
                    if (null != questElements && questElements.size() > 0) {
                        for (int i = 0; i < questElements.size(); i++) {
                            // 有图片
                            if (questElements.get(i).html().contains("src")) {
                                Elements questSrcElements = questElements.get(i).select("img[src],embed[src]");
                                for (Element questSrcElement : questSrcElements) {
                                    if (null != questSrcElement.getElementsByTag("embed") && questSrcElement.getElementsByTag("embed").size() > 0) {
                                        String embedSuffix = questSrcElement.getElementsByTag("embed").attr("src");
                                        if (StringUtils.isNotBlank(embedSuffix)) {
                                            String embedSrc = "http://222.22.63.178" + embedSuffix.replace("\"", "");
                                            if (StringUtils.isNotBlank(embedSrc)) {
                                                quest = quest + embedSrc + ";";
                                            }
                                        }
                                    } else if (null != questSrcElement.getElementsByTag("img") && questSrcElement.getElementsByTag("img").size() > 0) {
                                        String imgSuffix = questSrcElement.getElementsByTag("img").attr("src");
                                        if (StringUtils.isNotBlank(imgSuffix)) {
                                            String imgSrc = "http://222.22.63.178" + imgSuffix.replace("\"", "");
                                            if (StringUtils.isNotBlank(imgSrc)) {
                                                quest = quest + imgSrc + ";";
                                            }
                                        }
                                    }
                                }
                            }
                            if (StringUtils.isNotBlank(questElements.get(i).text())) {
                                quest = quest + questElements.get(i).text();
                            }
                        }
                    }
                    // 继续寻找题目
                    if (StringUtils.isBlank(quest)) {
                        Element questElement = document.select("div[class=shiti-item cl q-item]").select("p").first();
                        if (null != questElement) {
                            // 有图片
                            if (questElement.html().contains("src")) {
                                Elements questSrcElements = questElement.select("img[src],embed[src]");
                                for (Element questSrcElement : questSrcElements) {
                                    if (null != questSrcElement.getElementsByTag("embed") && questSrcElement.getElementsByTag("embed").size() > 0) {
                                        String embedSuffix = questSrcElement.getElementsByTag("embed").attr("src");
                                        if (StringUtils.isNotBlank(embedSuffix)) {
                                            String embedSrc = "http://222.22.63.178" + embedSuffix.replace("\"", "");
                                            if (StringUtils.isNotBlank(embedSrc)) {
                                                quest = quest + embedSrc + ";";
                                            }
                                        }
                                    } else if (null != questSrcElement.getElementsByTag("img") && questSrcElement.getElementsByTag("img").size() > 0) {
                                        String imgSuffix = questSrcElement.getElementsByTag("img").attr("src");
                                        if (StringUtils.isNotBlank(imgSuffix)) {
                                            String imgSrc = "http://222.22.63.178" + imgSuffix.replace("\"", "");
                                            if (StringUtils.isNotBlank(imgSrc)) {
                                                quest = quest + imgSrc + ";";
                                            }
                                        }
                                    }
                                }
                            }
                            if (StringUtils.isNotBlank(questElement.text())) {
                                quest = quest + questElements.text();
                            }
                        }
                        if (StringUtils.isNotBlank(quest)) {
                            quest = quest.replace("http://222.22.63.178;", "").replace(" 收藏 扫一扫上传图 预览", "");
                        }
                    }

                    // 选项
                    answersElements = document.select("div[class=shiti-item-left]").select("p[class=answer],p+div");
                    if (answersElements.size() > 0 && answersElements.size() % 2 == 0) {
                        if (null != answersElements && answersElements.size() > 0) {
                            for (int i = 0; i < answersElements.size(); i = i + 2) {
                                answers = answers + answersElements.get(i).text();
                                if (answersElements.get(i + 1).html().contains("src")) {
                                    // 有图片
                                    Elements answersImgElements = answersElements.get(i + 1).select("img[src],embed[src]");
                                    for (Element element : answersImgElements) {
                                        String src = "http://222.22.63.178" + element.attr("src").replace("\"", "");
                                        answers = answers + src + ";";
                                    }
                                }
                                answers = answers + answersElements.get(i + 1).text() + ";";
                                if (answersElements.get(i).toString().contains("data-o-right-flag=\"1\"")) {
                                    if (model.getQuestType().equals(QuestionType.PDT.getCode())) {
                                        answer = answer + answersElements.get(i + 1).text();
                                    } else {
                                        answer = answer + answersElements.get(i).text();
                                    }
                                }
                            }
                        }
                    }
                    // 答案
                    if (StringUtils.isBlank(answer)) {
                        answerElements = document.select("p[class=dacuo]");
                        if (null != answerElements && answerElements.size() > 0) {
                            answer = answer + answerElements.get(0).text();
                        }
                    }
                } else if (model.getQuestType().equals(QuestionType.YDLJ.getCode()) // 阅读理解
                        || model.getQuestType().equals(QuestionType.SXXWXTK.getCode()) // 十选项完形填空
                        || model.getQuestType().equals(QuestionType.TL2.getCode()) // 听力二
                        || model.getQuestType().equals(QuestionType.WXXWXTK.getCode()) // 五选项完型填空
                ) {
                    // 题目
                    questElements = document.select("div[data-q-id=" + model.getQuestId() + "]").select("div > h4,div > h3,div > h2,div > h1");
                    int k = 0;
                    if (null != questElements && questElements.size() > 0) {
                        for (Element element : questElements) {
                            if (StringUtils.isNotBlank(element.text())) {
                                k++;
                            }
                            if (k >= 2) {
                                break;
                            }
                        }
                        for (int i = 0; i < questElements.size(); i++) {
                            if (k >= 2) {
                                quest = quest + i + ":";
                            }
                            // 有图片
                            if (questElements.get(i).html().contains("src")) {
                                Elements questSrcElements = questElements.get(i).select("img[src],embed[src]");
                                for (Element questSrcElement : questSrcElements) {
                                    if (null != questSrcElement.getElementsByTag("embed") && questSrcElement.getElementsByTag("embed").size() > 0) {
                                        String embedSuffix = questSrcElement.getElementsByTag("embed").attr("src");
                                        if (StringUtils.isNotBlank(embedSuffix)) {
                                            String embedSrc = "http://222.22.63.178" + embedSuffix.replace("\"", "");
                                            if (StringUtils.isNotBlank(embedSrc)) {
                                                quest = quest + embedSrc + ";";
                                            }
                                        }
                                    } else if (null != questSrcElement.getElementsByTag("img") && questSrcElement.getElementsByTag("img").size() > 0) {
                                        String imgSuffix = questSrcElement.getElementsByTag("img").attr("src");
                                        if (StringUtils.isNotBlank(imgSuffix)) {
                                            String imgSrc = "http://222.22.63.178" + imgSuffix.replace("\"", "");
                                            if (StringUtils.isNotBlank(imgSrc)) {
                                                quest = quest + imgSrc + ";";
                                            }
                                        }
                                    }
                                }
                            }
                            if (StringUtils.isNotBlank(questElements.get(i).text())) {
                                quest = quest + questElements.get(i).text();
                            }
                        }
                    }
                    // 继续寻找题目
                    if (StringUtils.isBlank(quest)) {
                        Element questElement = document.select("div[class=shiti-item cl q-item]").select("p").first();
                        if (null != questElement) {
                            // 有图片
                            if (questElement.html().contains("src")) {
                                Elements questSrcElements = questElement.select("img[src],embed[src]");
                                for (Element questSrcElement : questSrcElements) {
                                    if (null != questSrcElement.getElementsByTag("embed") && questSrcElement.getElementsByTag("embed").size() > 0) {
                                        String embedSuffix = questSrcElement.getElementsByTag("embed").attr("src");
                                        if (StringUtils.isNotBlank(embedSuffix)) {
                                            String embedSrc = "http://222.22.63.178" + embedSuffix.replace("\"", "");
                                            if (StringUtils.isNotBlank(embedSrc)) {
                                                quest = quest + embedSrc + ";";
                                            }
                                        }
                                    } else if (null != questSrcElement.getElementsByTag("img") && questSrcElement.getElementsByTag("img").size() > 0) {
                                        String imgSuffix = questSrcElement.getElementsByTag("img").attr("src");
                                        if (StringUtils.isNotBlank(imgSuffix)) {
                                            String imgSrc = "http://222.22.63.178" + imgSuffix.replace("\"", "");
                                            if (StringUtils.isNotBlank(imgSrc)) {
                                                quest = quest + imgSrc + ";";
                                            }
                                        }
                                    }
                                }
                            }
                            if (StringUtils.isNotBlank(questElement.text())) {
                                quest = quest + questElements.text();
                            }
                        }
                        if (StringUtils.isNotBlank(quest)) {
                            quest = quest.replace("http://222.22.63.178;", "").replace(" 收藏 扫一扫上传图 预览", "");
                        }
                    }

                    // 选项
                    answersElements = document.select("div[class=shiti-item-left]").select("p[class=answer],p+div");
                    int j = 0;
                    if (answersElements.size() > 0 && answersElements.size() % 2 == 0) {
                        if (null != answersElements && answersElements.size() > 0) {
                            for (int i = 0; i < answersElements.size(); i = i + 2) {
                                if (answersElements.get(i).text().equals("A.")) {
                                    j++;
                                }
                                answers = answers + j + ":" + answersElements.get(i).text();
                                if (answersElements.get(i + 1).html().contains("src")) {
                                    // 有图片
                                    Elements answersImgElements = answersElements.get(i + 1).select("img[src],embed[src]");
                                    for (Element element : answersImgElements) {
                                        String src = "http://222.22.63.178" + element.attr("src").replace("\"", "");
                                        answers = answers + src + ";";
                                    }
                                }
                                answers = answers + answersElements.get(i + 1).text() + ";";
                                if (answersElements.get(i).toString().contains("data-o-right-flag=\"1\"")) {
                                    answer = answer + j + ":" + answersElements.get(i).text();
                                }
                            }
                        }
                    }
                    // 答案
                    if (StringUtils.isBlank(answer)) {
                        answerElements = document.select("p[class=dacuo]");
                        if (null != answerElements && answerElements.size() > 0) {
                            answer = answer + answerElements.get(0).text();
                        }
                    }
                } else if (model.getQuestType().equals(QuestionType.SCT.getCode()) // 上传题
                        || model.getQuestType().equals(QuestionType.SCTFZG.getCode())) { // 上传题(非主观)
                }
                if (StringUtils.isNotBlank(quest)) {
                    model.setQuestName(quest);
                }
                if (StringUtils.isNotBlank(answers) && !model.getQuestType().equals(QuestionType.PDT.getCode())) {
                    model.setAnswersName(answers);
                }
                if (StringUtils.isNotBlank(answer) && !answer.contains("没有详解")) {
                    model.setSubjAnswer(answer);
                    model.setIsReply(1);
                } else {
                    model.setIsReply(2);
                }
                succeed = succeed + examineMapper.update(model);
            }
            System.out.println("succeed:" + succeed);
            System.out.println("fail:" + fail);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 一次性获取已有账号需要考试的所有课程信息（慎用）
     */
    @RequestMapping(value = "/getCourseAll", method = RequestMethod.GET)
    public void getCourseAll() {
        try {
            List<User> users = userMapper.selectForList(new User());
            for (User user : users) {
                String ptopId = questionService.login(user);
                String cookieUrl = "http://222.22.63.178/student/wsdlLogin?ptopid=" + ptopId;
                // 设置cookie
                HttpClient.sendGetNoRedirects(cookieUrl, null);
                String examinesUrl = "http://222.22.63.178/student/courseList";
                String examinesHtml = HttpClient.sendGet(examinesUrl, null);
                Elements courseElements = new Elements();
                Document examineDocument = Jsoup.parse(examinesHtml);
                courseElements = examineDocument.select("li[class=class-list-li]");
                for (Element courseElement : courseElements) {
                    String courseName = courseElement.select("p[class=text_center class-name float-l]").text();
                    String courseUrl = courseElement.select("a[href]").attr("href");
                    String courseId = courseUrl.substring(courseUrl.length() - 4);
                    Course query = courseMapper.selectOne(courseId);
                    if (null != query) {
                        if (!query.getUid().equals(user.getUid())) {
                            query.setUid(user.getUid());
                            courseMapper.update(query);
                        }
                    } else {
                        Course course = new Course();
                        course.setKeId(courseId);
                        course.setKeName(courseName);
                        course.setUid(user.getUid());
                        course.setAmount(0);
                        course.setIsComplete(0);
                        courseMapper.insert(course);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 把课程信息和网考题关联起来
     */
    @RequestMapping(value = "/setCourseForExamine", method = RequestMethod.GET)
    public void setCourseForExamine() {
        try {
            Course model = new Course();
            model.setIsComplete(0);
            List<Course> courses = courseMapper.selectForList(model);
            int k = 0;
            for (Course course : courses) {
                User query = new User();
                query.setUid(course.getUid());
                User user = userMapper.selectOne(query);
                String ptopId = questionService.login(user);
                String cookieUrl = "http://222.22.63.178/student/wsdlLogin?ptopid=" + ptopId;
                // 设置cookie
                HttpClient.sendGetNoRedirects(cookieUrl, null);

                String examineDetailUrl = "http://222.22.63.178/student/courseSelect?studentCourseId=" + course.getUid() + course.getKeId();
                String examineDetailHtml = HttpClient.sendGet(examineDetailUrl, null);
                Document document = Jsoup.parse(examineDetailHtml);
                Elements examineElements = document.select("script[type=text/javascript]");
                JSONArray jsonArray = new JSONArray();
                for (Element element : examineElements) {
                    if (element.html().contains("var questionsJson =")) {
                        String str = element.html().replace("\n", ""); //这里是为了解决 无法多行匹配的问题
                        Matcher matcher = Pattern.compile("var questionsJson = \\[(.*?)\\]").matcher(str);
                        if (matcher.find()) {
                            String questionsVar = matcher.group().replace("var questionsJson =", "");
                            jsonArray = JSONObject.parseArray(questionsVar);
                        }
                    }
                }
                int amount = 0;
                for (int i = 0; i < jsonArray.size(); i++) {
                    String questId = jsonArray.getJSONObject(i).getString("id");
                    Examine examine = examineMapper.selectOne(questId);
                    if (null != examine) {
                        examine.setKeId(course.getKeId());
                        amount = amount + examineMapper.update(examine);
                    }
                    System.out.println(course.getKeId() + "==============关联数量=============" + ++k);
                }
                course.setAmount(amount);
                course.setIsComplete(1);
                courseMapper.update(course);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 全部账户增量注册所有测试和听课
     *
     * @return
     */
    @RequestMapping(value = "/startTest", method = RequestMethod.GET)
    public String startTest() {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            //发送 POST 请求登陆,注册已知课程
            List<User> userList = userMapper.selectForList(new User());
            for (User user : userList) {
                String ptopId = questionService.login(user);
                if (StringUtils.isNotBlank(ptopId)) {
                    Map<String, String> map = new HashMap<>();
                    map.put(user.getUid(), register0(user));
                    list.add(map);
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return JSONObject.toJSONString(list);
    }

    public String register0(User user) {
        try {
            String ptopId = null;
            System.out.println("===========================获取当前账号本学期所有需要考试课程==========================");
            String cookieUrl = "http://222.22.63.178/student/wsdlLogin?ptopid=" + user.getPtopId();
            // 设置cookie
            HttpClient.sendGetNoRedirects(cookieUrl, null);
            String examinesUrl = "http://222.22.63.178/student/courseList";
            String examinesHtml = HttpClient.sendGet(examinesUrl, null);
            if (examinesHtml.contains("若忘记了密码，请联系你所在的学习中心")) {
                return user.getUid() + "：密码错误";
            }
            Elements courseElements = new Elements();
            Document examineDocument = Jsoup.parse(examinesHtml);
            courseElements = examineDocument.select("li[class=class-list-li]");
            for (Element courseElement : courseElements) {
                String courseName = courseElement.select("p[class=text_center class-name float-l]").text();
                String courseUrl = courseElement.select("a[href]").attr("href");
                String courseId = courseUrl.substring(courseUrl.length() - 4);
                Course query = courseMapper.selectOne(courseId);
                if (null != query) {
                    if (!query.getUid().equals(user.getUid())) {
                        query.setUid(user.getUid());
                        courseMapper.update(query);
                    }
                } else {
                    Course course = new Course();
                    course.setKeId(courseId);
                    course.setKeName(courseName);
                    course.setUid(user.getUid());
                    course.setAmount(0);
                    course.setIsComplete(0);
                    courseMapper.insert(course);
                }
            }
            System.out.println("===========================打开学习主页==========================");
            // 打开学习主页
            String reqFinal = "http://171.8.225.170/vls5s/vls3isapi2.dll/getfirstpage?ptopid=" + user.getPtopId();
            String allclass = HttpClient.sendGet(reqFinal, null);
            if (allclass.contains("你的登录信息已经失效")) {
                ptopId = questionService.login(user);
                reqFinal = "http://171.8.225.170/vls5s/vls3isapi2.dll/getfirstpage?ptopid=" + ptopId;
                allclass = HttpClient.sendGet(reqFinal, null);
            }
            // 设置专业 和 设置入学时间
            if (StringUtils.isBlank(user.getMajor()) || StringUtils.isBlank(user.getGrade())) {
                Document allclassDocument = Jsoup.parse(allclass);
                String allclassStr = allclassDocument.text().replace(" ", "");
                // 设置专业
                String major = allclassStr.substring(allclassStr.lastIndexOf("你所学的专业是：") + "你所学的专业是：".length(),
                        allclassStr.lastIndexOf("；年级是："));
                user.setMajor(major);
                // 设置入学时间
                String grade = allclassStr.substring(allclassStr.lastIndexOf("年级是：") + "年级是：".length(),
                        allclassStr.lastIndexOf("年级是：") + "年级是：".length() + 5);
                user.setGrade(grade);
                userMapper.update(user);
            }
            System.out.println("===========================添加本学期听课列表==========================");
            List<String> keChengList = new ArrayList<>();
            Pattern pattern = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
            /*if(allclass.contains("你应已修习")){
                allclass = allclass.substring(0,allclass.indexOf("你应已修习"));
            }*/
            List<String> classList = new ArrayList<>();
            List<String> testList = new ArrayList<>();
            // 听课
            Matcher matcher = pattern.matcher(allclass);
            String info = "";
            while (matcher.find()) {
                String r = matcher.group(1).replace("\"", "");
                if (r.contains("lookonecourse")) {
                    String keId = r.substring(r.indexOf("keid=") + "keid=".length());
                    if (!keId.equals("0027") && !keId.equals("9011")) {
                        keChengList.add(keId);
                        String KeChengDetailUrl = "http://171.8.225.170/vls5s/vls3isapi2.dll/lookonecourse?ptopid=" + user.getPtopId() + "&keid=" + keId;
                        String KeChengDetailHtml = HttpClient.sendGet(KeChengDetailUrl, null);
                        if (KeChengDetailHtml.contains("你的登录信息已经失效")) {
                            ptopId = questionService.login(user);
                            KeChengDetailUrl = "http://171.8.225.170/vls5s/vls3isapi2.dll/lookonecourse?ptopid=" + ptopId + "&keid=" + keId;
                            KeChengDetailHtml = HttpClient.sendGet(KeChengDetailUrl, null);
                        }
                        String ruid = KeChengDetailHtml.substring(KeChengDetailHtml.indexOf("&ruid=") + "&ruid=".length(), KeChengDetailHtml.indexOf("&cid="));
                        Document document = Jsoup.parse(KeChengDetailHtml);
                        String text = document.body().text().trim();
                        // 听课
                        if (/*!text.contains("共10分，你已取得10分") &&*/ !text.contains("因库中无有效课件，你直接取得10分")
                                && !text.contains("因库中无有效课件，你直接取得10分") && !text.contains("点播课件不再计分")) {
                            ClazzUser clazzUser = new ClazzUser();
                            clazzUser.setClzssId(keId);
                            clazzUser.setUid(user.getUid());
                            if (text.contains("共10分，你已取得10分")) {
                                clazzUser.setScore(10);
                                clazzUser.setIsComplete(1);
                            } else {
                                String score = text.substring(text.indexOf("共10分，你已取得") + "共10分，你已取得".length(), text.indexOf("共10分，你已取得") + "共10分，你已取得".length() + 1);
                                clazzUser.setScore(Integer.valueOf(score));
                                clazzUser.setIsComplete(0);
                            }
                            clazzUserMapper.insertNotExist(clazzUser);
                            classList.add(keId);
                        }
                        // 练习
                        String testUrl = "http://171.8.225.170/vls2s/vls3isapi.dll/mygetonetest?ptopid=" + user.getPtopId() + "&ruid=" + ruid + "&keid=" + keId;
                        String testHtml = HttpClient.sendGet(testUrl, null);
                        if (!testHtml.contains("有效自测题数量不足") && !testHtml.contains("因此本课程的在线测试功能已对你关闭")) {
                            JXDocument jxDocument = new JXDocument(testHtml);
                            List<Object> rs = jxDocument.sel("//head/meta/@CONTENT");
                            String url = String.valueOf(rs.get(1));
                            url = url.substring(url.indexOf("http://")).replace("'", "");
                            String sonTestHtml = "";
                            int sum = 0;
                            for (int i = 1; i <= 100; i++) {
                                try{
                                    if (i == 1) {
                                        sonTestHtml = HttpClient.sendGet(url, null);
                                        sum = Integer.valueOf(sonTestHtml.substring(sonTestHtml.indexOf("共") + 1, sonTestHtml.indexOf("条"))) / 25 + 1;
                                    } else {
                                        sonTestHtml = HttpClient.sendGet(url, "&pn=" + i);
                                    }
                                } catch (Exception e) {
                                    info = info + keId+"(存在问题);";
                                    break;
                                }
                                /*if (sonTestHtml.contains("你的登录信息已经失效")) {
                                    ptopId = questionService.login(user);
                                    testHtml = HttpClient.sendGet(url, null);
                                }*/
                                Pattern pattern4 = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
                                Matcher matcher4 = pattern4.matcher(sonTestHtml);
                                while (matcher4.find()) {
                                    String rr = matcher4.group(1).replace("\"", "").replace("testonce0", "testonce");
                                    String ZhangId = rr.substring(rr.indexOf("zhang=") + "zhang=".length());
                                    TestUser testUser = new TestUser();
                                    testUser.setZhangId(ZhangId);
                                    testUser.setUid(user.getUid());
                                    testUser.setIsComplete(0);
                                    testUser.setIsSubmit(0);
                                    testUserMapper.insertNotExist(testUser);
                                    testList.add(rr);
                                }
                                if (i == sum) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }

//            System.out.println("===========================获取sid参数========================================");
//            String sid = "";
//            for (String keCheng : keChengList) {
//                // 测试
//                String testUrl = "http://171.8.225.170/vls2s/vls3isapi.dll/mygetonetest?ptopid=" + user.getPtopId() + "&keid=" + keCheng;
//                String testHtml = HttpClient.sendGet(testUrl, null);
//                if (!testHtml.contains("有效自测题数量不足") && !testHtml.contains("因此本课程的在线测试功能已对你关闭")) {
//                    JXDocument jxDocument = new JXDocument(testHtml);
//                    List<Object> rs = jxDocument.sel("//head/meta/@CONTENT");
//                    String url = String.valueOf(rs.get(1));
//                    url = url.substring(url.indexOf("http://")).replace("'", "");
//                    String sonTestHtml = HttpClient.sendGet(url, null);
//
//
//
//
//                    sid = testHtml.substring(testHtml.indexOf("&sid=") + "&sid=".length(), testHtml.indexOf("&wheres="));
//                }
//
//
//            }
//            if (StringUtils.isBlank(sid)) {
//                return "没有需要测试的科目";
//            }
//            System.out.println("===========================添加本学期测试列表========================================");
//            String needQuery = "http://171.8.225.170/vls2s/vls3isapi.dll/myviewdatalist";
//            String needParam = "ptopid=" + user.getPtopId() + "&sid=" + sid;
//            System.out.println(needQuery + "?" + needParam);
//            List<String> testList = new ArrayList<>();
//            int sum = 0;
//            String testHtml = "";
//            for (int i = 1; i <= 100; i++) {
//                /*if (i == 1) {
//                    testHtml = HttpClient.sendPost(needQuery, needParam);
//                    sum = Integer.valueOf(testHtml.substring(testHtml.indexOf("共") + 1, testHtml.indexOf("条"))) / 25 + 1;
//                }else {
//                    testHtml = HttpClient.sendPost(needQuery, needParam + "&pn=" + i);
//                }*/
//                testHtml = HttpClient.sendGet(needQuery, needParam + "&pn=" + i);
//                if (testHtml.contains("你的登录信息已经失效")) {
//                    ptopId = questionService.login(user);
//                    needParam = "ptopid=" + ptopId + "&sid=" + sid;
//                    testHtml = HttpClient.sendGet(needQuery, needParam);
//                }
//                if (!testHtml.contains("testonce0")) {
//                    break;
//                }
//                Pattern pattern4 = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
//                Matcher matcher4 = pattern4.matcher(testHtml);
//                while (matcher4.find()) {
//                    String r = matcher4.group(1).replace("\"", "").replace("testonce0", "testonce");
//                    String ZhangId = r.substring(r.indexOf("zhang=") + "zhang=".length());
//                    String keId = ZhangId.substring(0, 4);
//                    TestUser testUser = new TestUser();
//                    testUser.setZhangId(ZhangId);
//                    testUser.setUid(user.getUid());
//                    testUser.setIsComplete(0);
//                    testUser.setIsSubmit(0);
//                    testUserMapper.insertNotExist(testUser);
//                    testList.add(r);
//                }
//                /*if (i == sum) {
//                    break;
//                }*/
//            }
            String classInfo = "";
            if (null != classList && classList.size() > 0) {
                classInfo = "需要听课" + classList.size() + "门。";
            } else {
                classInfo = "需要听课0门。";
            }
            String testInfo = "";
            if (null != testList && testList.size() > 0) {
                testInfo = "需要做" + testList.size() + "套测试题。";
            } else {
                testInfo = "需要做0套测试题。";
            }
            return classInfo + testInfo + info;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/openTest", method = RequestMethod.GET)
    public String openTest() {
        try {
            String openTest = questionService.openTest();
            String submitAnswer = questionService.submitAnswer();
            return openTest + "\\n" + submitAnswer;
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
            for (Answer model : answerList) {
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

    /**
     * 把answer里面的没有答案的，在examine里找一遍
     *
     * @return
     */
    @RequestMapping(value = "/answerBuild", method = RequestMethod.GET)
    public Object answerBuild() {
        try {
            Answer answer = new Answer();
            answer.setIsCorrect(2);
            List<Answer> answerList = answerMapper.selectForList(answer);
            for (Answer model : answerList) {
                String questName = model.getQuestName().substring(2);
                String answersName = model.getAnswersName();
                if (StringUtils.isBlank(questName)) {
                    continue;
                }
                Examine query = new Examine();
                query.setQuestName(questName);
                List<Examine> examineList = examineMapper.selectForList(query);
                Examine examine = new Examine();
                if (null == examineList || examineList.size() == 0 || null == examineList.get(0)) {
                    continue;
                }
                if (examineList.size() > 1) {
                    for (Examine examineQuery : examineList) {
                        if (examineList.get(1).getQuestName().equals(questName)) {
                            examine = examineList.get(1);
                            break;
                        }
                    }
                } else {
                    examine = examineList.get(0);
                }
                if (StringUtils.isBlank(examine.getAnswersName()) || StringUtils.isBlank(examine.getSubjAnswer())) {
                    continue;
                }
                String[] answersArr = examine.getAnswersName().split(";");
                String[] subjArr = examine.getSubjAnswer().split("\\.");

                /*List<Map<String,String>> list = new ArrayList<>();
                Map<String,String> map = new HashMap<>();*/
                List<String> list = new ArrayList<>();

                for (int j = 0; j < subjArr.length; j++) {
                    for (int i = 0; i < answersArr.length; i++) {
                        if (subjArr[j].equals(answersArr[i].split("\\.")[0])) {
                            list.add(answersArr[i].split("\\.")[1]);
                        }
                    }
                }
                String newAnswers = "";
                for (String str : list) {
                    if (answersName.contains(str)) {
                        newAnswers = newAnswers + answersName.substring(answersName.indexOf(str) - 2, answersName.indexOf(str) - 1) + ",";
                    }
                }
                model.setAnswers(newAnswers);
                answerMapper.update(model);
            }
            return "完成";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }
}
