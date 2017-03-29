package com.junlenet.mongodb.demo.dao;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.junlenet.mongodb.demo.bo.Pager;
import com.junlenet.mongodb.demo.bo.UserBo;

/**
 * 用户DAO
 * @author huweijun
 * @date 2016年7月7日 下午8:49:18
 */
@Repository
public class UserDao {

	private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
	
		/**
		 * 操作mongodb的类,可以参考api
		 */
		@Autowired
		private MongoTemplate mongoTemplate;
		
		/**
		 * 保存用户信息
		 * @param userBo
		 * @return
		 * @author huweijun
		 * @date 2016年7月7日 下午8:27:37
		 */
		public UserBo save(UserBo userBo) {
			mongoTemplate.save(userBo);
			return userBo;
		}

		/**
		 * 修改用户信息
		 * @param userBo
		 * @return
		 * @author huweijun
		 * @date 2016年7月7日 下午8:27:37
		 */
		public UserBo updatePassword(UserBo userBo) {
			Query query = new Query();
			query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
			Update update = new Update();
			update.set("password", userBo.getPassword());
			mongoTemplate.updateFirst(query, update, UserBo.class);	
			return userBo;
		}
		
		public UserBo updateHeadPictureName(UserBo userBo) {
			Query query = new Query();
			query.addCriteria(new Criteria("phone").is(userBo.getPhone()));
			Update update = new Update();
			update.set("headPictureName", userBo.getHeadPictureName());
			mongoTemplate.updateFirst(query, update, UserBo.class);	
			return userBo;
		}
		/**
		 * 获取所有集合的名称
		 * @return
		 * @author huweijun
		 * @date 2016年7月7日 下午8:27:28
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
		 * @author huweijun
		 * @date 2016年7月7日 下午8:27:47
		 */
		public Pager selectPage(UserBo userBo,Pager pager){
			Query query = new Query();
			query.skip((pager.getPageNum()-1)*pager.getPageSize());
			query.limit(pager.getPageSize());
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
			UserBo userBo = mongoTemplate.findOne(query, UserBo.class);
			return userBo;
		}
		
		
		
		
		
}
