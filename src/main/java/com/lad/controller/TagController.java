package com.lad.controller;

import com.lad.bo.FriendsBo;
import com.lad.bo.TagBo;
import com.lad.bo.UserBo;
import com.lad.service.IFriendsService;
import com.lad.service.ITagService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.MyException;
import com.lad.vo.TagVo;
import com.lad.vo.UserBaseVo;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@EnableAsync
@Controller
@RequestMapping("tag")
public class TagController extends BaseContorller {

	private static Logger logger = LogManager.getLogger(TagController.class);

	@Autowired
	private ITagService tagService;
	@Autowired
	private IFriendsService friendsService;
	@Autowired
	private IUserService userService;

	@RequestMapping("/set-tags")
	@ResponseBody
	public String setTag(String tagNames, String friendid,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
			List<String> tags = Arrays.asList(CommonUtil.getIds(tagNames));
			removeTag(userBo,friendid, tags);
			logger.info("friendid : {},  tag -list: {}",friendid, tagNames);
			for (String tag : tags) {
				TagBo tagBo = tagService.getBynameAndUserid(tag, userBo.getId());
				if (tagBo != null) {
					LinkedHashSet<String> firendsSet = tagBo.getFriendsIds();
					if (!firendsSet.contains(friendid)) {
						firendsSet.add(friendid);
						tagService.updateTagFriends(tagBo.getId(), firendsSet);
					}
				} else {
					tagBo = new TagBo();
					LinkedHashSet<String> firendsSet = tagBo.getFriendsIds();
					firendsSet.add(friendid);
					tagBo.setFriendsIds(firendsSet);
					tagBo.setUserid(userBo.getId());
					tagBo.setName(tag);
					tagService.insert(tagBo);
				}
			}
		} catch (MyException e) {
			return e.getMessage();
		}
		return Constant.COM_RESP;
	}

	@Async
	private void removeTag(UserBo userBo, String friendid, List<String> tags){
		List<TagBo> tagBoList = tagService.getTagBoListByUseridAndFrinedid(
				userBo.getId(), friendid);
		if (tagBoList != null) {
			for (TagBo tagBo : tagBoList) {
				if (!tags.contains(tagBo.getName())){
					LinkedHashSet<String> firendsSet = tagBo.getFriendsIds();
					firendsSet.remove(friendid);
					tagService.updateTagFriends(tagBo.getId(), firendsSet);
				}
			}
		}
	}


	@RequestMapping("/add-tag-friends")
	@ResponseBody
	public String addTag(String tagName, String friendids,
						 HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
			String[] friendArr = friendids.split(",");
			TagBo tagBo = tagService.getBynameAndUserid(tagName, userBo.getId());
			if (tagBo != null) {
				LinkedHashSet<String> firendsSet = tagBo.getFriendsIds();
				for (String friendid : friendArr) {
					firendsSet.add(friendid);
				}
				tagService.updateTagFriends(tagBo.getId(), firendsSet);
			} else {
				tagBo = new TagBo();
				LinkedHashSet<String> firendsSet = tagBo.getFriendsIds();
				for (String friendid : friendArr) {
					firendsSet.add(friendid);
				}
				tagBo.setUserid(userBo.getId());
				tagBo.setName(tagName);
				tagService.insert(tagBo);
			}
		} catch (MyException e) {
			return e.getMessage();
		}
		return Constant.COM_RESP;
	}

	@RequestMapping("/get-tags")
	@ResponseBody
	public String getAllTags(HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		List<String> tags = new ArrayList<>();
		try {
			userBo = checkSession(request, userService);
			List<TagBo> tagBos = tagService.getTagBoListByUserid(userBo.getId());
			for (TagBo tagBo : tagBos) {
				tags.add(tagBo.getName());
			}
		} catch (MyException e) {
			return e.getMessage();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("tags", tags);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/get-tag-list")
	@ResponseBody
	public String getTagFriends(HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<TagVo> tagVos = new ArrayList<>();
		List<TagBo> tagBos = tagService.getTagBoListByUserid(userBo.getId());
		for (TagBo tagBo : tagBos) {
			LinkedHashSet<String> friends = tagBo.getFriendsIds();
			TagVo tagVo = new TagVo();
			LinkedHashSet<String> userNames = tagVo.getUserNames();
			HashSet<String> userNull = new LinkedHashSet<>();
			for (String friendid : friends) {
				FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userBo.getId(), friendid);
				if (friendsBo != null) {
					userNames.add(friendsBo.getBackname());
				} else {
					userNull.add(friendid);
				}
			}
			tagVo.setTagid(tagBo.getId());
			tagVo.setTagName(tagBo.getName());
			tagVo.setUserNum(userNames.size());
			tagVos.add(tagVo);
			if (userNull.size() > 0){
				friends.removeAll(userNull);
				tagService.updateTagFriends(tagBo.getId(), friends);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("tagVos", tagVos);
		return JSONObject.fromObject(map).toString();
	}


	@RequestMapping("/get-friend-tags")
	@ResponseBody
	public String getTag(String friendid, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(
				userBo.getId(), friendid);
		if (null == friendsBo) {
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		List<TagBo> tagBoList = tagService.getTagBoListByUseridAndFrinedid(
				userBo.getId(), friendid);
		List<String> tags = new ArrayList<>();
		for (TagBo tagBo : tagBoList) {
			tags.add(tagBo.getName());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("tags", tags);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/get-by-tagName")
	@ResponseBody
	public String getByTag(String tagName, HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		TagBo tagBo = tagService.getBynameAndUserid(tagName, userBo.getId());
		List<UserBaseVo> userBaseVos = new ArrayList<>();
		LinkedHashSet<String> removes = new LinkedHashSet<>();
		if (tagBo != null) {
			LinkedHashSet<String> friends = tagBo.getFriendsIds();
			for (String friendid: friends) {
				FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(
						userBo.getId(), friendid);
				if (friendsBo == null) {
					removes.add(friendid);
				} else {
					UserBo user = userService.getUser(friendid);
					if (user != null) {
						UserBaseVo baseVo = new UserBaseVo();
						BeanUtils.copyProperties(user, baseVo);
						baseVo.setUserName(friendsBo.getBackname());
						userBaseVos.add(baseVo);
					}
				}
			}
			if (removes.size() > 0) {
				friends.removeAll(removes);
				tagService.updateTagFriends(tagName,friends);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userVos", userBaseVos);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/delete-tag")
	@ResponseBody
	public String deleteTag(String tagid,
							HttpServletRequest request, HttpServletResponse response) {
		try {
			checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		TagBo tagBo = tagService.get(tagid);
		if (null != tagBo) {
			tagService.deleteById(tagid);
		}
		return Constant.COM_RESP;
	}


	@RequestMapping("/update-tag-name")
	@ResponseBody
	public String updateTagName(String tagid, String name,
								HttpServletRequest request, HttpServletResponse response) {
		try {
			checkSession(request, userService);
			TagBo tagBo = tagService.get(tagid);
			if (null == tagBo) {
				return CommonUtil.toErrorResult(ERRORCODE.TAG_NULL.getIndex(),
						ERRORCODE.TAG_NULL.getReason());
			}
			tagBo.setName(name.trim());
			tagService.updateTagName(tagBo);
		} catch (MyException e) {
			return e.getMessage();
		}
		return Constant.COM_RESP;
	}


	@RequestMapping("/cancel-friend-tag")
	@ResponseBody
	public String deleteTag(String tagid, String frinedids,
							HttpServletRequest request, HttpServletResponse response) {
		try {
			checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		TagBo tagBo = tagService.get(tagid);
		if (null == tagBo) {
			return CommonUtil.toErrorResult(ERRORCODE.TAG_NULL.getIndex(),
					ERRORCODE.TAG_NULL.getReason());
		}
		LinkedHashSet<String> oldFids = tagBo.getFriendsIds();
		String[] friends = frinedids.split(",");
		for (String frinedid: friends) {
			if (!oldFids.contains(frinedid)) {
				return CommonUtil.toErrorResult(ERRORCODE.FRIEND_TAG_NULL.getIndex(),
						ERRORCODE.FRIEND_TAG_NULL.getReason());
			}
			oldFids.remove(frinedid);
		}
		tagBo.setFriendsIds(oldFids);
		tagService.updateFriendsIdsById(tagBo);
		return Constant.COM_RESP;
	}

	@RequestMapping("/tag-add-friends")
	@ResponseBody
	public String tagAddFriends(String tagid, String friendsids,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
			TagBo tagBo = tagService.get(tagid);
			if (null == tagBo) {
				return CommonUtil.toErrorResult(ERRORCODE.TAG_NULL.getIndex(),
						ERRORCODE.TAG_NULL.getReason());
			}
			LinkedHashSet<String> firendsSet = tagBo.getFriendsIds();
			addFriend(friendsids, firendsSet,userBo.getId());
			tagBo.setFriendsIds(firendsSet);
			tagService.updateFriendsIdsById(tagBo);
		} catch (MyException e) {
			return e.getMessage();
		}
		return Constant.COM_RESP;
	}

	
	private void addFriend(String friendids, HashSet<String> firendsSet, String userId) throws MyException{
		String[] friends = friendids.split(",");
		for (String friendId : friends) {
			FriendsBo friendsBo = friendsService
					.getFriendByIdAndVisitorIdAgree(userId, friendId);
			if (null == friendsBo) {
				throw new MyException(CommonUtil.toErrorResult(
						ERRORCODE.FRIEND_NULL.getIndex(),
						ERRORCODE.FRIEND_NULL.getReason()));
			}
			firendsSet.add(friendId);
		}
	}
}
