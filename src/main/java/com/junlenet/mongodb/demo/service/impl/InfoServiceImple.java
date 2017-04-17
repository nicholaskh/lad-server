package com.junlenet.mongodb.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junlenet.mongodb.demo.bo.InfoBo;
import com.junlenet.mongodb.demo.dao.IInfoDao;
import com.junlenet.mongodb.demo.service.IInfoService;

@Service("infoService")
public class InfoServiceImple implements IInfoService {

	@Autowired
	private IInfoDao infoDao;
	
	public InfoBo insert(InfoBo infoBo) {
		return infoDao.insert(infoBo);
	}

	public List<InfoBo> selectByOwnerId(String ownerId) {
		return infoDao.selectByOwnerId(ownerId);
	}

}
