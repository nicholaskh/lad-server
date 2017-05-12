package com.lad.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lad.bo.ChatroomBo;
import com.lad.bo.FriendsBo;
import com.lad.bo.UserBo;
import com.lad.service.IChatroomService;
import com.lad.service.IFriendsService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;

import net.sf.json.JSONObject;

@Controller
@Scope("prototype")
@RequestMapping("friends")
public class FriendsController extends BaseContorller {

	@Autowired
	private IFriendsService friendsService;
	@Autowired
	private IChatroomService chatroomService;
	
	@RequestMapping("/insert")
	@ResponseBody
	public String insert(String friendid, HttpServletRequest request, HttpServletResponse response) {
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
		if (StringUtils.isEmpty(friendid)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		FriendsBo temp = friendsService.getFriendByIdAndVisitorId(userBo.getId(), friendid);
		if (temp != null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_EXIST.getIndex(), ERRORCODE.FRIEND_EXIST.getReason());
		}
		FriendsBo friendsBo = new FriendsBo();
		friendsBo.setUserid(userBo.getId());
		friendsBo.setFriendid(friendid);
		friendsService.insert(friendsBo);
		ChatroomBo ChatroomBo = new ChatroomBo();
		ChatroomBo.setName("群聊");
		HashSet<String> users = new HashSet<String>();
		users.add(userBo.getId());
		users.add(friendid);
		ChatroomBo.setUsers(users);
		chatroomService.insert(ChatroomBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	
	@RequestMapping("/set-VIP")
	@ResponseBody
	public String setVIP(String friendid, Integer VIP, HttpServletRequest request, HttpServletResponse response) {
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
		if (StringUtils.isEmpty(friendid)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		if (null == VIP) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_VIP_NULL.getIndex(),
					ERRORCODE.FRIEND_VIP_NULL.getReason());
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorId(userBo.getId(), friendid);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		friendsBo.setVIP(VIP);
		friendsService.updateVIP(friendsBo.getUserid(), friendsBo.getFriendid(), VIP);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/set-black")
	@ResponseBody
	public String setBlack(String friendid, Integer black, HttpServletRequest request, HttpServletResponse response) {
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
		if (StringUtils.isEmpty(friendid)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		if (null == black) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_BLACK_NULL.getIndex(),
					ERRORCODE.FRIEND_BLACK_NULL.getReason());
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorId(userBo.getId(), friendid);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		friendsBo.setBlack(black);
		friendsService.updateBlack(friendsBo.getUserid(), friendsBo.getFriendid(), black);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/set-backname")
	@ResponseBody
	public String setBackName(String friendid, String backname, HttpServletRequest request, HttpServletResponse response) {
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
		if (StringUtils.isEmpty(friendid)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		if (null == backname) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_BACKNAME_NULL.getIndex(),
					ERRORCODE.FRIEND_BACKNAME_NULL.getReason());
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorId(userBo.getId(), friendid);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		friendsBo.setBackname(backname);
		friendsService.updateBackname(friendsBo.getUserid(), friendsBo.getFriendid(), backname);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/set-tag")
	@ResponseBody
	public String setTag(String friendid, String tag, HttpServletRequest request, HttpServletResponse response) {
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
		if (StringUtils.isEmpty(friendid)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		if (null == tag) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_BACKNAME_NULL.getIndex(),
					ERRORCODE.FRIEND_BACKNAME_NULL.getReason());
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorId(userBo.getId(), friendid);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		List<String> tagList = friendsBo.getTag();
		tagList.add(tag);
		friendsBo.setTag(tagList);
		friendsService.updateTag(friendsBo.getUserid(), friendsBo.getFriendid(), tagList);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/set-phone")
	@ResponseBody
	public String setPhone(String friendid, String phone, HttpServletRequest request, HttpServletResponse response) {
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
		if (StringUtils.isEmpty(friendid)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		if (null == phone) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_PHONE_NULL.getIndex(),
					ERRORCODE.FRIEND_PHONE_NULL.getReason());
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorId(userBo.getId(), friendid);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		friendsBo.setPhone(phone);
		friendsService.updatePhone(friendsBo.getUserid(), friendsBo.getFriendid(), phone);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/set-description")
	@ResponseBody
	public String setDescription(String friendid, String description, HttpServletRequest request, HttpServletResponse response) {
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
		if (StringUtils.isEmpty(friendid)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		if (null == description) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_PHONE_NULL.getIndex(),
					ERRORCODE.FRIEND_PHONE_NULL.getReason());
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorId(userBo.getId(), friendid);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		friendsBo.setDescription(description);
		friendsService.updateDescription(friendsBo.getUserid(), friendsBo.getFriendid(), description);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
}
