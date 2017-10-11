package com.lad.dao.impl;

import com.lad.bo.ChatroomUserBo;
import com.lad.dao.IChatroomUserDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Company:d
 * Version: 1.0
 * Time:2017/10/11
 */
@Repository("chatroomUserDao")
public class ChatroomUserDaoImpl implements IChatroomUserDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ChatroomUserBo insert(ChatroomUserBo userBo) {
        mongoTemplate.insert(userBo);
        return userBo;
    }

    @Override
    public ChatroomUserBo findByRoomid(String chatroomid) {
        Query query = new Query();
        query.addCriteria(new Criteria("chatroomid").is(chatroomid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query, ChatroomUserBo.class);
    }

    @Override
    public WriteResult updateNickname(String id, HashMap<String, String> nicknames) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("nicknames", nicknames);
        return mongoTemplate.updateFirst(query, update, ChatroomUserBo.class);
    }

    @Override
    public WriteResult delete(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, ChatroomUserBo.class);
    }

    @Override
    public WriteResult deleteChatroom(String chatroomid) {
        Query query = new Query();
        query.addCriteria(new Criteria("chatroomid").is(chatroomid));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, ChatroomUserBo.class);
    }
}
