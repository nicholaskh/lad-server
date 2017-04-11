package com.junlenet.mongodb.demo.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.junlenet.mongodb.demo.bo.MessageBo;
import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.service.IMessageService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("message")
public class MessageController extends BaseContorller {

	@Autowired
	private IMessageService messageService;

	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(String content, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!(Boolean) session.getAttribute("isLogin")) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!StringUtils.hasLength(content)) {
			return "{\"ret\":-1,\"error\":\"error phone\"}";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		MessageBo messageBo = new MessageBo();
		messageBo.setContent(content);
		messageBo.setOwner_id(userBo.getId());
		messageService.insert(messageBo);
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/thumbsup")
	@ResponseBody
	public String thumbsup(String messageId, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!(Boolean) session.getAttribute("isLogin")) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (!StringUtils.hasLength(messageId)) {
			return "{\"ret\":-1,\"error\":\"error messageId\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		MessageBo messageBo = messageService.selectById(messageId);
		if(messageBo == null ){
			return "{\"ret\":-1,\"error\":\"error messageId\"}";
		}
		LinkedList<String> thumbsup_ids = messageBo.getThumbsup_ids();
		if(thumbsup_ids == null){
			thumbsup_ids = new LinkedList<String>();
		}
		if(thumbsup_ids.contains(userBo.getId())){
			return "{\"ret\":-1,\"error\":\"duplicate id\"}";
		}
		thumbsup_ids.add(userBo.getId());
		messageBo.setThumbsup_ids(thumbsup_ids);
		messageService.update_thumbsup_ids(messageBo);
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
}
