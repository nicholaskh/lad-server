package com.lad.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.lad.bo.ComplainBo;
import com.lad.dao.IComplainDao;

@Repository("complainDao")
public class ComplainDaoImpl implements IComplainDao {
	@Autowired
	private MongoTemplate mongoTemplate;
	public ComplainBo insert(ComplainBo complainBo) {
		mongoTemplate.insert(complainBo);
		return complainBo;
	}

}
