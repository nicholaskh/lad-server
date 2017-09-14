package com.lad.dao;

import com.lad.bo.UserTasteBo;
import com.mongodb.WriteResult;

import java.util.LinkedHashSet;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/13
 */
public interface IUserTasteDao {


    UserTasteBo add(UserTasteBo tasteBo);

    WriteResult updateSport(String id, LinkedHashSet<String> sports);

    WriteResult updateMusic(String id, LinkedHashSet<String> musics);

    WriteResult updateLife(String id, LinkedHashSet<String> lifes);

    WriteResult updateTrip(String id, LinkedHashSet<String> trips);

    UserTasteBo findByUserid(String userid);

}
