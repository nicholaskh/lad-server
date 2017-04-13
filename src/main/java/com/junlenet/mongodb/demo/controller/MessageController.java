package com.junlenet.mongodb.demo.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.junlenet.mongodb.demo.bo.MessageBo;
import com.junlenet.mongodb.demo.bo.ThumbsupBo;
import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.service.IMessageService;
import com.junlenet.mongodb.demo.service.IThumbsupService;
import com.junlenet.mongodb.demo.vo.ThumbsupVo;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("message")
public class MessageController extends BaseContorller {

	@Autowired
	private IMessageService messageService;

	@Autowired
	private IThumbsupService thumbsupService;

	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(String content, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!StringUtils.hasLength(content)) {
			return "{\"ret\":-1,\"error\":\"error phone\"}";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		MessageBo messageBo = new MessageBo();
		messageBo.setContent(content);
		messageBo.setOwner_id(userBo.getId());
		messageService.insert(messageBo);
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/thumbsup")
	@ResponseBody
	public String thumbsup(String message_id, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!StringUtils.hasLength(message_id)) {
			return "{\"ret\":-1,\"error\":\"error messageId\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		MessageBo messageBo = messageService.selectById(message_id);
		if (messageBo == null) {
			return "{\"ret\":-1,\"error\":\"error messageId\"}";
		}
		LinkedList<String> thumbsup_ids = messageBo.getThumbsup_ids();
		if (thumbsup_ids == null) {
			thumbsup_ids = new LinkedList<String>();
		}
		if (thumbsup_ids.contains(userBo.getId())) {
			return "{\"ret\":-1,\"error\":\"duplicate id\"}";
		}
		thumbsup_ids.add(userBo.getId());
		messageBo.setThumbsup_ids(thumbsup_ids);
		messageService.update_thumbsup_ids(messageBo);
		ThumbsupBo thumbsupBo = new ThumbsupBo();
		thumbsupBo.setMessage_id(message_id);
		thumbsupBo.setOwner_id(messageBo.getOwner_id());
		thumbsupBo.setVisitor_id(userBo.getId());
		thumbsupService.insert(thumbsupBo);
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/thumbsup-from-me")
	@ResponseBody
	public String thumbsup_from_me(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		String ownerId = userBo.getId();
		List<ThumbsupBo> thumbsup_from_me = thumbsupService.selectByOwnerId(ownerId);
		if(CollectionUtils.isEmpty(thumbsup_from_me)){
			return "{\"ret\":-1,\"error\":\"error ownerId\"}";
		}
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
	public String thumbsup_to_me(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		String ownerId = userBo.getId();
		List<ThumbsupBo> thumbsup_to_me = thumbsupService.selectByVisitorId(ownerId);
		if(CollectionUtils.isEmpty(thumbsup_to_me)){
			return "{\"ret\":-1,\"error\":\"error ownerId\"}";
		}
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
