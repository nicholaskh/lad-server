package com.lad.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lad.util.CommonUtil;

@Controller
@RequestMapping("test")
public class TestController extends BaseContorller {

	@RequestMapping("/send")
	@ResponseBody
	public void setTag(HttpServletRequest request, HttpServletResponse response) {
		CommonUtil.sendSMS2("18141908856", "test sms2");
	}

}
