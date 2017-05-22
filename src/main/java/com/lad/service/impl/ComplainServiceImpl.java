package com.lad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.ComplainBo;
import com.lad.dao.IComplainDao;
import com.lad.service.IComplainService;

@Service("complainService")
public class ComplainServiceImpl implements IComplainService {

	@Autowired
	private IComplainDao complainDao;
	
	public ComplainBo insert(ComplainBo complainBo) {
		return complainDao.insert(complainBo);
	}

}
