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
import com.lad.bo.UserBo;
import com.lad.service.IChatroomService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.lad.vo.ChatroomVo;
import com.lad.vo.UserVo;

import net.sf.json.JSONObject;

@Controller
@Scope("prototype")
@RequestMapping("chatroom")
public class ChatroomController extends BaseContorller {

	@Autowired
	private IChatroomService chatroomService;
	@Autowired
	private IUserService userService;

	@RequestMapping("/create")
	@ResponseBody
	public String create(String name, HttpServletRequest request, HttpServletResponse response) {
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
		if (StringUtils.isEmpty(name)) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NAME_NULL.getIndex(),
					ERRORCODE.CHATROOM_NAME_NULL.getReason());
		}
		ChatroomBo chatroomBo = new ChatroomBo();
		chatroomBo.setName(name);
		chatroomService.insert(chatroomBo);
//		ImAssistant imAssistant = PushedUtil.getPushed("180.76.173.200", 2222);
//		if (null == imAssistant) {
//			return CommonUtil.toErrorResult(ERRORCODE.PUSHED_ERROR.getIndex(), ERRORCODE.PUSHED_ERROR.getReason());
//		}
//		Boolean success = PushedUtil.subscribeToPushed(imAssistant, name, chatroomBo.getId(), userBo.getId());
//		if (!success) {
//			return CommonUtil.toErrorResult(ERRORCODE.PUSHED_ERROR.getIndex(), ERRORCODE.PUSHED_ERROR.getReason());
//		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("channelId", chatroomBo.getId());
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/update-name")
	@ResponseBody
	public String updateName(String roomid, String name, HttpServletRequest request, HttpServletResponse response) {
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
		if (StringUtils.isEmpty(name)) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NAME_NULL.getIndex(),
					ERRORCODE.CHATROOM_NAME_NULL.getReason());
		}
		if (StringUtils.isEmpty(roomid)) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_ID_NULL.getIndex(),
					ERRORCODE.CHATROOM_ID_NULL.getReason());
		}
		ChatroomBo chatroomBo = chatroomService.get(roomid);
		if (null == chatroomBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(), ERRORCODE.CHATROOM_NULL.getReason());
		}
		chatroomBo.setName(name);
		chatroomService.updateName(chatroomBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/insert-user")
	@ResponseBody
	public String insertUser(String userid, HttpServletRequest request, HttpServletResponse response) {
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
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_ID.getIndex(), ERRORCODE.ACCOUNT_ID.getReason());
		}
		ChatroomBo chatroomBo = new ChatroomBo();
		HashSet<String> set = chatroomBo.getUsers();
		set.add(userid);
		chatroomBo.setUsers(set);
		chatroomService.updateName(chatroomBo);
		HashSet<String> chatroom = userBo.getChatrooms();
		if (chatroom.contains(chatroomBo.getId())) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_EXIST.getIndex(), ERRORCODE.CHATROOM_EXIST.getReason());
		}
		chatroom.add(chatroomBo.getId());
		userService.updateChatrooms(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/delete-user")
	@ResponseBody
	public String deltetUser(String userid, HttpServletRequest request, HttpServletResponse response) {
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
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_ID.getIndex(), ERRORCODE.ACCOUNT_ID.getReason());
		}
		ChatroomBo chatroomBo = new ChatroomBo();
		HashSet<String> set = chatroomBo.getUsers();
		set.remove(userid);
		chatroomBo.setUsers(set);
		chatroomService.updateName(chatroomBo);
		HashSet<String> chatroom = userBo.getChatrooms();
		chatroom.remove(chatroomBo.getId());
		userService.updateChatrooms(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/get-friends")
	@ResponseBody
	public String getFriends(HttpServletRequest request, HttpServletResponse response)
			throws IllegalAccessException, InvocationTargetException {
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
		List<String> friends = userBo.getFriends();
		List<UserVo> userList = new LinkedList<UserVo>();
		for (String id : friends) {
			UserBo temp = userService.getUser(id);
			if (null != temp) {
				UserVo vo = new UserVo();
				BeanUtils.copyProperties(vo, temp);
				userList.add(vo);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("friends", userList);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/get-chatrooms")
	@ResponseBody
	public String getChatrooms(HttpServletRequest request, HttpServletResponse response)
			throws IllegalAccessException, InvocationTargetException {
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
		HashSet<String> Chatrooms = userBo.getChatrooms();
		List<ChatroomVo> ChatroomList = new LinkedList<ChatroomVo>();
		for (String id : Chatrooms) {
			UserBo temp = userService.getUser(id);
			if (null != temp) {
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(vo, temp);
				ChatroomList.add(vo);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("ChatroomList", ChatroomList);
		return JSONObject.fromObject(map).toString();
	}
}
