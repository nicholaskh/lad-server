package com.lad.controller;

import com.lad.bo.LocationBo;
import com.lad.bo.UserBo;
import com.lad.service.ILocationService;
import com.lad.service.IUserService;
import com.lad.util.Constant;
import com.lad.vo.UserVo;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("location")
public class LocationController extends BaseContorller {

	@Autowired
	private ILocationService locationService;
	@Autowired
	private IUserService userService;

	private final static Logger log = LogManager.getLogger(LocationController.class);

	@RequestMapping("/near")
	@ResponseBody
	public String near(double px, double py, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<LocationBo> locationBoList = locationService.findCircleNear(px, py, 5000);
		List<UserVo> list = new LinkedList<UserVo>();
		for (LocationBo bo : locationBoList) {
			String userid = bo.getUserid();
			UserBo temp = userService.getUser(userid);
			if (temp !=  null) {
				UserVo vo = new UserVo();
				BeanUtils.copyProperties(vo, temp);
				list.add(vo);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userList", list);
		return JSONObject.fromObject(map).toString();
	}


	@RequestMapping("/update")
	@ResponseBody
	public String updateLocation(double px, double py, String phone,
								 HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = userService.getUserByPhone(phone);
		if (null != userBo) {
			LocationBo locationBo  = locationService.getLocationBoByUserid(userBo.getId());
			double[] postion = new double[]{px, py};

			if (null == locationBo) {
				locationBo = new LocationBo();
				locationBo.setUserid(userBo.getId());
				locationBo.setPosition(postion);
				locationBo = locationService.insertUserPoint(locationBo);
			} else {
				locationBo.setPosition(postion);
				locationBo.setUpdateTime(new Date());
				locationService.updateUserPoint(locationBo);
			}
			if (!locationBo.getId().equals(userBo.getLocationid())) {
				userBo.setLocationid(locationBo.getId());
				userService.updateLocation(phone, locationBo.getId());
			}
		}
		return Constant.COM_RESP;
	}
}