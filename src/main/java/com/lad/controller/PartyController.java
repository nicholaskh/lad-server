package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.*;
import com.lad.vo.PartyListVo;
import com.lad.vo.PartyUserVo;
import com.lad.vo.PartyVo;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
 * Time:2017/9/7
 */
@Controller
@RequestMapping("/party")
public class PartyController extends BaseContorller {

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


    @RequestMapping("/create")
    @ResponseBody
    public String create(@RequestParam String partyJson,
                          @RequestParam("backPic") MultipartFile backPic,
                          @RequestParam("photos") MultipartFile[] photos,
                          @RequestParam("video") MultipartFile video,
                          HttpServletRequest request, HttpServletResponse response){

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
            String fileName = userId + "-" + time + "-" + video.getOriginalFilename();
            System.out.println("----file: " + video.getOriginalFilename() + ",  size: " + video.getSize());
            String[] paths = CommonUtil.uploadVedio(video, Constant.PARTY_PICTURE_PATH, fileName, 0);
            partyBo.setVideo(paths[0]);
            partyBo.setVideoPic(paths[1]);
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
    public String update(@RequestParam String partyJson,
                         @RequestParam String partyid,
                         @RequestParam("backPic") MultipartFile backPic,
                         @RequestParam("images") MultipartFile[] images,
                         @RequestParam("video") MultipartFile video,
                         HttpServletRequest request, HttpServletResponse response){

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
        if (commentBos != null && commentBos.isEmpty()) {
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
            String[] result = IMUtil.subscribe("", chatroomid, term, userid);
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
            HashSet<String> set = chatroomBo.getUsers();
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
            return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
                    ERRORCODE.CIRCLE_NOT_MASTER.getReason());
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("partyVo", "");
        return JSONObject.fromObject(map).toString();
    }

}
