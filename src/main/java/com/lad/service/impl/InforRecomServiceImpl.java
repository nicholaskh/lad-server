package com.lad.service.impl;

import com.lad.bo.*;
import com.lad.dao.*;
import com.lad.service.IInforRecomService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/25
 */
@Service("inforRecomService")
public class InforRecomServiceImpl implements IInforRecomService {

    @Autowired
    private IInforHistoryDao inforHistoryDao;

    @Autowired
    private IInforRecomDao inforRecomDao;

    @Autowired
    private IInforUserReadDao inforUserReadDao;

    @Autowired
    private IInforUserReadHisDao inforUserReadHisDao;
    
    @Autowired
    private IInforGroupRecomDao inforGroupRecomDao;


    @Override
    public InforHistoryBo addInfoHis(InforHistoryBo historyBo) {
        return inforHistoryDao.addInfoHis(historyBo);
    }

    @Override
    public InforHistoryBo findTodayHis(String inforid, Date zeroTime) {
        return inforHistoryDao.findTodayHis(inforid, zeroTime);
    }

    @Override
    public List<InforHistoryBo> findHalfYearHis(String inforid, Date halfYearTime) {
        return inforHistoryDao.findHalfYearHis(inforid, halfYearTime);
    }

    @Override
    public WriteResult deleteHis(String id) {
        return inforHistoryDao.deleteHis(id);
    }

    @Override
    public WriteResult updateHisDayNum(String id, int num) {
        return inforHistoryDao.updateHisDayNum(id, num);
    }

    @Override
    public InforRecomBo addInforRecom(InforRecomBo recomBo) {
        return inforRecomDao.addInforRecom(recomBo);
    }

    @Override
    public InforRecomBo findRecomByInforid(String inforid) {
        return inforRecomDao.findRecomByInforid(inforid);
    }

    @Override
    public WriteResult updateRecomByInforid(String id, int halfNum, int totalNum) {
        return inforRecomDao.updateRecomByInforid(id, halfNum, totalNum);
    }

    @Override
    public List<InforRecomBo> findRecomByTypeAndModule(int type, LinkedHashSet<String> modules) {
        return inforRecomDao.findRecomByTypeAndModule(type, modules);
    }

    @Override
    public List<InforRecomBo> findRecomByType(int type, int limit) {
        return inforRecomDao.findRecomByType(type, limit);
    }

    @Override
    public InforUserReadBo addUserRead(InforUserReadBo userReadBo) {
        return inforUserReadDao.addUserRead(userReadBo);
    }

    @Override
    public InforUserReadBo findUserReadByUserid(String userid) {
        return inforUserReadDao.findUserReadByUserid(userid);
    }

    @Override
    public WriteResult updateUserRead(String id, int type, LinkedHashSet<String> modules) {
        return inforUserReadDao.updateUserRead(id, type, modules);
    }

    @Override
    public WriteResult deleteUserRead(String id) {
        return inforUserReadDao.deleteUserRead(id);
    }

    @Override
    public WriteResult updateUserReadAll(InforUserReadBo userReadBo) {
        return inforUserReadDao.updateUserReadAll(userReadBo);
    }

    @Override
    public InforUserReadHisBo addUserReadHis(InforUserReadHisBo userReadHisBo) {
        return inforUserReadHisDao.addUserReadHis(userReadHisBo);
    }

    @Override
    public InforUserReadHisBo findUserReadHis(String userid, int type, String module, String className,Date halfTime) {
        return inforUserReadHisDao.findUserReadHis(userid, type, module,className, halfTime);
    }

    @Override
    public WriteResult updateUserReadHis(String id, Date currentDate) {
        return inforUserReadHisDao.updateUserReadHis(id, currentDate);
    }

    @Override
    public InforUserReadHisBo findByReadHis(String userid, int type, String module,String className) {
        return inforUserReadHisDao.findByReadHis(userid, type, module, className);
    }

    @Override
    public List<InforUserReadHisBo> findUserReadHisBeforeHalf(String userid, Date halfTime) {
        return inforUserReadHisDao.findUserReadHisBeforeHalf(userid, halfTime);
    }

    @Override
    public InforGroupRecomBo addInforGroup(InforGroupRecomBo groupRecomBo) {
        return inforGroupRecomDao.addInforGroup(groupRecomBo);
    }

    @Override
    public InforGroupRecomBo findInforGroup(String module, String className, int type) {
        return inforGroupRecomDao.findInforGroup(module, className, type);
    }

    @Override
    public InforGroupRecomBo findInforGroup(String module, int type) {
        return inforGroupRecomDao.findInforGroup(module, type);
    }

    @Override
    public List<InforGroupRecomBo> findInforGroupByModule(int type, LinkedHashSet<String> modules) {
        return inforGroupRecomDao.findInforGroupByModule(type, modules);
    }

    @Override
    public List<InforGroupRecomBo> findInforGroupWithoutModule(int type, LinkedHashSet<String> modules, int limit) {
        return inforGroupRecomDao.findInforGroupWithoutModule(type, modules, limit);
    }

    @Override
    public WriteResult updateInforGroup(String id, int halfNum, int totalNum) {
        return inforGroupRecomDao.updateInforGroup(id, halfNum, totalNum);
    }

    @Override
    public WriteResult updateZeroHis(List<String> ids) {
        return inforHistoryDao.updateZeroHis(ids);
    }
}
