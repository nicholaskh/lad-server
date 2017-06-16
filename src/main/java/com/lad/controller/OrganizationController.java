package com.lad.controller;

import com.lad.bo.OrganizationBo;
import com.lad.bo.UserBo;
import com.lad.service.IOrganizationService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.OrganizationVo;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Controller
@RequestMapping("organization")
public class OrganizationController extends BaseContorller {

	private static final Logger LOG = RootLogger.getLogger(OrganizationController.class);

	@Autowired
	private IOrganizationService organizationService;
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
		OrganizationBo organizationBo = new OrganizationBo();
		organizationBo.setCreateuid(userBo.getId());
		organizationBo.setPosition(new double[] { px, py });
		organizationBo.setLandmark(landmark);
		organizationBo.setName(name);
		organizationBo.setTag(tag);
		organizationBo.setSub_tag(sub_tag);
		//创始人默认为群主
		HashSet<String> masters = new HashSet<>();
		masters.add(userBo.getId());
		organizationBo.setMasters(masters);
		organizationService.insert(organizationBo);
		return Constant.COM_RESP;
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
		userBo = userService.getUser(userBo.getId());
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
		userBo = userService.getUser(userBo.getId());
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
		return Constant.COM_RESP;
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
		userBo = userService.getUser(userBo.getId());
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
		organizationService.updateMutil(organizationBo);
		return Constant.COM_RESP;
	}

	@RequestMapping("/set-description")
	@ResponseBody
	public String setDescription(
			@RequestParam(required = true) String organizationid,
			@RequestParam(required = true) String description,
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
		OrganizationBo organizationBo = organizationService.get(organizationid);
		if (organizationBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_IS_NULL.getReason());
		}
		organizationService.updateDescription(organizationid, description);
		return Constant.COM_RESP;
	}

	@RequestMapping("/info")
	@ResponseBody
	public String info(@RequestParam(required = true) String organizationid,
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
		OrganizationBo organizationBo = organizationService.get(organizationid);
		if (organizationBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_IS_NULL.getReason());
		}
		OrganizationVo organizationVo = new OrganizationVo();
		try {
			BeanUtils.copyProperties(organizationVo, organizationBo);
		} catch (IllegalAccessException e) {
			LOG.error(e);
		} catch (InvocationTargetException e) {
			LOG.error(e);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("organization", organizationVo);
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
		OrganizationBo organizationBo = organizationService.get(organizationid);
		HashSet<String> masters = organizationBo.getMasters();
		//操作人是不是群主
		boolean isMaster = false;
		//最开始 群主默认为 群组创建人
		if (masters.isEmpty()) {
			if (organizationBo.getCreateuid().equals(userBo.getId())) {
				isMaster = true;
				masters.add(userBo.getId());
			}
		} else if (masters.contains(userBo.getId())){
			isMaster = true;
		}
		if (isMaster) {
			HashSet<String> users = organizationBo.getUsers();
			if (!users.contains(userid)) {
				return CommonUtil.toErrorResult(
						ERRORCODE.ORGANIZATION_USER_NULL.getIndex(),
						ERRORCODE.ORGANIZATION_USER_NULL.getReason());
			}
			users.remove(userid);
			organizationBo.setMasters(masters);
			organizationService.updateMutil(organizationBo);
		} else {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_NOT_MASTER.getIndex(),
					ERRORCODE.ORGANIZATION_NOT_MASTER.getReason());
		}
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
		OrganizationBo organizationBo = organizationService.get(organizationid);
		HashSet<String> masters = organizationBo.getMasters();
		if (masters.isEmpty()) {
			if (userBo.getId().equals(organizationBo.getCreateuid())) {
				masters.add(userBo.getId());
			} else {
				return CommonUtil.toErrorResult(
						ERRORCODE.ORGANIZATION_NOT_MASTER.getIndex(),
						ERRORCODE.ORGANIZATION_NOT_MASTER.getReason());
			}
		}
		if (!masters.contains(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_NOT_MASTER.getIndex(),
					ERRORCODE.ORGANIZATION_NOT_MASTER.getReason());
		}
		if (userBo.getId().equals(userid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_IS_SELF.getIndex(),
					ERRORCODE.ORGANIZATION_IS_SELF.getReason());
		}
		if (!organizationBo.getUsers().contains(userid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ORGANIZATION_USER_NULL.getIndex(),
					ERRORCODE.ORGANIZATION_USER_NULL.getReason());
		}
		masters.remove(userBo.getId());
		masters.add(userid);
		organizationService.updateMutil(organizationBo);
		return Constant.COM_RESP;
	}

}
