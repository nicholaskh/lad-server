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

import com.junlenet.mongodb.demo.service.ILoginService;
import com.junlenet.mongodb.demo.util.CommonUtil;
import com.junlenet.mongodb.demo.util.ERRORCODE;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("login")
public class LoginController extends BaseContorller {

	@Autowired
	private ILoginService loginService;

	@RequestMapping("/verification-send")
	@ResponseBody
	public String verification_send(String phone, HttpServletRequest request, HttpServletResponse response) {
		if (!StringUtils.hasLength(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_ERROR.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_ERROR.getReason());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		session.setAttribute("phone", phone);
		session.setAttribute("verification", "111111");
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/login-quick")
	@ResponseBody
	public String login_quick(String phone, String verification, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if(session.isNew()){
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		if (!StringUtils.hasLength(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_ERROR.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_ERROR.getReason());
		}
		if (!StringUtils.hasLength(verification)) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		String verification_session = (String) session.getAttribute("verification");
		if(session.getAttribute("phone") == null){
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		String phone_session = (String) session.getAttribute("phone");
		Map<String, Object> map = new HashMap<String, Object>();
		if (verification_session.equals(verification) && phone_session.equals(phone)) {
			map.put("ret", 0);
			session.setAttribute("isLogin", true);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/login")
	@ResponseBody
	public String login(String phone, String password, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (!StringUtils.hasLength(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_ERROR.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_ERROR.getReason());
		}
		if (!StringUtils.hasLength(password)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PASSWORD.getIndex(),
					ERRORCODE.ACCOUNT_PASSWORD.getReason());
		}
		password = CommonUtil.getSHA256(password);
		Map<String, Object> map = new HashMap<String, Object>();
		if (loginService.getUser(phone, password) != null) {
			map.put("ret", 0);
			session.setAttribute("isLogin", true);
			session.setAttribute("userBo", loginService.getUser(phone, password));
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PASSWORD.getIndex(),
					ERRORCODE.ACCOUNT_PASSWORD.getReason());
		}
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/logout")
	@ResponseBody
	public String loginout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if(session.isNew()){
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		session.invalidate();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
}
