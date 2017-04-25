package com.lad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.FeedbackBo;
import com.lad.dao.IFeedbackDao;
import com.lad.service.IFeedbackService;

@Service("feedbackService")
public class FeedbackServiceImpl implements IFeedbackService{

	@Autowired
	private IFeedbackDao feedbackDao;
	
	public FeedbackBo insert(FeedbackBo feedbackBo) {
		return feedbackDao.insert(feedbackBo);
	}

}
