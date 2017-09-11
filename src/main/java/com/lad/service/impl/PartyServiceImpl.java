package com.lad.service.impl;

import com.lad.bo.PartyBo;
import com.lad.dao.IPartyDao;
import com.lad.service.IPartyService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/7
 */
@Service("partyService")
public class PartyServiceImpl implements IPartyService {

    @Autowired
    private IPartyDao partyDao;

    @Override
    public PartyBo insert(PartyBo partyBo) {
        return partyDao.insert(partyBo);
    }

    @Override
    public WriteResult update(PartyBo partyBo) {
        return partyDao.update(partyBo);
    }

    @Override
    public WriteResult delete(String id) {
        return partyDao.delete(id);
    }

    @Override
    public List<PartyBo> findByCreate(String createid, String start_id, int limit) {
        return partyDao.findByCreate(createid, start_id, limit);
    }

    @Override
    public List<PartyBo> findByMyJoin(String userid, String start_id, int limit) {
        return null;
    }

    @Override
    public PartyBo findById(String id) {
        return partyDao.findById(id);
    }
}
