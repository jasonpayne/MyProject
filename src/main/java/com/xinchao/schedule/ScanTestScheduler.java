package com.xinchao.schedule;

import com.xinchao.dao.entity.User;
import com.xinchao.dao.mapper.UserMapper;
import com.xinchao.model.MajorTest;
import com.xinchao.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
     * 定时扫描去完成测试任务
     * 每隔10分钟执行一次
     */
//    @Scheduled(cron = "0 5,15,25,35,45,55 * * * ?")
    @Async("asyncScheduleExecutor")
    public void scanTestScheduler() {
        LOGGER.info("定时扫描去完成测试任务");
        User testUser = new User();
        testUser.setIsClass(0);
        List<User> testUserList = userMapper.selectForPage(testUser);
        for(User user : testUserList){
            // 打开学习主页（能看到课程） 用session登陆获取本学期专业课列表
            List<String> List = questionService.majorList(user);
            if(null == List){
                // 需要再次登陆
                if(questionService.login(user)){
                    List = questionService.majorList(user);
                }
            }
            // 遍历不同的专业课（练习）
            for(String majorUrl : List){
                MajorTest majorTest = questionService.majorDetailToTest(majorUrl);

            }

        }

    }
}
