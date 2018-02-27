package com.lad.dao.impl;

import java.util.List;

import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
		query.addCriteria(new Criteria("_id").is(messageId).and("deleted").is(0));
		return mongoTemplate.findOne(query, MessageBo.class);
	}

	@Override
	public List<MessageBo> findUnReadByUserId(String userId, int status, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("userid").is(userId).and("deleted").is(0);
		if (status != -1) {
			criteria.and("status").is(status);
		}
		query.addCriteria(criteria);
		query.with(new Sort(Direction.DESC, "_id"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, MessageBo.class);
	}

	@Override
	public WriteResult deleteMessage(String id) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(id).and("deleted").is(0));
		Update update = new Update();
		update.set("deleted", Constant.DELETED);
		return mongoTemplate.updateFirst(query, update, MessageBo.class);
	}

	@Override
	public WriteResult deleteMessages(List<String> ids) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").in(ids).and("deleted").is(0));
		Update update = new Update();
		update.set("deleted", Constant.DELETED);
		return mongoTemplate.updateMulti(query, update, MessageBo.class);
	}

	@Override
	public WriteResult updateMessage(String id, int status) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(id).and("deleted").is(0));
		Update update = new Update();
		update.set("status", status);
		return mongoTemplate.updateFirst(query, update, MessageBo.class);
	}

	@Override
	public WriteResult betchUpdateMessage(List<String> ids, int status) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").in(ids).and("deleted").is(0));
		Update update = new Update();
		update.set("status", status);
		return mongoTemplate.updateMulti(query, update, MessageBo.class);
	}
}
