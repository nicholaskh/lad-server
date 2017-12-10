package com.lad.dao;

import com.lad.bo.InforHistoryBo;
import com.mongodb.WriteResult;

import java.util.Date;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/24
 */
public interface IInforHistoryDao {


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

    /**
     * 删除资讯信息
     * @param ids
     * @return
     */
    WriteResult updateZeroHis(List<String> ids);


}
