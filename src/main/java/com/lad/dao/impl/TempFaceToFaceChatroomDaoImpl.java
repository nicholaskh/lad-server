package com.lad.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.lad.bo.TempFaceToFaceChatroomBo;
import com.lad.dao.ITempFaceToFaceChatroomDao;
import com.mongodb.WriteResult;

@Repository("tempFaceToFaceChatroomDao")
public class TempFaceToFaceChatroomDaoImpl implements
		ITempFaceToFaceChatroomDao {

	@Autowired
	public MongoTemplate mongoTemplate;
	
	public TempFaceToFaceChatroomBo insert(
			TempFaceToFaceChatroomBo tempFaceToFaceChatroomBo) {
		mongoTemplate.insert(tempFaceToFaceChatroomBo);
		return tempFaceToFaceChatroomBo;
	}

	public TempFaceToFaceChatroomBo selectByUserid(String userid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, TempFaceToFaceChatroomBo.class);
	}

	public List<TempFaceToFaceChatroomBo> selectBySeq(String seq) {
		Query query = new Query();
		query.addCriteria(new Criteria("seq").is(seq));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, TempFaceToFaceChatroomBo.class);
	}

	public WriteResult deleteByUserid(String userid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.remove(query, TempFaceToFaceChatroomBo.class);
	}

	public WriteResult deleteBySeq(String seq) {
		Query query = new Query();
		query.addCriteria(new Criteria("seq").is(seq));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.remove(query, TempFaceToFaceChatroomBo.class);
	}

	public long countBySeq(String seq) {
		Query query = new Query();
		query.addCriteria(new Criteria("seq").is(seq));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.count(query, TempFaceToFaceChatroomBo.class);
	}

}
