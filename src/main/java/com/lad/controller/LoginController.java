package com.lad.controller;

import com.lad.bo.HomepageBo;
import com.lad.bo.IMTermBo;
import com.lad.bo.UserBo;
import com.lad.redis.RedisServer;
import com.lad.service.IHomepageService;
import com.lad.service.IIMTermService;
import com.lad.service.ILoginService;
import com.lad.service.IUserService;
import com.lad.util.*;
import com.pushd.ImAssistant;
import com.pushd.Message;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("login")
public class LoginController extends BaseContorller {

	private static Logger logger = LogManager.getLogger(LoginController.class);

	@Autowired
	private ILoginService loginService;
	@Autowired
	private IIMTermService iMTermService;
	@Autowired
	private RedisServer redisServer;
	@Autowired
	private IHomepageService homepageService;

	@Autowired
	private IUserService userService;

	@RequestMapping("/verification-send")
	@ResponseBody
	public String verification_send(String phone, HttpServletRequest request, HttpServletResponse response) {
		if (!StringUtils.hasLength(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_ERROR.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_ERROR.getReason());
		}
		HttpSession session = request.getSession();
		session.setAttribute("phone", phone);
		String code = CommonUtil.getRandom();
		CommonUtil.sendSMS2(phone, CommonUtil.buildCodeMsg(code));
		session.setAttribute("verification", code);
		session.setAttribute("verification-time", System.currentTimeMillis());
		return Constant.COM_RESP;
	}

	@RequestMapping("/login-quick")
	@ResponseBody
	public String login_quick(String phone, String verification, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (!StringUtils.hasLength(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_NULL.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_NULL.getReason());
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
		Map<String, Object> map = new HashMap<>();
		long codeTime = (long)session.getAttribute("verification-time");
		if (!CommonUtil.isTimeIn(codeTime)){
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_VERIFICATION_TIMEOUT.getIndex(),
					ERRORCODE.SECURITY_VERIFICATION_TIMEOUT.getReason());
		}
		if (verification_session.equals(verification) && phone_session.equals(phone)) {
			map.put("ret", 0);
			UserBo userBo = userService.checkByPhone(phone);
			boolean isNew = false;
			ImAssistant assistent = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
			if (null == assistent) {
				return CommonUtil.toErrorResult(ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
						ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
			}
			String term = IMUtil.getTerm(assistent);
			if ("timeout".equals(term)) {
				term = IMUtil.getTerm(assistent);
				if ("timeout".equals(term)) {
					return CommonUtil.toErrorResult(ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
							ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
				}
			}
			if (userBo == null) {
				userBo = new UserBo();
				Long time = System.currentTimeMillis()/1000;
				userBo.setUserName("user"+time);
				userBo.setPhone(phone);
				String initPass = phone.substring(phone.length() - 6);
				userBo.setPassword(CommonUtil.getSHA256(initPass));
				userService.save(userBo);
				HomepageBo homepageBo = new HomepageBo();
				homepageBo.setOwner_id(userBo.getId());
				homepageService.insert(homepageBo);
				try {
					IMTermBo iMTermBo = new IMTermBo();
					iMTermBo.setUserid(userBo.getId());
					iMTermBo.setTerm(term);
					iMTermService.insert(iMTermBo);
					assistent.setServerTerm(term);
					Message messageUser = assistent.createUser(userBo.getId());

					Message token = assistent.getToken();
					if (messageUser.getStatus() == Message.Status.termError) {
						term = IMUtil.getTerm(assistent);
						iMTermService.updateByUserid(userBo.getId(), term);
						messageUser = assistent.createUser(userBo.getId());
						token = assistent.getToken();
						if (Message.Status.success != messageUser.getStatus()) {
							return CommonUtil.toErrorResult(messageUser.getStatus(), messageUser.getMsg());
						}
					} else if (Message.Status.success != messageUser.getStatus()) {
						return CommonUtil.toErrorResult(messageUser.getStatus(), messageUser.getMsg());
					}
					map.put("token",token.getMsg());
				} finally {
					assistent.close();
				}
				String msg = "";
				try {
					msg = new String(Constant.QUICK_LOGIN.getBytes(), "GBK");
				} catch (Exception e) {
					logger.error(e);
				}
				CommonUtil.sendSMS2(phone, msg);
				isNew = true;
			} else if (userBo.getDeleted() == Constant.DELETED) {
				userBo.setDeleted(Constant.ACTIVITY);
				userService.updateUserStatus(userBo.getId(), Constant.ACTIVITY);
			}
			if (!isNew){
				try {
					String token = update(assistent, userBo, term);
					map.put("token",token);
				} catch (MyException e) {
					return e.getMessage();
				}
			}
			map.put("userid",userBo.getId());
			session.setAttribute("isLogin", true);
			session.setAttribute("loginTime", System.currentTimeMillis());
			session.setAttribute("userBo", userBo);
			logger.info("quick login ========== {} ; sessionid :{}",phone, session.getId());
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.SECURITY_WRONG_VERIFICATION.getIndex(),
					ERRORCODE.SECURITY_WRONG_VERIFICATION.getReason());
		}
		return JSONObject.fromObject(map).toString();
	}

	private String update(ImAssistant assistent, UserBo userBo, String term) throws MyException{
		IMTermBo iMTermBo = iMTermService.selectByUserid(userBo.getId());
		if(iMTermBo == null){
			iMTermBo = new IMTermBo();
			iMTermBo.setUserid(userBo.getId());
			iMTermBo.setTerm(term);
			iMTermService.insert(iMTermBo);
		}
		assistent.setServerTerm(iMTermBo.getTerm());
		Message message3 = assistent.getToken();
		if(message3.getStatus() == Message.Status.termError){
			term =IMUtil.getTerm(assistent);
			iMTermService.updateByUserid(userBo.getId(), term);
			message3 = assistent.getToken();
			if(Message.Status.success != message3.getStatus()){
				assistent.close();
				throw new MyException(CommonUtil.toErrorResult(message3.getStatus(),
						message3.getMsg()));
			}
			return message3.getMsg();

		}else if (Message.Status.success != message3.getStatus()) {
			assistent.close();
			throw new MyException(CommonUtil.toErrorResult(message3.getStatus(),
					message3.getMsg()));
		}
		return message3.getMsg();
	}

	@RequestMapping("/login")
	@ResponseBody
	public String login(@RequestParam("phone")String phone,
						@RequestParam("password")String password,
						HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (!StringUtils.hasLength(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_ERROR.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_ERROR.getReason());
		}
		UserBo userBo = userService.checkByPhone(phone);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_EXIST.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_EXIST.getReason());
		}
		if (!StringUtils.hasLength(password)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PASSWORD.getIndex(),
					ERRORCODE.ACCOUNT_PASSWORD.getReason());
		}
		password = CommonUtil.getSHA256(password);
		Map<String, Object> map = new HashMap<String, Object>();
		userBo = loginService.getUser(phone, password);
		if (userBo != null) {
			map.put("ret", 0);
			session.setAttribute("isLogin", true);
			session.setAttribute("userBo", userBo);
			session.setAttribute("loginTime", System.currentTimeMillis());
			ImAssistant assistent = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
			if(assistent == null){
				return CommonUtil.toErrorResult(ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
						ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
			}
			String term = IMUtil.getTerm(assistent);
			if ("timeout".equals(term)) {
				term = IMUtil.getTerm(assistent);
				if ("timeout".equals(term)) {
					return CommonUtil.toErrorResult(ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
							ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
				}
			}
			IMTermBo iMTermBo = iMTermService.selectByUserid(userBo.getId());
			if(iMTermBo == null){
				iMTermBo = new IMTermBo();
				iMTermBo.setUserid(userBo.getId());
				iMTermBo.setTerm(term);
				iMTermService.insert(iMTermBo);
			}
			assistent.setServerTerm(iMTermBo.getTerm());
			Message message3 = assistent.getToken();
			if(message3.getStatus() == Message.Status.termError){
				term =IMUtil.getTerm(assistent);
				iMTermService.updateByUserid(userBo.getId(), term);
			}else if (Message.Status.success != message3.getStatus()) {
				assistent.close();
				return CommonUtil.toErrorResult(message3.getStatus(), message3.getMsg());
			}
			map.put("token", message3.getMsg());
			map.put("userid",userBo.getId());
			logger.info("login ========== {} ; sessionid :{}",phone, session.getId());
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
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		long loginTime = (long)session.getAttribute("loginTime");
		long time = System.currentTimeMillis() - loginTime;
		userService.addUserLevel(userBo.getId(), time, Constant.LEVEL_HOUR);
		session.invalidate();
		logger.info("logout ========== {} ; sessionid :{}",userBo.getPhone(), session.getId());
		return Constant.COM_RESP;
	}

}
