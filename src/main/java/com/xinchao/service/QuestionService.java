package com.xinchao.service;


import com.xinchao.dao.entity.User;
import com.xinchao.model.MajorTest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 实现通用接口
 * @author xinchao.pan
 */
@Service
public interface QuestionService {

    // 登陆接口
    String login(User user);

    // session登陆获取最佳
    List<String> majorList(User user);

    MajorTest majorDetailToTest(String majorUrl);

    List<Map<String,Object>> testList(String majorUrl);

//    MajorTest testDetail(String majorUrl);


    /*List<QuestionBank> selectForPage(QuestionBank condition);

    int insert(QuestionBank question);

    int insertBatch(List<QuestionBank> list);

    int update(QuestionBank question);*/
}
