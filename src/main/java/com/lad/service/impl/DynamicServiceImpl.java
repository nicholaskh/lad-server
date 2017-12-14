package com.lad.service.impl;

import com.lad.bo.DynamicBackBo;
import com.lad.bo.DynamicBo;
import com.lad.bo.DynamicNumBo;
import com.lad.dao.IDynamicBackDao;
import com.lad.dao.IDynamicDao;
import com.lad.dao.IDynamicNumDao;
import com.lad.service.IDynamicService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/23
 */
@Service("dynamicService")
public class DynamicServiceImpl implements IDynamicService {

    @Autowired
    private IDynamicDao dynamicDao;

    @Autowired
    private IDynamicBackDao dynamicBackDao;

    @Autowired
    private IDynamicNumDao dynamicNumDao;


    @Override
    public DynamicBo addDynamic(DynamicBo dynamicBo) {
        return dynamicDao.insert(dynamicBo);
    }

    @Override
    public WriteResult deleteDynamic(String id) {
        return dynamicDao.delete(id);
    }

    @Override
    public DynamicBo findDynamicById(String id) {
        return dynamicDao.findById(id);
    }

    @Override
    public WriteResult updateDynamic(String id, int num, int type) {
        return dynamicDao.update(id, num, type);
    }


    @Override
    public List<DynamicBo> findAllFriendsMsg(List<String> friendids, int page, int limit) {
        return dynamicDao.findAllFriendsMsg(friendids, page, limit);
    }

    @Override
    public List<DynamicBo> findOneFriendMsg(String friendid, int page, int limit) {
        return dynamicDao.findAFriendsMsg(friendid, page, limit);
    }

    @Override
    public DynamicBackBo addDynamicBack(DynamicBackBo backBo) {
        return dynamicBackDao.insert(backBo);
    }

    @Override
    public DynamicBackBo findBackByUserid(String userid) {
        return dynamicBackDao.findByUserid(userid);
    }

    @Override
    public WriteResult updateBackNotSee(String id, HashSet<String> notSeeBacks) {
        return dynamicBackDao.updateNotSee(id, notSeeBacks);
    }

    @Override
    public WriteResult updateBackNotAllow(String id, HashSet<String> notAllowBacks) {
        return dynamicBackDao.updateNotAllow(id, notAllowBacks);
    }

    @Override
    public List<DynamicBackBo> findWhoBackMe(String userid) {
        return dynamicBackDao.findWhoBackMe(userid);
    }

    @Override
    public DynamicBo findByMsgid(String msgid) {
        return dynamicDao.findByMsgid(msgid);
    }

    @Override
    public DynamicNumBo addNum(DynamicNumBo numBo) {
        return dynamicNumDao.addNum(numBo);
    }

    @Override
    public DynamicNumBo findNumByUserid(String userid) {
        return dynamicNumDao.findByUserid(userid);
    }

    @Override
    public WriteResult updateNumbers(String id, int addNum) {
        return dynamicNumDao.updateNumbers(id, addNum);
    }
}
