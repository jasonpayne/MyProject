package com.payne.school.mapper;

import com.payne.school.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户表
 *
 * @author xinchao.pan
 * @date 2020/02/07
 */
@Mapper
@Repository
public interface UserMapper {

    /**
     * [查询] 根据主键 id 查询
     *
     * @author xinchao.pan
     * @date 2020/02/07
     **/
    List<User> selectForList(User user);

    /**
     * [查询] 根据主键 id 查询
     *
     * @author xinchao.pan
     * @date 2020/02/07
     **/
    User selectOne(User user);

    /**
     * [查询] 根据主键 id 查询
     *
     * @author xinchao.pan
     * @date 2020/02/07
     **/
    User login(User user);

    /**
     * [新增]
     *
     * @author xinchao.pan
     * @date 2020/02/07
     **/
    int insert(User user);

    /**
     * [更新]
     *
     * @author xinchao.pan
     * @date 2020/02/07
     **/
    int update(User user);


}