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
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Controller
@RequestMapping("tag")
public class TagController extends BaseContorller {

	@Autowired
	private ITagService tagService;
	@Autowired
	private IFriendsService friendsService;
	@Autowired
	private IUserService userService;

	@RequestMapping("/set-tag")
	@ResponseBody
	public String setTag(String name, String friendsids,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
			TagBo tagBo = tagService.getBynameAndUserid(name.trim(), userBo.getId());
			if (tagBo != null) {
				return CommonUtil.toErrorResult(ERRORCODE.TAG_NAME_EXIST.getIndex(),
						ERRORCODE.TAG_NAME_EXIST.getReason());
			}
			HashSet<String> firendsSet = new HashSet<String>();
			addFriend(friendsids,firendsSet, userBo.getId());
			tagBo = new TagBo();
			tagBo.setFriendsIds(firendsSet);
			tagBo.setUserid(userBo.getId());
			tagBo.setName(name.trim());
			tagService.insert(tagBo);
		} catch (MyException e) {
			return e.getMessage();
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

	@RequestMapping("/get-friend-tag")
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
		List<TagVo> tagVoList = new LinkedList<TagVo>();
		for (TagBo tagBo : tagBoList) {
			TagVo TagVo = new TagVo();
			try {
				BeanUtils.copyProperties(TagVo, tagBo);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			tagVoList.add(TagVo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("tag", tagVoList);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/get-tag-list")
	@ResponseBody
	public String getTagList(HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<TagBo> tagBoList = tagService.getTagBoListByUserid(userBo.getId());
		List<TagVo> tagVoList = new LinkedList<TagVo>();
		for (TagBo tagBo : tagBoList) {
			TagVo tagVo = new TagVo();
			tagVo.setId(tagBo.getId());
			tagVo.setName(tagBo.getName());
			tagVoList.add(tagVo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("tag", tagVoList);
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
		if (null == tagBo) {
			return CommonUtil.toErrorResult(ERRORCODE.TAG_NULL.getIndex(),
					ERRORCODE.TAG_NULL.getReason());
		}
		tagService.deleteById(tagid);
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
		HashSet<String> oldFids = tagBo.getFriendsIds();
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
			HashSet<String> firendsSet = tagBo.getFriendsIds();
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
