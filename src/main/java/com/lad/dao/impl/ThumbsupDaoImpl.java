package com.lad.dao.impl;

import com.lad.bo.ThumbsupBo;
import com.lad.dao.IThumbsupDao;
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

import java.util.List;

@Repository("thumbsupDao")
public class ThumbsupDaoImpl implements IThumbsupDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	public ThumbsupBo insert(ThumbsupBo thumbsupBo) {
		mongoTemplate.insert(thumbsupBo, "thumbsup");
		return thumbsupBo;
	}

	public WriteResult delete(String thumbsupId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(thumbsupId));
		Update update = new Update();
		update.set("deleted", 1);
		return mongoTemplate.updateFirst(query,update,ThumbsupBo.class );
	}

	@Override
	public ThumbsupBo findIsDelete(String ownerid, String visitorid) {
		Query query = new Query();
		query.addCriteria(new Criteria("owner_id").is(ownerid));
		query.addCriteria(new Criteria("visitor_id").is(visitorid));
		return mongoTemplate.findOne(query, ThumbsupBo.class);
	}

	public List<ThumbsupBo> selectByOwnerId(String ownerId) {
		Query query = new Query();
		query.addCriteria(new Criteria("owner_id").is(ownerId));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, ThumbsupBo.class);
	}

	public long selectByOwnerIdCount(String ownerId) {
		Query query = new Query();
		query.addCriteria(new Criteria("owner_id").is(ownerId));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.count(query, ThumbsupBo.class);
	}

	public List<ThumbsupBo> selectByVisitorId(String visitorId) {
		Query query = new Query();
		query.addCriteria(new Criteria("visitor_id").is(visitorId));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, ThumbsupBo.class);
	}

	public List<ThumbsupBo> selectByOwnerIdPaged(String startId, boolean gt, int limit, String ownerId, int type){
		Query query = new Query();
		query.addCriteria(new Criteria("deleted").is(0));
		query.addCriteria(new Criteria("owner_id").is(ownerId));
		query.addCriteria(new Criteria("type").is(type));
		query.limit(limit);
		query.with(new Sort(new Order(Direction.DESC, "_id")));
		if (!StringUtils.isEmpty(startId)) {
			if (gt) {
				query.addCriteria(new Criteria("_id").gt(startId));
			} else {
				query.addCriteria(new Criteria("_id").lt(startId));
			}
		}
		return mongoTemplate.find(query, ThumbsupBo.class);
	}

	public List<ThumbsupBo> selectByVisitorIdPaged(String startId, boolean gt, int limit, String visitorId,int type) {
		Query query = new Query();
		query.limit(limit);
		query.with(new Sort(new Order(Direction.DESC, "_id")));
		query.addCriteria(new Criteria("visitor_id").is(visitorId));
		query.addCriteria(new Criteria("type").is(type));
		query.addCriteria(new Criteria("deleted").is(0));
		if (!StringUtils.isEmpty(startId)) {
			if (gt) {
				query.addCriteria(new Criteria("_id").gt(startId));
			} else {
				query.addCriteria(new Criteria("_id").lt(startId));
			}
		}
		return mongoTemplate.find(query, ThumbsupBo.class);
	}

	public ThumbsupBo getByVidAndVisitorid(String vid, String visitorid) {
		Query query = new Query();
		query.addCriteria(new Criteria("owner_id").is(vid));
		query.addCriteria(new Criteria("visitor_id").is(visitorid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, ThumbsupBo.class);
	}

	public WriteResult updateDelete(String thumbsupId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(thumbsupId));
		Update update = new Update();
		update.set("deleted", 0);
		return mongoTemplate.updateFirst(query,update,ThumbsupBo.class );
	}
}
