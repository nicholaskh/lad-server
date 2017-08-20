package com.lad.dao;

import com.lad.bo.InforReadNumBo;
import com.mongodb.WriteResult;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/5
 */
public interface IInforReadNumDao {

    InforReadNumBo insert(InforReadNumBo readNumBo);

    /**
     * 阅读数+1
     * @param inforid
     * @return
     */
    WriteResult update(String inforid);

    InforReadNumBo findByInforid(String inforid);

    WriteResult updateComment(String inforid, int number);

    WriteResult updateThumpsub(String inforid, int number);
}
