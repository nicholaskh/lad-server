package com.lad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.BaseBo;
import com.lad.bo.TravelersBaseBo;
import com.lad.bo.TravelersRequireBo;
import com.lad.dao.ITravelersDao;
import com.lad.service.TravelersService;

@Service("travelersService")
public class TravelersServiceImpl implements TravelersService {

	@Autowired
	private ITravelersDao travelersDao;
	/**
	 * 查看当前用户发布条数
	 */
	@Override
	public int findPublishNum(String id) {
		return travelersDao.findPublishNum(id);
	}

	/**
	 * 查询一条发布请求
	 */
	@Override
	public TravelersRequireBo getRequireById(String requireId) {
		
		return travelersDao.getRequireById(requireId);
	}

	@Override
	public List<TravelersRequireBo> getRequireList(String id) {
		return travelersDao.getRequireList(id);
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
