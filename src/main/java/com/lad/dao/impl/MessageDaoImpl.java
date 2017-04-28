package com.lad.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.lad.bo.MessageBo;
import com.lad.dao.IMessageDao;

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

	public List<MessageBo> selectByUserIdPaged(String startId, boolean gt, int limit, String userId) {
		Query query = new Query();
		query.limit(limit);
		query.with(new Sort(new Order(Direction.DESC, "_id")));
		query.addCriteria(new Criteria("owner_id").is(userId));
		if (!StringUtils.isEmpty(startId)) {
			if (gt) {
				query.addCriteria(new Criteria("_id").gt(startId));
			} else {
				query.addCriteria(new Criteria("_id").lt(startId));
			}
		}
		return mongoTemplate.find(query, MessageBo.class);
	}
}
