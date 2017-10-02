package com.lad.service.impl;

import com.lad.bo.PartyBo;
import com.lad.bo.PartyUserBo;
import com.lad.dao.IPartyDao;
import com.lad.dao.IPartyUserDao;
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

    @Autowired
    private IPartyUserDao partyUserDao;

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
    public List<PartyBo> findByCreate(String createid, int page, int limit) {
        return partyDao.findByCreate(createid, page, limit);
    }

    @Override
    public List<PartyBo> findByMyJoin(String userid, int page, int limit) {
        return partyDao.findByMyJoin(userid, page, limit);
    }

    @Override
    public PartyBo findById(String id) {
        return partyDao.findById(id);
    }

    @Override
    public List<PartyBo> findByMyApply(String userid, int page, int limit) {
        return partyDao.findByMyApply(userid, page, limit);
    }

    @Override
    public WriteResult updateUser(String id, List<String> users) {
        return partyDao.updateUser(id, users);
    }

    @Override
    public WriteResult updateVisit(String id) {
        return partyDao.updateVisit(id);
    }

    @Override
    public WriteResult updateShare(String id, int num) {
        return partyDao.updateShare(id, num);
    }

    @Override
    public WriteResult updateCollect(String id, int num) {
        return partyDao.updateCollect(id, num);
    }

    @Override
    public WriteResult updateReport(String id, int num) {
        return partyDao.updateReport(id, num);
    }

    @Override
    public PartyUserBo addParty(PartyUserBo partyUserBo) {
        return partyUserDao.insert(partyUserBo);
    }

    @Override
    public List<PartyUserBo> findPartyUser(String partyid, int status) {
        return partyUserDao.findByParty(partyid, status);
    }
}
