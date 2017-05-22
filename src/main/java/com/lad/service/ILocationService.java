package com.lad.service;

import java.util.List;

import com.lad.bo.LocationBo;

public interface ILocationService extends IBaseService {
	public LocationBo insertUserPoint(LocationBo locationBo);
	public LocationBo updateUserPoint(LocationBo locationBo);
	public List<LocationBo> findCircleNear(double px, double py, double maxDistance);
	public LocationBo getLocationBoById(String locationId);
	public LocationBo getLocationBoByUserid(String userid);
}
