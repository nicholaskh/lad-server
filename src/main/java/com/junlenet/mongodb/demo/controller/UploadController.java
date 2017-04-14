package com.junlenet.mongodb.demo.controller;

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
import org.springframework.web.multipart.MultipartFile;

import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.service.IUserService;
import com.junlenet.mongodb.demo.util.CommonUtil;
import com.junlenet.mongodb.demo.util.Constant;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("upload")
public class UploadController extends BaseContorller {
	@Autowired
	private IUserService userService;

	@RequestMapping("/head-picture")
	@ResponseBody
	public String head_picture(@RequestParam("head_picture") MultipartFile file, HttpServletRequest request,
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
		String userId = userBo.getId();
		String fileName = userId + file.getOriginalFilename();
		CommonUtil.upload(file, Constant.HEAD_PICTURE_PATH, fileName);
		userBo.setHeadPictureName(fileName);
		userService.updateHeadPictureName(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("path", Constant.HEAD_PICTURE_PATH + fileName);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/feedback-picture")
	@ResponseBody
	public String feedback_picture(@RequestParam("feedback_picture") MultipartFile file, HttpServletRequest request,
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
		String userId = userBo.getId();
		String fileName = userId + file.getOriginalFilename();
		CommonUtil.upload(file, Constant.FEEDBACK_PICTURE_PATH, fileName);
		userBo.setHeadPictureName(fileName);
		userService.updateHeadPictureName(userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("path", Constant.FEEDBACK_PICTURE_PATH + fileName);
		return JSONObject.fromObject(map).toString();
	}

}
