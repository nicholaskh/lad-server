package com.lad.dao.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import net.sf.json.JSONObject;

@Repository("travelersDao")
public class TravelersDaoImpl implements ITravelersDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public WriteResult deletePublish(String requireId) {
		Query query = new Query(Criteria.where("_id").is(requireId).and("deleted").is(Constant.ACTIVITY));
		Update update = new Update();
		update.set("deleted", Constant.DELETED);
		return mongoTemplate.updateFirst(query, update, TravelersRequireBo.class);
	}

	@Override
	public int findPublishNum(String id) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("createuid").is(id), Criteria.where("deleted").is(Constant.ACTIVITY));
		query.addCriteria(criteria);
		return (int) mongoTemplate.count(query, TravelersRequireBo.class);
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
		Query query = new BasicQuery(criteria, filter);
		return mongoTemplate.findOne(query, TravelersRequireBo.class);
	}

	@Override
	public List<TravelersRequireBo> getRequireList(String id) {
		BasicDBObject criteria = new BasicDBObject();
		criteria.put("createuid", id);
		criteria.put("deleted", Constant.ACTIVITY);
		BasicDBObject filter = new BasicDBObject();
		filter.put("destination", true);
		filter.put("type", true);
		filter.put("times", true);
		Query query = new BasicQuery(criteria, filter);
		return mongoTemplate.find(query, TravelersRequireBo.class);
	}

	/**
	 * 向数据库插入一条数据
	 */
	@Override
	public String insert(BaseBo baseBo) {
		Logger logger = LoggerFactory.getLogger(getClass());
		logger.error("这里是添加时的实体类================"+baseBo);
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
		// 过滤时间 开始时间>当前时间 =没开始;结束时间>现在时间=没结束
		Criteria orcriteria = new Criteria();	
		orcriteria.orOperator(Criteria.where("times.0").gte(getFirsrtDay()), Criteria.where("times.1").gte(getFirsrtDay()));
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("deleted").is(Constant.ACTIVITY), Criteria.where("createuid").ne(id),
				orcriteria);
		query.addCriteria(criteria);
		
		Logger logger = LoggerFactory.getLogger(getClass());
		logger.error("这里是查看最新的query================"+query);
		query.skip((page - 1) * limit);
		query.limit(limit);
		query.with(new Sort(new Order(Direction.DESC, "createTime")));
		return mongoTemplate.find(query, TravelersRequireBo.class);
	}

	@Override
	public WriteResult updateByIdAndParams(String requireId, Map<String, Object> params) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("_id").is(requireId), Criteria.where("deleted").is(Constant.ACTIVITY));
		query.addCriteria(criteria);

		Update update = new Update();
		for (Entry<String, Object> entity : params.entrySet()) {
			update.set(entity.getKey(), entity.getValue());
		}
		return mongoTemplate.updateFirst(query, update, TravelersRequireBo.class);
	}

	@Override
	public List<TravelersRequireBo> findListByKeyword(String keyWord, int page, int limit,
			Class<TravelersRequireBo> clazz) {

		// 过滤时间 开始时间>当前时间 =没开始;结束时间>现在时间=没结束
		Criteria orcriteria = new Criteria();
		orcriteria.orOperator(Criteria.where("times.0").gte(getFirsrtDay()), Criteria.where("times.1").gte(getFirsrtDay()));
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("destination").regex(".*" + keyWord + ".*"),
				Criteria.where("deleted").is(Constant.ACTIVITY), orcriteria);
		Query query = new Query();
		query.addCriteria(criteria);
		query.skip((page - 1) * limit);
		query.limit(limit);
		query.with(new Sort(new Order(Direction.DESC, "createTime")));
		return mongoTemplate.find(query, clazz);
	}

	@Override
	public List<Map> getRecommend(TravelersRequireBo require) {
		// 随机取100个实体
		// 过滤时间 开始时间>当前时间 =没开始;结束时间>现在时间=没结束
		Criteria orcriteria = new Criteria();
		orcriteria.orOperator(Criteria.where("times.0").gte(getFirsrtDay()), Criteria.where("times.1").gte(getFirsrtDay()));
		// 过滤id和已删除数据
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("deleted").is(Constant.ACTIVITY),
				Criteria.where("createuid").ne(require.getCreateuid()), orcriteria);
		Query query = new Query(criteria);

		int count = (int) mongoTemplate.count(query, "waiters");
		Random r = new Random();
		int length = (count - 99) > 0 ? (count - 99) : 1;
		int skip = r.nextInt(length);
		query.skip(skip);
		query.limit(100);
		List<TravelersRequireBo> find = mongoTemplate.find(query, TravelersRequireBo.class);

		// 我的意向
		String destination = "不限";
		if (require.getDestination() != null) {
			destination = require.getDestination();
		}
		DateFormat format = new SimpleDateFormat("yyyy-MM");
		List<Date> times = require.getTimes();
		long myStart = Long.valueOf(format.format(times.get(0)).replaceAll("-", ""));
		long myEnd = Long.valueOf(format.format(times.get(1)).replaceAll("-", ""));

		String type = "不限";
		if (require.getType() != null) {
			type = require.getType();
		}
		String sex = "不限";
		if (require.getSex() != null) {
			sex = require.getSex();
		}
		String age = "不限";
		if (require.getAge() != null) {
			age = require.getAge();
		}

		List<String> temp = new ArrayList<>();
		List<Map> list = new ArrayList<>();

		for (TravelersRequireBo other : find) {
			if (temp.contains(other.getId())) {
				continue;
			}

			int match = 0;
			// 目的地
			if ("不限".equals(destination)) {
				match += 25;
			} else if (other.getDestination() != null) {
				if (destination.equals(other.getDestination())) {
					match += 25;
				}
			}

			// 时段
			List<Date> OtherTimes = other.getTimes();

			long othStart = Long.valueOf(format.format(OtherTimes.get(0)).replaceAll("-", ""));
			long othEnd = Long.valueOf(format.format(OtherTimes.get(1)).replaceAll("-", ""));
			// 交集或包含
			if ((othStart >= myStart && othStart <= myEnd) || (othEnd >= myStart && othEnd <= myEnd)
					|| (othStart <= myStart && othEnd >= myEnd)) {
				match += 25;
			}

			// 旅行方式
			if ("不限".equals(type)) {
				match += 20;
			} else if (other.getType() != null) {
				if (type.equals(other.getType())) {
					match += 20;
				}
			}

			// 驴友性别
			if ("不限".equals(sex)) {
				match += 15;
			} else if (other.getSex() != null) {
				if (sex.equals((String) other.getSex())) {
					match += 15;
				}
			}

			// 驴友年龄
			if ("不限".equals(age)) {
				match += 15;
			} else if (other.getAge() != null) {
				if (age.equals((String) other.getAge())) {
					match += 15;
				}
			}

			if (match > 0) {
				temp.add(other.getId());
				Map map = new HashMap<>();
				map.put("match", match);
				map.put("result", other);
				list.add(map);
			}
		}
		return list;
	}
	
	private Date getFirsrtDay(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

}
