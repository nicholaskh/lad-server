package com.lad.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lad.bo.IMTermBo;
import com.lad.bo.UserBo;
import com.lad.service.IIMTermService;
import com.lad.service.ILoginService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.pushd.ImAssistant;
import com.pushd.Message;

@Controller
@RequestMapping("login")
public class LoginController extends BaseContorller {

	@Autowired
	private ILoginService loginService;
	@Autowired
	private IIMTermService iMTermService;

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
		if (session.isNew()) {
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
		if (session.getAttribute("phone") == null) {
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
		UserBo userBo = loginService.getUser(phone, password);
		if (userBo != null) {
			map.put("ret", 0);
			session.setAttribute("isLogin", true);
			session.setAttribute("userBo", userBo);
			ImAssistant assistent = ImAssistant.init("180.76.173.200", 2222);
			if(assistent == null){
				return CommonUtil.toErrorResult(ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
						ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
			}
			IMTermBo iMTermBo = iMTermService.selectByUserid(userBo.getId());
			if(iMTermBo == null){
				iMTermBo = new IMTermBo();
				iMTermBo.setUserid(userBo.getId());
				Message message = assistent.getAppKey();
				String appKey = message.getMsg();
				Message message2 = assistent.authServer(appKey);
				String term = message2.getMsg();
				iMTermBo.setTerm(term);
				iMTermService.insert(iMTermBo);
			}
			assistent.setServerTerm(iMTermBo.getTerm());
			Message message3 = assistent.getToken();
			if(message3.getStatus() == Message.Status.termError){
				Message message = assistent.getAppKey();
				String appKey = message.getMsg();
				Message message2 = assistent.authServer(appKey);
				String term = message2.getMsg();
				iMTermService.updateByUserid(userBo.getId(), term);
			}else if (Message.Status.success != message3.getStatus()) {
				assistent.close();
				return CommonUtil.toErrorResult(message3.getStatus(), message3.getMsg());
			}
			map.put("token", message3.getMsg());
			assistent.close();
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
		if (session.isNew()) {
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
