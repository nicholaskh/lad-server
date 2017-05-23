package com.lad.dao;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.Pager;
import com.lad.bo.UserBo;
import com.mongodb.WriteResult;

@Repository
public class UserDao {

		@Autowired
		private MongoTemplate mongoTemplate;
		
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
			UserBo userBo = mongoTemplate.findOne(query, UserBo.class);	
			return userBo;
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
			UserBo userBo = mongoTemplate.findOne(query, UserBo.class);	
			return userBo;
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
			mongoTemplate.updateFirst(query, update, UserBo.class);	
			return userBo;
		}
		
		public UserBo updateChatroomsTop(UserBo userBo) {
			Query query = new Query();
			query.addCriteria(new Criteria("_id").is(userBo.getId()));
			query.addCriteria(new Criteria("deleted").is(0));
			Update update = new Update();
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
		public Pager selectPage(UserBo userBo,Pager pager){
			Query query = new Query();
			query.skip((pager.getPageNum()-1)*pager.getPageSize());
			query.limit(pager.getPageSize());
			query.addCriteria(new Criteria("deleted").is(0));
			Order order = new Order(Direction.DESC, "id");
			query.with(new Sort(order));
			//query.addCriteria(new Criteria("userNo").in("NO1468048113823"));
			long total = mongoTemplate.count(query, UserBo.class);
			List<UserBo> users = mongoTemplate.find(query, UserBo.class);
			pager.setResult(users);
			pager.setTotal(total);
			return pager;
		}
		
		public UserBo findById(String integer){
			Query query = new Query();
			query.addCriteria(new Criteria("_id").is(integer));
			query.addCriteria(new Criteria("deleted").is(0));
			UserBo userBo = mongoTemplate.findOne(query, UserBo.class);
			return userBo;
		}
		
		public WriteResult updateLocation(String phone, String locationid) {
			Query query = new Query();
			query.addCriteria(new Criteria("phone").is(phone));
			query.addCriteria(new Criteria("deleted").is(0));
			Update update = new Update();
			update.set("locationid", locationid);
			return mongoTemplate.updateFirst(query, update, UserBo.class);	
		}
			
}
