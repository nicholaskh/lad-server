package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.*;
import com.lad.vo.ChatroomUserVo;
import com.lad.vo.ChatroomVo;
import com.lad.vo.ReasonVo;
import com.lad.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Api(value = "ChatroomController", description = "聊天信息相关接口")
@RestController
@RequestMapping("chatroom")
public class ChatroomController extends BaseContorller {

	private static Logger logger = LogManager.getLogger(ChatroomController.class);

	@Autowired
	private IChatroomService chatroomService;
	@Autowired
	private IUserService userService;

	@Autowired
	private RedisServer redisServer;

	@Autowired
	private IReasonService reasonService;

	@Autowired
	private IFriendsService friendsService;

	@Autowired
	private IPartyService partyService;


	private String titlePush = "互动通知";

	@ApiOperation("创建群聊")
	@ApiImplicitParam(name = "name", value = "群聊名称", dataType = "string", paramType = "query")
	@PostMapping("/create")
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
		users.add(userBo.getId());
		chatroomService.insert(chatroomBo);
		//第一个为返回结果信息，第二位term信息
		String result = IMUtil.subscribe(0, chatroomBo.getId(), userBo.getId());
		if (!result.equals(IMUtil.FINISH)) {
			chatroomService.remove(chatroomBo.getId());
			return result;
		}
		userService.updateChatrooms(userBo);
		addChatroomUser(chatroomService, userBo, chatroomBo.getId(), userBo.getUserName());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("channelId", chatroomBo.getId());
		return JSONObject.fromObject(map).toString();
	}


	@ApiOperation("添加好友")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userids", value = "好友userid,多个以逗号隔开", required = true, paramType = "query",
					dataType = "string"),
			@ApiImplicitParam(name = "chatroomid", value = "群聊id",paramType = "query",dataType = "string")})
	@PostMapping("/insert-user")
	public String insertUser(String userids, String chatroomid,
							 HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
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
		if (chatroomBo.isVerify() && !chatroomBo.getMaster().equals(userBo.getId())) {
			for (String userid : useridArr) {
				ReasonBo reasonBo = reasonService.findByUserAndChatroom(userid, chatroomid);
				if (reasonBo == null) {
					reasonBo = new ReasonBo();
					reasonBo.setStatus(0);
					reasonBo.setCreateuid(userid);
					reasonBo.setReasonType(1);
					reasonBo.setChatroomid(chatroomid);
					reasonBo.setOperUserid(userBo.getId());
					reasonBo.setReason(userBo.getUserName().concat("邀请加入群聊"));
					reasonService.insert(reasonBo);
				} 
			}
			String path = "";
			String content = String.format("“%s”邀请您加入群聊", userBo.getUserName());
			JPushUtil.push(titlePush, content, path,  useridArr);
			return Constant.COM_RESP;
		}
		//第一个为返回结果信息，第二位term信息
		String result = IMUtil.subscribe(1,chatroomid, useridArr);
		if (!result.equals(IMUtil.FINISH)) {
			return result;
		}

		// 为IMUtil通知做数据准备
		LinkedHashSet<String> set = chatroomBo.getUsers();
		String[] tt = new String[set.size() -1];
		int i=0;
		for(String uu: set){
			if(uu.equals(userBo.getId())) continue;
			tt[i++] = uu;
		}
		Object[] otherNameAndId = ChatRoomUtil.getUserNamesAndIds(userService, tt, logger);
		ArrayList<String> imNames = new ArrayList<>();
		ArrayList<String> imIds = new ArrayList<>();

		for (String userid : useridArr) {
			if (set.contains(userid)) {
				continue;
			}
			UserBo user = userService.getUser(userid);
			if (null != user) {
				updateUserChatroom(userid, chatroomid, true);
				addChatroomUser(chatroomService, user, chatroomBo.getId(), user.getUserName());
				set.add(userid);
				imNames.add(user.getUserName());
				imIds.add(user.getId());
				JPushUtil.pushTo(String.format("“%s”邀请您加入群聊", userBo.getUserName()), userid);
			}
		}
		String name = chatroomBo.getName();
		// 如果群聊没有修改过名称，自动修改名称
		RLock lock = redisServer.getRLock(chatroomid.concat("users"));
		try {
			lock.lock(3, TimeUnit.SECONDS);
			if(!chatroomBo.isNameSet()){
				String newChatRoomName = ChatRoomUtil.generateChatRoomName(userService, set, chatroomid, logger);
				name = newChatRoomName != null ? newChatRoomName : name;
			}
			chatroomService.updateNameAndUsers(chatroomid, name, chatroomBo.isNameSet(), set);
		} finally {
			lock.unlock();
		}
		addRoomInfo(userBo, chatroomid, imIds, imNames, otherNameAndId);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("channelId", chatroomBo.getId());
		map.put("chatroomUser", set.size());
		map.put("chatroomName", name);
		return JSONObject.fromObject(map).toString();
	}


	@ApiOperation("群聊删除用户")
	@PostMapping("/delete-user")
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

		/**
		 * TODO 发通知成功，后面失败了如何处理
		 *
		 * 之所以先发通知，是因为调用了IMUtil.unSubscribe这个后，订阅的关系都解除了，就没法基于群聊发通知了，被踢出的人就不能收到消息
		 */
		Object[] nameAndIds = ChatRoomUtil.getUserNamesAndIds(userService, useridArr, logger);
		if (nameAndIds[0] != null){
			JSONObject json = new JSONObject();
			json.put("masterId", userBo.getId());
			json.put("masterName", userBo.getUserName());
			json.put("hitIds", nameAndIds[1]);
			json.put("hitNames", nameAndIds[0]);

			// 向群中发踢人通知
			String res = IMUtil.notifyInChatRoom(Constant.SOME_ONE_EXPELLED_FROM_CHAT_ROOM, chatroomid, json.toString());
			if(!IMUtil.FINISH.equals(res)){
				logger.error("failed notifyInChatRoom Constant.SOME_ONE_EXPELLED_FROM_CHAT_ROOM, %s",res);
				return res;
			}
		}

		//第一个为返回结果信息，第二位term信息
		String result = IMUtil.unSubscribe(chatroomid, useridArr);
		if (!result.equals(IMUtil.FINISH) && !result.contains("not found")) {
			return result;
		}

		StringBuilder imIds = new StringBuilder();
		StringBuilder imNames = new StringBuilder();

		LinkedHashSet<String> set = chatroomBo.getUsers();

		LinkedHashSet<String> deleteUsers = new LinkedHashSet<>();
		for (String userid : useridArr) {
			//只能删除非自己以外人员，自己需要退出
			if (!set.contains(userid) || userid.equals(userBo.getId())) {
				continue;
			}
			deleteUsers.add(userid);
			set.remove(userid);
			UserBo user = userService.getUser(userid);
			if (null != user) {
				updateUserChatroom(userid, chatroomid, false);
				imIds.append(user.getId());
				imIds.append(",");

				imNames.append(user.getUserName());
				imNames.append(",");
			}
		}
		String name = chatroomBo.getName();

		//聊天室少于2人则直接删除
		if (set.size() < 2) {
			String res = IMUtil.disolveRoom(chatroomid);
			if (!res.equals(IMUtil.FINISH) && !res.contains("not found")) {
				return res;
			}
			deletePartyChatroom(chatroomBo, set);
		} else {
			if(!chatroomBo.isNameSet()){
				String newChatRoomName = ChatRoomUtil.generateChatRoomName(userService, set, chatroomid, logger);
				name = newChatRoomName != null ? newChatRoomName : name;
			}
			// 如果群聊没有修改过名称，自动修改名称
			RLock lock = redisServer.getRLock(chatroomid.concat("users"));
			try {
				lock.lock(3, TimeUnit.SECONDS);

				chatroomService.updateNameAndUsers(chatroomid, name, chatroomBo.isNameSet(), set);
			} finally {
				lock.unlock();
			}
		}
		chatroomService.deleteChatroom(deleteUsers, chatroomid);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("channelId", chatroomBo.getId());
		map.put("chatroomUser", set.size());
		map.put("chatroomName", name);
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("退出群聊")
	@PostMapping("/quit")
	public String quit(String chatroomid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (null == chatroomBo) {
			updateUserChatroom(userBo.getId(), chatroomid, false);
			return Constant.COM_RESP;
		}

		/**
		 * TODO 发通知成功，后面失败了如何处理
		 *
		 * 之所以先发通知，是因为调用了IMUtil.unSubscribe这个后，订阅的关系都解除了，就没法基于群聊发通知了，被踢出的人就不能收到消息
		 * 目前先这样处理，之后需要添加基于个人的发送通知功能
		 */
		// 向群中发某人退出群聊通知
		JSONObject json = new JSONObject();
		json.put("masterId", userBo.getId());
		json.put("masterName", userBo.getUserName());
		String res2 = IMUtil.notifyInChatRoom(Constant.SOME_ONE_QUIT_CHAT_ROOM, chatroomid, json.toString());
		if(!IMUtil.FINISH.equals(res2)){
			logger.error("failed notifyInChatRoom Constant.SOME_ONE_QUIT_CHAT_ROOM, %s",res2);
			return res2;
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

				// 通知群主变更通知
				UserBo nextMaster = userService.getUser(nextId);
				if(nextMaster != null){
					JSONObject json2 = new JSONObject();
					json2.put("masterId", nextMaster.getId());
					json2.put("masterName", nextMaster.getUserName());
					String res3 = IMUtil.notifyInChatRoom(Constant.MASTER_CHANGE_CHAT_ROOM, chatroomid, json2.toString());
					if(!IMUtil.FINISH.equals(res3)){
						logger.error("failed notifyInChatRoom Constant.SOME_ONE_QUIT_CHAT_ROOM, %s",res3);
						return res3;
					}
				}
			}
		}
		updateUserChatroom(userBo.getId(), chatroomid, false);
		deleteNickname(userid, chatroomid);
		set.remove(userid);

		String name = chatroomBo.getName();

		if (set.size() < 2) {
			String res = IMUtil.disolveRoom(chatroomid);
			if (!res.equals(IMUtil.FINISH) && !res.contains("not found")) {
				return res;
			}
			deletePartyChatroom(chatroomBo, set);

		} else {
			// 如果群聊没有修改过名称，自动修改名称
			RLock lock = redisServer.getRLock(chatroomid.concat("users"));
			try {
				lock.lock(3, TimeUnit.SECONDS);
				if(!chatroomBo.isNameSet()){
					String newChatRoomName = ChatRoomUtil.generateChatRoomName(userService, set, chatroomid, logger);
					name = newChatRoomName != null ? newChatRoomName : name;
				}
				chatroomService.updateNameAndUsers(chatroomid, name, chatroomBo.isNameSet(), set);
			} finally {
				lock.unlock();
			}
		}
		chatroomService.deleteChatroomUser(userid, chatroomid);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("channelId", chatroomBo.getId());
		map.put("chatroomUser", set.size());
		map.put("chatroomName", name);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 更新当前用户的聊天室
	 * @param userid
	 * @param chatroomid
	 */
	@Async
	private void updateUserChatroom(String userid, String chatroomid, boolean isAdd){
		RLock lock = redisServer.getRLock(userid.concat("chatroom"));
		try {
			lock.lock(3, TimeUnit.SECONDS);
			UserBo userBo = userService.getUser(userid);
			if (userBo == null) {
				return;
			}
			HashSet<String> chatroom = userBo.getChatrooms();
			LinkedList<String> chatroomTops = userBo.getChatroomsTop();
			HashSet<String> showRooms = userBo.getShowChatrooms();
			if (isAdd) {
				userBo.setChatrooms(chatroom);
				//个人聊天室中没有当前聊天室，则添加到个人的聊天室
				if (!chatroom.contains(chatroomid)) {
					chatroom.add(chatroomid);
				}
				showRooms.add(chatroomid);
			} else {
				if (chatroom.contains(chatroomid)) {
					chatroom.remove(chatroomid);
					userBo.setChatrooms(chatroom);
				}
				if (chatroomTops.contains(chatroomid)) {
					chatroomTops.remove(chatroomid);
					userBo.setChatroomsTop(chatroomTops);
				}
				showRooms.remove(chatroomid);
			}
			userBo.setShowChatrooms(showRooms);
			userService.updateChatrooms(userBo);
		} finally {
			lock.unlock();
		}
		chatroomService.deleteChatroomUser(userid, chatroomid);
	}


	@ApiOperation("获取好友列表")
	@PostMapping("/get-friends")
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

	@ApiOperation("获取所有聊天室信息")
	@PostMapping("/get-my-chatrooms")
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
				ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userid, id);
				boolean has = chatroomUserBo != null;
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(temp, vo);
				if (temp.getType() != 1) {
					bo2vo(temp, vo);
					vo.setUserNum(temp.getUsers().size());
					vo.setShowNick(has && chatroomUserBo.isShowNick());
				}
				vo.setDisturb(has && chatroomUserBo.isDisturb());
				vo.setTop(1);
				addName(temp, vo);
				chatroomList.add(vo);
			} else {
				removeTops.add(id);
			}
		}
		for (String id : chatrooms) {
			ChatroomBo temp = chatroomService.get(id);
			if (null != temp) {
				ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userid, id);
				boolean has = chatroomUserBo != null;
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(temp, vo);
				if (temp.getType() != 1) {
					bo2vo(temp, vo);
					vo.setUserNum(temp.getUsers().size());
					vo.setShowNick(has && chatroomUserBo.isShowNick());
				}
				addName(temp, vo);
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

	@ApiOperation("更具时间戳，获取好友列表")
	@PostMapping("/my-chatrooms")
	public String getChatrooms(String timestamp, HttpServletRequest request,
							   HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		String userid = userBo.getId();
		LinkedList<String> chatroomsTop = userBo.getChatroomsTop();
		List<ChatroomVo> chatroomList = new LinkedList<ChatroomVo>();
		HashSet<String> removes = new LinkedHashSet<>();
		LinkedList<String> removeTops = new LinkedList<>();
		HashSet<String> showRooms = userBo.getShowChatrooms();
		Date times;
		try {
			times = CommonUtil.getDate(timestamp);
		} catch (ParseException e){
			return CommonUtil.toErrorResult(ERRORCODE.FORMAT_ERROR.getIndex(),
					ERRORCODE.FORMAT_ERROR.getReason());
		}

		List<ChatroomVo> chats = new LinkedList<>();

		List<ChatroomBo> chatroomBos = chatroomService.findMyChatrooms(userid, times);

		String timeStr = "";
		if (chatroomBos != null && !chatroomBos.isEmpty()) {
			ChatroomBo first = chatroomBos.get(0);
			timeStr = CommonUtil.getDateStr(first.getCreateTime(),"yyyy-MM-dd HH:mm:ss");
			for (ChatroomBo chatroomBo : chatroomBos) {
				//如果在展示的窗口中没有，表示已经删除
				if (!showRooms.contains(chatroomBo.getId())) {
					continue;
				}
				ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userid, chatroomBo.getId());
				boolean has = chatroomUserBo != null;
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(chatroomBo, vo);
				if (chatroomBo.getType() != 1) {
					bo2vo(chatroomBo, vo);
					vo.setUserNum(chatroomBo.getUsers().size());
					vo.setShowNick(has && chatroomUserBo.isShowNick());
				}
				vo.setDisturb(has && chatroomUserBo.isDisturb());
				if (chatroomsTop.contains(chatroomBo.getId())) {
					vo.setTop(1);
					chatroomList.add(vo);
				} else {
					chats.add(vo);
				}
				addName(chatroomBo, vo);
			}
			chatroomList.addAll(chats);
			updateUserRoom(userBo, removes, removeTops);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("timestamp", StringUtils.isNotEmpty(timeStr) ? timeStr : timestamp);
		map.put("ret", 0);
		map.put("ChatroomList", chatroomList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 临时聊天室名称
	 * @param chatroomBo
	 * @param chatroomVo
	 */
	private void addName(ChatroomBo chatroomBo, ChatroomVo chatroomVo){
		if (chatroomBo.getType() == 1 && StringUtils.isNotEmpty(chatroomBo.getTargetid())){
			UserBo userBo = userService.getUser(chatroomBo.getFriendid());
			if (userBo != null) {
				chatroomVo.setName(userBo.getUserName());
			}
		}
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

	private void bo2vo(ChatroomBo chatroomBo, ChatroomVo vo){
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
			if (StringUtils.isNotEmpty(chatroomUser.getNickname())) {
				userVo.setNickname(chatroomUser.getNickname());
			} else {
				userVo.setNickname(chatUser.getUserName());
			}
			userVos.add(userVo);
		}
	}

	@ApiOperation("获取聊天室详情")
	@PostMapping("/get-chatroom-info")
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
				chatroomUserBo.setNickname(userBo.getUserName());
				chatroomUserBo.setShowNick(false);
				chatroomUserBo.setDisturb(false);
				chatroomService.insertUser(chatroomUserBo);
				vo.setDisturb(false);
			} else {
				if (temp.getType() != 1) {
					bo2vo(temp, vo);
					vo.setUserNum(temp.getUsers().size());
					vo.setShowNick(chatroomUserBo.isShowNick());
				} else {
					FriendsBo bo = friendsService.getFriendByIdAndVisitorIdAgree(temp.getUserid(), temp
							.getFriendid());
					if (bo != null && StringUtils.isNotEmpty(bo.getBackname())) {
						vo.setName(bo.getBackname());
					} else {
						UserBo friend = userService.getUser(temp.getFriendid());
						if (friend != null){
							vo.setName(friend.getUserName());
						} else {
							vo.setName(chatroomUserBo.getUsername());
						}
					}
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

	@ApiOperation("置顶聊天室")
	@PostMapping("/set-top")
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

	@ApiOperation("取消聊天室置顶")
	@PostMapping("/cancel-top")
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

	@ApiOperation("创建面对面群聊")
	@PostMapping("/facetoface-create")
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
				chatroom.setName(userBo.getUserName());
                chatroomService.insert(chatroom);
            } else {
				// 如果群聊没有修改过名称，自动修改名称
				String name = chatroom.getName();
				if(!chatroom.isNameSet()){
					String newChatRoomName = ChatRoomUtil.generateChatRoomName(userService,
							chatroom.getUsers(), chatroom.getId(), logger);
					name = newChatRoomName != null ? newChatRoomName : name;
				}
				chatroomService.updateNameAndUsers(chatroom.getId(), name, chatroom.isNameSet(), chatroom.getUsers());
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
				updateChatroomUser(chatroom.getId(), userSet);
			}
			return res;
		}
		HashSet<String> chatrooms = userBo.getChatrooms();
		chatrooms.add(chatroom.getId());
		userBo.setChatrooms(chatrooms);
		userService.updateChatrooms(userBo);
		addChatroomUser(chatroomService, userBo, chatroom.getId(), userBo.getUserName());

		// 发送通知推送
		if(!isNew){
			LinkedHashSet<String> users = chatroom.getUsers();
			String[] tt = new String[users.size()-1];
			int i=0;
			for(String uu: users){
				if(uu.equals(userBo.getId())) continue;
				tt[i++] = uu;
			}
			Object[] otherNameAndId = ChatRoomUtil.getUserNamesAndIds(userService, tt, logger);

			if(otherNameAndId[0] != null){
				JSONObject json = new JSONObject();
				json.put("masterId", userBo.getId());
				json.put("masterName", userBo.getUserName());
				json.put("otherIds", otherNameAndId[1]);
				json.put("otherNames", otherNameAndId[0]);

				// 向群中发某人加入群聊通知
				String res2 = IMUtil.notifyInChatRoom(
						Constant.SOME_ONE_JOIN_CHAT_ROOM,
						chatroom.getId(),
						json.toString());
				if(!IMUtil.FINISH.equals(res2)){
					logger.error("failed notifyInChatRoom Constant.FACE_TO_FACE_SOME_ONE_JOIN_CHAT_ROOM, %s",res2);
				}
			}

		}else{

			JSONObject json = new JSONObject();
			json.put("masterId", userBo.getId());
			json.put("masterName", userBo.getUserName());
			json.put("number", seq);

			String res2 = IMUtil.notifyInChatRoom(
					Constant.FACE_TO_FACE_SOME_ONE_JOIN_CHAT_ROOM,
					chatroom.getId(),
					json.toString());
			if(!IMUtil.FINISH.equals(res2)){
				logger.error("failed notifyInChatRoom Constant.FACE_TO_FACE_SOME_ONE_JOIN_CHAT_ROOM, %s",res2);
			}
		}

		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("channelId", chatroom.getId());
		return JSONObject.fromObject(map).toString();
	}


	private ChatroomBo getChatroomBo(int seq, double[] position, UserBo userBo){
		ChatroomBo chatroom = new ChatroomBo();
		chatroom.setSeq(seq);
		chatroom.setCreateuid(userBo.getId());
		LinkedHashSet<String> userSet = chatroom.getUsers();
		userSet.add(userBo.getId());
		chatroom.setUsers(userSet);
		chatroom.setName(userBo.getUserName());
		chatroom.setMaster(userBo.getId());
		chatroom.setPosition(position);
		chatroom.setType(Constant.ROOM_FACE_2_FACE);
		return chatroom;
	}


	@ApiOperation("面对面群聊添加好友")
	@PostMapping("/factoface-add")
	public String faceToFaceAdd(int seq, double px, double py,
								HttpServletRequest request, HttpServletResponse response) {
		return faceToFaceCreate(seq, px, py, request, response);
	}



	@ApiOperation("转让群聊")
	@PostMapping("/trans-chatroom")
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

		// 通知群主变更通知
		JSONObject json = new JSONObject();
		json.put("masterId", master.getId());
		json.put("masterName", master.getUserName());
		String res3 = IMUtil.notifyInChatRoom(Constant.MASTER_CHANGE_CHAT_ROOM, chatroomid, json.toString());
		if(!IMUtil.FINISH.equals(res3)){
			logger.error("failed notifyInChatRoom Constant.SOME_ONE_QUIT_CHAT_ROOM, %s",res3);
			return res3;
		}

		String content = String.format("“%s将群聊【%s】转让给了您，快去看看吧", userBo.getUserName(),
				chatroomBo.getName());
        JPushUtil.push(titlePush, content, "", userid);
		return Constant.COM_RESP;
	}

	@ApiOperation("更新群聊名称")
	@PostMapping("/update-name")
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

		// 向群中发某人修改群聊名称通知
		JSONObject json = new JSONObject();
		json.put("masterId", userBo.getId());
		json.put("masterName", userBo.getUserName());
		json.put("chatRoomName", name);
		String res2 = IMUtil.notifyInChatRoom(Constant.SOME_ONE_MODIFY_NAME_OF_CHAT_ROOM, chatroomid, json.toString());
		if(!IMUtil.FINISH.equals(res2)){
			logger.error("failed notifyInChatRoom Constant.SOME_ONE_MODIFY_NAME_OF_CHAT_ROOM, %s",res2);
		}

		return Constant.COM_RESP;
	}

	@ApiOperation("更新群聊描述")
	@PostMapping("/update-description")
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

	@ApiOperation("更新群聊加入开发状态")
	@PostMapping("/update-open")
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

	@ApiOperation("更新群聊加入验证状态")
	@PostMapping("/update-verify")
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
			LinkedHashSet<String> set = chatroomBo.getUsers();
			set.remove(userBo.getId());
			ArrayList<String> ids = new ArrayList<>(set.size());
			ArrayList<String> names = new ArrayList<>(set.size());
			LinkedHashSet<String> removes = new LinkedHashSet<>();
			for (String userid : set) {
				UserBo user = userService.getUser(userid);
				if (user == null) {
					logger.error(" userid {} is not exists ", userid);
					removes.add(userid);
					continue;
				}
				ids.add(userid);
				names.add(user.getUserName());
			}
			if(ids.size() > 0){
				JSONObject json = new JSONObject();
				json.put("masterId", userBo.getId());
				json.put("masterName", userBo.getUserName());
				json.put("otherIds", ids);
				json.put("otherNames", names);
				json.put("verify", isVerify);
				// 向群中发某人加入群聊通知
				String res = IMUtil.notifyInChatRoom(
						Constant.MASTER_CHANGE_CHAT_VERIFY, chatroomBo.getId(), json.toString());
				if(!IMUtil.FINISH.equals(res)){
					logger.error("failed notifyInChatRoom Constant.MASTER_CHANGE_CHAT_VERIFY , %s",res);
				}
			}
			//删除不存在的用户
			if (!removes.isEmpty()) {
				set.add(userBo.getId());
				set.remove(removes);
				updateChatroomUser(chatroomid, set);
			}
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		return Constant.COM_RESP;
	}

	/**
	 * 删除用户
	 * @param chatroomid
	 * @param users
	 */
	@Async
	private void updateChatroomUser(String chatroomid, LinkedHashSet<String> users){
			RLock lock = redisServer.getRLock(chatroomid.concat("users"));
			try {
				lock.lock(2, TimeUnit.SECONDS);
				chatroomService.updateUsers(chatroomid, users);
			} finally {
				lock.unlock();
			}
	}


	@ApiOperation("修改群聊里面的个人昵称")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "chatroomid", value = "群聊id", required = true, dataType =
					"string", paramType = "query"),
			@ApiImplicitParam(name = "nickname", value = "昵称", required = true, dataType = "string", paramType =
					"query")})
	@PostMapping("/update-nickname")
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
		ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userBo.getId(), chatroomid);
		if (chatroomUserBo == null) {
			 chatroomUserBo = new ChatroomUserBo();
			 chatroomUserBo.setNickname(nickname);
			 chatroomUserBo.setUserid(userBo.getId());
			 chatroomUserBo.setChatroomid(chatroomid);
			 chatroomService.insertUser(chatroomUserBo);
		} else {
			chatroomService.updateUserNickname(userBo.getId(),chatroomid, nickname);
		}
		return Constant.COM_RESP;
	}

	@ApiOperation("获取群聊用户的昵称信息")
	@ApiImplicitParam(name = "chatroomid", value = "群聊id", required = true, dataType =
					"string", paramType = "query")
	@PostMapping("/get-nicknames")
	@ResponseBody
	public String getNickname(String chatroomid,
								 HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
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

	@ApiOperation("更新昵称显示状态")
	@PostMapping("/update-shownick")
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
		ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userBo.getId(), chatroomid);
		if (chatroomUserBo == null) {
			chatroomUserBo = new ChatroomUserBo();
			chatroomUserBo.setNickname(userBo.getUserName());
			chatroomUserBo.setUserid(userBo.getId());
			chatroomUserBo.setChatroomid(chatroomid);
			chatroomUserBo.setShowNick(isShowNick);
			chatroomService.insertUser(chatroomUserBo);
		} else {
			chatroomService.updateShowNick(userBo.getId(), chatroomid, isShowNick);
		}
		return Constant.COM_RESP;
	}

	@ApiOperation("更新群聊免打扰状态")
	@PostMapping("/update-disturb")
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


	@ApiOperation("申请加入群聊，包括通过二维码扫描")
	@PostMapping("/apply-insert")
	public String applyInsert(String chatroomid, String shareUserid,
							 HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String userid = userBo.getId();
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (chatroomBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		if (chatroomBo.isVerify()) {
			String reason = String.format("“%s”通过二维码申请加入群聊",userBo.getUserName());
			ReasonBo reasonBo = reasonService.findByUserAndChatroom(userid, chatroomid);
			if (reasonBo == null) {
				reasonBo = new ReasonBo();
				reasonBo.setStatus(0);
				reasonBo.setCreateuid(userid);
				reasonBo.setReasonType(1);
				reasonBo.setOperUserid(shareUserid);
				reasonBo.setChatroomid(chatroomid);
				reasonBo.setAddType(2);
				reasonBo.setReason(reason);
				reasonService.insert(reasonBo);
			} else if (reasonBo.getStatus() == Constant.ADD_APPLY){
				return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_APPLY_EXIST.getIndex(),
						ERRORCODE.CHATROOM_APPLY_EXIST.getReason());
			} else if (reasonBo.getStatus() == Constant.ADD_AGREE) {
				return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_HAS_ADD.getIndex(),
						ERRORCODE.CHATROOM_HAS_ADD.getReason());
			} else {
				reasonService.updateApply(reasonBo.getId(), Constant.ADD_APPLY, reason);
			}
			return Constant.RESP_SUCCES;
		} else {
			//第一个为返回结果信息，第二位term信息
			String result = IMUtil.subscribe(1,chatroomid, userBo.getId());
			if (!result.equals(IMUtil.FINISH)) {
				return result;
			}
			addChatroomUser(chatroomService, userBo, chatroomid, userBo.getUserName());
			HashSet<String> chatroom = userBo.getChatrooms();
			//个人聊天室中没有当前聊天室，则添加到个人的聊天室
			if (!chatroom.contains(chatroomid)) {
				chatroom.add(chatroomid);
				userBo.setChatrooms(chatroom);
				userService.updateChatrooms(userBo);
			}
			// 为IMUtil通知做数据准备
			LinkedHashSet<String> set = chatroomBo.getUsers();
			int size = set.size();
			if (set.contains(userBo.getId())) {
				size --;
			}
			String name = chatroomBo.getName();
			String[] tt = new String[size];
			int i=0;
			for(String uu: set){
				if (uu.equals(userBo.getId())) continue;
				tt[i++] = uu;
			}
			Object[] otherNameAndId = ChatRoomUtil.getUserNamesAndIds(userService, tt, logger);
			ArrayList<String> imNames = new ArrayList<>();
			ArrayList<String> imIds = new ArrayList<>();
			// 如果群聊没有修改过名称，自动修改名称
			if(!chatroomBo.isNameSet()){
				String newChatRoomName = ChatRoomUtil.generateChatRoomName(userService, set, chatroomid, logger);
				name = newChatRoomName != null ? newChatRoomName : name;
			}
			updateChatroomNameAndUser(chatroomid, name, chatroomBo.isNameSet(), set);
			addRoomInfo(userBo, chatroomid, imIds, imNames, otherNameAndId);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ret", 0);
			map.put("channelId", chatroomBo.getId());
			map.put("chatroomUser", set.size());
			map.put("chatroomName", name);
			return JSONObject.fromObject(map).toString();
		}
	}

	/**
	 * 加群验证信息提交
	 * @param chatroomid
	 * @param reason
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("申请加群聊验证信息提交")
	@PostMapping("/add-verify")
	public String addVerify(String chatroomid, String reason,
								HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		ReasonBo reasonBo = reasonService.findByUserAndChatroom(userBo.getId(), chatroomid);
		if (reasonBo == null) {
			reasonBo = new ReasonBo();
			reasonBo.setStatus(0);
			reasonBo.setCreateuid(userBo.getId());
			reasonBo.setReasonType(1);
			reasonBo.setChatroomid(chatroomid);
			reasonBo.setReason(reason);
			reasonService.insert(reasonBo);
		} else if (reasonBo.getStatus() == Constant.ADD_APPLY){
			reasonService.updateApply(reasonBo.getId(), Constant.ADD_APPLY, reason);
			return Constant.COM_RESP;
		} else if (reasonBo.getStatus() == Constant.ADD_AGREE) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_HAS_ADD.getIndex(),
					ERRORCODE.CHATROOM_HAS_ADD.getReason());
		} else {
			reasonService.updateApply(reasonBo.getId(), Constant.ADD_APPLY, reason);
		}
		return Constant.COM_RESP;
	}

	/**
	 * 加群验证申请信息
	 * @param chatroomid
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("群聊申请加入列表")
	@PostMapping("/apply-list")
	public String roomVerifyList(String chatroomid, int page, int limit,
							HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<ReasonBo> reasonBos = reasonService.findByChatroomHis(chatroomid, page, limit);
		List<ReasonVo> reasonVos = new ArrayList<>();
		for (ReasonBo reasonBo : reasonBos) {
			UserBo user = userService.getUser(reasonBo.getCreateuid());
			if (user != null) {
				ReasonVo reasonVo = new ReasonVo();
				BeanUtils.copyProperties(user, reasonVo);
				reasonVo.setApplyid(reasonBo.getId());
				reasonVo.setApplyTime(reasonBo.getCreateTime());
				reasonVo.setUserid(user.getId());
				reasonVo.setReason(reasonBo.getReason());
				reasonVo.setRefuse(reasonBo.getRefues());
				reasonVo.setStatus(reasonBo.getStatus());
				reasonVos.add(reasonVo);
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("userApplyVos", reasonVos);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 加群验证操作
	 * @param chatroomid
	 * @param refues
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("操作加入群聊信息的申请")
	@PostMapping("/apply-operate")
	public String applyVerify(String chatroomid, String applyid, boolean isAgree, String refues,
							HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (chatroomBo == null) {
			CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		ReasonBo reasonBo = reasonService.findById(applyid);
		if (reasonBo == null) {
			CommonUtil.toErrorResult(ERRORCODE.CHATROOM_APPLY_NULL.getIndex(),
					ERRORCODE.CHATROOM_APPLY_NULL.getReason());
		}
		String name = chatroomBo.getName();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("channelId", chatroomBo.getId());
		if (isAgree) {
			//第一个为返回结果信息，第二位term信息
			String result = IMUtil.subscribe(1,chatroomid, userBo.getId());
			if (!result.equals(IMUtil.FINISH)) {
				return result;
			}
			addChatroomUser(chatroomService, userBo, chatroomid, userBo.getUserName());
			HashSet<String> chatroom = userBo.getChatrooms();
			//个人聊天室中没有当前聊天室，则添加到个人的聊天室
			if (!chatroom.contains(chatroomid)) {
				chatroom.add(chatroomid);
				userBo.setChatrooms(chatroom);
				userService.updateChatrooms(userBo);
			}
			// 为IMUtil通知做数据准备
			LinkedHashSet<String> set = chatroomBo.getUsers();
			int size = set.size();
			if (set.contains(userBo.getId())) {
				size --;
			}
			String[] tt = new String[size];
			int i=0;
			for(String uu: set){
				if (uu.equals(userBo.getId())) continue;
				tt[i++] = uu;
			}
			Object[] otherNameAndId = ChatRoomUtil.getUserNamesAndIds(userService, tt, logger);
			ArrayList<String> imNames = new ArrayList<>();
			ArrayList<String> imIds = new ArrayList<>();
			// 如果群聊没有修改过名称，自动修改名称
			if(!chatroomBo.isNameSet()){
				String newChatRoomName = ChatRoomUtil.generateChatRoomName(userService, set, chatroomid, logger);
				name = newChatRoomName != null ? newChatRoomName : name;
			}
			updateChatroomNameAndUser(chatroomid, name, chatroomBo.isNameSet(), set);
			addRoomInfo(userBo, chatroomid, imIds, imNames, otherNameAndId);
			reasonService.updateApply(applyid, Constant.ADD_AGREE, "");

			String path = "";
			String content = String.format("%s已通过您的加群申请", userBo.getUserName());
			JPushUtil.push(titlePush, content, path,  reasonBo.getCreateuid());

			map.put("chatroomUser", set.size());
		} else {
			reasonService.updateApply(applyid, Constant.ADD_REFUSE, refues);
			map.put("chatroomUser", chatroomBo.getUsers().size());
		}
		map.put("chatroomName", name);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 删除用户群聊窗口
	 * @param chatroomid
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("客户端删除群聊窗口")
	@PostMapping("/delete-show")
	public String deleteChatroomTable(String chatroomid,
							  HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		HttpSession session = request.getSession();
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		HashSet<String> showChatrooms = userBo.getShowChatrooms();
		if (showChatrooms.contains(chatroomid)) {
			showChatrooms.remove(chatroomid);
			userService.updateShowChatrooms(userBo.getId(), showChatrooms);
			//刷新session中个人信息
			session.setAttribute("userBo", userBo);
		}
		return Constant.COM_RESP;
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


	// 向群中某人被邀请加入群聊通知
	private void addRoomInfo(UserBo userBo, String chatroomid, List<String> imIds, List<String> imNames,
							 Object[] otherNameAndId){
		if(imIds.size() > 0 && otherNameAndId[0] != null){
			JSONObject json = new JSONObject();
			json.put("masterId", userBo.getId());
			json.put("masterName", userBo.getUserName());
			json.put("hitIds", imIds);
			json.put("hitNames", imNames);
			json.put("otherIds", otherNameAndId[1]);
			json.put("otherNames", otherNameAndId[0]);

			String res = IMUtil.notifyInChatRoom(Constant.SOME_ONE_BE_INVITED_OT_CHAT_ROOM, chatroomid, json.toString());
			if(!IMUtil.FINISH.equals(res)){
				logger.error("failed notifyInChatRoom Constant.SOME_ONE_BE_INVITED_OT_CHAT_ROOM, %s",res);
			}
		}
	}


	@Async
	private void updateChatroomNameAndUser(String chatroomid, String name, boolean isNameSet,LinkedHashSet<String>
			set) {
		// 如果群聊没有修改过名称，自动修改名称
		RLock lock = redisServer.getRLock(chatroomid.concat("users"));
		try {
			lock.lock(2, TimeUnit.SECONDS);
			chatroomService.updateNameAndUsers(chatroomid, name, isNameSet, set);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 判断退出是不是聚会的群聊
	 * @param chatroomBo
	 * @param set
	 */
	private void deletePartyChatroom(ChatroomBo chatroomBo, HashSet<String> set){

		boolean isPartyEnd = true;
		if (StringUtils.isNotEmpty(chatroomBo.getTargetid())){
			PartyBo partyBo = partyService.findById(chatroomBo.getTargetid());
			isPartyEnd = (partyBo != null && partyBo.getStatus() != 3);
		}
		if (!isPartyEnd) {
			//删除最后一人的聊天室
			if (set.size() == 1) {
				String friendid = set.iterator().next();
				updateUserChatroom(friendid, chatroomBo.getId(), false);
			}
			chatroomService.delete(chatroomBo.getId());
		}
	}


	/**
	 * 修改用户群聊，保证用户群聊同步
	 */
	@Async
	private void updateUserChatroooms(String userid, String chatroomid){
		RLock lock = redisServer.getRLock(userid.concat("chatroom"));
		try {
			lock.lock(3, TimeUnit.SECONDS);
			UserBo userBo = userService.getUser(userid);
			HashSet<String> chatroom = userBo.getChatrooms();
			HashSet<String> showRooms = userBo.getShowChatrooms();
			showRooms.add(chatroomid);
			userBo.setChatrooms(chatroom);
			//个人聊天室中没有当前聊天室，则添加到个人的聊天室
			if (!chatroom.contains(chatroomid)) {
				chatroom.add(chatroomid);
				userBo.setShowChatrooms(showRooms);
			}
			userService.updateChatrooms(userBo);
		} finally {
			lock.unlock();
		}
	}
}
