package com.lad.service;

import com.lad.bo.InforHistoryBo;
import com.lad.bo.InforRecomBo;
import com.lad.bo.InforUserReadBo;
import com.lad.bo.InforUserReadHisBo;
import com.mongodb.WriteResult;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 功能描述：资讯推荐相关接口
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/25
 */

public interface IInforRecomService {


    InforHistoryBo addInfoHis(InforHistoryBo historyBo);

    /**
     * 查找当前资讯当天是否已经有浏览信息
     * @param inforid
     * @param zeroTime
     * @return
     */
    InforHistoryBo findTodayHis(String inforid, Date zeroTime);

    /**
     * 查找当前资讯180天前的浏览信息变删除
     * @param inforid
     * @param halfYearTime
     * @return
     */
    List<InforHistoryBo> findHalfYearHis(String inforid, Date halfYearTime);

    /**
     * 删除资讯信息
     * @param id
     * @return
     */
    WriteResult deleteHis(String id);

    /**
     * 更新资讯每天信息
     * @param id
     * @return
     */
    WriteResult updateHisDayNum(String id, int num);

    InforRecomBo addInforRecom(InforRecomBo recomBo);

    /**
     * 更具资讯id查询
     * @param inforid
     * @return
     */
    InforRecomBo findRecomByInforid(String inforid);

    /**
     * 更新热度信息
     * @param id
     * @param halfNum
     * @param totalNum
     * @return
     */
    WriteResult updateRecomByInforid(String id, int halfNum, int totalNum);

    /**
     * 根据类型和分类
     * @param type
     * @param modules
     * @return
     */
    List<InforRecomBo> findRecomByTypeAndModule(int type, LinkedHashSet<String> modules);

    /**
     * 根据类型的前50
     * @param type
     * @return
     */
    List<InforRecomBo> findRecomByType(int type);

    InforUserReadBo addUserRead(InforUserReadBo userReadBo);


    InforUserReadBo findUserReadByUserid(String userid);

    /**
     * 更具类型修改
     * @param id
     * @param type
     * @param modules
     * @return
     */
    WriteResult updateUserRead(String id, int type, LinkedHashSet<String> modules);


    WriteResult deleteUserRead(String id);

    WriteResult updateUserReadAll(InforUserReadBo userReadBo);

    InforUserReadHisBo addUserReadHis(InforUserReadHisBo userReadHisBo);

    /**
     * 查找用户半年内指定类型和分类的阅读记录
     * @param userid
     * @param type
     * @param module
     * @param halfTime
     * @return
     */
    InforUserReadHisBo findUserReadHis(String userid, int type, String module, Date halfTime);

    /**
     * 更新当前用户阅读记录
     * @param id
     * @param currentDate
     * @return
     */
    WriteResult updateUserReadHis(String id, Date currentDate);

    /**
     * 查找用户当天指定类型和分类的阅读记录
     * @param userid
     * @param type
     * @param module
     * @return
     */
    InforUserReadHisBo findByReadHis(String userid, int type, String module);

    /**
     * 查找用户半年之前的分类阅读记录
     * @param userid
     * @param halfTime
     * @return
     */
    List<InforUserReadHisBo> findUserReadHisBeforeHalf(String userid, Date halfTime);
}
