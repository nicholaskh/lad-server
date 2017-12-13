package com.lad.dao;

import com.lad.bo.CollectBo;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 收藏聊天记录
 */
@Repository("collectDao")
public class CollectDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;


	public CollectBo insert(CollectBo chatinfo){
		mongoTemplate.insert(chatinfo);
		return chatinfo;
	}

	public CollectBo findById(String id){
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(id));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.findOne(query, CollectBo.class);
	}

	public CollectBo findByUseridAndTargetid(String userid, String targetid){
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("targetid").is(targetid));
		return mongoTemplate.findOne(query, CollectBo.class);
	}

	/**
	 * 如果
	 * @return
	 */
	public WriteResult updateCollectDelete(String id, int status){
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(id));
		Update update = new Update();
		update.set("deleted",status);
		return mongoTemplate.updateFirst(query, update, CollectBo.class);
	}

	/**
	 * 查找当前用户所有收藏
	 * @param userid
	 * @param page
	 * @return
	 */
	public List<CollectBo> findAllByUserid(String userid, int page , int limit){
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
		page = page < 1 ? 1 : page;
		query.skip((page - 1)*limit);
		query.limit(limit);
		return mongoTemplate.find(query, CollectBo.class);
	}


	/**
	 * 根据具类型查找当前用户所有收藏
	 * @param userid
	 * @param page
	 * @return
	 */
	public List<CollectBo> findAllByUseridType(String userid, int type, int page , int limit){
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("type").is(type));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
		page = page < 1 ? 1 : page;
		query.skip((page - 1)*limit);
		query.limit(limit);
		return mongoTemplate.find(query, CollectBo.class);
	}

	/**
	 * 更新收藏标签
	 * @param id
	 * @param userTages
	 * @return
	 */
	public WriteResult updateTags(String id, LinkedHashSet<String> userTages){
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(id));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		Update update = new Update();
		update.set("userTags", userTages);
		return mongoTemplate.updateFirst(query,update,CollectBo.class);
	}

	/**
	 * 更具关键字或者分类类型
	 * @param keyword
	 * @return
	 */
	public List<CollectBo> findByKeyword(String keyword){
		Pattern pattern = Pattern.compile("^.*"+keyword+".*$", Pattern.CASE_INSENSITIVE);
		Criteria criteria =  new Criteria("title").regex(pattern);
		Criteria tags =  new Criteria("userTags").in(keyword);
		Query query = new Query(criteria.orOperator(tags));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		return mongoTemplate.find(query, CollectBo.class);
	}

	/** String userid, int page , int limit
	 * 更具用户分类
	 * @param tag
	 * @return
	 */
	public List<CollectBo> findByTag(String tag, String userid, int page , int limit){
		Query query = new Query();
		Criteria criteria = new Criteria("userid").is(userid).and("deleted").is(Constant.ACTIVITY);
		Criteria c1 = new Criteria("userTags").in(tag);
		Pattern pattern = Pattern.compile("^.*"+tag+".*$", Pattern.CASE_INSENSITIVE);
		Criteria c2 =  new Criteria("title").regex(pattern);
		query.addCriteria(criteria.orOperator(c1, c2));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
		page = page < 1 ? 1 : page;
		query.skip((page - 1)*limit);
		query.limit(limit);
		return mongoTemplate.find(query, CollectBo.class);
	}

	/**
	 * 删除单条记录
	 * @param chatid
	 * @return
	 */
	public WriteResult delete(String chatid) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatid));
		Update update = new Update();
		update.set("deleted",Constant.DELETED);
		return mongoTemplate.updateFirst(query, update, CollectBo.class);
	}

	public List<CollectBo> findChatByUserid(String userid, String start_id, int limit, int type){
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("type").is(type));
		query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
		if (StringUtils.isNotEmpty(start_id)) {
			query.addCriteria(new Criteria("_id").lt(start_id));
		}
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
		query.limit(limit);
		return mongoTemplate.find(query, CollectBo.class);
	}



}
