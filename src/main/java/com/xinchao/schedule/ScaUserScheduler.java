package com.xinchao.schedule;

import com.xinchao.dao.entity.ClazzUser;
import com.xinchao.dao.entity.TestUser;
import com.xinchao.dao.entity.User;
import com.xinchao.dao.mapper.ClazzUserMapper;
import com.xinchao.dao.mapper.TestUserMapper;
import com.xinchao.dao.mapper.UserMapper;
import com.xinchao.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 判断某个账户是否测试和听课
 *
 * @author xinchao.pan@bitmain.com
 */
@Component
public class ScaUserScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScaUserScheduler.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    TestUserMapper testUserMapper;

    @Autowired
    ClazzUserMapper clazzUserMapper;

    /**
     * 判断某个账户是否测试完成
     */
//    @Scheduled(cron = "0 0/2 * * * ?")
    @Async("asyncScheduleExecutor")
    public void scanTestScheduler() {
        LOGGER.info("判断某个账户是否测试完成");
        User user = new User();
        user.setIsTest(0);
        List<User> userList = userMapper.selectForList(user);
        for(User queryUser : userList){
            TestUser testUser = new TestUser();
            testUser.setUid(queryUser.getUid());
            List<TestUser> testUserList = testUserMapper.selectForList(testUser);
            boolean flag = true;
            for(TestUser queryTestUser : testUserList){
                if(null == queryTestUser.getIsComplete() || null == queryTestUser.getScore()
                       || queryTestUser.getIsComplete() != 1 || queryTestUser.getScore() != 20){
                    flag = false;
                    break;
                }
            }
            if(flag){
                // TODO 可以把答完的TestUser删了
                queryUser.setIsTest(1);
                userMapper.update(queryUser);
            }
        }
    }


    /**
     * 判断某个账户是否听课完成
     */
//    @Scheduled(cron = "0 0/2 * * * ?")
    @Async("asyncScheduleExecutor")
    public void scanClazzScheduler() {
        LOGGER.info("判断某个账户是否听课完成");
        User user = new User();
        user.setIsClazz(0);
        List<User> userList = userMapper.selectForList(user);
        for(User queryUser : userList){
            ClazzUser clazzUser = new ClazzUser();
            clazzUser.setUid(queryUser.getUid());
            List<ClazzUser> clazzUserList = clazzUserMapper.selectForList(clazzUser);
            boolean flag = true;
            for(ClazzUser queryClazzUser : clazzUserList){
                if(null == queryClazzUser.getIsComplete() || null == queryClazzUser.getScore()
                        || queryClazzUser.getIsComplete() != 1 || queryClazzUser.getScore() != 10){
                    flag = false;
                    break;
                }
            }
            if(flag){
                // TODO 可以把答完的TestUser删了
                queryUser.setIsClazz(1);
                userMapper.update(queryUser);
            }
        }
    }

}
