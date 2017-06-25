package com.lad.service.impl;

import com.lad.bo.CommentBo;
import com.lad.dao.ICommentDao;
import com.lad.service.ICommentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 功能描述：
 * Time:2017/6/25
 */
@Service("commentService")
public class CommentService implements ICommentService {

    private ICommentDao commentDao;

    @Override
    public List<CommentBo> selectByNoteid(String noteid) {
        return commentDao.selectByNoteid(noteid);
    }

    @Override
    public List<CommentBo> selectByParentid(String parentid) {
        return commentDao.selectByParentid(parentid);
    }
}
