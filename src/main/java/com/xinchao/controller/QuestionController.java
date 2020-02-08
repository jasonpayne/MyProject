package com.xinchao.controller;

import com.xinchao.dao.entity.Answer;
import com.xinchao.dao.entity.User;
import com.xinchao.dao.mapper.AnswerMapper;
import com.xinchao.dao.mapper.UserMapper;
import com.xinchao.enums.DanXuan;
import com.xinchao.enums.DuoXuan;
import com.xinchao.enums.PanDuan;
import com.xinchao.model.Student;
import com.xinchao.service.QuestionService;
import com.xinchao.utils.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * QuestionController
 * @author xinchao.pan 2020-02-04
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private AnswerMapper AnswerMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionService questionService;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(@RequestBody User user) {
        try {
            //发送 POST 请求登陆,返回
            if(questionService.login(user)) {
                return "对不起，你输入的账号和密码未通过系统的验证";
            }else{
                User loginInfo = userMapper.selectOne(user);
                if(null != loginInfo){
                    if(loginInfo.getIsClass()==0 || loginInfo.getIsClass()==0){
                        return "已经注册成功，正在操作，请稍等登陆查看";
                    }else {
                        return "已经注册成功，练习和答题已完成，请立即登陆查看";
                    }
                }else{
                    // 插入系统，并且初始化数据
                    user.setIsClass(0);
                    user.setIsTest(0);
                    userMapper.insert(user);
                    return "刚刚注册成功，正在操作，请稍等登陆查看";
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public String start(@RequestBody Student student) {
        try {
            for(int i=0;1<20;i++){
                String aa = start0(student);
                if(aa.equals("已经全部答对")){
                    return "结束了";
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public String start0(Student student) {
        try {
            //发送 POST 请求登陆
            String sr = HttpClient.sendPost("http://171.8.225.125/vls2s/zzjlogin.dll/login", "uid="+student.getUid()+"&pw="+student.getPw());
            String req = sr.substring(sr.indexOf("window.location")+"window.location".length()+ 2,sr.indexOf("&sid=")+"&sid=".length() );
            String reqFinal = req.replace("getmain","getfirstpage")
                    .replace("&sid=","").replace("vls2s","vls5s").
                            replace("vls3isapi","vls3isapi2");
            System.out.println("==============================登陆=====================================");
            // ptopId
            String ptopId = reqFinal.substring(reqFinal.indexOf("ptopid=")+"ptopid=".length());
            System.out.println(reqFinal);
            // 打开学习主页
            String allclass = HttpClient.sendGet(reqFinal,  null);

            System.out.println("===========================本学期所有需要学习课程==========================");
            List<String> needList = new ArrayList<String>();
            Pattern pattern = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
            Matcher matcher = pattern.matcher(allclass);
            while (matcher.find()) {
                String r = matcher.group(1).replace("\"", "");
                if(r.contains("lookonecourse") && !r.contains("0027") && !r.contains("9011")){
                    System.out.println(r);
                    needList.add(r.substring(r.indexOf("keid=")+"keid=".length()));
                }
            }
            System.out.println("===========================获取sid参数========================================");
            String need = "http://171.8.225.170/vls2s/vls3isapi.dll/mygetonetest?ptopid="+ptopId+"&keid="+needList.get(0);
            String need0 = HttpClient.sendGet(need,  null);
            String sid = need0.substring(need0.indexOf("&sid=")+"&sid=".length(),need0.indexOf("&wheres="));

            System.out.println("===========================进入测试列表========================================");
            String needQuery = "http://171.8.225.170/vls2s/vls3isapi.dll/myviewdatalist";
            String needParam = "ptopid="+ptopId+"&sid="+sid;
            List<String>  testList = new ArrayList<>();
            int sum = 0;
            for(int i=1;i<=10;i++){
                System.out.println(needQuery+"?"+needParam+"&pn="+i);
                String testHtml = HttpClient.sendPost(needQuery,  needParam+"&pn="+i);
                Pattern pattern4 = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
                Matcher matcher4 = pattern4.matcher(testHtml);// "开始在线测试";
                while (matcher4.find()) {
                    String r = matcher4.group(1).replace("\"", "").replace("testonce0","testonce");
                    System.out.println(r);
                    testList.add(r);
                }
                if(i == 1){
                    sum = Integer.valueOf(testHtml.substring(testHtml.indexOf("共")+1,testHtml.indexOf("条")))/25+1;
                }
                if(i == sum){
                    break;
                }
            }

            System.out.println("===========================进入测试详情========================================");
            String xiangqing = testList.get(7);
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
                    if(r.contains("A") || r.contains("B") || r.contains("C") || r.contains("D") || r.contains("E")){
                        questionSet.add(r.substring(0,r.length()-1));
                    }else {
                        questionSet.add(r);
                    }
                }
            }
            System.out.println(questionSet);

            System.out.println("==============================提交答案===============================");

            // 组装答案，并且初始化数据，如果已经初始化，就直接取出来（这也是填写的答案）
            Map<String,String> intiMap = new TreeMap<>();

            for (String str : questionSet) {
                Answer info = new Answer();
                info.setQuestId(str);
                List<Answer> quest = AnswerMapper.selectForPage(info);
                // 如果存在初始答案则不需要初始化
                if(null != quest && quest.size() > 0){
                    intiMap.put(str,quest.get(0).getAnswers());
                }else{
                    Answer isNotAnswer = new Answer();
                    isNotAnswer.setQuestId(str);
                    isNotAnswer.setZhengId(zhang);
                    if(str.contains(zhang+1)){
                        isNotAnswer.setAnswers("A");
                    }
                    else if(str.contains(zhang+2)){
                        isNotAnswer.setAnswers("A,B,C,D");
                    }
                    else if(str.contains(zhang+3)){
                        isNotAnswer.setAnswers("Y");
                    }
                    isNotAnswer.setIsCorrect(-1);
                    intiMap.put(str,isNotAnswer.getAnswers());
                    // 如果不存在初始答案则要初始化
                    AnswerMapper.insert(isNotAnswer);
                }
            }

            // 等待提交的答案
            Map<String,String> answerMap = new TreeMap<>();
            for (Map.Entry<String, String> map : intiMap.entrySet()) {
                if(map.getKey().contains(zhang+1)) {
                    answerMap.put(map.getKey(), map.getValue());
                }else if(map.getKey().contains(zhang+2)){
                    List<String> duoXuans = Arrays.asList(map.getValue().split(","));
                    for(String duoXuan : duoXuans){
                        answerMap.put(map.getKey()+duoXuan ,duoXuan);
                    }
                }else if(map.getKey().contains(zhang+3)){
                    answerMap.put(map.getKey(), map.getValue());
                }
            }

            String param = "submitpaper=submit&ptopid="+ptopId+"&paperid="+student.getUid()+zhang;
            for (Map.Entry<String, String> map : answerMap.entrySet()) {
                param = param + "&" + map.getKey() + "=" + map.getValue();
            }
            // 答题需要延迟2分钟提交
            int k = 1;
            while (k<5) {
                Thread.sleep(31000);
                k++;
                System.out.println("等待中========================"+k);
            }
            String submitURL = xiangqing.substring(0,xiangqing.indexOf("?")).replace("testonce","smpaper");
            String submit = HttpClient.sendPost(submitURL, param);

            Matcher matcherPoint = compile("<span [^>]*>([^<]*)</span>").matcher(submit);
            String info = "";
            while (matcherPoint.find()) {
                String m = matcherPoint.group(1);
                if(m.contains("分")){
                    info = m;
                }
            }
            if(submit.contains("抱歉")) {
                return "提交错误";
            }
            if(submit.contains("交卷操作成功完成")) {
                Pattern pattern6 = compile("<font [^>]*>([^<]*)</font>");
                Matcher matcher6 = pattern6.matcher(submit);
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
                for (Map.Entry<String, String> map : intiMap.entrySet()) {
                    for(int i=0;i<resultList.size();i++) {
                        Answer questUpdate = new Answer();
                        questUpdate.setQuestId(map.getKey());
                        if(resultList.get(i).equals("正确")){
                            questUpdate.setAnswers(map.getValue());
                            questUpdate.setIsCorrect(1);
                        }else if(resultList.get(i).equals("错误")){
                            // 单选
                            if(map.getKey().contains(zhang+1)){
                                questUpdate.setAnswers(DanXuan.getNote(DanXuan.getCode(map.getValue())+1));
                            }
                            // 多选
                            else if(map.getKey().contains(zhang+2)){
                                questUpdate.setAnswers(DuoXuan.getNote(DuoXuan.getCode(map.getValue())+1));
                            }
                            // 判断
                            else if(map.getKey().contains(zhang+3)){
                                questUpdate.setAnswers(PanDuan.getNote(PanDuan.getCode(map.getValue())+1));
                            }
                            questUpdate.setIsCorrect(0);
                        }
                        AnswerMapper.update(questUpdate);
                        resultList.remove(i);
                        break;
                    }
                }
                if(info.contains("20分")){
                    System.out.println("已经全部答对");
                    return "已经全部答对";
                }else{
                    System.out.println("当前:"+ info +"，需要继续答题");
                    return "当前:"+ info +"，需要继续答题";
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "答题发生错误";
    }

}
