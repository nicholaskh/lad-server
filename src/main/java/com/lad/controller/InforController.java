package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.scrapybo.BroadcastBo;
import com.lad.scrapybo.InforBo;
import com.lad.scrapybo.SecurityBo;
import com.lad.scrapybo.VideoBo;
import com.lad.service.ICommentService;
import com.lad.service.IInforService;
import com.lad.service.IThumbsupService;
import com.lad.service.IUserService;
import com.lad.util.*;
import com.lad.vo.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;
import org.redisson.api.RMapCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述： 资讯接口
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/7/29
 */
@Controller
@RequestMapping("infor")
@CrossOrigin
public class InforController extends BaseContorller {

    private static Logger logger = RootLogger.getLogger(InforController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IThumbsupService thumbsupService;
    @Autowired
    private RedisServer redisServer;
    
    @Autowired
    private IInforService inforService;

    @RequestMapping("/init-cache")
    @ResponseBody
    public String initCache(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();

        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);

        logger.info(cache.get("securityTypes"));

        List<InforBo> inforBos = inforService.findAllGroups();
        int size =  inforBos.size();
        HashSet<String> groupTypes = new LinkedHashSet<>();
        for (int i = 0; i< size; i++) {
            //聚合查询后，分类名称值被放置到id上了
            groupTypes.add(inforBos.get(i).getClassName());
        }

        List<SecurityBo> securityBos = inforService.findSecurityTypes();

        HashSet<String> securityTypes = new LinkedHashSet<>();
        for (SecurityBo securityBo : securityBos) {
            securityTypes.add(securityBo.getId());
        }

        List<BroadcastBo> broadcastBos = inforService.selectBroadGroups();
        HashSet<String> broadTypes = new LinkedHashSet<>();
        for (BroadcastBo broadcastBo : broadcastBos) {
            broadTypes.add(broadcastBo.getId());
        }

        List<VideoBo> videoBos = inforService.selectVdeoGroups();
        HashSet<String> videoTypes = new LinkedHashSet<>();
        for (VideoBo videoBo : videoBos) {
            videoTypes.add(videoBo.getId());
        }
        cache.clear();
        cache.put("securityTypes", securityTypes, 0, TimeUnit.MINUTES);
        cache.put("healthTypes", groupTypes, 0, TimeUnit.MINUTES);
        cache.put("radioTypes", broadTypes, 0, TimeUnit.MINUTES);
        cache.put("videoTypes", videoTypes, 0, TimeUnit.MINUTES);
        map.put("healthTypes", groupTypes);
        map.put("securityTypes", securityTypes);
        map.put("videoTypes", videoTypes);
        map.put("radioTypes", broadTypes);
        return JSONObject.fromObject(map).toString();
    }


    @RequestMapping("/group-types")
    @ResponseBody
    public String inforGroups(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ret", 0);
        boolean isGetType = false;
        if (!session.isNew() && session.getAttribute("isLogin") != null) {
            UserBo userBo = (UserBo) session.getAttribute("userBo");
            InforSubscriptionBo mySub = inforService.findMySubs(userBo.getId());
            if (mySub != null) {
                map.put("healthTypes", mySub.getSubscriptions());
                map.put("securityTypes", mySub.getSecuritys());
                map.put("radioTypes", mySub.getRadios());
                map.put("videoTypes", mySub.getVideos());
                isGetType  = true;
            }
        }
        if (!isGetType) {
            RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
            if (cache.containsKey("healthTypes")) {
                Object groupTypes = cache.get("healthTypes");
                Object securityTypes = cache.get("securityTypes");
                Object radioTypes = cache.get("radioTypes");
                Object videoTypes = cache.get("videoTypes");
                map.put("healthTypes", groupTypes);
                map.put("securityTypes", securityTypes);
                map.put("radioTypes", radioTypes);
                map.put("videoTypes", videoTypes);
            } else {
                List<InforBo> inforBos = inforService.findAllGroups();
                int size =  inforBos.size();
                HashSet<String> groupTypes = new LinkedHashSet<>();
                for (int i = 0; i< size; i++) {
                    //聚合查询后，分类名称值被放置到id上了
                    groupTypes.add(inforBos.get(i).getClassName());
                }
                List<SecurityBo> securityBos = inforService.findSecurityTypes();
                HashSet<String> securityTypes = new LinkedHashSet<>();
                for (SecurityBo securityBo : securityBos) {
                    securityTypes.add(securityBo.getId());
                }
                List<BroadcastBo> broadcastBos = inforService.selectBroadGroups();
                HashSet<String> broadTypes = new LinkedHashSet<>();
                for (BroadcastBo broadcastBo : broadcastBos) {
                    broadTypes.add(broadcastBo.getId());
                }
                List<VideoBo> videoBos = inforService.selectVdeoGroups();
                HashSet<String> videoTypes = new LinkedHashSet<>();
                for (VideoBo videoBo : videoBos) {
                    videoTypes.add(videoBo.getId());
                }
                cache.put("radioTypes", broadTypes, 0, TimeUnit.MINUTES);
                cache.put("securityTypes", securityTypes, 0, TimeUnit.MINUTES);
                cache.put("healthTypes", groupTypes, 0, TimeUnit.MINUTES);
                cache.put("videoTypes", videoTypes, 0, TimeUnit.MINUTES);
                map.put("healthTypes", groupTypes);
                map.put("securityTypes", securityTypes);
                map.put("videoTypes", videoTypes);
                map.put("radioTypes", broadTypes);
            }
        }
        return JSONObject.fromObject(map).toString();
    }

    @RequestMapping("/group-infors")
    @ResponseBody
    public String groupInfors(@RequestParam String groupName,
                              @RequestParam(required = false)String inforTime,
                              @RequestParam int limit,
                              HttpServletRequest request, HttpServletResponse response){
        List<InforBo> inforBos = inforService.findClassInfos(groupName, inforTime, limit);
        LinkedList<InforVo> inforVos = new LinkedList<>();
        for (InforBo inforBo : inforBos) {
            InforVo inforVo = new InforVo();
            inforVo.setInforid(inforBo.getId());
            inforVo.setClassName(inforBo.getClassName());
            inforVo.setImageUrls(inforBo.getImageUrls());
            inforVo.setSource(inforBo.getSource());
            inforVo.setTitle(inforBo.getTitle());
            inforVo.setTime(inforBo.getTime());
            inforVo.setSourceUrl(inforBo.getSourceUrl());
            //list不需要获取正文
//            inforVo.setText(inforBo.getText());
            inforVos.add(inforVo);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVoList", inforVos);
        return JSONObject.fromObject(map).toString();
    }


    @RequestMapping("/radio-list")
    @ResponseBody
    public String radioList(String module, int page,  int limit,
                              HttpServletRequest request, HttpServletResponse response){
        List<BroadcastBo> broadcastBos = inforService.findBroadByPage(module, page, limit);
        List<BroadcastVo> vos = new ArrayList<>();
        for (BroadcastBo bo : broadcastBos) {
            BroadcastVo broadcastVo = new BroadcastVo();
            BeanUtils.copyProperties(bo, broadcastVo);
            broadcastVo.setInforid(bo.getId());
            vos.add(broadcastVo);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("radioList", vos);
        return JSONObject.fromObject(map).toString();
    }

    @RequestMapping("/radio-infor")
    @ResponseBody
    public String radioInfors(String radioid, HttpServletRequest request, HttpServletResponse response){
        BroadcastBo broadcastBo = inforService.findBroadById(radioid);
        BroadcastVo broadcastVo = null;
        if (broadcastBo != null) {
            broadcastVo = new BroadcastVo();
            BeanUtils.copyProperties(broadcastBo, broadcastVo);
            broadcastVo.setInforid(broadcastBo.getId());
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("radio", broadcastVo);
        return JSONObject.fromObject(map).toString();
    }


    @RequestMapping("/video-list")
    @ResponseBody
    public String videoInfors(String module, int page,  int limit,
                              HttpServletRequest request, HttpServletResponse response){
        List<VideoBo> videoBos = inforService.findVideoByPage(module, page, limit);
        List<VideoVo> videoVos = new ArrayList<>();
        for (VideoBo bo : videoBos) {
            VideoVo videoVo = new VideoVo();
            BeanUtils.copyProperties(bo, videoVo);
            //缩略图
            if (StringUtils.isEmpty(bo.getPoster())){
                File file = new File(bo.getUrl());
                String picName = FFmpegUtil.inforTransfer(file, Constant.INFOR_PICTURE_PATH, bo.getId());
                if (StringUtils.isEmpty(picName)){
                    picName = FFmpegUtil.inforTransfer(file,  Constant.INFOR_PICTURE_PATH, bo.getId());
                }
                String vedioPic = QiNiu.uploadToQiNiu(Constant.INFOR_PICTURE_PATH, picName);
                File picfile = new File(Constant.INFOR_PICTURE_PATH, picName);
                if (null != picfile) {
                    picfile.delete();
                }
                inforService.updateVideoPicById(bo.getId(), vedioPic);
                videoVo.setPoster(vedioPic);
            }
            videoVo.setInforid(bo.getId());
            videoVos.add(videoVo);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("videoList", videoBos);
        return JSONObject.fromObject(map).toString();
    }

    @RequestMapping("/video-infor")
    @ResponseBody
    public String videoInfors(@RequestParam String videoid,
                              HttpServletRequest request, HttpServletResponse response){
        VideoBo videoBo = inforService.findVideoById(videoid);
        if (videoBo == null) {
            return CommonUtil.toErrorResult(
                    ERRORCODE.INFOR_IS_NULL.getIndex(),
                    ERRORCODE.INFOR_IS_NULL.getReason());
        }
        InforReadNumBo readNumBo = inforService.findReadByid(videoid);
        if (readNumBo == null) {
            readNumBo = new InforReadNumBo();
            readNumBo.setClassName(videoBo.getClassName());
            readNumBo.setInforid(videoid);
            readNumBo.setVisitNum(1);
            inforService.addReadNum(readNumBo);
        } else {
            inforService.updateReadNum(videoid);
        }
        VideoVo videoVo = new VideoVo();

        HttpSession session = request.getSession();
        if (!session.isNew() && session.getAttribute("isLogin") != null) {
            UserBo userBo = (UserBo) session.getAttribute("userBo");
            ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(videoid, userBo.getId());
            videoVo.setSelfSub(thumbsupBo != null);
        }
        videoVo.setInforid(videoid);
        BeanUtils.copyProperties( videoBo, videoVo);
        long thuSupNum = thumbsupService.selectByOwnerIdCount(videoid);
        videoVo.setThumpsubNum(thuSupNum);
        videoVo.setCommentNum((long)readNumBo.getCommentNum());
        videoVo.setReadNum(readNumBo.getVisitNum());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("video", videoVo);
        return JSONObject.fromObject(map).toString();
    }


    @RequestMapping("/recommend-groups")
    @ResponseBody
    public String recommendGroups(int type, HttpServletRequest request, HttpServletResponse response){

        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        InforSubscriptionBo mySub = inforService.findMySubs(userBo.getId());
        if (mySub == null) {
            mySub = new InforSubscriptionBo();
            mySub.setUserid(userBo.getId());
            inforService.insertSub(mySub);
        }
        LinkedList<String> mySubs = mySub.getSubscriptions();
        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
        List<String> groupList = new ArrayList<>();
        String keys = "";
        switch (type){
            case Constant.ONE:
                keys = "healthTypes";
                break;
            case Constant.TWO:
                keys = "securityTypes";
                break;
            case Constant.THREE:
                keys = "radioTypes";
                break;
            case Constant.FOUR:
                keys = "videoTypes";
                break;
            default:
                break;
        }
        if (cache.containsKey(keys)) {
            Object groupTypes = cache.get(keys);
            JSONArray array = JSONArray.fromObject(groupTypes);
            int size = array.size();
            for (int i = 0; i < size; i++) {
                String groupName = (String)array.get(i);
                if (!mySubs.contains(groupName)) {
                    groupList.add(groupName);
                } 
            }
        }
        map.put("mySubTypes", mySub.getSubscriptions());
        map.put("recoTypes", groupList);
        return JSONObject.fromObject(map).toString();
    }

    
    @RequestMapping("/update-groups")
    @ResponseBody
    public String updateGroups(String[] groupNames, int type, HttpServletRequest request, HttpServletResponse
            response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        InforSubscriptionBo mySub = inforService.findMySubs(userBo.getId());
        LinkedList<String> mySubs = (LinkedList<String>) Arrays.asList(groupNames);
        boolean isNew = false;
        if (null == mySub) {
            mySub = new InforSubscriptionBo();
            mySub.setUserid(userBo.getId());
            mySub.setCreateuid(userBo.getId());
            isNew = true;
        }
        switch (type){
            case Constant.ONE:
                mySub.setSubscriptions(mySubs);
                break;
            case Constant.TWO:
                mySub.setSecuritys(mySubs);
                break;
            case Constant.THREE:
                mySub.setRadios(mySubs);
                break;
            case Constant.FOUR:
                mySub.setSecuritys(mySubs);
                break;
            default:
                break;
        }
        if (isNew){
            inforService.insertSub(mySub);
        } else {
            inforService.updateSub(userBo.getId(), type, mySubs);
        }
        return Constant.COM_RESP;
    }

    @RequestMapping("/news-infor")
    @ResponseBody
    public String infor(String inforid, HttpServletRequest request, HttpServletResponse response){

        InforBo inforBo = inforService.findById(inforid);
        if (inforBo == null) {
            return CommonUtil.toErrorResult(
                    ERRORCODE.INFOR_IS_NULL.getIndex(),
                    ERRORCODE.INFOR_IS_NULL.getReason());
        }

        InforReadNumBo readNumBo = inforService.findReadByid(inforid);
        if (readNumBo == null) {
            readNumBo = new InforReadNumBo();
            readNumBo.setClassName(inforBo.getClassName());
            readNumBo.setInforid(inforBo.getId());
            readNumBo.setVisitNum(1);
            inforService.addReadNum(readNumBo);
        } else {
            inforService.updateReadNum(inforid);
            readNumBo.setVisitNum(readNumBo.getVisitNum() + 1);
        }
        InforVo inforVo = new InforVo();

        HttpSession session = request.getSession();

        if (!session.isNew() && session.getAttribute("isLogin") != null) {
            UserBo userBo = (UserBo) session.getAttribute("userBo");
            ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(inforBo.getId(), userBo.getId());
            inforVo.setSelfSub(thumbsupBo != null);
        }
        inforVo.setInforid(inforBo.getId());
        BeanUtils.copyProperties(inforBo, inforVo);
        long thuSupNum = thumbsupService.selectByOwnerIdCount(inforBo.getId());
        inforVo.setThumpsubNum(thuSupNum);
        inforVo.setCommentNum(commentService.selectCommentByTypeCount(Constant.INFOR_TYPE, inforid));
        inforVo.setReadNum(readNumBo.getVisitNum());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVo", inforVo);
        return JSONObject.fromObject(map).toString();
    }

    @RequestMapping("/security-infor")
    @ResponseBody
    public String securitys(String inforid, HttpServletRequest request, HttpServletResponse response){

        SecurityBo securityBo = inforService.findSecurityById(inforid);
        if (securityBo == null) {
            return CommonUtil.toErrorResult(
                    ERRORCODE.INFOR_IS_NULL.getIndex(),
                    ERRORCODE.INFOR_IS_NULL.getReason());
        }

        InforReadNumBo readNumBo = inforService.findReadByid(inforid);
        if (readNumBo == null) {
            readNumBo = new InforReadNumBo();
            readNumBo.setClassName(securityBo.getNewsType());
            readNumBo.setInforid(securityBo.getId());
            readNumBo.setVisitNum(1);
            inforService.addReadNum(readNumBo);
        } else {
            inforService.updateReadNum(inforid);
        }
        SecurityVo securityVo = new SecurityVo();

        HttpSession session = request.getSession();

        if (!session.isNew() && session.getAttribute("isLogin") != null) {
            UserBo userBo = (UserBo) session.getAttribute("userBo");
            ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(inforid, userBo.getId());
            securityVo.setSelfSub(thumbsupBo != null);
        }
        securityVo.setInforid(securityBo.getId());
        BeanUtils.copyProperties(securityBo, securityVo);
        long thuSupNum = thumbsupService.selectByOwnerIdCount(inforid);
        securityVo.setThumpsubNum(thuSupNum);
        securityVo.setCommentNum(commentService.selectCommentByTypeCount(Constant.INFOR_TYPE, inforid));
        securityVo.setReadNum(readNumBo.getVisitNum());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("securityVo", securityVo);
        return JSONObject.fromObject(map).toString();
    }

    @RequestMapping("/security-list")
    @ResponseBody
    public String securityList(@RequestParam String newsType,
                              @RequestParam(required = false)String inforTime,
                              @RequestParam int limit,
                              HttpServletRequest request, HttpServletResponse response){
        List<SecurityBo> securityBos = inforService.findSecurityByType(newsType, inforTime, limit);

        LinkedList<SecurityVo> vos = new LinkedList<>();
        for (SecurityBo securityBo : securityBos) {
            SecurityVo securityVo = new SecurityVo();
            securityVo.setCity(securityBo.getId());
            securityVo.setInforid(securityBo.getId());
            securityVo.setNewsType(newsType);
            securityVo.setTime(securityBo.getTime());
            securityVo.setTitle(securityBo.getTitle());
            //list不需要获取正文
//            inforVo.setText(inforBo.getText());
            vos.add(securityVo);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("securityVoList", vos);
        return JSONObject.fromObject(map).toString();
    }

    @RequestMapping("/recommend-securitys")
    @ResponseBody
    public String recommendSecuritys(HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        InforSubscriptionBo mySub = inforService.findMySubs(userBo.getId());
        if (mySub == null) {
            mySub = new InforSubscriptionBo();
            mySub.setUserid(userBo.getId());
            inforService.insertSub(mySub);
        }
        LinkedList<String> mySubs = mySub.getSecuritys();
        map.put("mySubSecuritys", mySub.getSecuritys());

        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
        List<String> groupList = new ArrayList<>();
        if (cache.containsKey("securityTypes")) {
            Object groupTypes = cache.get("securityTypes");
            JSONArray array = JSONArray.fromObject(groupTypes);
            int size = array.size();
            for (int i = 0; i < size; i++) {
                String groupName = (String)array.get(i);
                if (!mySubs.contains(groupName)) {
                    groupList.add(groupName);
                }
            }
        }
        map.put("recoSecuritys", groupList);
        return JSONObject.fromObject(map).toString();
    }

    @RequestMapping("/update-securitys")
    @ResponseBody
    public String updateSecuritys(String[] securityNames, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

        InforSubscriptionBo mySub = inforService.findMySubs(userBo.getId());

        LinkedList<String> securitys = (LinkedList<String>) Arrays.asList(securityNames);
        if (null == mySub) {
            mySub = new InforSubscriptionBo();
            mySub.setUserid(userBo.getId());
            mySub.setCreateuid(userBo.getId());
            mySub.setSecuritys(securitys);
            inforService.insertSub(mySub);
        } else {
            inforService.updateSecuritys(userBo.getId(), securitys);
        }
        return Constant.COM_RESP;
    }


    @RequestMapping("/add-comment")
    @ResponseBody
    public String addComment(@RequestParam String inforid,
                             @RequestParam String countent,
                             String parentid,
                             HttpServletRequest request, HttpServletResponse response){

        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

        Date currentDate = new Date();
        CommentBo commentBo = new CommentBo();
        commentBo.setTargetid(inforid);
        commentBo.setParentid(parentid);
        commentBo.setUserName(userBo.getUserName());
        commentBo.setContent(countent);
        commentBo.setCreateuid(userBo.getId());
        commentBo.setCreateTime(currentDate);
        commentBo.setType(Constant.INFOR_TYPE);
        commentService.insert(commentBo);

        userService.addUserLevel(userBo.getId(),1, Constant.LEVEL_COMMENT);

        inforService.updateComment(inforid, 1);

        CommentVo commentVo = comentBo2Vo(commentBo);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("commentVo", commentVo);
        return JSONObject.fromObject(map).toString();
    }

    @RequestMapping("/get-comments")
    @ResponseBody
    public String getComment(@RequestParam String inforid, String start_id, boolean gt, int limit,
                             HttpServletRequest request, HttpServletResponse response){
        List<CommentBo> commentBos = commentService.selectCommentByType(Constant.INFOR_TYPE, inforid,
                start_id, gt, limit);
        List<CommentVo> commentVos = new ArrayList<>();
        for (CommentBo commentBo : commentBos) {
            commentVos.add(comentBo2Vo(commentBo));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("commentVoList", commentVos);
        return JSONObject.fromObject(map).toString();
    }
   

    @RequestMapping("/thumbsup")
    @ResponseBody
    public String inforThumbsup(@RequestParam String targetid, @RequestParam int type,
            HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        ThumbsupBo thumbsupBo = thumbsupService.findHaveOwenidAndVisitorid(targetid, userBo.getId());
        if (null == thumbsupBo) {
            thumbsupBo = new ThumbsupBo();
            if (type == 0) {
                thumbsupBo.setType(Constant.INFOR_TYPE);
                inforService.updateThumpsub(targetid, 1);
            } else if (type == 1) {
                thumbsupBo.setType(Constant.INFOR_COM_TYPE);
                CommentBo commentBo = commentService.findById(targetid);
                if (commentBo == null) {
                    return CommonUtil.toErrorResult(
                            ERRORCODE.COMMENT_IS_NULL.getIndex(),
                            ERRORCODE.COMMENT_IS_NULL.getReason());
                }
                inforService.updateThumpsub(commentBo.getTargetid(), 1);
            } else {
                return CommonUtil.toErrorResult(
                        ERRORCODE.TYPE_ERROR.getIndex(),
                        ERRORCODE.TYPE_ERROR.getReason());
            }
            thumbsupBo.setOwner_id(targetid);
            thumbsupBo.setImage(userBo.getHeadPictureName());
            thumbsupBo.setVisitor_id(userBo.getId());
            thumbsupBo.setCreateuid(userBo.getId());
            thumbsupService.insert(thumbsupBo);

        } else {
            if (thumbsupBo.getDeleted() == Constant.DELETED) {
                thumbsupService.udateDeleteById(thumbsupBo.getId());
                inforService.updateThumpsub(thumbsupBo.getOwner_id(), 1);
            }
        }
        return Constant.COM_RESP;
    }

    @RequestMapping("/cancal-thumbsup")
    @ResponseBody
    public String cancelThumbsup(@RequestParam String targetid, @RequestParam int type,
                                 HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(targetid, userBo.getId());
        if (thumbsupBo != null) {
            thumbsupService.deleteById(thumbsupBo.getId());
        }
        if (type == 0) {
            inforService.updateThumpsub(targetid, -1);
        }else if (type == 1) {
            CommentBo commentBo = commentService.findById(targetid);
            if (commentBo != null) {
                inforService.updateThumpsub(commentBo.getTargetid(), 1);
            }
        }

        return Constant.COM_RESP;
    }


    private CommentVo comentBo2Vo(CommentBo commentBo){
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(commentBo, commentVo);
        commentVo.setCommentId(commentBo.getId());
        commentVo.setUserid(commentBo.getCreateuid());
        if (!StringUtils.isEmpty(commentBo.getParentid())) {
            CommentBo parent = commentService.findById(commentBo.getParentid());
            commentVo.setParentUserName(parent.getUserName());
            commentVo.setParentUserid(parent.getCreateuid());
        }
        UserBo userBo = userService.getUser(commentBo.getCreateuid());
        if (userBo != null){
            commentVo.setUserHeadPic(userBo.getHeadPictureName());
            commentVo.setUserBirth(userBo.getBirthDay());
            commentVo.setUserSex(userBo.getSex());
            commentVo.setUserLevel(userBo.getLevel());
        }
        return commentVo;
    }

    /**
     * s
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/home-health")
    @ResponseBody
    public String homeHealth(HttpServletRequest request, HttpServletResponse response){
        List<InforBo> inforBos = inforService.homeHealthRecom(50);
        LinkedList<InforVo> inforVos = new LinkedList<>();
        for (InforBo inforBo : inforBos) {
            InforVo inforVo = new InforVo();
            inforVo.setInforid(inforBo.getId());
            inforVo.setClassName(inforBo.getClassName());
            inforVo.setImageUrls(inforBo.getImageUrls());
            inforVo.setSource(inforBo.getSource());
            inforVo.setTitle(inforBo.getTitle());
            inforVo.setTime(inforBo.getTime());
            inforVo.setSourceUrl(inforBo.getSourceUrl());
            inforVos.add(inforVo);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVoList", inforVos);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * s
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/user-health")
    @ResponseBody
    public String userHealth(HttpServletRequest request, HttpServletResponse response){

        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        List<InforBo> inforBos = inforService.userHealthRecom(userBo.getId(), 50);
        LinkedList<InforVo> inforVos = new LinkedList<>();
        for (InforBo inforBo : inforBos) {
            InforVo inforVo = new InforVo();
            inforVo.setInforid(inforBo.getId());
            inforVo.setClassName(inforBo.getClassName());
            inforVo.setImageUrls(inforBo.getImageUrls());
            inforVo.setSource(inforBo.getSource());
            inforVo.setTitle(inforBo.getTitle());
            inforVo.setTime(inforBo.getTime());
            inforVo.setSourceUrl(inforBo.getSourceUrl());
            inforVos.add(inforVo);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVoList", inforVos);
        return JSONObject.fromObject(map).toString();

    }



}
