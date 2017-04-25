package com.lad.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.lad.bo.FeedbackBo;
import com.lad.dao.IFeedbackDao;

@Repository("feedbackDao")
public class FeedbakDaoImpl implements IFeedbackDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	public FeedbackBo insert(FeedbackBo feedbackBo) {
		mongoTemplate.insert(feedbackBo);
		return feedbackBo;
	}

}
