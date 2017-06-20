package com.lad.controller;

import com.lad.bo.CircleBo;
import com.lad.bo.UserBo;
import com.lad.service.ICircleService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.CircleVo;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 圈子代理原本的群组，不在叫做圈子，取消原本的群组
 */
@Controller
@RequestMapping("organization")
public class CircleController extends BaseContorller {

	@Autowired
	private ICircleService circleService;
	@Autowired
	private IUserService userService;

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
		//每人最多创建三个群
		List<CircleBo> circleBos = circleService.findByCreateid(userBo.getId());
		if (circleBos != null && circleBos.size() >= 3) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_MAX.getIndex(),
					ERRORCODE.ORGANIZATION_MAX.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		CircleBo circleBo = new CircleBo();
		circleBo.setCreateuid(userBo.getId());
		circleBo.setCategory(category);
		circleBo.setLandmark(landmark);
		circleBo.setPosition(new double[] { px, py });
		circleBo.setName(name);
		circleBo.setTag(tag);
		circleBo.setSub_tag(sub_tag);
		HashSet<String> users = new HashSet<>();
		users.add(userBo.getId());
		circleBo.setUsers(users);
		circleService.insert(circleBo);
		return Constant.COM_RESP;
	}

	@RequestMapping("/head-picture")
	@ResponseBody
	public String head_picture(
			@RequestParam("head_picture") MultipartFile file,
			@RequestParam(required = true) String organizationid,
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
		userBo = userService.getUser(userBo.getId());
		String userId = userBo.getId();
		String fileName = userId + file.getOriginalFilename();
		String path = CommonUtil.upload(file,
				Constant.CIRCLE_HEAD_PICTURE_PATH, fileName, 0);
		CircleBo circleBo = circleService.selectById(organizationid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_IS_NULL.getReason());
		}
		circleService.updateHeadPicture(organizationid, path);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("path", path);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/apply-insert")
	@ResponseBody
	public String applyIsnert(@RequestParam(required = true) String organizationid,
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
		userBo = userService.getUser(userBo.getId());
		CircleBo circleBo = circleService.selectById(organizationid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_IS_NULL.getReason());
		}
		HashSet<String> users = circleBo.getUsers();
		if (users.size() >= 500) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_USER_MAX.getIndex(),
					ERRORCODE.ORGANIZATION_USER_MAX.getReason());
		}
		HashSet<String> usersApply = circleBo.getUsersApply();
		if (usersApply.contains(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_USER_EXIST.getIndex(),
					ERRORCODE.ORGANIZATION_USER_EXIST.getReason());
		}
		usersApply.add(userBo.getId());
		circleService.updateUsersApply(organizationid, usersApply);
		return Constant.COM_RESP;
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
		userBo = userService.getUser(userBo.getId());
		List<CircleBo> circleBoList = circleService.selectByuserid(userBo
				.getId());
		List<CircleVo> CircleVoList = new LinkedList<>();
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
		map.put("organizationList", CircleVoList);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/user-apply")
	@ResponseBody
	public String userApply(@RequestParam(required = true) String organizationid,
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
		userBo = userService.getUser(userBo.getId());
		CircleBo circleBo = circleService.selectById(organizationid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_IS_NULL.getReason());
		}
		HashSet<String> usersApply = circleBo.getUsersApply();
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("usersApply", usersApply);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/user-apply-agree")
	@ResponseBody
	public String userApplyAgree(
			@RequestParam(required = true) String organizationid,
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
		userBo = userService.getUser(userBo.getId());
		CircleBo circleBo = circleService.selectById(organizationid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_IS_NULL.getReason());
		}
		HashSet<String> users = circleBo.getUsers();
		if (users.size() >= 500) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_USER_MAX.getIndex(),
					ERRORCODE.ORGANIZATION_USER_MAX.getReason());
		}
		if (!circleBo.getCreateuid().equals(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_NOT_MASTER.getIndex(),
					ERRORCODE.ORGANIZATION_NOT_MASTER.getReason());
		}
		UserBo user = userService.getUser(userid);
		if (null == user) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_NULL.getIndex(),
					ERRORCODE.USER_NULL.getReason());
		}
		HashSet<String> usersApply = circleBo.getUsersApply();
		if (!usersApply.contains(userid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_APPLY_USER_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_APPLY_USER_NULL.getReason());
		}
		usersApply.remove(userid);
		users.add(userid);
		circleService.updateUsers(circleBo.getId(), users);
		circleService.updateUsersApply(circleBo.getId(), usersApply);
		return Constant.COM_RESP;
	}

	@RequestMapping("/user-apply-refuse")
	@ResponseBody
	public String userApplyRefuse(
			@RequestParam(required = true) String organizationid,
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
		userBo = userService.getUser(userBo.getId());
		CircleBo circleBo = circleService.selectById(organizationid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_IS_NULL.getReason());
		}
		if (!circleBo.getCreateuid().equals(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_NOT_MASTER.getIndex(),
					ERRORCODE.ORGANIZATION_NOT_MASTER.getReason());
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
					ERRORCODE.ORGANIZATION_APPLY_USER_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_APPLY_USER_NULL.getReason());
		}
		usersApply.remove(userid);
		usersRefuse.add(userid);
		circleService.updateUsersRefuse(circleBo.getId(), usersRefuse);
		circleService.updateUsersApply(circleBo.getId(), usersApply);
		return Constant.COM_RESP;
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
		userBo = userService.getUser(userBo.getId());
		List<CircleBo> list = circleService
				.selectByType(tag, sub_tag, category);
		List<CircleVo> listVo = new LinkedList<>();
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
		map.put("organizationList", listVo);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/delete-user")
	@ResponseBody
	public String delete(@RequestParam(required = true) String organizationid,
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
		userBo = userService.getUser(userBo.getId());
		CircleBo circleBo = circleService.selectById(organizationid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_IS_NULL.getReason());
		}
		if (!circleBo.getCreateuid().equals(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_NOT_MASTER.getIndex(),
					ERRORCODE.ORGANIZATION_NOT_MASTER.getReason());
		}
		HashSet<String> users = circleBo.getUsers();
		if (!users.contains(userid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_USER_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_USER_NULL.getReason());
		}
		users.remove(userid);
		circleService.updateUsers(circleBo.getId(), users);
		return Constant.COM_RESP;
	}

	@RequestMapping("/transfer")
	@ResponseBody
	public String transfer(@RequestParam(required = true) String organizationid,
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
		userBo = userService.getUser(userBo.getId());
		CircleBo circleBo = circleService.selectById(organizationid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_IS_NULL.getReason());
		}
		if (!circleBo.getCreateuid().equals(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_NOT_MASTER.getIndex(),
					ERRORCODE.ORGANIZATION_NOT_MASTER.getReason());
		}
		if (userBo.getId().equals(userid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_SELF.getIndex(),
					ERRORCODE.ORGANIZATION_IS_SELF.getReason());
		}
		if (!circleBo.getUsers().contains(userid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_USER_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_USER_NULL.getReason());
		}
		//创建者默认为群主
		circleBo.setCreateuid(userid);
		circleService.updateMaster(circleBo);
		return Constant.COM_RESP;
	}

	@RequestMapping("/quit")
	@ResponseBody
	public String delete(@RequestParam(required = true) String organizationid,
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
		CircleBo circleBo = circleService.selectById(organizationid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_IS_NULL.getReason());
		}
		HashSet<String> users = circleBo.getUsers();
		users.remove(userBo.getId());
		circleService.updateUsers(circleBo.getId(), users);
		return Constant.COM_RESP;
	}

}
