package com.lad.service.impl;

import com.lad.bo.ReasonBo;
import com.lad.dao.IReasonDao;
import com.lad.service.IReasonService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public ReasonBo findByUserAndCircle(String userid, String circleid) {
        return reasonDao.findByUserAndCircle(userid, circleid);
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
}
