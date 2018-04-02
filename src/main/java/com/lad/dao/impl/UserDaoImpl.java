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
import org.springframework.util.StringUtils;

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
        userBo.setUpdateTime(new Date());
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
        return null;
    }

    public WriteResult updateChatrooms(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(userBo.getId()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("chatrooms", userBo.getChatrooms());
        update.set("chatroomsTop", userBo.getChatroomsTop());
        update.set("showChatrooms", userBo.getShowChatrooms());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public WriteResult updateHeadPictureName(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("headPictureName", userBo.getHeadPictureName());
        update.set("updateTime", new Date());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public WriteResult updateUserName(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("userName", userBo.getUserName());
        update.set("updateTime", new Date());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public WriteResult updateSex(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("sex", userBo.getSex());
        update.set("updateTime", new Date());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public WriteResult updatePersonalizedSignature(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("personalizedSignature", userBo.getPersonalizedSignature());
        update.set("updateTime", new Date());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    public WriteResult updateBirthDay(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("birthDay", userBo.getBirthDay());
        update.set("updateTime", new Date());
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
        update.set("updateTime", new Date());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    @Override
    public WriteResult updateUserStatus(String id, int status) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", status);
        update.set("updateTime", new Date());
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

    @Override
    public WriteResult updateUserDynamicPic(String id, String pic) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("dynamicPic", pic);
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    @Override
    public List<UserBo> findUserByIds(List<String> userids) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").in(userids));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        return mongoTemplate.find(query, UserBo.class);
    }


    @Override
    public WriteResult updateShowChatrooms(String userid, HashSet<String> chatrooms) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(userid));
        Update update = new Update();
        update.set("showChatrooms", chatrooms);
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    @Override
    public WriteResult updateUserInfo(UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(userBo.getId()));
        Update update = new Update();
        update.set("userName", userBo.getUserName());
        update.set("phone", userBo.getPhone());
        update.set("sex", userBo.getSex());
        update.set("headPictureName", userBo.getHeadPictureName());
        update.set("birthDay", userBo.getBirthDay());
        update.set("personalizedSignature", userBo.getPersonalizedSignature());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    @Override
    public UserBo findByOpenid(String openid) {
        Query query = new Query();
        query.addCriteria(new Criteria("openid").is(openid));
        return mongoTemplate.findOne(query, UserBo.class);
    }

    @Override
    public WriteResult updateRefeshToken(String openid, String acces_token, String refesh_token) {
        return null;
    }

    @Override
    public WriteResult updateUserByOpenid(String openid, UserBo userBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("openid").is(openid));
        Update update = new Update();
        update.set("userName", userBo.getUserName());
        update.set("sex", userBo.getSex());
        update.set("headPictureName", userBo.getHeadPictureName());
        update.set("province", userBo.getProvince());
        update.set("city", userBo.getCity());
        update.set("privilege", userBo.getPrivilege());
        update.set("unionid", userBo.getUnionid());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }


    @Override
    public UserBo findByUnionid(String unionid) {
        return null;
    }

    @Override
    public WriteResult updateLastLoginTime(int loginType, String id) {
        Query query = new Query();
        if (loginType == 0) {
            query.addCriteria(new Criteria("_id").is(id));
        } else {
            query.addCriteria(new Criteria("openid").is(id));
        }
        Update update = new Update();
        update.set("lastLoginTime", new Date());
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    @Override
    public WriteResult updateQQUserInfor(String id, String accessToken, String nickname, String userPic, String
            gender) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("accessToken", accessToken);
        if (!StringUtils.isEmpty(nickname)) {
            update.set("userName", nickname);
        }
        if (!StringUtils.isEmpty(gender)) {
            update.set("sex", gender);
        }
        if (!StringUtils.isEmpty(userPic)) {
            update.set("headPictureName", userPic);
        }
        return mongoTemplate.updateFirst(query, update, UserBo.class);
    }

    @Override
    public WriteResult removeUser(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return mongoTemplate.remove(query, UserBo.class);
    }
}
