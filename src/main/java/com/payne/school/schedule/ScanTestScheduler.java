package com.payne.school.schedule;

import com.payne.school.mapper.UserMapper;
import com.payne.school.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 测试
 *
 * @author xinchao.pan@bitmain.com
 */
@Component
public class ScanTestScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanTestScheduler.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionService questionService;

    private static final String kecheng = "http://123.15.57.74/vls5s/vls3isapi2.dll/";

    private static final String xuexi = "http://123.15.57.74/vls2s/vls3isapi.dll/";

    /**
     * 定时打开测试题
     */
//    @Scheduled(cron = "0 0,4,8,12,16,20,24,28,32,36,40,44,48,52,56 * * * ?")
//    @Scheduled(cron = "0 0,6,12,18,24,30,36,42,48,54 * * * ?")
    @Scheduled(cron = "0 0/3  * * * ?")
    @Async("asyncScheduleExecutor")
    public void scanTestScheduler() {
        LOGGER.info("定时打开测试题开始作答");
        System.out.println(questionService.openTest());
        System.out.println(questionService.submitAnswer());
    }


    /**
     * 定时提交答案
     */
//    @Scheduled(cron = "0 3,7,11,15,19,23,27,31,35,39,43,47,51,55,59 * * * ?")
//    @Scheduled(cron = "0 0/3 * * * ?")
    /*@Async("asyncScheduleExecutor")
    public void scanSubmitScheduler() {
        LOGGER.info("定时提交答案");
        System.out.println(questionService.submitAnswer());
    }*/
}
