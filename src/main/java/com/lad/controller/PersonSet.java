package com.lad.controller;

import com.lad.bo.LocationBo;
import com.lad.bo.UserBo;
import com.lad.service.IFriendsService;
import com.lad.service.ILocationService;
import com.lad.service.IRegistService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.lad.vo.UserVo;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("person-set")
public class PersonSet extends BaseContorller {

	@Autowired
	private IUserService userService;
	@Autowired
	private IRegistService registService;
	@Autowired
	private ILocationService locationService;

	@Autowired
	private IFriendsService friendsService;

	@RequestMapping("/username")
	@ResponseBody
	public String username(String username, HttpServletRequest request, HttpServletResponse response) {
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
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		if (StringUtils.isEmpty(username)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_USERNAME.getIndex(), ERRORCODE.USER_USERNAME.getReason());
		}
		userBo.setUserName(username);
		userService.updateUserName(userBo);
		updateUserName(userBo.getId(), username);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/birthday")
	@ResponseBody
	public String birth_day(String birthday, HttpServletRequest request, HttpServletResponse response) {
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
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		if (StringUtils.isEmpty(birthday)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_BIRTHDAY.getIndex(), ERRORCODE.USER_BIRTHDAY.getReason());
		}
		userBo.setBirthDay(birthday);
		userService.updateBirthDay(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/sex")
	@ResponseBody
	public String sex(String sex, HttpServletRequest request, HttpServletResponse response) {
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
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (StringUtils.isEmpty(sex)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_SEX.getIndex(), ERRORCODE.USER_SEX.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		userBo.setSex(sex);
		userService.updateSex(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/personalized-signature")
	@ResponseBody
	public String personalized_signature(String personalized_signature, HttpServletRequest request,
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
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		if (StringUtils.isEmpty(personalized_signature)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_SIGNATURE.getIndex(), ERRORCODE.USER_SIGNATURE.getReason());
		}
		userBo.setPersonalizedSignature(personalized_signature);
		userService.updatePersonalizedSignature(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/user-info")
	@ResponseBody
	public String user_info(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		UserVo vo = new UserVo();
		BeanUtils.copyProperties(vo, userBo);
		map.put("user", vo);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/search-by-name")
	@ResponseBody
	public String searchByName(String name, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		if (StringUtils.isEmpty(name)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_USERNAME.getIndex(), ERRORCODE.USER_USERNAME.getReason());
		}
		List<UserBo> list = userService.getUserByName(name);
		List<UserVo> userVoList = new LinkedList<UserVo>();
		for (UserBo item : list) {
			UserVo vo = new UserVo();
			BeanUtils.copyProperties(vo, item);
			userVoList.add(vo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userList", userVoList);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/search-by-phone")
	@ResponseBody
	public String searchByPhone(String phone, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
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
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		if (StringUtils.isEmpty(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_PHONE.getIndex(), ERRORCODE.USER_PHONE.getReason());
		}
		if (!registService.is_phone_repeat(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_NULL.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_NULL.getReason());
		}
		UserBo temp = userService.getUserByPhone(phone);
		UserVo vo = new UserVo();
		BeanUtils.copyProperties(vo, temp);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("user", vo);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/search-by-userid")
	@ResponseBody
	public String searchByUserid(String userid, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
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
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		if (StringUtils.isEmpty(userid)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_ID.getIndex(), ERRORCODE.USER_ID.getReason());
		}
		UserBo temp = userService.getUser(userid);
		UserVo vo = new UserVo();
		BeanUtils.copyProperties(vo, temp);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("user", vo);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/location")
	@ResponseBody
	public String location(double px, double py, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
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
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		String userid = userBo.getId();
		LocationBo locationBo = locationService.getLocationBoByUserid(userid);
		if(null != locationBo){
			locationBo.setPosition(new double[]{px,py});
			locationBo = locationService.updateUserPoint(locationBo);
		}else{
			locationBo = new LocationBo();
			locationBo.setPosition(new double[]{px,py});
			locationBo.setUserid(userid);
			locationBo = locationService.insertUserPoint(locationBo);
		}
		userService.updateLocation(userBo.getPhone(), locationBo.getId());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@Async
	private void updateUserName(String userid, String username){
		friendsService.updateUsernameByFriend(userid, username, "");
	}

}
