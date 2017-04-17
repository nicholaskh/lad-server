package com.junlenet.mongodb.demo.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.junlenet.mongodb.demo.bo.InfoBo;
import com.junlenet.mongodb.demo.dao.IInfoDao;

@Repository("infoDao")
public class InfoDaoImpl implements IInfoDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	public InfoBo insert(InfoBo infoBo) {
		mongoTemplate.insert(infoBo);
		return infoBo;
	}

	public List<InfoBo> selectByOwnerId(String ownerId) {
		Query query = new Query();
		query.addCriteria(new Criteria("ownerId").is(ownerId));
		return mongoTemplate.find(query, InfoBo.class);
	}

}
