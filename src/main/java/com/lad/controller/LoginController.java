package com.lad.controller;

import com.lad.bo.IMTermBo;
import com.lad.bo.UserBo;
import com.lad.redis.RedisServer;
import com.lad.service.IIMTermService;
import com.lad.service.ILoginService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.IMUtil;
import com.pushd.ImAssistant;
import com.pushd.Message;
import net.sf.json.JSONObject;
import org.redisson.api.RLock;
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

	@Autowired
	private ILoginService loginService;
	@Autowired
	private IIMTermService iMTermService;
	@Autowired
	private RedisServer redisServer;

	@Autowired
	private IUserService userService;

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
	public String login(@RequestParam("phone")String phone,
						@RequestParam("password")String password,
						HttpServletRequest request, HttpServletResponse response) {

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
			ImAssistant assistent = ImAssistant.init("180.76.138.200", 2222);
			if(assistent == null){
				return CommonUtil.toErrorResult(ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
						ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
			}
			IMTermBo iMTermBo = iMTermService.selectByUserid(userBo.getId());
			if(iMTermBo == null){
				iMTermBo = new IMTermBo();
				iMTermBo.setUserid(userBo.getId());
				String term =IMUtil.getTerm(assistent);
				iMTermBo.setTerm(term);
				iMTermService.insert(iMTermBo);
			}
			assistent.setServerTerm(iMTermBo.getTerm());
			Message message3 = assistent.getToken();
			if(message3.getStatus() == Message.Status.termError){
				String term =IMUtil.getTerm(assistent);
				iMTermService.updateByUserid(userBo.getId(), term);
			}else if (Message.Status.success != message3.getStatus()) {
				assistent.close();
				return CommonUtil.toErrorResult(message3.getStatus(), message3.getMsg());
			}
			map.put("token", message3.getMsg());
			map.put("userid",userBo.getId());
			assistent.close();
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PASSWORD.getIndex(),
					ERRORCODE.ACCOUNT_PASSWORD.getReason());
		}
		return JSONObject.fromObject(map).toString();
	}


	@RequestMapping("/login-no")
	@ResponseBody
	public String loginNoPas(String phone, HttpServletRequest request,
							 HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (!StringUtils.hasLength(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_ERROR.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_ERROR.getReason());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		UserBo userBo = userService.getUserByPhone(phone);
		RLock lock = redisServer.getRLock(Constant.CHAT_LOCK);
		lock.lock();
		try {
			System.out.println(lock.getName());

			if (userBo != null) {
				map.put("ret", 0);
				session.setAttribute("isLogin", true);
				session.setAttribute("userBo", userBo);
				System.out.println("===========" + session);
				ImAssistant assistent = ImAssistant.init("180.76.138.200", 2222);
				if(assistent == null){
					return CommonUtil.toErrorResult(ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
							ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
				}
				IMTermBo iMTermBo = iMTermService.selectByUserid(userBo.getId());
				if(iMTermBo == null){
					iMTermBo = new IMTermBo();
					iMTermBo.setUserid(userBo.getId());
					String term =IMUtil.getTerm(assistent);
					iMTermBo.setTerm(term);
					iMTermService.insert(iMTermBo);
				}
				assistent.setServerTerm(iMTermBo.getTerm());
				Message message3 = assistent.getToken();
				if(message3.getStatus() == Message.Status.termError){
					String term =IMUtil.getTerm(assistent);
					iMTermService.updateByUserid(userBo.getId(), term);
				}else if (Message.Status.success != message3.getStatus()) {
					assistent.close();
					return CommonUtil.toErrorResult(message3.getStatus(), message3.getMsg());
				}
				map.put("token", message3.getMsg());
				assistent.close();
			} else {
				return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_ERROR.getIndex(),
						ERRORCODE.ACCOUNT_PHONE_ERROR.getReason());
			}
		} catch (Exception e) {
			map.put("errot",  e.getMessage());
		} finally {
			lock.unlock();
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
