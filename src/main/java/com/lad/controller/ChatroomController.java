package com.lad.controller;

import com.lad.bo.ChatroomBo;
import com.lad.bo.ChatroomUserBo;
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
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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

	private static Logger logger = LogManager.getLogger(ChatroomController.class);

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
		String term = "";
		IMTermBo iMTermBo = iMTermService.selectByUserid(userBo.getId());
		if (iMTermBo != null) {
			term = iMTermBo.getTerm();
		}
		//第一个为返回结果信息，第二位term信息
		String[] result = IMUtil.subscribe(0, chatroomBo.getId(), term, userBo.getId());
		if (!result[0].equals(IMUtil.FINISH)) {
			chatroomService.remove(chatroomBo.getId());
			return result[0];
		}
		HashSet<String> chatrooms = userBo.getChatrooms();
		chatrooms.add(chatroomBo.getId());
		userBo.setChatrooms(chatrooms);
		userService.updateChatrooms(userBo);
		LinkedHashMap<String, String> nicknames = new LinkedHashMap<>();
		nicknames.put(userBo.getId(), userBo.getUserName());
		addChatroomUser(chatroomBo.getId(), nicknames);
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
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		if (StringUtils.isEmpty(userids)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_ID.getIndex(),
					ERRORCODE.ACCOUNT_ID.getReason());
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (null == chatroomBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		if (!chatroomBo.isOpen()) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NOT_OPEN.getIndex(),
					ERRORCODE.CHATROOM_NOT_OPEN.getReason());
		}
		//判断参数是一个还是多个
		String[] useridArr;
		if (userids.indexOf(',') > 0) {
			useridArr = userids.trim().split(",");
		} else {
			useridArr = new String[]{userids};
		}
		IMTermBo imTermBo = iMTermService.selectByUserid(userBo.getId());
		String term = imTermBo == null ? "" : imTermBo.getTerm();
		//第一个为返回结果信息，第二位term信息
		String[] result = IMUtil.subscribe(1,chatroomid,term, useridArr);
		if (!result[0].equals(IMUtil.FINISH)) {
			return result[0];
		}
		LinkedHashMap<String, String> nicknames = new LinkedHashMap<>();
		updateIMTerm(userBo.getId(), result[1]);
		LinkedHashSet<String> set = chatroomBo.getUsers();
		for (String userid : useridArr) {
			if (set.contains(userid)) {
				continue;
			}
			updateIMTerm(userid, result[1]);
			UserBo user = userService.getUser(userid);
			if (null == user) {
				return CommonUtil.toErrorResult(ERRORCODE.USER_NULL.getIndex(),
						 ERRORCODE.USER_NULL.getReason());
			}
			nicknames.put(user.getId(), user.getUserName());
			HashSet<String> chatroom = user.getChatrooms();
			//个人聊天室中没有当前聊天室，则添加到个人的聊天室
			if (!chatroom.contains(chatroomBo.getId())) {
				chatroom.add(chatroomBo.getId());
				user.setChatrooms(chatroom);
				userService.updateChatrooms(user);
			}
			set.add(userid);
		}
		addChatroomUser(chatroomid, nicknames);
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
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		if (StringUtils.isEmpty(userids)) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_ID.getIndex(),
					ERRORCODE.ACCOUNT_ID.getReason());
		}
		String[] useridArr = CommonUtil.getIds(userids.trim());
		IMTermBo termBo = iMTermService.selectByUserid(userBo.getId());
		String term = termBo == null ? "" : termBo.getTerm();
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (null == chatroomBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		if (!userBo.getId().equals(chatroomBo.getMaster())) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		//第一个为返回结果信息，第二位term信息
		String[] result = IMUtil.unSubscribe(chatroomid, term, useridArr);
		if (!result[0].equals(IMUtil.FINISH)) {
			if (!result[0].contains("not found")){
				return result[0];
			}
		}
		LinkedHashSet<String> set = chatroomBo.getUsers();
		for (String userid : useridArr) {
			if (!set.contains(userid)) {
				continue;
			}
			set.remove(userid);
			UserBo user = userService.getUser(userid);
			if (null != user) {
				updateUserChatroom(user, chatroomid);
			}
			if (!StringUtils.isEmpty(result[1])) {
				updateIMTerm(userid, result[1]);
			}
		}
		//聊天室少于2人则直接删除
		if (set.size() < 2) {
			String res = IMUtil.disolveRoom(iMTermService, userBo.getId(), chatroomid);
			if (!res.equals(IMUtil.FINISH) && !res.contains("not found")) {
				return res;
			}
			chatroomService.delete(chatroomid);
			chatroomService.deleteChatroomUser(chatroomid);
		} else {
			deleteNickname(chatroomid, useridArr);
			chatroomBo.setUsers(set);
			chatroomService.updateUsers(chatroomBo);
		}
		return Constant.COM_RESP;
	}

	@RequestMapping("/quit")
	@ResponseBody
	public String quit(String chatroomid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (null == chatroomBo) {
			updateUserChatroom(userBo, chatroomid);
			return Constant.COM_RESP;
		}
		String userid = userBo.getId();
		IMTermBo imTermBo = iMTermService.selectByUserid(userid);
		String term = imTermBo == null ? "" : imTermBo.getTerm();
		//第一个为返回结果信息，第二位term信息
		String[] result = IMUtil.unSubscribe(chatroomid, term, userid);
		if (!result[0].equals(IMUtil.FINISH)) {
			if (!result[0].contains("not found")) {
				return result[0];
			}
		}
		LinkedHashSet<String> set = chatroomBo.getUsers();
		if (set.size() >= 2) {
			//如果是群主退出，则由下一个人担当
			if (userid.equals(chatroomBo.getMaster())) {
				set.remove(userid);
				String nextId = set.iterator().next();
				chatroomService.updateMaster(chatroomid, nextId);
			}
		}
		updateUserChatroom(userBo, chatroomid);
		deleteNickname(chatroomid, userid);
		set.remove(userid);
		if (set.size() < 2) {
			String res = IMUtil.disolveRoom(iMTermService, userid, chatroomid);
			if (!res.equals(IMUtil.FINISH) && !res.contains("not found")) {
				return res;
			}
			chatroomService.deleteChatroomUser(chatroomid);
			chatroomService.delete(chatroomid);
		} else {
			chatroomBo.setUsers(set);
			chatroomService.updateUsers(chatroomBo);
		}
		return Constant.COM_RESP;
	}

	private void updateUserChatroom(UserBo userBo, String chatroomid){
		RLock lock = redisServer.getRLock("deleteUser");
		try {
			lock.lock(3, TimeUnit.SECONDS);
			HashSet<String> chatroom = userBo.getChatrooms();
			LinkedList<String> chatroomTops = userBo.getChatroomsTop();
			boolean hasRoom = false;
			if (chatroom.contains(chatroomid)){
				chatroom.remove(chatroomid);
				userBo.setChatrooms(chatroom);
				hasRoom = true;
			}
			if (chatroomTops.contains(chatroomid)){
				chatroomTops.remove(chatroomid);
				userBo.setChatroomsTop(chatroomTops);
				hasRoom = true;
			}
			if (hasRoom){
				userService.updateChatrooms(userBo);
				logger.info("user  {}  delete  chatroom  {}", userBo.getId(), chatroomid);
			}
		} finally {
			lock.unlock();
		}

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
		LinkedList<String> chatroomsTop = userBo.getChatroomsTop();
		List<ChatroomVo> chatroomList = new LinkedList<ChatroomVo>();
		for (String id : chatroomsTop) {
			ChatroomBo temp = chatroomService.get(id);
			if (null != temp) {
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(temp, vo);
				if (temp.getType() != 1) {
					bo2vo(temp, vo);
					vo.setUserNum(temp.getUsers().size());
				}
				vo.setTop(1);
				chatroomList.add(vo);
			}
		}
		for (String id : chatrooms) {
			ChatroomBo temp = chatroomService.get(id);
			if (null != temp) {
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(temp, vo);
				if (temp.getType() != 1) {
					bo2vo(temp, vo);
					vo.setUserNum(temp.getUsers().size());
				}
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
		ChatroomUserBo chatroomUserBo = chatroomService.findByUserRoomid(chatroomBo.getId());
		for (String userid : users) {
			UserBo chatUser = userService.getUser(userid);
			if (chatUser == null) {
				continue;
			}
			ChatroomUserVo userVo = new ChatroomUserVo();
			userVo.setUserid(userid);
			userVo.setUserPic(chatUser.getHeadPictureName());
			if (userid.equals(chatroomBo.getMaster())) {
				userVo.setRole(2);
			}
			if (chatroomUserBo != null) {
				HashMap<String, String> nicknames = chatroomUserBo.getNicknames();
				String nickname = nicknames.get(userid);
				if (StringUtils.isEmpty(nickname)) {
					userVo.setNickname(chatUser.getUserName());
				} else {
					userVo.setNickname(nickname);
				}
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
			if (temp.getType() != 1) {
				bo2vo(temp, vo);
				vo.setUserNum(temp.getUsers().size());
			}
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
		LinkedList<String> chatroomsTop = userBo.getChatroomsTop();
		if (chatrooms.contains(chatroomid)) {
			chatrooms.remove(chatroomid);
			userBo.setChatrooms(chatrooms);
		}
		if (chatroomsTop.contains(chatroomid)) {
			chatroomsTop.remove(chatroomid);
		}
		chatroomsTop.set(0, chatroomid);
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
		LinkedList<String> chatroomsTop = userBo.getChatroomsTop();
		if (chatroomsTop.contains(chatroomid)) {
			chatroomsTop.remove(chatroomid);
			userBo.setChatroomsTop(chatroomsTop);
		} 
		if (!chatrooms.contains(chatroomid)) {
			chatrooms.add(chatroomid);
			userBo.setChatrooms(chatrooms);
		}
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
		RLock lock = redisServer.getRLock(Constant.CHAT_LOCK);
		boolean isNew = false;
		double[] position = new double[]{px,py};
		ChatroomBo chatroom = null;
		try {
			//10s自动解锁
			lock.lock(4, TimeUnit.SECONDS);
			chatroom = chatroomService.selectBySeqInTen(seq, position, 100);
			if (null == chatroom) {
				chatroom = getChatroomBo(seq, position, userBo);
				isNew = true;
			} else {
				LinkedHashSet<String> userSet = chatroom.getUsers();
				userSet.add(userBo.getId());
				chatroom.setUsers(userSet);
			}
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
		String term = iMTermBo != null ? iMTermBo.getTerm() : "";
		int type = isNew ? 0 : 1;
		String[] res = IMUtil.subscribe(type,chatroom.getId(),term, userBo.getId());
		logger.info("face  user {}, chatroom {},  res {}", userBo.getId(), chatroom.getId(), res[0]);
		if (!res[0].equals(IMUtil.FINISH)) {
			//失败需要还原
			if (isNew) {
				chatroomService.remove(chatroom.getId());
			} else {
				LinkedHashSet<String> userSet = chatroom.getUsers();
				userSet.remove(userBo.getId());
				chatroom.setUsers(userSet);
				chatroomService.updateUsers(chatroom);
			}
			return res[0];
		}
		LinkedHashMap<String, String> nicknames = new LinkedHashMap<>();
		nicknames.put(userBo.getId(), userBo.getUserName());
		addChatroomUser(chatroom.getId(), nicknames);
		updateIMTerm(userBo.getId(), res[1]);
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("channelId", chatroom.getId());
		return JSONObject.fromObject(map).toString();
	}

	private ChatroomBo getChatroomBo(int seq, double[] position, UserBo userBo){
		ChatroomBo chatroom = new ChatroomBo();
		chatroom.setSeq(seq);
		chatroom.setUserid(userBo.getId());
		LinkedHashSet<String> userSet = chatroom.getUsers();
		userSet.add(userBo.getId());
		chatroom.setUsers(userSet);
		chatroom.setName(userBo.getUserName());
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
        JPushUtil.pushTo(userBo.getUserName()+"将"+chatroomBo.getName()+"转让给了您", userid);
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
		if (chatroomBo.getUsers().contains(userBo.getId())) {
			chatroomService.updateName(chatroomid, name);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_USER_NULL.getIndex(),
					ERRORCODE.CIRCLE_USER_NULL.getReason());
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


	@RequestMapping("/update-nickname")
	@ResponseBody
	public String updateNickname(String chatroomid, String nickname,
							   HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ChatroomUserBo chatroomUserBo = chatroomService.findByUserRoomid(chatroomid);
		if (chatroomUserBo == null) {
			chatroomUserBo = new ChatroomUserBo();
			LinkedHashMap<String, String> nicknames = chatroomUserBo.getNicknames();
			nicknames.put(userBo.getId(), nickname);
			chatroomUserBo.setChatroomid(chatroomid);
			chatroomUserBo.setNicknames(nicknames);
			chatroomService.insertUser(chatroomUserBo);
		} else {
			HashMap<String, String> nicknames = chatroomUserBo.getNicknames();
			nicknames.put(userBo.getId(), nickname);
			chatroomService.updateUserNickname(chatroomUserBo.getId(), nicknames);
		}
		return Constant.COM_RESP;
	}

	@RequestMapping("/get-nicknames")
	@ResponseBody
	public String getNickname(String chatroomid,
								 HttpServletRequest request, HttpServletResponse response) {
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (chatroomBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		ChatroomUserBo chatroomUserBo = chatroomService.findByUserRoomid(chatroomid);
		HashSet<String> users = chatroomBo.getUsers();
		List<ChatroomUserVo> userVos = new ArrayList<>();
		for (String userid : users) {
			UserBo chatUser = userService.getUser(userid);
			if (chatUser == null) {
				continue;
			}
			ChatroomUserVo userVo = new ChatroomUserVo();
			if (userid.equals(chatroomBo.getMaster())) {
				userVo.setRole(2);
			}
			userVo.setUserid(userid);
			userVo.setUserPic(chatUser.getHeadPictureName());
			if (chatroomUserBo != null) {
				HashMap<String, String> nicknames = chatroomUserBo.getNicknames();
				String nickname = nicknames.get(userid);
				if (StringUtils.isNotEmpty(nickname)) {
					userVo.setNickname(nickname);
				} else {
					userVo.setNickname(chatUser.getUserName());
				}
			}
			userVos.add(userVo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("chatroomUsers", userVos);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 添加聊天室用户的昵称
	 * @param chatroomid
	 * @param nicknames
	 */
	@Async
	private void addChatroomUser(String chatroomid, LinkedHashMap<String, String> nicknames){
		ChatroomUserBo chatroomUserBo = chatroomService.findByUserRoomid(chatroomid);
		if (chatroomUserBo == null) {
			chatroomUserBo = new ChatroomUserBo();
			chatroomUserBo.setChatroomid(chatroomid);
			chatroomUserBo.setNicknames(nicknames);
			chatroomService.insertUser(chatroomUserBo);
		} else {
			HashMap<String, String> nicks = chatroomUserBo.getNicknames();
			nicks.putAll(nicknames);
			chatroomService.updateUserNickname(chatroomUserBo.getId(), nicks);
		}
	}

	/**
	 * 删除群聊中的用户聊天昵称
	 * @param chatroomid
	 * @param userids
	 */
	@Async
	private void deleteNickname(String chatroomid, String... userids){
		ChatroomUserBo chatroomUserBo = chatroomService.findByUserRoomid(chatroomid);
		if (chatroomUserBo != null) {
			HashMap<String, String> nicknames = chatroomUserBo.getNicknames();
			for (String userid : userids) {
				nicknames.remove(userid);
			}
			chatroomService.updateUserNickname(chatroomUserBo.getId(), nicknames);
		}
	}
}
