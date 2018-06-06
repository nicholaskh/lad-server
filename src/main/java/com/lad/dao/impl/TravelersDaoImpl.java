package com.lad.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.BaseBo;
import com.lad.bo.TravelersBaseBo;
import com.lad.bo.TravelersRequireBo;
import com.lad.dao.ITravelersDao;
import com.lad.util.Constant;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
@Repository("travelersDao")
public class TravelersDaoImpl implements ITravelersDao {
	@Autowired
    private MongoTemplate mongoTemplate;
	
	@Override
	public WriteResult deletePublish(String requireId) {
		Query query = new Query(Criteria.where("_id").is(requireId));
		Update update = new Update();
		update.set("deleted", Constant.DELETED);
		return mongoTemplate.updateFirst(query, update, TravelersBaseBo.class);
	}
	
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

	@Override
	public List<TravelersRequireBo> getNewTravelers(int page, int limit, String id) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		Date date = new Date();
		long time = date.getTime()-7*24*60*60*1000;
		Date weekBefore = new Date(time);
		
		
		criteria.andOperator(Criteria.where("deleted").is(Constant.ACTIVITY),Criteria.where("createTime").gt(weekBefore),Criteria.where("createuid").ne(id));
		query.addCriteria(criteria);
		query.skip((page - 1) * limit);
		query.limit(limit);
		return mongoTemplate.find(query, TravelersRequireBo.class);
	}

	@Override
	public WriteResult updateByIdAndParams(String requireId, Map<String, Object> params) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("_id").is(requireId),Criteria.where("deleted").is(Constant.ACTIVITY));
		query.addCriteria(criteria);
		
		Update update = new Update();
		for (Entry<String, Object> entity : params.entrySet()) {
			update.set(entity.getKey(), entity.getValue());
		}
		return mongoTemplate.updateFirst(query, update, TravelersRequireBo.class);
	}

	@Override
	public List<TravelersRequireBo> findListByKeyword(String keyWord, Class<TravelersRequireBo> clazz) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("destination").regex( ".*"+keyWord+".*"),Criteria.where("deleted").is(Constant.ACTIVITY));
		query.addCriteria(criteria);
		return mongoTemplate.find(query, clazz);
	}


}
