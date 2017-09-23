package com.lad.controller;

import com.lad.bo.DynamicBackBo;
import com.lad.bo.DynamicBo;
import com.lad.bo.UserBo;
import com.lad.redis.RedisServer;
import com.lad.service.IDynamicService;
import com.lad.service.INoteService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.MyException;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/23
 */
@Controller
@RequestMapping("/dynamic")
public class DynamicController extends BaseContorller {

    @Autowired
    private RedisServer redisServer;

    @Autowired
    private IUserService userService;

    @Autowired
    private IDynamicService dynamicService;

    @Autowired
    private INoteService noteService;


    /**
     * 添加动态
     * @param px
     * @param py
     * @param title
     * @param content
     * @param landmark
     * @param pictures
     * @param type
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/insert")
    @ResponseBody
    public String insert(@RequestParam double px, @RequestParam double py,
                                  @RequestParam String title, @RequestParam String content,
                                  @RequestParam String landmark,
                                  @RequestParam("pictures") MultipartFile[] pictures,
                                  @RequestParam String type,
                                  HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        String userId = userBo.getId();
        DynamicBo dynamicBo = new DynamicBo();
        dynamicBo.setTitle(title);
        dynamicBo.setLandmark(landmark);
        dynamicBo.setContent(content);
        dynamicBo.setPostion(new double[] { px, py });
        dynamicBo.setCreateuid(userId);
        dynamicBo.setPicType(type);
        if (pictures != null) {
            LinkedHashSet<String> images = dynamicBo.getImages();
            Long time = Calendar.getInstance().getTimeInMillis();
            for (MultipartFile file : pictures) {
                String fileName = userId + "-" + time + "-"
                        + file.getOriginalFilename();
                if ("video".equals(type)) {
                    String[] paths = CommonUtil.uploadVedio(file, Constant.DYNAMIC_PICTURE_PATH, fileName, 0);
                    images.add(paths[0]);
                    dynamicBo.setVideoPic(paths[1]);
                } else {
                    String path = CommonUtil.upload(file, Constant.DYNAMIC_PICTURE_PATH,
                            fileName, 0);
                    images.add(path);
                }
            }
            dynamicBo.setImages(images);
        }
        dynamicService.addDynamic(dynamicBo);
        addDynamicMsgs(userId, dynamicBo.getId(), Constant.DYNAMIC_TYPE, dynamicService);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicid", dynamicBo.getId());
        return JSONObject.fromObject(map).toString();
    }


    /**
     * 所有好友动态
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/all-dynamics")
    @ResponseBody
    public String allDynamics(HttpServletRequest request, HttpServletResponse response){

        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

        List<String> friends = userBo.getFriends();

        DynamicBackBo backBo = dynamicService.findBackByUserid(userBo.getId());
        if (null != backBo) {
            HashSet<String> noSees = backBo.getNotSeeBacks();
            friends.removeAll(noSees);
        }


        
        



        return "";
    }


    
}
