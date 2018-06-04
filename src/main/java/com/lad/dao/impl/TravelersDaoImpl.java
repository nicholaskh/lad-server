package com.lad.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.lad.bo.BaseBo;
import com.lad.bo.TravelersRequireBo;
import com.lad.dao.ITravelersDao;
import com.lad.util.Constant;
import com.mongodb.BasicDBObject;
@Repository("travelersDao")
public class TravelersDaoImpl implements ITravelersDao {
	@Autowired
    private MongoTemplate mongoTemplate;
	
	@Override
	public int findPublishNum(String id) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("createuid").is(id),Criteria.where("deleted").is(Constant.ACTIVITY));
		return (int)mongoTemplate.count(query, TravelersRequireBo.class);
	}
	
	@Override
	public TravelersRequireBo getRequireById(String requireId) {
		BasicDBObject criteria = new BasicDBObject();
		criteria.put("_id", requireId);
		criteria.put("deleted", Constant.ACTIVITY);
		BasicDBObject filter = new BasicDBObject();
		// deleted 与createTime就算这里过滤也会字在转json中重新初始化,所以
		filter.put("updateTime", false);
		filter.put("updateuid", false);
		filter.put("createuid", false);
		filter.put("deleted", false);
		Query query = new BasicQuery(criteria,filter);
		return mongoTemplate.findOne(query, TravelersRequireBo.class);
	}

	@Override
	public List<TravelersRequireBo> getRequireList(String id) {
		BasicDBObject criteria = new BasicDBObject();
		criteria.put("createuid", id);
		criteria.put("deleted", Constant.ACTIVITY);
		BasicDBObject filter = new BasicDBObject();
		filter.put("destination", true);
		filter.put("days", true);
		filter.put("type", true);
		filter.put("deleted", false);
		Query query = new BasicQuery(criteria,filter);
		return mongoTemplate.find(query, TravelersRequireBo.class);
	}
	
	/**
	 * 向数据库插入一条数据
	 */
	@Override
	public String insert(BaseBo baseBo) {
		mongoTemplate.insert(baseBo);
		return baseBo.getId();
	}
	
	@Override
	public void test() {
		System.out.println(mongoTemplate);
	}











}
