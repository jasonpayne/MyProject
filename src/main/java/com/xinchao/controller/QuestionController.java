package com.xinchao.controller;

import com.xinchao.dao.entity.QuestionBank;
import com.xinchao.dao.entity.User;
import com.xinchao.dao.mapper.QuestionBankMapper;
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
    private QuestionBankMapper questionBankMapper;

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
            String sr = HttpClient.sendPost("http://202.196.64.120/vls2s/zzjlogin.dll/login", "uid="+student.getUid()+"&pw="+student.getPw());
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
            System.out.println("===========================本学期所有需要学习课程========================================");
//        Pattern pattern = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
//        Pattern pattern = Pattern.compile("href=[\"']?((https?://)?/?[^\"']+)[\"']?.*?>(.+)");
            //<a href="http://123.15.57.108/vls5s/vls3isapi2.dll/lookonecourse?ptopid=68E8965430F243C7B86475A86DFD865A&keid=0001">
//        Pattern pattern = Pattern.compile("href\\s?=\\s?(['\"]?)([^'\">\\s]+)\\1[>\\s]");
            List<String> needList = new ArrayList<String>();
            Pattern pattern = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
            Matcher matcher = pattern.matcher(allclass);
            while (matcher.find()) {
                String r = matcher.group(1).replace("\"", "");
                if(r.contains("lookonecourse") && !r.contains("0027") && !r.contains("9011")){
                    System.out.println(r);
                    needList.add(r);
                }
            }

            System.out.println("===========================进入某个专业课(测试)========================================");


            System.out.println("===================================================================");
            // 某个专业课的练习
            String need = HttpClient.sendGet(needList.get(0),  null);
            String keid = needList.get(5).substring(needList.get(5).indexOf("keid=")+"keid=".length());
            String needOne = null;
            Pattern pattern2 = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
            Matcher matcher2 = pattern2.matcher(need);

            while (matcher2.find()) {
                String r = matcher2.group(1).replace("\"", "");
                if(r.contains("mygetonetest")){
                    System.out.println(r);
                    needOne = r;
                    break;
                }
            }
            // ruid
            String ruid = needOne.substring(needOne.indexOf("ruid=")+"ruid=".length(), needOne.indexOf("&keid="));
            System.out.println("===========================进入练习列表(转换URL)========================================");
            String needlianxi = HttpClient.sendGet(needOne,  null);
            String needOneQuery = null;
            Pattern pattern3 = compile("url=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)");
            Matcher matcher3 = pattern3.matcher(needlianxi);
            while (matcher3.find()) {
                String r = matcher3.group(1).replace("\'", "");
                System.out.println(r);
                needOneQuery = r;
            }


            System.out.println("===========================进入练习列表========================================");
            String needlianxilist = HttpClient.sendGet(needOneQuery,  null);
            List<String>  needOneQuerylist = new ArrayList<String>();
//            Pattern pattern4 = compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
//            Pattern pattern4 = compile("(<tr>\\[\\$\\w+\\$\\]</tr>)?");
            Pattern pattern4 = compile("/<table><tr><td>(.*?)<\\/td>/s");
            Matcher matcher4 = pattern4.matcher(needlianxilist);// "开始在线测试";
            while (matcher4.find()) {
                String r = matcher4.group(1).replace("\"", "").replace("testonce0","testonce");
                System.out.println(r);
                needOneQuerylist.add(r);
            }

            System.out.println("===========================进入练习题详情========================================");
            String xiangqing = needOneQuerylist.get(6);
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
            System.out.println(questionSet);

            System.out.println("==============================提交答案===============================");

            // 组装答案，并且初始化数据，如果已经初始化，就直接取出来（这也是填写的答案）
            Map<String,String> intiMap = new TreeMap<>();

            for (String str : questionSet) {
                QuestionBank info = new QuestionBank();
                info.setQuestId(str);
                List<QuestionBank> quest = questionBankMapper.selectForPage(info);
                // 如果存在初始答案则不需要初始化
                if(null != quest && quest.size() > 0){
                    intiMap.put(str,quest.get(0).getAnswers());
                }else{
                    QuestionBank isNotAnswer = new QuestionBank();
                    isNotAnswer.setQuestId(str);
                    isNotAnswer.setZhengId(zhang);
                    isNotAnswer.setKeId(keid);
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
                    questionBankMapper.insert(isNotAnswer);
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

            String param = "submitpaper=submit&ptopid="+ptopId+"&paperid="+student.getUid()+zhang+"&ruid="+ruid;
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
                        QuestionBank questUpdate = new QuestionBank();
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
                        questionBankMapper.update(questUpdate);
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
