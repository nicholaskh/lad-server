package com.lad.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.FriendsBo;
import com.lad.bo.HomepageBo;
import com.lad.dao.IFriendsDao;
import com.mongodb.WriteResult;

@Repository("friendsDao")
public class FriendsDaoImpl implements IFriendsDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	public FriendsBo insert(FriendsBo friendsBo) {
		mongoTemplate.insert(friendsBo);
		return friendsBo;
	}

	public WriteResult updateBackname(String userid, String friendid, String backname) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("backname", backname);
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public WriteResult updateTag(String userid, String friendid, List tag) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("tag", tag);
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public WriteResult updatePhone(String userid, String friendid, String phone) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("phone", phone);
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public WriteResult updateDescription(String userid, String friendid, String description) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("description", description);
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public WriteResult updateVIP(String userid, String friendid, Integer VIP) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("VIP", VIP);
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public WriteResult updateBlack(String userid, String friendid, Integer black) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("black", black);
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public FriendsBo getFriendByIdAndVisitorId(String userid, String friendid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("apply").is(1));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, FriendsBo.class);
	}

	public List<FriendsBo> getFriendByUserid(String userid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("apply").is(1));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, FriendsBo.class);
	}

	public List<FriendsBo> getFriendByFirendid(String friendid) {
		Query query = new Query();
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("apply").is(1));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, FriendsBo.class);
	}

	public WriteResult delete(String userid, String friendid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("deleted", 1);
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public WriteResult updateApply(String id, int apply) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(id));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("apply", apply);
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public List<FriendsBo> getApplyFriendByuserid(String userid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("apply").is(0));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, FriendsBo.class);
	}

	public FriendsBo get(String id) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(id));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, FriendsBo.class);
	}

}
