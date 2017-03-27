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
import com.junlenet.mongodb.demo.session.MySessionContext;

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
			return "{\"ret\":-1,\"error\":\"error phone\"}";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		switch (registService.verification_send(phone)) {
		case 0:
			HttpSession session = request.getSession();
			session.setAttribute("phone", phone);
			session.setAttribute("verification", "111111");
			MySessionContext.AddSession(session);
			response= MySessionContext.putSessionIdToResponse(response, session);
			map.put("verification", "111111");
			map.put("ret", 0);
			break;
		case -1:
			map.put("ret", -1);
			map.put("error", "phone repeat");
			break;
		}
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/is_verification-right")
	@ResponseBody
	public String is_verification_right(String verification, HttpServletRequest request,
			HttpServletResponse response) {
		String sessionId = MySessionContext.getSessionIdFromRequest(request);
		if (!StringUtils.hasLength(sessionId)) {
			return "{\"ret\":-1,\"error\":\"sesssionId is null\"}";
		}
		if (!StringUtils.hasLength(verification)) {
			return "{\"ret\":-1,\"error\":\"verification is null\"}";
		}
		HttpSession session = MySessionContext.getSession(sessionId);
		if (session == null) {
			return "{\"ret\":-1,\"error\":\"sesssionId is null\"}";
		}
		String verification_session = (String) session.getAttribute("verification");
		Map<String, Object> map = new HashMap<String, Object>();
		if (verification_session.equals(verification)) {
			map.put("ret", 0);
			response = MySessionContext.putSessionIdToResponse(response, session);
		} else{
			map.put("ret", -1);
			map.put("error", "verification error");
		}
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/password-set")
	@ResponseBody
	public String password_set(String password1, String password2, HttpServletRequest request,
			HttpServletResponse response) {
		String sessionId = MySessionContext.getSessionIdFromRequest(request);
		if (!StringUtils.hasLength(sessionId)) {
			return "{\"ret\":-1,\"error\":\"sesssionId is null\"}";
		}
		if (!StringUtils.hasLength(password1) || !StringUtils.hasLength(password2) || !(password1.equals(password2))) {
			return "{\"ret\":-1,\"error\":\"password is error\"}";
		}
		HttpSession session = MySessionContext.getSession(sessionId);
		if (session == null) {
			return "{\"ret\":-1,\"error\":\"sesssionId is null\"}";
		}
		String phone = (String) session.getAttribute("phone");
		UserBo userBo = new UserBo();
		userBo.setPhone(phone);
		userBo.setPassword(password1);
		userService.save(userBo);
		return "{\"ret\":0}";
	}

}
