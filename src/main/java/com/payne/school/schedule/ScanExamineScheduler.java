//package com.payne.school.schedule;
//
//import com.alibaba.fastjson.JSONArray;
//import com.payne.school.model.Examine;
//import com.payne.school.model.User;
//import com.payne.school.dao.mapper.ExamineMapper;
//import com.payne.school.dao.mapper.UserMapper;
//import com.payne.school.service.QuestionService;
//import com.payne.school.utils.HttpClient;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * 测试
// *
// * @author xinchao.pan@bitmain.com
// */
//@Component
//public class ScanExamineScheduler {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ScanExamineScheduler.class);
//
//    @Autowired
//    UserMapper userMapper;
//
//    @Autowired
//    ExamineMapper examineMapper;
//
//    @Autowired
//    QuestionService questionService;
//
//    HttpClient HttpClient = new HttpClient();
//
//    private static final String kecheng = "http://123.15.57.74/vls5s/vls3isapi2.dll/";
//
//    private static final String xuexi = "http://123.15.57.74/vls2s/vls3isapi.dll/";
//
//    /**
//     * 定时打开测试题
//     */
////    @Scheduled(cron = "0 0,4,8,12,16,20,24,28,32,36,40,44,48,52,56 * * * ?")
////    @Scheduled(cron = "0 0,6,12,18,24,30,36,42,48,54 * * * ?")
////    @Scheduled(cron = "0 0/3 * * * ?")
//    @Async("asyncScheduleExecutor")
//    public void scanExamineScheduler() {
//        LOGGER.info("定时打开测试题");
//        List<User> users = userMapper.selectForList(new User());
//        String ptopId = questionService.login(users.get(20));
//        String cookieUrl = "http://222.22.63.178/student/wsdlLogin?ptopid="+ptopId;
//        // 设置cookie
//        HttpClient.sendGetNoRedirects(cookieUrl, null);
//        Examine examineQuery = new Examine();
//        examineQuery.setIsReply(0);
//        List<Examine> examines = examineMapper.selectForList(examineQuery);
//        for(Examine model :examines){
//            String examineDetailUrl = "http://222.22.63.178/student/getQuestion?isSimulate=1&qId="+model.getQuestId();
//            String examineDetailHtml = HttpClient.sendGet(examineDetailUrl, null);
//            if(model.getQuestType().equals("aa")){
//
//            }
//            Document document = Jsoup.parse(examineDetailHtml);
//            Elements examineElements = document.select("p");
//            JSONArray jsonArray = new JSONArray();
//            for(Element element : examineElements){
//                System.out.println();
//            }
//        }
//    }
//}
