package com.junlenet.mongodb.demo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.service.IUserService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("person-set")
public class PersonSet extends BaseContorller {

	@Autowired
	private IUserService userService;

	@RequestMapping("/username")
	@ResponseBody
	public String username(String username, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if(StringUtils.isEmpty(username)){
			return "{\"ret\":-1,\"error\":\"error username\"}";
		}
		userBo.setUserName(username);
		userService.updateUserName(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/birthday")
	@ResponseBody
	public String birth_day(String birthday, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if(StringUtils.isEmpty(birthday)){
			return "{\"ret\":-1,\"error\":\"error birthday\"}";
		}
		userBo.setBirthDay(birthday);
		userService.updateBirthDay(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/sex")
	@ResponseBody
	public String sex(String sex, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if(StringUtils.isEmpty(sex)){
			return "{\"ret\":-1,\"error\":\"error sex\"}";
		}
		userBo.setSex(sex);
		userService.updateSex(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/personalized-signature")
	@ResponseBody
	public String personalized_signature(String personalized_signature, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if(StringUtils.isEmpty(personalized_signature)){
			return "{\"ret\":-1,\"error\":\"error personalized_signature\"}";
		}
		userBo.setPersonalizedSignature(personalized_signature);
		userService.updatePersonalizedSignature(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

}
