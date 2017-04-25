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

import com.lad.bo.ThumbsupBo;
import com.lad.dao.IThumbsupDao;

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

	public List<ThumbsupBo> selectByOwnerIdPaged(String startId, boolean gt, int limit, String ownerId) {
		Query query = new Query();
		query.limit(limit);
		query.with(new Sort(new Order(Direction.DESC, "_id")));
		query.addCriteria(new Criteria("owner_id").is(ownerId));
		if (!StringUtils.isEmpty(startId)) {
			if (gt) {
				query.addCriteria(new Criteria("_id").gt(startId));
			} else {
				query.addCriteria(new Criteria("_id").lt(startId));
			}
		}
		return mongoTemplate.find(query, ThumbsupBo.class);
	}

	public List<ThumbsupBo> selectByVisitorIdPaged(String startId, boolean gt, int limit, String visitorId) {
		Query query = new Query();
		query.limit(limit);
		query.with(new Sort(new Order(Direction.DESC, "_id")));
		query.addCriteria(new Criteria("visitor_id").is(visitorId));
		if (!StringUtils.isEmpty(startId)) {
			if (gt) {
				query.addCriteria(new Criteria("_id").gt(startId));
			} else {
				query.addCriteria(new Criteria("_id").lt(startId));
			}
		}
		return mongoTemplate.find(query, ThumbsupBo.class);
	}

}
