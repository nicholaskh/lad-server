package com.lad.controller;

import com.lad.bo.CityBo;
import com.lad.service.ICityService;
import com.mongodb.BasicDBObject;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @RequestMapping("/get-citys")
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
        List<CityBo> cityBos = cityService.findByParams(province, city);
        for (CityBo cityBo : cityBos) {
            district.add(cityBo.getDistrit());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("districts", district);
        return JSONObject.fromObject(map).toString();
    }


}
