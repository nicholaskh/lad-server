package com.lad.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.BaseBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.lad.bo.WaiterBo;
import com.lad.dao.ISpouseDao;
import com.lad.util.Constant;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

@Repository("spouseDao")
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
		Query query =new BasicQuery(criteria,filter);
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
	public List<SpouseBaseBo> getNewSpouse(String sex,int page,int limit,String uid) {
		
		Query query = new Query();
		Criteria criteria = new Criteria();
		Date date = new Date();
		long time = date.getTime()-7*24*60*60*1000;
		Date weekBefore = new Date(time);
		criteria.andOperator(Criteria.where("deleted").is(0),Criteria.where("createTime").gt(weekBefore),Criteria.where("sex").is(sex),Criteria.where("createuid").ne(uid));
		query.addCriteria(criteria);
		System.out.println((page - 1) * limit);
		query.skip((page - 1) * limit);
		query.limit(limit);
		List<SpouseBaseBo> list = mongoTemplate.find(query, SpouseBaseBo.class);
		return list;		
	}
	
	@Override
	public List<String> getPassList(String spouseId) {
		SpouseBaseBo findOne = mongoTemplate.findOne(new Query(Criteria.where("_id").is(spouseId)), SpouseBaseBo.class);
		return findOne.getPass();
	}
	
	@Override
	public WriteResult updateCare(String spouseId, Map<String, List> map) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(spouseId);
        query.addCriteria(criteria);       
        Update update = new Update();
        
        update.set("care", map);
        WriteResult updateFirst = mongoTemplate.updateFirst(query, update, SpouseBaseBo.class);
        return updateFirst;
	}

	@Override
	public Map<String, List> getCareMap(String spouseId) {
		SpouseBaseBo findOne = mongoTemplate.findOne(new Query(Criteria.where("_id").is(spouseId)), SpouseBaseBo.class);
		return findOne.getCare();
	}



	@Override
	public String insert(BaseBo baseBo) {
		mongoTemplate.insert(baseBo);
		return baseBo.getId();
	}

	@Override
	public SpouseBaseBo findBaseById(String baseId) {
		return mongoTemplate.findOne(new Query(Criteria.where("_id").is(baseId)), SpouseBaseBo.class);
	}

	@Override
	public SpouseRequireBo findRequireById(String baseId) {
		return mongoTemplate.findOne(new Query(Criteria.where("baseId").is(baseId)), SpouseRequireBo.class);
	}


	@Override
	public void test() {
		System.out.println(mongoTemplate);
	}

	@Override
	public WriteResult updateByParams(String spouseId, Map<String, Object> params, Class class1) {
		System.out.println(class1);
		
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
		criteria.andOperator(Criteria.where("createuid").is(id),Criteria.where("deleted").is(Constant.ACTIVITY));
		query.addCriteria(criteria);
		return (int)mongoTemplate.count(query, SpouseBaseBo.class);
	}

	@Override
	public List<SpouseBaseBo> findListByKeyword(String keyWord,Class clazz) {
		Criteria c = new Criteria();
		c.orOperator(Criteria.where("nickName").regex( ".*"+keyWord+".*"),Criteria.where("address").regex(".*"+keyWord+".*"));
		Criteria criertia = new Criteria();
		criertia.andOperator(Criteria.where("deleted").is(Constant.ACTIVITY),c);
		Query query = new Query();
		query.addCriteria(criertia);
		return mongoTemplate.find(query, clazz);
	}
}
