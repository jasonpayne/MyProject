package com.payne.school.service.impl;

import com.payne.school.mapper.ClazzUserMapper;
import com.payne.school.mapper.UserMapper;
import com.payne.school.model.ClazzUser;
import com.payne.school.model.User;
import com.payne.school.service.ClazzService;
import com.payne.school.service.QuestionService;
import com.payne.school.utils.HttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClazzServiceImpl implements ClazzService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    ClazzUserMapper clazzUserMapper;

    @Autowired
    QuestionService questionService;

    com.payne.school.utils.HttpClient HttpClient = new HttpClient();

    @Override
    public String listenClazz() {
        // 进去那个页面(测试和测试主页列表)  http://171.8.225.170/vls5s/vls3isapi2.dll/lookonecourse?
        // ptopid=2B23987523F14866B116D23090D25C18&keid=7312

        // 进入听课列表里面  http://171.8.225.170/vls2s/vls3isapi.dll/kelista?
        // ptopid=2B23987523F14866B116D23090D25C18&ruid=132012599733&cid=7310&fun2=1
        try {
            ClazzUser clazzUser = new ClazzUser();
            clazzUser.setIsComplete(0);
            List<ClazzUser> clazzUserList = clazzUserMapper.selectForList(clazzUser);
            String ptopId = null;
            for (ClazzUser model : clazzUserList) {
                User user = new User();
                user.setUid(model.getUid());
                User nowUser = userMapper.selectOne(user);
                ptopId = nowUser.getPtopId();
                String KeChengDetailUrl = "http://171.8.225.170/vls5s/vls3isapi2.dll/lookonecourse?ptopid=" + ptopId + "&keid=" + model.getClzssId();
                String KeChengDetailHtml = HttpClient.sendGet(KeChengDetailUrl, null);
                if (KeChengDetailHtml.contains("你的登录信息已经失效") || KeChengDetailHtml.equals("发送GET请求出现异常")) {
                    ptopId = questionService.login(nowUser);
                    KeChengDetailUrl = "http://171.8.225.170/vls5s/vls3isapi2.dll/lookonecourse?ptopid=" + ptopId + "&keid=" + model.getClzssId();
                    KeChengDetailHtml = HttpClient.sendGet(KeChengDetailUrl, null);
                }
                Document document = Jsoup.parse(KeChengDetailHtml);
                String text = document.body().text().trim();
                if (text.contains("共10分，你已取得10分")) {
                    model.setScore(10);
                    model.setIsComplete(1);
                    clazzUserMapper.update(model);
                    continue;
                } else {
                    String score = text.substring(text.indexOf("共10分，你已取得") + "共10分，你已取得".length(), text.indexOf("共10分，你已取得") + "共10分，你已取得".length() + 1);
                    model.setScore(Integer.valueOf(score));
                    clazzUserMapper.update(model);
                    // 点进去做任务
                    String clazzUrl = "http://171.8.225.170/vls2s/vls3isapi.dll/kelista?ptopid=" + ptopId + "&cid=" + model.getClzssId() + "&fun2=1";
                    String clazzHtml = HttpClient.sendGet(clazzUrl, null);
                    if (clazzHtml.contains("你的登录信息已经失效") || clazzHtml.equals("发送GET请求出现异常")) {
                        ptopId = questionService.login(nowUser);
                        clazzUrl = "http://171.8.225.170/vls5s/vls3isapi2.dll/kelista?ptopid=" + ptopId + "&cid=" + model.getClzssId() + "&fun2=1";
                        clazzHtml = HttpClient.sendGet(clazzUrl, null);
                    }
                    if (clazzHtml.contains("对不起，本系统目前尚未提供此功能")) {
                        model.setScore(10);
                        model.setIsComplete(1);
                        clazzUserMapper.update(model);
                        continue;
                    } else {
                        Document documentDetail = Jsoup.parse(clazzHtml);
//                        Elements elements = documentDetail.select("a[href]").select("font[color=#0000FF]");
                        Elements elements = documentDetail.select("a");
                        for (Element element : elements) {
                            if (null != element && element.toString().contains("#0000FF")) {
                                System.out.println("打开听课:" + element.attr("href"));
                                HttpClient.sendGet(element.attr("href"), null);
                                break;
                            }
                        }
                    }
                }
            }
            return "听课中。。。。。。";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }
}
