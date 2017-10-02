package com.lad.dao.impl;

import com.lad.bo.CommentBo;
import com.lad.dao.ICommentDao;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
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

    public WriteResult deleteByNote(String noteid){
        Query query = new Query();
        query.addCriteria(new Criteria("noteid").is(noteid));
        Update update = new Update().set("deleted", Constant.DELETED);
        return mongoTemplate.updateMulti(query, update, CommentBo.class);
    }

    public List<CommentBo> selectByUser(String userid,  String startId, boolean gt, int limit){
        Query query = new Query();
        query.addCriteria(new Criteria("createuid").is(userid));
        query.addCriteria(new Criteria("deleted").is(0));
        query.addCriteria(new Criteria("type").is(Constant.NOTE_TYPE));
        CommonUtil.queryByIdPage(query,startId,gt,limit);
        return mongoTemplate.find(query, CommentBo.class);
    }

    public List<BasicDBObject> selectMyNoteReply(String userid, String startId, int limit){
        Criteria criteria = new Criteria("createuid").is(userid);
        criteria.and("deleted").is(Constant.ACTIVITY);
        criteria.and("type").is(Constant.NOTE_TYPE);
        criteria.and("ownerid").ne(userid);
        if (StringUtils.isNotEmpty(startId)) {
            criteria.and("noteid").lt(startId);
        }

        AggregationOperation match = Aggregation.match(criteria);

        ProjectionOperation project = Aggregation.project("noteid");

        GroupOperation group = Aggregation.group("noteid").first("noteid").as("noteid");
        Aggregation aggregation = Aggregation.newAggregation(match, project,  group,
                Aggregation.sort(new Sort(new Sort.Order(Sort.Direction.DESC, "noteid"))),
                Aggregation.limit(limit));
        AggregationResults<BasicDBObject> results = mongoTemplate.aggregate(aggregation, "commnet",
                BasicDBObject.class);
        return results.getMappedResults();
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

    @Override
    public long selectCommentByTypeCount(int type, String id) {
        Query query = new Query();
        if (type == Constant.NOTE_TYPE) {
            query.addCriteria(new Criteria("noteid").is(id));
        } else if (type == Constant.INFOR_TYPE) {
            query.addCriteria(new Criteria("targetid").is(id));
        }
        query.addCriteria(new Criteria("deleted").is(0));
        query.addCriteria(new Criteria("type").is(type));
        return mongoTemplate.count(query, CommentBo.class);
    }

    public List<CommentBo> selectByTargetUser(String targetid, String userid, int type) {
        Query query = new Query();
        if (type == Constant.NOTE_TYPE) {
            query.addCriteria(new Criteria("noteid").is(targetid));
        } else {
            query.addCriteria(new Criteria("targetid").is(targetid));
        }
        query.addCriteria(new Criteria("deleted").is(0));
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("type").is(type));
        return mongoTemplate.find(query, CommentBo.class);
    }
}
