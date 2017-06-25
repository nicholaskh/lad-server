package com.lad.dao.impl;

import com.lad.bo.NoteBo;
import com.lad.dao.INoteDao;
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

@Repository("noteDao")
public class NoteDaoImpl implements INoteDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	public NoteBo insert(NoteBo noteBo) {
		mongoTemplate.insert(noteBo);
		return noteBo;
	}

	public WriteResult updatePhoto(String noteId, String photo) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(noteId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("photo", photo);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public WriteResult updateVisit(String noteId, long visitcount) {
		Query query = new Query();
		Update update = new Update();
		update.set("visitcount", visitcount);
		query.addCriteria(new Criteria("_id").is(noteId));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public WriteResult updateCommemt(String noteId, long commentcount) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(new Criteria("_id").is(noteId));
		query.addCriteria(new Criteria("deleted").is(0));
		update.set("commentcount", commentcount);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public NoteBo selectById(String noteId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(noteId));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, NoteBo.class);
	}

	public List<NoteBo> selectByVisit(String circleid) {
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "visitcount")));
		query.addCriteria(new Criteria("content").regex("/^.{100,}$/"));
		query.addCriteria(new Criteria("circleId").is(circleid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, NoteBo.class);
	}

	public List<NoteBo> selectByComment(String noteId) {
		Query query = new Query();
		query.limit(4);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "commentcount")));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, NoteBo.class);
	}



	public List<NoteBo> finyByCreateTime(String circleid, String startId, boolean gt, int limit){
		Query query = new Query();
		query.limit(limit);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
		query.addCriteria(new Criteria("circleId").is(circleid));
		query.addCriteria(new Criteria("deleted").is(0));
		if (!StringUtils.isEmpty(startId)) {
			if (gt) {
				query.addCriteria(new Criteria("_id").gt(startId));
			} else {
				query.addCriteria(new Criteria("_id").lt(startId));
			}
		}
		return mongoTemplate.find(query, NoteBo.class);
	}

}
