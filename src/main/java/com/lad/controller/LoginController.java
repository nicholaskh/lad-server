package com.lad.controller;

import com.alibaba.fastjson.JSON;
import com.lad.bo.HomepageBo;
import com.lad.bo.UserBo;
import com.lad.redis.RedisServer;
import com.lad.service.IHomepageService;
import com.lad.service.IIMTermService;
import com.lad.service.ILoginService;
import com.lad.service.IUserService;
import com.lad.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Api("用户登录相关解耦")
@RestController
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

	private String weixin_ip = "https://api.weixin.qq.com/sns/oauth2/access_token?";

	private String weixin_user = "https://api.weixin.qq.com/sns/userinfo?";

	@ApiOperation("验证码发送")
	@PostMapping("/verification-send")
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

	@ApiOperation("快速登录")
	@PostMapping("/login-quick")
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
				// 在pushd创建用户
				String res = IMUtil.createUser(userBo.getId());
				if(!IMUtil.FINISH.equals(res)){
					return res;
				}
				// 从pushd获取连接token
				String token = IMUtil.getToken();
				if(token == null){
					return CommonUtil.toErrorResult(ERRORCODE.PUSHED_ERROR.getIndex(), "token produce error");
				}
				map.put("token",token);
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
				String token = IMUtil.getToken();
				if(token == null){
					return CommonUtil.toErrorResult(ERRORCODE.PUSHED_ERROR.getIndex(),
							"token produce error");
				}
				map.put("token",token);
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


	@ApiOperation("账号密码登录")
	@PostMapping("/login")
	public String login(@RequestParam("phone")String phone,
						@RequestParam("password")String password,
						HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (!StringUtils.hasLength(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_ERROR.getIndex(), ERRORCODE.ACCOUNT_PHONE_ERROR.getReason());
		}
		UserBo userBo = userService.checkByPhone(phone);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_EXIST.getIndex(), ERRORCODE.ACCOUNT_PHONE_EXIST.getReason());
		}
		if (!StringUtils.hasLength(password)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PASSWORD.getIndex(), ERRORCODE.ACCOUNT_PASSWORD.getReason());
		}
		password = CommonUtil.getSHA256(password);
		Map<String, Object> map = new HashMap<String, Object>();
		userBo = loginService.getUser(phone, password);
		if (userBo != null) {
			map.put("ret", 0);
			session.setAttribute("isLogin", true);
			session.setAttribute("userBo", userBo);
			session.setAttribute("loginTime", System.currentTimeMillis());

			// 从pushd获取连接token
			String token = IMUtil.getToken();
			if (token == null) {
				return CommonUtil.toErrorResult(ERRORCODE.PUSHED_ERROR.getIndex(), "token produce error");
			}
			map.put("token", token);
			map.put("userid", userBo.getId());
			logger.info("login ========== {} ; sessionid :{}", phone, session.getId());
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PASSWORD.getIndex(), ERRORCODE.ACCOUNT_PASSWORD.getReason());

		}
		return JSONObject.fromObject(map).toString();
	}


	@ApiOperation("退出登录")
	@GetMapping("/logout")
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
		session.invalidate();
		//登录时长
		long time = System.currentTimeMillis() - loginTime;
		double hours = time/3600000;
		DecimalFormat df = new DecimalFormat("###.00");
		double hour = Double.parseDouble(df.format(hours));
		userService.addUserLevel(userBo.getId(), time, Constant.LEVEL_HOUR, hour);
		logger.info("logout ========== {} ; sessionid :{}",userBo.getPhone(), session.getId());
		return Constant.COM_RESP;
	}



	@ApiOperation("微信授权登录")
	@PostMapping("/wx-open-login")
	public String openLogin(String code, String rantType, HttpServletRequest request, HttpServletResponse
			response){
		HttpSession session = request.getSession();
		rantType = StringUtils.isEmpty(rantType) ? "authorization_code" : rantType;
		StringBuilder url = new StringBuilder(weixin_ip);
		url.append("appid=").append(Constant.WX_APP_ID).append("&secret=").append(Constant.WX_APP_SECRET);
		url.append("&code=").append(code).append("&grant_type=").append(rantType);
		HttpClientUtil httpClient = HttpClientUtil.getInstance();
		String resp = httpClient.doGetRequest(url.toString());
		if (StringUtils.isEmpty(resp)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OPEN_ERROR.getIndex(),
					ERRORCODE.ACCOUNT_OPEN_ERROR.getReason());
		}
		logger.info("微信授权登录返回结果 ： {}" , resp);
		boolean isNew = false;
		Map<String, Object> map = new LinkedHashMap<>();
		try {
			JSONObject jsonObject = JSONObject.fromObject(resp);
			if (jsonObject.has("errcode")) {
				map.put("ret", jsonObject.getString("errcode"));
				map.put("error", jsonObject.getString("errmsg"));
				return JSONObject.fromObject(map).toString();
			}
			String openid = jsonObject.getString("openid");
			String access_token = jsonObject.getString("access_token");
			long expires_in = jsonObject.getLong("expires_in");
			String scope = jsonObject.getString("scope");
			String refresh_token = jsonObject.getString("refresh_token");

			StringBuilder userInforUrl = new StringBuilder(weixin_user);
			userInforUrl.append("access_token=").append(access_token).append("&openid=").append(openid);
			String userStr = httpClient.doGetRequest(userInforUrl.toString());
			logger.info("微信获取个人信息返回结果 ： {}" , userStr);
			if (StringUtils.isEmpty(userStr)) {
				return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OPEN_ERROR.getIndex(),
						ERRORCODE.ACCOUNT_OPEN_ERROR.getReason());
			}
			JSONObject userInfo = JSONObject.fromObject(userStr);
			if (userInfo.has("errcode")) {
				map.put("ret", jsonObject.getString("errcode"));
				map.put("error", jsonObject.getString("errmsg"));
				return JSONObject.fromObject(map).toString();
			}
			UserBo userBo = userService.findByOpenid(openid);
			if (userBo == null) {
				userBo = new UserBo();
				userBo.setOpenid(openid);
				isNew = true;
			}
			userBo.setAccessToken(access_token);
			userBo.setRefeshToken(refresh_token);
			userBo.setExpiresTime(expires_in);
			userBo.setScope(scope);
			userBo.setUserName(userInfo.getString("nickname"));
			userBo.setProvince(userInfo.getString("province"));
			userBo.setCity(userInfo.getString("city"));
			userBo.setHeadPictureName(userInfo.getString("headimgurl"));
			userBo.setUnionid(userInfo.getString("unionid"));
			userBo.setUpdateTime(new Date());
			int sex = userInfo.getInt("sex");
			userBo.setSex(sex == 1 ? "男" : "女");
			userService.save(userBo);
			if (isNew) {
				//新用户需要创建IM信息
				String res = IMUtil.createUser(userBo.getId());
				if(!IMUtil.FINISH.equals(res)){
					return res;
				}
			}
			// 从pushd获取连接token
			String token = IMUtil.getToken();
			if(token == null){
				return CommonUtil.toErrorResult(ERRORCODE.PUSHED_ERROR.getIndex(), "token produce error");
			}
			map.put("token",token);
			map.put("userid",userBo.getId());
			session.setAttribute("isLogin", true);
			session.setAttribute("loginTime", System.currentTimeMillis());
			session.setAttribute("userBo", userBo);
		} catch (Exception e) {
			logger.error(" 微信登录数据解析错误 ： {}", e);
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OPEN_ERROR.getIndex(),
					ERRORCODE.ACCOUNT_OPEN_ERROR.getReason());
		}
		return JSONObject.fromObject(map).toString();
	}
}
