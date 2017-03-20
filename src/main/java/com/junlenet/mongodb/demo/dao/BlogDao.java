package com.junlenet.mongodb.demo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.junlenet.mongodb.demo.bo.BlogBo;

@Repository
public class BlogDao{

	@Autowired
	private MongoTemplate mongoTemplate;
	
	public BlogBo findById(String id){
		return mongoTemplate.findById(id, BlogBo.class);
	}
	
	public String saveOrUpdate(BlogBo blogBo){
		mongoTemplate.save(blogBo);
		return blogBo.getId();
	}
	
	public List<BlogBo> list(BlogBo blogBo){
		Query query = new Query();
		//Criteria criteria = new Criteria("tags.name").is("tagName_10");
		Criteria criteria = new Criteria("tags").elemMatch(new Criteria("name").is("tagName_10"));
		query.addCriteria(criteria);
		//query.addCriteria(new Criteria("title").is("关于中国"));
		List<BlogBo> blogs = mongoTemplate.find(query, BlogBo.class);
		return blogs;
	}
}
