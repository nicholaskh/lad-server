package com.lad.controller;

import com.lad.bo.FeedbackBo;
import com.lad.bo.UserBo;
import com.lad.service.IFeedbackService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.MyException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		FeedbackBo feedbackBo = new FeedbackBo();
		if (StringUtils.isEmpty(content)) {
			return CommonUtil.toErrorResult(ERRORCODE.FEEDBACK_NULL.getIndex(),
					ERRORCODE.FEEDBACK_NULL.getReason());
		}
		feedbackBo.setContent(content);
		feedbackBo.setContactInfo(contactInfo);
		feedbackBo.setOwnerId(userBo.getId());
		feedbackBo.getImages().add(image);
		feedbackBo.setCreateuid(userBo.getId());
		feedbackService.insert(feedbackBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/note-tips-add")
	@ResponseBody
	public String noteTipsAdd(@RequestParam String module,
						   @RequestParam(required = false)String subModule,
						   @RequestParam String content,
						   @RequestParam String targetId,
						   @RequestParam String targetTitle,
						   @RequestParam(required = false) MultipartFile[] pictures,
						 HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		FeedbackBo feedbackBo = new FeedbackBo();
		feedbackBo.setContent(content);
		feedbackBo.setTargetId(targetId);
		feedbackBo.setTargetTitle(targetTitle);
		feedbackBo.setModule(module);
		feedbackBo.setSubModule(subModule);
		feedbackBo.setType(Constant.FEED_TIPS);
		feedbackBo.setSubType(Constant.NOTE_TYPE);
		feedbackBo.setCreateuid(userBo.getId());

		LinkedList<String> images = feedbackBo.getImages();
		String userId =  userBo.getId();
		if (pictures != null) {
			for (MultipartFile file : pictures) {
				long time = Calendar.getInstance().getTimeInMillis();
				String fileName = String.format("%s-%d-%s", userId, time, file.getOriginalFilename());
				String path = CommonUtil.upload(file, Constant.FEEDBACK_PICTURE_PATH,
						fileName, 0);
				images.add(path);
			}
		}
		feedbackService.insert(feedbackBo);
		return Constant.COM_RESP;
	}
}
