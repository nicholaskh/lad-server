package com.lad.dao;

import com.lad.bo.InforTotalNumBo;

import java.util.HashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/12/11
 */
public interface IInforTotalNumDao {


    InforTotalNumBo addTotalNum(InforTotalNumBo totalNumBo);

    
    InforTotalNumBo findByModuleClassType(String module, String className, int type);


    List<InforTotalNumBo> findByModuleClassType(HashSet<String> modules, HashSet<String> classNames, int type);

}
