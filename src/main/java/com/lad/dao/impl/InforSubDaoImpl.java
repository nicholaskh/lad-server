package com.lad.dao.impl;

import com.lad.bo.InforSubscriptionBo;
import com.lad.dao.IInforSubDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/5
 */
@Repository("inforSubDao")
public class InforSubDaoImpl implements IInforSubDao {
    
    @Autowired
    private MongoTemplate mongoTemplate;


    public InforSubscriptionBo insert(InforSubscriptionBo inforSubscriptionBo){
        mongoTemplate.insert(inforSubscriptionBo);
        return inforSubscriptionBo;
    }

    public WriteResult updateSub(String id, int type, LinkedHashSet<String> list){
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        switch (type){
            case Constant.INFOR_HEALTH:
                update.set("subscriptions", list);
                break;
            case Constant.INFOR_SECRITY:
                update.set("securitys", list);
                break;
            case Constant.INFOR_RADIO:
                update.set("radios", list);
                break;
            case Constant.INFOR_VIDEO:
                update.set("videos", list);
                break;
            case Constant.INFOR_DAILY:
                update.set("dailys", list);
                break;
            case Constant.INFOR_YANGLAO:
                update.set("yanglaos", list);
                break;
            default:
                return null;
        }
        return mongoTemplate.updateFirst(query, update, InforSubscriptionBo.class);
    }

    public WriteResult updateSecuritys(String userid, LinkedHashSet<String> securitys){
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("securitys", securitys);
        return mongoTemplate.updateFirst(query, update, InforSubscriptionBo.class);
    }

    public WriteResult updateCollect(String userid, LinkedHashSet<String> collects){
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("collects", collects);
        return mongoTemplate.updateFirst(query, update, InforSubscriptionBo.class);
    }

    public InforSubscriptionBo findByUserid(String userid){
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query, InforSubscriptionBo.class);
    }

    

}
