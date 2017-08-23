package com.lad.dao;

import com.lad.bo.UserLevelBo;
import com.mongodb.WriteResult;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/23
 */
public interface IUserLevelDao {

    UserLevelBo insert(UserLevelBo userLevelBo);

    UserLevelBo findByUserid(String userid);

    WriteResult update(String id, long num, int type);

}
