package com.lad.dao;

import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/3/19
 */
public class InforBaseDao<T extends Serializable> {

    @Autowired
    @Qualifier("mongoTemplateTwo")
    private MongoTemplate mongoTemplateTwo;


    public MongoTemplate getMongoTemplateTwo() {
        return mongoTemplateTwo;
    }

    /**
     * 创建一个Class的对象来获取泛型的class
     */
    private Class<T> clz;

    /**
     * 获取当前对象的类型
     * @return 类型
     */
    public Class<T> getClz() {
        if(clz==null) {
            //获取泛型的Class对象
            clz = ((Class<T>)
                    (((ParameterizedType)(this.getClass()
                            .getGenericSuperclass())).getActualTypeArguments()[0]));
        }
        return clz;
    }

    /**
     * 查找所有
     * @return
     */
    public List<T> finaAll(){
        return mongoTemplateTwo.findAll(getClz());
    }

    /**
     * 按照主键查询结果
     * @param id
     * @return
     */
    public T findById(String id){
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return mongoTemplateTwo.findOne(query, getClz());
    }

    /**
     * 插入
     * @param t
     * @return
     */
    public T insert(T t){
        mongoTemplateTwo.insert(t);
        return t;
    }

    /**
     * 插入
     * @param t
     * @return
     */
    public T save(T t){
        mongoTemplateTwo.save(t);
        return t;
    }

    /**
     * 单个更新
     * @param id 主键
     * @param params 参数
     */
    public WriteResult update(String id, Map<String, Object> params) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        if (params != null) {
            Update update = new Update();
            Set<Map.Entry<String, Object>> entrys = params.entrySet();
            for (Map.Entry<String, Object> entry : entrys) {
                update.set(entry.getKey(), entry.getValue());
            }
            return mongoTemplateTwo.updateFirst(query, update, getClz());
        }
        return null;
    }

    /**
     * 删除数据
     * @param id
     */
    public WriteResult deleteById(String id) {
        return mongoTemplateTwo.remove(new Query(new Criteria("_id").is(id)), getClz());
    }

    /**
     * 删除数据
     * @param ids
     */
    public WriteResult batchDeleteByIds(String... ids) {
        return mongoTemplateTwo.remove(new Query(new Criteria("_id").in(ids)), getClz());
    }


    /**
     * 注销数据
     * @param id
     */
    public WriteResult inActiveeById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", 1);
        return mongoTemplateTwo.updateFirst(query, update, getClz());
    }

    /**
     * 根据条件查询，只适合等于条件,主键降序排列
     * @param params
     * @return
     */
    public List<T> findByItems(Map<String, Object> params) {
        Query query = new Query();
        if (params != null) {
            Set<Map.Entry<String, Object>> entrys = params.entrySet();
            for (Map.Entry<String, Object> entry : entrys) {
                query.addCriteria(new Criteria(entry.getKey()).is(entry.getValue()));
            }
        }
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        return mongoTemplateTwo.find(query, getClz());
    }


    /**
     * 查找指定分类下资讯
     * @param module
     * @param collectionName
     * @return
     */
    public List<T> findByModule(String module, String collectionName){
        Criteria criteria = new Criteria("module").is(module);
        MatchOperation match = Aggregation.match(criteria);
        ProjectionOperation project = Aggregation.project("_id","className");
        GroupOperation groupOperation = Aggregation.group("className").count().as("nums");
        Aggregation aggregation = Aggregation.newAggregation(match, project, groupOperation,
                Aggregation.sort(Sort.Direction.DESC, "_id"));
        AggregationResults<T> results = mongoTemplateTwo.aggregate(aggregation, collectionName, getClz());
        return results != null ? results.getMappedResults() : null;
    }

    /**
     * 查找指定分类下资讯
     * @param className
     * @param page
     * @param limit
     * @return
     */
    public List<T> findByClassName(String className, int page, int limit){
        Query query = new Query();
        query.addCriteria(new Criteria("className").is(className));
        query.with(new Sort(Sort.Direction.DESC, "time", "num"));
        page = page < 1 ? 1 : page;
        query.skip((page -1) * limit);
        query.limit(limit);
        return mongoTemplateTwo.find(query, getClz());
    }

    /**
     * 更新阅读信息
     * @param id
     * @param type
     * @param num
     * @return
     */
    public WriteResult updateByType(String id, int type, int num) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        switch (type){
            case Constant.VISIT_NUM:
                update.inc("visitNum", num);
                break;
            case Constant.COMMENT_NUM:
                update.inc("commnetNum", num);
                break;
            case Constant.SHARE_NUM:
                update.inc("shareNum", num);
                break;
            case Constant.THUMPSUB_NUM:
                update.inc("thumpsubNum", num);
                break;
            case Constant.COLLECT_NUM:
                update.inc("collectNum", num);
                break;
            default:
                update.inc("visitNum", num);
                break;
        }
        return mongoTemplateTwo.updateFirst(query, update, getClz());
    }


    /**
     * 关键字查找
     * @param keywrod
     * @param page
     * @param limit
     * @return
     */
    public List<T> findByTitleKeyword(String keywrod, int page, int limit){
        Pattern pattern = Pattern.compile("^.*"+keywrod+".*$", Pattern.CASE_INSENSITIVE);
        Criteria title = new Criteria("title").regex(pattern);
        Criteria text = new Criteria("text").regex(pattern);
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.orOperator(title, text);
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC,"time", "_id"));
        page = page < 1 ? 1 : page;
        query.skip((page-1)*limit);
        query.limit(limit);
        return getMongoTemplateTwo().find(query, getClz());
    }

}
