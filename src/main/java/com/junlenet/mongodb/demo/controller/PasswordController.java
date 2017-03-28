package com.junlenet.mongodb.demo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
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
import com.junlenet.mongodb.demo.session.MySessionContext;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("password")
public class PasswordController extends BaseContorller {

	@Autowired
	private IRegistService registService;
	@Autowired
	private IUserService userService;
	
	@RequestMapping("/verification-generator")
	@ResponseBody
	public String verification_generator(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		session.setAttribute("verification_img", "fshg");
		MySessionContext.AddSession(session);
		Cookie cookie = new Cookie("sessionId", session.getId());
		response.addCookie(cookie);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("verification_img", "fshg");
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/verification-send")
	@ResponseBody
	public String verification_send(String phone, String verification_img, HttpServletRequest request,
			HttpServletResponse response) {
		if (!StringUtils.hasLength(phone)) {
			return "{\"ret\":-1,\"error\":\"error phone\"}";
		}
		if (!StringUtils.hasLength(verification_img)) {
			return "{\"ret\":-1,\"error\":\"error verification\"}";
		}
		if (!registService.is_phone_repeat(phone)) {
			return "{\"ret\":-1,\"error\":\"error phone\"}";
		}
		String sessionId = MySessionContext.getSessionIdFromRequest(request);
		if (!StringUtils.hasLength(sessionId)) {
			return "{\"ret\":-1,\"error\":\"session is null\"}";
		}
		HttpSession session = MySessionContext.getSession(sessionId);
		if (session == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		String verification_img_session = (String) session.getAttribute("verification_img");
		if (!verification_img_session.equals(verification_img)) {
			return "{\"ret\":-1,\"error\":\"error verification_img\"}";
		}
		session.setAttribute("verification", "111111");
		session.setAttribute("phone", phone);
		Cookie cookie = new Cookie("sessionId", session.getId());
		response.addCookie(cookie);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/verification-check")
	@ResponseBody
	public String verification_check(String verification, HttpServletRequest request, HttpServletResponse response) {
		if (!StringUtils.hasLength(verification)) {
			return "{\"ret\":-1,\"error\":\"verification is null\"}";
		}
		String sessionId = MySessionContext.getSessionIdFromRequest(request);
		if (!StringUtils.hasLength(sessionId)) {
			return "{\"ret\":-1,\"error\":\"session is null\"}";
		}
		HttpSession session = MySessionContext.getSession(sessionId);
		String verification_session = (String) session.getAttribute("verification");
		if (!verification_session.equals(verification)) {
			return "{\"ret\":-1,\"error\":\"error verification\"}";
		}
		Cookie cookie = new Cookie("sessionId", session.getId());
		response.addCookie(cookie);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/password-set")
	@ResponseBody
	public String password_set(String password1, String password2, HttpServletRequest request, HttpServletResponse response) {
		if (!StringUtils.hasLength(password1) || !StringUtils.hasLength(password2)) {
			return "{\"ret\":-1,\"error\":\"password is null\"}";
		}
		String sessionId = MySessionContext.getSessionIdFromRequest(request);
		if (!StringUtils.hasLength(sessionId)) {
			return "{\"ret\":-1,\"error\":\"session is null\"}";
		}
		HttpSession session = MySessionContext.getSession(sessionId);
		String phone = (String) session.getAttribute("phone");
		UserBo userBo  = new UserBo();
		userBo.setPassword(password1);
		userBo.setPhone(phone);
		userService.updatePassword(userBo);
		Cookie cookie = new Cookie("sessionId", session.getId());
		response.addCookie(cookie);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
}
