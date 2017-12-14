package com.lad.service.impl;

import com.lad.bo.ReasonBo;
import com.lad.dao.IReasonDao;
import com.lad.service.IReasonService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/11/27
 */
@Service("reasonService")
public class ReasonServiceImpl implements IReasonService {

    @Autowired
    private IReasonDao reasonDao;

    @Override
    public ReasonBo insert(ReasonBo reasonBo) {
        return reasonDao.insert(reasonBo);
    }

    @Override
    public WriteResult updateApply(String id, int status, String refuse) {
        return reasonDao.updateApply(id, status, refuse);
    }

    @Override
    public ReasonBo findByUserAndCircle(String userid, String circleid, int status) {
        return reasonDao.findByUserAndCircle(userid, circleid, status);
    }

    @Override
    public ReasonBo findById(String id) {
        return reasonDao.findById(id);
    }

    @Override
    public WriteResult deleteById(String id) {
        return reasonDao.deleteById(id);
    }

    @Override
    public List<ReasonBo> findByCircle(String circleid) {
        return reasonDao.findByCircle(circleid);
    }

    @Override
    public ReasonBo findByUserAndChatroom(String userid, String chatroomid) {
        return reasonDao.findByUserAndChatroom(userid, chatroomid);
    }

    @Override
    public List<ReasonBo> findByCircleHis(String circleid, int page, int limit) {
        return reasonDao.findByCircleHis(circleid, page, limit);
    }

    @Override
    public List<ReasonBo> findByChatroomHis(String chatroomid, int page, int limit) {
        return reasonDao.findByChatroomHis(chatroomid, page, limit);
    }

    @Override
    public List<ReasonBo> findByChatroom(String chatroomid) {
        return reasonDao.findByChatroom(chatroomid);
    }

    @Override
    public WriteResult updateMasterApply(String id, int status, boolean isMasterApply) {
        return reasonDao.updateMasterApply(id, status, isMasterApply);
    }


    @Override
    public WriteResult updateUnReadNum(String userid, String circleid, int num) {
        return reasonDao.updateUnReadNum(userid, circleid, num);
    }

    @Override
    public WriteResult updateUnReadNumZero(String id) {
        return reasonDao.updateUnReadNumZero(id);
    }

    @Override
    public WriteResult updateUnReadNumZero(String userid, String circleid) {
        return reasonDao.updateUnReadNumZero(userid, circleid);
    }

    @Override
    public WriteResult updateUnReadNum(HashSet<String> userids, String circleid) {
        return reasonDao.updateUnReadNum(userids, circleid);
    }

    @Override
    public ReasonBo findByUserAdd(String userid, String circleid) {
        return reasonDao.findByUserAdd(userid, circleid);
    }

    @Override
    public WriteResult removeUser(HashSet<String> removeUsers, String circleid) {
        return reasonDao.removeUser(removeUsers, circleid);
    }

    @Override
    public WriteResult removeUser(String userid, String circleid) {
        return reasonDao.removeUser(userid, circleid);
    }
}
