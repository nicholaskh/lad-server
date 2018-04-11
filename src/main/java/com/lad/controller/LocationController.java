package com.lad.controller;

import com.lad.bo.LocationBo;
import com.lad.bo.UserBo;
import com.lad.service.ILocationService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("location")
public class LocationController extends BaseContorller {

	@Autowired
	private ILocationService locationService;
	@Autowired
	private IUserService userService;

	private final static Logger log = LogManager.getLogger(LocationController.class);

	@RequestMapping(value = "/near",method = {RequestMethod.GET, RequestMethod.POST})
	public String near(double px, double py, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Point point = new Point(px, py);
		GeoResults<LocationBo> locationBoList = locationService.findUserNear(point, 10000);
		JSONArray array = new JSONArray();
		DecimalFormat df = new DecimalFormat("###.00");
		for (GeoResult<LocationBo> bo : locationBoList) {
			LocationBo locationBo = bo.getContent();
			UserBo temp = userService.getUser(locationBo.getUserid());
			if (temp !=  null) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id",temp.getId());
				jsonObject.put("userName",temp.getUserName());
				jsonObject.put("phone",temp.getPhone());
				jsonObject.put("sex",temp.getSex());
				jsonObject.put("headPictureName",temp.getHeadPictureName());
				jsonObject.put("birthDay",temp.getBirthDay());
				jsonObject.put("personalizedSignature",temp.getPersonalizedSignature());
				jsonObject.put("level",temp.getLevel());
				double dis = Double.parseDouble(df.format(bo.getDistance().getValue()));
				jsonObject.put("distance",dis);
				array.add(jsonObject);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userList", array);
		return JSONObject.fromObject(map).toString();
	}


	@RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
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

	@ApiOperation("根据用户id更新用户位置")
	@PostMapping("/update-location")
	public String updateLocationByUserid(String userid, double px, double py,
								 HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = userService.getUser(userid);
		if (null != userBo) {
			LocationBo locationBo  = locationService.getLocationBoByUserid(userid);
			double[] postion = new double[]{px, py};

			if (null == locationBo) {
				locationBo = new LocationBo();
				locationBo.setUserid(userBo.getId());
				locationBo.setPosition(postion);
				locationService.insertUserPoint(locationBo);
			} else {
				locationBo.setPosition(postion);
				locationBo.setUpdateTime(new Date());
				locationService.updateUserPoint(locationBo);
			}
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.USER_NULL.getIndex(),
					ERRORCODE.USER_NULL.getReason());
		}
		return Constant.COM_RESP;
	}
}