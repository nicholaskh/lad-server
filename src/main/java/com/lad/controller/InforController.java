package com.lad.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lad.bo.CommentBo;
import com.lad.bo.InforHistoryBo;
import com.lad.bo.InforReadNumBo;
import com.lad.bo.InforRecomBo;
import com.lad.bo.InforSubscriptionBo;
import com.lad.bo.InforUserReadBo;
import com.lad.bo.InforUserReadHisBo;
import com.lad.bo.ThumbsupBo;
import com.lad.bo.UserBo;
import com.lad.redis.RedisServer;
import com.lad.scrapybo.BroadcastBo;
import com.lad.scrapybo.InforBo;
import com.lad.scrapybo.SecurityBo;
import com.lad.scrapybo.VideoBo;
import com.lad.service.ICommentService;
import com.lad.service.IInforRecomService;
import com.lad.service.IInforService;
import com.lad.service.IThumbsupService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.FFmpegUtil;
import com.lad.util.MyException;
import com.lad.util.QiNiu;
import com.lad.vo.BroadcastVo;
import com.lad.vo.CommentVo;
import com.lad.vo.InforVo;
import com.lad.vo.SecurityVo;
import com.lad.vo.VideoVo;

import net.sf.json.JSONObject;

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

    private static Logger logger = LogManager.getLogger(InforController.class);

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

    @Autowired
    private IInforRecomService inforRecomService;


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
        cache.put(Constant.SECRITY_NAME, securityTypes, 0, TimeUnit.MINUTES);
        cache.put(Constant.HEALTH_NAME, groupTypes, 0, TimeUnit.MINUTES);
        cache.put(Constant.RADIO_NAME, broadTypes, 0, TimeUnit.MINUTES);
        cache.put(Constant.VIDEO_NAME, videoTypes, 0, TimeUnit.MINUTES);
        map.put(Constant.HEALTH_NAME, groupTypes);
        map.put(Constant.SECRITY_NAME, securityTypes);
        map.put(Constant.VIDEO_NAME, videoTypes);
        map.put(Constant.RADIO_NAME, broadTypes);
        return JSONObject.fromObject(map).toString();
    }


    @RequestMapping("/group-types")
    @ResponseBody
    public String inforGroups(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ret", 0);
        boolean isGetType = false;
        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
        if (!session.isNew() && session.getAttribute("isLogin") != null) {
            UserBo userBo = (UserBo) session.getAttribute("userBo");
            InforSubscriptionBo mySub = inforService.findMySubs(userBo.getId());
            if (mySub != null) {
                if (mySub.getSubscriptions().isEmpty()) {
                    map.put(Constant.HEALTH_NAME, cache.get(Constant.HEALTH_NAME));
                } else {
                    map.put(Constant.HEALTH_NAME, mySub.getSubscriptions());
                }
                if (mySub.getSecuritys().isEmpty()) {
                    map.put(Constant.HEALTH_NAME, cache.get(Constant.SECRITY_NAME));
                } else {
                    map.put(Constant.HEALTH_NAME, mySub.getSecuritys());
                }
                if (mySub.getRadios().isEmpty()) {
                    map.put(Constant.HEALTH_NAME, cache.get(Constant.RADIO_NAME));
                } else {
                    map.put(Constant.HEALTH_NAME, mySub.getRadios());
                }
                if (mySub.getVideos().isEmpty()) {
                    map.put(Constant.HEALTH_NAME, cache.get(Constant.VIDEO_NAME));
                } else {
                    map.put(Constant.HEALTH_NAME, mySub.getVideos());
                }
                isGetType  = true;
            }
        }
        if (!isGetType) {
            if (cache.containsKey(Constant.HEALTH_NAME) &&
                    cache.containsKey(Constant.SECRITY_NAME) &&
                    cache.containsKey(Constant.RADIO_NAME) &&
                    cache.containsKey(Constant.VIDEO_NAME)) {
                map.put(Constant.HEALTH_NAME, cache.get(Constant.HEALTH_NAME));
                map.put(Constant.SECRITY_NAME, cache.get(Constant.SECRITY_NAME));
                map.put(Constant.RADIO_NAME, cache.get(Constant.RADIO_NAME));
                map.put(Constant.VIDEO_NAME, cache.get(Constant.VIDEO_NAME));
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
                cache.put(Constant.SECRITY_NAME, securityTypes, 0, TimeUnit.MINUTES);
                cache.put(Constant.HEALTH_NAME, groupTypes, 0, TimeUnit.MINUTES);
                cache.put(Constant.RADIO_NAME, broadTypes, 0, TimeUnit.MINUTES);
                cache.put(Constant.VIDEO_NAME, videoTypes, 0, TimeUnit.MINUTES);
                map.put(Constant.HEALTH_NAME, groupTypes);
                map.put(Constant.SECRITY_NAME, securityTypes);
                map.put(Constant.VIDEO_NAME, videoTypes);
                map.put(Constant.RADIO_NAME, broadTypes);
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
            bo2vo(inforBo, inforVo);
            inforVos.add(inforVo);
        }
        UserBo userBo =  getUserLogin(request);
        if (userBo != null) {
            updateUserReadHis(userBo, groupName, Constant.INFOR_HEALTH);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVoList", inforVos);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 用户更新自己分类访问记录
     * @param userBo
     * @param module
     * @param type
     */
    @Async
    private void updateUserReadHis(UserBo userBo, String module, int type){
        Date currenDate = CommonUtil.getZeroDate(new Date());
        InforUserReadHisBo readHisBo = inforRecomService.findByReadHis(userBo.getId(), type, module);
        if (readHisBo != null) {
            //当前类的最后一次浏览时间
            if (!readHisBo.getLastDate().equals(currenDate)) {
                inforRecomService.updateUserReadHis(readHisBo.getId(), currenDate);
            }
        } else {
            readHisBo = new InforUserReadHisBo();
            readHisBo.setLastDate(currenDate);
            readHisBo.setModule(module);
            readHisBo.setType(type);
            readHisBo.setUserid(userBo.getId());
            inforRecomService.addUserReadHis(readHisBo);
        }
        boolean isNew = false;
        InforUserReadBo readBo = inforRecomService.findUserReadByUserid(userBo.getId());
        if (readBo == null) {
            readBo = new InforUserReadBo();
            readBo.setUserid(userBo.getId());
            isNew = true;
        }
        LinkedHashSet<String> sets = null;
        if (Constant.INFOR_HEALTH == type) {
            sets = readBo.getHealths();
        } else if (Constant.INFOR_SECRITY == type) {
            sets = readBo.getSecuritys();
        } else if (Constant.INFOR_RADIO == type) {
            sets= readBo.getRadios();
        } else if (Constant.INFOR_VIDEO == type) {
            sets = readBo.getVideos();
        } else {
            sets = new LinkedHashSet<>();
        }
        if (isNew) {
            sets.add(module);
            inforRecomService.addUserRead(readBo);
        } else {
            if (!sets.contains(module)){
                sets.add(module);
                inforRecomService.updateUserRead(readBo.getId(), type, sets);
                //更新其他过时分类
                updateUserReadAll(userBo, readBo);
            }
        }
    }

    /**
     * 删除180天前的浏览分类
     * @param userBo
     * @param readBo
     */
    private void updateUserReadAll(UserBo userBo, InforUserReadBo readBo){
        Date currenDate = CommonUtil.getZeroDate(new Date());
        Date halfTime = CommonUtil.getHalfYearTime(currenDate);

        List<InforUserReadHisBo> readHisBos = inforRecomService.findUserReadHisBeforeHalf(userBo.getId(), halfTime);
        if (readHisBos == null || readHisBos.isEmpty()){
           return;
        }
        HashSet<String> healths = readBo.getHealths();
        HashSet<String> securitys = readBo.getSecuritys();
        HashSet<String> radios = readBo.getRadios();
        HashSet<String> videos = readBo.getVideos();
        for (InforUserReadHisBo readHisBo: readHisBos) {
            int type = readHisBo.getType();
            if (Constant.INFOR_HEALTH == readHisBo.getType()) {
                healths.remove(readHisBo.getModule());
            } else if (Constant.INFOR_SECRITY == type) {
                securitys.remove(readHisBo.getModule());
            } else if (Constant.INFOR_RADIO == type) {
                radios.remove(readHisBo.getModule());
            } else if (Constant.INFOR_VIDEO == type) {
                videos.remove(readHisBo.getModule());
            }
        }
        inforRecomService.updateUserReadAll(readBo);
    }

    /**
     * 更新单条咨询访问信息记录
     * @param inforid
     * @param module
     * @param type
     */
    @Async
    private void updateInforHistroy(String inforid, String module, int type){
        Date currenDate = CommonUtil.getZeroDate(new Date());
        Date halfTime = CommonUtil.getHalfYearTime(currenDate);
        RLock lock = redisServer.getRLock("inforHis");
        List<InforHistoryBo> historyBos = null;
        try {
            lock.lock(5, TimeUnit.SECONDS);
            InforHistoryBo historyBo = inforRecomService.findTodayHis(inforid, currenDate);
            if (historyBo == null) {
                historyBo = new InforHistoryBo();
                historyBo.setDayNum(1);
                historyBo.setInforid(inforid);
                historyBo.setModule(module);
                historyBo.setType(type);
                historyBo.setReadDate(currenDate);
                inforRecomService.addInfoHis(historyBo);
            } else {
                inforRecomService.updateHisDayNum(historyBo.getId(),1);
            }
            InforRecomBo recomBo = inforRecomService.findRecomByInforid(inforid);
            if (recomBo == null) {
                recomBo = new InforRecomBo();
                recomBo.setInforid(inforid);
                recomBo.setModule(module);
                recomBo.setType(type);
                recomBo.setHalfyearNum(1);
                recomBo.setTotalNum(1);
                inforRecomService.addInforRecom(recomBo);
            } else {
                historyBos = inforRecomService.findHalfYearHis(inforid, halfTime);
                if (historyBos == null || historyBos.isEmpty()){
                    inforRecomService.updateRecomByInforid(recomBo.getId(), 1, 1);
                    return;
                } else {
                    int disNum = 0;
                    for (InforHistoryBo history : historyBos) {
                        disNum += history.getDayNum();
                    }
                    inforRecomService.updateRecomByInforid(recomBo.getId(), -disNum, 1);
                }
            }
        } finally {
            lock.unlock();
        }
        if (historyBos != null){
            for (InforHistoryBo history : historyBos) {
                inforRecomService.deleteHis(history.getId());
            }
        }
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
        UserBo userBo =  getUserLogin(request);
        if (userBo != null) {
            updateUserReadHis(userBo, module, Constant.INFOR_RADIO);
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
            UserBo userBo = getUserLogin(request);
            if (userBo != null) {
                updateInforHistroy(radioid, broadcastBo.getModule(), Constant.INFOR_RADIO);
            }
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
                String picName = FFmpegUtil.inforTransfer(bo.getUrl(), Constant.INFOR_PICTURE_PATH, bo.getId());
                if (StringUtils.isEmpty(picName)){
                    picName = FFmpegUtil.inforTransfer(bo.getUrl(),  Constant.INFOR_PICTURE_PATH, bo.getId());
                }
                if (!StringUtils.isEmpty(picName)){
                    File file = null;
                    try {
                        file = new File(Constant.INFOR_PICTURE_PATH, picName);
                    } catch (Exception e){
                        logger.error(e);
                    }
                    if (file != null && file.exists()){
                        String vedioPic = QiNiu.uploadToQiNiu(Constant.INFOR_PICTURE_PATH, picName);
                        String path = Constant.QINIU_URL + vedioPic + "?v=" + CommonUtil.getRandom1();
                        inforService.updateVideoPicById(bo.getId(), path);
                        videoVo.setPicture(path);
                        file.delete();
                    }
                }
            } else {
                videoVo.setPicture(bo.getPoster());
            }
            videoVo.setInforid(bo.getId());
            videoVos.add(videoVo);
        }
        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            updateUserReadHis(userBo, module, Constant.INFOR_VIDEO);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("videoList", videoVos);
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

        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(videoid, userBo.getId());
            videoVo.setSelfSub(thumbsupBo != null);
            updateInforHistroy(videoid, videoBo.getModule(), Constant.INFOR_RADIO);
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
        HashSet<String> mySubs = null;
        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
        String keys = "";
        switch (type){
            case Constant.INFOR_HEALTH:
                keys = Constant.HEALTH_NAME;
                mySubs = mySub.getSubscriptions();
                break;
            case Constant.INFOR_SECRITY:
                keys = Constant.SECRITY_NAME;
                mySubs = mySub.getSecuritys();
                break;
            case Constant.INFOR_RADIO:
                keys = Constant.RADIO_NAME;
                mySubs = mySub.getRadios();
                break;
            case Constant.INFOR_VIDEO:
                keys = Constant.VIDEO_NAME;
                mySubs = mySub.getVideos();
                break;
            default:
                mySubs = new LinkedHashSet<>();
                break;
        }
        HashSet<String> groupTypes = (HashSet<String>)cache.get(keys);
        groupTypes.removeAll(mySubs);
        map.put("recoTypes", groupTypes);
        map.put("mySubTypes", mySubs);
        return JSONObject.fromObject(map).toString();
    }

    
    @RequestMapping("/update-groups")
    @ResponseBody
    public String updateGroups(@RequestParam String groupNames,
                               @RequestParam int type,
                               HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

        InforSubscriptionBo mySub = inforService.findMySubs(userBo.getId());
        boolean isNew = false;
        if (null == mySub) {
            mySub = new InforSubscriptionBo();
            mySub.setUserid(userBo.getId());
            mySub.setCreateuid(userBo.getId());
            isNew = true;
        }
        LinkedHashSet<String> mySubs = new LinkedHashSet<>();
        String keys = "";
        switch (type){
            case Constant.INFOR_HEALTH:
                mySub.setSubscriptions(mySubs);
                keys = Constant.HEALTH_NAME;
                break;
            case Constant.INFOR_SECRITY:
                mySub.setSecuritys(mySubs);
                keys = Constant.SECRITY_NAME;
                break;
            case Constant.INFOR_RADIO:
                mySub.setRadios(mySubs);
                keys = Constant.RADIO_NAME;
                break;
            case Constant.INFOR_VIDEO:
                mySub.setVideos(mySubs);
                keys = Constant.VIDEO_NAME;
                break;
            default:
                break;
        }
        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
        LinkedHashSet<String> groupTypes = (LinkedHashSet<String>) cache.get(keys);
        if (groupNames.indexOf(',') > -1) {
            String[] namesArr = groupNames.split(",");
            for (String name : namesArr) {
                if (groupTypes.contains(name)) {
                    mySubs.add(name);
                } 
            }
        } else {
            if (groupTypes.contains(groupNames)) {
                mySubs.add(groupNames);
            } 
        }
        if (isNew){
            inforService.insertSub(mySub);
        } else {
            inforService.updateSub(mySub.getId(), type, mySubs);
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
        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(inforBo.getId(), userBo.getId());
            inforVo.setSelfSub(thumbsupBo != null);
            updateInforHistroy(inforid, inforBo.getClassName(), Constant.INFOR_HEALTH);
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


        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(inforid, userBo.getId());
            securityVo.setSelfSub(thumbsupBo != null);
            updateInforHistroy(inforid, securityBo.getNewsType(), Constant.INFOR_SECRITY);
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
            vos.add(securityVo);
        }
        UserBo userBo =  getUserLogin(request);
        if (userBo != null) {
            updateUserReadHis(userBo, newsType, Constant.INFOR_SECRITY);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("securityVoList", vos);
        return JSONObject.fromObject(map).toString();
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

        List<InforRecomBo> recomBos = inforRecomService.findRecomByType(Constant.INFOR_HEALTH);
        List<InforVo> inforVos = new ArrayList<>();
        int num = 0;
        for (InforRecomBo recomBo : recomBos) {
            InforBo inforBo = inforService.findById(recomBo.getInforid());
            if (inforBo != null) {
                InforVo inforVo = new InforVo();
                bo2vo(inforBo, inforVo);
                inforVos.add(inforVo);
                num++;
            }
        }
        if (num < 50) {
            List<InforBo> inforBos = inforService.homeHealthRecom(50 - num);
            for (InforBo inforBo : inforBos) {
                InforVo inforVo = new InforVo();
                bo2vo(inforBo, inforVo);
                inforVos.add(inforVo);
            }
        }
        addTop4(inforVos);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVoList", inforVos);
        return JSONObject.fromObject(map).toString();
    }

    @Async
    private void addTop4(List<InforVo> inforVos){
        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);

        ArrayList<InforVo> top4 = new ArrayList<>();
        int size = 0;
        for (InforVo inforVo : inforVos) {
            if (size > 4){
                break;
            }
            LinkedList<String> images = inforVo.getImageUrls();
            if (images != null && !images.isEmpty()) {
               top4.add(inforVo);
               size++;
            }
        }
        cache.put("top4",top4);
    }

    /**
     * s
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/home-top4")
    @ResponseBody
    public String homeHealthTop(HttpServletRequest request, HttpServletResponse response){
        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
        List<InforVo> top4 = null;
        if (cache.containsKey("top4")){
            top4 = (List<InforVo>) cache.get("top4");
        } else {
            int size = 0;
            List<InforBo> inforBos = inforService.homeHealthRecom(50);
            for (InforBo inforBo : inforBos) {
                if (size > 4){
                    break;
                }
                LinkedList<String> images = inforBo.getImageUrls();
                if (images != null && !images.isEmpty()) {
                    InforVo inforVo = new InforVo();
                    bo2vo(inforBo, inforVo);
                    top4.add(inforVo);
                    size ++;
                }
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVoList", top4);
        return JSONObject.fromObject(map).toString();
    }


    private void bo2vo(InforBo inforBo, InforVo inforVo){
        inforVo.setInforid(inforBo.getId());
        inforVo.setClassName(inforBo.getClassName());
        inforVo.setImageUrls(inforBo.getImageUrls());
        inforVo.setSource(inforBo.getSource());
        inforVo.setTitle(inforBo.getTitle());
        inforVo.setTime(inforBo.getTime());
        inforVo.setSourceUrl(inforBo.getSourceUrl());
        inforVo.setInforid(inforBo.getId());
    }

    /**
     * 健康个性推荐
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
        List<InforVo> inforVos = new ArrayList<>();
        int num = 0;
        InforUserReadBo readBo = inforRecomService.findUserReadByUserid(userBo.getId());
        if (readBo == null) {
            readBo = new InforUserReadBo();
            readBo.setUserid(userBo.getId());
            inforRecomService.addUserRead(readBo);
        } else {
            LinkedHashSet<String> set = readBo.getHealths();
            List<InforRecomBo> recomBos = null;
            if (set.size() > 0) {
                recomBos = inforRecomService.findRecomByTypeAndModule(Constant.INFOR_HEALTH, set);
            } else {
                recomBos = inforRecomService.findRecomByType(Constant.INFOR_HEALTH);
            }
            if (recomBos != null){
                for (InforRecomBo recomBo : recomBos) {
                    InforBo inforBo = inforService.findById(recomBo.getInforid());
                    if (inforBo != null) {
                        InforVo inforVo = new InforVo();
                        bo2vo(inforBo, inforVo);
                        num++;
                        inforVos.add(inforVo);
                    }
                }
            }
        }
        if (num < 50) {
            List<InforBo> inforBos = inforService.homeHealthRecom(50 - num);
            for (InforBo inforBo : inforBos) {
                InforVo inforVo = new InforVo();
                bo2vo(inforBo, inforVo);
                inforVos.add(inforVo);
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVoList", inforVos);
        return JSONObject.fromObject(map).toString();
    }


    /**
     * 安防个性推荐
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/user-securitys")
    @ResponseBody
    public String userSecritys(HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        List<SecurityVo> inforVos = new ArrayList<>();
        int num = 0;
        InforUserReadBo readBo = inforRecomService.findUserReadByUserid(userBo.getId());
        if (readBo == null) {
            readBo = new InforUserReadBo();
            readBo.setUserid(userBo.getId());
            inforRecomService.addUserRead(readBo);
        } else {
            LinkedHashSet<String> set = readBo.getSecuritys();
            List<InforRecomBo> recomBos = null;
            if (set.size() > 0) {
                recomBos = inforRecomService.findRecomByTypeAndModule(Constant.INFOR_SECRITY, set);
            } else {
                recomBos = inforRecomService.findRecomByType(Constant.INFOR_SECRITY);
            }
            if (recomBos != null){
                for (InforRecomBo recomBo : recomBos) {
                    SecurityBo inforBo = inforService.findSecurityById(recomBo.getInforid());
                    if (inforBo != null) {
                        SecurityVo inforVo = new SecurityVo();
                        num++;
                        inforVos.add(inforVo);
                    }
                }
            }
        }
        if (num < 50) {
            List<SecurityBo> securityBos = inforService.findSecurityTypes();
            for (SecurityBo securityBo : securityBos) {
                SecurityVo securityVo = new SecurityVo();
                BeanUtils.copyProperties(securityBo,securityVo);
                securityVo.setText("");
                securityVo.setInforid(securityBo.getId());
                inforVos.add(securityVo);
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("securityVoList", inforVos);
        return JSONObject.fromObject(map).toString();
    }


    /**
     * 广播个性推荐
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/user-radios")
    @ResponseBody
    public String userRadios(HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        List<SecurityVo> inforVos = new ArrayList<>();
        int num = 0;
        InforUserReadBo readBo = inforRecomService.findUserReadByUserid(userBo.getId());
        if (readBo == null) {
            readBo = new InforUserReadBo();
            readBo.setUserid(userBo.getId());
            inforRecomService.addUserRead(readBo);
        } else {
            LinkedHashSet<String> set = readBo.getSecuritys();
            List<InforRecomBo> recomBos = null;
            if (set.size() > 0) {
                recomBos = inforRecomService.findRecomByTypeAndModule(Constant.INFOR_SECRITY, set);
            } else {
                recomBos = inforRecomService.findRecomByType(Constant.INFOR_SECRITY);
            }
            if (recomBos != null){
                for (InforRecomBo recomBo : recomBos) {
                    SecurityBo inforBo = inforService.findSecurityById(recomBo.getInforid());
                    if (inforBo != null) {
                        SecurityVo inforVo = new SecurityVo();
                        num++;
                        inforVos.add(inforVo);
                    }
                }
            }
        }
        if (num < 50) {
            List<SecurityBo> securityBos = inforService.findSecurityTypes();
            for (SecurityBo securityBo : securityBos) {
                SecurityVo securityVo = new SecurityVo();
                BeanUtils.copyProperties(securityBo,securityVo);
                securityVo.setText("");
                securityVo.setInforid(securityBo.getId());
                inforVos.add(securityVo);
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("securityVoList", inforVos);
        return JSONObject.fromObject(map).toString();
    }


}
