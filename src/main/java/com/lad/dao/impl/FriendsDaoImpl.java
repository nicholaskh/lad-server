package com.lad.dao.impl;

import com.lad.bo.FriendsBo;
import com.lad.dao.IFriendsDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

@Repository("friendsDao")
public class FriendsDaoImpl implements IFriendsDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	public FriendsBo insert(FriendsBo friendsBo) {
		friendsBo.setUpdateTime(new Date());
		mongoTemplate.insert(friendsBo);
		return friendsBo;
	}

	public WriteResult updateBackname(String userid, String friendid, String backname) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("backname", backname);
		update.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public WriteResult updateTag(String userid, String friendid, List tag) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("tag", tag);
		update.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public WriteResult updatePhone(String userid, String friendid, HashSet<String> phones) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("phone", phones);
		update.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public WriteResult updateDescription(String userid, String friendid, String description) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("description", description);
		update.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public WriteResult updateVIP(String userid, String friendid, Integer VIP) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("VIP", VIP);
		update.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public WriteResult updateBlack(String userid, String friendid, Integer black) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("black", black);
		update.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public FriendsBo getFriendByIdAndVisitorId(String userid, String friendid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, FriendsBo.class);
	}
	
	public FriendsBo getFriendByIdAndVisitorIdAgree(String userid, String friendid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("apply").is(1));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, FriendsBo.class);
	}

	public List<FriendsBo> getFriendByUserid(String userid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("apply").is(1));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, FriendsBo.class);
	}

	public List<FriendsBo> getFriendByFriendid(String friendid) {
		Query query = new Query();
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("apply").is(1));
		query.addCriteria(new Criteria("deleted").is(0));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC,"_id")));
		return mongoTemplate.find(query, FriendsBo.class);
	}

	public WriteResult delete(String userid, String friendid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("deleted", 1);
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public WriteResult updateApply(String id, int apply, String chatroomid) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(id));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("apply", apply);
		if (StringUtils.isNotEmpty(chatroomid)){
			update.set("chatroomid", chatroomid);
		}
		update.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	public List<FriendsBo> getApplyFriendByuserid(String userid) {
		Query query = new Query();
		query.addCriteria(new Criteria("friendid").is(userid));
		query.addCriteria(new Criteria("apply").is(0));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, FriendsBo.class);
	}

	public FriendsBo get(String id) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(id));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, FriendsBo.class);
	}

	@Override
	public List<FriendsBo> searchCircleUsers(HashSet<String> circleUsers, String userid, String keywords) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").in(circleUsers));
		Pattern pattern = Pattern.compile("^.*"+keywords+".*$", Pattern.CASE_INSENSITIVE);
		query.addCriteria(new Criteria("backname").regex(pattern));
		return mongoTemplate.find(query, FriendsBo.class);
	}


	public List<FriendsBo> searchInviteCircleUsers(HashSet<String> circleUsers, String userid, String keywords) {
		Query query = new Query();
		Criteria criteria = new Criteria("userid").is(userid);
		criteria.and("friendid").nin(circleUsers);
		Pattern pattern = Pattern.compile("^.*"+keywords+".*$", Pattern.CASE_INSENSITIVE);
		Criteria backname = new Criteria("backname").regex(pattern);
		Criteria username = new Criteria("username").regex(pattern);
		criteria.orOperator(backname, username);
		return mongoTemplate.find(query, FriendsBo.class);
	}

	@Override
	public List<FriendsBo> getFriendByUserid(String userid, Date timestap) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("apply").is(Constant.ADD_AGREE));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC,"updateTime")));
		if (null != timestap) {
			query.addCriteria(new Criteria("updateTime").gt(timestap));
		}
		return mongoTemplate.find(query, FriendsBo.class);
	}

	@Override
	public WriteResult updateUsernameByFriend(String friendid, String username, String userHeadPic) {
		Query query = new Query();
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		Update update = new Update();
		if (StringUtils.isNotEmpty(userHeadPic)) {
			update.set("friendHeadPic", userHeadPic);
		}
		if (StringUtils.isNotEmpty(username)) {
			update.set("username", username);
		}
		update.set("updateTime", new Date());
		return mongoTemplate.updateMulti(query, update, FriendsBo.class);
	}

	@Override
	public List<FriendsBo> getFriendByInList(String userid, List<String> friendids) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("apply").is(Constant.ADD_AGREE));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		query.addCriteria(new Criteria("friendid").in(friendids));
		return mongoTemplate.find(query, FriendsBo.class);
	}

	@Override
	public WriteResult updateRelateStatus(String id, int relateStatus, boolean isParent) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(id));
		Update update = new Update();
		update.set("relateStatus", relateStatus);
		update.set("parent", isParent);
		update.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, FriendsBo.class);
	}

	@Override
	public List<FriendsBo> findAllApplyList(String userid, int page , int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("friendid").is(userid).and("deleted").is(Constant.ACTIVITY);
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.DESC,"updateTime", "_id"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, FriendsBo.class);
	}

	@Override
	public List<FriendsBo> findByStatus(String userid, int applyStatus, int relateStatus, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("userid").is(userid).and("deleted").is(Constant.ACTIVITY);
		criteria.and("apply").is(Constant.ADD_AGREE).and("relateStatus").is(3);
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.DESC,"_id"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, FriendsBo.class);
	}
}
