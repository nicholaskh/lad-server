package com.lad.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.lad.bo.BaseBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.lad.dao.SpouseDao;

@Repository("spouseDao")
public class SpouseDaoImpl implements SpouseDao {
	@Autowired
    private MongoTemplate mongoTemplate;

	

	@Override
	public List<String> getCaresList(String baseId, String key) {
		SpouseBaseBo findOne = mongoTemplate.findOne(new Query(Criteria.where("_id").is(baseId)), SpouseBaseBo.class);
		
		List list = new ArrayList<>();
		if("cares".equals(key) && findOne.getCare()!=null){
				list = findOne.getCare();		
		}
		
		if("pass".equals(key) && findOne.getPass()!=null){
			list = findOne.getPass();
		}
		
		
		return null;
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
