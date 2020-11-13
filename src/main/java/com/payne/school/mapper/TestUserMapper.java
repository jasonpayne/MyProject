package com.payne.school.mapper;

import com.payne.school.model.TestUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学生测试表
 *
 * @author xinchao.pan
 * @date 2020/02/08
 */
@Mapper
@Repository
public interface TestUserMapper {

    /**
     * [查询] 根据主键 id 查询
     *
     * @author xinchao.pan
     * @date 2020/02/08
     **/
    TestUser selectOne(TestUser testUser);

    /**
     * [查询] 分页查询
     *
     * @author xinchao.pan
     * @date 2020/02/08
     **/
    List<TestUser> selectForList(TestUser testUser);

    /**
     * [不存在就新增]
     *
     * @author xinchao.pan
     * @date 2020/02/08
     **/
    int insertNotExist(TestUser testUser);

    /**
     * [新增]
     *
     * @author xinchao.pan
     * @date 2020/02/08
     **/
    int insert(TestUser testUser);

    /**
     * [更新]
     *
     * @author xinchao.pan
     * @date 2020/02/08
     **/
    int update(TestUser testUser);

    /**
     * [刪除]
     *
     * @author xinchao.pan
     * @date 2020/02/08
     **/
    int delete(int id);
}
