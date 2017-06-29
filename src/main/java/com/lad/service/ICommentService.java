package com.lad.service;

import com.lad.bo.CommentBo;
import com.lad.bo.RedstarBo;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * 功能描述：
 * Version: 1.0
 * Time:2017/6/25
 */
public interface ICommentService {

    CommentBo insert(CommentBo commentBo);

    CommentBo insert(CommentBo commentBo, RedstarBo redstarBo);

    /**
     * 查询帖子内的评论数
     * @param noteid
     * @return
     */
    List<CommentBo> selectByNoteid(String noteid);

    /**
     * 查询回帖
     * @param parentid
     * @return
     */
    List<CommentBo> selectByParentid(String parentid);

    /**
     * 查到评论
     * @param commentId
     * @return
     */
    CommentBo findById(String commentId);

    /**
     * 删除评论
     * @param commentId
     * @return
     */
    WriteResult delete(String commentId);

    /**
     * 查到用户所有的帖子
     * @param userid
     * @return
     */
    List<CommentBo> selectByUser(String userid);

    /**
     * 更新红人周榜
     * @param weekNo
     * @return
     */
    WriteResult updateRedWeek(int weekNo);

    /**
     * 更新周榜信息
     * @param userid
     * @return
     */
    WriteResult updateCommmentCount(String userid, String circleid);

    /**
     * 红人表信息
     * @param userid
     * @return
     */
    RedstarBo findRedstarBo(String userid, String circleid);
}
