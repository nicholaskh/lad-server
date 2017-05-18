package com.lad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.PointBo;
import com.lad.dao.IPointDao;
import com.lad.service.IPointService;

@Service("pointService")
public class PointServiceImpl implements IPointService {

	@Autowired
	private IPointDao pointDao;

	public PointBo insertUserPoint(PointBo point) {
		return pointDao.insertUserPoint(point);
	}

	public PointBo updateUserPoint(PointBo point) {
		return pointDao.updateUserPoint(point);
	}

}
