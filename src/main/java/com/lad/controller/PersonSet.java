package com.lad.controller;

import com.lad.bo.*;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.lad.vo.CircleBaseVo;
import com.lad.vo.UserBaseVo;
import com.lad.vo.UserInfoVo;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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

	@Autowired
	private ICircleService circleService;

	@RequestMapping("/username")
	@ResponseBody
	public String username(String username, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isEmpty(username)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_USERNAME.getIndex(), ERRORCODE.USER_USERNAME.getReason());
		}
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
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
		if (StringUtils.isEmpty(birthday)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_BIRTHDAY.getIndex(), ERRORCODE.USER_BIRTHDAY.getReason());
		}
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
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
		if (StringUtils.isEmpty(sex)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_SEX.getIndex(), ERRORCODE.USER_SEX.getReason());
		}
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
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
		if (StringUtils.isEmpty(personalized_signature)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_SIGNATURE.getIndex(), ERRORCODE.USER_SIGNATURE.getReason());
		}
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo.setPersonalizedSignature(personalized_signature);
		userService.updatePersonalizedSignature(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/user-info")
	@ResponseBody
	public String user_info(HttpServletRequest request, HttpServletResponse response) throws
			Exception {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserInfoVo infoVo = new UserInfoVo();
		bo2vo(userBo, infoVo);
		LocationBo locationBo = locationService.getLocationBoByUserid(userBo.getId());
		if (locationBo != null) {
			infoVo.setPostion(locationBo.getPosition());
		}
		List<CircleBo> circleBos = circleService.findMyCircles(userBo.getId(), "", true, 4);
		List<CircleBaseVo> circles = new LinkedList<>();
		for (CircleBo circleBo : circleBos) {
			CircleBaseVo circleBaseVo = new CircleBaseVo();
			BeanUtils.copyProperties(circleBo,circleBaseVo);
			circleBaseVo.setCircleid(circleBo.getId());
			circleBaseVo.setNotesSize(circleBo.getNoteSize());
			circleBaseVo.setUsersSize(circleBo.getTotal());
			circles.add(circleBaseVo);
		}
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("ret", 0);
		map.put("user", infoVo);
		map.put("userCricles", circles);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/search-by-name")
	@ResponseBody
	public String searchByName(String name, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (StringUtils.isEmpty(name)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_USERNAME.getIndex(), ERRORCODE.USER_USERNAME.getReason());
		}
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<UserBo> list = userService.getUserByName(name);
		List<UserBaseVo> userVoList = new LinkedList<>();
		for (UserBo item : list) {
			UserBaseVo vo = new UserBaseVo();
			BeanUtils.copyProperties(item, vo);
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
		if (StringUtils.isEmpty(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_PHONE.getIndex(), ERRORCODE.USER_PHONE.getReason());
		}
		if (!registService.is_phone_repeat(phone)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_PHONE_NULL.getIndex(),
					ERRORCODE.ACCOUNT_PHONE_NULL.getReason());
		}
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo temp = userService.getUserByPhone(phone);
		UserBaseVo vo = new UserBaseVo();
		BeanUtils.copyProperties(temp, vo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("user", vo);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/search-by-userid")
	@ResponseBody
	public String searchByUserid(String userid, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (StringUtils.isEmpty(userid)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_ID.getIndex(), ERRORCODE.USER_ID.getReason());
		}
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo temp = userService.getUser(userid);
		UserBaseVo vo = new UserBaseVo();
		BeanUtils.copyProperties(temp, vo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("user", vo);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/location")
	@ResponseBody
	public String location(double px, double py, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
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


	private void bo2vo(UserBo userBo, UserInfoVo infoVo){
		BeanUtils.copyProperties(userBo, infoVo);
		UserTasteBo tasteBo = userService.findByUserId(userBo.getId());
		if (tasteBo == null) {
			tasteBo = new UserTasteBo();
			tasteBo.setUserid(userBo.getId());
			userService.addUserTaste(tasteBo);
		}
		infoVo.setSports(tasteBo.getSports());
		infoVo.setMusics(tasteBo.getMusics());
		infoVo.setLifes(tasteBo.getLifes());
		infoVo.setTrips(tasteBo.getTrips());
		infoVo.setRegistTime(userBo.getCreateTime());
	}

}
