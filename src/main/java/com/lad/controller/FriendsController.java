package com.lad.controller;

import com.lad.bo.*;
import com.lad.service.*;
import com.lad.util.*;
import com.lad.vo.FriendDisVo;
import com.lad.vo.FriendsVo;
import com.lad.vo.UserBaseVo;
import com.lad.vo.UserVoFriends;
import com.pushd.ImAssistant;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

@EnableAsync
@RestController
@RequestMapping("friends")
public class FriendsController extends BaseContorller {

	@Autowired
	private IFriendsService friendsService;
	@Autowired
	private IChatroomService chatroomService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ILocationService locationService;

	@Autowired
	private ITagService tagService;

	@Autowired
	private IMessageService messageService;

	private String pushTitle = "好友通知";

	@RequestMapping(value = "/apply", method = {RequestMethod.GET, RequestMethod.POST})
	public String apply(String friendid, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		UserBo friendBo = userService.getUser(friendid);
		if (friendBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		FriendsBo temp = friendsService.getFriendByIdAndVisitorId(
				userBo.getId(), friendid);
		if (temp != null) {
			if (temp.getApply() == 1) {
				return CommonUtil.toErrorResult(
						ERRORCODE.FRIEND_EXIST.getIndex(),
						ERRORCODE.FRIEND_EXIST.getReason());
			}
			return CommonUtil.toErrorResult(
					ERRORCODE.FRIEND_APPLY_EXIST.getIndex(),
					ERRORCODE.FRIEND_APPLY_EXIST.getReason());
		}
		FriendsBo friendsBo = new FriendsBo();
		friendsBo.setUserid(userBo.getId());
		friendsBo.setFriendid(friendid);
		friendsBo.setUsername(friendBo.getUserName());
		friendsBo.setApply(0);
		friendsService.insert(friendsBo);
		String path = "/friends/apply-list.do";
		JPushUtil.push(pushTitle,userBo.getUserName() + JPushUtil.APPLY, path, friendid);
		addMessage(messageService, path, userBo.getUserName() + JPushUtil.APPLY,
				pushTitle, friendid);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 *
	 * @param id  好友关系中的ID，不是userid或者朋友ID
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("同意好友请求")
	@ApiImplicitParam(name = "id", value = "好友关系中的ID，不是userid或者朋友ID", required = true, paramType = "query", dataType =
			"string")
	@RequestMapping(value = "/agree",method = {RequestMethod.GET, RequestMethod.POST})
	public String agree(String id, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		FriendsBo friendsBo = friendsService.get(id);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_ERROR.getIndex(),
					ERRORCODE.FRIEND_ERROR.getReason());
		}
		String userid = friendsBo.getFriendid();
		String friendid = friendsBo.getUserid();
		UserBo friendBo = userService.getUser(friendid);
		if (friendBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		ChatroomBo chatroomBo = chatroomService.selectByUserIdAndFriendid(
				userid, friendid);
		boolean isNew = false;
		UserBo user = userService.getUser(userid);
		UserBo friend = userService.getUser(friendid);
		//在聊天室中，用户ID和好友ID是一对，所以互换ID能够查询到，都算同一个channel
		if (null == chatroomBo) {
			chatroomBo = chatroomService.selectByUserIdAndFriendid(
					friendid, userid);
			if (null == chatroomBo){
				isNew = true;
			}
		}
		chatroomBo = savekUserAndFriendChatroom(user, friend, chatroomBo);
		//是不是创建聊天室
		int type = isNew ? 0 : 1;
		String res = IMUtil.subscribe(type,chatroomBo.getId(), userid, friendid);
		if (!res.equals(IMUtil.FINISH)) {
			if (res.contains("channelId is not exists")) {
				res = IMUtil.subscribe(0,chatroomBo.getId(), userid, friendid);
			}
			if (!res.equals(IMUtil.FINISH)) {
				return res;
			}
		}
		FriendsBo temp = friendsService.getFriendByIdAndVisitorId(
				userid, friendid);
		if (temp != null) {
			friendsService.updateApply(temp.getId(), 1, chatroomBo.getId());
		} else {
			//更新同意人的好友信息
			FriendsBo friendsBo2 = new FriendsBo();
			friendsBo2.setUserid(userid);
			friendsBo2.setFriendid(friendid);
			friendsBo2.setUsername(friendBo.getUserName());
			friendsBo2.setApply(1);
			friendsBo2.setChatroomid(chatroomBo.getId());
			friendsService.insert(friendsBo2);
		}
		//更新申请人的好友信息
		friendsService.updateApply(id, 1, chatroomBo.getId());
		
		JPushUtil.pushTo(userBo.getUserName() + JPushUtil.AGREE_APPLY_FRIEND,
				friendid);
		addMessage(messageService, "", userBo.getUserName() + JPushUtil.AGREE_APPLY_FRIEND,
				pushTitle, friendid);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("channelId", chatroomBo.getId());
		return JSONObject.fromObject(map).toString();
	}


	/**
	 * 判断并保存用户与好友之间的chatroom关系
	 * @param user 本人
	 * @param friend 好友
	 * @param chatroomBo 聊天室
	 * @return
	 */
	private ChatroomBo savekUserAndFriendChatroom(UserBo user, UserBo friend, ChatroomBo chatroomBo){
		if (null == chatroomBo) {
			chatroomBo = new ChatroomBo();
			chatroomBo.setType(1);
			chatroomBo.setName(friend.getUserName());
			chatroomBo.setUserid(user.getId());
			chatroomBo.setFriendid(friend.getId());
			chatroomService.insert(chatroomBo);
		} else {
			//有可能之前创建了临时的聊天室
			if(chatroomBo.getType() != Constant.ROOM_SINGLE) {
			  	Map<String, Object> params = new HashMap<>();
			  	params.put("type", 1);
			  	chatroomService.updateRoomByParams(chatroomBo.getId(), params);
			}
		}
		HashSet<String> userChatrooms = user.getChatrooms();
		HashSet<String> friendChatrooms = friend.getChatrooms();
		userChatrooms.add(chatroomBo.getId());
		friendChatrooms.add(chatroomBo.getId());
		user.setChatrooms(userChatrooms);
		friend.setChatrooms(friendChatrooms);
		userService.updateChatrooms(user);
		userService.updateChatrooms(friend);
		return chatroomBo;
	}


	@RequestMapping(value = "/refuse", method = {RequestMethod.GET, RequestMethod.POST})
	public String refuse(String id, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		if (StringUtils.isEmpty(id)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		FriendsBo friendsBo = friendsService.get(id);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		friendsService.updateApply(id, -1, "");
		JPushUtil.pushTo(userBo.getUserName() + JPushUtil.REFUSE_APPLY_FRIEND,
				friendsBo.getUserid());
		addMessage(messageService, "", userBo.getUserName() + JPushUtil.REFUSE_APPLY_FRIEND,
				pushTitle, friendsBo.getUserid());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping(value = "/apply-list", method = {RequestMethod.GET, RequestMethod.POST})
	public String applyList(HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<FriendsBo> friendsBoList = friendsService
				.getApplyFriendByuserid(userBo.getId());
		List<UserVoFriends> userVoList = new LinkedList<UserVoFriends>();
		for (FriendsBo friendsBo : friendsBoList) {
			UserBo userBoTemp = userService.getUser(friendsBo.getUserid());
			if (null == userBoTemp) {
				return CommonUtil.toErrorResult(
						ERRORCODE.FRIEND_DATA_ERROR.getIndex(),
						ERRORCODE.FRIEND_DATA_ERROR.getReason());
			}
			UserVoFriends user = new UserVoFriends();
			BeanUtils.copyProperties(userBoTemp, user);
			user.setApply(friendsBo.getApply());
			user.setFriendsTableId(friendsBo.getId());
			userVoList.add(user);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userVoList", userVoList);
		return JSONObject.fromObject(map).toString();
	}



	@PostMapping("/set-VIP")
	public String setVIP(String friendid, Integer VIP,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		if (StringUtils.isEmpty(friendid)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		if (null == VIP) {
			return CommonUtil.toErrorResult(
					ERRORCODE.FRIEND_VIP_NULL.getIndex(),
					ERRORCODE.FRIEND_VIP_NULL.getReason());
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(
				userBo.getId(), friendid);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		friendsBo.setVIP(VIP);
		friendsService.updateVIP(friendsBo.getUserid(),
				friendsBo.getFriendid(), VIP);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@PostMapping("/set-black")
	public String setBlack(String friendid, Integer black,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		if (StringUtils.isEmpty(friendid)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		if (null == black) {
			return CommonUtil.toErrorResult(
					ERRORCODE.FRIEND_BLACK_NULL.getIndex(),
					ERRORCODE.FRIEND_BLACK_NULL.getReason());
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(
				userBo.getId(), friendid);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		friendsBo.setBlack(black);
		friendsService.updateBlack(friendsBo.getUserid(),
				friendsBo.getFriendid(), black);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@PostMapping("/set-backname")
	public String setBackName(String friendid, String backname,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		if (StringUtils.isEmpty(friendid)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		if (StringUtils.isEmpty(backname)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.FRIEND_BACKNAME_NULL.getIndex(),
					ERRORCODE.FRIEND_BACKNAME_NULL.getReason());
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(
				userBo.getId(), friendid);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		friendsService.updateBackname(userBo.getId(),
				friendsBo.getFriendid(), backname);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@PostMapping("/set-phone")
	public String setPhone(String friendid, String phone,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		if (StringUtils.isEmpty(friendid)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		if (null == phone) {
			return CommonUtil.toErrorResult(
					ERRORCODE.FRIEND_PHONE_NULL.getIndex(),
					ERRORCODE.FRIEND_PHONE_NULL.getReason());
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(
				userBo.getId(), friendid);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		HashSet<String> phones = friendsBo.getPhone();
		String[] phoneArr = phone.split(",");
		for (String str : phoneArr) {
			phones.add(str);
		}
		friendsService.updatePhone(friendsBo.getUserid(),
				friendsBo.getFriendid(), phones);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@PostMapping("/set-description")
	public String setDescription(String friendid, String description,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		if (StringUtils.isEmpty(friendid)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		if (null == description) {
			return CommonUtil.toErrorResult(
					ERRORCODE.FRIEND_PHONE_NULL.getIndex(),
					ERRORCODE.FRIEND_PHONE_NULL.getReason());
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(
				userBo.getId(), friendid);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		friendsBo.setDescription(description);
		friendsService.updateDescription(friendsBo.getUserid(),
				friendsBo.getFriendid(), description);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping(value = "/get-friends",method = {RequestMethod.GET, RequestMethod.POST})
	public String getFriends(HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		String userid = userBo.getId();
		List<FriendsBo> list = friendsService.getFriendByUserid(userid);
		List<FriendsVo> voList = new LinkedList<FriendsVo>();
		for (FriendsBo friendsBo : list) {
			FriendsVo vo = new FriendsVo();
			ChatroomBo chatroomBo = chatroomService.selectByUserIdAndFriendid(
					userBo.getId(), friendsBo.getFriendid());
			if (chatroomBo == null) {
				chatroomBo = chatroomService.selectByUserIdAndFriendid(
						 friendsBo.getFriendid(), userBo.getId());
				if (chatroomBo == null) {
					UserBo friend = userService.getUser(friendsBo.getFriendid());
					chatroomBo = savekUserAndFriendChatroom(userBo, friend, chatroomBo);
					//首次创建聊天室，需要输入名称
					String res = IMUtil.subscribe(0, chatroomBo.getId(), userid, friend.getId());
					if (!res.equals(IMUtil.FINISH)) {
						return res;
					}
				}
			}

			BeanUtils.copyProperties(friendsBo, vo);
			String friendid = friendsBo.getFriendid();
			UserBo friend = userService.getUser(friendid);
			if (friend == null) {
				continue;
			}
			List<TagBo> tagBos = tagService.getTagBoListByUseridAndFrinedid(userid, friendid);
			List<String> tagList = new ArrayList<>();
			for (TagBo tagBo : tagBos) {
				tagList.add(tagBo.getName());
			}
			vo.setSex(friend.getSex());
			vo.setTag(tagList);
			vo.setUsername(friend.getUserName());
			vo.setPicture(friend.getHeadPictureName());
			vo.setChannelId(chatroomBo.getId());
			if (StringUtils.isEmpty(friendsBo.getBackname())) {
				vo.setBackname(friend.getUserName());
			} else {
				vo.setBackname(friendsBo.getBackname());
			}
			voList.add(vo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("tag", voList);
		return JSONObject.fromObject(map).toString();
	}



	@PostMapping("/delete")
	public String delete(String friendid, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		if (StringUtils.isEmpty(friendid)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		ChatroomBo chatroomBo = chatroomService.selectByUserIdAndFriendid(
				userBo.getId(), friendid);
		if (null != chatroomBo) {
			String result = IMUtil.disolveRoom(chatroomBo.getId());
			if (!result.equals(IMUtil.FINISH) && !result.contains("not found")) {
				return result;
			}
			//删除好友互相设置信息user
			chatroomService.deleteChatroomUser(userBo.getId(),chatroomBo.getId());
			chatroomService.deleteChatroomUser(friendid,chatroomBo.getId());
			chatroomService.delete(chatroomBo.getId());
		}
		chatroomBo = chatroomService.selectByUserIdAndFriendid(
				friendid,userBo.getId());
		if (chatroomBo != null) {
			String result = IMUtil.disolveRoom(chatroomBo.getId());
			if (!result.equals(IMUtil.FINISH) && !result.contains("not found")) {
				return result;
			}
			chatroomService.delete(chatroomBo.getId());
			//删除好友互相设置信息user
			chatroomService.deleteChatroomUser(userBo.getId(),chatroomBo.getId());
			chatroomService.deleteChatroomUser(friendid,chatroomBo.getId());
		}
		FriendsBo temp = friendsService.getFriendByIdAndVisitorId(
				userBo.getId(), friendid);
		if (temp != null) {
			friendsService.delete(userBo.getId(), friendid);
		}
		//在添加好友的会互换id保存
		temp = friendsService.getFriendByIdAndVisitorId(friendid,
				userBo.getId());
		if (temp != null ) {
			friendsService.delete(friendid, userBo.getId());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@PostMapping("/multi-insert")
	public String multiInsert(String friendids, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		if (StringUtils.isEmpty(friendids)) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		if (!friendids.contains(",")) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		ImAssistant assistent = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
		if (assistent == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
		}
		String[] idsList = friendids.split(",");
		LinkedHashSet<String> userSet = new LinkedHashSet<String>();
		for (String id : idsList) {
			FriendsBo temp = friendsService.getFriendByIdAndVisitorIdAgree(
					userBo.getId(), id);
			if (temp == null) {
				if (id.equals(userBo.getId())) {
					userSet.add(id);
					continue;
				}
				return CommonUtil.toErrorResult(
						ERRORCODE.FRIEND_NULL.getIndex(),
						ERRORCODE.FRIEND_NULL.getReason());
			}
			userSet.add(id);
		}

		/**
		 * 因为没有看懂上面for循环中，为什么有可能会包含 userBo.getId()
		 * 所以为了保险起见，remove掉，再add进去
		 */
		userSet.remove(userBo.getId());
		String[] tt = new String[userSet.size()];
		int i=0;
		for(String uu: userSet){
			tt[i++] = uu;
		}
		Object[] nameAndIds = ChatRoomUtil.getUserNamesAndIds(userService, tt, null);

		userSet.add(userBo.getId());
		ChatroomBo chatroomBo = new ChatroomBo();
		chatroomBo.setType(2);
		chatroomBo.setUsers(userSet);
		chatroomBo.setMaster(userBo.getId());
		chatroomBo.setCreateuid(userBo.getId());

		// 生成群聊名称
		String newChatRoomName = ChatRoomUtil.generateChatRoomName(userService, userSet, chatroomBo.getId(), null);
		if(newChatRoomName != null){
			chatroomBo.setName(newChatRoomName);
		}else{
			/**
			 *  出现这种情况的原因： 请看ChatRoomUtil.generateChatRoomName 这个方法的内部处理。
			 *  如果前端传的userIds参数没问题，那么不会出现这种群聊名称
			 *
			 *  如果出现了这种情况，为了不至于没有群聊名称，默认为"群聊"。这样处理不会造成糟糕bug，特别是给前端，虽然让用户感到一点困惑
			 */
			chatroomBo.setName("群聊");
		}

		chatroomService.insert(chatroomBo);
		for (String id : userSet) {
			UserBo user = userService.getUser(id);
			HashSet<String> chatroomsSet = user.getChatrooms();
			chatroomsSet.add(chatroomBo.getId());
			user.setChatrooms(chatroomsSet);
			userService.updateChatrooms(user);
			addChatroomUser(user, chatroomBo.getId());
		}
		String res = IMUtil.subscribe(0, chatroomBo.getId(), idsList);
		if(!IMUtil.FINISH.equals(res)){
			return res;
		}
		JPushUtil.pushTo(userBo.getUserName() + JPushUtil.MULTI_INSERT, idsList);
		addMessage(messageService, "", userBo.getUserName() + JPushUtil.MULTI_INSERT,
				pushTitle, userBo.getId(), idsList);

		// 某人被邀请加入群聊通知
		if(nameAndIds[0] != null){
			JSONObject json = new JSONObject();
			json.put("masterId", userBo.getId());
			json.put("masterName", userBo.getUserName());
			json.put("hitIds", nameAndIds[1]);
			json.put("hitNames", nameAndIds[0]);
			json.put("otherIds", new ArrayList<String>());
			json.put("otherNames", new ArrayList<String>());

			String res2 = IMUtil.notifyInChatRoom(Constant.SOME_ONE_BE_INVITED_OT_CHAT_ROOM,
					chatroomBo.getId(),
					json.toString());

			//		if(!IMUtil.FINISH.equals(res2)){
			//			logger.error("failed notifyInChatRoom Constant.SOME_ONE_BE_INVITED_OT_CHAT_ROOM, %s",res2);
			//		}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("channelId", chatroomBo.getId());
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 添加聊天室用户
	 * @param chatroomid
	 */
	@Async
	private void addChatroomUser(UserBo userBo, String chatroomid){
		ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userBo.getId(), chatroomid);
		if (chatroomUserBo == null) {
			chatroomUserBo = new ChatroomUserBo();
			chatroomUserBo.setChatroomid(chatroomid);
			chatroomUserBo.setUserid(userBo.getId());
			chatroomUserBo.setUsername(userBo.getUserName());
			chatroomUserBo.setShowNick(false);
			chatroomUserBo.setDisturb(false);
			chatroomService.insertUser(chatroomUserBo);
		} else {
			chatroomService.updateUserNickname(chatroomUserBo.getId(), "");
		}
	}


	@PostMapping("/multi-out")
	public String multiOut(String chatroomid, HttpServletRequest request,
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
		String userid = userBo.getId();
		LinkedHashSet<String> userids = chatroomBo.getUsers();
		if (userid.equals(chatroomBo.getMaster())) {
			  if (userids.size() > 2) {
				  return CommonUtil.toErrorResult(ERRORCODE.CHATROOM_MASTER_QUIT.getIndex(),
						  ERRORCODE.CHATROOM_MASTER_QUIT.getReason());
			  }
		}
		userBo = userService.getUser(userid);
		HashSet<String> chatrooms = userBo.getChatrooms();
		String res = IMUtil.unSubscribe(chatroomid,  userid);
		if (!res.equals(IMUtil.FINISH)) {
			return res;
		}
		if (chatrooms.contains(chatroomid)) {
			chatrooms.remove(chatroomid);
		}
		userBo.setChatrooms(chatrooms);
		userService.updateChatrooms(userBo);
		if (userids.contains(userid)) {
			userids.remove(userid);
		}
		if (userids.size() <= 2) {
			chatroomService.delete(chatroomid);
		} else {
			chatroomBo.setUsers(userids);
			chatroomService.updateUsers(chatroomBo);
		}
		return Constant.COM_RESP;
	}



	@PostMapping("/sign-users")
	public String signUsers(String[] phones, HttpServletRequest request, HttpServletResponse response) {
		List<UserBaseVo> userBaseVos = new ArrayList<>();
		if(null != phones) {
			for (String phone : phones) {
				UserBo user = userService.getUserByPhone(phone);
				if(null != user) {
					UserBaseVo baseVo = new UserBaseVo();
					BeanUtils.copyProperties(user, baseVo);
					userBaseVos.add(baseVo);
				} 
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userVos", userBaseVos);
		return JSONObject.fromObject(map).toString();
	}


	@PostMapping("/sign-user-time")
	public String signUsers(String[] phones,String timestamp, HttpServletRequest request, HttpServletResponse
			response) {
		List<UserBaseVo> userBaseVos = new ArrayList<>();
		List<String> phoneList = new ArrayList<>();
		String timeStr = "";
		if(null != phones) {
			Collections.addAll(phoneList, phones);
			try {
				Date times = CommonUtil.getDate(timestamp);
				List<UserBo> userBos = userService.getUserByPhoneAndTime(phoneList, times);
				if (!CommonUtil.isEmpty(userBos)) {
					UserBo first = userBos.get(0);
					timeStr = CommonUtil.getDateStr(first.getCreateTime(),"yyyy-MM-dd HH:mm:ss");
					for (UserBo userBo : userBos) {
						UserBaseVo baseVo = new UserBaseVo();
						BeanUtils.copyProperties(userBo, baseVo);
						userBaseVos.add(baseVo);
					}
				}
			} catch (ParseException e){
				return CommonUtil.toErrorResult(ERRORCODE.FORMAT_ERROR.getIndex(),
						ERRORCODE.FORMAT_ERROR.getReason());
			}
		}
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("ret", 0);
		map.put("timestamp", StringUtils.isNotEmpty(timeStr) ? timeStr : timestamp);
		map.put("userVos", userBaseVos);
		return JSONObject.fromObject(map).toString();
	}



	@PostMapping("/get-friends-time")
	public String getFriendsTime( String timestamp, HttpServletRequest request,HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		String userid = userBo.getId();
		List<FriendsVo> voList = new LinkedList<>();
		String timeStr = "";
		try {
			Date times = CommonUtil.getDate(timestamp);
			List<FriendsBo> list = friendsService.getFriendByUserid(userid, times);
			if (!CommonUtil.isEmpty(list)) {
				FriendsBo first = list.get(0);
				Date time = first.getUpdateTime() == null ? first.getCreateTime() : first.getUpdateTime();
				timeStr = CommonUtil.getDateStr(time,"yyyy-MM-dd HH:mm:ss");
			}
			for (FriendsBo friendsBo : list) {
				FriendsVo vo = new FriendsVo();
				BeanUtils.copyProperties(friendsBo, vo);
				String friendid = friendsBo.getFriendid();
				List<TagBo> tagBos = tagService.getTagBoListByUseridAndFrinedid(userid, friendid);
				List<String> tagList = new ArrayList<>();
				for (TagBo tagBo : tagBos) {
					tagList.add(tagBo.getName());
				}
				vo.setPicture(friendsBo.getFriendHeadPic());
				vo.setTag(tagList);
				if (StringUtils.isEmpty(friendsBo.getBackname())) {
					UserBo friend = userService.getUser(friendid);
					vo.setBackname(friend.getUserName());
					vo.setUsername(friend.getUserName());
				} else {
					vo.setBackname(friendsBo.getBackname());
					vo.setUsername(friendsBo.getUsername());
				}
				if (StringUtils.isEmpty(friendsBo.getChatroomid())) {
					//如果当初没有创建成功
					ChatroomBo chatroomBo = chatroomService.selectByUserIdAndFriendid(
							userBo.getId(), friendsBo.getFriendid());
					if (chatroomBo == null) {
						chatroomBo = chatroomService.selectByUserIdAndFriendid(
								friendsBo.getFriendid(), userBo.getId());
						if (chatroomBo == null) {
							UserBo friend = userService.getUser(friendsBo.getFriendid());
							chatroomBo = savekUserAndFriendChatroom(userBo, friend, chatroomBo);
							//首次创建聊天室，需要输入名称
							String res = IMUtil.subscribe(0, chatroomBo.getId(), userid, friend.getId());
							if (!res.equals(IMUtil.FINISH)) {
								return res;
							}
						}
					}
					friendsService.updateApply(friendsBo.getId(), Constant.ADD_AGREE, chatroomBo.getId());
					vo.setChannelId(friendsBo.getChatroomid());
				} else {
					vo.setChannelId(friendsBo.getChatroomid());
				}
				voList.add(vo);
			}
		} catch (ParseException e){
			return CommonUtil.toErrorResult(ERRORCODE.FORMAT_ERROR.getIndex(),
					ERRORCODE.FORMAT_ERROR.getReason());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("timestamp", StringUtils.isNotEmpty(timeStr) ? timeStr : timestamp);
		map.put("tag", voList);
		return JSONObject.fromObject(map).toString();
	}



	@PostMapping("/near-friends")
	public String nearFriends(double px, double py, HttpServletRequest request,
								 HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo ==null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<String> friendids = new LinkedList<>();
		Map<String, Double> disMap = new HashMap<>();
		Point point = new Point(px, py);
		GeoResults<LocationBo> results = locationService.findUserNear(point, 10000);
		for (GeoResult<LocationBo> result : results) {
			String userid = result.getContent().getUserid();
			friendids.add(userid);
			disMap.put(userid, result.getDistance().getValue());
		}
		List<FriendDisVo> voList = new LinkedList<>();
		if (!friendids.isEmpty()) {
			DecimalFormat df = new DecimalFormat("###.00");
			List<FriendsBo> friendsBos = friendsService.getFriendByInList(userBo.getId(), friendids);
			for (FriendsBo friendsBo : friendsBos) {
				FriendDisVo vo = new FriendDisVo();
				BeanUtils.copyProperties(friendsBo, vo);
				String friendid = friendsBo.getFriendid();
				vo.setPicture(friendsBo.getFriendHeadPic());
				if (StringUtils.isEmpty(friendsBo.getBackname())) {
					UserBo friend = userService.getUser(friendid);
					vo.setBackname(friend.getUserName());
					vo.setUsername(friend.getUserName());
				}
				vo.setChannelId(friendsBo.getChatroomid());
				double dis = Double.parseDouble(df.format(disMap.get(friendid)));
				vo.setDistance(dis);
				voList.add(vo);
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("friendVos", voList);
		return JSONObject.fromObject(map).toString();
	}



	@ApiOperation("关联父母或子女账号")
	@ApiImplicitParams({ @ApiImplicitParam(name = "friendid", value = "需要关联用户的id", required = true,
			paramType = "query",  dataType = "string"),
			@ApiImplicitParam(name = "isParent", value = "申请关联角色，true 我是父母，false 我是子女", paramType = "query",
					dataType = "boolean")})
	@PostMapping("/relate-account")
	public String relateAccount(String friendid, boolean isParent, HttpServletRequest request,
							  HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo ==null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userBo.getId(), friendid);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NOT_EXIST.getIndex(),
					ERRORCODE.FRIEND_NOT_EXIST.getReason());
		}
		FriendsBo friend = friendsService.getFriendByIdAndVisitorIdAgree(friendid, userBo.getId());
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NOT_HAS_YOU.getIndex(),
					ERRORCODE.FRIEND_NOT_HAS_YOU.getReason());
		}
		if (friendsBo.getRelateStatus() == 1) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_RELATE_APPLY.getIndex(),
					ERRORCODE.ACCOUNT_RELATE_APPLY.getReason());
		}
		if (friendsBo.getRelateStatus() == 1 || friendsBo.getRelateStatus() == 2) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_RELATE_APPLY.getIndex(),
					ERRORCODE.ACCOUNT_RELATE_APPLY.getReason());
		}
		if (friendsBo.getRelateStatus() == 3 ) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_RELATE_EXIST.getIndex(),
					ERRORCODE.ACCOUNT_RELATE_EXIST.getReason());
		}
		//更新自己好友列表信息
		friendsService.updateRelateStatus(friendsBo.getId(), 1, isParent);
		friendsService.updateRelateStatus(friend.getId(), 2, !isParent);
		String path = String.format("/friends/all-apply-list.do?page=%d&limit=%d", 1, 10);
		String name = StringUtils.isEmpty(friend.getBackname()) ? userBo.getUserName() :friend.getBackname();
		String message = name + "申请与您进行账号关联，快去看看吧！";
		JPushUtil.push(pushTitle, message, path, friendid);
		addMessage(messageService, path, message, pushTitle, friendid);
		return Constant.COM_RESP;
	}


	@ApiOperation("同意或拒绝账号关联")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "当前好友关系的id", required = true,
			paramType = "query",  dataType = "string"),
			@ApiImplicitParam(name = "isAgree", value = "是否同意", paramType = "query",
					dataType = "boolean")})
	@PostMapping("/operate-relate-account")
	public String updateRelateAccount(String id, boolean isAgree, HttpServletRequest request,
								HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo ==null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		FriendsBo friendsBo = friendsService.get(id);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NOT_EXIST.getIndex(),
					ERRORCODE.FRIEND_NOT_EXIST.getReason());
		}
		FriendsBo friend = friendsService.getFriendByIdAndVisitorIdAgree(friendsBo.getFriendid(), userBo.getId());
		if (friend == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NOT_HAS_YOU.getIndex(),
					ERRORCODE.FRIEND_NOT_HAS_YOU.getReason());
		}
		if (isAgree) {
			friendsService.updateRelateStatus(id, 3, friendsBo.isParent());
			friendsService.updateRelateStatus(friend.getId(), 3, !friendsBo.isParent());

			String path = String.format("/friends/all-apply-list.do?page=%d&limit=%d", 1, 10);
			String role = friendsBo.isParent() ? "子女" : "父母";
			String message = "您已成功关联" + role + "，快去看看吧！";
			JPushUtil.push(pushTitle, message, path, friendsBo.getFriendid());
			addMessage(messageService, path, message, pushTitle, friendsBo.getFriendid());
		} else {
			friendsService.updateRelateStatus(id, -1, friendsBo.isParent());
			friendsService.updateRelateStatus(friend.getId(), -1, !friendsBo.isParent());
		}
		return Constant.COM_RESP;
	}

	@ApiOperation("所有好友请求信息包括账号管理列表分页接口")
	@PostMapping("all-apply-list")
	public String applyList(int page, int limit, HttpServletRequest request,
							HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo ==null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<FriendsBo> friendsBoList = friendsService.findAllApplyList(userBo.getId(), page, limit);
		List<FriendsVo> userVoList = new LinkedList<FriendsVo>();
		for (FriendsBo friendsBo : friendsBoList) {
			//当前的用户属于被申请的角色，所以查找的好友是申请的用户
			String friendid = friendsBo.getUserid();
			UserBo friend = userService.getUser(friendid);
			if (friend == null) {
				continue;
			}
			FriendsVo user = new FriendsVo();
			BeanUtils.copyProperties(friendsBo, user);
			List<TagBo> tagBos = tagService.getTagBoListByUseridAndFrinedid(userBo.getId(), friendid);
			List<String> tagList = new ArrayList<>();
			for (TagBo tagBo : tagBos) {
				tagList.add(tagBo.getName());
			}
			user.setSex(friend.getSex());
			user.setTag(tagList);
			user.setUsername(friend.getUserName());
			user.setPicture(friend.getHeadPictureName());
			user.setChannelId(friendsBo.getChatroomid());
			if (!StringUtils.isEmpty(friendsBo.getBackname())) {
				user.setBackname(friendsBo.getBackname());
			}
			userVoList.add(user);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userVoList", userVoList);
		return JSONObject.fromObject(map).toString();
	}



	@ApiOperation("已关联的账号列表")
	@PostMapping("has-relate-account")
	public String haRelateAccount(int page, int limit, HttpServletRequest request,
							HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo ==null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<FriendsBo> friendsBoList = friendsService.findByStatus(userBo.getId(),
				Constant.ADD_AGREE, 3, page, limit);
		List<UserVoFriends> userVoList = new LinkedList<UserVoFriends>();
		for (FriendsBo friendsBo : friendsBoList) {
			UserBo userBoTemp = userService.getUser(friendsBo.getFriendid());
			if (null == userBoTemp) {
				continue;
			}
			UserVoFriends user = new UserVoFriends();
			BeanUtils.copyProperties(userBoTemp, user);
			user.setFriendsTableId(friendsBo.getId());
			user.setApply(friendsBo.getApply());
			user.setBackname(friendsBo.getBackname());
			user.setRelateStatus(friendsBo.getRelateStatus());
			user.setParent(friendsBo.isParent());
			userVoList.add(user);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userVoList", userVoList);
		return JSONObject.fromObject(map).toString();
	}


	@ApiOperation("取消账号关联")
	@ApiImplicitParam(name = "id", value = "当前好友关系的id", required = true,
			paramType = "query",  dataType = "string")
	@PostMapping("cancel-relate-account")
	public String cancelRelateAccount(String id, HttpServletRequest request,
								  HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo ==null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		FriendsBo friendsBo = friendsService.get(id);
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NOT_EXIST.getIndex(),
					ERRORCODE.FRIEND_NOT_EXIST.getReason());
		}
		FriendsBo friend = friendsService.getFriendByIdAndVisitorIdAgree(friendsBo.getFriendid(), friendsBo.getUserid());
		if (friendsBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NOT_HAS_YOU.getIndex(),
					ERRORCODE.FRIEND_NOT_HAS_YOU.getReason());
		}
		friendsService.updateRelateStatus(id, 0, false);
		friendsService.updateRelateStatus(friend.getId(), 0, false);

		String path = String.format("/friends/all-apply-list.do?page=%d&limit=%d", 1, 10);
		String name = StringUtils.isEmpty(friend.getBackname()) ? userBo.getUserName() :friend.getBackname();
		String message =  name + "已取消与您的账号关联，快去看看吧！";
		JPushUtil.push(pushTitle, message, path, friendsBo.getFriendid());
		addMessage(messageService, path, message, pushTitle, friendsBo.getFriendid());
		return Constant.COM_RESP;
	}
}
