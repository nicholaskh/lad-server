package com.lad.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.IMTermBo;
import com.lad.dao.IIMTermDao;
import com.mongodb.WriteResult;

@Repository("iMTermDao")
public class IMTermDaoImpl implements IIMTermDao {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	public IMTermBo insert(IMTermBo iMTermBo) {
		mongoTemplate.insert(iMTermBo);
		return iMTermBo;
	}

	public IMTermBo selectByUserid(String userid) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, IMTermBo.class);
	}

	public WriteResult updateByUserid(String userid, String term) {
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("term", term);
		return mongoTemplate.updateFirst(query, update, IMTermBo.class);
	}

}
