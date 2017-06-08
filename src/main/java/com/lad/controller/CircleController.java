package com.lad.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.lad.bo.CircleBo;
import com.lad.bo.OrganizationBo;
import com.lad.bo.UserBo;
import com.lad.service.ICircleService;
import com.lad.service.IOrganizationService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.CircleVo;
import com.lad.vo.OrganizationVo;

@Controller
@RequestMapping("circle")
public class CircleController extends BaseContorller {

	@Autowired
	private ICircleService circleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrganizationService organizationService;

	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(@RequestParam(required = true) double px,
			@RequestParam(required = true) double py,
			@RequestParam(required = true) String landmark,
			@RequestParam(required = true) String name,
			@RequestParam(required = true) String tag,
			@RequestParam(required = true) String sub_tag,
			@RequestParam(required = true) String category,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = new CircleBo();
		circleBo.setCreateuid(userBo.getId());
		circleBo.setCategory(category);
		circleBo.setLandmark(landmark);
		circleBo.setPosition(new double[] { px, py });
		circleBo.setName(name);
		circleBo.setTag(tag);
		circleBo.setSub_tag(sub_tag);
		HashSet<String> users = new HashSet<String>();
		users.add(userBo.getId());
		circleBo.setUsers(users);
		circleService.insert(circleBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/add-organization")
	@ResponseBody
	public String addOrganization(
			@RequestParam(required = true) String organizationid,
			@RequestParam(required = true) String circleid,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (!circleBo.getCreateuid().equals(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
					ERRORCODE.CIRCLE_MASTER_NULL.getReason());
		}
		OrganizationBo organizationBo = organizationService.get(organizationid);
		if (null == organizationBo) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_IS_NULL.getReason());
		}
		HashSet<String> organizations = circleBo.getOrganizations();
		organizations.add(organizationBo.getId());
		circleService.updateOrganizations(circleid, organizations);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/head-picture")
	@ResponseBody
	public String head_picture(
			@RequestParam("head_picture") MultipartFile file,
			@RequestParam(required = true) String circleid,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":20002,\"error\":\":未登录\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":20002,\"error\":\":未登录\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":20002,\"error\":\":未登录\"}";
		}
		String userId = userBo.getId();
		String fileName = userId + file.getOriginalFilename();
		String path = CommonUtil.upload(file,
				Constant.CIRCLE_HEAD_PICTURE_PATH, fileName, 0);
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		circleService.updateHeadPicture(circleid, path);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("path", path);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/apply-insert")
	@ResponseBody
	public String applyIsnert(@RequestParam(required = true) String circleid,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		HashSet<String> usersApply = circleBo.getUsersApply();
		if (usersApply.contains(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_USER_EXIST.getIndex(),
					ERRORCODE.CIRCLE_USER_EXIST.getReason());
		}
		usersApply.add(userBo.getId());
		circleService.updateUsersApply(circleid, usersApply);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/my-info")
	@ResponseBody
	public String myInfo(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<CircleBo> circleBoList = circleService.selectByuserid(userBo
				.getId());
		List<CircleVo> CircleVoList = new LinkedList<CircleVo>();
		for (CircleBo CircleBo : circleBoList) {
			CircleVo circleVo = new CircleVo();
			try {
				BeanUtils.copyProperties(circleVo, CircleBo);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			CircleVoList.add(circleVo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("CircleVoList", CircleVoList);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/user-apply")
	@ResponseBody
	public String userApply(@RequestParam(required = true) String circleid,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		HashSet<String> usersApply = circleBo.getUsersApply();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("usersApply", usersApply);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/user-apply-agree")
	@ResponseBody
	public String userApplyAgree(
			@RequestParam(required = true) String circleid,
			@RequestParam(required = true) String userid,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (!circleBo.getCreateuid().equals(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		UserBo user = userService.getUser(userid);
		if (null == user) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_NULL.getIndex(),
					ERRORCODE.USER_NULL.getReason());
		}
		HashSet<String> usersApply = circleBo.getUsersApply();
		HashSet<String> users = circleBo.getUsers();
		if (!usersApply.contains(userid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_APPLY_USER_NULL.getIndex(),
					ERRORCODE.CIRCLE_APPLY_USER_NULL.getReason());
		}
		usersApply.remove(userid);
		users.add(userid);
		circleService.updateUsers(circleBo.getId(), users);
		circleService.updateUsersApply(circleBo.getId(), usersApply);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/user-apply-refuse")
	@ResponseBody
	public String userApplyRefuse(
			@RequestParam(required = true) String circleid,
			@RequestParam(required = true) String userid,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (!circleBo.getCreateuid().equals(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		UserBo user = userService.getUser(userid);
		if (null == user) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_NULL.getIndex(),
					ERRORCODE.USER_NULL.getReason());
		}
		HashSet<String> usersApply = circleBo.getUsersApply();
		HashSet<String> usersRefuse = circleBo.getUsersRefuse();
		if (!usersApply.contains(userid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_APPLY_USER_NULL.getIndex(),
					ERRORCODE.CIRCLE_APPLY_USER_NULL.getReason());
		}
		usersApply.remove(userid);
		usersRefuse.add(userid);
		circleService.updateUsersRefuse(circleBo.getId(), usersRefuse);
		circleService.updateUsersApply(circleBo.getId(), usersApply);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/list")
	@ResponseBody
	public String list(@RequestParam(required = true) String tag,
			@RequestParam(required = true) String sub_tag,
			@RequestParam(required = true) String category,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<CircleBo> list = circleService
				.selectByType(tag, sub_tag, category);
		List<CircleVo> listVo = new LinkedList<CircleVo>();
		for (CircleBo item : list) {
			CircleVo circleVo = new CircleVo();
			circleVo.setId(item.getId());
			circleVo.setName(item.getName());
			circleVo.setUsersSize((long) item.getUsers().size());
			circleVo.setNotesSize((long) item.getNotes().size());
			listVo.add(circleVo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("CircleVo", listVo);
		return JSONObject.fromObject(map).toString();
	}

}
