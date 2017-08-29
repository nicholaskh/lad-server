package com.lad.dao.impl;

import com.lad.bo.UserLevelBo;
import com.lad.dao.IUserLevelDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/23
 */
@Repository("userLevelDao")
public class UserLevelDaoImpl implements IUserLevelDao{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public UserLevelBo insert(UserLevelBo userLevelBo) {
        mongoTemplate.insert(userLevelBo);
        return userLevelBo;
    }

    @Override
    public UserLevelBo findByUserid(String userid) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        return mongoTemplate.findOne(query, UserLevelBo.class);
    }

    @Override
    public WriteResult update(String id, long num, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        switch (type){
            case Constant.LEVEL_HOUR:
                update.inc("onlineHours", num);
            case Constant.LEVEL_PARTY:
                update.inc("launchPartys", num);
            case Constant.LEVEL_NOTE:
                update.inc("noteNum", num);
            case Constant.LEVEL_COMMENT:
                update.inc("commentNum", num);
            case Constant.LEVEL_TRANS:
                update.inc("transmitNum", num);
            case Constant.LEVEL_SHARE:
                update.inc("shareNum", num);
            case Constant.LEVEL_CIRCLE:
                update.inc("circleNum", num);
            default:
                break;
        }
        return mongoTemplate.updateFirst(query, update, UserLevelBo.class);
    }
}
