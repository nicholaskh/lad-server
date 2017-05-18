package com.lad.service;

import com.lad.bo.PointBo;

public interface IPointService extends IBaseService {
	public PointBo insertUserPoint(PointBo point);

	public PointBo updateUserPoint(PointBo point);
}
