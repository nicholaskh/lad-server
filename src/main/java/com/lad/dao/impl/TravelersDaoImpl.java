package com.lad.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.lad.bo.BaseBo;
import com.lad.bo.TravelersBaseBo;
import com.lad.dao.ITravelersDao;
import com.lad.vo.TravelersRequireVo;
import com.mongodb.BasicDBObject;
@Repository("travelersDao")
public class TravelersDaoImpl implements ITravelersDao {
	@Autowired
    private MongoTemplate mongoTemplate;
	
	

	@Override
	public TravelersRequireVo getRequireByBaseId(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 根据用户id查找他的发布
	 */
	@Override
	public List<TravelersBaseBo> getTravelersByUserId(String id) {
		BasicDBObject criteria = new BasicDBObject();
		criteria.put("createuid", id);
		BasicDBObject filter = new BasicDBObject();
		filter.put("id", true);
		filter.put("hobbys", true);
		
		Query query = new BasicQuery(criteria,filter);
		return mongoTemplate.find(query, TravelersBaseBo.class);
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
