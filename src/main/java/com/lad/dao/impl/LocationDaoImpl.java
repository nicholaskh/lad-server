package com.lad.dao.impl;

import com.lad.bo.LocationBo;
import com.lad.dao.ILocationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("locationDao")
public class LocationDaoImpl implements ILocationDao {
	@Autowired
	MongoTemplate mongoTemplate;
	
	public List<LocationBo> findCircleNear(Point point, double maxDistance) {
		Query query = new Query();
		Distance distance = new Distance(maxDistance/1000, Metrics.KILOMETERS);
		Circle circle = new Circle(point, distance);
		Criteria criteria1 = Criteria.where("position").withinSphere(circle);
		query.addCriteria(criteria1);
		query.limit(50);
		return mongoTemplate.find(query, LocationBo.class);
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
		update.set("updateTime", new Date());
		mongoTemplate.updateFirst(query, update, LocationBo.class);
		return locationBo;
	}

	public LocationBo getLocationBoById(String locationId) {
		Query query = new Query();
		Criteria criteria1 = new Criteria("_id").is(locationId);
		query.addCriteria(new Criteria("deleted").is(0));
		query.addCriteria(criteria1);
		return mongoTemplate.findOne(query, LocationBo.class);
	}

	public LocationBo getLocationBoByUserid(String userid) {
		Query query = new Query();
		Criteria criteria1 = new Criteria("userid").is(userid);
		query.addCriteria(new Criteria("deleted").is(0));
		query.addCriteria(criteria1);
		return  mongoTemplate.findOne(query, LocationBo.class);
	}

	@Override
	public GeoResults<LocationBo> findNearFriends(double[] position, double maxDistance, List<String> friendids) {
		Query query = new Query();
		Point point = new Point(position[0], position[1]);
		Distance distance = new Distance(maxDistance/1000, Metrics.KILOMETERS);
		NearQuery nearQuery = NearQuery.near(point);
		nearQuery.maxDistance(distance);
		query.addCriteria(new Criteria("userid").in(friendids));
		nearQuery.query(query);
		return mongoTemplate.geoNear(nearQuery, LocationBo.class);
	}

	@Override
	public GeoResults<LocationBo> findUserNear(Point point, double maxDistance) {
		Query query = new Query();
		Distance distance = new Distance(maxDistance/1000, Metrics.KILOMETERS);
		NearQuery nearQuery = NearQuery.near(point);
		nearQuery.maxDistance(distance);
		nearQuery.query(query);
		return mongoTemplate.geoNear(nearQuery, LocationBo.class);
	}
}
