package com.lad.dao;

import com.lad.bo.PointBo;

public interface IPointDao extends IBaseDao {

	public PointBo insertUserPoint(PointBo point);

	public PointBo updateUserPoint(PointBo point);

}
