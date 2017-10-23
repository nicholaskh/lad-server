package com.lad.dao.impl;

import com.lad.bo.TagBo;
import com.lad.dao.ITagDao;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;

@Repository("tagDao")
public class TagDaoImpl implements ITagDao {

	@Autowired
	public MongoTemplate mongoTemplate;
	
	public TagBo insert(TagBo tagBo) {
		mongoTemplate.insert(tagBo);
		return tagBo;
	}

	public WriteResult updateFriendsIdsById(TagBo tagBo) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(tagBo.getId()));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("friendsIds", tagBo.getFriendsIds());
		return mongoTemplate.updateFirst(query, update, TagBo.class);
	}

	public WriteResult updateTagName(TagBo tagBo){
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(tagBo.getId()));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("name", tagBo.getName());
		return mongoTemplate.updateFirst(query, update, TagBo.class);
	}

	public List<TagBo> getTagBoListByUserid(String userid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, TagBo.class);
	}

	public WriteResult deleteById(String tagId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(tagId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("deleted", 1);
		return mongoTemplate.updateFirst(query, update, TagBo.class);
	}

	public List<TagBo> getTagBoListByUseridAndFrinedid(String userid, String friendid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("deleted").is(0));
		query.addCriteria(new Criteria("friendsIds").in(friendid));
		return mongoTemplate.find(query, TagBo.class);
	}
	public TagBo get(String tagId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(tagId));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, TagBo.class);
	}

	public TagBo getBynameAndUserid(String tagName, String userid){
		Query query = new Query();
		query.addCriteria(new Criteria("name").is(tagName));
		query.addCriteria(new Criteria("userid").is(userid));
		return mongoTemplate.findOne(query, TagBo.class);
	}

	@Override
	public WriteResult updateTagFriends(String tagid, LinkedHashSet<String> friendsIds) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(tagid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("friendsIds", friendsIds);
		return mongoTemplate.updateFirst(query, update, TagBo.class);
	}
}
