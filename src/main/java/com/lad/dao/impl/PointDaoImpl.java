package com.lad.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.PointBo;
import com.lad.dao.IPointDao;

@Repository("pointDao")
public class PointDaoImpl implements IPointDao {
	@Autowired
	public MongoTemplate mongoTemplate;

	public PointBo insertUserPoint(PointBo point) {
		mongoTemplate.save(point);
		return point;
	}

	public PointBo updateUserPoint(PointBo point) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(point.getId()));
		Update update = new Update();
		update.set("coordinate", point.getCoordinate());
		mongoTemplate.updateFirst(query, update, PointBo.class);
		return point;
	}

}
