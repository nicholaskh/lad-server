package com.junlenet.mongodb.demo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.service.IRegistService;
import com.junlenet.mongodb.demo.service.IUserService;
import com.junlenet.mongodb.demo.util.CommonUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("regist")
public class RegistController extends BaseContorller {

	@Autowired
	private IRegistService registService;
	@Autowired
	private IUserService userService;

	@RequestMapping("/verification-send")
	@ResponseBody
	public String verification_send(String phone, HttpServletRequest request, HttpServletResponse response) {
		if (!StringUtils.hasLength(phone)) {
			return "{\"ret\":10003,\"error\":\"手机号码错误\"}";
		}
		if (!CommonUtil.isRightPhone(phone)) {
			return "{\"ret\":10003,\"error\":\"手机号码错误\"}";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		switch (registService.verification_send(phone)) {
		case 0:
			HttpSession session = request.getSession();
			session.setAttribute("phone", phone);
			session.setAttribute("verification", "111111");
			map.put("ret", 0);
			break;
		case -1:
			map.put("ret", 10005);
			map.put("error", "手机号码重复");
			break;
		}
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/is-verification-right")
	@ResponseBody
	public String is_verification_right(String verification, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if(session.isNew()){
			return "{\"ret\":10009,\"error\":\"验证码错误\"}";
		}
		if (!StringUtils.hasLength(verification)) {
			return "{\"ret\":10009,\"error\":\"验证码错误\"}";
		}
		if(session.getAttribute("verification") == null){
			return "{\"ret\":10009,\"error\":\"验证码错误\"}";
		}
		String verification_session = (String) session.getAttribute("verification");
		Map<String, Object> map = new HashMap<String, Object>();
		if (verification_session.equals(verification)) {
			map.put("ret", 0);
			session.setAttribute("isVerificationRight", true);
		} else{
			map.put("ret", 10009);
			map.put("error", "验证码错误");
		}
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/password-set")
	@ResponseBody
	public String password_set(String password1, String password2, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if(session.isNew()){
			return "{\"ret\":10009,\"error\":\"验证码错误\"}";
		}
		if(session.getAttribute("isVerificationRight") == null){
			return "{\"ret\":10009,\"error\":\"验证码错误\"}";
		}
		if(session.getAttribute("isVerificationRight") == null){
			return "{\"ret\":10009,\"error\":\"验证码错误\"}";
		}
		if (!StringUtils.hasLength(password1) || !StringUtils.hasLength(password2) || !(password1.equals(password2))) {
			return "{\"ret\":-10003,\"error\":\"输入密码错误\"}";
		}

		String phone = (String) session.getAttribute("phone");
		UserBo userBo = new UserBo();
		userBo.setPhone(phone);
		userBo.setPassword(CommonUtil.getSHA256(password1));
		userService.save(userBo);
		session.invalidate();
		return "{\"ret\":0}";
	}
	
}
