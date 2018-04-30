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

import java.util.HashSet;
import java.util.List;

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
    public List<ChatroomUserBo> findByRoomid(String chatroomid) {
        Query query = new Query();
        query.addCriteria(new Criteria("chatroomid").is(chatroomid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.find(query, ChatroomUserBo.class);
    }

    @Override
    public ChatroomUserBo findByUserAndRoomid(String userid, String chatroomid) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid).and("chatroomid").is(chatroomid));
        return mongoTemplate.findOne(query, ChatroomUserBo.class);
    }

    @Override
    public WriteResult updateNickname(String id, String nickname) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("nickname", nickname);
        update.set("deleted", Constant.ACTIVITY);
        return mongoTemplate.updateFirst(query, update, ChatroomUserBo.class);
    }

    @Override
    public WriteResult updateDisturb(String id, boolean isDisturb) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("isDisturb", isDisturb);
        update.set("deleted", Constant.ACTIVITY);
        return mongoTemplate.updateFirst(query, update, ChatroomUserBo.class);
    }

    @Override
    public WriteResult updateDisturb(String userid, String chatroomid, boolean isDisturb) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("chatroomid").is(chatroomid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("isDisturb", isDisturb);
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
    public WriteResult deleteChatroom(String userid, String chatroomid) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid).and("chatroomid").is(chatroomid)
                .and("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, ChatroomUserBo.class);
    }

    @Override
    public WriteResult deleteChatroom(HashSet<String> userids, String chatroomid) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").in(userids).and("chatroomid").is(chatroomid)
                .and("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateMulti(query, update, ChatroomUserBo.class);
    }

    @Override
    public WriteResult updateShowNick(String id, boolean isShowNick) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("isShowNick", isShowNick);
        update.set("deleted", Constant.ACTIVITY);
        return mongoTemplate.updateFirst(query, update, ChatroomUserBo.class);
    }

    @Override
    public WriteResult updateShowNick(String userid, String chatroomid, boolean isShowNick) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("chatroomid").is(chatroomid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("isShowNick", isShowNick);
        return mongoTemplate.updateFirst(query, update, ChatroomUserBo.class);
    }

    @Override
    public WriteResult updateNickname(String userid, String chatroomid, String nickname) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("chatroomid").is(chatroomid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("nickname", nickname);
        return mongoTemplate.updateFirst(query, update, ChatroomUserBo.class);
    }
}
