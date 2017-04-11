package com.junlenet.mongodb.demo.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.junlenet.mongodb.demo.bo.HomepageBo;
import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.service.IHomepageService;

import net.sf.json.JSONObject;

@Controller
@Scope("prototype")
@RequestMapping("homepage")
public class HomepageController extends BaseContorller {
	@Autowired
	private IHomepageService homepageService;

	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!(Boolean) session.getAttribute("isLogin")) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		HomepageBo homepageBo = new HomepageBo();
		homepageBo.setOwner_id(userBo.getId());
		homepageService.insert(homepageBo);
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/visit_my_homepage")
	@ResponseBody
	public String visit_my_homepage(String visitor_id, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!(Boolean) session.getAttribute("isLogin")) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!StringUtils.hasLength(visitor_id)) {
			return "{\"ret\":-1,\"error\":\"error visitor_id\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		HomepageBo homepageBo = homepageService.selectByUserId(userBo.getId());
		if (homepageBo == null) {
			return "{\"ret\":-1,\"error\":\"error user\"}";
		}
		LinkedList<String> visitor_ids = homepageBo.getVisitor_ids();
		if(visitor_ids == null){
			visitor_ids = new LinkedList<String>();
		}
		if(visitor_ids.contains(visitor_id)){
			return "{\"ret\":-1,\"error\":\"duplicate visitor_id\"}";
		}
		visitor_ids.add(visitor_id);
		Integer new_visitors_count = homepageBo.getNew_visitors_count();
		if(new_visitors_count == null){
			new_visitors_count = 0;
		}
		new_visitors_count++;
		Integer total_visitors_count = homepageBo.getNew_visitors_count();
		if(total_visitors_count == null){
			total_visitors_count = 0;
		}
		total_visitors_count++;
		homepageBo.setNew_visitors_count(new_visitors_count);
		homepageBo.setTotal_visitors_count(total_visitors_count);
		homepageBo.setVisitor_ids(visitor_ids);
		homepageService.update_total_visitors_count(homepageBo);
		homepageService.update_new_visitors_count(homepageBo);
		homepageService.update_visitor_ids(homepageBo);
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/new_visitors_count")
	@ResponseBody
	public String new_visitors_count(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!(Boolean) session.getAttribute("isLogin")) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		HomepageBo homepageBo = homepageService.selectByUserId(userBo.getId());
		if (homepageBo == null) {
			return "{\"ret\":-1,\"error\":\"error user\"}";
		}
		Integer new_visitors_count = homepageBo.getNew_visitors_count();
		if(new_visitors_count == null){
			new_visitors_count = 0;
		}
		map.put("ret", 0);
		map.put("new_visitors_count", new_visitors_count);
		homepageBo.setNew_visitors_count(0);
		homepageService.update_new_visitors_count(homepageBo);
		return JSONObject.fromObject(map).toString();
	}
}
