package com.lad.dao;

import com.lad.bo.CommentBo;

import java.util.List;

/**
 * 功能描述：
 * Time:2017/6/25
 */
public interface ICommentDao {

    List<CommentBo> selectByNoteid(String noteid);

    List<CommentBo> selectByParentid(String noteid);
}
