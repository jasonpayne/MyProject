package com.payne.school.dao.mapper;

import com.payne.school.dao.entity.Answer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * question
 * @author xinchao.pan
 * @date 2020/02/04
 */

public interface AnswerMapper {

    /**
     * [查询] 根据主键 id 查询
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    Answer selectOne(String id);

    /**
     * 列表查询
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    List<Answer> selectForList(Answer answer);

    /**
     * [新增]
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    int insert(Answer answer);

    /**
     * 人像批量添加
     * @param list
     * @return
     */
    int insertBatch(@Param("list") List<Answer> list);

    /**
     * [更新]
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    int update(Answer answer);

}
