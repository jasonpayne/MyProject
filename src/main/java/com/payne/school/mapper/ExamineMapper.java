package com.payne.school.mapper;

import com.payne.school.model.Examine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * question
 *
 * @author xinchao.pan
 * @date 2020/02/04
 */

public interface ExamineMapper {

    /**
     * [查询] 根据主键 id 查询
     *
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    Examine selectOne(String id);

    /**
     * 列表查询
     *
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    List<Examine> selectForList(Examine Examine);

    /**
     * [新增]
     *
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    int insert(Examine Examine);

    /**
     * 批量添加
     *
     * @param list
     * @return
     */
    int insertBatch(@Param("list") List<Examine> list);

    /**
     * [更新]
     *
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    int update(Examine Examine);

}
