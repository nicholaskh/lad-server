package com.lad.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.ReadHistoryBo;
import com.lad.dao.IReadHistoryDao;

@Repository("readHistoryDao")
public class ReadHistoryDaoImpl implements IReadHistoryDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public String addReadHistory(ReadHistoryBo historyBo) {
		mongoTemplate.insert(historyBo);
		return historyBo.getId();
	}

	@Override
	public ReadHistoryBo getHistoryByUseridAndNoteId(String userid, String id) {
		return mongoTemplate.findOne(new Query(new Criteria("readerId").is(userid).and("beReaderId").is(id)), ReadHistoryBo.class);
	}

	@Override
	public String updateReadNum(String id,int readNum) {
		Query query = new Query(new Criteria("_id").is(id));
		Update update = new Update();
		update.set("readNum", readNum);
		mongoTemplate.updateFirst(query, update, ReadHistoryBo.class);
		return id;
	}
} 
