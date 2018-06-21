package com.lad.dao.impl;

import com.lad.bo.PartyBo;
import com.lad.dao.IPartyDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/7
 */

@Repository("partyDao")
public class PartyDaoImpl implements IPartyDao {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public PartyBo insert(PartyBo partyBo) {
        mongoTemplate.insert(partyBo);
        return partyBo;
    }

    @Override
    public WriteResult update(PartyBo partyBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(partyBo.getId()));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("title", partyBo.getTitle());
        update.set("content", partyBo.getContent());
        update.set("startTime", partyBo.getStartTime());
        update.set("addrType", partyBo.getAddrType());
        update.set("addrInfo", partyBo.getAddrInfo());
        update.set("position", partyBo.getPosition());
        update.set("landmark", partyBo.getLandmark());
        update.set("payOrFree", partyBo.getPayOrFree());
        update.set("payAmount", partyBo.getPayAmount());
        update.set("payName", partyBo.getPayName());
        update.set("payInfo", partyBo.getPayInfo());
        update.set("appointment", partyBo.getAppointment());
        update.set("userLimit", partyBo.getUserLimit());
        update.set("isPhone", partyBo.isPhone());
        update.set("isOpen", partyBo.isOpen());
        update.set("reminder", partyBo.getReminder());
        update.set("photos", partyBo.getPhotos());
        update.set("status", partyBo.getStatus());
        update.set("video", partyBo.getVideo());
        update.set("videoPic", partyBo.getVideoPic());
        update.set("backPic", partyBo.getBackPic());
        return mongoTemplate.updateFirst(query, update, PartyBo.class);
    }

    @Override
    public WriteResult delete(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, PartyBo.class);
    }


    @Override
    public PartyBo findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query, PartyBo.class);
    }

    @Override
    public List<PartyBo> findByCreate(String createid, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("createuid").is(createid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "status")));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        page = page < 1 ? 1 : page;
        query.skip((page -1)*limit);
        query.limit(limit);
        return mongoTemplate.find(query, PartyBo.class);
    }

    @Override
    public List<PartyBo> findByMyJoin(String userid, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("createuid").ne(userid));
        query.addCriteria(new Criteria("users").in(userid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "status")));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        page = page < 1 ? 1 : page;
        query.skip((page -1)*limit);
        query.limit(limit);
        return mongoTemplate.find(query, PartyBo.class);
    }

    @Override
    public WriteResult updateUser(String id, List<String> users, int userNum) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("users", users);
        update.set("partyUserNum", userNum);
        return mongoTemplate.updateFirst(query, update, PartyBo.class);
    }

    @Override
    public WriteResult updateRefus(String id, LinkedHashSet<String> refuses) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("refuseUsers", refuses);
        return mongoTemplate.updateFirst(query, update, PartyBo.class);
    }

    @Override
    public WriteResult updateVisit(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.inc("visitNum", 1);
        return mongoTemplate.updateFirst(query, update, PartyBo.class);
    }

    @Override
    public WriteResult updateShare(String id, int num) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.inc("shareNum", num);
        return mongoTemplate.updateFirst(query, update, PartyBo.class);
    }

    @Override
    public WriteResult updateCollect(String id, int num) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.inc("collectNum", num);
        return mongoTemplate.updateFirst(query, update, PartyBo.class);
    }

    @Override
    public WriteResult updateReport(String id, int num) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.inc("reportNum", num);
        return mongoTemplate.updateFirst(query, update, PartyBo.class);
    }

    @Override
    public List<PartyBo> findByMyApply(String userid, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("createuid").ne(userid));
        query.addCriteria(new Criteria("applyUsers").in(userid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "status")));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        page = page < 1 ? 1 : page;
        query.skip((page -1)*limit);
        query.limit(limit);
        return mongoTemplate.find(query, PartyBo.class);
    }

    @Override
    public WriteResult updateChatroom(String partyid, String chatroomid) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(partyid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("chatroomid", chatroomid);
        return mongoTemplate.updateFirst(query, update, PartyBo.class);
    }

    @Override
    public List<PartyBo> findByCircleid(String circleid, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("circleid").is(circleid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "status")));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        page = page < 1 ? 1 : page;
        query.skip((page -1)*limit);
        query.limit(limit);
        return  mongoTemplate.find(query, PartyBo.class);
    }

    @Override
    public WriteResult outParty(String id, String userid) {
        return null;
    }

    @Override
    public WriteResult updatePartyStatus(String id, int status) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("status", status);
        return mongoTemplate.updateFirst(query, update, PartyBo.class);
    }

    @Override
    public long findNumByCircleid(String circleid) {
        Query query = new Query();
        query.addCriteria(new Criteria("circleid").is(circleid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.count(query, PartyBo.class);
    }

    public GeoResults<PartyBo> findNearParty(double[] position, int maxDistance, int limit,int page){
        Point point = new Point(position[0],position[1]);
        NearQuery near =NearQuery.near(point);
        Distance distance = new Distance(maxDistance/1000, Metrics.KILOMETERS);
        near.maxDistance(distance);
        Query query = new Query();
		page = page < 1 ? 1 : page;
		query.skip((page -1) * limit);
        query.limit(limit);
        near.query(query);
        return mongoTemplate.geoNear(near, PartyBo.class);
    }
}
