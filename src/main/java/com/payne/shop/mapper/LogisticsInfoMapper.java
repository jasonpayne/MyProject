package com.payne.shop.mapper;

import com.payne.shop.model.LogisticsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * logistics_info
 *
 * @author panxinchao
 * @date 2020/11/13
 */
@Mapper
@Repository
public interface LogisticsInfoMapper {

    /**
     * [新增]
     *
     * @author panxinchao
     * @date 2020/11/13
     **/
    int insert(LogisticsInfo logisticsInfo);

    /**
     * [新增(批量)]
     *
     * @author panxinchao
     * @date 2020/10/13
     **/
    int insertBatch(List<LogisticsInfo> logisticsInfoList);

    /**
     * [刪除]
     *
     * @author panxinchao
     * @date 2020/11/13
     **/
    int delete(int id);

    /**
     * [更新]
     *
     * @author panxinchao
     * @date 2020/11/13
     **/
    int update(LogisticsInfo logisticsInfo);

    /**
     * [查询] 根据 inTranCode 查询
     *
     * @author panxinchao
     * @date 2020/11/13
     **/
    LogisticsInfo load(String inTranCode);

    /**
     * [查询] 分页查询
     *
     * @author panxinchao
     * @date 2020/11/13
     **/
    List<LogisticsInfo> pageList(int offset, int pagesize);

    /**
     * [查询] 分页查询 count
     *
     * @author panxinchao
     * @date 2020/11/13
     **/
    int pageListCount(int offset, int pagesize);

}