package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.MyException;
import com.lad.vo.DynamicVo;
import com.lad.vo.UserBaseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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
@Api("动态信息相关接口")
@RestController
@RequestMapping("/dynamic")
public class DynamicController extends BaseContorller {

    @Autowired
    private RedisServer redisServer;

    @Autowired
    private IUserService userService;

    @Autowired
    private IDynamicService dynamicService;

    @Autowired
    private IThumbsupService thumbsupService;

    @Autowired
    private ICircleService circleService;

    @Autowired
    private IFriendsService friendsService;


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
    @ApiOperation("添加动态信息")
    @RequestMapping(value = "/insert", method = {RequestMethod.GET, RequestMethod.POST})
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
            for (MultipartFile file : pictures) {
                Long time = Calendar.getInstance().getTimeInMillis();
                String fileName = String.format("%s-%d-%s", userBo.getId(), time, file.getOriginalFilename());
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
        updateDynamicNums(userId, 1, dynamicService, redisServer);
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
    @ApiOperation("所有好友动态列表")
    @RequestMapping(value = "/all-dynamics", method = {RequestMethod.GET, RequestMethod.POST})
    public String allDynamics(int page, int limit, HttpServletRequest request, HttpServletResponse response){

        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

        List<FriendsBo> friendsBos = friendsService.getFriendByUserid(userBo.getId());
        List<String> friends = new LinkedList<>();
        for (FriendsBo friendsBo : friendsBos) {
            friends.add(friendsBo.getFriendid());
        }
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
        List<DynamicBo> msgBos = dynamicService.findAllFriendsMsg(friends, page, limit);
        List<DynamicVo> dynamicVos = new ArrayList<>();
        bo2vo(msgBos, dynamicVos, userBo);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicVos", dynamicVos);
        map.put("backPic", userBo.getDynamicPic());
        map.put("headPic", userBo.getHeadPictureName());
        map.put("signature", userBo.getPersonalizedSignature());
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 获取所有好友动态数量
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("获取所有好友动态数量")
    @RequestMapping(value = "/all-dynamics-num", method = {RequestMethod.GET, RequestMethod.POST})
    public String allFriendsNum(HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

        List<FriendsBo> friendsBos = friendsService.getFriendByUserid(userBo.getId());
        List<String> friends = new LinkedList<>();
        for (FriendsBo friendsBo : friendsBos) {
            friends.add(friendsBo.getFriendid());
        }
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
        List<DynamicBo> msgBos = dynamicService.findAllFriendsMsg(friends, -1, 0);
        int total = msgBos != null ? msgBos.size() : 0;
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
    @ApiOperation("好友动态列表")
    @RequestMapping(value = "/friend-dynamics", method = {RequestMethod.GET, RequestMethod.POST})
    public String allDynamics(String friendid,int page, int limit,
                              HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        UserBo friend = userService.getUser(friendid);
        if (friend == null) {
            return CommonUtil.toErrorResult(ERRORCODE.FRIEND_NULL.getIndex(),
                    ERRORCODE.FRIEND_NULL.getReason());
        }
        addVisitHis(userBo.getId(),friendid);
        List<DynamicBo> msgBos = dynamicService.findOneFriendMsg(friendid, page, limit);
        List<DynamicVo> dynamicVos = new ArrayList<>();
        bo2vo(msgBos,dynamicVos, userBo);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicVos", dynamicVos);
        map.put("backPic", friend.getDynamicPic());
        map.put("headPic", friend.getHeadPictureName());
        map.put("signature", friend.getPersonalizedSignature());
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 获取好友动态数量
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(" 获取好友动态数量")
    @RequestMapping(value = "/one-dynamics-num", method = {RequestMethod.GET, RequestMethod.POST})
    public String friendsNum(String friendid, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        List<DynamicBo> msgBos = dynamicService.findOneFriendMsg(friendid, -1, 0);

//        DynamicNumBo numBo = dynamicService.findNumByUserid(friendid);
//        long total = 0L;
//        if (numBo != null) {
//            total = numBo.getNumber();
//        }

        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicNum", msgBos == null ? 0 : msgBos.size());
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 我的动态
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("我的动态")
    @RequestMapping(value = "/my-dynamics", method = {RequestMethod.GET, RequestMethod.POST})
    public String myDynamics(int page, int limit,
                              HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        List<DynamicBo> msgBos = dynamicService.findOneFriendMsg(userBo.getId(), page, limit);
        List<DynamicVo> dynamicVos = new ArrayList<>();
        bo2vo(msgBos,dynamicVos, userBo);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicVos", dynamicVos);
        map.put("backPic", userBo.getDynamicPic());
        map.put("headPic", userBo.getHeadPictureName());
        map.put("signature", userBo.getPersonalizedSignature());
        UserVisitBo userVisitBo = userService.findUserVisitFirst(userBo.getId(), 1);
        UserBaseVo show = new UserBaseVo();
        if (userVisitBo != null) {
            UserBo user = userService.getUser(userVisitBo.getVisitid());
            if (user != null) {
                BeanUtils.copyProperties(user, show);
            }
        }
        map.put("showUser", show);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 我的动态数量
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("我的动态数量")
    @RequestMapping(value = "/my-dynamics-num", method = {RequestMethod.GET, RequestMethod.POST})
    public String myDynamicsNum(HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
//        DynamicNumBo numBo = dynamicService.findNumByUserid(userBo.getId());
//        long total = 0L;
//        if (numBo != null) {
//            total = numBo.getNumber();
//        }
        List<DynamicBo> msgBos = dynamicService.findOneFriendMsg(userBo.getId(), -1, 0);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicNum", msgBos == null ? 0 : msgBos.size());
        return JSONObject.fromObject(map).toString();
    }


    /**
     *
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("不看他的动态")
    @RequestMapping(value = "/dynamic-not-see", method = {RequestMethod.GET, RequestMethod.POST})
    public String notSee(String friendid, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
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


    /**
     * 谁看过我的动态
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("谁看过我的动态")
    @RequestMapping(value = "/visit-my-dynamic", method = {RequestMethod.GET, RequestMethod.POST})
    public String visitMyDynamics(int page, int limit,
                             HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        List<UserVisitBo> visitBos = userService.visitToMeList(userBo.getId(), 1, page, limit);
        List<UserBaseVo> visitUsers = new LinkedList<>();
        for (UserVisitBo visitBo : visitBos) {
            UserBo user = userService.getUser(visitBo.getVisitid());
            if (user != null) {
                UserBaseVo baseVo = new UserBaseVo();
                BeanUtils.copyProperties(user, baseVo);
                visitUsers.add(baseVo);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("visitUserVos", visitUsers);
        return JSONObject.fromObject(map).toString();
    }


    /**
     * 我的动态
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("更新动态背景图片")
    @RequestMapping(value = "/update-backpic", method = {RequestMethod.GET, RequestMethod.POST})
    public String updateDynamicsPic(MultipartFile backPic,
                                  HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        if (backPic != null) {
            Long time = Calendar.getInstance().getTimeInMillis();
            String fileName = String.format("%s-%d-%s", userBo.getId(), time, backPic.getOriginalFilename());
            String path = CommonUtil.upload(backPic, Constant.DYNAMIC_PICTURE_PATH,
                    fileName, 0);
            userService.updateUserDynamicPic(userBo.getId(), path);
            userBo.setDynamicPic(path);
            request.getSession().setAttribute("userBo", userBo);
        }
        return Constant.COM_RESP;
    }


    /**
     * 访问记录添加
     * @param userid
     * @param friendid
     */
    @Async
    private void addVisitHis(String userid, String friendid){
        UserVisitBo visitBo = new UserVisitBo();
        visitBo.setVisitTime(new Date());
        visitBo.setVisitid(userid);
        visitBo.setOwnerid(friendid);
        visitBo.setType(1);
        userService.addUserVisit(visitBo);
    }

    
    private void bo2vo(List<DynamicBo> msgBos, List<DynamicVo> dynamicVos, UserBo userBo){
        for (DynamicBo msgBo : msgBos) {
            DynamicVo dynamicVo = new DynamicVo();
            BeanUtils.copyProperties(msgBo, dynamicVo);
            //vo中msgid是东西信息id；
            dynamicVo.setMsgid(msgBo.getId());
            //bo中的msgid是转发来源的id
            dynamicVo.setSourceid(msgBo.getMsgid());
            if (!userBo.getId().equals(msgBo.getCreateuid())) {
                UserBo user = userService.getUser(msgBo.getCreateuid());
                    dynamicVo.setUserPic(user.getHeadPictureName());
                    dynamicVo.setUserid(msgBo.getCreateuid());
                    FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userBo.getId(), msgBo
                            .getCreateuid());
                    if (friendsBo != null && !StringUtils.isEmpty(friendsBo.getBackname())) {
                        dynamicVo.setUserName(friendsBo.getBackname());
                    } else {
                        dynamicVo.setUserName(user.getUserName());
                    }
            } else {
                dynamicVo.setUserPic(userBo.getHeadPictureName());
                dynamicVo.setUserid(userBo.getId());
                dynamicVo.setUserName(userBo.getUserName());
            }
            int type = msgBo.getType();
            if (type == Constant.NOTE_TYPE) {
                dynamicVo.setCircleid(msgBo.getSourceid());
                CircleBo circleBo = circleService.selectById(msgBo.getSourceid());
                if (circleBo != null) {
                    dynamicVo.setCircleUserNum(circleBo.getTotal());
                    dynamicVo.setCircleNoteNum(circleBo.getNoteSize());
                    dynamicVo.setCircleName(circleBo.getName());
                } else {
                    dynamicVo.setCircleName(msgBo.getSourceName());
                }
            } else if (type == Constant.CIRCLE_TYPE) {
                CircleBo circleBo = circleService.selectById(msgBo.getMsgid());
                if (circleBo != null) {
                    dynamicVo.setCircleName(circleBo.getName());
                    dynamicVo.setCircleUserNum(circleBo.getTotal());
                    dynamicVo.setCircleNoteNum(circleBo.getNoteSize());
                } else {
                    dynamicVo.setCircleName(msgBo.getSourceName());
                }
            }
            dynamicVo.setTime(msgBo.getCreateTime());
            ThumbsupBo thumbsupBo = thumbsupService.findHaveOwenidAndVisitorid(msgBo.getMsgid(), userBo.getId());
            dynamicVo.setMyThumbsup(thumbsupBo != null);
            dynamicVos.add(dynamicVo);
        }
    }
}
