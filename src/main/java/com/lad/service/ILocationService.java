package com.lad.service;

import com.lad.bo.LocationBo;

import java.util.List;

public interface ILocationService extends IBaseService {
	public LocationBo insertUserPoint(LocationBo locationBo);
	public LocationBo updateUserPoint(LocationBo locationBo);
	public List<LocationBo> findCircleNear(double px, double py, double maxDistance);
	public LocationBo getLocationBoById(String locationId);
	public LocationBo getLocationBoByUserid(String userid);


	/**
	 * 查找附近好友
	 * @param position
	 * @param friendids
	 * @return
	 */
	List<LocationBo> findNearFriends(double[] position, double maxDistance, List<String> friendids);
}
