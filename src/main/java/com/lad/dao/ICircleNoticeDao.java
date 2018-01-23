package com.lad.dao;

import com.lad.bo.CircleNoticeBo;
import com.mongodb.WriteResult;

import java.util.LinkedHashSet;
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
    List<CircleNoticeBo> findCircleNotice(String targetid, int noticeType, int page, int limit);

    /**
     * 查找最后一条历史信息
     * @param circleid
     * @return
     */
    CircleNoticeBo findLastNotice(String targetid, int noticeType);

    /**
     * 更具id查找
     * @param id
     * @return
     */
    CircleNoticeBo findNoticeById(String id);

    /**
     * 删除公告
     * @param id
     * @return
     */
    WriteResult deleteNotice(String id, String userid);

    /**
     * 更新阅读数
     * @param id
     * @param readUsers
     * @param unReadUsers
     * @return
     */
    WriteResult updateNoticeRead(String id, LinkedHashSet<String> readUsers, LinkedHashSet<String> unReadUsers);

    /**
     * 更新公告内容
     * @param noticeBo
     * @return
     */
    WriteResult updateNotice(CircleNoticeBo noticeBo);

    /**
     * 查找未读公告信息
     * @param userid
     * @return
     */
    List<CircleNoticeBo> unReadNotice(String userid, String targetid, int noticeType);

    /**
     * 查找未读公告信息
     * @param userid
     * @return
     */
    List<CircleNoticeBo> unReadNotice(String userid, String targetid, int noticeType, int page, int limit);


    /**
     * 更具id查找
     * @param id
     * @return
     */
    List<CircleNoticeBo> findNoticeByIds(String... ids);

}
