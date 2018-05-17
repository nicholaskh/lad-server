package com.lad.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.BaseBo;
import com.lad.bo.OptionBo;
import com.lad.bo.RequireBo;
import com.lad.bo.WaiterBo;
import com.lad.dao.IMarriageDao;
import com.lad.vo.OptionVo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository("marriageDao")
public class MarriageDaoImpl implements IMarriageDao {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public String insertPublish(BaseBo bb) {
		System.out.println(bb.getClass());
		mongoTemplate.insert(bb);
		System.out.println(bb.getId());
		return bb.getId();
	}
	
	@Override
	public List<String> getUnrecommendList(String waiterId) {
		
		DBObject dbObject = new BasicDBObject();  
		dbObject.put("_id", waiterId);  //查询条件  
		  
		BasicDBObject fieldsObject=new BasicDBObject();  
		//指定返回的字段  
		fieldsObject.put("pass", true);  
		  
		Query query = new BasicQuery(dbObject,fieldsObject);
		WaiterBo findOne = mongoTemplate.findOne(query, WaiterBo.class); 
		return findOne.getPass();
	}
	
	/**
	 * 查询单个Require
	 */
	@Override
	public RequireBo findRequireById(String requireId) {
		Query query = new Query(Criteria.where("_id").is(requireId));
		return mongoTemplate.findOne(query, RequireBo.class);
	}
	
	
	/**
	 * 查询单个waiter
	 */
	@Override
	public WaiterBo findWaiterById(String caresId) {
		Query query = new Query(Criteria.where("_id").is(caresId));
		return mongoTemplate.findOne(query, WaiterBo.class);
	}

	/**
	 * 查询关注列表,返回值为waiter的id列表
	 * @return list
	 * 
	 */
	@Override
	public List<String> getCaresList(String waiterId,String key) {
		
		DBObject dbObject = new BasicDBObject();  
		dbObject.put("_id", waiterId);  //查询条件  
		  
		BasicDBObject fieldsObject=new BasicDBObject();  
		//指定返回的字段  
		fieldsObject.put(key, true);  
		  
		Query query = new BasicQuery(dbObject,fieldsObject);
		WaiterBo findOne = mongoTemplate.findOne(query, WaiterBo.class);
		List list = null;
		if("cares".equals(key)){
			list = findOne.getCares();
		}
		
		if("pass".equals(key)){
			list = findOne.getPass();
		}
		return list;
	}

	/**
	 * 删除发布,将状态值改为1
	 */
	@Override
	public WriteResult deletePublish(String pubId) {
		Query query= new Query(Criteria.where("_id").is(pubId));
		Update update = new Update();
		update.set("deleted", 1);
		return mongoTemplate.updateFirst(query, update, WaiterBo.class);
	}
	
	/**
	 * 获取发布列表,实际上是获取waiter列表
	 * 
	 */
	@Override
	public List<WaiterBo> getPublishById(String userId) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("createuid").is(userId),Criteria.where("deleted").is(0));
		query.addCriteria(criteria);
		List<WaiterBo> find = mongoTemplate.find(query, WaiterBo.class);
		return find;
	}

	/**
	 * 添加发布
	 */
	@Override
	public String insert(Object obj) {
		System.out.println(obj.getClass());
		return null;
	}

	/**
	 * 获取选项列表
	 */
	@Override
	public List<OptionBo> getOptions(OptionVo ov) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		if(ov.getSupId() == null){
			criteria.andOperator(Criteria.where("field").is(ov.getField()),Criteria.where("deleted").is(1));
		}else{
			criteria.andOperator(Criteria.where("field").is(ov.getField()),Criteria.where("deleted").is(1),Criteria.where("supId").is(ov.getSupId()));
		}
		query.addCriteria(criteria);
		
		return mongoTemplate.find(query, OptionBo.class);
	}

	/**
	 * 更新数据
	 * @return WriteResult
	 * @param 
	 * 		id 待更新 的id 
	 * 		params 变更的参数
	 * 		class1 对应实体类的class
	 */	
	@Override
	public WriteResult updateByParams(String id, Map<String, Object> params, Class class1) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(id);
        query.addCriteria(criteria);       
        Update update = new Update();
        if (params != null) {
            Set<Map.Entry<String, Object>> entrys = params.entrySet();
            for (Map.Entry<String, Object> entry : entrys) {
                update.set(entry.getKey(), entry.getValue());
            }
        }
        return mongoTemplate.updateFirst(query, update, class1);
	}
}
