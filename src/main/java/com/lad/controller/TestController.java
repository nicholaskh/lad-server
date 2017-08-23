package com.lad.controller;

import com.lad.util.CommonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("test")
public class TestController extends BaseContorller {

	@RequestMapping("/send")
	@ResponseBody
	public void setTag(HttpServletRequest request, HttpServletResponse response) {
		int res = CommonUtil.sendSMS2("15320542105", "test sms2");
		System.out.println("SMS  message : ====== "  + res);
	}

}
