package com.lad.dao;

import com.lad.bo.CircleShowBo;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/1/9
 */
public interface ICircleShowDao {

    /**
     * 添加
     * @param showBo
     * @return
     */
    CircleShowBo addCircleShow(CircleShowBo showBo);

    /**
     * 查找
     * @param circleid
     * @param page
     * @param limit
     * @return
     */
    List<CircleShowBo> findCircleShows(String circleid, int page, int limit);

    /**
     * 删除帖子或者聚会时要删除该数据
     * @param targetid
     * @return
     */
    WriteResult deleteShow(String targetid);


}
