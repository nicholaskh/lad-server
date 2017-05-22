package com.lad.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lad.bo.LocationBo;
import com.lad.bo.UserBo;
import com.lad.service.ILocationService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.lad.vo.UserVo;

@Controller
@RequestMapping("location")
public class LocationController extends BaseContorller {
	
	@Autowired
	private ILocationService locationService;
	@Autowired
	private IUserService userService;
	
	@RequestMapping("/near")
	@ResponseBody
	public String near(double px, double py, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
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
		List<LocationBo> locationBoList = locationService.findCircleNear(px, py, 100);
		List<UserVo> list = new LinkedList<UserVo>();
		for(LocationBo bo : locationBoList){
			String userid = bo.getUserid();
			UserBo temp = userService.getUser(userid);
			UserVo vo = new UserVo();
			BeanUtils.copyProperties(vo, temp);
			list.add(vo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userList", list);
		return JSONObject.fromObject(map).toString();
	}
}
