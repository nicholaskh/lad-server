package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.*;
import com.lad.vo.CommentVo;
import com.lad.vo.PartyListVo;
import com.lad.vo.PartyUserVo;
import com.lad.vo.PartyVo;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
 * Time:2017/9/7
 */
@Controller
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
    private IIMTermService iMTermService;

    @Autowired
    private ICollectService collectService;


    @RequestMapping("/create")
    @ResponseBody
    public String create(@RequestParam String partyJson, MultipartFile backPic,
                          MultipartFile[] photos, MultipartFile video,
                          HttpServletRequest request, HttpServletResponse response){

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
        Long time = Calendar.getInstance().getTimeInMillis();
        if (photos != null) {
            LinkedHashSet<String> photo = new LinkedHashSet<>();
            for (MultipartFile file : photos) {
                String fileName = userId + "-" + time + "-" + file.getOriginalFilename();
                String path = CommonUtil.upload(file, Constant.PARTY_PICTURE_PATH,
                        fileName, 0);
                photo.add(path);
            }
            partyBo.setPhotos(photo);
        }
        if (video != null) {
            try {
                String fileName = userId + "-" + time + "-" + video.getOriginalFilename();
                logger.info("---- party file: {} ,  size: {}" , video.getOriginalFilename(), video.getSize());
                String[] paths = CommonUtil.uploadVedio(video, Constant.PARTY_PICTURE_PATH, fileName, 0);
                partyBo.setVideo(paths[0]);
                partyBo.setVideoPic(paths[1]);
            } catch (Exception e) {
                logger.error(e);
            }
        }
        if (backPic != null) {
            String fileName = userId + "-" + time + "-" + backPic.getOriginalFilename();
            String path =  CommonUtil.upload(backPic, Constant.PARTY_PICTURE_PATH, fileName, 0);
            partyBo.setBackPic(path);
        }
        partyBo.setStatus(0);
        partyBo.setCreateuid(userId);
        partyService.insert(partyBo);

        //用户等级
        userService.addUserLevel(userBo.getId(), 1, Constant.PARTY_TYPE);
        //圈子热度
        updateCircleHot(circleService, redisServer, partyBo.getCircleid(), 1, Constant.CIRCLE_PARTY);
        //动态信息表
        addDynamicMsgs(userId, partyBo.getId(), Constant.PARTY_TYPE, dynamicService);
        updateDynamicNums(userId, 1, dynamicService, redisServer);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("partyid", partyBo.getId());
        return JSONObject.fromObject(map).toString();
    }

    @RequestMapping("/update")
    @ResponseBody
    public String update(@RequestParam String partyJson, @RequestParam String partyid,
                         MultipartFile backPic, MultipartFile[] images, MultipartFile video,
                         HttpServletRequest request, HttpServletResponse response){

        logger.info("update partyJson : {}",partyJson);

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
        } catch (Exception e) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_ERROR.getIndex(),
                    ERRORCODE.PARTY_ERROR.getReason());
        }
        PartyBo oldParty = partyService.findById(partyid);



        String userId = userBo.getId();
        Long time = Calendar.getInstance().getTimeInMillis();
        if (images != null) {
            LinkedHashSet<String> photos = oldParty.getPhotos();
            for (MultipartFile file : images) {
                String fileName = userId + "-" + time + "-" + file.getOriginalFilename();
                String path = CommonUtil.upload(file, Constant.PARTY_PICTURE_PATH,
                        fileName, 0);
                photos.add(path);
            }
            partyBo.setPhotos(photos);
        }
        if (video != null) {
            String fileName = userId + "-" + time + "-" + video.getOriginalFilename();
            String[] paths = CommonUtil.uploadVedio(video, Constant.PARTY_PICTURE_PATH, fileName, 0);
            oldParty.setVideo(paths[0]);
            oldParty.setVideoPic(paths[1]);
        }
        if (backPic != null) {
            String fileName = userId + "-" + time + "-" + backPic.getOriginalFilename();
            String path =  CommonUtil.upload(backPic, Constant.PARTY_PICTURE_PATH, fileName, 0);
            oldParty.setBackPic(path);
        }
        partyBo.setCreateuid(userId);
        partyService.insert(partyBo);


        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("partyid", partyBo.getId());
        return JSONObject.fromObject(map).toString();
    }


    @RequestMapping("/party-info")
    @ResponseBody
    public String manageParty(@RequestParam String partyid,
                         HttpServletRequest request, HttpServletResponse response){

        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

        CircleBo circleBo = circleService.selectById(partyBo.getCircleid());
        if (circleBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(),
                    ERRORCODE.CIRCLE_IS_NULL.getReason());
        }

        List<PartyUserVo> partyUserVos = new ArrayList<>();
        LinkedList<String> users = partyBo.getUsers();
        int length = users.size() -1;
        int userAdd = 0;

        boolean inCircle = circleBo.getUsers().contains(userBo.getId());
        boolean isAdd = users.contains(userBo.getId());
        
        for (int i = length; i >=0 ; i--) {
            UserBo user =  userService.getUser(users.get(i));
            if (userAdd > 10) {
                break;
            }
            if (user !=null) {
                PartyUserVo userVo = new PartyUserVo();
                partyUserBo2Vo(user, userVo);
                partyUserVos.add(userVo);
                userAdd ++;
            }
        }
        PartyVo partyVo = new PartyVo();
        BeanUtils.copyProperties(partyBo, partyVo);
        partyVo.setPartyid(partyBo.getId());
        partyVo.setCircleName(circleBo.getName());
        partyVo.setCirclePic(circleBo.getHeadPicture());
        partyVo.setInCircle(inCircle);
        partyVo.setInParty(isAdd);
        partyVo.setUsers(partyUserVos);
        if (partyBo.getCreateuid().equals(userBo.getId())) {
            partyVo.setCreate(true);
        }
        UserBo createBo = userService.getUser(partyBo.getCreateuid());
        if (createBo != null) {
            PartyUserVo createVo = new PartyUserVo();
            partyUserBo2Vo(createBo, createVo);
            partyVo.setCreater(createVo);
        }
        List<CommentBo> commentBos = commentService.selectByTargetUser(partyid, userBo.getId(), Constant.PARTY_TYPE);
        if (commentBos != null && !commentBos.isEmpty()) {
            partyVo.setComment(true);
        }
        partyVo.setUserNum(partyBo.getUsers().size());
        partyService.updateVisit(partyid);

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
    @RequestMapping("/enroll-party")
    @ResponseBody
    public String enrollParty(String partyid,String phone, String joinInfo, int userNum, double amount,
                            HttpServletRequest request, HttpServletResponse response){
        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        if (partyBo.getUsers().size() >= partyBo.getUserLimit()) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_USER_MAX.getIndex(),
                    ERRORCODE.PARTY_USER_MAX.getReason());
        }
        String userid = userBo.getId();
        PartyUserBo partyUserBo = new PartyUserBo();
        partyUserBo.setAmount(amount);
        partyUserBo.setJoinInfo(joinInfo);
        partyUserBo.setJoinPhone(phone);
        partyUserBo.setUserid(userBo.getId());
        partyUserBo.setPartyid(partyid);
        partyUserBo.setUserNum(userNum);
        partyUserBo.setStatus(0);

        partyService.addParty(partyUserBo);
        LinkedList<String> users = partyBo.getUsers();
        users.add(userBo.getId());
        partyService.updateUser(partyid, users);
        String chatroomid = partyBo.getChatroomid();
        if (StringUtils.isNotEmpty(chatroomid)) {
            ChatroomBo chatroomBo = chatroomService.get(chatroomid);

            IMTermBo imTermBo = iMTermService.selectByUserid(userid);
            String term = "";
            if (imTermBo != null) {
                term = imTermBo.getTerm();
            }
            //第一个为返回结果信息，第二位term信息
            String[] result = IMUtil.subscribe(1, chatroomid, term, userid);
            if (!result[0].equals(IMUtil.FINISH)) {
                return result[0];
            }
            if (imTermBo == null) {
                imTermBo = new IMTermBo();
                imTermBo.setTerm(term);
                imTermBo.setUserid(userid);
                iMTermService.insert(imTermBo);
            } else {
                iMTermService.updateByUserid(userBo.getId(), term);
            }
            LinkedHashSet<String> set = chatroomBo.getUsers();
            HashSet<String> chatroom = userBo.getChatrooms();
            //个人聊天室中没有当前聊天室，则添加到个人的聊天室
            if (!chatroom.contains(chatroomid)) {
                chatroom.add(chatroomid);
                userBo.setChatrooms(chatroom);
                userService.updateChatrooms(userBo);
            }
            set.add(userid);
            chatroomBo.setUsers(set);
            chatroomService.updateUsers(chatroomBo);
        }
        return Constant.COM_RESP;
    }

    /**
     * 获取报名人员列表
     * @param partyid
     * @return
     */
    @RequestMapping("/get-users")
    @ResponseBody
    public String enrollUsers(String partyid, HttpServletRequest request, HttpServletResponse response){

        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        if (!userBo.getId().equals(partyBo.getCreateuid())) {
            return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
                    ERRORCODE.CIRCLE_NOT_MASTER.getReason());
        }
        List<PartyUserVo> partyUserVos = new ArrayList<>();
        LinkedList<String> users = partyBo.getUsers();
        int length = users.size() -1;
        for (int i = length; i >=0 ; i--) {
            UserBo user =  userService.getUser(users.get(i));
            if (user !=null) {
                PartyUserVo userVo = new PartyUserVo();
                partyUserBo2Vo(user, userVo);
                partyUserVos.add(userVo);
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
    @RequestMapping("/manage-enroll")
    @ResponseBody
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
    @RequestMapping("/my-partys")
    @ResponseBody
    public String getMyPartys(int page, int limit, HttpServletRequest request, HttpServletResponse response){

        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        List<PartyBo> partyBos = partyService.findByCreate(userBo.getId(), page, limit);
        List<PartyListVo> partyListVos = new ArrayList<>();
        bo2listVo(partyBos, partyListVos);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("partyListVos", partyListVos);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 获取我参与的聚会
     * @return
     */
    @RequestMapping("/join-partys")
    @ResponseBody
    public String getJoinPartys(int page, int limit, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        List<PartyBo> partyBos = partyService.findByMyJoin(userBo.getId(), page, limit);
        List<PartyListVo> partyListVos = new ArrayList<>();
        bo2listVo(partyBos, partyListVos);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("partyListVos", partyListVos);
        return JSONObject.fromObject(map).toString();
    }

    private void bo2listVo(List<PartyBo> partyBos, List<PartyListVo> partyListVos){
        for(PartyBo partyBo : partyBos) {
            PartyListVo listVo = new PartyListVo();
            BeanUtils.copyProperties(partyBo, listVo);
            listVo.setPartyid(partyBo.getId());
            listVo.setUserNum(partyBo.getUsers().size());
            partyListVos.add(listVo);
        }
    }

    /**
     * 发起群聊
     * @param partyid
     * @return
     */
    @RequestMapping("/launch-talk")
    @ResponseBody
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
        
        IMTermBo imTermBo = iMTermService.selectByUserid(userBo.getId());
        String term = "";
        if (imTermBo != null) {
            term = imTermBo.getTerm();
        } else {
            imTermBo = new IMTermBo();
            imTermBo.setTerm(term);
            imTermBo.setUserid(userBo.getId());
            iMTermService.insert(imTermBo);
        }
        String[] useridArr = (String[]) partyBo.getUsers().toArray();
        //第一个为返回结果信息，第二位term信息
        String[] result = IMUtil.subscribe(0, chatroomid, term, useridArr);
        if (!result[0].equals(IMUtil.FINISH)) {
            chatroomService.remove(chatroomid);
            return result[0];
        }
        iMTermService.updateByUserid(userBo.getId(), result[1]);
        updateUserChatroom(chatroomBo, useridArr, result[1]);
        partyService.updateChatroom(partyid, chatroomid);
        return Constant.COM_RESP;
    }

    /**
     * 更新用户聊天室列表
     * @param term
     */
    @Async
    private void updateUserChatroom(ChatroomBo chatroomBo, String[] useridArr, String term){

        LinkedHashSet<String> users = chatroomBo.getUsers();
        for (String userid : useridArr) {
            UserBo user = userService.getUser(userid);
            if (null == user) {
                continue;
            }
            IMTermBo imTermBo = iMTermService.selectByUserid(userid);
            if (imTermBo == null) {
                imTermBo = new IMTermBo();
                imTermBo.setTerm(term);
                imTermBo.setUserid(userid);
                iMTermService.insert(imTermBo);
            } else {
                iMTermService.updateByUserid(userid, term);
            }
            HashSet<String> chatroom = user.getChatrooms();
            //个人聊天室中没有当前聊天室，则添加到个人的聊天室
            if (!chatroom.contains(chatroomBo.getId())) {
                chatroom.add(chatroomBo.getId());
                user.setChatrooms(chatroom);
                userService.updateChatrooms(user);
            }
            users.add(userid);
        }
        chatroomBo.setUsers(users);
        chatroomService.updateUsers(chatroomBo);
    }


    /**
     * 改聚会是否已经发起了群聊
     * @param partyid
     * @return
     */
    @RequestMapping("/has-chatroom")
    @ResponseBody
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
     * 收藏群聊
     * @param partyid
     * @return
     */
    @RequestMapping("/collect-party")
    @ResponseBody
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

        CollectBo collectBo = new CollectBo();
        collectBo.setUserid(userBo.getId());
        collectBo.setTitle(partyBo.getTitle());
        collectBo.setType(Constant.COLLET_URL);
        collectBo.setSub_type(Constant.PARTY_TYPE);
        collectBo.setTargetid(partyid);
        collectService.insert(collectBo);
        partyService.updateCollect(partyid, 1);

        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("col-time", CommonUtil.time2str(collectBo.getCreateTime()));
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 收藏群聊
     * @param partyid
     * @return
     */
    @RequestMapping("/delete-party")
    @ResponseBody
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
        if (!userBo.getId().equals(partyBo.getCreateuid())) {
            return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
                    ERRORCODE.CIRCLE_NOT_MASTER.getReason());
        }
        partyService.delete(partyid);
        return Constant.COM_RESP;
    }

    /**
     * 收藏群聊
     * @param partyid
     * @return
     */
    @RequestMapping("/add-comment")
    @ResponseBody
    public String addComment(String partyid, String content, boolean isSync,
                             MultipartFile[] photos, MultipartFile video,
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

        List<CommentBo> commentBos = commentService.selectByTargetUser(partyid, userBo.getId(), Constant.PARTY_TYPE);
        if (commentBos != null && !commentBos.isEmpty()) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_HAS_COMMENT.getIndex(),
                    ERRORCODE.PARTY_HAS_COMMENT.getReason());
        }

        CommentBo commentBo = new CommentBo();
        commentBo.setCreateuid(userBo.getId());
        commentBo.setContent(content);
        commentBo.setType(Constant.PARTY_TYPE);
        commentBo.setTargetid(partyid);
        commentBo.setUserName(userBo.getUserName());

        String userId = userBo.getId();
        Long time = Calendar.getInstance().getTimeInMillis();
        if (photos != null) {
            LinkedHashSet<String> photo = new LinkedHashSet<>();
            for (MultipartFile file : photos) {
                String fileName = userId + "-" + time + "-" + file.getOriginalFilename();
                String path = CommonUtil.upload(file, Constant.PARTY_PICTURE_PATH,
                        fileName, 0);
                photo.add(path);
            }
            commentBo.setPhotos(photo);
        }
        if (video != null) {
            String fileName = userId + "-" + time + "-" + video.getOriginalFilename();
            String[] paths = CommonUtil.uploadVedio(video, Constant.PARTY_PICTURE_PATH, fileName, 0);
            commentBo.setVideo(paths[0]);
            commentBo.setVideoPic(paths[1]);
        }
        commentService.insert(commentBo);
        if (isSync) {
            //动态信息表
            addDynamicMsgs(userId, partyBo.getId(), Constant.PARTY_COM_TYPE, dynamicService);
            updateDynamicNums(userId, 1, dynamicService, redisServer);
        }
        updateRedStar(userBo, partyBo, new Date());
        return Constant.COM_RESP;
    }

    /**
     * 更新红人信息
     * @param userBo
     * @param partyBo
     * @param currentDate
     */
    @Async
    private void updateRedStar(UserBo userBo, PartyBo partyBo, Date currentDate){
        String circleid = partyBo.getCircleid();
        RedstarBo redstarBo = commentService.findRedstarBo(userBo.getId(), circleid);
        int curretWeekNo = CommonUtil.getWeekOfYear(currentDate);
        int year = CommonUtil.getYear(currentDate);
        if (redstarBo == null) {
            redstarBo = new RedstarBo();
            redstarBo.setUserid(userBo.getId());
            redstarBo.setCommentTotal((long) 1);
            redstarBo.setCommentWeek((long) 1);
            redstarBo.setWeekNo(curretWeekNo);
            redstarBo.setCircleid(circleid);
            redstarBo.setYear(year);
            commentService.insertRedstar(redstarBo);
        }
        boolean isNotSelf = !userBo.getId().equals(partyBo.getCreateuid());
        boolean isNoteUserCurrWeek = true;
        //如果帖子作者不是自己
        if (isNotSelf) {
            //帖子作者没有红人数据信息，则添加
            RedstarBo noteRedstarBo = commentService.findRedstarBo(partyBo.getCreateuid(), circleid);
            if (noteRedstarBo == null) {
                redstarBo = new RedstarBo();
                redstarBo.setUserid(userBo.getId());
                redstarBo.setCommentTotal((long) 1);
                redstarBo.setCommentWeek((long) 1);
                redstarBo.setWeekNo(curretWeekNo);
                redstarBo.setCircleid(partyBo.getCircleid());
                redstarBo.setYear(year);
                commentService.insertRedstar(noteRedstarBo);
            } else {
                //判断帖子作者周榜是不是当前周，是则添加数据，不是则更新周榜数据
                isNoteUserCurrWeek = (year == noteRedstarBo.getYear() && curretWeekNo == noteRedstarBo.getWeekNo());
            }
        }
        //判断自己周榜是不是同一周，是则添加数据，不是则更新周榜数据
        boolean isCurrentWeek = (year == redstarBo.getYear() && curretWeekNo == redstarBo.getWeekNo());
        //更新自己或他人红人评论数量，需要加锁，保证数据准确
        RLock lock = redisServer.getRLock(Constant.COMOMENT_LOCK);
        try {
            lock.lock(5, TimeUnit.SECONDS);
            //更新自己的红人信息
            if (isCurrentWeek) {
                commentService.addRadstarCount(userBo.getId(), circleid);
            } else {
                commentService.updateRedWeekByUser(userBo.getId(), curretWeekNo, year);
            }
            if (isNotSelf) {
                //更新聚会作者的红人信息
                if (isNoteUserCurrWeek) {
                    commentService.addRadstarCount(partyBo.getCreateuid(), circleid);
                } else {
                    commentService.updateRedWeekByUser(partyBo.getCreateuid(), curretWeekNo, year);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 收藏群聊
     * @param partyid
     * @return
     */
    @RequestMapping("/get-comments")
    @ResponseBody
    public String getComment(String partyid,
                             HttpServletRequest request, HttpServletResponse response){

        PartyBo partyBo = partyService.findById(partyid);
        if (partyBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.PARTY_NULL.getIndex(),
                    ERRORCODE.PARTY_NULL.getReason());
        }
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
            vo.setContent(commentBo.getContent());
            vo.setCommentId(commentBo.getId());
            vo.setCreateTime(commentBo.getCreateTime());
            vo.setPhotos(commentBo.getPhotos());
            vo.setVideo(commentBo.getVideo());
            vo.setVideoPic(commentBo.getVideoPic());
            commentVos.add(vo);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("commentVos", commentVos);
        return JSONObject.fromObject(map).toString();
    }
}
