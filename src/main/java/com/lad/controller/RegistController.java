package com.lad.controller;

import com.lad.bo.HomepageBo;
import com.lad.bo.IMTermBo;
import com.lad.bo.UserBo;
import com.lad.service.IHomepageService;
import com.lad.service.IIMTermService;
import com.lad.service.IRegistService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.IMUtil;
import com.pushd.ImAssistant;
import com.pushd.Message;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("regist")
public class RegistController extends BaseContorller {

	@Autowired
	private IRegistService registService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IHomepageService homepageService;
	@Autowired
	private IIMTermService iMTermService;


	@RequestMapping("/verification-send")
	@ResponseBody
	public String verification_send(String phone, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (!StringUtils.hasLength(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_ERROR.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_ERROR.getReason());
		}
		if (!CommonUtil.isRightPhone(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_ERROR.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_ERROR.getReason());
		}
		switch (registService.verification_send(phone)) {
		case 0:
			HttpSession session = request.getSession();
			session.setAttribute("phone", phone);
			String code = CommonUtil.getRandom();
			CommonUtil.sendSMS2(phone, CommonUtil.buildCodeMsg(code));
			session.setAttribute("verification", code);
			session.setAttribute("verification-time", System.currentTimeMillis());
			map.put("ret", 0);
			break;
		case -1:
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_REPEAT.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_REPEAT.getReason());
		}
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/is-verification-right")
	@ResponseBody
	public String is_verification_right(String verification, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		if (!StringUtils.hasLength(verification)) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		if (session.getAttribute("verification") == null) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		long codeTime = (long)session.getAttribute("verification-time");
		if (!CommonUtil.isTimeIn(codeTime)){
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_VERIFICATION_TIMEOUT.getIndex(),
					ERRORCODE.SECURITY_VERIFICATION_TIMEOUT.getReason());
		}
		String verification_session = (String) session.getAttribute("verification");
		Map<String, Object> map = new HashMap<String, Object>();
		if (verification_session.equals(verification)) {
			map.put("ret", 0);
			session.setAttribute("isVerificationRight", true);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/password-set")
	@ResponseBody
	public String password_set(String password1, String password2, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		if (session.getAttribute("isVerificationRight") == null) {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		if (!StringUtils.hasLength(password1) || !StringUtils.hasLength(password2) || !(password1.equals(password2))) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PASSWORD.getIndex(),
					ERRORCODE.ACCOUNT_PASSWORD.getReason());
		}
		String phone = (String) session.getAttribute("phone");
		if(registService.is_phone_repeat(phone)){
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_REPEAT.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_REPEAT.getReason());
		}
		UserBo userBo = new UserBo();
		Long time = System.currentTimeMillis()/1000;
		userBo.setUserName("user"+time);
		userBo.setPhone(phone);
		userBo.setPassword(CommonUtil.getSHA256(password1));
		userService.save(userBo);
		HomepageBo homepageBo = new HomepageBo();
		homepageBo.setOwner_id(userBo.getId());
		homepageService.insert(homepageBo);
		session.invalidate();
		ImAssistant assistent = ImAssistant.init("180.76.138.200", 2222);
		if (null == assistent) {
			return CommonUtil.toErrorResult(ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
		}
		try {
			String term = IMUtil.getTerm(assistent);
			IMTermBo iMTermBo = new IMTermBo();
			iMTermBo.setUserid(userBo.getId());
			iMTermBo.setTerm(term);
			iMTermService.insert(iMTermBo);
			assistent.setServerTerm(term);
			Message messageUser = assistent.createUser(userBo.getId());
			if (messageUser.getStatus() == Message.Status.termError) {
				term = IMUtil.getTerm(assistent);
				iMTermService.updateByUserid(userBo.getId(), term);
				messageUser = assistent.createUser(userBo.getId());
				if (Message.Status.success != messageUser.getStatus()) {
					return CommonUtil.toErrorResult(messageUser.getStatus(), messageUser.getMsg());
				}
			} else if (Message.Status.success != messageUser.getStatus()) {
				return CommonUtil.toErrorResult(messageUser.getStatus(), messageUser.getMsg());
			}
		} finally {
			assistent.close();
		}
		return Constant.COM_RESP;
	}
}
