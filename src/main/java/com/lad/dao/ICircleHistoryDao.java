package com.lad.dao;

import com.lad.bo.CircleHistoryBo;
import com.mongodb.WriteResult;

import java.util.Date;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/16
 */
public interface ICircleHistoryDao {

    
    CircleHistoryBo insert(CircleHistoryBo circleHistoryBo);

    WriteResult updateHistory(String id, double[] position);

    List<CircleHistoryBo> findNear(String cirlcid, String userid, double[] position, double maxDistance);

    List<CircleHistoryBo> findByCricleId(String circleid, Date time, int limit);

    List<CircleHistoryBo> findByUserId(String userid, Date time, int limit);

    CircleHistoryBo findByUserIdAndCircleId(String userid, String circleid);

    /**
     * 根据id查找
     * @param id
     * @return
     */
    CircleHistoryBo findCircleHisById(String id);

    /**
     * 根据用户查找
     * @param userid
     * @param type
     * @param page
     * @param limit
     * @return
     */
    List<CircleHistoryBo> findCircleHisByUserid(String userid, int type, int page, int limit);


    /**
     * 根据圈子查找
     * @param circleid
     * @param type
     * @param page
     * @param limit
     * @return
     */
    List<CircleHistoryBo> findCircleHisByCricleid(String circleid, int type, int page, int limit);


    WriteResult deleteHis(String id);

    /**
     * 批量删除
     * @param ids
     * @return
     */
    WriteResult deleteHisBitch(List<String> ids);
}
