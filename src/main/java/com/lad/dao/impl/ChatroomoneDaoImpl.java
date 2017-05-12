package com.lad.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.ChatroomoneBo;
import com.lad.dao.IChatroomoneDao;
import com.mongodb.WriteResult;

@Repository("chatroomoneDao")
public class ChatroomoneDaoImpl implements IChatroomoneDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	public ChatroomoneBo insert(ChatroomoneBo chatroomoneBo) {
		mongoTemplate.insert(chatroomoneBo);
		return chatroomoneBo;
	}

	public ChatroomoneBo updateName(ChatroomoneBo chatroomoneBo) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroomoneBo.getId()));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("name", chatroomoneBo.getName());
		mongoTemplate.updateFirst(query, update, ChatroomoneBo.class);
		return chatroomoneBo;
	}

	public WriteResult delete(String userid, String friendid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("deleted", 1);
		return mongoTemplate.updateFirst(query, update, ChatroomoneBo.class);
	}

	public ChatroomoneBo get(String chatroomoneId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroomoneId));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, ChatroomoneBo.class);
	}

	public ChatroomoneBo get(String userid, String friendid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, ChatroomoneBo.class);
	}

}
