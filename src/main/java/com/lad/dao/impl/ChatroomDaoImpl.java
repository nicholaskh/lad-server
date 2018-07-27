package com.lad.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.ChatroomBo;
import com.lad.controller.ChatroomController;
import com.lad.dao.IChatroomDao;
import com.lad.util.Constant;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository("chatroomDao")
public class ChatroomDaoImpl implements IChatroomDao {

	private String collectionName = "chatroom";

	private boolean isIndex = false;

	@Autowired
	private MongoTemplate mongoTemplate;

	public ChatroomBo insert(ChatroomBo chatroom) {
		if (!isIndex) {
			DBCollection collection = mongoTemplate.getCollection(collectionName);
			if (!hasIndex(collection, "position")) {
				collection.createIndex(new BasicDBObject("position", "2dsphere"), "position");
			}
		}
		mongoTemplate.insert(chatroom);
		return chatroom;
	}

	public ChatroomBo updateName(ChatroomBo chatroom) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroom.getId()));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("name", chatroom.getName());
		mongoTemplate.updateFirst(query, update, ChatroomBo.class);
		return chatroom;
	}

	public ChatroomBo get(String chatroomId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroomId));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, ChatroomBo.class);
	}

	public ChatroomBo updateUsers(ChatroomBo chatroom) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroom.getId()));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("users", chatroom.getUsers());
		mongoTemplate.updateFirst(query, update, ChatroomBo.class);
		return chatroom;
	}

	public WriteResult delete(String chatroomId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroomId));
		Update update = new Update();
		update.set("deleted", 1);
		return mongoTemplate.updateFirst(query, update, ChatroomBo.class);
	}

	public WriteResult remove(String chatroomId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroomId));
		return mongoTemplate.remove(query, ChatroomBo.class);
	}

	public ChatroomBo selectByUserIdAndFriendid(String userid, String friendid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("friendid").is(friendid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, ChatroomBo.class);
	}

	public ChatroomBo selectBySeq(int seq) {
		Query query = new Query();
		query.addCriteria(new Criteria("seq").is(seq));
		query.addCriteria(new Criteria("type").is(3));
		query.addCriteria(new Criteria("deleted").is(0));
		query.addCriteria(new Criteria("expire").is(1));
		return mongoTemplate.findOne(query, ChatroomBo.class);
	}

	public WriteResult setSeqExpire(int seq) {
		Query query = new Query();
		query.addCriteria(new Criteria("seq").is(seq));
		query.addCriteria(new Criteria("type").is(3));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("expire", 0);
		return mongoTemplate.updateFirst(query, update, ChatroomBo.class);
	}

	public boolean withInRange(String chatroomId, double[] position, int radius) {
		//主键筛选条件
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(new ObjectId(chatroomId)));
		//位置索引查询
		Point location = new Point(position[0], position[1]);
		NearQuery nearQuery = NearQuery.near(location)
				.maxDistance(new Distance(radius/6378137.0)).spherical(true).query(query);
		Aggregation aggregation = Aggregation.newAggregation(Aggregation.geoNear(nearQuery,"position"));
		AggregationResults<ChatroomBo> results = mongoTemplate.aggregate(aggregation,collectionName,ChatroomBo.class);
		List<ChatroomBo> list = results.getMappedResults();
		return list != null && !list.isEmpty();
	}

	/**
	 * 查看是否有索引，没有则创建
	 * @param collection
	 * @param indexName
	 */
	private boolean hasIndex(DBCollection collection, String indexName){
		List<DBObject> indexList = collection.getIndexInfo();
		if(null!=indexList){
			for(DBObject o:indexList){
				String name = (String) o.get("name");
				if (StringUtils.isNotEmpty(name) && name.equals(indexName)) {
					return (isIndex = true);
				}
			}
		}
		return false;
	}


	@Override
	public ChatroomBo selectBySeqInTen(int seq, double[] position, int radius) {
		org.slf4j.Logger logger2 = org.slf4j.LoggerFactory.getLogger(ChatroomController.class);
		logger2.error("==========数据库层面传入的定位信息:"+Arrays.toString(position)+"===========");
		//主键筛选条件
		Query query = new Query();
		query.addCriteria(new Criteria("seq").is(seq));
		query.addCriteria(new Criteria("type").is(Constant.ROOM_FACE_2_FACE));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		query.addCriteria(new Criteria("expire").is(1));
		long before = System.currentTimeMillis() - 10 * 60 *1000;
		Date beforeTime = new Date(before);
		query.addCriteria(new Criteria("createTime").gte(beforeTime));
		//位置索引查询
		Point location = new Point(position[0], position[1]);
		Criteria criteria1 = Criteria.where("position").nearSphere(location)
				.maxDistance(radius/6378137.0);
		query.addCriteria(criteria1);
		logger2.error("==========query语句:"+query.toString()+"===========");
		return mongoTemplate.findOne(query, ChatroomBo.class);
	}

	@Override
	public WriteResult updateMaster(String chatroomId, String masterid) {
		//主键筛选条件
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroomId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("master", masterid);
		return mongoTemplate.updateFirst(query, update, ChatroomBo.class);
	}

	@Override
	public WriteResult updateName(String chatroomId, String name) {
		//主键筛选条件
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroomId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("name", name);
		return mongoTemplate.updateFirst(query, update, ChatroomBo.class);
	}

	public WriteResult updateName(String chatRoomId, String name, boolean isNameSet){
		//主键筛选条件
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatRoomId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("name", name);
		update.set("isNameSet", isNameSet);
		return mongoTemplate.updateFirst(query, update, ChatroomBo.class);
	}

	@Override
	public WriteResult updateNameAndUsers(String chatRoomId, String name, boolean isNameSet, LinkedHashSet<String> users) {
		//主键筛选条件
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatRoomId).and("deleted").is(Constant.ACTIVITY));
		Update update = new Update();
		update.set("name", name);
		update.set("isNameSet", isNameSet);
		update.set("users", users);
		return mongoTemplate.updateFirst(query, update, ChatroomBo.class);
	}

	@Override
	public WriteResult updateUsers(String chatroomId, LinkedHashSet<String> users) {
		//主键筛选条件
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroomId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("users", users);
		return mongoTemplate.updateFirst(query, update, ChatroomBo.class);
	}

	@Override
	public WriteResult updateDescription(String chatroomId, String description) {
		//主键筛选条件
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroomId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("description", description);
		return mongoTemplate.updateFirst(query, update, ChatroomBo.class);
	}

	@Override
	public WriteResult updateOpen(String chatroomId, boolean isOpen) {
		//主键筛选条件
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroomId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("isOpen", isOpen);
		return mongoTemplate.updateFirst(query, update, ChatroomBo.class);
	}

	@Override
	public WriteResult updateVerify(String chatroomId, boolean isVerify) {
		//主键筛选条件
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroomId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("isVerify", isVerify);
		return mongoTemplate.updateFirst(query, update, ChatroomBo.class);
	}

	@Override
	public List<ChatroomBo> findMyChatrooms(String userid) {
		Query query = new Query();
		Criteria single = new Criteria("userid").is(userid);
		Criteria single2 = new Criteria("friendid").is(userid);
		Criteria mulit = new Criteria("users").in(userid);
		query.addCriteria(single.orOperator(single2,mulit));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		return mongoTemplate.find(query, ChatroomBo.class);
	}

	@Override
	public List<ChatroomBo> findMyChatrooms(String userid, Date timestamp) {

		Criteria c = new Criteria();
		Criteria single = new Criteria("userid").is(userid).and("type").is(Constant.ROOM_SINGLE);
		Criteria single2 = new Criteria("friendid").is(userid).and("type").is(Constant.ROOM_SINGLE);
		Criteria mulit = new Criteria("users").in(userid);
		c.orOperator(single,single2,mulit);
		Query query = new Query();
		query.addCriteria(c);
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		if (timestamp != null) {
			query.addCriteria(new Criteria("createTime").gt(timestamp));
		}
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
		return mongoTemplate.find(query, ChatroomBo.class);
	}

	@Override
	public WriteResult deleteTempChat(String targetid, int roomType) {
		Query query = new Query();
		query.addCriteria(new Criteria("type").is(roomType));
		query.addCriteria(new Criteria("targetid").is(targetid));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		Update update = new Update();
		update.set("deleted", Constant.DELETED);
		return mongoTemplate.updateMulti(query, update, ChatroomBo.class);
	}

	@Override
	public WriteResult addPartyChartroom(String chatroomId, String partyid) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroomId).and("deleted").is(Constant.ACTIVITY));
		Update update = new Update();
		update.set("targetid", partyid);
		return  mongoTemplate.updateFirst(query, update, ChatroomBo.class);
	}

	@Override
	public List<ChatroomBo> haveSameChatroom(String userid, String friendid) {

		HashSet<String> users = new LinkedHashSet<>();
		users.add(userid);
		users.add(friendid);
		Query query = new Query();
		query.addCriteria(new Criteria("users").all(users).and("type").ne(Constant.ROOM_SINGLE).and("deleted").is
				(Constant.ACTIVITY));
		return mongoTemplate.find(query, ChatroomBo.class);
	}

	public WriteResult updateRoomByParams(String chatRoomId, Map<String, Object> params){
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatRoomId).and("deleted").is(0));
		if (params != null) {
			Update update = new Update();
			params.forEach((key, value) -> update.set(key, value));
			return mongoTemplate.updateFirst(query, update, ChatroomBo.class);
		}
		return null;
	}
}
