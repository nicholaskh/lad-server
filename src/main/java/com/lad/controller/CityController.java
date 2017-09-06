package com.lad.controller;

import com.lad.bo.CityBo;
import com.lad.redis.RedisServer;
import com.lad.service.ICityService;
import com.lad.util.Constant;
import com.mongodb.BasicDBObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/6
 */
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
    @RequestMapping("/get-province")
    @ResponseBody
    public String getProvince(HttpServletRequest request, HttpServletResponse response) {

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
    @RequestMapping("/get-city")
    @ResponseBody
    public String getCitys(String province, HttpServletRequest request, HttpServletResponse response) {

        List<String> citys = new ArrayList<>();
        if (province.equals("北京市") || province.equals("天津市") || province.equals("上海市")
						|| province.equals("重庆市")) {
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
     * 获取区县
     */
    @RequestMapping("/get-district")
    @ResponseBody
    public String getDistrict(String province,String city, HttpServletRequest request, HttpServletResponse response) {

        List<String> district = new ArrayList<>();
        List<CityBo> cityBos = null;
        if (province.equals("北京市") || province.equals("天津市") || province.equals("上海市")
                || province.equals("重庆市")) {
            cityBos = cityService.findByParams(province, "");
        } else {
            cityBos = cityService.findByParams(province, city);
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
    @RequestMapping("/get-all")
    @ResponseBody
    public String getAlls(HttpServletRequest request, HttpServletResponse response) {
        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
        String cityJson = "";
        if (cache.containsKey("citys")) {
            cityJson = (String) cache.get("citys");
            return cityJson;
        } else {
            cityJson =  getAllCitys();
            cache.put("citys",cityJson);
        }
        return cityJson ;
    }

    /**
     * 获取城市和区
     */
    @RequestMapping("/init")
    @ResponseBody
    public String init(HttpServletRequest request, HttpServletResponse response) {
        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
        cache.clear();
        String res = getAllCitys();
        cache.put("citys",res);
        return res ;
    }

    private String getAllCitys(){
        List<BasicDBObject> objects = cityService.findProvince();
        JSONObject proObject = new JSONObject();
        for (BasicDBObject object : objects) {
            String province = object.get("province").toString();
            Map<String , ArrayList<String>> citys = new LinkedHashMap<>();
            JSONArray ciArr = new JSONArray();
            if (province.equals("北京市") || province.equals("天津市") || province.equals("上海市")
                    || province.equals("重庆市")) {
                List<CityBo> cityBos = cityService.findByParams(province, "");
                for (CityBo cityBo : cityBos) {
                    ciArr.add(cityBo.getDistrit());
                }
                proObject.put(province, ciArr);
            } else if (province.equals("台湾省") || province.contains("香港") || province.equals("澳门")) {
                proObject.put(province, ciArr);
            }  else {
                List<BasicDBObject> citObjs = cityService.findCitys(province);
                JSONObject disObject = new JSONObject();
                for (BasicDBObject basicDBObject : citObjs) {
                    String city = basicDBObject.get("city").toString();
                    List<CityBo> cityBoDis = cityService.findByParams(province, city, "");
                    JSONArray disArr= new JSONArray();
                    for (CityBo cityBo : cityBoDis) {
                        disArr.add(cityBo.getDistrit());
                    }
                    disObject.put(city, disArr);
                }
                ciArr.add(disObject);
                proObject.put(province, ciArr);
            }
        }
        return  proObject.toString();
    }

}
