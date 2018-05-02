package com.lad.dao;

import cn.jiguang.common.utils.StringUtils;
import com.lad.bo.ShowBo;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/26
 */
@Repository("showDao")
public class ShowDao extends BaseDao<ShowBo>{

    /**
     * 根据关键和type 查询
     * @param keyword
     * @param type
     * @param page
     * @param limit
     * @return
     */
    public List<ShowBo> findByList(String keyword, int type, int page, int limit){
        Query query = new Query();
        Criteria criteria = new Criteria("type").is(type);
        if (!StringUtils.isEmpty(keyword)) {
            criteria.orOperator(new Criteria("showType").regex("^.*"+keyword+".*$"),
                    new Criteria("title").regex("^.*"+keyword+".*$"));
        }
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        page = page < 1 ? 1 : page;
        query.skip((page-1)*limit);
        query.limit(limit);
        return getMongoTemplate().find(query, ShowBo.class);
    }


    /**
     * 根据关键和type 查询
     * @param keyword
     * @param type
     * @return
     */
    public List<ShowBo> findByShowType(String keyword, int type){
        Query query = new Query();
        Criteria criteria = new Criteria("type").is(type).and("deleted").is(Constant.ACTIVITY).and("showType").is(keyword)
                .and("status").is(0);
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        return getMongoTemplate().find(query, ShowBo.class);
    }


    /**
     * 根据关键和type 查询
     * @param userid
     * @param type
     * @param page
     * @param limit
     * @return
     */
    public List<ShowBo> findByMyShows(String userid, int type, int page, int limit){
        Query query = new Query();
        query.addCriteria(new Criteria("type").is(type).and("createuid").is(userid)
                .and("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        page = page < 1 ? 1 : page;
        query.skip((page-1)*limit);
        query.limit(limit);
        return getMongoTemplate().find(query, ShowBo.class);
    }



    /**
     * 根据关键和type 查询
     * @param circleid
     * @param type
     * @return
     */
    public List<ShowBo> findByCircleid(String circleid, int status, int type){
        Query query = new Query();
        Criteria criteria = new Criteria("type").is(type).and("circleid").is(circleid)
                .and("deleted").is(Constant.ACTIVITY);
        if (status != -1) {
            criteria.and("status").is(status);
        }
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        return getMongoTemplate().find(query, ShowBo.class);
    }


    /**
     * 根据关键和type 查询
     * @param showids
     * @param status
     * @return
     */
    public WriteResult updateShowStatus(List<String> showids, int status){
        Query query = new Query();
        Criteria criteria = new Criteria("_id").in(showids).and("status").is(0)
                .and("deleted").is(Constant.ACTIVITY);
        query.addCriteria(criteria);
        Update update = new Update();
        update.set("status", status);
        return getMongoTemplate().updateMulti(query, update,  ShowBo.class);
    }


    /**
     * 根据关键和type 查询
     * @param showid
     * @param status
     * @return
     */
    public WriteResult updateShowStatus(String showid, int status){
        Query query = new Query(new Criteria("_id").is(showid));
        Update update = new Update();
        update.set("status", status);
        return getMongoTemplate().updateFirst(query, update,  ShowBo.class);
    }

}
