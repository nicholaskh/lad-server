package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.*;
import com.lad.vo.ChatroomUserVo;
import com.lad.vo.ChatroomVo;
import com.lad.vo.ReasonVo;
import com.lad.vo.UserBaseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Callable;
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

	@Autowired
	private ICircleService circleService;

	@Autowired
	private IMessageService messageService;

	@Autowired
	private AsyncController asyncController;


	private String titlePush = "互动通知";

	@ApiOperation("创建群聊")
	@ApiImplicitParam(name = "name", value = "群聊名称", dataType = "string", paramType = "query")
	@PostMapping("/create")
	public String create(String name, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
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
		updateUserSession(request, userService);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("channelId", chatroomBo.getId());
		return JSONObject.fromObject(map).toString();
	}


	@ApiOperation("群聊添加好友")
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
			addMessage(messageService, path, content, titlePush, userBo.getId(),useridArr);
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
				asyncController.updateUserChatroom(userid, chatroomid, true);
				addChatroomUser(chatroomService, user, chatroomBo.getId(), user.getUserName());
				set.add(userid);
				imNames.add(user.getUserName());
				imIds.add(user.getId());
				String msg = String.format("“%s”邀请您加入群聊", userBo.getUserName());
				JPushUtil.pushTo(msg, userid);
				addMessage(messageService, "", msg, titlePush, userid);
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
		asyncController.addRoomInfo(userBo, chatroomid, imIds, imNames, otherNameAndId);

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
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
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
				asyncController.updateUserChatroom(userid, chatroomid, false);
				imIds.append(user.getId());
				imIds.append(",");

				imNames.append(user.getUserName());
				imNames.append(",");
			}
		}
		String name = chatroomBo.getName();

		//聊天室最后1人退出人则直接删除
		if (set.size() < 1) {
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
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (null == chatroomBo) {
			asyncController.updateUserChatroom(userBo.getId(), chatroomid, false);
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
		if (set.size() >= 1) {
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
		asyncController.updateUserChatroom(userBo.getId(), chatroomid, false);
		asyncController.deleteNickname(userid, chatroomid);
		set.remove(userid);

		String name = chatroomBo.getName();

		if (set.size() < 1) {
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



	@ApiOperation("获取所有聊天室信息")
	@PostMapping("/get-my-chatrooms")
	public String getChatrooms(HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
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
		asyncController.updateUserRoom(userBo, removes, removeTops);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("ChatroomList", chatroomList);
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("更具时间戳，获取好友列表")
	@PostMapping("/my-chatrooms")
	public String getChatrooms(String timestamp, HttpServletRequest request,
							   HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
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
			asyncController.updateUserRoom(userBo, removes, removeTops);
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
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
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
			} 
			
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
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
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
		updateUserSession(request, userService);
		return Constant.COM_RESP;
	}

	@ApiOperation("取消聊天室置顶")
	@PostMapping("/cancel-top")
	public String cancelTop(String chatroomid, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
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
		updateUserSession(request, userService);
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
				asyncController.updateChatroomUser(chatroom.getId(), userSet);
			}
			return res;
		}
		HashSet<String> chatrooms = userBo.getChatrooms();
		chatrooms.add(chatroom.getId());
		userBo.setChatrooms(chatrooms);
		userService.updateChatrooms(userBo);
		updateUserSession(request, userService);
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
		addMessage(messageService, "", content, titlePush, userid);
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
				//异步推送群聊信息
				Callable call = () -> {
					JSONObject json = new JSONObject();
					json.put("masterId", userBo.getId());
					json.put("masterName", userBo.getUserName());
					json.put("otherIds", ids);
					json.put("otherNames", names);
					json.put("verify", isVerify);
					// 向群中发某人加入群聊通知
					String res = IMUtil.notifyInChatRoom(
							Constant.MASTER_CHANGE_CHAT_VERIFY, chatroomid, json.toString());
					if(!IMUtil.FINISH.equals(res)){
						logger.error("failed notifyInChatRoom Constant.MASTER_CHANGE_CHAT_VERIFY , %s",res);
					}
					return null;
				};
			}
			//删除不存在的用户
			if (!removes.isEmpty()) {
				set.add(userBo.getId());
				set.removeAll(removes);
				asyncController.updateChatroomUser(chatroomid, set);
			}
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		return Constant.COM_RESP;
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
			asyncController.deleteNickname(userBo.getId(), chatroomid);
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
			asyncController.deleteNickname(userBo.getId(), chatroomid);
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
			asyncController.updateChatroomNameAndUser(chatroomid, name, chatroomBo.isNameSet(), set);
			asyncController.addRoomInfo(userBo, chatroomid, imIds, imNames, otherNameAndId);
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
			asyncController.updateChatroomNameAndUser(chatroomid, name, chatroomBo.isNameSet(), set);
			asyncController.addRoomInfo(userBo, chatroomid, imIds, imNames, otherNameAndId);
			reasonService.updateApply(applyid, Constant.ADD_AGREE, "");

			String path = "";
			String content = String.format("%s已通过您的加群申请", userBo.getUserName());
			JPushUtil.push(titlePush, content, path,  reasonBo.getCreateuid());
			addMessage(messageService, path, content, titlePush, reasonBo.getCreateuid());
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
	 * 添加公告
	 */
	@ApiOperation("添加群聊公告")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "circleid", value = "群聊id", required = true, paramType = "query",
					dataType = "string"),
			@ApiImplicitParam(name = "title", value = "公告标题", paramType = "query",dataType = "string"),
			@ApiImplicitParam(name = "content", value = "公告内容", paramType = "query",dataType = "string"),
			@ApiImplicitParam(name = "images", value = "公告图片数组", dataType = "file")})
	@PostMapping("/add-notice")
	public String chatroomAddNotice(@RequestParam String chatroomid,
								  String title, String content, MultipartFile[] images,
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
		if (chatroomBo.getMaster().equals(userid)) {
			CircleNoticeBo noticeBo = new CircleNoticeBo();
			noticeBo.setContent(content);
			noticeBo.setTitle(title);
			noticeBo.setCreateuid(userid);
			noticeBo.setChatroomid(chatroomid);
			noticeBo.setNoticeType(1);
			//发布人默认阅读
			LinkedHashSet<String> readUsers = noticeBo.getReadUsers();
			readUsers.add(userid);
			HashSet<String> users = chatroomBo.getUsers();
			users.remove(userid);
			LinkedHashSet<String> unReadUsers = new LinkedHashSet<>();
			unReadUsers.addAll(users);
			noticeBo.setUnReadUsers(unReadUsers);
			noticeBo.setType(0);
			if (images != null) {
				LinkedHashSet<String> files = noticeBo.getImages();
				for (MultipartFile file : images) {
					long time = Calendar.getInstance().getTimeInMillis();
					String fileName = String.format("%s-%d-%s", userid, time, file.getOriginalFilename());
					String path = CommonUtil.upload(file,
							Constant.CHATROOM_PICTURE_PATH, fileName, 0);
					logger.info("chatroom add notice pic path: {},  size: {} ", path, file.getSize());
					files.add(path);
				}
				noticeBo.setImages(files);
			}
			circleService.addNotice(noticeBo);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_MASTER_NULL.getReason());
		}
		return Constant.COM_RESP;
	}

	/**
	 * 添加或修改公告
	 */
	@ApiOperation("修改群聊公告,不修改参数可为空")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "noticeid", value = "公告id", required = true, paramType = "query",
					dataType = "string"),
			@ApiImplicitParam(name = "title", value = "公告标题", paramType = "query",dataType = "string"),
			@ApiImplicitParam(name = "content", value = "公告内容", paramType = "query",dataType = "string"),
			@ApiImplicitParam(name = "addImages", value = "新增的公告图片", dataType = "file"),
			@ApiImplicitParam(name = "delImages", value = "要删除的公告图片url，多个以逗号隔开", paramType = "query",
					dataType = "string")})
	@PostMapping("/update-notice")
	public String updateNotice(@RequestParam String noticeid, String title, String content,
							   MultipartFile[] addImages, String delImages,
							   HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String userid = userBo.getId();
		CircleNoticeBo noticeBo = circleService.findNoticeById(noticeid);
		if (noticeBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NOTICE_NULL.getIndex(),
					ERRORCODE.CHATROOM_NOTICE_NULL.getReason());
		}
		ChatroomBo chatroomBo = chatroomService.get(noticeBo.getChatroomid());
		if (chatroomBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		if (!chatroomBo.getMaster().contains(userid)) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
					ERRORCODE.CIRCLE_MASTER_NULL.getReason());
		}
		if (StringUtils.isNotEmpty(content)) {
			noticeBo.setContent(content);
		}
		if (StringUtils.isNotEmpty(title)) {
			noticeBo.setTitle(title);
		}
		noticeBo.setUpdateuid(userid);
		noticeBo.setUpdateTime(new Date());
		noticeBo.setType(1);
		LinkedHashSet<String> files = noticeBo.getImages();
		if (addImages != null) {
			for (MultipartFile file : addImages) {
				long time = Calendar.getInstance().getTimeInMillis();
				String fileName = String.format("%s-%d-%s", userid, time, file.getOriginalFilename());
				String path = CommonUtil.upload(file,
						Constant.CHATROOM_PICTURE_PATH, fileName, 0);
				files.add(path);
			}
		}
		if(StringUtils.isNotEmpty(delImages)){
			String[] urls = CommonUtil.getIds(delImages);
			for (String url : urls) {
				files.remove(url);
			}
		}
		noticeBo.setImages(files);
		circleService.updateNotice(noticeBo);
		return Constant.COM_RESP;
	}


	/**
	 * 添加或修改公告
	 */
	@ApiOperation("获取群聊公告详情")
	@PostMapping("/get-notice")
	public String getNotice(@RequestParam String noticeid,
							HttpServletRequest request, HttpServletResponse response) {
		CircleNoticeBo noticeBo = circleService.findNoticeById(noticeid);
		if (noticeBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NOTICE_NULL.getIndex(),
					ERRORCODE.CHATROOM_NOTICE_NULL.getReason());
		}
		ChatroomBo chatroomBo = chatroomService.get(noticeBo.getChatroomid());
		if (chatroomBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		UserBo loginUser = getUserLogin(request);
		LinkedHashSet<String> readUsers = noticeBo.getReadUsers();
		int readNum = readUsers.size();
		int role = 0;
		if (loginUser != null) {
			String userid = loginUser.getId();
			HashSet<String> users = chatroomBo.getUsers();
			asyncController.updateNoticeRead(users,noticeid, loginUser.getId());
			readNum = !readUsers.contains(userid) && users.contains(userid) ? readNum+1 : readNum;
		}
		UserBo userBo = userService.getUser(noticeBo.getCreateuid());
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("ret", 0);
		map.put("noticeid",noticeBo.getId());
		map.put("noticeTitle", noticeBo.getTitle());
		map.put("notice", noticeBo.getContent());
		map.put("noticeTime", noticeBo.getCreateTime());
		map.put("readNum", readNum);
		map.put("image", noticeBo.getImages());
		if (userBo != null) {
			UserBaseVo userBaseVo = new UserBaseVo();
			BeanUtils.copyProperties(userBo, userBaseVo);
			map.put("noticeUser", userBaseVo);
		}
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 添加或修改公告
	 */
	@ApiOperation("群聊公告阅读详情")
	@PostMapping("/notice-read")
	public String getNoticeRead(@RequestParam String noticeid,
								HttpServletRequest request, HttpServletResponse response) {
		CircleNoticeBo noticeBo = circleService.findNoticeById(noticeid);
		if (noticeBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOTICE_NULL.getIndex(),
					ERRORCODE.CIRCLE_NOTICE_NULL.getReason());
		}
		LinkedHashSet<String> readUsers = noticeBo.getReadUsers();
		LinkedHashSet<String> unReadUser = noticeBo.getUnReadUsers();

		List<String> readids = new LinkedList<>(readUsers);
		List<String> unReadids = new LinkedList<>(unReadUser);

		List<UserBaseVo> readVos = new LinkedList<>();
		List<UserBo> readBos = userService.findUserByIds(readids);
		for (UserBo userBo : readBos) {
			UserBaseVo baseVo = new UserBaseVo();
			BeanUtils.copyProperties(userBo, baseVo);
			readVos.add(baseVo);
		}

		List<UserBaseVo> unReadVos = new LinkedList<>();
		List<UserBo> unReadBos = userService.findUserByIds(unReadids);
		for (UserBo userBo : unReadBos) {
			UserBaseVo baseVo = new UserBaseVo();
			BeanUtils.copyProperties(userBo, baseVo);
			unReadVos.add(baseVo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("readNum", readUsers.size());
		map.put("unReadNum", unReadUser.size());
		map.put("readUserVos", readVos);
		map.put("unReadUserVos", unReadVos);
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("删除群聊公告")
	@PostMapping("/delete-notice")
	public String deleteNoticeRead(@RequestParam String noticeid,
								   HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleNoticeBo noticeBo = circleService.findNoticeById(noticeid);
		if (noticeBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOTICE_NULL.getIndex(),
					ERRORCODE.CIRCLE_NOTICE_NULL.getReason());
		}
		String userid = userBo.getId();
		ChatroomBo chatroomBo = chatroomService.get(noticeBo.getChatroomid());
		if (chatroomBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		if (chatroomBo.getMaster().contains(userid)) {
			circleService.deleteNotice(noticeid, userid);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
					ERRORCODE.CIRCLE_MASTER_NULL.getReason());
		}
		return Constant.COM_RESP;
	}

	@ApiOperation("获取圈子公告历史列表, 返回最近10条")
	@PostMapping("/get-notice-list")
	public String getNoticeList(@RequestParam String chatroomid,int page, int limit,
								HttpServletRequest request, HttpServletResponse response) {
		ChatroomBo chatroomBo = chatroomService.get(chatroomid);
		if (chatroomBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		List<CircleNoticeBo> noticeBos = circleService.findCircleNotice(chatroomid,1, page, limit);
		JSONArray array = new JSONArray();
		if (!CommonUtil.isEmpty(noticeBos)) {
			for (CircleNoticeBo noticeBo : noticeBos) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("noticeid",noticeBo.getId());
				jsonObject.put("noticeTitle", noticeBo.getTitle());
				jsonObject.put("notice", noticeBo.getContent());
				jsonObject.put("noticeTime", noticeBo.getCreateTime());
				jsonObject.put("image", noticeBo.getImages());
				jsonObject.put("readNum", noticeBo.getReadUsers().size());
				UserBo userBo = userService.getUser(noticeBo.getCreateuid());
				if (userBo != null) {
					UserBaseVo userBaseVo = new UserBaseVo();
					BeanUtils.copyProperties(userBo, userBaseVo);
					jsonObject.put("noticeUser", userBaseVo);
				}
				array.add(jsonObject);
			}
		}
		map.put("noticeList", array);
		return JSONObject.fromObject(map).toString();
	}


	/**
	 * 添加或修改公告
	 */
	@ApiOperation("获取所有未读公告信息")
	@PostMapping("/unRead-notice-list")
	public String unReadNotices(String chatroomid,int page, int limit, HttpServletRequest request, HttpServletResponse
			response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		List<CircleNoticeBo> noticeBos = circleService.findUnReadNotices(userBo.getId(),
				chatroomid, 1, page, limit);
		JSONArray array = new JSONArray();
		if (!CommonUtil.isEmpty(noticeBos)) {
			for (CircleNoticeBo noticeBo : noticeBos) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("noticeid",noticeBo.getId());
				jsonObject.put("noticeTitle", noticeBo.getTitle());
				jsonObject.put("notice", noticeBo.getContent());
				jsonObject.put("noticeTime", noticeBo.getCreateTime());
				jsonObject.put("image", noticeBo.getImages());
				jsonObject.put("readNum", noticeBo.getReadUsers().size());
				UserBo user = userService.getUser(noticeBo.getCreateuid());
				if (userBo != null) {
					UserBaseVo userBaseVo = new UserBaseVo();
					BeanUtils.copyProperties(user, userBaseVo);
					jsonObject.put("noticeUser", userBaseVo);
				}
				array.add(jsonObject);
			}
		}
		map.put("noticeList", array);
		return JSONObject.fromObject(map).toString();
	}


	/**
	 * 添加或修改公告
	 */
	@ApiOperation("将指定的公告集合设置为已读")
	@ApiImplicitParam(name = "noticeids", value = "公告id,多个以逗号隔开", required = true, paramType = "query",
			dataType = "string")
	@PostMapping("/update-unRead-list")
	public String readNotices(@RequestParam String noticeids, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String[] ids = CommonUtil.getIds(noticeids);
		List<CircleNoticeBo> noticeBos = circleService.findNoticeByIds(ids);
		for (CircleNoticeBo noticeBo : noticeBos) {
			asyncController.updateNoticeRead(noticeBo.getId(), userBo.getId());
		}
		return Constant.COM_RESP;
	}



	/**
	 * 添加或修改公告
	 */
	@ApiOperation("创建临时聊天室")
	@ApiImplicitParam(name = "tempUserid", value = "临时聊天的用户", required = true, paramType = "query",
			dataType = "string")
	@PostMapping("/temp-chatroom")
	public String tempChatroom(@RequestParam String tempUserid, HttpServletRequest request, HttpServletResponse
			response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo tempUser = userService.getUser(tempUserid);
		if (tempUser == null) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_NULL.getIndex(),
					ERRORCODE.USER_NULL.getReason());
		}
		ChatroomBo chatroomBo = chatroomService.selectByUserIdAndFriendid(userBo.getId(), tempUserid);
		if (chatroomBo == null) {
			chatroomBo = chatroomService.selectByUserIdAndFriendid(tempUserid, userBo.getId());
			if (chatroomBo == null) {
				chatroomBo = new ChatroomBo();
				chatroomBo.setType(5);
				chatroomBo.setName(tempUser.getUserName());
				chatroomBo.setUserid(userBo.getId());
				chatroomBo.setFriendid(tempUserid);
				chatroomService.insert(chatroomBo);
				String res = IMUtil.subscribe(0 ,chatroomBo.getId(), userBo.getId(), tempUserid);
				if (!res.equals(IMUtil.FINISH)) {
					chatroomService.remove(chatroomBo.getId());
					return res;
				}
				HashSet<String> userChatrooms = userBo.getChatrooms();
				HashSet<String> friendChatrooms = tempUser.getChatrooms();
				userChatrooms.add(chatroomBo.getId());
				friendChatrooms.add(chatroomBo.getId());
				userService.updateChatrooms(userBo);
				userService.updateChatrooms(tempUser);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("channelId", chatroomBo.getId());
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("删除临时聊天室")
	@ApiImplicitParam(name = "charroomid", value = "临时聊天室id", required = true, paramType = "query",
			dataType = "string")
	@PostMapping("/delete-temp-chatroom")
	public String deleteTempChatroom(@RequestParam String charroomid, HttpServletRequest request, HttpServletResponse
			response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		ChatroomBo chatroomBo = chatroomService.get(charroomid);
		if (chatroomBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_NULL.getIndex(),
					ERRORCODE.CHATROOM_NULL.getReason());
		}
		//4 和 5表示临时聊天室
		if (chatroomBo.getType() == 4 || chatroomBo.getType() == 5) {
			String result = IMUtil.disolveRoom(charroomid);
			if (!result.equals(IMUtil.FINISH) && !result.contains("not found")) {
				return result;
			}
			chatroomService.delete(charroomid);
			//删除好友互相设置信息user
		}
		return Constant.COM_RESP;
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
				asyncController.updateUserChatroom(friendid, chatroomBo.getId(), false);
			}
			chatroomService.delete(chatroomBo.getId());
		}
	}



}
