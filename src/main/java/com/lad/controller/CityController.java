package com.lad.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.lad.bo.CityBo;
import com.lad.redis.RedisServer;
import com.lad.service.ICityService;
import com.lad.util.Constant;
import com.lad.util.PinyinComparator;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * 功能描述： Copyright: Copyright (c) 2017 Version: 1.0 Time:2017/9/6
 */
@Api("城市信息接口")
@Controller
@RequestMapping("/city")
public class CityController extends BaseContorller {

	@Autowired
	private ICityService cityService;

	@Autowired
	private RedisServer redisServer;

	/**
	 * 获取省市
	 */
	@RequestMapping(value = "/get-province", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getProvince() {
		List<BasicDBObject> objects = cityService.findProvince();
		List<String> prpvince = new ArrayList<>();
		for (BasicDBObject object : objects) {
			prpvince.add(object.get("province").toString());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("provinces", prpvince);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 获取城市和区
	 */
	@RequestMapping(value = "/get-province-city", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getProvinceCitys(String province) {

		List<String> citys = new ArrayList<>();
		if (province.equals("北京市") || province.equals("天津市") || province.equals("上海市") || province.equals("重庆市")) {
			List<CityBo> cityBos = cityService.findByParams(province, "");
			for (CityBo cityBo : cityBos) {
				citys.add(cityBo.getDistrit());
			}
		} else {
			List<BasicDBObject> objects = cityService.findCitys(province);
			for (BasicDBObject object : objects) {
				citys.add(object.get("city").toString());
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("citys", citys);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 获取城市和区
	 */
	@RequestMapping(value = "/get-city", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getCitys(String city) {
		List<String> district = new ArrayList<>();
		List<CityBo> cityBos = null;
		if (city.equals("北京市") || city.equals("天津市") || city.equals("上海市") || city.equals("重庆市")) {
			cityBos = cityService.findByParams(city, "");
		} else {
			cityBos = cityService.findByParams("", city, "");
		}
		for (CityBo cityBo : cityBos) {
			district.add(cityBo.getDistrit());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("citys", district);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 获取区县
	 */
	@RequestMapping(value = "/get-district", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getDistrict(String province, String city) {

		List<String> district = new ArrayList<>();
		List<CityBo> cityBos = null;
		if (province.equals("北京市") || province.equals("天津市") || province.equals("上海市") || province.equals("重庆市")) {
			cityBos = cityService.findByParams(province, "");
		} else {
			cityBos = cityService.findByParams(province, city, "");
		}
		for (CityBo cityBo : cityBos) {
			district.add(cityBo.getDistrit());
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("districts", district);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 获取城市和区
	 */
	@ApiOperation("根据英文字母顺序获取所有城市和区县")
	@RequestMapping(value = "/get-all", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getAlls() {
		RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
		String cityJson = "";
		if (cache.containsKey("citys")) {
			cityJson = (String) cache.get("citys");
			return cityJson;
		} else {
			cityJson = getOrderCitys();
			cache.put("citys", cityJson);
		}
		return cityJson;
	}

	/**
	 * 获取城市和区
	 */
	@RequestMapping(value = "/init", method = RequestMethod.GET)
	@ResponseBody
	public String init(HttpServletRequest request, HttpServletResponse response) {
		RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
		cache.clear();
		String res = getOrderCitys();
		cache.put("citys", res);
		return res;
	}

	@ApiOperation("获取所有省市区")
	@RequestMapping(value = "/get-all-citys", method = RequestMethod.GET)
	@ResponseBody
	public String getAllCitys() {
		List<BasicDBObject> objects = cityService.findProvince();
		JSONObject proObject = new JSONObject();
		for (BasicDBObject object : objects) {
			String province = object.get("province").toString();
			JSONArray ciArr = new JSONArray();
			if (province.equals("北京市") || province.equals("天津市") || province.equals("上海市") || province.equals("重庆市")) {
				List<CityBo> cityBos = cityService.findByParams(province, "");
				for (CityBo cityBo : cityBos) {
					ciArr.add(cityBo.getDistrit());
				}
				proObject.put(province, ciArr);
			} else {
				List<BasicDBObject> citObjs = cityService.findCitys(province);
				JSONObject disObject = new JSONObject();
				for (BasicDBObject basicDBObject : citObjs) {
					String city = basicDBObject.get("city").toString();
					List<CityBo> cityBoDis = cityService.findByParams(province, city, "");
					JSONArray disArr = new JSONArray();
					for (CityBo cityBo : cityBoDis) {
						disArr.add(cityBo.getDistrit());
					}
					disObject.put(city, disArr);
				}
				ciArr.add(disObject);
				proObject.put(province, ciArr);
			}
		}
		Map map = new HashMap<>();
		map.put("city", proObject);
		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}

	/**
	 * 获取市和县
	 * 
	 * @return
	 */
	private String getOrderCitys() {
		List<String> citys = new ArrayList<>();
		List<BasicDBObject> objects = cityService.findProvince();
		for (BasicDBObject object : objects) {
			String province = object.get("province").toString();
			if (province.equals("北京市") || province.equals("天津市") || province.equals("上海市") || province.equals("重庆市")) {
				citys.add(province);
			} else {
				List<BasicDBObject> citObjs = cityService.findCitys(province);
				for (BasicDBObject basicDBObject : citObjs) {
					String city = basicDBObject.get("city").toString();
					if (city.contains("地区")) {
						continue;
					}
					citys.add(city);
					List<CityBo> cityBoDis = cityService.findByParams(province, city, "");
					for (CityBo cityBo : cityBoDis) {
						if (!cityBo.getDistrit().contains("区")) {
							citys.add(cityBo.getDistrit());
						}
					}
				}
			}
		}

		Map<String, List<String>> map = new LinkedHashMap<>();
		Collections.sort(citys, new PinyinComparator());
		for (String string : citys) {
			String[] arrs = PinyinHelper.toHanyuPinyinStringArray(string.charAt(0));
			String first = String.valueOf(arrs[0].toUpperCase().charAt(0));
			if (string.contains("重庆")) {
				map.get("C").add(string);
				continue;
			}
			if (map.containsKey(first)) {
				map.get(first).add(string);
			} else {
				List<String> city = new ArrayList<>();
				city.add(string);
				map.put(first, city);
			}
		}
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("获取所有省市区")
	@RequestMapping(value = "/all-citys", method = RequestMethod.GET)
	@ResponseBody
	public String allCitys() {
		
		List<Map> resultList = new ArrayList<>();
		// 获取所有省
		List<String> citys = cityService.getProvince();
		// 遍历省份
		for (String provice : citys) {
			/**
			 * { 
			 * "province": "黑龙江省", 
			 * "cities": [
			 * 		{ "citys": "黑河市", "county": ["龙沙区", "建华区"] }, 
			 * 		{ "citys": "黑河市", "county": ["龙沙区", "建华区"] }
			 * 	]
			 * }
			 * 
			 * Map<String,Object>
			 * 		List<Map>
			 * 			Map<String,String>
			 * 			Map<String,List<String>>
			 */
			Map<String, Object> province = new HashMap<>();
			province.put("province", provice);

			// 获取所有市
			List<String> cityList =  cityService.getCity(provice);
			
			
			List<Map> citysList = new ArrayList<>();
			for (String city : cityList) {
				//获取所有县
				List<String> distritList = cityService.getDistrit(city);
				distritList.remove("市辖区");
				Map<String,Object> cityMap = new HashMap<>();
				cityMap.put("citys", city);
				cityMap.put("distrit", distritList);
				citysList.add(cityMap);
			}
			province.put("citys", citysList);
			resultList.add(province);
		}
		
		Map result = new HashMap<>();
		result.put("provinces", resultList);
		return JSONObject.fromObject(result).toString();
	}
}
