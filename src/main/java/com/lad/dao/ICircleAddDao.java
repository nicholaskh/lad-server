package com.lad.dao;

import com.lad.bo.CircleAddBo;
import com.mongodb.WriteResult;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/17
 */
public interface ICircleAddDao {

    CircleAddBo insert(CircleAddBo addBo);


    CircleAddBo findByUserAndCircle(String userid, String circleid);
    

    WriteResult updateJoinStatus(String id, int status);

}
