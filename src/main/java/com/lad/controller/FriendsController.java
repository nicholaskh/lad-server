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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lad.bo.ChatroomBo;
import com.lad.bo.ChatroomoneBo;
import com.lad.bo.FriendsBo;
import com.lad.bo.UserBo;
import com.lad.service.IChatroomService;
import com.lad.service.IChatroomoneService;
import com.lad.service.IFriendsService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.lad.vo.FriendsVo;

import net.sf.json.JSONObject;

@Controller
@Scope("prototype")
@RequestMapping("friends")
public class FriendsController extends BaseContorller {

	@Autowired
	private IFriendsService friendsService;
	@Autowired
	private IChatroomService chatroomService;
	@Autowired
	private IChatroomoneService chatroomoneService;
	@Autowired
	private IUserService userService;

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
		ChatroomoneBo chatroomoneBo = new ChatroomoneBo();
		chatroomoneBo.setName("群聊");
		chatroomoneBo.setUserid(userBo.getId());
		chatroomoneBo.setFriendid(friendid);
		chatroomoneService.insert(chatroomoneBo);
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
	public String setBackName(String friendid, String backname, HttpServletRequest request,
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
	public String setDescription(String friendid, String description, HttpServletRequest request,
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

	@RequestMapping("/get-tag")
	@ResponseBody
	public String getTag(String userid, HttpServletRequest request, HttpServletResponse response) {
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
		if (StringUtils.isEmpty(userid)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_ID.getIndex(), ERRORCODE.USER_ID.getReason());
		}
		List<FriendsBo> list = friendsService.getFriendByFirendid(userid);
		List<List<String>> tagList = new LinkedList<List<String>>();
		for (FriendsBo friendsBo : list) {
			tagList.add(friendsBo.getTag());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("tag", tagList);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/get-friends")
	@ResponseBody
	public String getFriends(String userid, HttpServletRequest request, HttpServletResponse response) {
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
		if (StringUtils.isEmpty(userid)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_ID.getIndex(), ERRORCODE.USER_ID.getReason());
		}
		List<FriendsBo> list = friendsService.getFriendByUserid(userid);
		List<FriendsVo> voList = new LinkedList<FriendsVo>();
		for (FriendsBo friendsBo : list) {
			FriendsVo vo = new FriendsVo();
			try {
				BeanUtils.copyProperties(vo, friendsBo);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			voList.add(vo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("tag", voList);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/delete")
	@ResponseBody
	public String delete(String friendid, HttpServletRequest request, HttpServletResponse response) {
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
		if (temp == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		friendsService.delete(userBo.getId(), friendid);
		chatroomoneService.delete(userBo.getId(), friendid);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/multi-insert")
	@ResponseBody
	public String multiInsert(String friendids, HttpServletRequest request, HttpServletResponse response) {
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
		if (StringUtils.isEmpty(friendids)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		if (!friendids.contains(",")) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
		}
		String[] idsList = friendids.split(",");
		HashSet<String> userSet = new HashSet<String>();
		for (String id : idsList) {
			FriendsBo temp = friendsService.getFriendByIdAndVisitorId(userBo.getId(), id);
			if (temp == null) {
				return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(), ERRORCODE.FRIEND_NULL.getReason());
			}
			userSet.add(id);
		}
		ChatroomBo chatroomBo = new ChatroomBo();
		chatroomBo.setName("群聊");
		chatroomBo.setUsers(userSet);
		chatroomService.insert(chatroomBo);
		for (String id : idsList) {
			UserBo user = userService.getUser(id);
			HashSet<String> chatroomsSet = user.getChatrooms();
			chatroomsSet.add(id);
			userBo.setChatrooms(chatroomsSet);
			userService.updateChatrooms(user);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/multi-out")
	@ResponseBody
	public String multiOut(String chatroomid, HttpServletRequest request, HttpServletResponse response) {
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
		if (chatroomid == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_ID_NULL.getIndex(),
					ERRORCODE.CHATROOM_ID_NULL.getReason());
		}
		String userid = userBo.getId();
		HashSet<String> chatrooms = new HashSet<String>();
		chatrooms = userBo.getChatrooms();
		if (chatrooms.contains(chatroomid)) {
			chatrooms.remove(chatroomid);
		}
		userBo.setChatrooms(chatrooms);
		userService.updateChatrooms(userBo);
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (null == chatroomBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(), ERRORCODE.CHATROOM_NULL.getReason());
		}
		HashSet<String> userids = chatroomBo.getUsers();
		if (userids.contains(userid)) {
			userids.remove(userid);
		}
		if (userids.size() <= 2) {
			chatroomService.delete(chatroomid);
		} else {
			chatroomBo.setUsers(userids);
			chatroomService.updateUsers(chatroomBo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

}
