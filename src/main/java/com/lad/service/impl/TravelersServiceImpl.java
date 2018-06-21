package com.lad.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.BaseBo;
import com.lad.bo.TravelersRequireBo;
import com.lad.dao.ITravelersDao;
import com.lad.service.TravelersService;
import com.mongodb.WriteResult;

@Service("travelersService")
public class TravelersServiceImpl implements TravelersService {

	@Autowired
	private ITravelersDao travelersDao;
	
	@Override
	public List<Map> getRecommend(TravelersRequireBo require) {
		return travelersDao.getRecommend(require);
	}
	
	@Override
	public WriteResult deletePublish(String requireId) {
		return travelersDao.deletePublish(requireId);
	}
	
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

	@Override
	public List<TravelersRequireBo> getNewTravelers(int page, int limit, String id) {
		return travelersDao.getNewTravelers(page, limit, id);
	}

	@Override
	public WriteResult updateByIdAndParams(String requireId, Map<String, Object> params) {
		return travelersDao.updateByIdAndParams(requireId,params);
	}

	@Override
	public List<TravelersRequireBo> findListByKeyword(String keyWord,int page,int limit, Class<TravelersRequireBo> clazz) {
		return travelersDao.findListByKeyword(keyWord,page,limit,clazz);
	}
}
