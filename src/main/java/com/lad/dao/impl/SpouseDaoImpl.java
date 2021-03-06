package com.lad.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

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

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.lad.bo.BaseBo;
import com.lad.bo.CareAndPassBo;
import com.lad.bo.OptionBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.lad.dao.ISpouseDao;
import com.lad.util.Constant;
import com.lad.vo.SpouseBaseVo;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

@Repository("spouseDao")
@SuppressWarnings("all")
public class SpouseDaoImpl implements ISpouseDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * 查看当前用户下的发布
	 */
	@Override
	public SpouseBaseBo getSpouseByUserId(String uid) {

		BasicDBObject criteria = new BasicDBObject();
		criteria.put("createuid", uid);
		criteria.put("deleted", Constant.ACTIVITY);
		BasicDBObject filter = new BasicDBObject();
		filter.put("pass", false);
		filter.put("care", false);
		filter.put("createuid", false);
		filter.put("updateTime", false);
		Query query = new BasicQuery(criteria, filter);
		return mongoTemplate.findOne(query, SpouseBaseBo.class);
	}

	@Override
	public WriteResult deletePublish(String spouseId) {
		Query query = new Query(Criteria.where("id").is(spouseId));
		Update update = new Update();
		update.set("deleted", 1);
		WriteResult updateFirst = mongoTemplate.updateFirst(query, update, SpouseBaseBo.class);
		return updateFirst;
	}

	@Override
	public List<SpouseBaseBo> getNewSpouse(String sex, int page, int limit, String uid) {

		Query query = new Query();
		Criteria criteria = new Criteria();

		if (sex != null) {
			criteria.andOperator(Criteria.where("deleted").is(Constant.ACTIVITY), Criteria.where("sex").is(sex));
		} else {
			criteria.andOperator(Criteria.where("deleted").is(Constant.ACTIVITY));
		}

		query.addCriteria(criteria);
		query.skip((page - 1) * limit);
		query.limit(limit);
		query.with(new Sort(new Order(Direction.DESC, "createTime")));
		return mongoTemplate.find(query, SpouseBaseBo.class);
	}

	@Override
	public String insert(BaseBo baseBo) {
		mongoTemplate.insert(baseBo);
		return baseBo.getId();
	}

	@Override
	public SpouseBaseBo findBaseById(String baseId) {
		return mongoTemplate.findOne(new Query(Criteria.where("_id").is(baseId).and("deleted").is(Constant.ACTIVITY)),
				SpouseBaseBo.class);
	}

	@Override
	public SpouseRequireBo findRequireById(String baseId) {
		return mongoTemplate.findOne(
				new Query(Criteria.where("baseId").is(baseId).and("deleted").is(Constant.ACTIVITY)),
				SpouseRequireBo.class);
	}

	@Override
	public void test() {
		System.out.println(mongoTemplate);
	}

	@Override
	public WriteResult updateByParams(String spouseId, Map<String, Object> params, Class class1) {
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(spouseId);
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
	public int getNum(String id) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("createuid").is(id), Criteria.where("deleted").is(Constant.ACTIVITY));
		query.addCriteria(criteria);
		return (int) mongoTemplate.count(query, SpouseBaseBo.class);
	}

	@Override
	public List<SpouseBaseBo> findListByKeyword(String keyWord, String sex, int page, int limit, Class clazz) {
		Criteria c = new Criteria();
		c.orOperator(Criteria.where("nickName").regex(".*" + keyWord + ".*"),
				Criteria.where("address").regex(".*" + keyWord + ".*"));
		Criteria criertia = new Criteria();
		if (sex != null) {
			criertia.andOperator(Criteria.where("sex").is(sex), Criteria.where("deleted").is(Constant.ACTIVITY), c);
		} else {
			criertia.andOperator(Criteria.where("deleted").is(Constant.ACTIVITY), c);
		}

		Query query = new Query();
		query.addCriteria(criertia).skip((page - 1) * limit).limit(limit)
				.with(new Sort(new Order(Direction.DESC, "createTime")));
		return mongoTemplate.find(query, clazz);
	}

	@Override
	public List<Map> getRecommend(SpouseRequireBo require, String uid,String baseId) {
		// 查询关于与黑名单
		CareAndPassBo careAndPass = mongoTemplate.findOne(new Query(Criteria.where("mainId").is(baseId)), CareAndPassBo.class);
		Set<String> skipId = new LinkedHashSet<>();
		if(careAndPass!=null){
			// 将黑名单加入跳过列表
			Set<String> passRoster = careAndPass.getPassRoster();
			if(passRoster!=null){
				skipId.addAll(passRoster);
			}
			Map<String, Set<String>> careRoster = careAndPass.getCareRoster();
			if(careRoster!=null){
				for (String key : careRoster.keySet()) {
					skipId.addAll(careRoster.get(key));
				}
			}
		}

		
		// 随机取100个实体
		Query query = new Query(Criteria.where("sex").is(require.getSex()).and("deleted").is(Constant.ACTIVITY)
				.and("createuid").ne(uid).and("_id").nin(skipId));
		int count = (int) mongoTemplate.count(query, SpouseBaseBo.class);
		if(count<100){
			query.with(new Sort(Sort.Direction.DESC,"_id"));
		}else{
			Random r = new Random();
			int length = (count - 99) > 0 ? (count - 99) : 1;
			int skip = r.nextInt(length);
			query.skip(skip);
			query.limit(100);
		}

		List<SpouseBaseBo> find = mongoTemplate.find(query, SpouseBaseBo.class);

		// 年龄要求
		int minAge = 0;
		int maxAge = 150;
		if (require.getAge() != null && !("不限".equals(require.getAge()))) {
			String[] age = require.getAge().split("-");
			minAge = Integer.valueOf(age[0].replaceAll("\\D*", ""));
			maxAge = Integer.valueOf(age[1].replaceAll("\\D+", ""));
		}

		// 月收入要求

		List<OptionBo> optionBos = mongoTemplate.find(
				new Query(Criteria.where("field").is("salary").and("deleted").is(Constant.ACTIVITY)), OptionBo.class);
		int salary = 0;
		if (require.getSalary() != null) {
			for (OptionBo optionBo : optionBos) {
				if (require.getSalary().equals(optionBo.getValue())) {
					salary = optionBo.getSort();
				}
			}
		}

		// 居住地 同省:50分 同市80分 同县100
		String address = "不限";
		if (require.getAddress() != null) {
			address = require.getAddress();
		}

		// 兴趣爱好
		Map<String, Set<String>> myHobbys = require.getHobbys();

		List<Map> list = new ArrayList<>();

		List tempList = new ArrayList<>();
		for (SpouseBaseBo bo : find) {
			if (tempList.contains(bo.getId())) {
				continue;
			}
			// 地址
			int addressNum = 0;
			if (bo.getAddress() != null) {
				if ("不限".equals(bo.getAddress()) || address.equals(bo.getAddress())) {
					addressNum = 100;
				}
			}

			// 兴趣
			int temp = 0;
			int hobbysNum = 0;
			int myHobNum = 0;
			for (Entry<String, Set<String>> myHobby : myHobbys.entrySet()) {
				myHobNum += myHobby.getValue().size();
				Set<String> requireSet = bo.getHobbys().get(myHobby.getKey());
				for (String hob : myHobby.getValue()) {
					if (requireSet.contains(hob)) {
						temp++;
					}
				}
			}

			if (temp == 1) {
				hobbysNum = 60;
			} else if (temp > 1) {
				hobbysNum = temp / myHobNum * 40 + 60;
			}

			// 年龄
			int ageNum = 0;

			if ("不限".equals(require.getAge())) {
				ageNum = 100;
			} else {
				int boage = bo.getAge();

				if (boage >= minAge && boage <= maxAge) {
					ageNum = 100;
				}
				if (boage > maxAge) {
					ageNum = 100 - (boage - maxAge) * 5;
				}
				if (boage < minAge) {
					ageNum = 100 - (minAge - boage) * 5;
				}
			}

			// 月收入
			int salaryNum = 0;

			int boSalary = 0;
			if (bo.getSalary() != null) {
				for (OptionBo string : optionBos) {
					if (bo.getSalary().equals(string)) {
						boSalary = string.getSort();
					}
				}
			}

			if (salary == 0) {
				salaryNum = 100;
			} else if (salary <= boSalary) {
				salaryNum = 100;
			} else if (salary > boSalary) {
				salaryNum = 100 - (salary - boSalary) * 15;
			}

			int match = (int) Math.rint((addressNum + hobbysNum + ageNum + salaryNum) * 0.25);

			tempList.add(bo.getId());
			if (match >= 0) {
				Map map = new HashMap<>();
				map.put("match", match);
				SpouseBaseVo baseVo = new SpouseBaseVo();
				BeanUtils.copyProperties(bo, baseVo);
				map.put("spouseBo", baseVo);
				list.add(map);
			}
		}

		return list;
	}

	@Override
	public int findPublishNum(String uid) {
		return (int) mongoTemplate.count(
				new Query(Criteria.where("createuid").is(uid).and("deleted").is(Constant.ACTIVITY)),
				SpouseBaseBo.class);
	}

	@Override
	public WriteResult updateRequireSex(String requireId, String requireSex, Class clazz) {
		Update update = new Update();
		update.set("sex", requireSex);
		Query query = new Query(Criteria.where("baseId").is(requireId));
		return mongoTemplate.updateFirst(query, update, clazz);
	}

}
