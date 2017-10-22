package com.lad.controller;

import com.lad.bo.CollectBo;
import com.lad.bo.UserBo;
import com.lad.bo.UserTagBo;
import com.lad.service.ICollectService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.MyException;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/collect")
public class CollectController extends BaseContorller {
	
	@Autowired
	private ICollectService collectService;
	@Autowired
	private IUserService userService;
	
	@RequestMapping("/chat")
	@ResponseBody
	public String chat(@RequestParam String title, @RequestParam String content,
			HttpServletRequest request, HttpServletResponse response){

		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CollectBo chatBo = new CollectBo();
		chatBo.setCreateuid(userBo.getId());
		chatBo.setUserid(userBo.getId());
		chatBo.setContent(content);
		chatBo.setTitle(title);
		chatBo.setType(Constant.CHAT_TYPE);
		chatBo = collectService.insert(chatBo);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("col-time", CommonUtil.time2str(chatBo.getCreateTime()));
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/my-chats")
	@ResponseBody
	public String myChats(@RequestParam(required = false) String start_id, @RequestParam int limit,
					   HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<CollectBo> collectBos = collectService.findChatByUserid(userBo.getId(),
				start_id, limit, Constant.CHAT_TYPE);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("col-chats", collectBos);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/add-tag")
	@ResponseBody
	public String addTag(String name,
						  HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		UserTagBo userTagBo = new UserTagBo();
		userTagBo.setUserid(userBo.getId());
		userTagBo.setTagName(name);
		userTagBo.setTagType(0);
		collectService.insertTag(userTagBo);
		return  Constant.COM_RESP;
	}

	@RequestMapping("/my-tags")
	@ResponseBody
	public String myTag(HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<UserTagBo> tagBos = collectService.findTagByUserid(userBo.getId(), 0);
		List<String> tagNames = new ArrayList<>();
		for (UserTagBo tagBo : tagBos) {
			tagNames.add(tagBo.getTagName());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("tags", tagNames);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/my-collects")
	@ResponseBody
	public String myCols(int page, int limit,
						 HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<CollectBo> collectBos = collectService.findAllByUserid(userBo.getId(), page, limit);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("tags", collectBos);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/col-note")
	@ResponseBody
	public String colNotes(String noteid, HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}

		CollectBo chatBo = new CollectBo();
		chatBo.setCreateuid(userBo.getId());
		chatBo.setUserid(userBo.getId());
		chatBo.setTargetid(noteid);
		chatBo.setType(Constant.COLLET_URL);
		chatBo.setSub_type(Constant.NOTE_TYPE);
		collectService.insert(chatBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("col-time", CommonUtil.time2str(chatBo.getCreateTime()));
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/col-party")
	@ResponseBody
	public String colParty(String partyid, HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CollectBo chatBo = new CollectBo();
		chatBo.setCreateuid(userBo.getId());
		chatBo.setUserid(userBo.getId());
		chatBo.setTargetid(partyid);
		chatBo.setType(Constant.COLLET_URL);
		chatBo.setSub_type(Constant.PARTY_TYPE);
		collectService.insert(chatBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("col-time", CommonUtil.time2str(chatBo.getCreateTime()));
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/col-files")
	@ResponseBody
	public String colFile(String path, int fileType, HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CollectBo chatBo = new CollectBo();
		chatBo.setCreateuid(userBo.getId());
		chatBo.setUserid(userBo.getId());
		chatBo.setPath(path);
		switch (fileType){
			case 1: //文件
				chatBo.setType(Constant.COLLET_URL);
				chatBo.setSub_type(Constant.FILE_TYPE);
				break;
			case 2://图片
				chatBo.setType(Constant.COLLET_PIC);
				break;
			case 3:
				chatBo.setType(Constant.COLLET_MUSIC);
				break;
			case 4:
				chatBo.setType(Constant.COLLET_VIDEO);
				break;
			case 5:
				chatBo.setType(Constant.COLLET_VOICE);
				break;
			default:
				return CommonUtil.toErrorResult(
					ERRORCODE.COLLECT_TYPE_ERR.getIndex(),
					ERRORCODE.COLLECT_TYPE_ERR.getReason());
		}
		collectService.insert(chatBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("col-time", CommonUtil.time2str(chatBo.getCreateTime()));
		return JSONObject.fromObject(map).toString();
	}



	@RequestMapping("/by-tagName")
	@ResponseBody
	public String findByTag(String tagName, int page, int limit, HttpServletRequest request, HttpServletResponse
			response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}

		List<CollectBo> collectBos = collectService.findByTag(tagName, userBo.getId(), page, limit);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("tags", collectBos);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/by-type")
	@ResponseBody
	public String findByTag(int type, int page, int limit, HttpServletRequest request, HttpServletResponse
			response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<CollectBo> collectBos = collectService.findByUseridAndType(userBo.getId(), type, page, limit);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("tags", collectBos);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 给收藏添加分类
	 * @param tags
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/add-col-tag")
	@ResponseBody
	public String addCollectTag(String tags, String collectid,
								HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CollectBo collectBo = collectService.findById(collectid);
		if (collectBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.COLLECT_IS_NULL.getIndex(),
					ERRORCODE.COLLECT_IS_NULL.getReason());
		}
		String[] tagArr = tags.split(",");
		LinkedHashSet<String> userTags =  collectBo.getUserTags();
		for (String tag: tagArr) {
			userTags.add(tag);
		}
		collectService.updateTags(collectid, userTags);
		return  Constant.COM_RESP;
	}
}
