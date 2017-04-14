package com.junlenet.mongodb.demo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.junlenet.mongodb.demo.bo.FeedbackBo;
import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.service.IFeedbackService;

import net.sf.json.JSONObject;

@Controller
@Scope("prototype")
@RequestMapping("feedback")
public class FeedbackController extends BaseContorller {

	@Autowired
	private IFeedbackService feedbackService;

	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(String content, String contactInfo, String image, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		FeedbackBo feedbackBo = new FeedbackBo();
		if (StringUtils.isEmpty(content)) {
			return "{\"ret\":-1,\"error\":\"feeadback is null\"}";
		}
		feedbackBo.setContent(content);
		feedbackBo.setContactInfo(contactInfo);
		feedbackBo.setOwnerId(userBo.getId());
		feedbackBo.setImage(image);
		feedbackService.insert(feedbackBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
}
