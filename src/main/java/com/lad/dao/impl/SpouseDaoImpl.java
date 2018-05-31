package com.lad.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.BaseBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.lad.dao.SpouseDao;
import com.mongodb.WriteResult;

@Repository("spouseDao")
public class SpouseDaoImpl implements SpouseDao {
	@Autowired
    private MongoTemplate mongoTemplate;
	
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
}
