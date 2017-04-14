package com.junlenet.mongodb.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junlenet.mongodb.demo.bo.FeedbackBo;
import com.junlenet.mongodb.demo.dao.IFeedbackDao;
import com.junlenet.mongodb.demo.service.IFeedbackService;

@Service("feedbackService")
public class FeedbackServiceImpl implements IFeedbackService{

	@Autowired
	private IFeedbackDao feedbackDao;
	
	public FeedbackBo insert(FeedbackBo feedbackBo) {
		return feedbackDao.insert(feedbackBo);
	}

}
