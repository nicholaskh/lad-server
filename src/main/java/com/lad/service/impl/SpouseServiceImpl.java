package com.lad.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.BaseBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.lad.dao.ISpouseDao;
import com.lad.service.SpouseService;
import com.mongodb.WriteResult;

@Service("spouseService")
public class SpouseServiceImpl implements SpouseService {
	@Autowired
	private ISpouseDao spouseDao;
	

	@Override
	public SpouseBaseBo getSpouseByUserId(String uid) {
		return spouseDao.getSpouseByUserId(uid);
	}
	
	
	/**
	 * 取消发布
	 */
	@Override
	public WriteResult deletePublish(String spouseId) {
		return spouseDao.deletePublish(spouseId);
	}

	/**
	 * 获取最新的发布信息
	 */
	@Override
	public List<SpouseBaseBo> getNewSpouse(String sex,int page,int limit,String uid) {
		return spouseDao.getNewSpouse(sex,page,limit,uid);
	}
	
	@Override
	public WriteResult updateByParams(String spouseId, Map<String, Object> params, Class class1) {	
		return spouseDao.updateByParams(spouseId, params, class1);
	}

	/**
	 * 获取不在推荐列表
	 */
	@Override
	public List<String> getPassList(String spouseId) {
		return spouseDao.getPassList(spouseId);
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

	@Override
	public int getNum(String id) {
		return spouseDao.getNum(id);
	}
	
	@Override
	public void test() {
		spouseDao.test();
	}


	@Override
	public List<SpouseBaseBo> findListByKeyword(String keyWord,String sex,int page,int limit, Class<SpouseBaseBo> clazz) {
		return spouseDao.findListByKeyword(keyWord,sex,page,limit,clazz);
	}


	@Override
	public List<Map> getRecommend(SpouseRequireBo require) {
		return spouseDao.getRecommend(require);
	}




}
