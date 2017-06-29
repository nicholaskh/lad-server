package com.lad.service.impl;

import com.lad.bo.CommentBo;
import com.lad.bo.RedstarBo;
import com.lad.dao.ICommentDao;
import com.lad.dao.IRedstarDao;
import com.lad.service.ICommentService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 功能描述：
 * Time:2017/6/25
 */
@Service("commentService")
public class CommentService implements ICommentService {

    @Autowired
    private ICommentDao commentDao;

    @Autowired
    private IRedstarDao redstarDao;

    public CommentBo insert(CommentBo commentBo, RedstarBo redstarBo) {
            redstarDao.insert(redstarBo);
        return commentDao.insert(commentBo);
    }

    public CommentBo insert(CommentBo commentBo) {
        redstarDao.addCommentCount(commentBo.getCreateuid(), "");
        return commentDao.insert(commentBo);
    }

    @Override
    public List<CommentBo> selectByNoteid(String noteid) {
        return commentDao.selectByNoteid(noteid);
    }

    @Override
    public List<CommentBo> selectByParentid(String parentid) {
        return commentDao.selectByParentid(parentid);
    }

    @Override
    public CommentBo findById(String commentId) {
        return commentDao.findById(commentId);
    }

    @Override
    public WriteResult delete(String commentId) {
        return commentDao.delete(commentId);
    }

    @Override
    public List<CommentBo> selectByUser(String userid) {
        return commentDao.selectByUser(userid);
    }

    public WriteResult updateRedWeek(int weekNo){
        return redstarDao.updateRedWeek(weekNo);
    }

    public WriteResult updateCommmentCount(String userid, String circleid){
        return redstarDao.addCommentCount(userid, circleid);
    }

    public RedstarBo findRedstarBo(String userid, String circleid){
        return redstarDao.findByUserAndCircle(userid, circleid);
    }

}
