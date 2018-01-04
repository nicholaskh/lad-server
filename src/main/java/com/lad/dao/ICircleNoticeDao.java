package com.lad.dao;

import com.lad.bo.CircleNoticeBo;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/1/4
 */
public interface ICircleNoticeDao {

    CircleNoticeBo addNotice(CircleNoticeBo noticeBo);

    /**
     * 查找圈子公告历史信息
     * @param circleid
     * @param page
     * @param limit
     * @return
     */
    List<CircleNoticeBo> findCircleNotice(String circleid, int page, int limit);

    /**
     * 查找最后一条历史信息
     * @param circleid
     * @return
     */
    CircleNoticeBo findLastNotice(String circleid);
}
