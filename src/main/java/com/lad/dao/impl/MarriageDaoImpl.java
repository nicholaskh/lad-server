package com.lad.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.BeanUtils;
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
import com.lad.bo.OptionBo;
import com.lad.bo.RequireBo;
import com.lad.bo.WaiterBo;
import com.lad.dao.IMarriageDao;
import com.lad.util.Constant;
import com.lad.vo.OptionVo;
import com.lad.vo.WaiterVo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository("marriageDao")
public class MarriageDaoImpl implements IMarriageDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public int findPublishNum(String id) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("createuid").is(id), Criteria.where("deleted").is(Constant.ACTIVITY),
				Criteria.where("sex").is(1));
		query.addCriteria(criteria);
		return (int) mongoTemplate.count(query, WaiterBo.class);
	}

	/**
	 * 查询新的发布
	 */
	@Override
	public List<WaiterBo> getNewPublic(int type, int page, int limit, String uid) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("deleted").is(0), Criteria.where("sex").is(type),
				Criteria.where("createuid").ne(uid));
		query.addCriteria(criteria);
		query.skip((page - 1) * limit);
		query.limit(limit);
		query.with(new Sort(new Order(Direction.DESC, "createTime")));

		return mongoTemplate.find(query, WaiterBo.class);
	}

	/**
	 * 获取所有选项
	 */
	@Override
	public List<OptionBo> getOptions() {

		DBObject dbObject = new BasicDBObject();
		dbObject.put("deleted", 1); // 查询条件

		BasicDBObject fieldsObject = new BasicDBObject();
		// 指定返回的字段
		fieldsObject.put("value", true);
		fieldsObject.put("field", true);
		fieldsObject.put("sort", true);
		fieldsObject.put("_id", true);
		fieldsObject.put("supId", true);

		Query query = new BasicQuery(dbObject, fieldsObject);
		return mongoTemplate.find(query, OptionBo.class);
	}

	@Override
	public String insertPublish(BaseBo bb) {
		mongoTemplate.insert(bb);
		return bb.getId();
	}

	@Override
	public Set<String> getPass(String waiterId) {

		DBObject dbObject = new BasicDBObject();
		dbObject.put("_id", waiterId); // 查询条件
		dbObject.put("deleted", 0);

		BasicDBObject fieldsObject = new BasicDBObject();
		// 指定返回的字段
		fieldsObject.put("pass", true);

		Query query = new BasicQuery(dbObject, fieldsObject);
		WaiterBo findOne = mongoTemplate.findOne(query, WaiterBo.class);
		return findOne.getPass();
	}

	/**
	 * 查询单个Require
	 */
	@Override
	public RequireBo findRequireById(String waiterId) {
		return mongoTemplate.findOne(
				new Query(Criteria.where("waiterId").is(waiterId).and("deleted").is(Constant.ACTIVITY)),
				RequireBo.class);
	}

	/**
	 * 查询单个waiter
	 */
	@Override
	public WaiterBo findWaiterById(String caresId) {

		BasicDBObject cirteria = new BasicDBObject();
		cirteria.put("_id", caresId);
		cirteria.put("deleted", Constant.ACTIVITY);

		BasicDBObject filter = new BasicDBObject();
		filter.put("createTime", false);
		filter.put("deleted", false);
		filter.put("updateTime", false);
		filter.put("updateuid", false);
		filter.put("pass", false);
		filter.put("cares", false);

		Query query = new BasicQuery(cirteria, filter);
		return mongoTemplate.findOne(query, WaiterBo.class);
	}

	/**
	 * 删除发布,将状态值改为1
	 */
	@Override
	public WriteResult deletePublish(String pubId) {
		Query query = new Query(Criteria.where("_id").is(pubId));
		Update update = new Update();
		update.set("deleted", 1);
		return mongoTemplate.updateFirst(query, update, WaiterBo.class);
	}

	/**
	 * 获取发布列表,实际上是获取waiter列表
	 * 
	 */
	@Override
	public List<WaiterBo> getPublishById(String userId) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("createuid").is(userId), Criteria.where("deleted").is(0));
		query.addCriteria(criteria);
		List<WaiterBo> find = mongoTemplate.find(query, WaiterBo.class);
		return find;
	}

	/**
	 * 获取选项列表
	 */
	@Override
	public List<OptionBo> getOptions(OptionVo ov) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		if (ov.getSupId() == null) {
			criteria.andOperator(Criteria.where("field").is(ov.getField()), Criteria.where("deleted").is(1));
		} else {
			criteria.andOperator(Criteria.where("field").is(ov.getField()), Criteria.where("deleted").is(1),
					Criteria.where("supId").is(ov.getSupId()));
		}
		query.addCriteria(criteria);

		return mongoTemplate.find(query, OptionBo.class);
	}

	/**
	 * 更新数据
	 * 
	 * @return WriteResult
	 * @param id
	 *            待更新 的id params 变更的参数 class1 对应实体类的class
	 */
	@Override
	public WriteResult updateByParams(String id, Map<String, Object> params, Class class1) {
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(id);
		query.addCriteria(criteria);
		Update update = new Update();
		if (params != null) {
			Set<Map.Entry<String, Object>> entrys = params.entrySet();
			for (Map.Entry<String, Object> entry : entrys) {
				update.set(entry.getKey(), entry.getValue());
			}
		}
		return mongoTemplate.updateFirst(query, update, class1);
	}

	@Override
	public Map<String, Set<String>> getCareMap(String waiterId) {
		WaiterBo waiter = mongoTemplate.findOne(
				new Query(Criteria.where("_id").is(waiterId).and("deleted").is(Constant.ACTIVITY)), WaiterBo.class);
		if (waiter == null) {
			return new HashMap<String, Set<String>>();
		}
		return waiter.getCares();
	}

	@Override
	public WriteResult updateCare(String waiterId, Map<String, Set<String>> map) {
		Query query = new Query(Criteria.where("_id").is(waiterId).and("deleted").is(Constant.ACTIVITY));
		Update update = new Update();
		update.set("cares", map);
		WriteResult updateFirst = mongoTemplate.updateFirst(query, update, WaiterBo.class);
		return updateFirst;
	}

	@Override
	public List<WaiterBo> findListByKeyword(String keyWord, int type, int page, int limit, Class clazz) {
		Criteria c = new Criteria();
		c.orOperator(Criteria.where("nickName").regex(".*" + keyWord + ".*"),
				Criteria.where("nowin").regex(".*" + keyWord + ".*"));
		Criteria criertia = new Criteria();
		criertia.andOperator(Criteria.where("sex").is(type), Criteria.where("deleted").is(Constant.ACTIVITY), c);
		Query query = new Query();
		query.addCriteria(criertia).skip((page - 1) * limit).limit(limit)
				.with(new Sort(new Order(Direction.DESC, "createTime")));
		return mongoTemplate.find(query, clazz);
	}

	@Override
	public int findPublishGirlNum(String uid) {
		return (int) mongoTemplate.count(
				new Query(Criteria.where("createuid").is(uid).and("deleted").is(Constant.ACTIVITY).and("sex").is(0)),
				WaiterBo.class);
	}

	@Override
	public List<WaiterBo> getBoysByUserId(String userId) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("createuid").is(userId), Criteria.where("deleted").is(0),
				Criteria.where("sex").is(1));
		query.addCriteria(criteria);

		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "updateTime")));

		return mongoTemplate.find(query, WaiterBo.class);
	}

	@Override
	public List<WaiterBo> getGirlsByUserId(String userId) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("createuid").is(userId), Criteria.where("deleted").is(0),
				Criteria.where("sex").is(0));
		query.addCriteria(criteria);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "updateTime")));
		return mongoTemplate.find(query, WaiterBo.class);
	}

	/**
	 * 推荐
	 */
	public List<Map> getRecommend(String waiterId) {
		// 查找waiter的需求
		Query waiter = new Query(Criteria.where("waiterId").is(waiterId).and("deleted").is(Constant.ACTIVITY));
		RequireBo requireBo = mongoTemplate.findOne(waiter, RequireBo.class);

		// 随机取100个实体
		Query query = new Query(Criteria.where("sex").is(requireBo.getSex()));
		int count = (int) mongoTemplate.count(query, "waiters");
		Random r = new Random();
		int length = (count - 99) > 0 ? (count - 99) : 1;
		int skip = r.nextInt(length);
		query.skip(skip);
		query.limit(100);
		List<WaiterBo> find = mongoTemplate.find(query, WaiterBo.class);

		List<Map> result = new ArrayList<>();
		List tempList = new ArrayList<>();
		for (WaiterBo waiterBo : find) {
			if (tempList.contains(waiterBo.getId())) {
				continue;
			}
			Map map = getMatch(mongoTemplate, requireBo, waiterBo);
			tempList.add(waiterBo.getId());
			result.add(map);
		}
		return result;
	}

	/**
	 * 计算匹配度
	 * 
	 * @param mongoTemplate
	 * @param requireBo
	 * @param waiterBo
	 * @return
	 */
	private Map getMatch(MongoTemplate mongoTemplate, RequireBo requireBo, WaiterBo waiterBo) {
		int matchNum = 0;

		// 基础条件匹配
		int baseNum = 0;
		if (requireBo.getNowin() != null && requireBo.getNowin() != ""
				&& requireBo.getNowin().equals(waiterBo.getNowin())) {
			baseNum += 20;
		}
		if (requireBo.getMarriaged() == waiterBo.getMarriaged()) {
			baseNum += 20;
		}

		// 其他条件匹配
		int otherNum = 0;

		// 工作匹配
		if (requireBo.getJob() != null && requireBo.getJob().contains(waiterBo.getJob())) {
			otherNum += 10;
		}
		// 兴趣匹配
		int hobbyMacthNum = 0;
		int myHobNum = 0;
		if (waiterBo.getHobbys() != null) {
			for (Entry<String, Set<String>> hobbys : waiterBo.getHobbys().entrySet()) {
				myHobNum += hobbys.getValue().size();
				Set<String> requireSet = requireBo.getHobbys().get(hobbys.getKey());
				for (String string : hobbys.getValue()) {
					if (requireSet.contains(string)) {
						hobbyMacthNum++;
					}
				}
			}
			if (hobbyMacthNum >= 1) {
				int round = Math.round(hobbyMacthNum / myHobNum * 40);
				otherNum += Math.round((round + 60) * 0.1);
			}
		}

		// 学历匹配
		int educationMatch = 0;
		// 如果学历不限,或者基础资料学历大于要求学历
		Integer requireEducation = requireBo.getEducation();
		Integer waiterEducation = waiterBo.getEducation();
		if (requireEducation != null && waiterEducation != null) {
			if (requireEducation == 0 || waiterEducation - requireEducation >= 0) {
				educationMatch = 100;
			}
		}

		otherNum += educationMatch * 0.1;

		// 收入匹配
		int salaryNum = 0;

		Query salarQuery = new Query(Criteria.where("value").is(waiterBo.getSalary()));
		OptionBo waiterOptoins = mongoTemplate.findOne(salarQuery, OptionBo.class);
		salarQuery = new Query(Criteria.where("value").is(requireBo.getSalary()));
		OptionBo requireOptoins = mongoTemplate.findOne(salarQuery, OptionBo.class);

		if (waiterOptoins != null && requireOptoins != null) {
			Integer waiterSort = waiterOptoins.getSort();
			Integer requireSort = requireOptoins.getSort();
			if (requireSort == 0 || waiterSort - requireSort >= 0) {
				salaryNum = 100;
			}
		}

		otherNum += salaryNum * 0.3;

		// 身高匹配
		int hightMatch = 0;
		Integer waiterHight = waiterBo.getHight();
		if (waiterHight != null) {
			String[] split = requireBo.getHight().replace("厘米", "").split("-");
			int minHight = Integer.valueOf(split[0]);
			int maxHight = Integer.valueOf(split[1]);

			if (waiterHight >= minHight && waiterHight <= maxHight) {
				hightMatch = 100;
			}
			if (waiterHight < minHight) {
				hightMatch = (100 - 50 * (minHight - waiterHight) / 10 > 0) ? (100 - 50 * (minHight - waiterHight) / 10)
						: 0;
			}
			if (waiterHight > maxHight) {
				hightMatch = (100 - 30 * (waiterHight - maxHight) / 10 > 0) ? (100 - 50 * (minHight - waiterHight) / 10)
						: 0;
			}
		}

		otherNum += hightMatch * 0.3;

		// 年龄匹配
		int ageMatch = 0;
		Integer waiterAge = waiterBo.getAge();
		if (waiterAge != null) {
			String[] split2 = requireBo.getAge().replace("岁", "").split("-");
			int minAge = Integer.valueOf(split2[0]);
			int maxAge = Integer.valueOf(split2[1]);

			if (waiterAge >= minAge && waiterAge <= maxAge) {
				ageMatch = 100;
			}
			if (waiterAge < minAge) {
				ageMatch = (100 - 30 * (minAge - waiterAge) / 10 > 0) ? (100 - 30 * (minAge - waiterAge) / 10) : 0;
			}
			if (waiterAge > maxAge) {
				ageMatch = (100 - 50 * (waiterAge - maxAge) / 10 > 0) ? (100 - 50 * (waiterAge - maxAge) / 10) : 0;
			}
		}

		otherNum += ageMatch * 0.3;

		matchNum = (int) (baseNum + Math.round(otherNum * 0.6));

		Map map = new HashMap<>();
		WaiterVo waiterVo = new WaiterVo();
		BeanUtils.copyProperties(waiterBo, waiterVo);
		if (matchNum > 0) {
			map.put("match", matchNum);
			map.put("waiter", waiterVo);
		}

		return map;
	}

	@Override
	public List<OptionBo> getHobbysSupOptions() {
		return mongoTemplate.find(new Query(
				Criteria.where("field").is("marriageHobbys").and("supId").is("0").and("deleted").is(Constant.ACTIVITY)),
				OptionBo.class);
	}

	@Override
	public List<OptionBo> getHobbysSonOptions(String id) {
		return mongoTemplate.find(new Query(
				Criteria.where("field").is("marriageHobbys").and("supId").is(id).and("deleted").is(Constant.ACTIVITY)),
				OptionBo.class);
	}
}
