package com.lad.dao;

import com.lad.bo.DynamicBo;
import com.mongodb.WriteResult;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/21
 */
public interface IDynamicDao {

    /**
     * 添加
     * @param dynamicBo
     * @return
     */
    DynamicBo insert(DynamicBo dynamicBo);

    /**
     * 删除
     * @param id
     * @return
     */
    WriteResult delete(String id);

    /**
     * 根据id
     * @param id
     * @return
     */
    DynamicBo findById(String id);

    /**
     * 更新数量
     * @param id
     * @param num
     * @param type
     * @return
     */
    WriteResult update(String id, int num, int type);


}
