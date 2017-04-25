package com.lad.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lad.bo.MessageBo;
import com.lad.bo.UserBo;
import com.lad.service.IMessageService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.lad.vo.MessageVo;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("message")
public class MessageController extends BaseContorller {

	@Autowired
	private IMessageService messageService;

	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(String content, String source, HttpServletRequest request, HttpServletResponse response) {
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
		if (!StringUtils.hasLength(content)) {
			return CommonUtil.toErrorResult(ERRORCODE.CONTACT_CONTENT.getIndex(),
					ERRORCODE.CONTACT_CONTENT.getReason());
		}
		if (!StringUtils.hasLength(source)) {
			return CommonUtil.toErrorResult(ERRORCODE.CONTACT_SOURCE.getIndex(),
					ERRORCODE.CONTACT_SOURCE.getReason());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		MessageBo messageBo = new MessageBo();
		messageBo.setContent(content);
		messageBo.setSource(source);
		messageBo.setOwnerId(userBo.getId());
		messageService.insert(messageBo);
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/my-message")
	@ResponseBody
	public String my_message(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
		List<MessageBo> list = messageService.selectByUserId(userBo.getId());
		List<MessageVo> message_from_me_vo = new ArrayList<MessageVo>();
		for (MessageBo item : list) {
			MessageVo vo = new MessageVo();
			BeanUtils.copyProperties(vo, item);
			message_from_me_vo.add(vo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("message_from_me_vo", message_from_me_vo);
		return JSONObject.fromObject(map).toString();
	}

}
