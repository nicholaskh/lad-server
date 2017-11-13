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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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

    /**
     * 通讯录信息获取
     * @param timestamp
     * @return
     */
    public List<UserBo> getUserByPhoneAndTime(List<String> phones, Date timestamp) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").in(phones));
        query.addCriteria(new Criteria("deleted").is(0));
        if (null != timestamp) {
            query.addCriteria(new Criteria("createTime").gt(timestamp));
        }
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC,"createTime")));
        return mongoTemplate.find(query, UserBo.class);
    }

    @Override
    public UserBo checkByPhone(String phone) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(phone));
        return mongoTemplate.findOne(query, UserBo.class);
    }

    public WriteResult updatePhone(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(userBo.getId()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("phone", userBo.getPhone());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public WriteResult updateFriends(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(userBo.getId()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("friends", userBo.getFriends());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public WriteResult updateChatrooms(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(userBo.getId()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("chatrooms", userBo.getChatrooms());
        update.set("chatroomsTop", userBo.getChatroomsTop());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public WriteResult updateHeadPictureName(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("headPictureName", userBo.getHeadPictureName());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public WriteResult updateUserName(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("userName", userBo.getUserName());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public WriteResult updateSex(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("sex", userBo.getSex());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public WriteResult updatePersonalizedSignature(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("personalizedSignature", userBo.getPersonalizedSignature());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public WriteResult updateBirthDay(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("birthDay", userBo.getBirthDay());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
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

    public WriteResult updateTopCircles(String userid, List<String> topCircles) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(userid));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("circleTops", topCircles);
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public List<UserBo> getAllUser() {
        return mongoTemplate.findAll(UserBo.class);
    }

    @Override
    public WriteResult updateLevel(String id, int level) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("level", level);
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    @Override
    public WriteResult updateUserStatus(String id, int status) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", status);
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    @Override
    public List<UserBo> searchCircleUsers(HashSet<String> circleUsers, String keywords) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").in(circleUsers));
        Pattern pattern = Pattern.compile("^.*"+keywords+".*$", Pattern.CASE_INSENSITIVE);
        query.addCriteria(new Criteria("userName").regex(pattern));
        return mongoTemplate.find(query, UserBo.class);
    }
}
