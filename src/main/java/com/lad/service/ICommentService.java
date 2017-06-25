package com.lad.service;

import com.lad.bo.CommentBo;

import java.util.List;

/**
 * 功能描述：
 * Version: 1.0
 * Time:2017/6/25
 */
public interface ICommentService {

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
}
