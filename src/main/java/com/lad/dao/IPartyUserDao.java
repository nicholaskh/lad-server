package com.lad.dao;

import com.lad.bo.PartyUserBo;
import com.mongodb.WriteResult;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/27
 */
public interface IPartyUserDao {

    PartyUserBo insert(PartyUserBo partyUserBo);


    WriteResult refuse(String id, String info);


    WriteResult apply(String id, String info);



}
