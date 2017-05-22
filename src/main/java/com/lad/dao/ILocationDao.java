package com.lad.dao;

import java.util.List;

import org.springframework.data.geo.Point;

import com.lad.bo.LocationBo;

public interface ILocationDao extends IBaseDao {

	public LocationBo insertUserPoint(LocationBo locationBo);
	public LocationBo updateUserPoint(LocationBo locationBo);
	public List<LocationBo> findCircleNear(Point point, double maxDistance);
	public LocationBo getLocationBoById(String locationId);
	public LocationBo getLocationBoByUserid(String userid);
}
