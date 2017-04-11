package com.junlenet.mongodb.demo.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.junlenet.mongodb.demo.bo.HomepageBo;
import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.dao.IHomepageDao;

@Repository("homepageDao")
public class HomepageDaoImpl implements IHomepageDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	public HomepageBo insert(HomepageBo homepageBo) {
		mongoTemplate.insert(homepageBo);
		return homepageBo;
	}

	public HomepageBo update_new_visitors_count(HomepageBo homepageBo) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(homepageBo.getId()));
		Update update = new Update();
		update.set("new_visitors_count", homepageBo.getNew_visitors_count());
		mongoTemplate.updateFirst(query, update, HomepageBo.class);
		return homepageBo;
	}

	public HomepageBo update_total_visitors_count(HomepageBo homepageBo) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(homepageBo.getId()));
		Update update = new Update();
		update.set("total_visitors_count", homepageBo.getTotal_visitors_count());
		mongoTemplate.updateFirst(query, update, HomepageBo.class);
		return homepageBo;
	}

	public HomepageBo selectByUserId(String userId) {
		Query query = new Query();
		query.addCriteria(new Criteria("owner_id").is(userId));
		return mongoTemplate.findOne(query, HomepageBo.class);
	}

	public HomepageBo update_visitor_ids(HomepageBo homepageBo) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(homepageBo.getId()));
		Update update = new Update();
		update.set("visitor_ids", homepageBo.getVisitor_ids());
		mongoTemplate.updateFirst(query, update, HomepageBo.class);
		return homepageBo;
	}

}
