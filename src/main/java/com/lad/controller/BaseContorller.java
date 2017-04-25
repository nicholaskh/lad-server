package com.lad.controller;


import org.springframework.ui.ModelMap;

public abstract class BaseContorller {
	/**
	 * 定向到错误页面
	 * 
	 * @return view
	 */
	public String toErrorPage() {
		return "/error";
	}
	
	/**
	 * 定向到错误页面
	 * 
	 * @param msg 错误消息
	 * @param model ModelMap
	 * @return view
	 */
	public String toErrorPage(String msg, ModelMap model) {
		model.addAttribute("ERROR_MSG", msg);
		return this.toErrorPage();
	}

}
