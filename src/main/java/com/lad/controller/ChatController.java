package com.lad.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lad.bo.ChatinfoBo;
import com.lad.bo.UserBo;
import com.lad.service.IChatService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/chat")
public class ChatController extends BaseContorller {
	
	@Autowired
	private IChatService chatService;
	@Autowired
	private IUserService userService;
	
	@RequestMapping("/collect-chat")
	@ResponseBody
	public String collect(@RequestParam(required = true) String title,
			@RequestParam(required = true) String content,
			HttpServletRequest request, HttpServletResponse response){
		
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
		
		ChatinfoBo chatBo = new ChatinfoBo();
		chatBo.setUserid(userBo.getId());
		chatBo.setContent(content);
		chatBo.setTitle(title);
		chatBo = chatService.saveChat(chatBo);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("col-time", CommonUtil.time2str(chatBo.getCreateTime()));
		return JSONObject.fromObject(map).toString();
	}

}
