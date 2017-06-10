package com.lad.controller;

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

import com.lad.bo.FeedbackBo;
import com.lad.bo.UserBo;
import com.lad.service.IFeedbackService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;

import net.sf.json.JSONObject;

@Controller
@Scope("prototype")
@RequestMapping("feedback")
public class FeedbackController extends BaseContorller {

	@Autowired
	private IFeedbackService feedbackService;
	@Autowired
	private IUserService userService;

	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(String content, String contactInfo, String image,
			HttpServletRequest request, HttpServletResponse response) {
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
		FeedbackBo feedbackBo = new FeedbackBo();
		if (StringUtils.isEmpty(content)) {
			return CommonUtil.toErrorResult(ERRORCODE.FEEDBACK_NULL.getIndex(),
					ERRORCODE.FEEDBACK_NULL.getReason());
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
