package com.lad.dao;

import com.lad.bo.CommentBo;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * 功能描述：
 * Time:2017/6/25
 */
public interface ICommentDao {

    /**
     * 添加评论
     * @param commentBo
     * @return
     */
    CommentBo insert(CommentBo commentBo);

    /**
     * 查询帖子下的所有评论
     * @param noteid
     * @return
     */
    List<CommentBo> selectByNoteid(String noteid, String startId, boolean gt, int limit);

    /**
     * 查询当前回复的评论
     * @param parentId
     * @return
     */
    List<CommentBo> selectByParentid(String parentId);

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
     * 查到用户所有的帖子
     * @param userid
     * @return
     */
    List<CommentBo> selectByUser(String userid, String startId, boolean gt, int limit);

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

    /**
     * 根据类型查找
     * @param targetid
     * @param userid
     * @param type
     * @return
     */
    List<CommentBo> selectByTargetUser(String targetid, String userid, int type);

    /**
     * 删除评论
     * @param commentId
     * @return
     */
    WriteResult updateThumpsubNum(String commentId, int num);
}
