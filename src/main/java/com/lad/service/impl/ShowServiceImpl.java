package com.lad.service.impl;

import com.lad.bo.ShowBo;
import com.lad.dao.ShowDao;
import com.lad.service.IShowService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/28
 */
@Service("showService")
public class ShowServiceImpl implements IShowService {

    @Autowired
    private ShowDao showDao;

    @Override
    public ShowBo insert(ShowBo showBo) {
        showDao.insert(showBo);
        return showBo;
    }

    @Override
    public WriteResult update(String id, Map<String, Object> params) {
        return showDao.update(id, params);
    }

    @Override
    public WriteResult delete(String id) {
        return showDao.deleteById(id);
    }

    @Override
    public WriteResult batchDelete(String... ids) {
        return showDao.batchDeleteByIds(ids);
    }

    @Override
    public List<ShowBo> findByCreateuid(String userid, int type, int page, int limit) {
        return showDao.findByMyShows(userid, type, page, limit);
    }

    @Override
    public List<ShowBo> findByKeyword(String keyword, int type, int page, int limit) {
        return showDao.findByList(keyword, type, page, limit);
    }

    @Override
    public List<ShowBo> findByCircleid(String circleid, int status, int type) {
        return showDao.findByCircleid(circleid, status, type);
    }

    @Override
    public List<ShowBo> findByShowType(String keyword, int type) {
        return showDao.findByShowType(keyword, type);
    }
}
