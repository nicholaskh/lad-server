package com.lad.dao.impl;

import com.lad.bo.OrganizationBo;
import com.lad.dao.IOrganizationDao;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Repository("organizationDao")
public class OrganizationDaoImpl implements IOrganizationDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	public OrganizationBo insert(OrganizationBo organizationBo) {
		mongoTemplate.insert(organizationBo);
		return organizationBo;
	}

	public OrganizationBo get(String organizationBoId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(organizationBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, OrganizationBo.class);
	}

	public List<OrganizationBo> selectByTag(String tag, String sub_tag) {
		Query query = new Query();
		query.addCriteria(new Criteria("tag").is(tag));
		query.addCriteria(new Criteria("sub_tag").is(sub_tag));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, OrganizationBo.class);
	}

	public WriteResult updateUsers(String organizationBoId,
			HashSet<String> users) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(organizationBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("users", users);
		return mongoTemplate.updateFirst(query, update, OrganizationBo.class);
	}

	public WriteResult updateUsersApply(String organizationBoId,
			HashSet<String> usersApply) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(organizationBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("usersApply", usersApply);
		return mongoTemplate.updateFirst(query, update, OrganizationBo.class);
	}

	public WriteResult updateDescription(String organizationBoId,
			String description) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(organizationBoId));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("description", description);
		return mongoTemplate.updateFirst(query, update, OrganizationBo.class);
	}

	@Override
	public WriteResult updateMutil(OrganizationBo organizationBo) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(organizationBo.getId()));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("masters", organizationBo.getMasters());
		update.set("usersApply", organizationBo.getUsersApply());
		update.set("users", organizationBo.getUsers());
		return mongoTemplate.updateFirst(query, update, OrganizationBo.class);
	}
}
