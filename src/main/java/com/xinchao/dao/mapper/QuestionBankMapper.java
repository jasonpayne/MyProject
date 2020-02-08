package com.xinchao.dao.mapper;

import com.xinchao.dao.entity.QuestionBank;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * question
 * @author xinchao.pan
 * @date 2020/02/04
 */

public interface QuestionBankMapper {

    /**
     * [查询] 根据主键 id 查询
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    QuestionBank selectOne(int id);

    /**
     * 列表查询
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    List<QuestionBank> selectForPage(QuestionBank condition);

    /**
     * [新增]
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    int insert(QuestionBank question);

    /**
     * 人像批量添加
     * @param list
     * @return
     */
    int insertBatch(@Param("list") List<QuestionBank> list);

    /**
     * [更新]
     * @author xinchao.pan
     * @date 2020/02/04
     **/
    int update(QuestionBank question);

}
