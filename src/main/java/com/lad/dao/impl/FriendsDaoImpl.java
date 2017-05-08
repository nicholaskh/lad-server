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

	public WriteResult updateBackName(String userid, String firendid, String backName) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("firendid").is(firendid));
		Update update = new Update();
		update.set("backName", backName);
		return mongoTemplate.updateFirst(query, update, HomepageBo.class);
	}

	public WriteResult updateTag(String userid, String firendid, List tag) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("firendid").is(firendid));
		Update update = new Update();
		update.set("tag", tag);
		return mongoTemplate.updateFirst(query, update, HomepageBo.class);
	}

	public WriteResult updatePhone(String userid, String firendid, String phone) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("firendid").is(firendid));
		Update update = new Update();
		update.set("phone", phone);
		return mongoTemplate.updateFirst(query, update, HomepageBo.class);
	}

	public WriteResult updateDescription(String userid, String firendid, String description) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("firendid").is(firendid));
		Update update = new Update();
		update.set("description", description);
		return mongoTemplate.updateFirst(query, update, HomepageBo.class);
	}

	public WriteResult updateVIP(String userid, String firendid, Integer VIP) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("firendid").is(firendid));
		Update update = new Update();
		update.set("VIP", VIP);
		return mongoTemplate.updateFirst(query, update, HomepageBo.class);
	}

	public WriteResult updateBlack(String userid, String firendid, Integer black) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("firendid").is(firendid));
		Update update = new Update();
		update.set("black", black);
		return mongoTemplate.updateFirst(query, update, HomepageBo.class);
	}

}
