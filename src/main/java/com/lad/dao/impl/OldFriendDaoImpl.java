package com.lad.dao.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.OldFriendRequireBo;
import com.lad.bo.UserBo;
import com.lad.bo.UserTasteBo;
import com.lad.controller.OldFriendController;
import com.lad.dao.IOldFriendDao;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.mongodb.WriteResult;

@Repository("oldFriendDao")
public class OldFriendDaoImpl implements IOldFriendDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public String getInitData(String id) {
		OldFriendRequireBo oldFriendRequire = mongoTemplate.findOne(
				new Query(Criteria.where("createuid").is(id).and("deleted").is(Constant.ACTIVITY)),
				OldFriendRequireBo.class);
		if (oldFriendRequire == null) {
			return null;
		}
		return oldFriendRequire.getId();
	}

	@Override
	public String insert(OldFriendRequireBo requireBo) {
		mongoTemplate.save(requireBo);
		return requireBo.getId();
	}

	@Override
	public long getRequireCount(String uid) {
		return mongoTemplate.count(new Query(Criteria.where("createuid").is(uid).and("deleted").is(Constant.ACTIVITY)),
				OldFriendRequireBo.class);
	}

	@Override
	public WriteResult deleteByRequireId(String uid, String requireId) {
		Query query = new Query(
				Criteria.where("createuid").is(uid).and("_id").is(requireId).and("deleted").is(Constant.ACTIVITY));
		Update update = new Update();
		update.set("deleted", Constant.DELETED);
		return mongoTemplate.updateFirst(query, update, OldFriendRequireBo.class);
	}

	@Override
	public OldFriendRequireBo getByRequireId(String id, String requireId) {
		return mongoTemplate.findOne(new Query(
				Criteria.where("createuid").is(id).and("_id").is(requireId).and("deleted").is(Constant.ACTIVITY)),
				OldFriendRequireBo.class);
	}

	@Override
	public WriteResult updateByParams(Map<String, Object> params, String requireId) {
		Query query = new Query(Criteria.where("_id").is(requireId).and("deleted").is(Constant.ACTIVITY));
		Update update = new Update();
		for (Entry<String, Object> entity : params.entrySet()) {
			update.set(entity.getKey(), entity.getValue());
		}
		return mongoTemplate.updateFirst(query, update, OldFriendRequireBo.class);
	}

	@Override
	public List<UserBo> findListByKeyword(String keyWord, int page, int limit, String uid) {

		Query query = new Query();
		Criteria c = new Criteria();
		c.orOperator(Criteria.where("userName").regex(".*" + keyWord + ".*"),
				Criteria.where("address").regex(".*" + keyWord + ".*"));
		Criteria criteria = new Criteria();
		criteria.andOperator(c, Criteria.where("deleted").is(Constant.ACTIVITY), Criteria.where("createuid").ne(uid));

		query.addCriteria(criteria);
		query.skip((page - 1) * limit);
		query.limit(limit);
		query.with(new Sort(new Order(Direction.DESC, "createTime")));
		return mongoTemplate.find(query, UserBo.class);
	}

	@Override
	public List<OldFriendRequireBo> findNewPublish(int page, int limit, String id) {
		Query query = new Query(Criteria.where("deleted").is(Constant.ACTIVITY));
		query.with(new Sort((new Order(Direction.DESC, "createTime"))));
		return mongoTemplate.find(query,OldFriendRequireBo.class);
	}

	@Override
	public OldFriendRequireBo getByRequireId(String requireId) {
		return mongoTemplate.findOne(
				new Query(Criteria.where("_id").is(requireId).and("deleted").is(Constant.ACTIVITY)),
				OldFriendRequireBo.class);
	}

	@Override
	public OldFriendRequireBo getRequireByCreateUid(String id) {
		return mongoTemplate.findOne(new Query(Criteria.where("createuid").is(id).and("deleted").is(Constant.ACTIVITY)),
				OldFriendRequireBo.class);
	}

	@Override
	public List<Map> getRecommend(OldFriendRequireBo require) {

		// 随机取100个实体
		Query query = new Query(
				Criteria.where("deleted").is(Constant.ACTIVITY).and("createuid").ne(require.getCreateuid()));
		int count = (int) mongoTemplate.count(query, OldFriendRequireBo.class);
		
		
		if(count<100){
			query.with(new Sort(Sort.Direction.DESC,"_id"));
		}else{
			Random r = new Random();
			int length = (count - 99) > 0 ? (count - 99) : 1;
			int skip = r.nextInt(length);
			query.skip(skip);
			query.limit(100);
		}

		List<OldFriendRequireBo> find = mongoTemplate.find(query, OldFriendRequireBo.class);

		// 性别要求
		String sexRequire = "不限";
		if (require.getSex() != null) {
			sexRequire = require.getSex();
		}

		// 年龄要求
		int minAgeRequire = 0;
		int maxAgeRequire = 150;
		if (require.getAge() != null && !("不限".equals(require.getAge()))) {
			String[] split = require.getAge().split("-");
			minAgeRequire = Integer.valueOf(split[0].replaceAll("\\D*", ""));
			if (split.length >= 2) {
				maxAgeRequire = Integer.valueOf(split[1].replaceAll("\\D*", ""));
			}

		}
		// 兴趣要求

		Map<String, Set<String>> hobbysRequire = new HashMap<String,Set<String>>();
		if (require.getHobbys() != null) {
			hobbysRequire  = require.getHobbys();
		}

		// 居住地要求
		String addressRequire = "不限";
		if (require.getAddress() != null) {
			addressRequire = require.getAddress();
		}

		List<String> temp = new ArrayList<>();

		List<Map> result = new ArrayList<>();
		System.out.println(find);
		for (OldFriendRequireBo bo : find) {
			if (temp.contains(bo.getId())) {
				continue;
			}

			int match = 0;

			UserBo user = mongoTemplate.findOne(new Query(Criteria.where("_id").is(bo.getCreateuid())), UserBo.class);
			// 匹配性别
			if (user.getSex() != null) {
				if ("不限".equals(sexRequire) || user.getSex().equals(sexRequire)) {
					match += 25;
				}
			}

			// 匹配年龄
			if (user.getBirthDay() != null) {
				String birthDay = user.getBirthDay();
				DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
				try {
					Date parse = format.parse(birthDay);
					int userAge = CommonUtil.getAge(parse);

					if ((userAge > minAgeRequire && userAge < maxAgeRequire) || "不限".equals(require.getAge())) {
						match += 25;
					}
					if (userAge < minAgeRequire) {
						int x = (int) ((100 - (minAgeRequire - userAge) * 15) * 0.25);
						if (x > 0) {
							match += x;
						}
					}
					if (userAge > maxAgeRequire) {
						int x = (int) ((100 - (userAge - maxAgeRequire) * 15) * 0.25);
						if (x > 0) {
							match += x;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// 匹配居住地
			if (user.getCity() != null) {
				if ("不限".equals(addressRequire) || addressRequire.equals(user.getCity())) {
					match += 25;
				}
			}

			// 匹配兴趣爱好
			UserTasteBo tasteBo = mongoTemplate.findOne(new Query(Criteria.where("userid").is(user.getId())),
					UserTasteBo.class);
			int hobbyNum = 0;
			LinkedHashSet<String> lifes = tasteBo.getLifes();
			LinkedHashSet<String> sports = tasteBo.getSports();
			LinkedHashSet<String> musics = tasteBo.getMusics();
			LinkedHashSet<String> trips = tasteBo.getTrips();
			int requireSize = 0;
			for (String key : hobbysRequire.keySet()) {
				if("生活".equals(key)){
					Iterator<String> iterator = hobbysRequire.get(key).iterator();
					requireSize+=hobbysRequire.get(key).size();
					while(iterator.hasNext()){
						String next = iterator.next();
						if(lifes.contains(next)){
							hobbyNum+=1;
						}
					}
				}
				if("运动".equals(key)){
					Iterator<String> iterator = hobbysRequire.get(key).iterator();
					requireSize+=hobbysRequire.get(key).size();
					while(iterator.hasNext()){
						String next = iterator.next();
						if(sports.contains(next)){
							hobbyNum+=1;
						}
					}
				}
				if("旅行足迹".equals(key)){
					Iterator<String> iterator = hobbysRequire.get(key).iterator();
					requireSize+=hobbysRequire.get(key).size();
					while(iterator.hasNext()){
						String next = iterator.next();
						if(trips.contains(next)){
							hobbyNum+=1;
						}
					}
				}
				if("音乐".equals(key)){
					Iterator<String> iterator = hobbysRequire.get(key).iterator();
					requireSize+=hobbysRequire.get(key).size();
					while(iterator.hasNext()){
						String next = iterator.next();
						if(musics.contains(next)){
							hobbyNum+=1;
						}
					}
				}
			}
			
			if (hobbyNum >= 1) {
				match += (60 + (hobbyNum - 1) / requireSize - 1 * 40) * 0.25;
			}

			if (match > 0) {
				temp.add(bo.getId());
				Map map = new HashMap<>();
				map.put("match", match);
				map.put("requireBo", bo);
				result.add(map);
			}
		}

		return result;
	}

	@Override
	public int findPublishNum(String uid) {
		return (int) mongoTemplate.count(
				new Query(Criteria.where("createuid").is(uid).and("deleted").is(Constant.ACTIVITY)),
				OldFriendRequireBo.class);
	}

}
