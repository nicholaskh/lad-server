package com.lad.dao;

import com.lad.bo.DynamicBackBo;
import com.mongodb.WriteResult;

import java.util.HashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/21
 */
public interface IDynamicBackDao {


    DynamicBackBo insert(DynamicBackBo backBo);

    /**
     * 查询黑名单
     * @param userid
     * @return
     */
    DynamicBackBo findByUserid(String userid);

    /**
     * 查询谁的黑名单有我
     * @param userid
     * @return
     */
    List<DynamicBackBo> findWhoBackMe(String userid);

    /**
     * 更新 我不看谁 黑名单
     * @param id
     * @param notSeeBacks
     * @return
     */
    WriteResult updateNotSee(String id, HashSet<String> notSeeBacks);

    /**
     * 更新 不让谁看我黑名单
     * @param id
     * @param notAllowBacks
     * @return
     */
    WriteResult updateNotAllow(String id, HashSet<String> notAllowBacks);

}
