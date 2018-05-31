package com.lad.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.BaseBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.lad.dao.SpouseDao;
import com.lad.service.SpouseService;
import com.mongodb.WriteResult;

@Service("spouseService")
public class SpouseServiceImpl implements SpouseService {
	@Autowired
	private SpouseDao spouseDao;

	@Override
	public void test() {
		spouseDao.test();
	}

	
	/**
	 * 添加找老伴消息
	 */
	@Override
	public String insert(BaseBo baseBo) {
		
		return spouseDao.insert(baseBo);
	}

	/**
	 * 查看找老伴基础资料
	 */
	@Override
	public SpouseBaseBo findBaseById(String baseId) {
		return spouseDao.findBaseById(baseId);
	}


	@Override
	public SpouseRequireBo findRequireById(String baseId) {
		return spouseDao.findRequireById(baseId);
	}





	@Override
	public Map<String, List> getCareMap(String spouseId) {
		return spouseDao.getCareMap(spouseId);
	}


	@Override
	public WriteResult updateCare(String spouseId, Map<String, List> map) {
		return spouseDao.updateCare(spouseId, map);
	}



}
