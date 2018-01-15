package com.lad.service;

import com.lad.bo.LocationBo;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;

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
	GeoResults<LocationBo> findNearFriends(double[] position, double maxDistance, List<String> friendids);

	/**
	 * 查找附近人员信息
	 * @param point
	 * @param maxDistance
	 * @return
	 */
	GeoResults<LocationBo> findUserNear(Point point, double maxDistance);


}
