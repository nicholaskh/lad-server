package com.lad.dao;

import com.lad.bo.RedstarBo;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * 功能描述：
 * Version: 1.0
 * Time:2017/6/29
 */
public interface IRedstarDao {

    RedstarBo insert(RedstarBo redstarBo);

    /**
     * 添加评论红人计数
     * @param userid
     * @return
     */
    WriteResult addCommentCount(String userid, String circleid, int count);

    /**
     * 红人周榜更新
     * @param weekNo  第几周
     * @return
     */
    WriteResult updateRedWeek(int weekNo);

    /**
     * 更新单个人的红人榜
     * @param userid
     * @param weekNo
     * @param year
     * @return
     */
    WriteResult updateRedWeekByUser(String userid, int weekNo, int year);

    /**
     * 红人总榜
     * @return
     */
    List<RedstarBo> findRedTotal(String circleid);

    /**
     * 红人周榜
     * @param circleid
     * @param weekNo
     * @param year
     * @return
     */
    List<RedstarBo> findRedWeek(String circleid, int weekNo, int year);

    /**
     * 个人评论
     * @param userid
     * @return
     */
    RedstarBo findByUserAndCircle(String userid, String circleid);

}
