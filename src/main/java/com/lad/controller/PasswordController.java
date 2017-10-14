package com.lad.controller;

import com.lad.bo.UserBo;
import com.lad.service.IRegistService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

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
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (!StringUtils.hasLength(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_ERROR.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_ERROR.getReason());
		}
		UserBo userBo = userService.checkByPhone(phone);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_EXIST.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_EXIST.getReason());
		}
		if (!StringUtils.hasLength(verification_img)) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		if (session.getAttribute("verification_img") == null) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		String verification_img_session = (String) session.getAttribute("verification_img");
		if (!verification_img_session.equals(verification_img)) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		String code = CommonUtil.getRandom();
		session.setAttribute("verification", code);
		session.setAttribute("verification-time", System.currentTimeMillis());
		CommonUtil.sendSMS2(phone, CommonUtil.buildPassMsg(code));
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
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (!StringUtils.hasLength(verification)) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_VERIFICATION_NULL.getIndex(),
					ERRORCODE.SECURITY_VERIFICATION_NULL.getReason());
		}
		String verification_session = (String) session.getAttribute("verification");
		if (verification_session == null) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_VERIFICATION_NULL.getIndex(),
					ERRORCODE.SECURITY_VERIFICATION_NULL.getReason());
		}
		long codeTime = (long)session.getAttribute("verification-time");
		if (!CommonUtil.isTimeIn(codeTime)){
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_VERIFICATION_TIMEOUT.getIndex(),
					ERRORCODE.SECURITY_VERIFICATION_TIMEOUT.getReason());
		}
		if (!verification_session.equals(verification)) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
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
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (!StringUtils.hasLength(password1) || !StringUtils.hasLength(password2)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PASSWORD.getIndex(),
					ERRORCODE.ACCOUNT_PASSWORD.getReason());
		}
		if (session.getAttribute("isVerification") == null) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		long codeTime = (long)session.getAttribute("verification-time");
		if (!CommonUtil.isTimeIn(codeTime)){
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_VERIFICATION_TIMEOUT.getIndex(),
					ERRORCODE.SECURITY_VERIFICATION_TIMEOUT.getReason());
		}
		String phone = (String) session.getAttribute("phone");
		UserBo userBo = userService.checkByPhone(phone);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_EXIST.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_EXIST.getReason());
		}
		userBo = userService.getUserByPhone(phone);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_ERROR.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_ERROR.getReason());
		}
		userBo.setPassword(CommonUtil.getSHA256(password1));
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
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (!StringUtils.hasLength(password1) || !StringUtils.hasLength(password2)
				|| !StringUtils.hasLength(old_password)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PASSWORD.getIndex(),
					ERRORCODE.ACCOUNT_PASSWORD.getReason());
		}
		if (!password1.equals(password2)) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_PASSWORD_INCONSISTENCY.getIndex(),
					ERRORCODE.SECURITY_PASSWORD_INCONSISTENCY.getReason());
		}
		if (session.getAttribute("userBo") == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		userBo = userService.getUser(userBo.getId());
		if (!CommonUtil.getSHA256(old_password).equals(userBo.getPassword())) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PASSWORD.getIndex(),
					ERRORCODE.ACCOUNT_PASSWORD.getReason());
		}
		userBo.setPassword(CommonUtil.getSHA256(password1));
		userService.updatePassword(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		session.invalidate();
		return JSONObject.fromObject(map).toString();
	}
}
