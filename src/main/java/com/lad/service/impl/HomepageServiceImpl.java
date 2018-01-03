package com.lad.service.impl;

import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.HomepageBo;
import com.lad.dao.IHomepageDao;
import com.lad.service.IHomepageService;

@Service("homepageService")
public class HomepageServiceImpl implements IHomepageService {

	@Autowired
	private IHomepageDao homepageDao;

	public HomepageBo insert(HomepageBo homepageBo) {
		return homepageDao.insert(homepageBo);
	}

	public HomepageBo update_new_visitors_count(HomepageBo homepageBo) {
		return homepageDao.update_new_visitors_count(homepageBo);
	}

	public HomepageBo update_total_visitors_count(HomepageBo homepageBo) {
		return homepageDao.update_total_visitors_count(homepageBo);
	}

	public HomepageBo selectByUserId(String userId) {
		return homepageDao.selectByUserId(userId);
	}

	public HomepageBo update_visitor_ids(HomepageBo homepageBo) {
		return homepageDao.update_visitor_ids(homepageBo);
	}

	@Override
	public WriteResult updateNewCount(String id, int num) {
		return homepageDao.updateNewCount(id, num);
	}
}
