package com.lad.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.lad.bo.UserBo;
import com.lad.dao.IRegistDao;

@Repository("registDao")
public class RegistDaoImpl implements IRegistDao {

	@Autowired
	public MongoTemplate mongoTemplate;

	// 返回phone的用户ID
	public Integer searchPhone(String phone) {
		Query query = new Query();
		Criteria criteria = new Criteria("phone").is(phone);
		query.addCriteria(criteria);
		UserBo user = mongoTemplate.findOne(query, UserBo.class);
		if (user != null) {
			return 0;
		}
		return -1;
	}

}
