package com.lad.dao.impl;

import com.lad.bo.CommentBo;
import com.lad.dao.ICommentDao;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    public CommentBo findById(String commentId){
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(commentId));
        query.addCriteria(new Criteria("deleted").is(0));
        return mongoTemplate.findOne(query, CommentBo.class);
    }

    public List<CommentBo> selectByNoteid(String noteid, String startId, boolean gt, int limit){
        Query query = new Query();
        query.addCriteria(new Criteria("noteid").is(noteid));
        query.addCriteria(new Criteria("deleted").is(0));
        CommonUtil.queryByIdPage(query,startId,gt,limit);
        return mongoTemplate.find(query, CommentBo.class);
    }

    public List<CommentBo> selectByParentid(String parentId){
        Query query = new Query();
        query.addCriteria(new Criteria("parentid").is(parentId));
        query.addCriteria(new Criteria("deleted").is(0));
        return mongoTemplate.find(query, CommentBo.class);
    }

    public WriteResult delete(String commentId){
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(commentId));
        Update update = new Update().set("deleted", 1);
        return mongoTemplate.updateFirst(query, update, CommentBo.class);
    }

    public List<CommentBo> selectByUser(String userid,  String startId, boolean gt, int limit){
        Query query = new Query();
        query.addCriteria(new Criteria("createuid").is(userid));
        query.addCriteria(new Criteria("deleted").is(0));
        query.addCriteria(new Criteria("type").is(Constant.NOTE_TYPE));
        CommonUtil.queryByIdPage(query,startId,gt,limit);
        return mongoTemplate.find(query, CommentBo.class);
    }

    @Override
    public List<CommentBo> selectCommentByType(int type, String id, String startId, boolean gt, int limit) {

        Query query = new Query();
        if (type == Constant.NOTE_TYPE) {
            query.addCriteria(new Criteria("noteid").is(id));
        } else if (type == Constant.INFOR_TYPE) {
            query.addCriteria(new Criteria("targetid").is(id));
        }
        query.addCriteria(new Criteria("deleted").is(0));
        query.addCriteria(new Criteria("type").is(type));
        CommonUtil.queryByIdPage(query,startId,gt,limit);
        return mongoTemplate.find(query, CommentBo.class);
    }
}
