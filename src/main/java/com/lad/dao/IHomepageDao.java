package com.lad.dao;

import com.lad.bo.HomepageBo;
import com.mongodb.WriteResult;

public interface IHomepageDao extends IBaseDao {
	public HomepageBo insert(HomepageBo homepageBo);

	public HomepageBo update_new_visitors_count(HomepageBo homepageBo);

	public HomepageBo update_total_visitors_count(HomepageBo homepageBo);
	
	public HomepageBo update_visitor_ids(HomepageBo homepageBo);

	public HomepageBo selectByUserId(String userId);

	WriteResult updateNewCount(String id, int num);
	
}
