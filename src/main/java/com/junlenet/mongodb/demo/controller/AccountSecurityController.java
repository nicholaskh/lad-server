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
import com.junlenet.mongodb.demo.service.IUserService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("account-security")
public class AccountSecurityController extends BaseContorller {

	@Autowired
	private IUserService userService;
	
	@RequestMapping("/verification-send")
	@ResponseBody
	public String verification_send(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"login error\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":-1,\"error\":\"login error\"}";
		}
		if (session.getAttribute("userBo") == null) {
			return "{\"ret\":-1,\"error\":\"login error\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		session.setAttribute("account-security.verification-send", "111111");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/is-verification-right")
	@ResponseBody
	public String is_verification_right(String verification, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"sesssion is null\"}";
		}
		if (session.getAttribute("account-security.verification-send") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!StringUtils.hasLength(verification)) {
			return "{\"ret\":-1,\"error\":\"verification is null\"}";
		}
		String verification_session = (String) session.getAttribute("account-security.verification-send");
		Map<String, Object> map = new HashMap<String, Object>();
		if (verification_session.equals(verification)) {
			map.put("ret", 0);
			session.setAttribute("isVerificationRight", true);
		} else {
			map.put("ret", -1);
			map.put("error", "verification error");
		}
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/verification-send-phone")
	@ResponseBody
	public String verification_send_phone(String phone, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("isVerificationRight") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("userBo") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		session.setAttribute("account-security.verification-send-phone", "111111");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/phone-change")
	@ResponseBody
	public String phone_schange(String phone, String verification, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("account-security.verification-send-phone") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!StringUtils.hasLength(phone)) {
			return "{\"ret\":-1,\"error\":\"phone is null\"}";
		}
		if (!StringUtils.hasLength(verification)) {
			return "{\"ret\":-1,\"error\":\"verification is null\"}";
		}
		String verification_session = (String) session.getAttribute("account-security.verification-send-phone");
		Map<String, Object> map = new HashMap<String, Object>();
		if (verification_session.equals(verification)) {
			map.put("ret", 0);
			if (session.getAttribute("userBo") == null) {
				return "{\"ret\":-1,\"error\":\"error session\"}";
			}
			UserBo userBo = (UserBo) session.getAttribute("userBo");
			userBo.setPhone(phone);
			userService.updatePhone(userBo);
		} else {
			map.put("ret", -1);
			map.put("error", "verification error");
		}
		session.invalidate();
		return JSONObject.fromObject(map).toString();
	}

}
