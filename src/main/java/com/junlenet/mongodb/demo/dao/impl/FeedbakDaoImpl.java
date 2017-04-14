package com.junlenet.mongodb.demo.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.junlenet.mongodb.demo.bo.FeedbackBo;
import com.junlenet.mongodb.demo.dao.IFeedbackDao;

@Repository("feedbackDao")
public class FeedbakDaoImpl implements IFeedbackDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	public FeedbackBo insert(FeedbackBo feedbackBo) {
		mongoTemplate.insert(feedbackBo);
		return feedbackBo;
	}

}
