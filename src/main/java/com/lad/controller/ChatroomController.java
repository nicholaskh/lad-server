package com.lad.controller;

import com.lad.bo.ChatroomBo;
import com.lad.bo.IMTermBo;
import com.lad.bo.UserBo;
import com.lad.service.IChatroomService;
import com.lad.service.IIMTermService;
import com.lad.service.IUserService;
import com.lad.util.*;
import com.lad.vo.ChatroomVo;
import com.lad.vo.UserVo;
import com.pushd.ImAssistant;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("chatroom")
public class ChatroomController extends BaseContorller {

	@Autowired
	private IChatroomService chatroomService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IIMTermService iMTermService;

	@RequestMapping("/create")
	@ResponseBody
	public String create(String name, HttpServletRequest request,
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
		if (StringUtils.isEmpty(name)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CHATROOM_NAME_NULL.getIndex(),
					ERRORCODE.CHATROOM_NAME_NULL.getReason());
		}
		ChatroomBo chatroomBo = new ChatroomBo();
		chatroomBo.setName(name);
		chatroomBo.setType(2);
		chatroomBo.setCreateuid(userBo.getId());
		chatroomService.insert(chatroomBo);
		HashSet<String> chatrooms = userBo.getChatrooms();
		chatrooms.add(chatroomBo.getId());
		userBo.setChatrooms(chatrooms);
		userService.updateChatrooms(userBo);
		ImAssistant assistent = ImAssistant.init("180.76.138.200", 2222);
		if (assistent == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
		}
		String term = "";
		IMTermBo iMTermBo = iMTermService.selectByUserid(userBo.getId());
		if (iMTermBo != null) {
			term = iMTermBo.getTerm();
		}
		//第一个为返回结果信息，第二位term信息
		String[] result = IMUtil.subscribe(name, chatroomBo.getId(), term, userBo.getId());
		if (!result[0].equals(IMUtil.FINISH)) {
			return result[0];
		}
		updateIMTerm(userBo.getId(), result[1]);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("channelId", chatroomBo.getId());
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/update-name")
	@ResponseBody
	public String updateName(String roomid, String name,
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
		if (StringUtils.isEmpty(name)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CHATROOM_NAME_NULL.getIndex(),
					ERRORCODE.CHATROOM_NAME_NULL.getReason());
		}
		if (StringUtils.isEmpty(roomid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CHATROOM_ID_NULL.getIndex(),
					ERRORCODE.CHATROOM_ID_NULL.getReason());
		}
		ChatroomBo chatroomBo = chatroomService.get(roomid);
		if (null == chatroomBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		chatroomBo.setName(name);
		chatroomService.updateName(chatroomBo);
		return Constant.COM_RESP;
	}

	@RequestMapping("/insert-user")
	@ResponseBody
	public String insertUser(String userids, String chatroomid,
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
		if (StringUtils.isEmpty(userids)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_ID.getIndex(),
					ERRORCODE.ACCOUNT_ID.getReason());
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (null == chatroomBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		//判断参数是一个还是多个
		String[] useridArr;
		if (userids.indexOf(',') > 0) {
			useridArr = userids.trim().split(",");
		} else {
			useridArr = new String[]{userids};
		}
		IMTermBo imTermBo = iMTermService.selectByUserid(userBo.getId());
		String term = "";
		if (imTermBo != null) {
			term = imTermBo.getTerm();
		}
		//第一个为返回结果信息，第二位term信息
		String[] result = IMUtil.subscribe("", chatroomid, term, useridArr);
		if (!result[0].equals(IMUtil.FINISH)) {
			return result[0];
		}
		updateIMTerm(userBo.getId(), result[1]);
		HashSet<String> set = chatroomBo.getUsers();
		for (String userid : useridArr) {
			updateIMTerm(userid, result[1]);
			UserBo user = userService.getUser(userid);
			if (null == user) {
				return CommonUtil.toErrorResult(ERRORCODE.USER_NULL.getIndex(),
						 ERRORCODE.USER_NULL.getReason() + ", "+ userid);
			}
			HashSet<String> chatroom = user.getChatrooms();
			//个人聊天室中没有当前聊天室，则添加到个人的聊天室
			if (!chatroom.contains(chatroomBo.getId())) {
				chatroom.add(chatroomBo.getId());
				user.setChatrooms(chatroom);
				userService.updateChatrooms(user);
			}
			set.add(userid);
		}
		chatroomBo.setUsers(set);
		chatroomService.updateUsers(chatroomBo);
		return Constant.COM_RESP;
	}
	/**
	 *
	 * @param userid
	 * @param term
	 */
	private void updateIMTerm(String userid, String term){
		IMTermBo imTermBo = iMTermService.selectByUserid(userid);
		if (imTermBo == null) {
			imTermBo = new IMTermBo();
			imTermBo.setTerm(term);
			imTermBo.setUserid(userid);
			iMTermService.insert(imTermBo);
		} else {
			iMTermService.updateByUserid(userid, term);
		}
	}

	@RequestMapping("/delete-user")
	@ResponseBody
	public String deltetUser(String userids, String chatroomid,
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
		if (StringUtils.isEmpty(userids)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_ID.getIndex(),
					ERRORCODE.ACCOUNT_ID.getReason());
		}
		String[] useridArr;
		if (userids.indexOf(',') > 0) {
			useridArr = userids.trim().split(",");
		} else {
			useridArr = new String[]{userids.trim()};
		}
		IMTermBo imTermBo = iMTermService.selectByUserid(userBo.getId());
		String term = "";
		if (imTermBo != null) {
			term = imTermBo.getTerm();
		}
		//第一个为返回结果信息，第二位term信息
		String[] result = IMUtil.unSubscribe(chatroomid, term, useridArr);
		if (!result[0].equals(IMUtil.FINISH)) {
			return result[0];
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (null == chatroomBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		HashSet<String> set = chatroomBo.getUsers();
		for (String userid : useridArr) {
			updateIMTerm(userid, result[1]);
			UserBo user = userService.getUser(userid);
			if (null == user) {
				return CommonUtil.toErrorResult(ERRORCODE.USER_NULL.getIndex(),
						ERRORCODE.USER_NULL.getReason());
			}
			HashSet<String> chatroom = user.getChatrooms();
			chatroom.remove(chatroomBo.getId());
			user.setChatrooms(chatroom);
			userService.updateChatrooms(user);
			set.remove(userid);
		}
		chatroomBo.setUsers(set);
		chatroomService.updateUsers(chatroomBo);
		return Constant.COM_RESP;
	}

	@RequestMapping("/get-friends")
	@ResponseBody
	public String getFriends(HttpServletRequest request,
			HttpServletResponse response) throws IllegalAccessException,
			InvocationTargetException {
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

	@RequestMapping("/get-my-chatrooms")
	@ResponseBody
	public String getChatrooms(HttpServletRequest request,
			HttpServletResponse response) throws IllegalAccessException,
			InvocationTargetException {
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
		UserBo userBoTemp = (UserBo) session.getAttribute("userBo");
		if (userBoTemp == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = userService.getUser(userBoTemp.getId());
		HashSet<String> Chatrooms = userBo.getChatrooms();
		HashSet<String> ChatroomsTop = userBo.getChatroomsTop();
		List<ChatroomVo> chatroomList = new LinkedList<ChatroomVo>();
		for (String id : ChatroomsTop) {
			ChatroomBo temp = chatroomService.get(id);
			if (null != temp) {
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(vo, temp);
				vo.setTop(1);
				chatroomList.add(vo);
			}
		}
		for (String id : Chatrooms) {
			ChatroomBo temp = chatroomService.get(id);
			if (null != temp) {
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(vo, temp);
				chatroomList.add(vo);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("ChatroomList", chatroomList);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/get-chatroom-info")
	@ResponseBody
	public String getChatroomInfo(
			@RequestParam(required = true) String chatroomid,
			HttpServletRequest request, HttpServletResponse response)
			throws IllegalAccessException, InvocationTargetException {
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
		ChatroomBo temp = chatroomService.get(chatroomid);
		ChatroomVo vo = new ChatroomVo();
		if (null != temp) {
			BeanUtils.copyProperties(vo, temp);
		} else {
			return CommonUtil.toErrorResult(
					ERRORCODE.CHATROOM_ID_NULL.getIndex(),
					ERRORCODE.CHATROOM_ID_NULL.getReason());
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("chatroom", vo);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/set-top")
	@ResponseBody
	public String setTop(String chatroomid, HttpServletRequest request,
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
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (null == chatroomBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		HashSet<String> chatrooms = userBo.getChatrooms();
		LinkedHashSet<String> chatroomsTop = userBo.getChatroomsTop();
		if (chatrooms.contains(chatroomid)) {
			chatrooms.remove(chatroomid);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		if (chatroomsTop.contains(chatroomid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CHATROOM_TOP_EXIST.getIndex(),
					ERRORCODE.CHATROOM_TOP_EXIST.getReason());
		} else {
			chatroomsTop.add(chatroomid);
		}
		userBo.setChatrooms(chatrooms);
		userBo.setChatroomsTop(chatroomsTop);
		userService.updateChatrooms(userBo);
		return Constant.COM_RESP;
	}

	@RequestMapping("/cancel-top")
	@ResponseBody
	public String cancelTop(String chatroomid, HttpServletRequest request,
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
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (null == chatroomBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		HashSet<String> chatrooms = userBo.getChatrooms();
		LinkedHashSet<String> chatroomsTop = userBo.getChatroomsTop();
		if (chatroomsTop.contains(chatroomid)) {
			chatroomsTop.remove(chatroomid);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		if (chatrooms.contains(chatroomid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CHATROOM_EXIST.getIndex(),
					ERRORCODE.CHATROOM_EXIST.getReason());
		} else {
			chatrooms.add(chatroomid);
		}
		userBo.setChatrooms(chatrooms);
		userBo.setChatroomsTop(chatroomsTop);
		userService.updateChatrooms(userBo);
		return Constant.COM_RESP;
	}

	@RequestMapping("/factoface-create")
	@ResponseBody
	public String faceToFaceCreate(final int seq, double px, double py,
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
		HashSet<String> userSet = new HashSet<>();
		ChatroomBo chatroom = null;
		boolean isNew = false;
		RedissonClient redisson = RedisUtil.init();
		RLock lock = redisson.getLock("chatLock");
		double[] position = new double[]{px,py};
		try {
			//10s自动解锁
			lock.lock(20, TimeUnit.SECONDS);
			chatroom = chatroomService.selectBySeq(seq);
			if (null == chatroom) {
				chatroom = new ChatroomBo();
				chatroom.setSeq(seq);
				chatroom.setUserid(userBo.getId());
				userSet.add(userBo.getId());
				chatroom.setUsers(userSet);
				chatroom.setName("FaceToFaceChatroom");
				chatroom.setPosition(position);
				chatroomService.insert(chatroom);
				isNew = true;
			} else {
				if (0 == chatroom.getExpire()) {
					return CommonUtil.toErrorResult(
							ERRORCODE.CHATROOM_SEQ_EXPIRE.getIndex(),
							ERRORCODE.CHATROOM_SEQ_EXPIRE.getReason());
				}

				if (!CommonUtil.isTimeInTen(chatroom.getCreateTime())) {
					return CommonUtil.toErrorResult(
							ERRORCODE.CHATROOM_TIME_OUT.getIndex(),
							ERRORCODE.CHATROOM_TIME_OUT.getReason());
				}
				//聊天室位置是否在100m之内
				boolean isRange = chatroomService.withInRange(
						chatroom.getId(), position, 100);
				if (!isRange) {
					return CommonUtil.toErrorResult(
							ERRORCODE.CHATROOM_RANGE_OUT.getIndex(),
							ERRORCODE.CHATROOM_RANGE_OUT.getReason());
				}
				userSet.add(userBo.getId());
				chatroom.setUsers(userSet);
				chatroomService.updateUsers(chatroom);
			}
		} finally {
			lock.unlock();
			redisson.shutdown();
		}
		HashSet<String> chatrooms = userBo.getChatrooms();
		chatrooms.add(chatroom.getId());
		userBo.setChatrooms(chatrooms);
		userService.updateChatrooms(userBo);
		IMTermBo iMTermBo = iMTermService.selectByUserid(userBo.getId());
		String term = "";
		if (iMTermBo != null) {
			term = iMTermBo.getTerm();
		}
		String chatroomName = "";
		//首次创建聊天室，需要输入名称
		if (isNew) {
			chatroomName = chatroom.getName();
		}
		String[] res = IMUtil.subscribe(chatroomName,chatroom.getId(),term, userBo.getId());
		if (!res[0].equals(IMUtil.FINISH)) {
			return res[0];
		}
		updateIMTerm(userBo.getId(), res[1]);
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("channelId", chatroom.getId());
		return JSONObject.fromObject(map).toString();
	}


	@RequestMapping("/factoface-add")
	@ResponseBody
	public String faceToFaceAdd(final int seq, double px, double py,
								   HttpServletRequest request, HttpServletResponse response) {
		 return faceToFaceCreate(seq, px, py, request, response);
	}
}
