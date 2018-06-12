package com.lad.dao.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.BaseBo;
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
		Query query = new Query(Criteria.where("_id").is(requireId).and("deleted").is(Constant.ACTIVITY));
		Update update = new Update();
		update.set("deleted", Constant.DELETED);
		TravelersRequireBo findOne = mongoTemplate.findOne(query, TravelersRequireBo.class);
		return mongoTemplate.updateFirst(query, update, TravelersRequireBo.class);
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
		query.with(new Sort(new Order(Direction.DESC,"createTime")));
		
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
	public List<TravelersRequireBo> findListByKeyword(String keyWord,int page,int limit, Class<TravelersRequireBo> clazz) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("destination").regex( ".*"+keyWord+".*"),Criteria.where("deleted").is(Constant.ACTIVITY));
		query.addCriteria(criteria);
		query.skip((page-1)*limit);
		query.limit(limit);
		query.with(new Sort(new Order(Direction.DESC,"createTime")));
		return mongoTemplate.find(query, clazz);
	}

	@Override
	public List<Map> getRecommend(TravelersRequireBo require) {
		// 随机取100个实体
		Query query = new Query(Criteria.where("sex").is(require.getSex()).and("deleted").is(Constant.ACTIVITY).and("createuid").ne(require.getCreateuid()));
		int count = (int) mongoTemplate.count(query, "waiters");
		Random r = new Random();
		int length = (count - 99) > 0 ? (count - 99) : 1;
		int skip = r.nextInt(length);
		query.skip(skip);
		query.limit(100);
		List<TravelersRequireBo> find = mongoTemplate.find(query, TravelersRequireBo.class);
		
		// 我的资料
		String destination = require.getDestination();
		String days = require.getDays();
		String type = require.getType();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String assembleTime = format.format(require.getAssembleTime());
		String sex = require.getSex();
		String age = require.getAge();
		
		List temp = new ArrayList<>();
		List<Map> list = new ArrayList<>();
		for (TravelersRequireBo other : find) {	
			if(temp.contains(other.getId())){
				continue;
			}
			
			int match = 0;
			if(destination!=null && other.getDestination()!=null){
				if(destination.equals(other.getDestination())){
					match+=22;
				}
			}
			
			if(days!=null && other.getDays()!=null){
				if(days.equals(other.getDays())){
					match+=22;
				}
			}
			if(type!=null && other.getType()!=null){
				if(type.equals(other.getType())){
					match+=17;
				}
			}
			if(assembleTime!=null && other.getAssembleTime()!=null){
				if(type.equals(format.format(other.getAssembleTime()))){
					match+=15;
				}
			}

			if(sex!=null && other.getSex()!=null){
				if(sex.equals((String)other.getSex()) || "不限".equals(sex)){
					match+=12;
				}
			}

			if(age!=null && other.getAge()!=null){
				if(age.equals((String)other.getAge()) || "不限".equals(age)){
					match+=12;
				}
			}
			
			temp.add(other.getId());
			Map map = new HashMap<>();
			map.put("match", match);
			map.put("result", other);
			list.add(map);
			
			
			/*if(match>60){
				Map map = new HashMap<>();
				map.put("match", match);
				map.put("result", other);
				list.add(map);
			}*/
			
		}
		
		return list;
	}


}
