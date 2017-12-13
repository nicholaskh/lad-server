package com.lad.dao.impl;

import com.lad.bo.NoteBo;
import com.lad.dao.INoteDao;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Repository("noteDao")
public class NoteDaoImpl implements INoteDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	public NoteBo insert(NoteBo noteBo) {
		mongoTemplate.insert(noteBo);
		return noteBo;
	}

	public WriteResult updatePhoto(String noteId, LinkedList<String> photos) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(noteId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("photos", photos);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public WriteResult updateVisitCount(String noteId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(noteId).and("deleted").is(0));
		Update update = new Update();
		update.inc("visitcount", 1);
		update.inc("temp",1);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public WriteResult updateCommentCount(String noteId, long commentcount) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(new Criteria("_id").is(noteId).and("deleted").is(0));
		update.inc("commentcount", commentcount);
		update.inc("temp", commentcount);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public WriteResult updateTransCount(String noteId, long transcount) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(new Criteria("_id").is(noteId).and("deleted").is(0));
		update.inc("transcount", transcount);
		update.inc("temp", transcount);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}


	public WriteResult updateThumpsubCount(String noteId, long thumpsubcount) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(new Criteria("_id").is(noteId).and("deleted").is(0));
		update.inc("thumpsubcount", thumpsubcount);
		update.inc("temp",thumpsubcount);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public NoteBo selectById(String noteId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(noteId).and("deleted").is(0));
		return mongoTemplate.findOne(query, NoteBo.class);
	}

	public List<NoteBo> selectByVisit(String circleid) {
		Query query = new Query();
		query.addCriteria(new Criteria("circleId").is(circleid).and("deleted").is(0));
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


	public List<NoteBo> finyMyNoteByComment(String userid, String startId, boolean gt, int limit){
		Query query = new Query();
		query.addCriteria(new Criteria("createuid").is(userid));
		query.addCriteria(new Criteria("commentcount").gt(0));
		return findNotesByPage(query, startId, gt, limit);
	}
	
	public List<NoteBo> finyByCreateTime(String circleid, String startId, boolean gt, int limit){
		Query query = new Query();
		query.addCriteria(new Criteria("circleId").is(circleid));
		return findNotesByPage(query, startId, gt, limit);
	}

	public long finyNotesNum(String circleid){
		Query query = new Query();
		query.addCriteria(new Criteria("circleId").is(circleid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.count(query, NoteBo.class);
	}

	public List<NoteBo> selectHotNotes(String circleid){
		Query query = new Query();
		query.addCriteria(new Criteria("createTime").gte(CommonUtil.getBeforeWeekDate()));
		query.addCriteria(new Criteria("circleId").is(circleid));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "temp")));
		query.limit(10);
		return mongoTemplate.find(query, NoteBo.class);
	}

	public List<NoteBo> selectTopNotes(String circleid){
		Query query = new Query();
		query.addCriteria(new Criteria("circleId").is(circleid));
		query.addCriteria(new Criteria("deleted").is(0));
		query.addCriteria(new Criteria("content").regex("^[\\s\\S]{200,}"));
		query.addCriteria(new Criteria("photos.2").exists(true));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
		query.limit(2);
		return mongoTemplate.find(query, NoteBo.class);
	}

	public WriteResult deleteNote(String noteId, String deleteuid) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(noteId));
		Update update = new Update();
		update.set("deleted", 1);
		//更新删除人信息
		update.set("updateuid", deleteuid);
		update.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public List<NoteBo> selectMyNotes(String userid, String startId, boolean gt, int limit){
		Query query = new Query();
		query.addCriteria(new Criteria("createuid").is(userid));
		return findNotesByPage(query, startId, gt, limit);
	}


	public int selectPeopleNum(String circleid){
		Criteria criteria = new Criteria("circleId").is(circleid);
		criteria.and("deleted").is(Constant.ACTIVITY);
		AggregationOperation match = Aggregation.match(criteria);
		GroupOperation group = Aggregation.group("circleId").sum("temp").as("totals");

		Aggregation aggregation = Aggregation.newAggregation(match, group);

		AggregationResults<BasicDBObject> results = mongoTemplate.aggregate(aggregation, "note", BasicDBObject.class);

		List<BasicDBObject> dbObjects = results.getMappedResults();
		for (BasicDBObject dbObject : dbObjects) {
			return Integer.valueOf(dbObject.get("totals").toString());
		}
		return 0;
	}

	/**
	 * 按照主键分页查询
	 * @param query
	 * @param startId
	 * @param gt
	 * @param limit
	 * @return
	 */
	private List<NoteBo> findNotesByPage(Query query, String startId, boolean gt, int limit){
		query.addCriteria(new Criteria("deleted").is(0));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
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

	public WriteResult updateTemp(String id, long number){
		Query query = new Query();
		Update update = new Update();
		update.inc("temp",number);
		query.addCriteria(new Criteria("_id").is(id));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.updateFirst(query, update,NoteBo.class);
	}

	@Override
	public List<NoteBo> selectCircleNotes(String circleId, String startId, int limit) {
		Query query = new Query();
		query.addCriteria(new Criteria("circleId").is(circleId));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "top")));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "essence")));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		if (!StringUtils.isEmpty(startId)) {
			query.addCriteria(new Criteria("_id").gt(startId));
		}
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	@Override
	public WriteResult updateToporEssence(String noteid, int status, int type) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(noteid));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		Update update = new Update();
		if (type == Constant.NOTE_TOP) {
			update.set("top",status);
		} else if (type == Constant.NOTE_JIAJING) {
			update.set("essence",status);
		}
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	@Override
	public List<NoteBo> findByTopEssence(String circleid, int type, String startId, int limit) {
		Query query = new Query();
		query.addCriteria(new Criteria("circleId").is(circleid));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		if (type == Constant.NOTE_TOP) {
			query.addCriteria(new Criteria("top").is(1));
		} else if (type == Constant.NOTE_JIAJING) {
			query.addCriteria(new Criteria("essence").is(1));
		}
		if (!StringUtils.isEmpty(startId)) {
			query.addCriteria(new Criteria("_id").gt(startId));
		}
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	@Override
	public List<NoteBo> findByTopAndEssence(String circleid, String startId, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("circleId").is(circleid).and("deleted").is(Constant.ACTIVITY);
		Criteria top = new Criteria("top").is(1);
		Criteria essence  = new Criteria("essence").is(1);
		query.addCriteria(criteria.orOperator(top, essence));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		if (!StringUtils.isEmpty(startId)) {
			query.addCriteria(new Criteria("_id").gt(startId));
		}
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	@Override
	public WriteResult updateCollectCount(String noteId, int num) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(noteId).and("deleted").is(0));
		Update update = new Update();
		update.inc("collectcount", 1);
		update.inc("temp",1);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}
}
