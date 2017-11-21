package com.lad.service.impl;

import com.lad.bo.InforReadNumBo;
import com.lad.bo.InforSubscriptionBo;
import com.lad.dao.*;
import com.lad.scrapybo.BroadcastBo;
import com.lad.scrapybo.InforBo;
import com.lad.scrapybo.SecurityBo;
import com.lad.scrapybo.VideoBo;
import com.lad.service.IInforService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/5
 */
@Service("inforSerivce")
public class InforSerivceImpl implements IInforService {

    @Autowired
    private IInforReadNumDao inforReadNumDao;

    @Autowired
    private IInforSubDao inforSubDao;

    @Autowired
    private IInforDao inforDao;

    @Autowired
    private ISecurityDao securityDao;

    @Autowired
    private IBroadcastDao broadcastDao;

    @Autowired
    private IVideoDao videoDao;

    @Override
    public List<InforBo> findAllGroups() {
        return inforDao.selectAllInfos();
    }

    @Override
    public List<InforBo> findGroupInfos(String module) {
        return null;
    }

    @Override
    public List<InforBo> findGroupClass(String className) {
        return null;
    }

    @Override
    public List<InforBo> findClassInfos(String className, String createTime, int limit) {
        return inforDao.findByList(className, createTime, limit);
    }

    @Override
    public InforBo findById(String id) {
        return inforDao.findById(id);
    }

    @Override
    public InforSubscriptionBo findMySubs(String userid) {
        return inforSubDao.findByUserid(userid);
    }

    @Override
    public void subscriptionGroup(String groupName, boolean isAdd) {

    }

    @Override
    public List<InforSubscriptionBo> findMyCollects(String userid) {
        return null;
    }

    @Override
    public void collectInfor(String id, boolean isAdd) {

    }

    @Override
    public Long findReadNum(String inforid) {
        InforReadNumBo readNumBo = inforReadNumDao.findByInforid(inforid);
        if (readNumBo != null) {
            return readNumBo.getVisitNum();
        }
        return 0L;
    }

    public InforReadNumBo findReadByid(String inforid){
       return inforReadNumDao.findByInforid(inforid);
    }

    @Override
    public InforSubscriptionBo insertSub(InforSubscriptionBo inforSubscriptionBo) {
        return inforSubDao.insert(inforSubscriptionBo);
    }

    @Override
    public WriteResult updateSub(String userid, int type,LinkedHashSet<String> subscriptions) {
        return inforSubDao.updateSub(userid, type, subscriptions);
    }

    @Override
    public WriteResult updateSecuritys(String userid, LinkedHashSet<String> securitys) {
        return inforSubDao.updateSecuritys(userid, securitys);
    }

    @Override
    public WriteResult updateCollect(String userid, LinkedHashSet<String> collects) {
        return inforSubDao.updateCollect(userid, collects);
    }

    @Override
    public InforSubscriptionBo findByUserid(String userid) {
        return null;
    }

    @Override
    public InforReadNumBo addReadNum(InforReadNumBo readNumBo) {
        return inforReadNumDao.insert(readNumBo);
    }

    @Override
    public void updateReadNum(String inforid) {
        inforReadNumDao.update(inforid);
    }

    @Override
    public void updateComment(String inforid, int number) {
        inforReadNumDao.updateComment(inforid, number);
    }

    @Override
    public void updateThumpsub(String inforid, int number) {
        inforReadNumDao.updateThumpsub(inforid, number);
    }

    @Override
    public List<SecurityBo> findSecurityTypes() {
        return securityDao.findAllTypes();
    }

    @Override
    public List<SecurityBo> findSecurityByType(String typeName, String createTime, int limit) {
        return securityDao.findByType(typeName, createTime, limit);
    }

    @Override
    public List<SecurityBo> findSecurityByCity(String cityName, String createTime, int limit) {
        return securityDao.findByCity(cityName, createTime, limit);
    }

    @Override
    public SecurityBo findSecurityById(String id) {
        return securityDao.findById(id);
    }


    @Override
    public BroadcastBo findBroadById(String id) {
        return broadcastDao.findById(id);
    }

    @Override
    public List<BroadcastBo> selectBroadGroups() {
        return broadcastDao.selectGroups();
    }

    @Override
    public List<BroadcastBo> selectBroadClassByGroups(String groupName) {
        return broadcastDao.selectClassByGroups(groupName);
    }

    @Override
    public List<BroadcastBo> findBroadByPage(String groupName, int page, int limit) {
        return broadcastDao.findByPage(groupName, page, limit);
    }

    @Override
    public List<BroadcastBo> findBroadByClassName(String groupName, String className) {
        return broadcastDao.findByClassName(groupName, className);
    }

    @Override
    public List<VideoBo> findVideoByPage(String groupName, int page, int limit) {
        return videoDao.findByPage(groupName, page, limit);
    }

    @Override
    public VideoBo findVideoById(String id) {
        return videoDao.findById(id);
    }

    @Override
    public List<VideoBo> selectVdeoGroups() {
        return videoDao.selectGroups();
    }

    @Override
    public List<VideoBo> selectVideoClassByGroups(String groupName) {
        return videoDao.selectClassByGroups(groupName);
    }

    @Override
    public List<BroadcastBo> findByClassNamePage(String groupName, String className, int start, int end) {
        return broadcastDao.findByClassNamePage(groupName, className, start, end);
    }

    @Override
    public WriteResult updateVideoPicById(String id, String pic) {
        return videoDao.updatePicById(id, pic);
    }

    @Override
    public List<InforBo> homeHealthRecom(int limit) {
        return inforDao.homeHealthRecom(limit);
    }

    @Override
    public List<InforBo> userHealthRecom(String userid, int limit) {
        return inforDao.userHealthRecom(userid, limit);
    }


    @Override
    public List<SecurityBo> findSecurityByLimit(int limit) {
        return securityDao.findByLimiy(limit);
    }

    @Override
    public List<BroadcastBo> findRadioByLimit(int limit) {
        return broadcastDao.findByLimit(limit);
    }

    @Override
    public List<VideoBo> findVideoByLimit(int limit) {
        return videoDao.findByLimit(limit);
    }

    @Override
    public WriteResult updateVideoNum(String inforid, int type, int num) {
        return videoDao.updateVideoNum(inforid, type, num);
    }

    @Override
    public WriteResult updateSecurityNum(String inforid, int type, int num) {
        return securityDao.updateSecurityNum(inforid, type, num);
    }

    @Override
    public WriteResult updateRadioNum(String radioid, int type, int num) {
        return broadcastDao.updateRadioNum(radioid, type, num);
    }

    @Override
    public List<InforBo> findHealthByIds(List<String> healthIds) {
        return inforDao.findHealthByIds(healthIds);
    }

    @Override
    public List<SecurityBo> findSecurityByIds(List<String> securityIds) {
        return securityDao.findSecurityByIds(securityIds);
    }

    @Override
    public List<BroadcastBo> findRadioByIds(List<String> radioIds) {
        return broadcastDao.findRadioByIds(radioIds);
    }

    @Override
    public List<VideoBo> findVideoByIds(List<String> videoIds) {
        return videoDao.findVideoByIds(videoIds);
    }
}
