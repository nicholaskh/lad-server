package com.lad.service.impl;

import com.lad.bo.LocationBo;
import com.lad.dao.ILocationDao;
import com.lad.service.ILocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("locationService")
public class LocationServiceImpl implements ILocationService {

	@Autowired
	private ILocationDao locationDao;

	public LocationBo insertUserPoint(LocationBo locationBo) {
		return locationDao.insertUserPoint(locationBo);
	}

	public LocationBo updateUserPoint(LocationBo locationBo) {
		return locationDao.updateUserPoint(locationBo);
	}

	public List<LocationBo> findCircleNear(double px, double py,
			double maxDistance) {
		Point point = new Point(px,py);
		return locationDao.findCircleNear(point, maxDistance);
	}

	public LocationBo getLocationBoById(String locationId) {
		return locationDao.getLocationBoById(locationId);
	}

	public LocationBo getLocationBoByUserid(String userid) {
		return locationDao.getLocationBoByUserid(userid);
	}


	@Override
	public List<LocationBo> findNearFriends(double[] position, double maxDistance, List<String> friendids) {
		return locationDao.findNearFriends(position, maxDistance, friendids);
	}
}
