package com.lad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.BaseBo;
import com.lad.bo.TravelersBaseBo;
import com.lad.dao.ITravelersDao;
import com.lad.service.TravelersService;
import com.lad.vo.TravelersRequireVo;

@Service("travelersService")
public class TravelersServiceImpl implements TravelersService {

	@Autowired
	private ITravelersDao travelersDao;
	

	@Override
	public TravelersRequireVo getRequireByBaseId(String id) {
		return travelersDao.getRequireByBaseId(id);
	}
	
	
	/**
	 * 根据用户id查找他的发布
	 */
	@Override
	public List<TravelersBaseBo> getTravelersByUserId(String id) {
		return travelersDao.getTravelersByUserId(id);
	}
	
	/**
	 * 向数据库插入一条发布
	 */
	@Override
	public String insert(BaseBo baseBo) {
		return travelersDao.insert(baseBo);
	}

	
	@Override
	public void test() {
		travelersDao.test();
	}






}
