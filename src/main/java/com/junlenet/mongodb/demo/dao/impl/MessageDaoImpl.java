package com.junlenet.mongodb.demo.dao.impl;

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

	public MessageBo update_thumbsup_ids(MessageBo messageBo) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(messageBo.getId()));
		Update update = new Update();
		update.set("thumbsup_ids", messageBo.getThumbsup_ids());
		mongoTemplate.updateFirst(query, update, MessageBo.class);
		return messageBo;
	}

}
