package com.junlenet.mongodb.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junlenet.mongodb.demo.bo.BlogBo;
import com.junlenet.mongodb.demo.dao.BlogDao;

@Service
public class BlogService {

	@Autowired
	private BlogDao blogDao;
	
	public BlogBo saveOrUpdate(BlogBo blogBo){
		blogDao.saveOrUpdate(blogBo);
		blogBo = blogDao.findById(blogBo.getId());
		return blogBo;
	}
	
	public BlogBo findById(String id){
		BlogBo blogBo = blogDao.findById(id);
		return blogBo;
	}
	
	public List<BlogBo> list(BlogBo blogBo){
		return blogDao.list(blogBo);
	}
}
