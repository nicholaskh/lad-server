package com.lad.dao;

import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/22
 */
public class BaseDao<T extends Serializable> {

    @Autowired
    @Qualifier("mongoTemplate")
    private MongoTemplate mongoTemplate;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
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
        return mongoTemplate.findAll(getClz());
    }

    /**
     * 按照主键查询结果
     * @param id
     * @return
     */
    public T findById(String id){
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return mongoTemplate.findOne(query, getClz());
    }

    /**
     * 插入
     * @param t
     * @return
     */
    public T insert(T t){
        mongoTemplate.insert(t);
        return t;
    }

    /**
     * 插入
     * @param t
     * @return
     */
    public T save(T t){
        mongoTemplate.save(t);
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
            params.forEach(update :: set);
            return mongoTemplate.updateFirst(query, update, getClz());
        }
        return null;
    }

    /**
     * 删除数据
     * @param id
     */
    public WriteResult deleteById(String id) {
        return mongoTemplate.remove(new Query(new Criteria("_id").is(id)), getClz());
    }

    /**
     * 删除数据
     * @param ids
     */
    public WriteResult batchDeleteByIds(String... ids) {
        return mongoTemplate.remove(new Query(new Criteria("_id").in(ids)), getClz());
    }


    /**
     * 注销数据
     * @param id
     */
    public WriteResult inActiveById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", 1);
        return mongoTemplate.updateFirst(query, update, getClz());
    }

    /**
     * 根据条件查询，只适合等于条件,主键降序排列
     * @param params
     * @return
     */
    public List<T> findByItems(Map<String, Object> params) {
        Query query = new Query();
        if (params != null) {
            params.forEach((key, value) -> query.addCriteria(new Criteria(key).is(value)));
        }
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        return mongoTemplate.find(query, getClz());
    }


    /**
     * 更新阅读。点赞。等数据
     * @param id
     * @param numType
     * @param num
     * @return
     */
    public WriteResult updateCounts(String id, int numType, int num){
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        switch (numType) {
            case Constant.VISIT_NUM:
                update.inc("visitNum", num);
                break;
            case Constant.COMMENT_NUM:
                update.inc("commnetNum", num);
                break;
            case Constant.THUMPSUB_NUM:
                update.inc("thumpsubNum", num);
                break;
            case Constant.SHARE_NUM:
                update.inc("shareNum", num);
                break;
            case Constant.COLLECT_NUM:
                update.inc("collectNum", num);
                break;
            default:
                update.inc("visitNum", 1);
                break;
        }
        return mongoTemplate.updateFirst(query, update, getClz());
    }

}
