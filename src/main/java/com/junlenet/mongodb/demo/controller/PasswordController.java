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
import com.junlenet.mongodb.demo.util.CommonUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("password")
public class PasswordController extends BaseContorller {

	@Autowired
	private IRegistService registService;
	@Autowired
	private IUserService userService;

	@RequestMapping("/verification-send")
	@ResponseBody
	public String verification_send(String phone, String verification_img, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!StringUtils.hasLength(phone)) {
			return "{\"ret\":-1,\"error\":\"error phone\"}";
		}
		if (!StringUtils.hasLength(verification_img)) {
			return "{\"ret\":-1,\"error\":\"error verification\"}";
		}
		if (!registService.is_phone_repeat(phone)) {
			return "{\"ret\":-1,\"error\":\"error phone\"}";
		}
		if (session.getAttribute("verification_img") == null) {
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
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!StringUtils.hasLength(verification)) {
			return "{\"ret\":-1,\"error\":\"verification is null\"}";
		}
		if (session.getAttribute("verification") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		String verification_session = (String) session.getAttribute("verification");
		if (!verification_session.equals(verification)) {
			return "{\"ret\":-1,\"error\":\"error verification\"}";
		}
		session.setAttribute("isVerification", true);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/password-set")
	@ResponseBody
	public String password_set(String password1, String password2, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!StringUtils.hasLength(password1) || !StringUtils.hasLength(password2)) {
			return "{\"ret\":-1,\"error\":\"password is null\"}";
		}
		if (session.getAttribute("isVerification") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!(Boolean) session.getAttribute("isVerification")) {
			return "{\"ret\":-1,\"error\":\"error isVerification\"}";
		}
		String phone = (String) session.getAttribute("phone");
		UserBo userBo = new UserBo();
		userBo.setPassword(CommonUtil.getSHA256(password1));
		userBo.setPhone(phone);
		userService.updatePassword(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		session.invalidate();
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/password-change")
	@ResponseBody
	public String password_change(String old_password, String password1, String password2, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!StringUtils.hasLength(password1) || !StringUtils.hasLength(password2)
				|| !StringUtils.hasLength(old_password)) {
			return "{\"ret\":-1,\"error\":\"password is error\"}";
		}
		if (!password1.equals(password2)) {
			return "{\"ret\":-1,\"error\":\"password is error\"}";
		}
		if (session.getAttribute("userBo") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if(!old_password.equals(userBo.getPassword())){
			return "{\"ret\":-1,\"error\":\"password is error\"}";
		}
		userBo.setPassword(CommonUtil.getSHA256(password1));
		userService.updatePassword(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		session.invalidate();
		return JSONObject.fromObject(map).toString();
	}
}
