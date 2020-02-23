package com.xinchao.schedule;

import com.xinchao.dao.mapper.UserMapper;
import com.xinchao.service.ClazzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 听课
 *
 * @author xinchao.pan@bitmain.com
 */
@Component
public class ScanClazzScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanTestScheduler.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    ClazzService clazzService;

    private static final String kecheng = "http://123.15.57.74/vls5s/vls3isapi2.dll/";

    private static final String xuexi = "http://123.15.57.74/vls2s/vls3isapi.dll/";

    /**
     * 定时听课
     */
//    @Scheduled(cron = "0 0,4,8,12,16,20,24,28,32,36,40,44,48,52,56 * * * ?")
//    @Scheduled(cron = "0 0/5 * * * ?")
//    @Scheduled(cron = "0 0,6,12,18,24,30,36,42,48,54 * * * ?")
//    @Scheduled(cron = "0 0/6 * * * ?")
    @Async("asyncScheduleExecutor")
    public void scanTestScheduler() {
        LOGGER.info("定时听课");
        System.out.println(clazzService.listenClazz());
    }
}
