package com.lad.dao;

import com.lad.bo.UserTagBo;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * 功能描述：用户标签dao
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/28
 */
public interface IUserTagDao {


    UserTagBo insert(UserTagBo tagBo);


    WriteResult delete(String id);


    UserTagBo findByName(String name, String userid);


    List<UserTagBo> findByUserid(String userid, int type);
    

    WriteResult updateTimes(String name, String userid, int type);


}
