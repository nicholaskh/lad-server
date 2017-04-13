package com.junlenet.mongodb.demo.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.junlenet.mongodb.demo.bo.ThumbsupBo;
import com.junlenet.mongodb.demo.dao.IThumbsupDao;

@Repository("thumbsupDao")
public class ThumbsupDaoImpl implements IThumbsupDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	public ThumbsupBo insert(ThumbsupBo thumbsupBo) {
		mongoTemplate.insert(thumbsupBo);
		return thumbsupBo;
	}

	public List<ThumbsupBo> selectByOwnerId(String ownerId) {
		Query query = new Query();
		query.addCriteria(new Criteria("owner_id").is(ownerId));
		return mongoTemplate.find(query, ThumbsupBo.class);
	}

	public List<ThumbsupBo> selectByVisitorId(String visitorId) {
		Query query = new Query();
		query.addCriteria(new Criteria("visitor_id").is(visitorId));
		return mongoTemplate.find(query, ThumbsupBo.class);
	}

}
