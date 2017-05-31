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

import com.lad.bo.OrganizationBo;
import com.lad.bo.UserBo;
import com.lad.service.IOrganizationService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.lad.vo.OrganizationVo;

@Controller
@RequestMapping("organization")
public class OrganizationController extends BaseContorller {

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
		OrganizationBo organizationBo = new OrganizationBo();
		organizationBo.setCreateuid(userBo.getId());
		organizationBo.setPosition(new double[] { px, py });
		organizationBo.setLandmark(landmark);
		organizationBo.setName(name);
		organizationBo.setTag(tag);
		organizationBo.setSub_tag(sub_tag);
		organizationService.insert(organizationBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/list")
	@ResponseBody
	public String list(@RequestParam(required = true) String tag,
			@RequestParam(required = true) String sub_tag,
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
		List<OrganizationBo> list = organizationService.selectByTag(tag,
				sub_tag);
		List<OrganizationVo> listVo = new LinkedList<OrganizationVo>();
		for (OrganizationBo item : list) {
			OrganizationVo organizationVo = new OrganizationVo();
			try {
				BeanUtils.copyProperties(organizationVo, item);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			listVo.add(organizationVo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("OrganizationList", listVo);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/apply-join")
	@ResponseBody
	public String appluJoin(
			@RequestParam(required = true) String organizationid,
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
		OrganizationBo organizationBo = organizationService.get(organizationid);
		if (organizationBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_IS_NULL.getReason());
		}
		HashSet<String> userApply = organizationBo.getUsersApply();
		if (userApply.contains(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_APPLY_EXIST.getIndex(),
					ERRORCODE.ORGANIZATION_APPLY_EXIST.getReason());
		}
		userApply.add(userBo.getId());
		organizationService.updateUsersApply(organizationid, userApply);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/apply-agree")
	@ResponseBody
	public String appluAgrees(
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
		OrganizationBo organizationBo = organizationService.get(organizationid);
		if (organizationBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_IS_NULL.getReason());
		}
		HashSet<String> userApply = organizationBo.getUsersApply();
		HashSet<String> users = organizationBo.getUsers();
		if (!userApply.contains(userid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_APPLY_USER_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_APPLY_USER_NULL.getReason());
		}
		userApply.remove(userid);
		users.add(userid);
		organizationService.updateUsersApply(organizationid, userApply);
		organizationService.updateUsers(organizationid, users);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

}
