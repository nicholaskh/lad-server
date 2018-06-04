package com.lad.service.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.BaseBo;
import com.lad.bo.OptionBo;
import com.lad.bo.RequireBo;
import com.lad.bo.WaiterBo;
import com.lad.dao.IMarriageDao;
import com.lad.service.IMarriageService;
import com.lad.vo.OptionVo;
import com.lad.vo.RequireVo;
import com.lad.vo.WaiterVo;
import com.mongodb.WriteResult;
@Service("marriageService")
public class MarriageServiceImpl implements IMarriageService {
	
	@Autowired
	public IMarriageDao marriageDao;
	
	@Override
	public int findPublishNum(String id) {
		return marriageDao.findPublishNum(id);
	}
	
	@Override
	public List<WaiterBo> getNewPublish(int type, int page, int limit,String userId) {
		return marriageDao.getNewPublic(type, page, limit,userId);
	}
	
	@Override
	public List<OptionBo> getOptions() {
		return marriageDao.getOptions();
	}
	
	@Override
	public String insertPublish(BaseBo bb) {
		return marriageDao.insertPublish(bb);
	}

	@Override
	public List<String> getUnrecommendList(String waiterId) {
		return marriageDao.getUnrecommendList(waiterId);
	}
	
	@Override
	public WaiterBo findWaiterById(String caresId) {
		return marriageDao.findWaiterById(caresId);
	}
	
	@Override
	public RequireBo findRequireById(String waiterId) {
		return marriageDao.findRequireById(waiterId);
	}
	
	@Override
	public List<String> getPassList(String waiterId) {
		return marriageDao.getPassList(waiterId);
	}
	
	@Override
	public WriteResult deletePublish(String pubId) {
		return marriageDao.deletePublish(pubId);
	}
	@Override
	public WriteResult updateByParams(String id, Map<String, Object> params, Class class1) {
		return marriageDao.updateByParams(id,params,class1);
	}

	@Override
	public List<WaiterBo> getPublishById(String userId) {
		return marriageDao.getPublishById(userId);
	}

	@Override
	public Map<String, Object> getPublishDescById(String WaiterId) {
		return null;
	}

//	@Override
//	public String insertPublish(WaiterBo wb, RequireBo rb) {
//		String rbid = marriageDao.insert(rb);
//		wb.setRequireId(rbid);
//		String wbid = marriageDao.insert(wb);
//		
//		return wbid;
//	}

	@Override
	public WriteResult updateWaiter(WaiterVo wv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WriteResult updateRequire(RequireVo rv) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<OptionBo> getOptions(OptionVo ov) {
		return marriageDao.getOptions(ov);
	}

	@Override
	public List<Map> getRecommend(String waiterId) {
		return marriageDao.getRecommend(waiterId);
	}

	@Override
	public List<WaiterBo> addUnRecommend(String WaiterId, String unRecommendId) {
		return null;
	}

	@Override
	public List<WaiterBo> addCare(String WaiterId, String CareId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WriteResult deleteCare(String WaiterId, String CareId) {
		return null;
	}

	@Override
	public List<WaiterBo> getCares(String WaiterId) {
		return null;
	}

	@Override
	public List<String> insertImage(File[] image) {
		return null;
	}

	@Override
	public String getNickName() {
		return null;
	}

	@Override
	public Map<String, List> getCareMap(String waiterId) {
		
		return marriageDao.getCareMap(waiterId);
	}

	@Override
	public WriteResult updateCare(String waiterId, Map<String, List> map) {
		return marriageDao.updateCare(waiterId, map);
		
	}




















}
