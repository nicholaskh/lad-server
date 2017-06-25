package com.lad.dao.impl;

import com.lad.bo.CommentBo;
import com.lad.dao.ICommentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能描述：
 * Time:2017/6/25
 */
@Repository("commentDao")
public class CommentDaoImpl implements ICommentDao {


    @Autowired
    private MongoTemplate mongoTemplate;

    public CommentBo insert(CommentBo commentBo){
        mongoTemplate.insert(commentBo);
        return commentBo;
    }

    public List<CommentBo> selectByNoteid(String noteid){
        Query query = new Query();
        query.addCriteria(new Criteria("noteid").is(noteid));
        query.addCriteria(new Criteria("deleted").is(0));
        return mongoTemplate.find(query, CommentBo.class);
    }

    public List<CommentBo> selectByParentid(String noteid){
        Query query = new Query();
        query.addCriteria(new Criteria("parentid").is(noteid));
        query.addCriteria(new Criteria("deleted").is(0));
        return mongoTemplate.find(query, CommentBo.class);
    }


}
