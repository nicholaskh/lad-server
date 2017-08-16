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

    List<CircleHistoryBo> findNear(double[] position, double maxDistance);

    List<CircleHistoryBo> findByCricleId(String circleid, Date time, int limit);

    List<CircleHistoryBo> findByUserId(String userid, Date time, int limit);

    CircleHistoryBo findByUserIdAndCircleId(String userid, String circleid);

}
