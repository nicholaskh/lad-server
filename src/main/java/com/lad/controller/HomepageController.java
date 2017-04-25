package com.lad.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lad.bo.HomepageBo;
import com.lad.bo.ThumbsupBo;
import com.lad.bo.UserBo;
import com.lad.service.IHomepageService;
import com.lad.service.IThumbsupService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.lad.vo.ThumbsupVo;

import net.sf.json.JSONObject;

@Controller
@Scope("prototype")
@RequestMapping("homepage")
public class HomepageController extends BaseContorller {

	@Autowired
	private IHomepageService homepageService;
	@Autowired
	private IThumbsupService thumbsupService;

	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(HttpServletRequest request, HttpServletResponse response) {
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
		Map<String, Object> map = new HashMap<String, Object>();
		HomepageBo homepageBo = new HomepageBo();
		homepageBo.setOwner_id(userBo.getId());
		homepageService.insert(homepageBo);
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/visit-my-homepage")
	@ResponseBody
	public String visit_my_homepage(String visitor_id, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (!StringUtils.hasLength(visitor_id)) {
			return CommonUtil.toErrorResult(ERRORCODE.CONTACT_VISITOR.getIndex(),
					ERRORCODE.CONTACT_VISITOR.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		HomepageBo homepageBo = homepageService.selectByUserId(userBo.getId());
		if (homepageBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CONTACT_HOMEPAGE.getIndex(),
					ERRORCODE.CONTACT_HOMEPAGE.getReason());
		}
		LinkedList<String> visitor_ids = homepageBo.getVisitor_ids();
		if (visitor_ids == null) {
			visitor_ids = new LinkedList<String>();
		}
		visitor_ids.add(visitor_id);
		Integer new_visitors_count = homepageBo.getNew_visitors_count();
		if (new_visitors_count == null) {
			new_visitors_count = 0;
		}
		new_visitors_count++;
		Integer total_visitors_count = homepageBo.getNew_visitors_count();
		if (total_visitors_count == null) {
			total_visitors_count = 0;
		}
		total_visitors_count++;
		homepageBo.setNew_visitors_count(new_visitors_count);
		homepageBo.setTotal_visitors_count(total_visitors_count);
		homepageBo.setVisitor_ids(visitor_ids);
		homepageService.update_total_visitors_count(homepageBo);
		homepageService.update_new_visitors_count(homepageBo);
		homepageService.update_visitor_ids(homepageBo);
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/new-visitors-count")
	@ResponseBody
	public String new_visitors_count(HttpServletRequest request, HttpServletResponse response) {
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
		Map<String, Object> map = new HashMap<String, Object>();
		HomepageBo homepageBo = homepageService.selectByUserId(userBo.getId());
		if (homepageBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CONTACT_HOMEPAGE.getIndex(),
					ERRORCODE.CONTACT_HOMEPAGE.getReason());
		}
		Integer new_visitors_count = homepageBo.getNew_visitors_count();
		if (new_visitors_count == null) {
			new_visitors_count = 0;
		}
		map.put("ret", 0);
		map.put("new_visitors_count", new_visitors_count);
		homepageBo.setNew_visitors_count(0);
		homepageService.update_new_visitors_count(homepageBo);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/thumbsup")
	@ResponseBody
	public String thumbsup(String user_id, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (!StringUtils.hasLength(user_id)) {
			return CommonUtil.toErrorResult(ERRORCODE.CONTACT_VISITOR.getIndex(),
					ERRORCODE.CONTACT_VISITOR.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CONTACT_HOMEPAGE.getIndex(),
					ERRORCODE.CONTACT_HOMEPAGE.getReason());
		}
		String owner_id = userBo.getId();
		HomepageBo homepageBo = homepageService.selectByUserId(user_id);
		ThumbsupBo thumbsupBo = new ThumbsupBo();
		thumbsupBo.setOwner_id(owner_id);
		thumbsupBo.setVisitor_id(user_id);
		thumbsupBo.setHomepage_id(homepageBo.getId());
		thumbsupService.insert(thumbsupBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/thumbsup-from-me")
	@ResponseBody
	public String thumbsup_from_me(String start_id, boolean gt, int limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
		String ownerId = userBo.getId();
		List<ThumbsupBo> thumbsup_from_me = thumbsupService.selectByOwnerIdPaged(start_id, gt, limit, ownerId);
		List<ThumbsupVo> thumbsup_from_me_vo = new ArrayList<ThumbsupVo>();
		for (ThumbsupBo item : thumbsup_from_me) {
			ThumbsupVo vo = new ThumbsupVo();
			BeanUtils.copyProperties(vo, item);
			thumbsup_from_me_vo.add(vo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("thumbsup_from_me", thumbsup_from_me_vo);

		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/thumbsup-to-me")
	@ResponseBody
	public String thumbsup_to_me(String start_id, boolean gt, int limit, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
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
		String ownerId = userBo.getId();
		List<ThumbsupBo> thumbsup_to_me = thumbsupService.selectByVisitorIdPaged(start_id, gt, limit, ownerId);
		List<ThumbsupVo> thumbsup_to_me_vo = new ArrayList<ThumbsupVo>();
		for (ThumbsupBo item : thumbsup_to_me) {
			ThumbsupVo vo = new ThumbsupVo();
			BeanUtils.copyProperties(vo, item);
			thumbsup_to_me_vo.add(vo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("thumbsup_to_me", thumbsup_to_me_vo);
		return JSONObject.fromObject(map).toString();
	}
}
