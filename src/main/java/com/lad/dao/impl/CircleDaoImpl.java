package com.lad.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.CircleAddBo;
import com.lad.bo.CircleBo;
import com.lad.dao.ICircleDao;
import com.lad.util.Constant;
import com.mongodb.CommandResult;
import com.mongodb.WriteResult;

@Repository("circleDao")
public class CircleDaoImpl implements ICircleDao {

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

	public WriteResult updateUsersApply(String circleBoId, HashSet<String> usersApply) {
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

	public WriteResult updateUsersRefuse(String circleBoId, HashSet<String> usersApply, HashSet<String> usersRefuse) {
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

	public List<CircleBo> selectByType(String tag, String sub_tag, String category) {
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
		update.inc("noteSize", noteSize);
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
	public long findCreateCricles(String createuid) {
		Query query = new Query();
		query.addCriteria(new Criteria("createuid").is(createuid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.count(query, CircleBo.class);
	}

	public List<CircleBo> findBykeyword(String keyword, String city, int page, int limit) {

		Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
		Criteria cr = new Criteria();
		Criteria name = new Criteria("name").regex(pattern);
		Criteria tag = new Criteria("tag").is(keyword);
		Criteria sub_tag = new Criteria("sub_tag").is(keyword);
		cr.orOperator(name, tag, sub_tag);
		if (StringUtils.isNotEmpty(city)) {
			Criteria cr2 = new Criteria();
			Criteria pro = new Criteria("province").is(city);
			Criteria ci = new Criteria("city").is(city);
			Criteria dist = new Criteria("district").is(city);
			cr.andOperator(cr2.orOperator(pro, ci, dist));
		}
		Query query = new Query(cr);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "hotNum")));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
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
		// 创建者默认为群主，后续修改需要更改群主字段
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
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "usernum")));
		query.limit(10);
		return mongoTemplate.find(query, CircleBo.class);
	}

	public List<CircleBo> findMyCircles(String userid, int page, int limit) {
		Query query = new Query();
		query.addCriteria(new Criteria("users").in(userid).and("deleted").is(0));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, CircleBo.class);
	}

	@Override
	public WriteResult updateTotal(String circleid, int total) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleid));
		Update update = new Update();
		update.set("total", total);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	@Override
	public List<CircleBo> findByType(String tag, String sub_tag, String city, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("deleted").is(Constant.ACTIVITY);
		if (StringUtils.isNotEmpty(tag)) {
			criteria.and("tag").is(tag);
		}
		if (StringUtils.isNotEmpty(sub_tag)) {
			criteria.and("sub_tag").is(sub_tag);
		}
		if (StringUtils.isNotEmpty(city)) {
			Criteria pro = new Criteria("province").is(city);
			Criteria ci = new Criteria("city").is(city);
			Criteria dist = new Criteria("district").is(city);
			criteria.orOperator(pro, ci, dist);
		}
		query.addCriteria(criteria);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "hotNum")));
		if (page <= 0) {
			page = 1;
		}
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, CircleBo.class);
	}

	public GeoResults<CircleBo> findNearCircle(String userid, double[] position, int maxDistance, int page, int limit) {
		Point point = new Point(position[0], position[1]);
		NearQuery near = NearQuery.near(point);
		Distance distance = new Distance(maxDistance / 1000, Metrics.KILOMETERS);
		near.maxDistance(distance);
		Query query = new Query();
		if (StringUtils.isNotEmpty(userid)) {
			query.addCriteria(new Criteria("users").nin(userid));
		}
		/*query.with(new Sort(new Order(Direction.DESC,"_id")));*/
		int skip = (page-1)*limit;
		skip = (page-1)*limit<0?0:(page-1)*limit;
		query.skip(skip);
		query.limit(limit);
		query.with(new Sort(new Order(Direction.DESC, "createTime")));
		near.query(query);
		return mongoTemplate.geoNear(near, CircleBo.class);
	}

	public List<CircleBo> selectUsersLike(String userid, String city, double[] position, int minDistance) {
		Query query = new Query();
		query.addCriteria(new Criteria("deleted").is(0));
		if (StringUtils.isNotEmpty(userid)) {
			query.addCriteria(new Criteria("users").nin(userid));
		}
		Point point = new Point(position[0], position[1]);
		Criteria criteria = Criteria.where("position").nearSphere(point).minDistance(minDistance / 6378137.0);
		if (StringUtils.isNotEmpty(city)) {
			Criteria cr = new Criteria();
			Criteria pro = new Criteria("province").is(city);
			Criteria ci = new Criteria("city").is(city);
			Criteria dist = new Criteria("district").is(city);
			criteria.orOperator(pro, ci, dist);
		}
		query.addCriteria(criteria);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "usernum")));
		query.limit(10);
		return mongoTemplate.find(query, CircleBo.class);
	}

	public List<CircleBo> selectByCity(String city, int page, int limit) {
		Criteria cr = new Criteria();
		Criteria pro = new Criteria("province").is(city);
		Criteria ci = new Criteria("city").is(city);
		Criteria dist = new Criteria("district").is(city);
		Query query = new Query(cr.orOperator(pro, ci, dist));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "hotNum")));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, CircleBo.class);
	}

	@Override
	public WriteResult updateNotice(CircleBo circleBo) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleBo.getId()));
		Update update = new Update();
		update.set("notice", circleBo.getNotice());
		update.set("noticeTitle", circleBo.getNoticeTitle());
		update.set("noticeTime", circleBo.getNoticeTime());
		update.set("noticeUserid", circleBo.getNoticeUserid());
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	@Override
	public WriteResult updateCircleName(String circleid, String name) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("name", name);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	@Override
	public WriteResult updateOpen(String circleid, boolean isOpen) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleid));
		Update update = new Update();
		update.set("isOpen", isOpen);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	@Override
	public WriteResult updateisVerify(String circleid, boolean isVerify) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleid));
		Update update = new Update();
		update.set("isVerify", isVerify);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	@Override
	public WriteResult updateCircleHot(String circleid, int num, int type) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleid));
		Update update = new Update();
		switch (type) {
		// 圈子阅读数量
		case Constant.CIRCLE_VISIT:
			update.inc("visitNum", num);
			update.inc("total", num);
			break;
		// 圈子评论数量
		case Constant.CIRCLE_COMMENT:
			update.inc("commentNum", num);
			update.inc("total", num);
			break;
		// 圈子转发数量
		case Constant.CIRCLE_TRANS:
			update.inc("transmitNum", num);
			update.inc("total", num);
			update.inc("hotNum", num * 0.2);
			break;
		// 圈子点赞数量
		case Constant.CIRCLE_THUMP:
			update.inc("thumpNum", num);
			update.inc("total", num);
			update.inc("hotNum", num * 0.1);
			break;
		// 圈子阅读数量包含帖子的阅读
		case Constant.CIRCLE_NOTE_VISIT:
			update.inc("visitNum", num);
			update.inc("total", num);
			update.inc("hotNum", num * 0.05);
			break;
		// 圈子内帖子数量
		case Constant.CIRCLE_NOTE:
			update.inc("noteNum", num);
			update.inc("hotNum", num * 0.25);
			break;
		// 圈子聚会数量
		case Constant.CIRCLE_PARTY_VISIT:
			update.inc("partyVisit", num);
			update.inc("hotNum", num);
			break;
		// 圈子聚会点赞数量
		case Constant.CIRCLE_PARTY_THUMP:
			update.inc("partyThump", num);
			update.inc("hotNum", num * 0.4);
			break;
		// 圈子聚会分享数量
		case Constant.CIRCLE_PARTY_SHARE:
			update.inc("partyShare", num);
			update.inc("hotNum", num * 0.2);
			break;
		default:
			update.inc("hotNum", 0);
			break;
		}
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	@Override
	public List<CircleBo> findByCitys(String province, String city, String district, int page, int limit) {
		Query query = new Query();
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		if (StringUtils.isNotEmpty(province)) {
			query.addCriteria(new Criteria("province").is(province));
		}
		if (StringUtils.isNotEmpty(city)) {
			query.addCriteria(new Criteria("city").is(city));
		}
		if (StringUtils.isNotEmpty(district)) {
			query.addCriteria(new Criteria("district").is(district));
		}
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "hotNum")));
		if (page <= 0) {
			page = 1;
		}
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, CircleBo.class);
	}

	@Override
	public List<CircleBo> selectUsersLike(String userid, String tag, String subTag) {
		return null;
	}

	@Override
	public List<CircleBo> findRelatedCircles(String circleid, String tag, String sub_tag, int page, int limit) {
		Query query = new Query();
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		query.addCriteria(new Criteria("_id").ne(circleid));
		if (StringUtils.isNotEmpty(tag)) {
			query.addCriteria(new Criteria("tag").is(tag));
		}
		if (StringUtils.isNotEmpty(sub_tag)) {
			query.addCriteria(new Criteria("sub_tag").is(sub_tag));
		}
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "hotNum")));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, CircleBo.class);
	}

	@Override
	public List<CircleBo> findByCityName(String cityName, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		Criteria province = new Criteria("province").is(cityName);
		Criteria city = new Criteria("city").is(cityName);
		Criteria district = new Criteria("district").is(cityName);
		criteria.orOperator(province, city, district);
		query.addCriteria(criteria);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "hotNum")));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, CircleBo.class);
	}

	@Override
	public CircleBo findByTagAndName(String name, String tag, String sub_tag) {
		Query query = new Query();
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		query.addCriteria(new Criteria("name").is(name));
		query.addCriteria(new Criteria("tag").is(tag));
		query.addCriteria(new Criteria("sub_tag").is(sub_tag));
		return mongoTemplate.findOne(query, CircleBo.class);
	}

	@Override
	public CircleBo selectByIdIgnoreDel(String circleid) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleid));
		return mongoTemplate.findOne(query, CircleBo.class);
	}

	@Override
	public List<CircleBo> findCirclesInList(List<String> circleids) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").in(circleids).and("deleted").is(Constant.ACTIVITY));
		return mongoTemplate.find(query, CircleBo.class);
	}

	@Override
	public WriteResult updateTakeShow(String circleid, boolean takeShow) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(circleid));
		Update update = new Update();
		update.set("takeShow", takeShow);
		return mongoTemplate.updateFirst(query, update, CircleBo.class);
	}

	@Override
	public List<CircleBo> selectByRegexName(String showType) {
		Pattern pattern = Pattern.compile("^.*" + showType + ".*$", Pattern.CASE_INSENSITIVE);
		Query query = new Query(
				new Criteria("takeShow").is(true).and("deleted").is(Constant.ACTIVITY).and("name").regex(pattern));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		return mongoTemplate.find(query, CircleBo.class);
	}

	@Override
	public List<CircleBo> findHotCircles(String city, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("deleted").is(Constant.ACTIVITY);
		if (!org.springframework.util.StringUtils.isEmpty(city)) {
			criteria.and("city").is(city);
		}
		query.addCriteria(criteria);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "hotNum")));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, CircleBo.class);
	}

	@Override
	public List<CircleBo> findHotCircles(int page, int limit) {
		return findHotCircles(null, page, limit);
	}

	@Override
	public List<CircleAddBo> findApplyCircleAddByUid(String uid) {
		Query query = new Query(Criteria.where("userid").is(uid).and("status").is(1));
		query.with(new Sort(Direction.DESC, "_id"));
		return mongoTemplate.find(query, CircleAddBo.class);
	}

	@Override
	public CommandResult findNearCircleByCommond(String userid, double[] position, int i, int page, int limit) {
		String jsonCommand = "{geoNear:\"circle\",near:{type:\"Point\",coordinates:["+position[0]+","+position[1]+"]},spherical:true,minDistance:0,maxDistance:"+i+"}";
		CommandResult executeCommand = mongoTemplate.executeCommand(jsonCommand);
		return executeCommand;
	}
}
