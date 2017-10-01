package com.lad.controller;

import com.lad.bo.ChatroomBo;
import com.lad.bo.IMTermBo;
import com.lad.bo.UserBo;
import com.lad.redis.RedisServer;
import com.lad.service.IChatroomService;
import com.lad.service.IIMTermService;
import com.lad.service.IUserService;
import com.lad.util.*;
import com.lad.vo.ChatroomUserVo;
import com.lad.vo.ChatroomVo;
import com.lad.vo.UserVo;
import com.pushd.ImAssistant;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
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

	@Autowired
	private RedisServer redisServer;

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
		chatroomBo.setType(Constant.ROOM_MULIT);
		chatroomBo.setCreateuid(userBo.getId());
		chatroomBo.setMaster(userBo.getId());
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
						 ERRORCODE.USER_NULL.getReason());
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
			updateIMTerm(userid, result[1]);
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
				BeanUtils.copyProperties(temp, vo);
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
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		HashSet<String> chatrooms = userBo.getChatrooms();
		HashSet<String> chatroomsTop = userBo.getChatroomsTop();
		List<ChatroomVo> chatroomList = new LinkedList<ChatroomVo>();
		for (String id : chatroomsTop) {
			ChatroomBo temp = chatroomService.get(id);
			if (null != temp) {
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(temp, vo);
				bo2vo(temp, vo);
				vo.setUserNum(temp.getUsers().size());
				vo.setTop(1);
				chatroomList.add(vo);
			}
		}
		for (String id : chatrooms) {
			ChatroomBo temp = chatroomService.get(id);
			if (null != temp) {
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(temp, vo);
				vo.setUserNum(temp.getUsers().size());
				bo2vo(temp, vo);
				chatroomList.add(vo);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("ChatroomList", chatroomList);
		return JSONObject.fromObject(map).toString();
	}

	private void bo2vo(ChatroomBo chatroomBo, ChatroomVo vo){
		HashSet<String> users = chatroomBo.getUsers();
		LinkedHashSet<ChatroomUserVo> userVos = vo.getUserVos();
		for (String userid : users) {
			UserBo chatUser = userService.getUser(userid);
			if (chatUser == null) {
				continue;
			}
			ChatroomUserVo userVo = new ChatroomUserVo();
			userVo.setUsername(chatUser.getUserName());
			userVo.setUserid(userid);
			userVo.setUserPic(chatUser.getHeadPictureName());
			if (userid.equals(chatroomBo.getMaster())) {
				userVo.setRole(2);
			}
			userVos.add(userVo);
		}
	}

	@RequestMapping("/get-chatroom-info")
	@ResponseBody
	public String getChatroomInfo(@RequestParam String chatroomid,
			HttpServletRequest request, HttpServletResponse response){

		try {
			checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}

		ChatroomBo temp = chatroomService.get(chatroomid);
		ChatroomVo vo = new ChatroomVo();
		if (null != temp) {
			BeanUtils.copyProperties(temp,vo);
			bo2vo(temp, vo);
			vo.setUserNum(temp.getUsers().size());
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

	@RequestMapping("/facetoface-create")
	@ResponseBody
	public String faceToFaceCreate(@RequestParam int seq, @RequestParam double px, @RequestParam double py,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		boolean isNew = false;
		double[] position = new double[]{px,py};
        ChatroomBo chatroom = chatroomService.selectBySeqInTen(seq, position, 100);
		if (null == chatroom) {
			chatroom = getChatroomBo(chatroom, seq, position, userBo);
			isNew = true;
		} else {
			//相同序列是否在10分钟内创建
            if (0 == chatroom.getExpire()) {
                return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_SEQ_EXPIRE.getIndex(),
                        ERRORCODE.CHATROOM_SEQ_EXPIRE.getReason());
            }
            HashSet<String> userSet = chatroom.getUsers();
			userSet.add(userBo.getId());
			chatroom.setUsers(userSet);
		}
        RLock lock = redisServer.getRLock(Constant.CHAT_LOCK);
		try {
			//10s自动解锁
			lock.lock(3, TimeUnit.SECONDS);
			if (isNew) {
                chatroomService.insert(chatroom);
            } else {
                chatroomService.updateUsers(chatroom);
            }
		} finally {
			lock.unlock();
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

	private ChatroomBo getChatroomBo(ChatroomBo chatroom, int seq, double[] position, UserBo userBo){
		chatroom = new ChatroomBo();
		chatroom.setSeq(seq);
		chatroom.setUserid(userBo.getId());
		HashSet<String> userSet = chatroom.getUsers();
		userSet.add(userBo.getId());
		chatroom.setUsers(userSet);
		chatroom.setName("FaceToFaceChatroom");
		chatroom.setMaster(userBo.getId());
		chatroom.setPosition(position);
		chatroom.setType(Constant.ROOM_FACE_2_FACE);
		return chatroom;
	}


	@RequestMapping("/factoface-add")
	@ResponseBody
	public String faceToFaceAdd(int seq, double px, double py,
								HttpServletRequest request, HttpServletResponse response) {
		return faceToFaceCreate(seq, px, py, request, response);
	}



	@RequestMapping("/trans-chatroom")
	@ResponseBody
	public String tansRoom(String chatroomid, String userid,
								HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (chatroomBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		UserBo master = userService.getUser(userid);
		if (master == null) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_NULL.getIndex(),
					ERRORCODE.USER_NULL.getReason());
		}
		if (userBo.getId().equals(chatroomBo.getMaster())) {
			chatroomService.updateMaster(chatroomid, userid);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		return Constant.COM_RESP;
	}

	@RequestMapping("/update-name")
	@ResponseBody
	public String updateName(String chatroomid, String name,
								HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (chatroomBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		if (userBo.getId().equals(chatroomBo.getMaster())) {
			chatroomService.updateName(chatroomid, name);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		return Constant.COM_RESP;
	}

	@RequestMapping("/update-description")
	@ResponseBody
	public String updateDescription(String chatroomid, String description,
							 HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (chatroomBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		if (userBo.getId().equals(chatroomBo.getMaster())) {
			chatroomService.updateDescription(chatroomid, description);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		return Constant.COM_RESP;
	}

	@RequestMapping("/update-open")
	@ResponseBody
	public String updateOpen(String chatroomid, boolean isOpen,
									HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (chatroomBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		if (userBo.getId().equals(chatroomBo.getMaster())) {
			chatroomService.updateOpen(chatroomid, isOpen);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		return Constant.COM_RESP;
	}

	@RequestMapping("/update-verify")
	@ResponseBody
	public String updateVerify(String chatroomid, boolean isVerify,
									HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (chatroomBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		if (userBo.getId().equals(chatroomBo.getMaster())) {
			chatroomService.updateVerify(chatroomid, isVerify);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		return Constant.COM_RESP;
	}
}
