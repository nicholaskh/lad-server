package com.lad.controller;

import com.lad.bo.CollectBo;
import com.lad.bo.UserBo;
import com.lad.service.ICollectService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.MyException;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


	@RequestMapping("/note")
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
}
