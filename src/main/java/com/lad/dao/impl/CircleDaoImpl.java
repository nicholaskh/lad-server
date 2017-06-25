package com.lad.dao.impl;

import com.lad.bo.CircleBo;
import com.lad.dao.ICircleDao;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Repository("circleDao")
public class CircleDaoImpl implements ICircleDao {

	private String collectionName = "circle";

	@Autowired
	private MongoTemplate mongoTemplate;

	public CircleBo insert(CircleBo circleBo) {
		mongoTemplate.insert(circleBo);
		return circleBo;
	}

	public CircleBo selectById(String circleBoId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, CircleBo.class);
	}

	public List<CircleBo> selectByuserid(String userid) {
		Query query = new Query();
		query.addCriteria(new Criteria("users").is(userid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, CircleBo.class);
	}

	public WriteResult updateUsers(String circleBoId, HashSet<String> users) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("users", users);
		update.set("usernum", users.size());
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	public WriteResult updateUsersApply(String circleBoId,
			HashSet<String> usersApply) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("usersApply", usersApply);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	public WriteResult updateUsersRefuse(String circleBoId,
			HashSet<String> usersRefuse) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("usersRefuse", usersRefuse);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	public WriteResult updateHeadPicture(String circleBoId, String headPicture) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("headPicture", headPicture);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	public List<CircleBo> selectByType(String tag, String sub_tag,
			String category) {
		Query query = new Query();
		query.addCriteria(new Criteria("tag").is(tag));
		query.addCriteria(new Criteria("sub_tag").is(sub_tag));
		query.addCriteria(new Criteria("category").is(category));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, CircleBo.class);
	}

	public WriteResult updateNotes(String circleBoId, HashSet<String> notes) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("notes", notes);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	@Override
	public List<CircleBo> findByCreateid(String createid) {
		Query query = new Query();
		query.addCriteria(new Criteria("createuid").is(createid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, CircleBo.class);
	}

	@Override
	public WriteResult updateMaster(CircleBo circleBo) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBo.getId()));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		//创建者默认为群主，后续修改需要更改群主字段
		update.set("createuid", circleBo.getCreateuid());
		update.set("usernum", circleBo.getUsers().size());
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	public List<CircleBo> selectUsersPre() {
		Query query = new Query();
		query.limit(10);
		query.addCriteria(new Criteria("deleted").is(0));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC,"usernum")));
		return mongoTemplate.find(query, CircleBo.class);
	}

	public List<CircleBo> findMyCircles(String userid, String startId, boolean gt, int limit) {
		Query query = new Query();
		query.limit(limit);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		query.addCriteria(new Criteria("users").in(userid));
		query.addCriteria(new Criteria("deleted").is(0));
		if (!StringUtils.isEmpty(startId)) {
			if (gt) {
				query.addCriteria(new Criteria("_id").gt(startId));
			} else {
				query.addCriteria(new Criteria("_id").lt(startId));
			}
		}
		return mongoTemplate.find(query, CircleBo.class);
	}
}
