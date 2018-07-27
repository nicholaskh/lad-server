package com.lad.controller;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.lad.bo.ChatroomBo;
import com.lad.bo.ChatroomUserBo;
import com.lad.bo.CircleBo;
import com.lad.bo.CircleShowBo;
import com.lad.bo.CollectBo;
import com.lad.bo.CommentBo;
import com.lad.bo.DynamicBo;
import com.lad.bo.FriendsBo;
import com.lad.bo.PartyBo;
import com.lad.bo.PartyNoticeBo;
import com.lad.bo.PartyUserBo;
import com.lad.bo.ThumbsupBo;
import com.lad.bo.UserBo;
import com.lad.redis.RedisServer;
import com.lad.service.IChatroomService;
import com.lad.service.ICircleService;
import com.lad.service.ICollectService;
import com.lad.service.ICommentService;
import com.lad.service.IDynamicService;
import com.lad.service.IFriendsService;
import com.lad.service.IMessageService;
import com.lad.service.IPartyService;
import com.lad.service.IThumbsupService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.IMUtil;
import com.lad.util.JPushUtil;
import com.lad.util.MyException;
import com.lad.vo.CommentVo;
import com.lad.vo.PartyComment;
import com.lad.vo.PartyListVo;
import com.lad.vo.PartyUserDetail;
import com.lad.vo.PartyUserVo;
import com.lad.vo.PartyVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/7
 */
@Api(value = "PartyController", description = "圈子聚会相关接口")
@RestController
@RequestMapping("/party")
public class PartyController extends BaseContorller {

    private static Logger logger = LogManager.getLogger(PartyController.class);

    @Autowired
    private IPartyService partyService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICircleService circleService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IThumbsupService thumbsupService;
    
    @Autowired
    private RedisServer redisServer;

    @Autowired
    private IDynamicService dynamicService;

    @Autowired
    private IChatroomService chatroomService;

    @Autowired
    private ICollectService collectService;

    @Autowired
    private IFriendsService friendsService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private AsyncController asyncController;

    private String titlePush = "聚会通知";


    @ApiOperation("创建发布聚会")
    @PostMapping("/create")
    public String create(@RequestParam String partyJson, MultipartFile backPic,
                          MultipartFile[] photos, MultipartFile video,
                          HttpServletRequest request, HttpServletResponse response){

    	
    	org.slf4j.Logger logger2 = LoggerFactory.getLogger(PartyController.class);
    	
    	
    	
        logger.info("partyJson : {}",partyJson);
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        PartyBo partyBo = null;
        try {
            JSONObject jsonObject = JSONObject.fromObject(partyJson);
            partyBo = (PartyBo)JSONObject.toBean(jsonObject, PartyBo.class);
            
            logger2.error("手机a创建聚会时的坐标:"+Arrays.toString(partyBo.getPosition()));
            
        } catch (Exception e) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_ERROR.getIndex(),
                    ERRORCODE.PARTY_ERROR.getReason());
        }
        CircleBo circleBo = circleService.selectById(partyBo.getCircleid());
        if (circleBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(),
                    ERRORCODE.CIRCLE_IS_NULL.getReason());
        }
        String userId = userBo.getId();
        addPicVideo(userId, partyBo, backPic, photos, video);
        partyBo.setStatus(1);
        partyBo.setCreateuid(userId);
        LinkedList<String> partyUsers = partyBo.getUsers();
        
        partyUsers.add(userId);
        partyBo.setPartyUserNum(1);
        partyBo.setForward(0);

        ChatroomBo chatroomBo = new ChatroomBo();
        chatroomBo.setName(partyBo.getTitle());
        chatroomBo.setType(Constant.ROOM_MULIT);
        chatroomBo.setCreateuid(userBo.getId());
        chatroomBo.setMaster(userBo.getId());
        chatroomBo.setOpen(true);
        chatroomBo.setVerify(false);
        HashSet<String> users = chatroomBo.getUsers();
        users.add(userId);
        chatroomService.insert(chatroomBo);
        //第一个为返回结果信息，第二位term信息
        String result = IMUtil.subscribe(0, chatroomBo.getId(), userBo.getId());
        if (!result.equals(IMUtil.FINISH)) {
            chatroomService.remove(chatroomBo.getId());
            return result;
        }
        userService.updateChatrooms(userBo);
        addChatroomUser(chatroomService, userBo, chatroomBo.getId(), userBo.getUserName());
        partyBo.setChatroomid(chatroomBo.getId());
        partyService.insert(partyBo);
        chatroomService.addPartyChartroom(chatroomBo.getId(), partyBo.getId());
        HashSet<String> circleUsers = circleBo.getUsers();

//        addCircleShow(partyBo);
        String path = "/party/party-info.do?partyid=" + partyBo.getId();
        String content = String.format("“%s”发起了聚会【%s】，快去看看吧", userBo.getUserName(),
                partyBo.getTitle());
        if (circleUsers.size() > 0) {
            String[] userids = new String[circleUsers.size()];
            circleUsers.toArray(userids);
            JPushUtil.push(titlePush, content, path, userids);
            addMessage(messageService, path, content, titlePush, userId, userids);
        }
        if (circleBo.isOpen()) {
           asyncController.pushFriends(userId, content, path, circleUsers);
        }
        //用户等级
        userService.addUserLevel(userBo.getId(), 1, Constant.PARTY_TYPE, 0);
        //圈子热度
        updateCircleHot(circleService, redisServer, partyBo.getCircleid(), 1, Constant.CIRCLE_PARTY_VISIT);

        updateDynamicNums(userId, 1, dynamicService, redisServer);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("partyid", partyBo.getId());
        return JSONObject.fromObject(map).toString();
    }




    /**
     * 更新聚会信息
     * @param partyid
     * @param partyJson
     * @param backPic
     * @param photos
     * @param delPhotos
     * @param video
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("更新聚会信息")
    @PostMapping("/update")
    public String update(@RequestParam String partyid, String partyJson, String delPhotos,
                         MultipartFile backPic, MultipartFile[] photos, MultipartFile video,
                         HttpServletRequest request, HttpServletResponse response){

        logger.info("update partyJson : {}",partyJson);
        logger.info("update delPhotos : {}" , delPhotos);

        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        PartyBo partyBo = null;
        try {
            if (StringUtils.isNotEmpty(partyJson)) {
                JSONObject jsonObject = JSONObject.fromObject(partyJson);
                partyBo = (PartyBo)JSONObject.toBean(jsonObject, PartyBo.class);
            }
        } catch (Exception e) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_ERROR.getIndex(),
                    ERRORCODE.PARTY_ERROR.getReason());
        }
        PartyBo oldParty = partyService.findById(partyid);
        if (oldParty == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        if (partyBo == null){
            partyBo = oldParty;
        } else {
            copyOld(oldParty, partyBo);
            partyBo.setPhotos(oldParty.getPhotos());
            partyBo.setUsers(oldParty.getUsers());
        }
        String userId = userBo.getId();
        LinkedHashSet<String> photo = partyBo.getPhotos();
        if (StringUtils.isNotEmpty(delPhotos)) {
            String[] paths = CommonUtil.getIds(delPhotos);
            for (String url : paths) {
                if (photo.contains(url)){
                    photo.remove(url);
                }
            }
            partyBo.setPhotos(photo);
        }
        addPicVideo(userId, partyBo, backPic, photos, video);
        partyService.update(partyBo);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("partyid", partyBo.getId());
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 图片及视频文件添加
     * @param userId
     * @param partyBo
     * @param backPic
     * @param photos
     * @param video
     */
    private void addPicVideo(String userId, PartyBo partyBo,  MultipartFile backPic,
                             MultipartFile[] photos, MultipartFile video){
        if (photos != null) {
            LinkedHashSet<String> photo = new LinkedHashSet<>();
            for (MultipartFile file : photos) {
                Long time = Calendar.getInstance().getTimeInMillis();
                String fileName = String.format("%s-%d-%s", userId, time, file.getOriginalFilename());
                String path = CommonUtil.upload(file, Constant.PARTY_PICTURE_PATH,
                        fileName, 0);
                photo.add(path);
            }
            partyBo.setPhotos(photo);
        }
        if (video != null) {
            try {
                Long time = Calendar.getInstance().getTimeInMillis();
                String fileName = String.format("%s-%d-%s", userId, time, video.getOriginalFilename());
                logger.info("---- party file: {} ,  size: {}" , video.getOriginalFilename(), video.getSize());
                String[] paths = CommonUtil.uploadVedio(video, Constant.PARTY_PICTURE_PATH, fileName, 0);
                partyBo.setVideo(paths[0]);
                partyBo.setVideoPic(paths[1]);
            } catch (Exception e) {
                logger.error(e);
            }
        }
        if (backPic != null) {
            Long time = Calendar.getInstance().getTimeInMillis();
            String fileName = String.format("%s-%d-%s", userId, time, backPic.getOriginalFilename());
            String path =  CommonUtil.upload(backPic, Constant.PARTY_PICTURE_PATH, fileName, 0);
            partyBo.setBackPic(path);
        }
    }


    @ApiOperation("聚会详情信息")
    @PostMapping("/party-info")
    public String manageParty(@RequestParam String partyid,HttpServletRequest request, HttpServletResponse response){

        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        UserBo userBo = getUserLogin(request);
        String userid = userBo != null ? userBo.getId() : "";

        CircleBo circleBo = circleService.selectById(partyBo.getCircleid());
        if (circleBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(),
                    ERRORCODE.CIRCLE_IS_NULL.getReason());
        }

        PartyVo partyVo = new PartyVo();
        BeanUtils.copyProperties(partyBo, partyVo);
        if (partyBo.getStatus() != 3){
            int status = getPartyStatus(partyBo.getStartTime(), partyBo.getAppointment());
            //人数以报满
            if (status == 1 && partyBo.getUserLimit() <= partyBo.getPartyUserNum() &&
                    partyBo.getUserLimit() != 0) {
                if (partyBo.getStatus() != 2) {
                    updatePartyStatus(partyBo.getId(), 2);
                    partyBo.setStatus(2);
                }
            } else if (status != partyBo.getStatus()){
                partyBo.setStatus(status);
                updatePartyStatus(partyBo.getId(), status);
            }
        }
        List<PartyUserVo> partyUserVos = new ArrayList<>();
        int userAdd = 0;
        UserBo createBo = null;
        if (userBo != null && userBo.getId().equals(partyBo.getCreateuid())){
            createBo = userBo;
        } else {
            createBo = userService.getUser(partyBo.getCreateuid());
        }
        if (createBo != null) {
            PartyUserVo createVo = new PartyUserVo();
            partyUserBo2Vo(createBo, createVo);
            partyUserVos.add(createVo);
            partyVo.setCreater(createVo);
            userAdd ++;
        }
        partyVo.setPartyid(partyBo.getId());
        partyVo.setCircleName(circleBo.getName());
        partyVo.setCirclePic(circleBo.getHeadPicture());
        partyVo.setInCircle(circleBo.getUsers().contains(userid));


        LinkedList<String> users = partyBo.getUsers();
        int length = users.size() -1;

        for (int i = length; i >=0 ; i--) {
            String userStr = users.get(i);
            if (userStr.equals(partyBo.getCreateuid())) {
                continue;
            }
            if (userAdd > 10) {
                break;
            }
            UserBo user =  userService.getUser(userStr);
            if (user !=null) {
                PartyUserVo userVo = new PartyUserVo();
                partyUserBo2Vo(user, userVo);
                partyUserVos.add(userVo);
                userAdd ++;
            }
        }
        partyVo.setUsers(partyUserVos);
        partyVo.setCreate(partyBo.getCreateuid().equals(userid));

        if (StringUtils.isNotEmpty(userid)){
            PartyUserBo partyUserBo = partyService.findPartyUserIgnoreDel(partyid, userid);
            partyVo.setInParty(partyUserBo != null && partyUserBo.getDeleted() == 0);
            partyVo.setCollect(partyUserBo != null && partyUserBo.getCollectParty() == 1);
            List<CommentBo> commentBos = commentService.selectByTargetUser(partyid, userid, Constant.PARTY_TYPE);
            partyVo.setComment(commentBos != null && !commentBos.isEmpty());
        }
        partyVo.setUserNum(partyBo.getPartyUserNum());
        partyService.updateVisit(partyid);
        //圈子热度
        updateCircleHot(circleService, redisServer, partyBo.getCircleid(), 1, Constant.CIRCLE_PARTY_VISIT);

        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("partyVo", partyVo);
        return JSONObject.fromObject(map).toString();
    }

    private void partyUserBo2Vo(UserBo userBo, PartyUserVo userVo){
        userVo.setUserPic(userBo.getHeadPictureName());
        userVo.setUsername(userBo.getUserName());
        userVo.setUserid(userBo.getId());
    }

    /**
     * 报名聚会
     * @param partyid
     * @return
     */
    @ApiOperation("报名聚会")
    @PostMapping("/enroll-party")
    public String enrollParty(String partyid,String phone, String joinInfo, int userNum, double amount,
                            HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

        RLock lock = redisServer.getRLock(partyid+"partyUserLock");
        String chatroomid;
        PartyBo partyBo = null;
        boolean isMax = false;
        try {
            lock.lock(2, TimeUnit.SECONDS);
            partyBo = partyService.findById(partyid);
            if (partyBo == null) {
                return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                        ERRORCODE.PARTY_NULL.getReason());
            }
            if (partyBo.getStatus() != 1) {
                return CommonUtil.toErrorResult(ERRORCODE.PARTY_HAS_END.getIndex(),
                        ERRORCODE.PARTY_HAS_END.getReason());
            }
            int userTotal = partyBo.getPartyUserNum() + userNum;
            if (partyBo.getUserLimit() != 0 && userTotal > partyBo.getUserLimit()) {
                return CommonUtil.toErrorResult(ERRORCODE.PARTY_ENROLL_MAX.getIndex(),
                        ERRORCODE.PARTY_ENROLL_MAX.getReason());
            }
            isMax = userTotal == partyBo.getUserLimit();
            partyBo.setPartyUserNum(userTotal);
            chatroomid = partyBo.getChatroomid();
            LinkedList<String> users = partyBo.getUsers();
            if (!users.contains(userBo.getId())) {
                users.add(userBo.getId());
                partyService.updateUser(partyid, users, userTotal);
            } else {
                return CommonUtil.toErrorResult(ERRORCODE.PARTY_HAS_ADD.getIndex(),
                        ERRORCODE.PARTY_HAS_ADD.getReason());
            }
        } finally {
            lock.unlock();
        }
        String userid = userBo.getId();
        PartyUserBo partyUserBo = partyService.findPartyUserIgnoreDel(partyid, userid);
        if (partyUserBo == null) {
            partyUserBo = new PartyUserBo();
            partyUserBo.setAmount(amount);
            partyUserBo.setJoinInfo(joinInfo);
            partyUserBo.setJoinPhone(phone);
            partyUserBo.setUserid(userBo.getId());
            partyUserBo.setPartyid(partyid);
            partyUserBo.setUserNum(userNum);
            partyUserBo.setStatus(1);
            partyService.addParty(partyUserBo);
        } else {
            if (partyUserBo.getDeleted() == Constant.DELETED) {
                partyUserBo.setAmount(amount);
                partyUserBo.setJoinInfo(joinInfo);
                partyUserBo.setJoinPhone(phone);
                partyUserBo.setUserNum(userNum);
                partyUserBo.setStatus(1);
                partyUserBo.setDeleted(Constant.ACTIVITY);
                partyService.updatePartyUser(partyUserBo);
            }
        }
        if (StringUtils.isNotEmpty(chatroomid)) {
            ChatroomBo chatroomBo = chatroomService.get(chatroomid);
            if (chatroomBo != null) {
                LinkedHashSet<String> chatroomUsers = chatroomBo.getUsers();
                if (!chatroomUsers.contains(userid)){
                    //第一个为返回结果信息，第二位term信息
                    String result = IMUtil.subscribe(1, chatroomid, userid);
                    if (!result.equals(IMUtil.FINISH)) {
                        return result;
                    }
                    HashSet<String> chatroom = userBo.getChatrooms();
                    //个人聊天室中没有当前聊天室，则添加到个人的聊天室
                    if (!chatroom.contains(chatroomid)) {
                        chatroom.add(chatroomid);
                        userBo.setChatrooms(chatroom);
                        userService.updateChatrooms(userBo);
                    }
                    chatroomUsers.add(userid);
                    chatroomBo.setUsers(chatroomUsers);
                    chatroomService.updateUsers(chatroomBo);
                }
            }
        }
        ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userid, chatroomid);
        if (chatroomUserBo == null){
            chatroomUserBo = new ChatroomUserBo();
            chatroomUserBo.setChatroomid(chatroomid);
            chatroomUserBo.setUserid(userid);
            chatroomUserBo.setUsername(userBo.getUserName());
            chatroomService.insertUser(chatroomUserBo);
        }
        if (isMax) {
            updatePartyStatus(partyid, 2);
        }
        String path = String.format("/party/enroll-detail.do?partyid=%s&userid=%s", partyid, userid);
        String content = String.format("“%s”报名了您发起的聚会【%s】，请尽快与他沟通参与事宜", userBo.getUserName(),
                partyBo.getTitle());
        JPushUtil.push(titlePush, content, path,  partyBo.getCreateuid());
        addMessage(messageService, path, content, titlePush, partyBo.getCreateuid());
        return Constant.COM_RESP;
    }

    /**
     * 获取报名人员列表
     * @param partyid
     * @return
     */
    @ApiOperation("获取聚会报名人员信息列表")
    @PostMapping("/get-users")
    public String enrollUsers(String partyid, HttpServletRequest request, HttpServletResponse response){

        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        List<PartyUserBo> partyUserBos = partyService.findPartyUser(partyid, 1);
        List<PartyUserVo> partyUserVos = new ArrayList<>();
        UserBo creater = userService.getUser(partyBo.getCreateuid());
        if (creater != null) {
            PartyUserVo userVo = new PartyUserVo();
            partyUserBo2Vo(creater, userVo);
            partyUserVos.add(userVo);
        }
        for (PartyUserBo partyUserBo : partyUserBos) {
            UserBo user =  userService.getUser(partyUserBo.getUserid());
            if (user !=null) {
                PartyUserVo userVo = new PartyUserVo();
                partyUserBo2Vo(user, userVo);
                partyUserVos.add(userVo);
            } else {
                partyService.outParty(partyUserBo.getId());
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("partyUserVos", partyUserVos);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 管理聚会
     * @param partyid
     * @return
     */
    @PostMapping("/manage-enroll")
    @Deprecated
    public String getEnroll(@RequestParam String partyid,
                           HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("partyVo", "");
        return JSONObject.fromObject(map).toString();
    }


    /**
     * 获取我发起的聚会
     * @return
     */
    @ApiOperation("我发布的聚会列表信息")
    @PostMapping("/my-partys")
    public String getMyPartys(int page, int limit, HttpServletRequest request, HttpServletResponse response){

        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        List<PartyBo> partyBos = partyService.findByCreate(userBo.getId(), page, limit);
        System.out.println(JSON.toJSONString(partyBos));
        List<PartyListVo> partyListVos = new ArrayList<>();
        bo2listVo(partyBos, partyListVos, userBo);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("partyListVos", partyListVos);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 获取我参与的聚会
     * @return
     */
    @ApiOperation("我参与聚会的列表信息")
    @PostMapping("/join-partys")
    public String getJoinPartys(int page, int limit, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        List<PartyBo> partyBos = partyService.findByMyJoin(userBo.getId(), page, limit);
        List<PartyListVo> partyListVos = new ArrayList<>();
        for(PartyBo partyBo : partyBos) {
            //判断用户是否已经删除这个信息
            PartyUserBo partyUserBo = partyService.findPartyUser(partyBo.getId(), userBo.getId());
            if (partyUserBo != null && partyUserBo.getUserDelete() == Constant.DELETED) {
                continue;
            }
            PartyNoticeBo partyNoticeBo = partyService.findPartyNotice(partyBo.getId());
            PartyListVo listVo = new PartyListVo();
            BeanUtils.copyProperties(partyBo, listVo);
            if (partyBo.getStatus() != 3) {
                int status = getPartyStatus(partyBo.getStartTime(), partyBo.getAppointment());
                //人数以报满
                if (status == 1 && partyBo.getUserLimit() <= partyBo.getPartyUserNum() && partyBo.getUserLimit()
                        !=0 ) {
                    if (partyBo.getStatus() != 2) {
                        updatePartyStatus(partyBo.getId(), 2);
                        listVo.setStatus(2);
                    }
                } else if (status != partyBo.getStatus()){
                    listVo.setStatus(status);
                    updatePartyStatus(partyBo.getId(), status);
                }
            }
            listVo.setJoin(true);
            listVo.setPartyid(partyBo.getId());
            listVo.setHasNotice(partyNoticeBo != null);
            listVo.setUserNum(partyBo.getPartyUserNum());
            partyListVos.add(listVo);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("partyListVos", partyListVos);
        return JSONObject.fromObject(map).toString();
    }

    private void bo2listVo(List<PartyBo> partyBos, List<PartyListVo> partyListVos, UserBo userBo){
        for(PartyBo partyBo : partyBos) {
            PartyListVo listVo = new PartyListVo();
            if (partyBo.getForward() == 1) {
                PartyBo forward = partyService.findById(partyBo.getSourcePartyid());
                if (forward == null) {
                    continue;
                }
                BeanUtils.copyProperties(forward, listVo);
                addValues(listVo, forward);
                listVo.setPartyid(forward.getId());
                listVo.setUserNum(forward.getPartyUserNum());
                listVo.setSourceCirid(forward.getCircleid());
                CircleBo circleBo = circleService.selectById(forward.getCircleid());
                if (circleBo != null) {
                    listVo.setSourceCirName(circleBo.getName());
                }
                String createid = partyBo.getCreateuid();
                listVo.setFromUserid(createid);
                String name = "";
                if (null != userBo ) {
                    listVo.setJoin(forward.getUsers().contains(userBo.getId()));
                    if (!userBo.getId().equals(createid)) {
                        FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userBo.getId(), createid);
                        if (friendsBo != null && StringUtils.isNotEmpty(friendsBo.getBackname())) {
                            name = friendsBo.getBackname();
                        }
                        userBo = userService.getUser(createid);
                    }
                } else {
                    userBo = userService.getUser(createid);
                }
                listVo.setFromUserName("".equals(name) ? userBo.getUserName() : name);
                listVo.setFromUserPic(userBo.getHeadPictureName());
                listVo.setFromUserSex(userBo.getSex());
                listVo.setFromUserSign(userBo.getPersonalizedSignature());
                listVo.setFromUserBirth(userBo.getBirthDay());
                listVo.setForward(true);
                listVo.setView(partyBo.getView());
            } else {
                PartyNoticeBo partyNoticeBo = partyService.findPartyNotice(partyBo.getId());
                BeanUtils.copyProperties(partyBo, listVo);
                addValues(listVo, partyBo);
                listVo.setPartyid(partyBo.getId());
                listVo.setHasNotice(partyNoticeBo != null);
                listVo.setUserNum(partyBo.getPartyUserNum());
                listVo.setJoin(partyBo.getUsers().contains(userBo.getId()));
            }
            partyListVos.add(listVo);
        }
    }

    private void addValues(PartyListVo listVo, PartyBo partyBo){
        LinkedHashSet<String> startTimes = partyBo.getStartTime();
        PartyNoticeBo partyNoticeBo = partyService.findPartyNotice(partyBo.getId());
        listVo.setHasNotice(partyNoticeBo != null);
        BeanUtils.copyProperties(partyBo, listVo);
        if (partyBo.getStatus() != 3) {
            int status = getPartyStatus(startTimes, partyBo.getAppointment());
            //人数以报满
            if (status == 1 && partyBo.getUserLimit() <= partyBo.getPartyUserNum() && partyBo.getUserLimit()
                    !=0) {
                if (partyBo.getStatus() != 2) {
                    updatePartyStatus(partyBo.getId(), 2);
                    listVo.setStatus(2);
                }
            } else if (status != partyBo.getStatus()){
                listVo.setStatus(status);
                updatePartyStatus(partyBo.getId(), status);
            }
        }
        listVo.setPartyid(partyBo.getId());
        listVo.setUserNum(partyBo.getPartyUserNum());
    }


    private void updatePartyStatus(String partyid, int status){
        partyService.updatePartyStatus(partyid, status);
        //聚会结束,删除所有临时聊天
        if (status == 3) {
            chatroomService.deleteTempChat(partyid, Constant.ROOM_SINGLE);
        }
    }

    /**
     * 发起群聊
     * @param partyid
     * @return
     */
    @ApiOperation("聚会发起群聊")
    @PostMapping("/launch-talk")
    public String launchTalk(@RequestParam String partyid,
                            HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        if(!partyBo.getCircleid().equals(userBo.getId())) {
            return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
                    ERRORCODE.CIRCLE_NOT_MASTER.getReason());
        }
        if(StringUtils.isNotEmpty(partyBo.getChatroomid())) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_TALK_EXIST.getIndex(),
                    ERRORCODE.PARTY_TALK_EXIST.getReason());
        }

        ChatroomBo chatroomBo = new ChatroomBo();
        chatroomBo.setName(partyBo.getTitle());
        chatroomBo.setMaster(userBo.getId());
        chatroomBo.setCreateuid(userBo.getId());
        chatroomBo.setType(Constant.ROOM_MULIT);
        chatroomBo.setOpen(true);
        chatroomBo.setVerify(false);
        chatroomService.insert(chatroomBo);

        String chatroomid = chatroomBo.getId();
        
        String[] useridArr = (String[]) partyBo.getUsers().toArray();
        //第一个为返回结果信息，第二位term信息
        String result = IMUtil.subscribe(0, chatroomid, useridArr);
        if (!result.equals(IMUtil.FINISH)) {
            chatroomService.remove(chatroomid);
            return result;
        }
        userService.updateChatrooms(userBo);
        updateUserChatroom(chatroomBo, useridArr);
        partyService.updateChatroom(partyid, chatroomid);
        return Constant.COM_RESP;
    }

    /**
     * 更新用户聊天室列表
     */
    private void updateUserChatroom(ChatroomBo chatroomBo, String[] useridArr){
        LinkedHashSet<String> users = chatroomBo.getUsers();
        for (String userid : useridArr) {
            UserBo user = userService.getUser(userid);
            if (null == user) {
                continue;
            }
            HashSet<String> chatroom = user.getChatrooms();
            //个人聊天室中没有当前聊天室，则添加到个人的聊天室
            if (!chatroom.contains(chatroomBo.getId())) {
                chatroom.add(chatroomBo.getId());
                user.setChatrooms(chatroom);
                userService.updateChatrooms(user);
            }
            users.add(userid);
            addChatroomUser(chatroomService, user, chatroomBo.getId(), user.getUserName());
        }
        chatroomBo.setUsers(users);
        chatroomService.updateUsers(chatroomBo);
    }


    /**
     * 改聚会是否已经发起了群聊
     * @param partyid
     * @return
     */
    @ApiOperation("聚会是否已经存在群聊")
    @PostMapping("/has-chatroom")
    public String hasTalk(@RequestParam String partyid,
                             HttpServletRequest request, HttpServletResponse response){

        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("chatroomid", partyBo.getChatroomid());
        return JSONObject.fromObject(map).toString();
    }


    /**
     * 收藏聚会
     * @param partyid
     * @return
     */
    @ApiOperation("收藏聚会")
    @PostMapping("/collect-party")
    public String collectParty(@RequestParam String partyid,
                             HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        CollectBo collectBo = collectService.findByUseridAndTargetid(userBo.getId(), partyid);
        if (collectBo == null) {
            collectBo = new CollectBo();
            collectBo.setUserid(userBo.getId());
            collectBo.setTitle(partyBo.getTitle());
            collectBo.setType(Constant.COLLET_URL);
            collectBo.setSub_type(Constant.PARTY_TYPE);
            collectBo.setTargetPic(partyBo.getBackPic());
            CircleBo circleBo = circleService.selectById(partyBo.getCircleid());
            if (circleBo != null) {
                collectBo.setSource(circleBo.getName());
                collectBo.setSourceType(5);
                collectBo.setSourceid(circleBo.getId());
            }
            collectBo.setTargetid(partyid);
            collectService.insert(collectBo);
        } 
        PartyUserBo partyUserBo = partyService.findPartyUserIgnoreDel(partyid, userBo.getId());
        if (partyUserBo == null) {
            partyUserBo = new PartyUserBo();
            partyUserBo.setUserid(userBo.getId());
            partyUserBo.setPartyid(partyid);
            partyUserBo.setStatus(0);
            partyUserBo.setCollectParty(1);
            partyService.addParty(partyUserBo);
        } else {
            partyService.collectParty(partyid, userBo.getId(), true);
        }
        asyncController.updatePartyCollectNum(partyid, 1);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("col-time", CommonUtil.time2str(collectBo.getCreateTime()));
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 取消收藏
     * @param partyid
     * @return
     */
    @ApiOperation("取消聚会收藏")
    @PostMapping("/del-collect")
    public String deleteCollectParty(@RequestParam String partyid,
                                     HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        CollectBo collectBo = collectService.findByUseridAndTargetid(userBo.getId(), partyid);
        if (collectBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.COLLECT_IS_NULL.getIndex(),
                    ERRORCODE.COLLECT_IS_NULL.getReason());
        } else {
            if(collectBo.getDeleted() == 0) {
                collectService.updateCollectDelete(collectBo.getId(), Constant.DELETED);
                asyncController.updatePartyCollectNum(partyid, -1);
            }
            partyService.collectParty(partyid, userBo.getId(), false);
        }
        return Constant.COM_RESP;
    }

    /**
     * 删除聚会
     * @param partyid
     * @return
     */
    @ApiOperation("删除聚会")
    @PostMapping("/delete-party")
    public String delelteParty(@RequestParam String partyid,
                               HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        CircleBo circleBo = circleService.selectById(partyBo.getCircleid());
        if (!userBo.getId().equals(partyBo.getCreateuid()) &&
                !circleBo.getMasters().contains(userBo.getId())) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NO_AUTH.getIndex(),
                    ERRORCODE.PARTY_NO_AUTH.getReason());
        }
        partyService.delete(partyid);
        deleteShouw(partyid);
        partyService.deleteMulitByaPartyid(partyBo.getId());
        chatroomService.deleteTempChat(partyid, Constant.ROOM_SINGLE);
        return Constant.COM_RESP;
    }

    /**
     * 添加评论
     * @param partyComment
     * @return
     */
    @ApiOperation("聚会添加评论")
    @PostMapping("/add-comment")
    public String addComment(String partyComment,MultipartFile[] photos,
                             HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

        PartyComment comment = null;
        try {
            JSONObject jsonObject = JSONObject.fromObject(partyComment);
            comment = (PartyComment)JSONObject.toBean(jsonObject, PartyComment.class);
        } catch (Exception e) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_ERROR.getIndex(),
                    ERRORCODE.PARTY_ERROR.getReason());
        }

        PartyBo partyBo = partyService.findById(comment.getPartyid());
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }

        List<CommentBo> commentBos = commentService.selectByTargetUser(comment.getPartyid(), userBo.getId(), Constant.PARTY_TYPE);
        if (commentBos != null && !commentBos.isEmpty()) {
            boolean hasComment = false;
            for (CommentBo commentBo : commentBos) {
                if (StringUtils.isEmpty(commentBo.getParentid())){
                    hasComment = true;
                    break;
                }
            }
            if (hasComment && StringUtils.isEmpty(comment.getParentid())) {
                return CommonUtil.toErrorResult(ERRORCODE.PARTY_HAS_COMMENT.getIndex(),
                        ERRORCODE.PARTY_HAS_COMMENT.getReason());
            }
        }

        CommentBo commentBo = new CommentBo();
        commentBo.setCreateuid(userBo.getId());
        commentBo.setContent(comment.getContent());
        commentBo.setType(Constant.PARTY_TYPE);
        commentBo.setTargetid(comment.getPartyid());
        commentBo.setUserName(userBo.getUserName());
        commentBo.setParentid(comment.getParentid());

        String userId = userBo.getId();
        if (photos != null) {
            LinkedHashSet<String> photo = new LinkedHashSet<>();
            for (MultipartFile file : photos) {
                Long time = Calendar.getInstance().getTimeInMillis();
                String fileName = String.format("%s-%d-%s", userId, time, file.getOriginalFilename());
                String path = CommonUtil.upload(file, Constant.PARTY_PICTURE_PATH,
                        fileName, 0);
                photo.add(path);
            }
            commentBo.setPhotos(photo);
        }
        commentService.insert(commentBo);

        String path = "/party/party-info.do?partyid=" + comment.getPartyid();
        String content = "有人刚刚评论了你的聚会，快去看看吧!";
        JPushUtil.pushMessage(titlePush, content, path,  partyBo.getCreateuid());
        addMessage(messageService, path, content, titlePush, partyBo.getCreateuid());
        if (!StringUtils.isEmpty(comment.getParentid())) {
            CommentBo commentBo1 = commentService.findById(comment.getParentid());
            if (commentBo1 != null) {
                content = "有人刚刚回复了你的评论，快去看看吧!";
                JPushUtil.pushMessage(titlePush, content, path,  commentBo1.getCreateuid());
                addMessage(messageService, path, content, titlePush, partyBo.getCreateuid());
            }
        }
        //圈子热度
        updateCircleHot(circleService, redisServer, partyBo.getCircleid(), 1, Constant.CIRCLE_PARTY_VISIT);
        if (comment.isSync()) {
            DynamicBo dynamicBo = new DynamicBo();
            dynamicBo.setTitle(partyBo.getTitle());
            dynamicBo.setMsgid(partyBo.getId());
            dynamicBo.setCreateuid(userBo.getId());
            dynamicBo.setOwner(partyBo.getCreateuid());
            if (partyBo.getPhotos() != null) {
                dynamicBo.setPhotos(new LinkedHashSet<>(partyBo.getPhotos()));
            }
            dynamicBo.setVideo(partyBo.getVideo());
            dynamicBo.setVideoPic(partyBo.getVideoPic());
            dynamicBo.setType(Constant.NOTE_TYPE);
            CircleBo circleBo = circleService.selectById(partyBo.getCircleid());
            if (circleBo != null) {
                dynamicBo.setSourceName(circleBo.getName());
            }
            dynamicService.addDynamic(dynamicBo);
            partyService.updateShare(partyBo.getId(), 1);
            updateDynamicNums(userId, 1, dynamicService, redisServer);
        }
        asyncController.updateRedStar(userBo, partyBo, new Date());
        return Constant.COM_RESP;
    }



    /**
     * 获取评论
     * @param partyid
     * @return
     */
    @ApiOperation("获取聚会评论信息")
    @PostMapping("/get-comments")
    public String getComment(String partyid,
                             HttpServletRequest request, HttpServletResponse response){

        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }

        UserBo user = getUserLogin(request);

        List<CommentBo> commentBos = commentService.selectByTargetUser(partyid,"", Constant.PARTY_TYPE);

        List<CommentVo> commentVos = new ArrayList<>();
        for (CommentBo commentBo : commentBos) {
            CommentVo vo = new CommentVo();
            UserBo userBo = userService.getUser(commentBo.getCreateuid());
            if (userBo != null) {
                vo.setUserSex(userBo.getSex());
                vo.setUserHeadPic(userBo.getHeadPictureName());
                vo.setUserLevel(userBo.getLevel());
                vo.setUserName(userBo.getUserName());
                vo.setUserBirth(userBo.getBirthDay());
                vo.setUserid(userBo.getId());
            }
            if (!StringUtils.isEmpty(commentBo.getParentid())) {
                CommentBo parent = commentService.findById(commentBo.getParentid());
                vo.setParentUserName(parent.getUserName());
                vo.setParentUserid(parent.getCreateuid());
            }
            vo.setContent(commentBo.getContent());
            vo.setCommentId(commentBo.getId());
            vo.setCreateTime(commentBo.getCreateTime());
            vo.setPhotos(commentBo.getPhotos());
            if (null != user) {
                //判断当前用户是否点赞
                ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(commentBo.getId(), user.getId());
                vo.setMyThumbsup(thumbsupBo != null);
            }
            vo.setThumpsubCount(commentBo.getThumpsubNum());
            commentVos.add(vo);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("commentVos", commentVos);
        return JSONObject.fromObject(map).toString();
    }


    /**
     * 获取圈子所有聚会
     * @return
     */
    @ApiOperation("获取圈子所有聚会")
    @PostMapping("/all-partys")
    public String getAllPartys(String circleid, int page, int limit, HttpServletRequest request, HttpServletResponse
            response){
        List<PartyBo> partyBos = partyService.findByCircleid(circleid, page, limit);
        List<PartyListVo> partyListVos = new ArrayList<>();
        bo2listVo(partyBos, partyListVos, getUserLogin(request));
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("partyListVos", partyListVos);
        return JSONObject.fromObject(map).toString();
    }


    /**
     * 取消报名
     * @return
     */
    @ApiOperation("取消聚会报名")
    @PostMapping("/cancel-enroll")
    public String cancelPartys(String partyid,HttpServletRequest request, HttpServletResponse
            response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        RLock lock = redisServer.getRLock(partyid + "partyUserLock");
        PartyBo partyBo = null;
        int userTotal = 0;
        try {
            lock.lock(3, TimeUnit.SECONDS);
            partyBo = partyService.findById(partyid);
            if (partyBo == null) {
                return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                        ERRORCODE.PARTY_NULL.getReason());
            }
            LinkedList<String> users = partyBo.getUsers();
            if (users.contains(userBo.getId())) {
                users.remove(userBo.getId());
                PartyUserBo partyUserBo = partyService.findPartyUser(partyid, userBo.getId());
                userTotal = partyBo.getPartyUserNum();
                if (partyUserBo != null) {
                    userTotal = userTotal - partyUserBo.getUserNum();
                } else {
                    userTotal--;
                }
                userTotal = userTotal < 0 ? 0 : userTotal;
                partyService.updateUser(partyid, users, userTotal);
            }
        } finally {
            lock.unlock();
        }
        if (userTotal < partyBo.getUserLimit()) {
            int status = getPartyStatus(partyBo.getStartTime(), partyBo.getAppointment());
            if (status != partyBo.getStatus()) {
                //人员饱和之外不用更新
                if (!(status == 1 && partyBo.getStatus() == 2 &&
                        partyBo.getUserLimit() <= partyBo.getPartyUserNum())) {
                    partyService.updatePartyStatus(partyid, status);
                }
            }
        }
        partyService.outParty(partyBo.getId(), userBo.getId());
        return Constant.COM_RESP;
    }


    /**
     * 删除我参与的的聚会
     * @return
     */
    @ApiOperation("删除我参与的聚会信息")
    @PostMapping("/delete-join-party")
    public String delMyJoinPartys(String partyid, HttpServletRequest request, HttpServletResponse
            response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        partyService.outParty(partyid, userBo.getId());
        return Constant.COM_RESP;
    }

    /**
     * 用户聚会报名详情
     * @return
     */
    @ApiOperation("用户报名聚会详情信息")
    @PostMapping("/enroll-detail")
    public String enrollDetail(String partyid, String userid, HttpServletRequest request, HttpServletResponse
            response){
        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        PartyUserBo partyUserBo = partyService.findPartyUser(partyid, userid);
        if (partyUserBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_USER_NULL.getIndex(),
                    ERRORCODE.PARTY_USER_NULL.getReason());
        }
        PartyUserDetail userDetail = new PartyUserDetail();
        BeanUtils.copyProperties(partyUserBo, userDetail);
        UserBo userBo = userService.getUser(partyUserBo.getUserid());
        if (userBo != null) {
            userDetail.setUsername(userBo.getUserName());
            userDetail.setUserPic(userBo.getHeadPictureName());
        }
        userDetail.setAddrType(partyBo.getAddrType());
        userDetail.setAddrInfo(partyBo.getAddrInfo());
        userDetail.setStartTime(partyBo.getStartTime());
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("partyUser", userDetail);
        return JSONObject.fromObject(map).toString();
    }


    /**
     * 评论点赞
     * @return
     */
    @ApiOperation("评论点赞或取消点赞")
    @PostMapping("/comment-thumbsup")
    public String commentThumbsup(String commentId, int type, HttpServletRequest request, HttpServletResponse
            response) {
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        ThumbsupBo thumbsupBo = thumbsupService.findHaveOwenidAndVisitorid(commentId, userBo.getId());
        int num = 0;
        if (type == 0) {
            if (null == thumbsupBo) {
                thumbsupBo = new ThumbsupBo();
                thumbsupBo.setType(Constant.PARTY_COM_TYPE);
                thumbsupBo.setOwner_id(commentId);
                thumbsupBo.setImage(userBo.getHeadPictureName());
                thumbsupBo.setVisitor_id(userBo.getId());
                thumbsupBo.setCreateuid(userBo.getId());
                thumbsupService.insert(thumbsupBo);
            } else {
                if (thumbsupBo.getDeleted() == Constant.DELETED) {
                    thumbsupService.udateDeleteById(thumbsupBo.getId());
                }
            }
            num = 1;
        } else if (type == 1) {
            if (null != thumbsupBo && thumbsupBo.getDeleted() == Constant.ACTIVITY) {
                thumbsupService.deleteById(thumbsupBo.getId());
                num = -1;
            }
        } else {
            return CommonUtil.toErrorResult(ERRORCODE.TYPE_ERROR.getIndex(),
                    ERRORCODE.TYPE_ERROR.getReason());
        }
        RLock lock = redisServer.getRLock(commentId);
        try {
            lock.lock(1, TimeUnit.SECONDS);
            commentService.updateThumpsubNum(commentId, num);
        } finally {
            lock.unlock();
        }
        if (type == 1){
            CommentBo commentBo = commentService.findById(commentId);
            if (commentBo != null) {
                String path = "/party/party-info.do?partyid=" + commentBo.getTargetid();
                JPushUtil.pushMessage(titlePush, "有人刚刚赞了你的聚会，快去看看吧!", path,  commentBo.getCreateuid());
                addMessage(messageService, path, "有人刚刚赞了你的聚会，快去看看吧!", titlePush,
                        commentBo.getCreateuid());
            }
        }
        return Constant.COM_RESP;
    }


    /**
     * 举报聚会
     * @param partyid
     * @return
     */
    @ApiOperation("举报聚会")
    @PostMapping("/tip-off-party")
    public String tipOffParty(@RequestParam String partyid,
                               HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }

        return "";
    }



    /**
     * 复制参数 ,将old参数都复制到new上，如果new不存在，则复制
     * @param oldParty
     * @param newParty
     */
    private void copyOld(PartyBo oldParty, PartyBo newParty) {
        // 获取属性
        try {
            BeanInfo sourceBean = Introspector.getBeanInfo(oldParty.getClass(), java.lang.Object.class);
            PropertyDescriptor[] sourceProperty = sourceBean.getPropertyDescriptors();
            BeanInfo destBean = Introspector.getBeanInfo(newParty.getClass(), java.lang.Object.class);
            PropertyDescriptor[] destProperty = destBean.getPropertyDescriptors();
            for (int i = 0; i < sourceProperty.length; i++) {
                for (int j = 0; j < destProperty.length; j++) {
                    PropertyDescriptor descriptors = destProperty[j];
                    if (sourceProperty[i].getName().equals(descriptors.getName())) {
                        Object value = descriptors.getReadMethod().invoke(newParty);
                        //如果new的为空 或者
                        if (value == null) {
                            descriptors.getWriteMethod().invoke(newParty, sourceProperty[i].getReadMethod().invoke(oldParty));
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @ApiOperation("与聚会报名人员进行临时聊天")
    @PostMapping("/temp-chatroom")
    public String tempChatroom(String partyid, String friendid,
                               HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        UserBo friend = userService.getUser(friendid);
        if (friend == null) {
            return CommonUtil.toErrorResult(ERRORCODE.USER_NULL.getIndex(),
                    ERRORCODE.USER_NULL.getReason());
        }
        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_HAS_END.getIndex(),
                    ERRORCODE.PARTY_HAS_END.getReason());
        }

        String userid = userBo.getId();
        ChatroomBo chatroomBo = chatroomService.selectByUserIdAndFriendid(userid, friendid);
        if (chatroomBo == null) {
            chatroomBo = chatroomService.selectByUserIdAndFriendid(friendid, userid);
        }
        //单人聊天也存在免打扰信息
        if (chatroomBo == null) {
            chatroomBo = new ChatroomBo();
            chatroomBo.setName(friend.getUserName());
            chatroomBo.setUserid(userid);
            chatroomBo.setFriendid(friendid);
            chatroomBo.setType(Constant.ROOM_SINGLE);
            chatroomBo.setTargetid(partyid);
            chatroomBo.setCreateuid(userid);
            chatroomService.insert(chatroomBo);
            String res = IMUtil.subscribe(0,chatroomBo.getId(), userid, friendid);
            if (!res.equals(IMUtil.FINISH)) {
                return res;
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("channelId", chatroomBo.getId());
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 发送通知
     * @param partyid
     * @param content
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("发送聚会通知")
    @PostMapping("/send-notice")
    public String sendNotice(String partyid, String content,
                               HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(), ERRORCODE.PARTY_NULL.getReason());
        }
        if (!partyBo.getCreateuid().equals(userBo.getId())) {
            return CommonUtil.toErrorResult(ERRORCODE.NOTE_NOT_MASTER.getIndex(), ERRORCODE.NOTE_NOT_MASTER.getReason());
        }
        LinkedList users = partyBo.getUsers();
        PartyNoticeBo noticeBo = new PartyNoticeBo();
        noticeBo.setPartyid(partyid);
        noticeBo.setContent(content);
        noticeBo.setUsers(users);
        noticeBo.setCreateuid(userBo.getId());
        partyService.addPartyNotice(noticeBo);

        if (users.size() > 0) {
            String path = "/party/party-notice.do?partyid=" + partyid;
            String[] userids = new String[users.size()];
            users.toArray(userids);
            JPushUtil.push(titlePush, content, path, userids);
            addMessage(messageService, path, content, titlePush, userBo.getId(), userids);
        }
        return Constant.COM_RESP;
    }

    /**
     * 通知详情
     * @param noticeid
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("聚会通知信息详情")
    @PostMapping("/notice")
    public String getNotice(String noticeid,
            HttpServletRequest request, HttpServletResponse response) {

        PartyNoticeBo partyNoticeBo = partyService.findNoticeById(noticeid);
        if (partyNoticeBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NOTICE_NULL.getIndex(),
                    ERRORCODE.PARTY_NOTICE_NULL.getReason());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("noticeVo", partyNoticeBo);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 聚会的所有通知
     * @param partyid
     * @param page
     * @param limit
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("聚会所有通知信息列表")
    @PostMapping("/party-notice")
    public String partyNotice(String partyid, int page, int limit,
                            HttpServletRequest request, HttpServletResponse response) {
        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(), ERRORCODE.PARTY_NULL.getReason());
        }
        List<PartyNoticeBo> noticeBos = partyService.findNoticeByPartyid(partyid, page, limit);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("noticeVos", noticeBos);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 我收到的通知
     * @param page
     * @param limit
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("我收到的聚会通知信息列表")
    @PostMapping("/get-my-notices")
    public String getPartyNotices(int page, int limit,
                              HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        List<PartyNoticeBo> noticeBos = partyService.findNoticeByPartyid(userBo.getId(), page, limit);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("noticeVos", noticeBos);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 转发到我的动态
     */
    @ApiOperation("将聚会转发到我的动态")
    @ApiImplicitParams({@ApiImplicitParam(name = "partyid", value = "聚会id", required = true,paramType = "query",
            dataType = "string"),
            @ApiImplicitParam(name = "view", value = "转发的评论", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "landmark", value = "地标", paramType = "query", dataType = "string")})
    @PostMapping("/forward-dynamic")
    public String forwardDynamic(String partyid, String view, String landmark, HttpServletRequest request,
                                 HttpServletResponse response) {
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        PartyBo partyBo = partyService.findById(partyid);
        if (null == partyBo) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        DynamicBo dynamicBo = new DynamicBo();
        dynamicBo.setTitle(partyBo.getTitle());
        dynamicBo.setView(view);
        dynamicBo.setMsgid(partyid);
        dynamicBo.setCreateuid(userBo.getId());
        dynamicBo.setOwner(partyBo.getCreateuid());
        if (partyBo.getPhotos() != null) {
            dynamicBo.setPhotos(new LinkedHashSet<>(partyBo.getPhotos()));
        }
        dynamicBo.setVideo(partyBo.getVideo());
        dynamicBo.setVideoPic(partyBo.getVideoPic());
        dynamicBo.setType(Constant.NOTE_TYPE);
        dynamicBo.setLandmark(landmark);
        CircleBo circleBo = circleService.selectById(partyBo.getCircleid());
        if (circleBo != null) {
            dynamicBo.setSourceName(circleBo.getName());
        }
        dynamicService.addDynamic(dynamicBo);
        partyService.updateShare(partyid, 1);
        updateDynamicNums(userBo.getId(), 1,dynamicService, redisServer);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicid", dynamicBo.getId());
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 转发到我的动态
     */
    @ApiOperation("将聚会转发到指定圈子")
    @ApiImplicitParams({@ApiImplicitParam(name = "partyid", value = "聚会id", required = true, paramType = "query",
            dataType = "string"),
            @ApiImplicitParam(name = "circleid", value = "要转发的圈子id",  required = true,paramType = "query",
                    dataType = "string"),
            @ApiImplicitParam(name = "view", value = "转发的评论", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "landmark", value = "地标", paramType = "query", dataType = "string")})
    @PostMapping("/forward-circle")
    public String forwardCircle(String partyid, String circleid, String view, String landmark, HttpServletRequest
            request, HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);

        if (userBo == null){
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        PartyBo partyBo = partyService.findById(partyid);
        if (null == partyBo) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        CircleBo circleBo = circleService.selectById(circleid);
        if (null == circleBo) {
            return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(),
                    ERRORCODE.CIRCLE_IS_NULL.getReason());
        }
        PartyBo forward = new PartyBo();
        forward.setForward(1);
        forward.setSourcePartyid(partyid);
        forward.setView(view);
        forward.setCreateuid(userBo.getId());
        forward.setCircleid(circleid);
        partyService.insert(forward);
//        addCircleShow(partyBo);
        //用户等级
        userService.addUserLevel(userBo.getId(), 1, Constant.PARTY_TYPE, 0);
        //圈子热度
        updateCircleHot(circleService, redisServer, partyBo.getCircleid(), 1, Constant.CIRCLE_PARTY_VISIT);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("partyid", partyBo.getId());
        return JSONObject.fromObject(map).toString();
    }



    @ApiOperation("附近聚会列表,默认10千米范围")
    @ApiImplicitParams({@ApiImplicitParam(name = "px", value = "当前人位置经度", required = true, paramType = "query",
            dataType = "double"),
            @ApiImplicitParam(name = "py", value = "当前人位置纬度",  required = true,paramType = "query",
                    dataType = "double"),
            @ApiImplicitParam(name = "limit", value = "显示条数",  required = true,paramType = "query",
                    dataType = "int")})
    @PostMapping("/near-partys")
    public String nearPeopel(double px, double py, int limit,int page, HttpServletRequest request, HttpServletResponse
            response) {
        double[] position = new double[]{px, py};
        
        org.slf4j.Logger logger = LoggerFactory.getLogger(PartyController.class);
        logger.error("调用附近聚会时的坐标:"+Arrays.toString(position));
        //未登录情况
        UserBo userBo = getUserLogin(request);
        String userid = userBo != null ? userBo.getId() : "";
        GeoResults<PartyBo> partyBos = partyService.findNearParty(position, 10000, limit,page);
        List<PartyListVo> listVos = new LinkedList<>();
        DecimalFormat df = new DecimalFormat("###.00");
        for(GeoResult<PartyBo> result : partyBos) {
            PartyBo partyBo = result.getContent();
            logger.error("数据库查询结果第一手资料:"+JSON.toJSONString(partyBo));
            //判断用户是否已经删除这个信息
            PartyNoticeBo partyNoticeBo = partyService.findPartyNotice(partyBo.getId());
            PartyListVo listVo = new PartyListVo();
            BeanUtils.copyProperties(partyBo, listVo);
            if (partyBo.getStatus() != 3) {
                int status = getPartyStatus(partyBo.getStartTime(), partyBo.getAppointment());
                //人数以报满
                if (status == 1 && partyBo.getUserLimit() <= partyBo.getPartyUserNum() && partyBo.getUserLimit()
                        !=0 ) {
                    if (partyBo.getStatus() != 2) {
                        updatePartyStatus(partyBo.getId(), 2);
                        listVo.setStatus(2);
                    }
                } else if (status != partyBo.getStatus()){
                    listVo.setStatus(status);
                    updatePartyStatus(partyBo.getId(), status);
                }
            }
            listVo.setJoin(partyBo.getUsers().contains(userid));
            double dis = Double.parseDouble(df.format(result.getDistance().getValue()));
            listVo.setDistance(dis);
            listVo.setPartyid(partyBo.getId());
            listVo.setHasNotice(partyNoticeBo != null);
            listVo.setUserNum(partyBo.getPartyUserNum());
            listVos.add(listVo);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("partyListVos", listVos);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 需要和聚会展示最新信息
     * @param partyBo
     */
    private void addCircleShow(PartyBo partyBo){
        CircleShowBo circleShowBo = new CircleShowBo();
        circleShowBo.setCircleid(partyBo.getCircleid());
        circleShowBo.setTargetid(partyBo.getId());
        circleShowBo.setType(1);
        circleShowBo.setCreateTime(partyBo.getCreateTime());
        circleService.addCircleShow(circleShowBo);
    }

    /**
     * 删除展示信息
     * @param partyid
     */
    private void deleteShouw(String partyid){
        circleService.deleteShow(partyid);
    }

}
