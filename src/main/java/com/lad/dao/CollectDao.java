package com.lad.dao;

import com.lad.bo.CollectBo;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 收藏聊天记录
 */
@Repository("collectionDao")
public class CollectDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public CollectBo save(CollectBo chatinfo){
		mongoTemplate.save(chatinfo);
		return chatinfo;
	}
	
	public List<CollectBo> findChatByUserid(String userid){
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, CollectBo.class);
	}
	/**
	 * 删除单条聊天记录
	 * @param chatid
	 * @return
	 */
	public WriteResult delete(String chatid) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("deleted", 1);
		return mongoTemplate.updateFirst(query, update, CollectBo.class);
	}

	public List<CollectBo> findChatByUserid(String userid, String start_id, int limit){
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("deleted").is(0));
		if (StringUtils.isNotEmpty(start_id)) {
			query.addCriteria(new Criteria("_id").gt(start_id));
		}
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		query.limit(limit);
		return mongoTemplate.find(query, CollectBo.class);
	}

}
