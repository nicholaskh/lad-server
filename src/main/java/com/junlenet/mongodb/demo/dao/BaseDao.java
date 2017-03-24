package com.junlenet.mongodb.demo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.junlenet.mongodb.demo.bo.BaseBo;

public class BaseDao<T extends BaseBo> {

	@Autowired
	public MongoTemplate mongoTemplate;

	public String saveOrUpdate(T bo){
		mongoTemplate.save(bo);
		return bo.getId();
	}
	
	
}
