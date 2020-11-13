package com.payne.school.mapper;

import com.payne.school.model.ClazzUser;
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
public interface ClazzUserMapper {

    /**
     * [查询] 根据主键 id 查询
     *
     * @author xinchao.pan
     * @date 2020/02/08
     **/
    ClazzUser selectOne(ClazzUser clazzUser);

    /**
     * [查询] 分页查询
     *
     * @author xinchao.pan
     * @date 2020/02/08
     **/
    List<ClazzUser> selectForList(ClazzUser clazzUser);

    /**
     * [不存在就新增]
     *
     * @author xinchao.pan
     * @date 2020/02/08
     **/
    int insertNotExist(ClazzUser clazzUser);

    /**
     * [新增]
     *
     * @author xinchao.pan
     * @date 2020/02/08
     **/
    int insert(ClazzUser clazzUser);

    /**
     * [更新]
     *
     * @author xinchao.pan
     * @date 2020/02/08
     **/
    int update(ClazzUser clazzUser);

    /**
     * [刪除]
     *
     * @author xinchao.pan
     * @date 2020/02/08
     **/
    int delete(int id);

}
