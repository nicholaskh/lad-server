package com.lad.service.impl;

import com.lad.bo.CityBo;
import com.lad.dao.ICityDao;
import com.lad.service.ICityService;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/6
 */
@Service("cityService")
public class CityServiceImpl implements ICityService {

    @Autowired
    private ICityDao cityDao;

    @Override
    public List<CityBo> findAllCitys() {
        return cityDao.findAllCitys();
    }

    @Override
    public List<CityBo> findByParams(String province, String city, String distrit) {
        return cityDao.findByParams(province, city, distrit);
    }

    @Override
    public List<CityBo> findByParams(String province, String distrit) {
        return cityDao.findByParams(province, distrit);
    }

    @Override
    public List<BasicDBObject> findProvince() {
        return cityDao.findProvince();
    }

    @Override
    public List<BasicDBObject> findCitys(String province) {
        return cityDao.findCitys(province);
    }

	@Override
	public List<String> getProvince() {
		return cityDao.getProvince();
	}

	@Override
	public List<String> getCity(String provice) {
		return cityDao.getCity(provice);
	}

	@Override
	public List<String> getDistrit(String city) {
		return cityDao.getDistrit(city);
	}
}
