package com.lad.service;

import com.lad.bo.CommentBo;
import com.lad.bo.RedstarBo;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * 功能描述：
 * Version: 1.0
 * Time:2017/6/25
 */
public interface ICommentService {

    /**
     * 当前使用红人列表已经存在时
     * @param commentBo
     * @return
     */
    CommentBo insert(CommentBo commentBo);

    /**
     * 当前使用人红人列表不存在
     * @param redstarBo
     * @return
     */
    RedstarBo insertRedstar(RedstarBo redstarBo);

    /**
     * 查询帖子内的评论数
     * @param noteid
     * @return
     */
    List<CommentBo> selectByNoteid(String noteid, String startId, boolean gt, int limit);

    /**
     * 查询置顶类型下 置顶 id 下的评论
     * @param type
     * @param id
     * @param startId
     * @param gt
     * @param limit
     * @return
     */
    List<CommentBo> selectCommentByType(int type, String id, String startId, boolean gt, int limit);

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
     * 删除帖子下所有评论
     * @param noteid
     * @return
     */
    WriteResult deleteByNote(String noteid);

    /**
     * 查到用户所有的评论
     * @param userid
     * @return
     */
    List<CommentBo> selectByUser(String userid, String startId, boolean gt, int limit);

    /**
     * 更新红人周榜,涉及到所有人员，所以异步执行
     * @param weekNo
     * @return
     */
    WriteResult updateRedWeek(int weekNo);

    /**
     * 更新单个人的红人周榜,总榜数据也+1
     * @param userid
     * @param weekNo
     * @param year
     * @return
     */
    WriteResult updateRedWeekByUser(String userid, int weekNo, int year);

    /**
     * 红人表信息
     * @param userid
     * @return
     */
    RedstarBo findRedstarBo(String userid, String circleid);

    /**
     * 添加红人评论数
     * @param userid
     * @param circleid
     * @return
     */
    WriteResult addRadstarCount(String userid, String circleid);

    /**
     * 查询置顶类型下 置顶 id 下的评论
     * @param type
     * @param id
     * @return
     */
    long selectCommentByTypeCount(int type, String id);

    /**
     * 获取用户评论别人的帖子
     * @param userid
     * @param startId
     * @param limit
     * @return
     */
    List<BasicDBObject> selectMyNoteReply(String userid, String startId, int limit);

}
