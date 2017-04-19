package com.junlenet.mongodb.demo.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.junlenet.mongodb.demo.bo.MessageBo;
import com.junlenet.mongodb.demo.dao.IMessageDao;

@Repository("messageDao")
public class MessageDaoImpl implements IMessageDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	public MessageBo insert(MessageBo messageBo) {
		mongoTemplate.save(messageBo);
		return messageBo;
	}

	public MessageBo selectById(String messageId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(messageId));
		return mongoTemplate.findOne(query, MessageBo.class);
	}

	public List<MessageBo> selectByUserId(String userId) {
		Query query = new Query();
		query.addCriteria(new Criteria("ownerId").is(userId));
		return mongoTemplate.find(query, MessageBo.class);
	}
}
