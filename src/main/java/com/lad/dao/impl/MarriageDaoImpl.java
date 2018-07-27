package com.lad.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.util.StringUtils;

import com.lad.bo.BaseBo;
import com.lad.bo.CareAndPassBo;
import com.lad.bo.OptionBo;
import com.lad.bo.RequireBo;
import com.lad.bo.WaiterBo;
import com.lad.dao.IMarriageDao;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.vo.OptionVo;
import com.lad.vo.WaiterVo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository("marriageDao")
@SuppressWarnings("all")
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
		dbObject.put("deleted", 0); // 查询条件

		BasicDBObject fieldsObject = new BasicDBObject();
		// 指定返回的字段
		fieldsObject.put("value", true);
		fieldsObject.put("field", true);
		fieldsObject.put("sort", true);
		fieldsObject.put("_id", true);
		fieldsObject.put("supId", true);

		Query query = new BasicQuery(dbObject, fieldsObject);
		query.with(new Sort(Sort.Direction.ASC, "sort"));
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
		query.with(new Sort(new Order(Direction.DESC, "createTime")));
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

		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));

		return mongoTemplate.find(query, WaiterBo.class);
	}

	@Override
	public List<WaiterBo> getGirlsByUserId(String userId) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("createuid").is(userId), Criteria.where("deleted").is(0),
				Criteria.where("sex").is(0));
		query.addCriteria(criteria);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
		return mongoTemplate.find(query, WaiterBo.class);
	}

	/**
	 * 推荐
	 */
	public List<Map> getRecommend(String waiterId, String uid) {

		// 查询关于与黑名单
		CareAndPassBo careAndPass = mongoTemplate.findOne(new Query(Criteria.where("mainId").is(waiterId)),
				CareAndPassBo.class);
		Set<String> skipId = new LinkedHashSet<>();
		if (careAndPass != null) {
			// 将黑名单加入跳过列表
			Set<String> passRoster = careAndPass.getPassRoster();
			if (passRoster != null) {
				skipId.addAll(passRoster);
			}
			Map<String, Set<String>> careRoster = careAndPass.getCareRoster();
			if (careRoster != null) {
				for (String key : careRoster.keySet()) {
					skipId.addAll(careRoster.get(key));
				}
			}
		}

		// 查找当前匹配者的需求
		Query waiter = new Query(Criteria.where("waiterId").is(waiterId).and("deleted").is(Constant.ACTIVITY));
		RequireBo requireBo = mongoTemplate.findOne(waiter, RequireBo.class);

		// 随机取100个实体
		Query query = new Query(Criteria.where("sex").is(requireBo.getSex()).and("deleted").is(Constant.ACTIVITY)
				.and("createuid").ne(uid).and("waiterId").nin(skipId));

		int count = (int) mongoTemplate.count(query, WaiterBo.class);
		List<Map> result = new ArrayList<>();
		List tempList = new ArrayList<>();

		// 不足20条数据重新循环,如果数据库数据量是在不足,则返回
		/*
		 * int x = 1; while (result.size() < 20 && tempList.size() < count) {
		 * List<WaiterBo> find = CommonUtil.randomQuery(mongoTemplate, query,
		 * WaiterBo.class); Logger logger =
		 * LoggerFactory.getLogger(MarriageDaoImpl.class);
		 * logger.error("{第"+x+"从从数据库获取数据,随机抓取的25条消息}"); x++; for (WaiterBo
		 * waiterBo : find) { if (tempList.contains(waiterBo.getId())) {
		 * continue; } Map map = getMatch(requireBo, waiterBo); if
		 * (!StringUtils.isEmpty(map)) { result.add(map); } if (result.size() >=
		 * 20) { break; } tempList.add(waiterBo.getId()); } }
		 */
		List<WaiterBo> find = CommonUtil.randomQuery(mongoTemplate, query, WaiterBo.class);
		for (WaiterBo waiterBo : find) {
			if (tempList.contains(waiterBo.getId())) {
				continue;
			}
			Map map = getMatch(requireBo, waiterBo);
			if (!StringUtils.isEmpty(map)) {
				result.add(map);
			}
			if (result.size() >= 20) {
				break;
			}
			tempList.add(waiterBo.getId());
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
	private Map getMatch(RequireBo requireBo, WaiterBo waiterBo) {
		/**
		 * 总匹配度 = 基础分(40分)+其他分(60分) 基础分 = 地址(20分)+婚史(20分) 其他分 =
		 * 工作(0.1)+兴趣(0.1)+学历(0.1)+收入(0.3)+身高(0.3)+年龄(0.1)
		 */

		int matchNum = 100;
		// 基础条件匹配
		String requireAddress = requireBo.getNowin();
		String waiterAddress = waiterBo.getNowin();
		if (!StringUtils.isEmpty(requireAddress) && !StringUtils.isEmpty(waiterAddress)) {
			if (!"不限".equals(requireAddress) && !requireAddress.equals(waiterAddress)) {
				matchNum -= 20;
			}
		}

		int requireMarriaged = requireBo.getMarriaged();
		int WaiterMarriaged = waiterBo.getMarriaged();
		if (requireMarriaged != 0 && requireMarriaged != WaiterMarriaged) {
			matchNum -= 20;
		}

		// 其他条件匹配
		// 工作匹配
		Set<String> requireJob = requireBo.getJob();
		String waiterJob = waiterBo.getJob();
		if (!StringUtils.isEmpty(requireJob) && !StringUtils.isEmpty(waiterJob)) {
			if (!requireJob.contains("不限") && !requireJob.contains(waiterJob)) {
				matchNum -= 6;
			}
		}

		// 兴趣匹配
		Map<String, Set<String>> requireHobbys = requireBo.getHobbys();
		Map<String, Set<String>> waiterHobbys = waiterBo.getHobbys();

		Set<String> requireHobbysSet = new LinkedHashSet<>();
		Logger logger = LoggerFactory.getLogger(MarriageDaoImpl.class);

		for (String key : requireHobbys.keySet()) {
			requireHobbysSet.addAll(requireHobbys.get(key));
		}
		logger.error("{找儿媳匹配推荐}----{" + requireHobbysSet.toString() + "}");
		Set<String> waiterHobbysSet = new LinkedHashSet<>();
		if (requireHobbysSet.size() > 0) {
			for (String key : waiterHobbys.keySet()) {
				waiterHobbysSet.addAll(waiterHobbys.get(key));
			}
			// 未匹配集合
			Set<String> notContain = new LinkedHashSet<>(requireHobbysSet);
			for (String string : waiterHobbysSet) {
				if (requireHobbysSet.contains(string)) {
					notContain.remove(string);
				}
			}

			matchNum -= Math.floor(notContain.size() * 6 / requireHobbysSet.size());
		}
		// 学历匹配
		int requireEducation = requireBo.getEducation();
		int waiterEducation = waiterBo.getEducation();
		if (waiterEducation == 0) {
			matchNum -= 6;
		} else if (requireEducation != 0 && requireEducation > waiterEducation) {
			matchNum -= 6;
		}

		// 收入匹配,高位不限,如果最高收入低于最低要求,则不匹配
		String regex = "\\D+";
		int minSalaryRequire = 0;
		String requireSalary = requireBo.getSalary();
		if ("3000元以下".equals(requireSalary) || "不限".equals(requireSalary)) {
			minSalaryRequire = 0;
		} else if ("25000元以上".equals(requireSalary)) {
			minSalaryRequire = 25000;
		} else {
			String[] split = requireSalary.split("-");
			minSalaryRequire = Integer.valueOf(split[0].replaceAll(regex, ""));
		}
		int maxSalaryProvide = 0;
		String waiterSalary = waiterBo.getSalary();
		if ("3000元以下".equals(waiterSalary) || "不限".equals(waiterSalary)) {
			maxSalaryProvide = 3000;
		} else if ("25000元以上".equals(waiterSalary)) {
			maxSalaryProvide = 250000;
		} else {
			String[] split = waiterSalary.split("-");
			maxSalaryProvide = Integer.valueOf(split[1].replaceAll(regex, ""));
		}
		if (maxSalaryProvide < minSalaryRequire) {
			matchNum -= 18;
		}

		// 身高匹配 请求者身高小于最低要求则减分,大于最高要求减分
		// 修改过需求,不限更改为xx米以上或xx米以下,对应数据库数据为100厘米-xx厘米,xx厘米-250厘米
		// minhr: minHightRequire 最低要求
		int minhr = Integer.valueOf(requireBo.getHight().split("-")[0].replaceAll(regex, ""));
		// maxhr: maxHightRequire 最高要求
		int maxhr = Integer.valueOf(requireBo.getHight().split("-")[1].replaceAll(regex, ""));
		// wh = waiterHight
		int wh = waiterBo.getHight();
		if (wh < minhr || wh > maxhr) {
			matchNum -= 18;
		}

		// 年龄匹配,同上
		int minar = Integer.valueOf(requireBo.getAge().split("-")[0].replaceAll(regex, ""));
		int maxar = Integer.valueOf(requireBo.getAge().split("-")[1].replaceAll(regex, ""));
		int wa = waiterBo.getAge();
		if (wa < minar || wa > maxar) {
			matchNum -= 6;
		}

		Map map = new HashMap<>();
		WaiterVo waiterVo = new WaiterVo();
		BeanUtils.copyProperties(waiterBo, waiterVo);
		if (matchNum > 60) {
			map.put("match", matchNum);
			map.put("waiter", waiterVo);
			return map;
		}
		return null;
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

	/**
	 * 查询所有职位选项,添加伪数据时使用
	 * 
	 * @return
	 */
	@Override
	public List<OptionBo> getJobOptions() {
		return mongoTemplate.find(new Query(Criteria.where("field").is("job").and("deleted").is(Constant.ACTIVITY)),
				OptionBo.class);
	}

	@Override
	public List<OptionBo> getSalaryOptions() {
		return mongoTemplate.find(new Query(Criteria.where("field").is("salary").and("deleted").is(Constant.ACTIVITY)),
				OptionBo.class);
	}

	/**
	 * 根据条件查询,添加模拟数据是是用那个
	 * 
	 * @param criteria
	 * @return
	 */
	@Override
	public List<WaiterBo> findUserCriteria(Criteria criteria) {
		Query query = new Query(criteria);
		return mongoTemplate.find(query, WaiterBo.class);
	}

	@Override
	public WriteResult deleteMany(Criteria criteria, Class clazz) {
		return mongoTemplate.remove(new Query(criteria), clazz);
	}

	@Override
	public WaiterBo findWaiterByNickName(String nickName, String uid) {
		return mongoTemplate.findOne(new Query(
				Criteria.where("nickName").is(nickName).and("createuid").is(uid).and("deleted").is(Constant.ACTIVITY)),
				WaiterBo.class);
	}
}
