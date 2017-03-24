package com.junlenet.mongodb.demo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.junlenet.mongodb.demo.bo.BlogBo;
import com.junlenet.mongodb.demo.bo.Pager;
import com.junlenet.mongodb.demo.bo.TagBo;
import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.service.BlogService;
import com.junlenet.mongodb.demo.service.impl.UserService;
/**
 * demo 控制器
 * @author huweijun
 * @date 2016年7月7日 下午8:45:08
 */
@Controller
@Scope("prototype")
@RequestMapping("indexController")
public class IndexController {
	
		//https://github.com/magicdict/MongoCola/downloads
		//访问地址: http://127.0.0.1:8080/mongodb-demo/indexController/index.do
	//   安装过程中，你可以通过点击 "Custom(自定义)" 按钮来设置你的安装目录。
		@Autowired
		private UserService userService;
		
		@Autowired
		private BlogService blogService;

		@RequestMapping("blog/index")
		public @ResponseBody List<BlogBo> blogIndex(ModelMap modelMap) {
			BlogBo blogBo = new BlogBo();
			//blogBo.setId("5781b5a8bcfaa017c4f925d4");
			blogBo.setAuthorName("hwj");
			blogBo.setContent("关于中国");
			blogBo.setCreateDate(new Date());
			blogBo.setTitle("关于中国");
			List<TagBo> tags = new ArrayList<TagBo>();
			TagBo tagBo = null;
			for(int i=0;i<6;i++){
				tagBo = new TagBo();
				tagBo.setCode("xxx"+i);
				tagBo.setName("tagName_"+i);
				tags.add(tagBo);
			}
			blogBo.setTags(tags);
			//blogBo = blogService.saveOrUpdate(blogBo);
			List<BlogBo> blogs = blogService.list(blogBo);
			return null;
		}
		
		
		@RequestMapping("index")
		public @ResponseBody String index(ModelMap modelMap) {
			UserBo userBo = new UserBo();
			Long time = new Date().getTime();
			//userBo.setId(""+time);
			userBo.setPassword("junlenet");
			userBo.setPhone("130279814XX");
			userBo.setSex("NV");
			userBo.setUserName("www.junlenet.com");
			userBo = userService.save(userBo);
			Set<String> collections = userService.getCollectionNames();
			for (String str : collections) {
				System.out.println(str);
			}
			Pager pager = userService.selectPage(userBo, new Pager());
			@SuppressWarnings("unchecked")
			List<UserBo> users = pager.getResult();
			for (UserBo user : users) {
				System.out.println(JSONObject.toJSONString(user));
			}
			return JSON.toJSONString(pager);
		}
}
