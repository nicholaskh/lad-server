package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.MyException;
import com.lad.vo.DynamicVo;
import com.lad.vo.NoteVo;
import net.sf.json.JSONObject;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private IThumbsupService thumbsupService;

    @Autowired
    private ICircleService circleService;


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
    public String insert(double px, double py, String title, String content, String landmark,
                         MultipartFile[] pictures, String type, HttpServletRequest request,
                         HttpServletResponse response){
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
            LinkedHashSet<String> images = dynamicBo.getPhotos();
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
            dynamicBo.setPhotos(images);
        }
        dynamicService.addDynamic(dynamicBo);
        addDynamicMsgs(userId, dynamicBo.getId(), Constant.DYNAMIC_TYPE, dynamicService);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicid", dynamicBo.getId());
        return JSONObject.fromObject(map).toString();
    }


    /**
     * 所有好友动态列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/all-dynamics")
    @ResponseBody
    public String allDynamics(int page, int limit, HttpServletRequest request, HttpServletResponse response){

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
        List<DynamicBackBo> backBos = dynamicService.findWhoBackMe(userBo.getId());
        if(backBos != null && !backBos.isEmpty()) {
             for (DynamicBackBo bo : backBos) {
                 if (friends.contains(bo.getUserid())) {
                     friends.remove(bo.getUserid());
                 }
             }
        }
        List<DynamicMsgBo> msgBos = dynamicService.findAllFriendsMsg(friends, page, limit);
        List<DynamicVo> dynamicVos = new ArrayList<>();
        bo2vo(msgBos,dynamicVos, null);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicVos", dynamicVos);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 获取所有好友动态数量
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/all-dynamics-num")
    @ResponseBody
    public String allFriendsNum(HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

        List<String> friends = userBo.getFriends();
        long total = 0L;
        for (String friendid : friends) {
            DynamicNumBo numBo = dynamicService.findNumByUserid(friendid);
            if (numBo != null) {
                total += numBo.getNumber();
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicNum", total);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 好友动态列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/friend-dynamics")
    @ResponseBody
    public String allDynamics(String friendid,int page, int limit,
                              HttpServletRequest request, HttpServletResponse response){
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
        List<DynamicBackBo> backBos = dynamicService.findWhoBackMe(userBo.getId());
        if(backBos != null && !backBos.isEmpty()) {
            for (DynamicBackBo bo : backBos) {
                if (friends.contains(bo.getUserid())) {
                    friends.remove(bo.getUserid());
                }
            }
        }
        List<DynamicMsgBo> msgBos = dynamicService.findOneFriendMsg(friendid, page, limit);
        List<DynamicVo> dynamicVos = new ArrayList<>();
        bo2vo(msgBos,dynamicVos, userService.getUser(friendid));
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicVos", dynamicVos);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 获取好友动态数量
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/one-dynamics-num")
    @ResponseBody
    public String friendsNum(String friendid, HttpServletRequest request, HttpServletResponse response){
        try {
            checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        DynamicNumBo numBo = dynamicService.findNumByUserid(friendid);
        long total = 0L;
        if (numBo != null) {
            total = numBo.getNumber();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicNum", total);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 我的动态
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/my-dynamics")
    @ResponseBody
    public String myDynamics(int page, int limit,
                              HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        List<DynamicMsgBo> msgBos = dynamicService.findOneFriendMsg(userBo.getId(), page, limit);
        List<DynamicVo> dynamicVos = new ArrayList<>();
        bo2vo(msgBos,dynamicVos, userBo);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicVos", dynamicVos);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 我的动态数量
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/my-dynamics-num")
    @ResponseBody
    public String myDynamicsNum(HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        DynamicNumBo numBo = dynamicService.findNumByUserid(userBo.getId());
        long total = 0L;
        if (numBo != null) {
            total = numBo.getNumber();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicNum", total);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 好友动态列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/dynamic-detail")
    @ResponseBody
    public String allDynamics(String msgid, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        DynamicMsgBo msgBo = dynamicService.findByMsgid(msgid);
        int type = msgBo.getDynamicType();
        String id = msgBo.getTargetid();
        ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(userBo.getId(), id);
        if (type == Constant.NOTE_TYPE) {
            NoteBo noteBo = noteService.selectById(id);
            updateCircleHot(circleService, redisServer, noteBo.getCircleId(), 1, Constant.CIRCLE_NOTE_VISIT);
            RLock lock = redisServer.getRLock(Constant.VISIT_LOCK);
            try {
                lock.lock(3, TimeUnit.SECONDS);
                noteService.updateVisitCount(id);
            } finally {
                lock.unlock();
            }
            NoteVo noteVo = new NoteVo();
            BeanUtils.copyProperties(noteBo, noteVo);
            if (userBo!= null) {
                noteVo.setSex(userBo.getSex());
                noteVo.setBirthDay(userBo.getBirthDay());
                noteVo.setHeadPictureName(userBo.getHeadPictureName());
                noteVo.setUsername(userBo.getUserName());
                noteVo.setUserLevel(userBo.getLevel());
            }
            noteVo.setPosition(noteBo.getPosition());
            noteVo.setCommontCount(noteBo.getCommentcount());
            noteVo.setVisitCount(noteBo.getVisitcount());
            noteVo.setNodeid(noteBo.getId());
            noteVo.setTransCount(noteBo.getTranscount());
            noteVo.setThumpsubCount(noteBo.getThumpsubcount());
            //这个帖子自己是否点赞
            noteVo.setMyThumbsup(null != thumbsupBo);
            map.put("noteVo", noteVo);
        } else if (type == Constant.PARTY_TYPE) {


        } else if (type == Constant.DYNAMIC_TYPE) {
            DynamicBo dynamicBo = dynamicService.findDynamicById(id);
            DynamicVo dynamicVo = new DynamicVo();
            BeanUtils.copyProperties(dynamicBo, dynamicVo);
            dynamicVo.setPhotos(new ArrayList<>(dynamicBo.getPhotos()));
            map.put("dynamicVo", dynamicVo);
        }
        return JSONObject.fromObject(map).toString();
    }


    /**
     * 不看他的动态
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/dynamic-not-see")
    @ResponseBody
    public String notSee(String friendid, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        DynamicBackBo backBo = dynamicService.findBackByUserid(userBo.getId());
        if (backBo == null) {
            backBo = new DynamicBackBo();
            backBo.setUserid(userBo.getId());
            HashSet<String> notSees = backBo.getNotSeeBacks();
            notSees.add(friendid);
            backBo.setNotSeeBacks(notSees);
            dynamicService.addDynamicBack(backBo);
        } else {
            HashSet<String> notSees = backBo.getNotSeeBacks();
            notSees.add(friendid);
            dynamicService.updateBackNotSee(backBo.getId(), notSees);
        }
        return Constant.COM_RESP;
    }







    
    private void bo2vo(List<DynamicMsgBo> msgBos, List<DynamicVo> dynamicVos, UserBo userBo){
        for (DynamicMsgBo msgBo : msgBos) {
            int type = msgBo.getDynamicType();
            String id = msgBo.getTargetid();
            DynamicVo dynamicVo = new DynamicVo();
            dynamicVo.setMsgid(msgBo.getId());
            if (userBo == null) {
                userBo = userService.getUser(msgBo.getUserid());
            }
            dynamicVo.setUserid(msgBo.getUserid());
            dynamicVo.setUserPic(userBo.getHeadPictureName());
            if (type == Constant.NOTE_TYPE) {
                NoteBo noteBo = noteService.selectById(id);
                dynamicVo.setTitle(noteBo.getSubject());
                dynamicVo.setContent(noteBo.getContent());
                dynamicVo.setPhotos(noteBo.getPhotos());
                dynamicVo.setCommentNum(noteBo.getCommentcount());
                dynamicVo.setThumpNum(noteBo.getThumpsubcount());
                dynamicVo.setCommentNum(noteBo.getCommentcount());
                dynamicVo.setLandmark(noteBo.getLandmark());
            } else if (type == Constant.PARTY_TYPE) {


            } else if (type == Constant.DYNAMIC_TYPE) {
                DynamicBo dynamicBo = dynamicService.findDynamicById(id);
                BeanUtils.copyProperties(dynamicBo, dynamicVo);
                dynamicVo.setPhotos(new ArrayList<>(dynamicBo.getPhotos()));

            }
            dynamicVos.add(dynamicVo);
        }

    }
    
}
