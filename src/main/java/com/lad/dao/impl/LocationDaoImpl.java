package com.lad.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.LocationBo;
import com.lad.bo.UserBo;
import com.lad.dao.ILocationDao;

@Repository("locationDao")
public class LocationDaoImpl implements ILocationDao {
	@Autowired
	MongoTemplate mongoTemplate;
	
	public List<LocationBo> findCircleNear(Point point, double maxDistance) {
		Query query = new Query();
		Criteria criteria1 = Criteria.where("position").nearSphere(point);
		query.addCriteria(criteria1);
		query.limit(1000);
		List<LocationBo> list = mongoTemplate.find(query, LocationBo.class);
		return list;
	}

	public LocationBo insertUserPoint(LocationBo locationBo) {
		mongoTemplate.save(locationBo);
		return locationBo;
	}

	public LocationBo updateUserPoint(LocationBo locationBo) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(locationBo.getId()));
		Update update = new Update();
		update.set("position", locationBo.getPosition());
		mongoTemplate.updateFirst(query, update, LocationBo.class);
		return locationBo;
	}

	public LocationBo getLocationBoById(String locationId) {
		Query query = new Query();
		Criteria criteria1 = new Criteria("_id").is(locationId);
		query.addCriteria(new Criteria("deleted").is(0));
		query.addCriteria(criteria1);
		LocationBo locationBo = mongoTemplate.findOne(query, LocationBo.class);
		return locationBo;
	}

	public LocationBo getLocationBoByUserid(String userid) {
		Query query = new Query();
		Criteria criteria1 = new Criteria("userid").is(userid);
		query.addCriteria(new Criteria("deleted").is(0));
		query.addCriteria(criteria1);
		LocationBo locationBo = mongoTemplate.findOne(query, LocationBo.class);
		return locationBo;
	}
}
