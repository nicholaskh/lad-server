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
		query.with(new Sort(Direction.DESC, "createTime"));
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

	@Override
	public List<MessageBo> findUnReadByNoteId(String noteid, int status) {
		Query query = new Query();
		query.addCriteria(new Criteria("deleted").is(0).and("status").is(status)
				.and("targetid").is(noteid).and("type").ne(0));
		return mongoTemplate.find(query, MessageBo.class);
	}

	public List<MessageBo> findUnReadByMyUserid(String userid, String circleid) {
		Query query = new Query();
		Criteria criteria = new Criteria("deleted").is(0).and("status").is(0).and("userid").is(userid);
		criteria.and("circleid").is(circleid).and("type").ne(0);
		query.addCriteria(criteria);
		query.with(new Sort(Direction.DESC, "createTime"));
		return mongoTemplate.find(query, MessageBo.class);
	}

	@Override
	public MessageBo findMessageBySource(String sourceid, int type) {
		Query query = new Query();
		Criteria criteria = new Criteria("sourceid").is(sourceid);
		//-1 表示查询当前帖子的全部内容信息
		if (type != -1) {
			criteria.and("type").is(type);
		}
		query.addCriteria(criteria);
		return mongoTemplate.findOne(query, MessageBo.class);
	}

	@Override
	public WriteResult deleteMessageBySource(String sourceid, int type) {
		Query query = new Query(new Criteria("sourceid").is(sourceid).and("type").is(type));
		return mongoTemplate.remove(query, MessageBo.class);
	}


	public WriteResult deleteMessageByNoteid(String noteid, int type) {
		Query query = new Query();
		Criteria criteria = new Criteria("targetid").is(noteid);
		if (type != -1) {
			criteria.and("type").is(type);
		}
		query.addCriteria(criteria);
		//-1 表示查询当前帖子的全部内容信息
		Update update = new Update();
		update.set("deleted", Constant.DELETED);
		return mongoTemplate.updateMulti(query, update, MessageBo.class);
	}

	@Override
	public WriteResult clearUnReadByMyUserid(String userid, String circleid) {
		Query query = new Query();
		Criteria criteria = new Criteria("deleted").is(0).and("status").is(0).and("userid").is(userid);
		criteria.and("circleid").is(circleid).and("type").ne(0);
		query.addCriteria(criteria);
		Update update = new Update();
		update.set("status", Constant.DELETED);
		return mongoTemplate.updateMulti(query, update, MessageBo.class);
	}
}
