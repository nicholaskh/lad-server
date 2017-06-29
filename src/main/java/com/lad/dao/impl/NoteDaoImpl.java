package com.lad.dao.impl;

import com.lad.bo.NoteBo;
import com.lad.dao.INoteDao;
import com.lad.util.CommonUtil;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
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

	public WriteResult updateVisitCount(String noteId) {
		Query query = new Query();
		Update update = new Update();
		update.inc("visitcount", 1);
		query.addCriteria(new Criteria("_id").is(noteId));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public WriteResult updateCommentCount(String noteId, long commentcount) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(new Criteria("_id").is(noteId));
		query.addCriteria(new Criteria("deleted").is(0));
		update.set("commentcount", commentcount);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public WriteResult updateTransCount(String noteId, long transcount) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(new Criteria("_id").is(noteId));
		query.addCriteria(new Criteria("deleted").is(0));
		update.set("transcount", transcount);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}


	public WriteResult updateThumpsubCount(String noteId, long thumpsubcount) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(new Criteria("_id").is(noteId));
		query.addCriteria(new Criteria("deleted").is(0));
		update.inc("thumpsubcount", 1);
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
		query.addCriteria(new Criteria("circleId").is(circleid));
		query.addCriteria(new Criteria("deleted").is(0));
		query.addCriteria(new Criteria("content").regex("^[\\s\\S]{100,}"));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "visitcount")));
		query.limit(10);
		return mongoTemplate.find(query, NoteBo.class);
	}

	public List<NoteBo> selectByComment(String noteId) {
		Query query = new Query();
		query.limit(4);
		query.addCriteria(new Criteria("circleId").is(noteId));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "commentcount")));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, NoteBo.class);
	}

	
	public List<NoteBo> finyByCreateTime(String circleid, String startId, boolean gt, int limit){
		Query query = new Query();
		query.addCriteria(new Criteria("circleId").is(circleid));
		query.addCriteria(new Criteria("deleted").is(0));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
		if (!StringUtils.isEmpty(startId)) {
			if (gt) {
				query.addCriteria(new Criteria("_id").gt(startId));
			} else {
				query.addCriteria(new Criteria("_id").lt(startId));
			}
		}
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	public List<NoteBo> selectHotNotes(String circleid){

		Criteria criteria = new Criteria("createTime").gt(CommonUtil.getBeforeWeekDate());
		criteria.and("circleId").is(circleid);
		AggregationOperation match = Aggregation.match(criteria);

		String[] fields = new String[]{"subject", "_id", "visitcount", "transcount",
				"commentcount", "thumpsubcount", "result", "createTime"};

		AggregationOperation project = Aggregation.project(fields).and("temp")
				.plus("visitcount").plus("transcount").plus("commentcount").plus("thumpsubcount").as("result");

		Aggregation aggregation = Aggregation.newAggregation(match,
				project, Aggregation.sort(Sort.Direction.DESC, "result"),
				Aggregation.limit(10));
		AggregationResults<NoteBo> results = mongoTemplate.aggregate(aggregation, "note", NoteBo.class);
		return results.getMappedResults();
	}

}
