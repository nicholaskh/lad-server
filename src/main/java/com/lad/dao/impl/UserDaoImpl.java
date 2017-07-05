package com.lad.dao.impl;

import com.lad.bo.Pager;
import com.lad.bo.UserBo;
import com.lad.dao.IUserDao;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 功能描述：
 * Version: 1.0
 * Time:2017/7/2
 */
@Repository("userDao")
public class UserDaoImpl implements IUserDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public UserBo save(UserBo userBo) {
        mongoTemplate.save(userBo);
        return userBo;
    }

    public UserBo updatePassword(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("password", userBo.getPassword());
        mongoTemplate.updateFirst(query, update, UserBo.class);
        return userBo;
    }

    public UserBo getUser(String userId) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(userId));
        query.addCriteria(new Criteria("deleted").is(0));
        return mongoTemplate.findOne(query, UserBo.class);
    }

    public List<UserBo> getUserByName(String name) {
        Query query = new Query();
        query.addCriteria(new Criteria("userName").is(name));
        query.addCriteria(new Criteria("deleted").is(0));
        List<UserBo> userBoList = mongoTemplate.find(query, UserBo.class);
        return userBoList;
    }

    public UserBo getUserByPhone(String phone) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(phone));
        query.addCriteria(new Criteria("deleted").is(0));
        return mongoTemplate.findOne(query, UserBo.class);
    }

    public UserBo updatePhone(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(userBo.getId()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("phone", userBo.getPhone());
        mongoTemplate.updateFirst(query, update, UserBo.class);
        return userBo;
    }

    public UserBo updateFriends(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(userBo.getId()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("friends", userBo.getFriends());
        mongoTemplate.updateFirst(query, update, UserBo.class);
        return userBo;
    }

    public UserBo updateChatrooms(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(userBo.getId()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("chatrooms", userBo.getChatrooms());
        update.set("chatroomsTop", userBo.getChatroomsTop());
        mongoTemplate.updateFirst(query, update, UserBo.class);
        return userBo;
    }

    public UserBo updateHeadPictureName(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("headPictureName", userBo.getHeadPictureName());
        mongoTemplate.updateFirst(query, update, UserBo.class);
        return userBo;
    }

    public UserBo updateUserName(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("userName", userBo.getUserName());
        mongoTemplate.updateFirst(query, update, UserBo.class);
        return userBo;
    }

    public UserBo updateSex(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("sex", userBo.getSex());
        mongoTemplate.updateFirst(query, update, UserBo.class);
        return userBo;
    }

    public UserBo updatePersonalizedSignature(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("personalizedSignature", userBo.getPersonalizedSignature());
        mongoTemplate.updateFirst(query, update, UserBo.class);
        return userBo;
    }

    public UserBo updateBirthDay(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("birthDay", userBo.getBirthDay());
        mongoTemplate.updateFirst(query, update, UserBo.class);
        return userBo;
    }
    /**
     * 获取所有集合的名称
     * @return
     */
    public Set<String> getCollectionNames() {
        Set<String> collections = mongoTemplate.getCollectionNames();
        return collections;
    }

    /**
     * 分页查询数据
     * @param userBo
     * @param pager
     * @return
     */
    public Pager selectPage(UserBo userBo, Pager pager){
        Query query = new Query();
        query.skip((pager.getPageNum()-1)*pager.getPageSize());
        query.limit(pager.getPageSize());
        query.addCriteria(new Criteria("deleted").is(0));
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        query.with(new Sort(order));
        //query.addCriteria(new Criteria("userNo").in("NO1468048113823"));
        long total = mongoTemplate.count(query, UserBo.class);
        List<UserBo> users = mongoTemplate.find(query, UserBo.class);
        pager.setResult(users);
        pager.setTotal(total);
        return pager;
    }

    public WriteResult updateLocation(String phone, String locationid) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(phone));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("locationid", locationid);
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public List<UserBo> getAllUser() {
        return mongoTemplate.findAll(UserBo.class);
    }

}