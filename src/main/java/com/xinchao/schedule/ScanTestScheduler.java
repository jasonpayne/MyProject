package com.xinchao.schedule;

import com.xinchao.controller.QuestionController;
import com.xinchao.dao.entity.User;
import com.xinchao.dao.mapper.UserMapper;
import com.xinchao.model.MajorTest;
import com.xinchao.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 扫描检测识别端核心板
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
    @Scheduled(cron = "0 0/5 * * * ?")
    @Async("asyncScheduleExecutor")
    public void scanTestScheduler() {
        LOGGER.info("定时打开测试题");
        System.out.println(questionService.openTest());
    }


    /**
     * 定时提交答案
     */
    @Scheduled(cron = "0 4,9,14,19,24,29,34,39,44,49,54,59 * * * ?")
    @Async("asyncScheduleExecutor")
    public void scanTest222Scheduler() {
        LOGGER.info("定时提交答案");
        System.out.println(questionService.submitAnswer());
    }
}
