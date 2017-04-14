package com.junlenet.mongodb.demo.dao;

import com.junlenet.mongodb.demo.bo.FeedbackBo;

public interface IFeedbackDao  extends IBaseDao {
	public FeedbackBo insert(FeedbackBo feedbackBo);

}
