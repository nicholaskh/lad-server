package com.lad.controller;


import com.lad.bo.UserBo;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.lad.util.MyException;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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


	/**
	 * 统一校验session是否存在，不存在以异常跑出
	 * @param request
	 * @throws MyException
	 */
	public UserBo checkSession(HttpServletRequest request, IUserService userService) throws MyException{
		HttpSession session = request.getSession();
		if (session.isNew()) {
			throw new MyException(CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason()));
		}
		if (session.getAttribute("isLogin") == null) {
			throw new MyException(CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason()));
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			throw new MyException(CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason()));
		}
		userBo = userService.getUser(userBo.getId());
		if (null == userBo) {
			throw new MyException(CommonUtil.toErrorResult(
					ERRORCODE.USER_NULL.getIndex(),
					ERRORCODE.USER_NULL.getReason()));
		}
		return userBo;
	}

}
