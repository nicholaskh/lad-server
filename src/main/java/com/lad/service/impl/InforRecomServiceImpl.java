package com.lad.service.impl;

import com.lad.bo.InforHistoryBo;
import com.lad.bo.InforRecomBo;
import com.lad.bo.InforUserReadBo;
import com.lad.bo.InforUserReadHisBo;
import com.lad.dao.IInforHistoryDao;
import com.lad.dao.IInforRecomDao;
import com.lad.dao.IInforUserReadDao;
import com.lad.dao.IInforUserReadHisDao;
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


    @Override
    public InforHistoryBo addInfoHis(InforHistoryBo historyBo) {
        return inforHistoryDao.addInfoHis(historyBo);
    }

    @Override
    public InforHistoryBo findTodayHis(String inforid, Date zeroTime) {
        return inforHistoryDao.findTodayHis(inforid, zeroTime);
    }

    @Override
    public WriteResult findHalfYearHis(String inforid, Date halfYearTime) {
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
    public List<InforRecomBo> findRecomByType(int type) {
        return inforRecomDao.findRecomByType(type);
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
    public InforUserReadHisBo addUserReadHis(InforUserReadHisBo userReadHisBo) {
        return inforUserReadHisDao.addUserReadHis(userReadHisBo);
    }

    @Override
    public InforUserReadHisBo findUserReadHis(String userid, int type, String module, Date halfTime) {
        return inforUserReadHisDao.findUserReadHis(userid, type, module, halfTime);
    }

    @Override
    public WriteResult updateUserReadHis(String id, Date currentDate) {
        return inforUserReadHisDao.updateUserReadHis(id, currentDate);
    }
}
