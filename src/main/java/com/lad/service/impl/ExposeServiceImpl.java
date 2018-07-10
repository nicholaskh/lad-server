package com.lad.service.impl;

import com.lad.bo.ExposeBo;
import com.lad.dao.ExposeDao;
import com.lad.service.IExposeService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/22
 */
@Service("exposeService")
public class ExposeServiceImpl implements IExposeService {

    @Autowired
    private ExposeDao exposeDao;

    @Override
    public ExposeBo insert(ExposeBo exposeBo) {
        return exposeDao.insert(exposeBo);
    }

    @Override
    public WriteResult updateExpose(String id, Map<String, Object> params) {
        return exposeDao.updateByParam(id, params);
    }

    @Override
    public List<ExposeBo> findByRegex(String title, List<String> exposeTypes, int page, int limit) {
        return exposeDao.findRegexByPage(title, exposeTypes, page, limit);
    }

    @Override
    public ExposeBo findById(String id) {
        return exposeDao.findById(id);
    }

    @Override
    public WriteResult deleteById(String id) {
        return exposeDao.deleteById(id);
    }

    @Override
    public List<ExposeBo> findByParams(Map<String, Object> params, int page, int limit) {
        return exposeDao.findParamsByPage(params, page, limit);
    }

    @Override
    public WriteResult updateCounts(String id, int numType, int num) {
        return exposeDao.updateCounts(id, numType, num);
    }

	@Override
	public void updateVisitNum(String exposeid, int i) {
		exposeDao.updateVisitNum(exposeid,i);
	}
}
