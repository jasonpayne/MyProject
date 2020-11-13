package com.payne.school.mapper;

import com.payne.school.model.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * question
 *
 * @author xinchao.pan
 * @date 2020/02/04
 */

public interface CourseMapper {

    /**
     * [查询] 根据主键 id 查询
     *
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    Course selectOne(String keId);

    /**
     * 列表查询
     *
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    List<Course> selectForList(Course course);

    /**
     * [新增]
     *
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    int insert(Course course);

    /**
     * 人像批量添加
     *
     * @param list
     * @return
     */
    int insertBatch(@Param("list") List<Course> list);

    /**
     * [更新]
     *
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    int update(Course course);

}
