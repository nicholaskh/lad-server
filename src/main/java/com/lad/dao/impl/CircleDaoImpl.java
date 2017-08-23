package com.lad.dao.impl;

import com.lad.bo.CircleBo;
import com.lad.dao.ICircleDao;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

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

	public WriteResult updateUsersApply(String circleBoId,HashSet<String> usersApply) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("usersApply", usersApply);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	@Override
	public WriteResult updateApplyAgree(String circleBoId, HashSet<String> users, HashSet<String> usersApply) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("users", users);
		update.set("usernum", users.size());
		update.set("usersApply", usersApply);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	public WriteResult updateUsersRefuse(String circleBoId, HashSet<String> usersApply,
										 HashSet<String> usersRefuse) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("usersApply", usersApply);
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

	public WriteResult updateNotes(String circleBoId, long noteSize) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("noteSize", noteSize);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	@Override
	public List<CircleBo> findByCreateid(String createid) {
		Query query = new Query();
		query.addCriteria(new Criteria("createuid").is(createid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, CircleBo.class);
	}

	public List<CircleBo> findBykeyword(String keyword) {
		Query query = new Query();
		Pattern pattern = Pattern.compile("^.*"+keyword+".*$", Pattern.CASE_INSENSITIVE);
		query.addCriteria(new Criteria("name").regex(pattern));
		query.limit(10);
		return mongoTemplate.find(query, CircleBo.class);
	}

	@Override
	public WriteResult updateMaster(CircleBo circleBo) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBo.getId()));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("masters", circleBo.getMasters());
		update.set("updateTime", circleBo.getUpdateTime());
		update.set("updateuid", circleBo.getUpdateuid());
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	public WriteResult updateCreateUser(CircleBo circleBo) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBo.getId()));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		//创建者默认为群主，后续修改需要更改群主字段
		update.set("createuid", circleBo.getCreateuid());
		update.set("usernum", circleBo.getUsers().size());
		update.set("updateTime", circleBo.getUpdateTime());
		update.set("updateuid", circleBo.getUpdateuid());
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	public List<CircleBo> selectUsersPre(String userid) {
		Query query = new Query();
		query.addCriteria(new Criteria("deleted").is(0));
		query.addCriteria(new Criteria("users").nin(userid));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC,"usernum")));
		query.limit(10);
		return mongoTemplate.find(query, CircleBo.class);
	}

	public WriteResult uddateName(String userid, String name){
		Query query = new Query();
		query.addCriteria(new Criteria("deleted").is(0));
		query.addCriteria(new Criteria("_id").is(userid));
		Update update = new Update();
		//创建者默认为群主，后续修改需要更改群主字段
		update.set("name", name);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
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

	@Override
	public WriteResult updateTotal(String circleid, int total) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleid));
		Update update = new Update();
		//创建者默认为群主，后续修改需要更改群主字段
		update.set("total", total);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	@Override
	public List<CircleBo> findByType(String type, int level, String startId,  boolean gt,int limit) {
		Query query = new Query();
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		if (level == 1) {
			query.addCriteria(new Criteria("tag").is(type));
		} else if (level == 2) {
			query.addCriteria(new Criteria("sub_tag").is(type));
		}
		query.addCriteria(new Criteria("deleted").is(0));
		if (!StringUtils.isEmpty(startId)) {
			if (gt) {
				query.addCriteria(new Criteria("_id").gt(startId));
			} else {
				query.addCriteria(new Criteria("_id").lt(startId));
			}
		}
		query.limit(limit);
		return mongoTemplate.find(query, CircleBo.class);
	}

	public List<CircleBo> findNearCircle(double[] position, int maxDistance, int limit){
		Point point = new Point(position[0],position[1]);
		Query query = new Query();
		Criteria criteria1 = Criteria.where("position").nearSphere(point)
				.maxDistance(maxDistance/6378137.0);
		query.addCriteria(criteria1);
		query.limit(limit);
		return mongoTemplate.find(query, CircleBo.class);
	}
}
