package com.junlenet.mongodb.demo.service;

import com.junlenet.mongodb.demo.bo.FeedbackBo;

public interface IFeedbackService extends IBaseService {
	public FeedbackBo insert(FeedbackBo feedbackBo);

}
