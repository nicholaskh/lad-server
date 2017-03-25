package com.junlenet.mongodb.demo.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.dao.ILoginDao;

@Repository("loginDao")
public class LoginDaoImpl implements ILoginDao {

	@Autowired
	public MongoTemplate mongoTemplate;
	
	public UserBo searchUser(String username, String password) {
		Query query = new Query();
		Criteria criteria1 = new Criteria("phone").is(username);
		Criteria criteria2 = new Criteria("password").is(password);
		query.addCriteria(criteria1).addCriteria(criteria2);
		UserBo user = mongoTemplate.findOne(query, UserBo.class);
		return user;
	}

}
