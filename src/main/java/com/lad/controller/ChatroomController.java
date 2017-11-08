package com.lad.controller;

import com.lad.bo.ChatroomBo;
import com.lad.bo.ChatroomUserBo;
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
		ChatroomBo chatroomBo = new ChatroomBo();
		chatroomBo.setName(userBo.getUserName());
		chatroomBo.setType(Constant.ROOM_MULIT);
		chatroomBo.setCreateuid(userBo.getId());
		chatroomBo.setMaster(userBo.getId());
		HashSet<String> users = chatroomBo.getUsers();
		users.add(chatroomBo.getId());
		chatroomService.insert(chatroomBo);
		//第一个为返回结果信息，第二位term信息
		String result = IMUtil.subscribe(0, chatroomBo.getId(), userBo.getId());
		if (!result.equals(IMUtil.FINISH)) {
			chatroomService.remove(chatroomBo.getId());
			return result;
		}
		userService.updateChatrooms(userBo);
		addChatroomUser(userBo, chatroomBo.getId(), userBo.getUserName());
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
		//第一个为返回结果信息，第二位term信息
		String result = IMUtil.subscribe(1,chatroomid, useridArr);
		if (!result.equals(IMUtil.FINISH)) {
			return result;
		}
		// 如果群聊名称没有修改过，那么此对象生成的字符串将追加到群聊名称后面
		StringBuilder chatRoomNameAppend = new StringBuilder();
		LinkedHashSet<String> set = chatroomBo.getUsers();
		for (String userid : useridArr) {
			if (set.contains(userid)) {
				continue;
			}
			UserBo user = userService.getUser(userid);
			if (null != user) {
				addChatroomUser(user, chatroomBo.getId(), user.getUserName());
				HashSet<String> chatroom = user.getChatrooms();
				//个人聊天室中没有当前聊天室，则添加到个人的聊天室
				if (!chatroom.contains(chatroomBo.getId())) {
					chatroom.add(chatroomBo.getId());
					user.setChatrooms(chatroom);
					userService.updateChatrooms(user);
				}
				if(!chatroomBo.isNameSet()){
					chatRoomNameAppend.append("、").append(user.getUserName());
				}
				set.add(userid);
				JPushUtil.pushTo(String.format("%s邀请您加入群聊", user.getUserName()), userid);
			}
		}
		// 如果群聊没有修改过名称，那么根据添加的用户自动修改名称
		if(chatRoomNameAppend.length() > 0){
			chatroomService.updateName(chatroomid,
					String.format("%s%s", chatroomBo.getName(), chatRoomNameAppend.toString()),
					false);
		}
		chatroomBo.setUsers(set);
		chatroomService.updateUsers(chatroomBo);
		return Constant.COM_RESP;
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
		String result = IMUtil.unSubscribe(chatroomid, useridArr);
		if (!result.equals(IMUtil.FINISH) && !result.contains("not found")) {
			return result;
		}
		StringBuilder builder = null;
		if(!chatroomBo.isNameSet()){
			builder = new StringBuilder(chatroomBo.getName());
		}
		LinkedHashSet<String> set = chatroomBo.getUsers();
		for (String userid : useridArr) {
			//只能删除非自己以外人员，自己需要退出
			if (!set.contains(userid) || userid.equals(userBo.getId())) {
				continue;
			}
			removeUserNameFromChatRoomName(chatroomBo, userid, builder);
			set.remove(userid);
			UserBo user = userService.getUser(userid);
			if (null != user) {
				updateFriendChatroom(user, chatroomid);
			}
		}
		//聊天室少于2人则直接删除
		if (set.size() < 2) {
			String res = IMUtil.disolveRoom(chatroomid);
			if (!res.equals(IMUtil.FINISH) && !res.contains("not found")) {
				return res;
			}
			//删除最后一人的聊天室
			if (set.size() == 1) {
				String friendid = set.iterator().next();
				UserBo friend = userService.getUser(friendid);
				if (friend != null){
					updateFriendChatroom(friend, chatroomid);
				}
			}
			chatroomService.delete(chatroomid);
		} else {
			// 修改群聊名称
			if(builder != null){
				chatroomService.updateName(chatroomid, builder.toString(), false);
			}
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
		//第一个为返回结果信息，第二位term信息
		String result = IMUtil.unSubscribe(chatroomid, userid);
		if (!result.equals(IMUtil.FINISH) && !result.contains("not found")) {
			return result;
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
		deleteNickname(userid, chatroomid);
		set.remove(userid);
		if (set.size() < 2) {
			String res = IMUtil.disolveRoom(chatroomid);
			if (!res.equals(IMUtil.FINISH) && !res.contains("not found")) {
				return res;
			}
			//删除最后一人的聊天室
			if (set.size() == 1) {
				String friendid = set.iterator().next();
				UserBo friend = userService.getUser(friendid);
				if (friend != null){
					updateFriendChatroom(friend, chatroomid);
				}
			}
			chatroomService.delete(chatroomid);
		} else {
			// 判断修改群聊名称
			if(!chatroomBo.isNameSet()){
				StringBuilder builder = new StringBuilder(chatroomBo.getName());
				removeUserNameFromChatRoomName(chatroomBo, userid, builder);
				chatroomService.updateName(chatroomid, builder.toString(), false);
			}
			chatroomBo.setUsers(set);
			chatroomService.updateUsers(chatroomBo);
		}
		return Constant.COM_RESP;
	}

	/**
	 * @param chatroomBo
	 * @param userid
	 * @param builder
	 */
	private void removeUserNameFromChatRoomName(ChatroomBo chatroomBo, String userid, StringBuilder builder){
		if(chatroomBo == null || chatroomBo.isNameSet() || builder == null) return;
		UserBo temp = userService.getUser(userid);
		if(temp != null){
			String userNameTemp = temp.getUserName();
			int start = builder.indexOf(userNameTemp);
			if(start == -1){
				// 如果在此操作之前用户修改过自己的名称，此时将发生此warning
				logger.warn(String.format("chatRoom(id:%s)名称异常,没有包含%s用户名称，且群聊名称也没有被用户手动更改过",
						chatroomBo.getId(),
						userNameTemp)
				);
				//TODO 此时会出现群聊名称没有自动更改的情况，该如何处理？

				//TODO 方案一： think about 获取所有用户id和名称，一个一个对比，找出不存在的那个。如果有两个都修改了名称，无解
				return;
			}
			// 删除此用户名称
			builder.delete(start, start + userNameTemp.length());
			if(start != 0){
				// 删除多余的顿号(、)
				builder.deleteCharAt(start-1);
			}
		}else{
			logger.error(String.format("chatRoom(id:%s)中有一个用户id(%s)，在系统中不存在", chatroomBo.getId(), userid));
		}
	}

	/**
	 * 更新当前用户的聊天室
	 * @param userBo
	 * @param chatroomid
	 */
	private void updateUserChatroom(UserBo userBo, String chatroomid){
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
		chatroomService.deleteChatroomUser(userBo.getId(), chatroomid);
	}

	/**
	 * 删除其他人的聊天室信息
	 * @param userBo
	 * @param chatroomid
	 */
	private void updateFriendChatroom(UserBo userBo, String chatroomid){
		RLock lock = redisServer.getRLock("deleteUser");
		try {
			lock.lock(3, TimeUnit.SECONDS);
			updateUserChatroom(userBo, chatroomid);
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
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		String userid = userBo.getId();
		HashSet<String> chatrooms = userBo.getChatrooms();
		LinkedList<String> chatroomsTop = userBo.getChatroomsTop();
		List<ChatroomVo> chatroomList = new LinkedList<ChatroomVo>();
		HashSet<String> removes = new LinkedHashSet<>();
		LinkedList<String> removeTops = new LinkedList<>();
		for (String id : chatroomsTop) {
			ChatroomBo temp = chatroomService.get(id);
			if (null != temp) {
				if (temp.getType() == Constant.ROOM_SINGLE) {
					continue;
				}
				ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userid, id);
				boolean has = chatroomUserBo != null;
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(temp, vo);
				if (temp.getType() != 1) {
					bo2vo(has && chatroomUserBo.isShowNick(),temp, vo);
					vo.setUserNum(temp.getUsers().size());
					vo.setShowNick(has && chatroomUserBo.isShowNick());
				}
				vo.setDisturb(has && chatroomUserBo.isDisturb());
				vo.setTop(1);
				chatroomList.add(vo);
			} else {
				removeTops.add(id);
			}
		}
		for (String id : chatrooms) {
			ChatroomBo temp = chatroomService.get(id);
			if (null != temp) {
				if (temp.getType() == Constant.ROOM_SINGLE) {
					continue;
				}
				ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userid, id);
				boolean has = chatroomUserBo != null;
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(temp, vo);
				if (temp.getType() != 1) {
					bo2vo(has && chatroomUserBo.isShowNick(), temp, vo);
					vo.setUserNum(temp.getUsers().size());
					vo.setShowNick(has && chatroomUserBo.isShowNick());
				}
				chatroomList.add(vo);
				vo.setDisturb(has && chatroomUserBo.isDisturb());
			} else {
				removes.add(id);
			}
		}
		updateUserRoom(userBo, removes, removeTops);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("ChatroomList", chatroomList);
		return JSONObject.fromObject(map).toString();
	}

	@Async
	private void updateUserRoom(UserBo userBo, HashSet<String> removes, LinkedList<String> removeTops){
		HashSet<String> chatrooms = userBo.getChatrooms();
		LinkedList<String> chatroomsTop = userBo.getChatroomsTop();
		chatroomsTop.removeAll(removeTops);
		chatrooms.removeAll(removes);
		userBo.setChatroomsTop(chatroomsTop);
		userBo.setChatrooms(chatrooms);
		userService.updateChatrooms(userBo);
	}

	private void bo2vo(boolean isShowNick, ChatroomBo chatroomBo, ChatroomVo vo){
		LinkedHashSet<ChatroomUserVo> userVos = vo.getUserVos();
		List<ChatroomUserBo> chatroomUserBos = chatroomService.findByUserRoomid(chatroomBo.getId());
		for (ChatroomUserBo chatroomUser : chatroomUserBos) {
			String userid = chatroomUser.getUserid();
			UserBo chatUser = userService.getUser(userid);
			if (chatUser == null) {
				chatroomService.deleteUser(chatroomUser.getId());
				continue;
			}
			ChatroomUserVo userVo = new ChatroomUserVo();
			userVo.setUserid(chatUser.getId());
			userVo.setUserPic(chatUser.getHeadPictureName());
			if (userid.equals(chatroomBo.getMaster())) {
				userVo.setRole(2);
			}
			String nickname = isShowNick ? chatroomUser.getNickname() : chatroomUser.getUsername();
			userVo.setNickname(nickname);
			userVos.add(userVo);
		}
	}

	@RequestMapping("/get-chatroom-info")
	@ResponseBody
	public String getChatroomInfo(@RequestParam String chatroomid,
			HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ChatroomBo temp = chatroomService.get(chatroomid);
		ChatroomVo vo = new ChatroomVo();
		if (null != temp) {
			BeanUtils.copyProperties(temp,vo);
			ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userBo.getId(), chatroomid);
			if (chatroomUserBo == null) {
				chatroomUserBo = new ChatroomUserBo();
				chatroomUserBo.setChatroomid(chatroomid);
				chatroomUserBo.setUserid(userBo.getId());
				chatroomUserBo.setUsername(userBo.getUserName());
				chatroomUserBo.setShowNick(false);
				chatroomUserBo.setDisturb(false);
				chatroomService.insertUser(chatroomUserBo);
				vo.setDisturb(false);
			} else {
				if (temp.getType() != 1) {
					bo2vo(chatroomUserBo.isShowNick(), temp, vo);
					vo.setUserNum(temp.getUsers().size());
					vo.setShowNick(chatroomUserBo.isShowNick());
				}
				vo.setDisturb(chatroomUserBo.isDisturb());
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
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (null == chatroomBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		HashSet<String> chatrooms = userBo.getChatrooms();
		LinkedList<String> chatroomsTop = userBo.getChatroomsTop();
		if (chatrooms.contains(chatroomid)) {
			chatrooms.remove(chatroomid);
		}
		if (chatroomsTop.contains(chatroomid)) {
			chatroomsTop.remove(chatroomid);
		}
		chatroomsTop.add(0, chatroomid);
		userService.updateChatrooms(userBo);
		return Constant.COM_RESP;
	}

	@RequestMapping("/cancel-top")
	@ResponseBody
	public String cancelTop(String chatroomid, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (null == chatroomBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		HashSet<String> chatrooms = userBo.getChatrooms();
		LinkedList<String> chatroomsTop = userBo.getChatroomsTop();
		if (chatroomsTop.contains(chatroomid)) {
			chatroomsTop.remove(chatroomid);
		}
		if (!chatrooms.contains(chatroomid)) {
			chatrooms.add(chatroomid);
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
		int type = isNew ? 0 : 1;
		String res = IMUtil.subscribe(type,chatroom.getId(), userBo.getId());
		logger.info("face  user {}, chatroom {},  res {}", userBo.getId(), chatroom.getId(), res);
		if (!res.equals(IMUtil.FINISH)) {
			//失败需要还原
			if (isNew) {
				chatroomService.remove(chatroom.getId());
			} else {
				LinkedHashSet<String> userSet = chatroom.getUsers();
				userSet.remove(userBo.getId());
				chatroom.setUsers(userSet);
				chatroomService.updateUsers(chatroom);
			}
			return res;
		}
		HashSet<String> chatrooms = userBo.getChatrooms();
		chatrooms.add(chatroom.getId());
		userBo.setChatrooms(chatrooms);
		userService.updateChatrooms(userBo);
		addChatroomUser(userBo, chatroom.getId(), userBo.getUserName());
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
			chatroomService.updateName(chatroomid, name, true);
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
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (chatroomBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		chatroomService.updateUserNickname(userBo.getId(),chatroomid, nickname);
		return Constant.COM_RESP;
	}

	@RequestMapping("/get-nicknames")
	@ResponseBody
	public String getNickname(String chatroomid,
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
		List<ChatroomUserBo> chatroomUserBos = chatroomService.findByUserRoomid(chatroomid);
		List<ChatroomUserVo> userVos = new ArrayList<>();
		for (ChatroomUserBo chatroomUserBo : chatroomUserBos) {
			String userid = chatroomUserBo.getUserid();
			UserBo chatUser = userService.getUser(userid);
			if (chatUser == null) {
				chatroomService.deleteUser(chatroomUserBo.getId());
				continue;
			}
			ChatroomUserVo userVo = new ChatroomUserVo();
			if (userid.equals(chatroomBo.getMaster())) {
				userVo.setRole(2);
			}
			userVo.setUserid(userid);
			userVo.setUserPic(chatUser.getHeadPictureName());
			userVo.setNickname(chatroomUserBo.getNickname());
			userVos.add(userVo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("chatroomUsers", userVos);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/update-shownick")
	@ResponseBody
	public String updateShowNickname(String chatroomid, boolean isShowNick,
								 HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (chatroomBo == null) {
			deleteNickname(userBo.getId(), chatroomid);
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		chatroomService.updateShowNick(userBo.getId(), chatroomid, isShowNick);
		return Constant.COM_RESP;
	}

	@RequestMapping("/update-disturb")
	@ResponseBody
	public String updateDisturb(String chatroomid, boolean isDisturb,
								 HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (chatroomBo == null) {
			deleteNickname(userBo.getId(), chatroomid);
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userBo.getId(), chatroomid);
		//单人聊天也存在免打扰信息
		if (chatroomUserBo == null) {
			chatroomUserBo = new ChatroomUserBo();
			chatroomUserBo.setChatroomid(chatroomid);
			chatroomUserBo.setUserid(userBo.getId());
			chatroomUserBo.setUsername(userBo.getUserName());
			chatroomUserBo.setShowNick(false);
			chatroomUserBo.setDisturb(isDisturb);
			chatroomService.insertUser(chatroomUserBo);
		} else {
			chatroomService.updateDisturb(chatroomUserBo.getId(), isDisturb);
		}
		return Constant.COM_RESP;
	}

	/**
	 * 添加聊天室用户的昵称
	 * @param chatroomid
	 * @param nickname
	 */
	@Async
	private void addChatroomUser(UserBo userBo, String chatroomid, String nickname){
		ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userBo.getId(), chatroomid);
		if (chatroomUserBo == null) {
			chatroomUserBo = new ChatroomUserBo();
			chatroomUserBo.setChatroomid(chatroomid);
			chatroomUserBo.setUserid(userBo.getId());
			chatroomUserBo.setNickname(nickname);
			chatroomUserBo.setUsername(userBo.getUserName());
			chatroomUserBo.setShowNick(false);
			chatroomUserBo.setDisturb(false);
			chatroomService.insertUser(chatroomUserBo);
		} else {
			chatroomService.updateUserNickname(chatroomUserBo.getId(), nickname);
		}
	}

	/**
	 * 删除群聊中的用户聊天昵称
	 * @param userid
	 * @param chatroomid
	 */
	@Async
	private void deleteNickname(String userid, String chatroomid){
		chatroomService.deleteChatroomUser(userid, chatroomid);
	}
}
