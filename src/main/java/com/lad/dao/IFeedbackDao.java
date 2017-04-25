package com.lad.dao;

import com.lad.bo.FeedbackBo;

public interface IFeedbackDao  extends IBaseDao {
	public FeedbackBo insert(FeedbackBo feedbackBo);

}
