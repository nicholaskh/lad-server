package com.lad.dao.impl;

import com.lad.bo.NoteBo;
import com.lad.dao.INoteDao;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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
		update.inc("temp", 0.05);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public WriteResult updateCommentCount(String noteId, long commentcount) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(new Criteria("_id").is(noteId).and("deleted").is(0));
		update.inc("commentcount", commentcount);
		update.inc("temp", commentcount * 0.35);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public WriteResult updateTransCount(String noteId, long transcount) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(new Criteria("_id").is(noteId).and("deleted").is(0));
		update.inc("transcount", transcount);
		update.inc("temp", transcount * 0.45);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public WriteResult updateThumpsubCount(String noteId, long thumpsubcount) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(new Criteria("_id").is(noteId).and("deleted").is(0));
		update.inc("thumpsubcount", thumpsubcount);
		update.inc("temp", thumpsubcount * 0.15);
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
		query.addCriteria(new Criteria("deleted").is(0));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "commentcount")));
		return mongoTemplate.find(query, NoteBo.class);
	}

	public List<NoteBo> finyMyNoteByComment(String userid, int page, int limit) {
		Query query = new Query();
		query.addCriteria(new Criteria("createuid").is(userid).and("commentcount").gt(0));
		return findNotesByPage(query, page, limit);
	}

	public List<NoteBo> finyByCreateTime(String circleid, int page, int limit) {
		Query query = new Query();
		query.addCriteria(new Criteria("circleId").is(circleid));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
		return findNotesByPage(query, page, limit);
	}

	public long findNotesNum(String circleid) {
		Query query = new Query();
		query.addCriteria(new Criteria("circleId").is(circleid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.count(query, NoteBo.class);
	}

	public List<NoteBo> selectHotNotes(String circleid) {
		Query query = new Query();
		query.addCriteria(new Criteria("circleId").is(circleid));
		query.addCriteria(new Criteria("createTime").gte(CommonUtil.getBeforeWeekDate()));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "temp")));
		query.limit(10);
		return mongoTemplate.find(query, NoteBo.class);
	}

	public List<NoteBo> selectTopNotes(String circleid) {
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
		// 更新删除人信息
		update.set("updateuid", deleteuid);
		update.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	public List<NoteBo> selectMyNotes(String userid, int page, int limit) {
		Query query = new Query();
		query.addCriteria(new Criteria("createuid").is(userid));
		query.with(new Sort(Sort.Direction.DESC, "createTime"));
		return findNotesByPage(query, page, limit);
	}

	public int selectPeopleNum(String circleid) {
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
	 * 
	 * @param query
	 * @param limit
	 * @return
	 */
	private List<NoteBo> findNotesByPage(Query query, int page, int limit) {
		query.addCriteria(new Criteria("deleted").is(0));
		// query.with(new Sort(Sort.Direction.DESC, "_id"));
		query.with(new Sort(Sort.Direction.DESC, "createTime", "temp"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	public WriteResult updateTemp(String id, long number) {
		Query query = new Query();
		Update update = new Update();
		update.inc("temp", number);
		query.addCriteria(new Criteria("_id").is(id));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	@Override
	public List<NoteBo> selectCircleNotes(String circleId, int page, int limit) {
		Query query = new Query();
		query.addCriteria(new Criteria("circleId").is(circleId));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		// query.with(new Sort(Sort.Direction.DESC, "_id"));
		query.with(new Sort(Sort.Direction.DESC, "createTime", "temp"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
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
			update.set("top", status);
		} else if (type == Constant.NOTE_JIAJING) {
			update.set("essence", status);
		}
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	@Override
	public List<NoteBo> findByTopEssence(String circleid, int type, int page, int limit) {
		Query query = new Query();
		query.addCriteria(new Criteria("circleId").is(circleid));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		if (type == Constant.NOTE_TOP) {
			query.addCriteria(new Criteria("top").is(1));
		} else if (type == Constant.NOTE_JIAJING) {
			query.addCriteria(new Criteria("essence").is(1));
		}
		// query.with(new Sort(Sort.Direction.DESC, "_id"));
		query.with(new Sort(Sort.Direction.DESC, "createTime", "temp"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	@Override
	public List<NoteBo> findByTopAndEssence(String circleid, int status, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("circleId").is(circleid).and("deleted").is(Constant.ACTIVITY);
		Criteria top = new Criteria("top").is(status);
		Criteria essence = new Criteria("essence").is(status);
		query.addCriteria(criteria.orOperator(top, essence));
		// query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		query.with(new Sort(Sort.Direction.DESC, "createTime", "temp"));
		query.skip(((page < 1 ? 1 : page) - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	public List<NoteBo> findNotTopAndEssence(String circleid, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("circleId").is(circleid).and("top").is(0).and("essence").is(0).and("deleted")
				.is(Constant.ACTIVITY);
		query.addCriteria(criteria);
		// query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		query.with(new Sort(Sort.Direction.DESC, "createTime", "temp"));
		query.skip(((page < 1 ? 1 : page) - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	@Override
	public List<NoteBo> findByDate(String circleid, Date date, int type, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("circleId").is(circleid).and("deleted").is(Constant.ACTIVITY);
		query.addCriteria(criteria);
		Criteria cri;
		if (type == 1) {
			cri = new Criteria("top").is(1);
		} else if (type == 2) {
			cri = new Criteria("essence").is(1);
		} else if (type == 3) {
			cri = new Criteria("top").is(1).and("essence").is(1);
		} else {
			cri = new Criteria("top").is(0).and("essence").is(0);
		}
		query.addCriteria(cri);
		Date startTime = CommonUtil.getZeroDate(date);
		Date endTime = CommonUtil.getLastDate(date);
		query.addCriteria(new Criteria("createTime").gte(startTime).lte(endTime));
		query.with(new Sort(Sort.Direction.DESC, "_id"));
		query.with(new Sort(Sort.Direction.DESC, "createTime", "temp"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	@Override
	public WriteResult updateCollectCount(String noteId, int num) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(noteId).and("deleted").is(0));
		Update update = new Update();
		update.inc("collectcount", 1);
		return mongoTemplate.updateFirst(query, update, NoteBo.class);
	}

	@Override
	public List<NoteBo> selectByTitle(String circleid, String title, String type, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("circleId").is(circleid).and("deleted").is(Constant.ACTIVITY);
		if (!StringUtils.isEmpty(title)) {
			Pattern pattern = Pattern.compile("^.*" + title + ".*$", Pattern.CASE_INSENSITIVE);
			criteria.and("subject").regex(pattern);
		}
		if (!StringUtils.isEmpty(type)) {
			criteria.and("type").is(type);
		}
		query.addCriteria(criteria);
		// query.with(new Sort(Sort.Direction.DESC, "_id"));
		query.with(new Sort(Sort.Direction.DESC, "createTime", "temp"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	@Override
	public List<NoteBo> selectByUserid(String circleid, String userid, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("deleted").is(Constant.ACTIVITY);
		if (!StringUtils.isEmpty(circleid)) {
			criteria.and("circleId").is(circleid);
		}
		criteria.and("createuid").is(userid);
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.DESC, "createTime"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	@Override
	public List<NoteBo> selectByCreatTime(String circleid, Date startTime, Date endTime, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("circleId").is(circleid).and("deleted").is(Constant.ACTIVITY);
		criteria.and("createTime").gte(startTime).lte(endTime);
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.DESC, "createTime", "temp"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	@Override
	public List<NoteBo> findTypeNotes(String type, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("deleted").is(Constant.ACTIVITY);
		if (!StringUtils.isEmpty(type)) {
			criteria.and("type").is(type);
		}
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.DESC, "createTime", "temp"));
		// query.with(new Sort(Sort.Direction.DESC, "_id"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	@Override
	public List<NoteBo> selectByNoteType(String circleid, String type, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("circleId").is(circleid).and("deleted").is(Constant.ACTIVITY);
		criteria.and("type").is(type);
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.DESC, "_id"));
		query.with(new Sort(Sort.Direction.DESC, "createTime", "temp"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	public GeoResults<NoteBo> findNearNote(double[] position, int maxDistance, int limit, int page) {
		Point point = new Point(position[0], position[1]);
		NearQuery near = NearQuery.near(point);
		Distance distance = new Distance(maxDistance / 1000, Metrics.KILOMETERS);
		near.maxDistance(distance);
		Query query = new Query();
		int skip = page - 1 < 0 ? 0 : page - 1;
		query.skip(skip * limit);
		query.limit(limit);
		near.query(query);
		return mongoTemplate.geoNear(near, NoteBo.class);
	}

	@Override
	public List<NoteBo> dayNewNotes(List<String> circleids, int page, int limit) {

		Query query = new Query();
		Criteria criteria = new Criteria("deleted").is(Constant.ACTIVITY);
		if (!CommonUtil.isEmpty(circleids)) {
			criteria.and("circleId").in(circleids);
		}
		query.with(new Sort(Sort.Direction.DESC, "createTime", "temp"));
		query.addCriteria(criteria);
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}

	@Override
	public List<NoteBo> dayHotNotes(int page, int limit) {
		return dayHotNotes(null, page, limit);
	}

	@Override
	public List<NoteBo> dayHotNotes(Set<String> circleSet, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("deleted").is(Constant.ACTIVITY);
		if (!StringUtils.isEmpty(circleSet)) {
			criteria.and("circleId").in(circleSet);
		}
		query.with(new Sort(Sort.Direction.DESC, "temp", "createTime"));
		query.addCriteria(criteria);
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, NoteBo.class);
	}
}
