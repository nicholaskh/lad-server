package com.lad.dao;

import com.lad.bo.LocationBo;
import org.springframework.data.geo.Point;

import java.util.List;

public interface ILocationDao extends IBaseDao {

	public LocationBo insertUserPoint(LocationBo locationBo);
	public LocationBo updateUserPoint(LocationBo locationBo);
	public List<LocationBo> findCircleNear(Point point, double maxDistance);
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
