package com.lad.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.CareAndPassBo;
import com.lad.dao.CareAndPassDao;
import com.lad.util.Constant;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

@Repository("careAndPassDao")
public class CareAndPassDaoImpl implements CareAndPassDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	
	@Override
	public Map<String, List<String>> findMarriageCareMap(String mainId) {
		return getFindQuery(Constant.CARE, Constant.MARRIAGE, mainId).getRoster();
	}


	@Override
	public Map<String, List<String>> findSpouseCareMap(String mainId) {
		return getFindQuery(Constant.CARE, Constant.SPOUSE, mainId).getRoster();
	}


	@Override
	public Map<String, List<String>> findTravelersCareMap(String mainId) {
		return getFindQuery(Constant.CARE, Constant.TRAVELERS, mainId).getRoster();
	}
	

	
	
	/**
	 * 关注-儿媳
	 */
	@Override
	public CareAndPassBo findMarriageCare(String mainId) {		
		return getFindQuery(Constant.CARE, Constant.MARRIAGE, mainId);
	}

	/**
	 * 关注-老伴
	 */
	@Override
	public CareAndPassBo findSpouseCare(String mainId) {
		return getFindQuery(Constant.CARE, Constant.SPOUSE, mainId);
	}

	/**
	 * 关注-驴友
	 */
	@Override
	public CareAndPassBo findTravelersCare(String mainId) {
		return getFindQuery(Constant.CARE, Constant.TRAVELERS, mainId);
	}

	/**
	 * 黑名单-儿媳
	 */
	@Override
	public CareAndPassBo findMarriagePass(String mainId) {
		return getFindQuery(Constant.PASS, Constant.MARRIAGE, mainId);
	}

	/**
	 * 黑名单-老伴
	 */
	@Override
	public CareAndPassBo findSpousePass(String mainId) {
		return getFindQuery(Constant.PASS, Constant.SPOUSE, mainId);
	}

	/**
	 * 黑名单-驴友
	 */
	@Override
	public CareAndPassBo findTravelersPass(String mainId) {
		return getFindQuery(Constant.PASS, Constant.TRAVELERS, mainId);
	}
	
	/**
	 * 添加一条数据
	 */
	@Override
	public String insert(CareAndPassBo care) {
		mongoTemplate.insert(care);
		return care.getId();
	}
	
	/**
	 * 修改一条数据
	 * @param situation
	 * @param type
	 * @param mainId
	 * @param roster
	 */
	@Override
	public WriteResult update(String situation, String type, String mainId, Map<String, List<String>> roster) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("situation").is(situation),Criteria.where("type").is(type),Criteria.where("mainId").is(mainId),Criteria.where("deleted").is(Constant.ACTIVITY));
		
		Update update = new Update();
		update.set("roster", roster);
		return mongoTemplate.updateFirst(query, update, CareAndPassBo.class);
	}
	
	
	// 设置根据主id查找实体的条件与过滤条件
	private CareAndPassBo getFindQuery(String type,String situation,String mainId){
		BasicDBObject criteria = new BasicDBObject();
		criteria.put("mainId", mainId);
		criteria.put("deleted", Constant.ACTIVITY);
		criteria.put("type", type);
		criteria.put("situation", situation);
		
		BasicDBObject filter = new BasicDBObject();
		filter.put("_id", true);
		filter.put("roster", true);
		Query query = new BasicQuery(criteria,filter);
		return mongoTemplate.findOne(query, CareAndPassBo.class);
	}
	
	@Override
	public String test() {		
		return mongoTemplate.toString();
	}


	
}
