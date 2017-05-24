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

import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lad.bo.FriendsBo;
import com.lad.bo.TagBo;
import com.lad.bo.UserBo;
import com.lad.service.IFriendsService;
import com.lad.service.ITagService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.lad.vo.TagVo;

@Controller
@RequestMapping("tag")
public class TagController extends BaseContorller {
	
	@Autowired
	private ITagService tagService;
	@Autowired
	private IFriendsService friendsService;
	
	@RequestMapping("/set-tag")
	@ResponseBody
	public String setTag(String name, String friendsids, HttpServletRequest request, HttpServletResponse response) {
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
		String friends[] = friendsids.split(",");
		HashSet<String> firendsSet = new HashSet<String>();
		for(String friendId : friends){
			FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userBo.getId(), friendId);
			if(null == friendsBo){
				return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
						ERRORCODE.FRIEND_NULL.getReason());
			}
			firendsSet.add(friendId);
		}
		TagBo tagBo = new TagBo();
		tagBo.setFriendsIds(firendsSet);
		tagBo.setUserid(userBo.getId());
		tagBo.setName(name);
		tagService.insert(tagBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/get-friend-tag")
	@ResponseBody
	public String getTag(String friendid, HttpServletRequest request, HttpServletResponse response) {
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
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userBo.getId(), friendid);
		if(null == friendsBo){
			return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
					ERRORCODE.FRIEND_NULL.getReason());
		}
		List<TagBo> tagBoList = tagService.getTagBoListByUseridAndFrinedid(userBo.getId(), friendid);
		List<TagVo> tagVoList = new LinkedList<TagVo>();
		for(TagBo tagBo : tagBoList){
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
	public String getTagList(HttpServletRequest request, HttpServletResponse response) {
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
		List<TagBo> tagBoList = tagService.getTagBoListByUserid(userBo.getId());
		List<TagVo> tagVoList = new LinkedList<TagVo>();
		for(TagBo tagBo : tagBoList){
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
	
	@RequestMapping("/delete-tag")
	@ResponseBody
	public String deleteTag(String tagid, String frinedid, HttpServletRequest request, HttpServletResponse response) {
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
		TagBo tagBo = tagService.get(tagid);
		if(null == tagBo){
			return CommonUtil.toErrorResult(ERRORCODE.TAG_NULL.getIndex(),
					ERRORCODE.TAG_NULL.getReason());
		}
		HashSet<String> friendsids = new HashSet<String>();
		if(friendsids.contains(frinedid)){
			return CommonUtil.toErrorResult(ERRORCODE.TAG_NULL.getIndex(),
					ERRORCODE.TAG_NULL.getReason());
		}
		friendsids.remove(frinedid);
		tagBo.setFriendsIds(friendsids);
		tagService.updateFriendsIdsById(tagBo);
		if(friendsids.size() == 0){
			tagService.deleteById(tagid);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/tag-add-friends")
	@ResponseBody
	public String tagAddFriends(String tagid, String friendsids, HttpServletRequest request, HttpServletResponse response) {
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
		TagBo tagBo = tagService.get(tagid);
		if(null == tagBo){
			return CommonUtil.toErrorResult(ERRORCODE.TAG_NULL.getIndex(),
					ERRORCODE.TAG_NULL.getReason());
		}
		HashSet<String> firendsSet = tagBo.getFriendsIds();
		String [] friends = friendsids.split(",");
		for(String friendId : friends){
			FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userBo.getId(), friendId);
			if(null == friendsBo){
				return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
						ERRORCODE.FRIEND_NULL.getReason());
			}
			firendsSet.add(friendId);
		}
		tagBo.setFriendsIds(firendsSet);
		tagService.updateFriendsIdsById(tagBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
}
