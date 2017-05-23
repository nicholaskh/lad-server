package com.lad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.IMTermBo;
import com.lad.dao.IIMTermDao;
import com.lad.service.IIMTermService;
import com.mongodb.WriteResult;

@Service("iMTermService")
public class IMTermServiceImpl implements IIMTermService {

	@Autowired
	private IIMTermDao iMTermDao;
	
	public IMTermBo insert(IMTermBo iMTermBo) {
		return iMTermDao.insert(iMTermBo);
	}

	public IMTermBo selectByUserid(String userid) {
		return iMTermDao.selectByUserid(userid);
	}

	public WriteResult updateByUserid(String userid, String term) {
		return iMTermDao.updateByUserid(userid, term);
	}

}
