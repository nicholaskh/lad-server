package com.lad.controller;

import com.alibaba.fastjson.JSON;
import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.scrapybo.*;
import com.lad.service.*;
import com.lad.util.*;
import com.lad.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述： 资讯接口
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/7/29
 */
@Api(value = "InforController", description = "资讯信息相关接口")
@RestController
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

    @Autowired
    private ICollectService collectService;

    @Autowired
    private IDynamicService dynamicService;

    @Autowired
    private INoteService noteService;

    @Autowired
    private ICircleService circleService;

    @Autowired
    private ISearchService searchService;

    @Autowired
    private AsyncController asyncController;


    @ApiOperation("刷新资讯分类缓存信息")
    @GetMapping("/init-cache")
    public String initCache(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new LinkedHashMap<>();

        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
        logger.info(cache.get("securityTypes"));
        initCache(cache);
        map.put(Constant.HEALTH_NAME, cache.get(Constant.HEALTH_NAME));
        map.put(Constant.SECRITY_NAME, cache.get(Constant.SECRITY_NAME));
        map.put(Constant.RADIO_NAME, cache.get(Constant.RADIO_NAME));
        map.put(Constant.VIDEO_NAME, cache.get(Constant.VIDEO_NAME));
        map.put(Constant.DAILY_NAME, cache.get(Constant.DAILY_NAME));
        map.put(Constant.YANGLAO_NAME, cache.get(Constant.YANGLAO_NAME));
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("获取资讯分类信息，用户若登录则返回已收藏的分类信息")
    @GetMapping("/group-types")
    public String inforGroups(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ret", 0);
        boolean isGetType = false;
        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            InforSubscriptionBo mySub = inforService.findMySubs(userBo.getId());
            if (mySub != null) {
                if (mySub.getSubscriptions().isEmpty()) {
                    map.put(Constant.HEALTH_NAME, cache.get(Constant.HEALTH_NAME));
                } else {
                    map.put(Constant.HEALTH_NAME, mySub.getSubscriptions());
                }
                if (mySub.getSecuritys().isEmpty()) {
                    map.put(Constant.SECRITY_NAME, cache.get(Constant.SECRITY_NAME));
                } else {
                    map.put(Constant.SECRITY_NAME, mySub.getSecuritys());
                }
                if (mySub.getRadios().isEmpty()) {
                    map.put(Constant.RADIO_NAME, cache.get(Constant.RADIO_NAME));
                } else {
                    map.put(Constant.RADIO_NAME, mySub.getRadios());
                }
                if (mySub.getVideos().isEmpty()) {
                    map.put(Constant.VIDEO_NAME, cache.get(Constant.VIDEO_NAME));
                } else {
                    map.put(Constant.VIDEO_NAME, mySub.getVideos());
                }
                if (CommonUtil.isEmpty(mySub.getDailys())) {
                    map.put(Constant.DAILY_NAME, cache.get(Constant.DAILY_NAME));
                } else {
                    map.put(Constant.DAILY_NAME, mySub.getDailys());
                }
                if (CommonUtil.isEmpty(mySub.getYanglaos())) {
                    map.put(Constant.YANGLAO_NAME, cache.get(Constant.YANGLAO_NAME));
                } else {
                    map.put(Constant.YANGLAO_NAME, mySub.getYanglaos());
                }
                isGetType  = true;
                logger.info("============== userid {}, inforSub {}", userBo.getId(),
                        JSON.toJSONString(map));
            } else {
                mySub = new InforSubscriptionBo();
                mySub.setUserid(userBo.getId());
                addCacheToSub(mySub, cache);
                inforService.insertSub(mySub);
                logger.info("============== userid {}, inforSub {}", userBo.getId(),
                        JSON.toJSONString(mySub));
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
                map.put(Constant.DAILY_NAME, cache.get(Constant.DAILY_NAME));
                map.put(Constant.YANGLAO_NAME, cache.get(Constant.YANGLAO_NAME));
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
                HashSet<String> dailyTypes = new LinkedHashSet<>();
                List<DailynewsBo> dailynewsBos =  inforService.selectDailynewsGroups();
                for (DailynewsBo videoBo : dailynewsBos) {
                    dailyTypes.add(videoBo.getId());
                }
                HashSet<String> yanglaoTypes = new LinkedHashSet<>();
                List<YanglaoBo> yanglaoBos =  inforService.selectYanglaoGroups();
                for (YanglaoBo videoBo : yanglaoBos) {
                    yanglaoTypes.add(videoBo.getId());
                }
                cache.put(Constant.SECRITY_NAME, securityTypes, 0, TimeUnit.MINUTES);
                cache.put(Constant.HEALTH_NAME, groupTypes, 0, TimeUnit.MINUTES);
                cache.put(Constant.RADIO_NAME, broadTypes, 0, TimeUnit.MINUTES);
                cache.put(Constant.VIDEO_NAME, videoTypes, 0, TimeUnit.MINUTES);
                cache.put(Constant.DAILY_NAME, dailyTypes, 0, TimeUnit.MINUTES);
                cache.put(Constant.YANGLAO_NAME, yanglaoTypes, 0, TimeUnit.MINUTES);
                map.put(Constant.HEALTH_NAME, groupTypes);
                map.put(Constant.SECRITY_NAME, securityTypes);
                map.put(Constant.VIDEO_NAME, videoTypes);
                map.put(Constant.RADIO_NAME, broadTypes);
                map.put(Constant.DAILY_NAME, dailyTypes);
                map.put(Constant.YANGLAO_NAME, yanglaoTypes);
            }
        }
        return JSONObject.fromObject(map).toString();
    }

    @ApiOperation("获取健康养生指定分类下资讯信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupName", value = "健康资讯分类", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "inforTime", value = "资讯分分页时最后一条时间，若为空表示第一页开始",paramType = "query", required =
                    true, dataType = "string"),
            @ApiImplicitParam(name = "limit", value = "资讯分页每页条数", required = true,paramType = "query", dataType =
                    "int")})
    @RequestMapping(value = "/group-infors",method = {RequestMethod.GET, RequestMethod.POST})
    public String groupInfors(@RequestParam String groupName,
                              @RequestParam(required = false)String inforTime,
                              @RequestParam int limit,
                              HttpServletRequest request, HttpServletResponse response){
        List<InforBo> inforBos = inforService.findClassInfos(groupName, inforTime, limit);
        LinkedList<InforVo> inforVos = new LinkedList<>();
        for (InforBo inforBo : inforBos) {
            InforVo inforVo = new InforVo();
            bo2vo(inforBo, inforVo);
            inforVo.setThumpsubNum(inforBo.getThumpsubNum());
            inforVo.setCommentNum(inforBo.getCommnetNum());
            inforVo.setReadNum(inforBo.getVisitNum());
            inforVos.add(inforVo);
        }
        UserBo userBo =  getUserLogin(request);
        if (userBo != null) {
            asyncController.updateUserReadHis(userBo.getId(), groupName, "", Constant.INFOR_HEALTH);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVoList", inforVos);
        return JSONObject.fromObject(map).toString();
    }

    @ApiOperation("获取广播指定分类下列表信息，已弃用，新方法见radio-classes")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "module", value = "广播资讯分类", required = true, dataType =
            "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "分页页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "分页条数", required = true, dataType = "int", paramType = "query")})
    @RequestMapping(value = "/radio-list", method = {RequestMethod.GET, RequestMethod.POST})
    @Deprecated
    public String radioList(String module, int page,  int limit,
                              HttpServletRequest request, HttpServletResponse response){
        List<BroadcastBo> broadcastBos = inforService.findBroadByPage(module, page, limit);
        List<BroadcastVo> vos = new ArrayList<>();
        for (BroadcastBo bo : broadcastBos) {
            BroadcastVo broadcastVo = new BroadcastVo();
            BeanUtils.copyProperties(bo, broadcastVo);
            broadcastVo.setInforid(bo.getId());
            broadcastVo.setReadNum(bo.getVisitNum());
            broadcastVo.setCommentNum(bo.getCommnetNum());
            broadcastVo.setThumpsubNum(bo.getThumpsubNum());
            vos.add(broadcastVo);
        }
        UserBo userBo =  getUserLogin(request);
        if (userBo != null) {
            asyncController.updateUserReadHis(userBo.getId(), module, "", Constant.INFOR_RADIO);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("radioList", vos);
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("获取广播指定分类下合集信息")
    @ApiImplicitParam(name = "module", value = "广播资讯分类", required = true, dataType = "string", paramType = "query")
    @RequestMapping(value = "/radio-classes", method = {RequestMethod.GET, RequestMethod.POST})
    public String radioGroups(String module,
                            HttpServletRequest request, HttpServletResponse response){
        List<BroadcastBo> broadcastBos = inforService.selectBroadClassByGroups(module);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ret", 0);
        List<RadioClassVo> classVos = new ArrayList<>();
        addRadios(broadcastBos, classVos);
        jsonObject.put("radioClasses", classVos);
        return jsonObject.toString();
    }

    private void addRadios(List<BroadcastBo> broadcastBos, List<RadioClassVo> classVos){
        if (broadcastBos == null) {
            return;
        }
        for (BroadcastBo bo : broadcastBos) {
            RadioClassVo classVo = new RadioClassVo();
            JSONObject object = new JSONObject();
            classVo.setModule(bo.getModule());
            classVo.setTitle(bo.getClassName());
            classVo.setSource(bo.getSource());
            classVo.setTotalVisit(bo.getVisitNum());
            classVos.add(classVo);
        }
    }


    @ApiOperation("获取广播指定分类下列表信息，已弃用，新方法见radio-classes")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "module", value = "广播资讯分类", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "className", value = "广播二级分类",paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "start", value = "开始集数",paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "end", value = "结束集数",paramType = "query", dataType = "int")})
    @RequestMapping(value = "/radio-groups", method = {RequestMethod.GET, RequestMethod.POST})
    public String radioGroups(String module, String className,int start, int end,
                              HttpServletRequest request, HttpServletResponse response){
        List<BroadcastBo> broadcastBos = inforService.findByClassNamePage(module, className,start, end);

        long counts = inforService.findRadioByClassCount(module, className);

        List<BroadcastVo> vos = new ArrayList<>();
        for (BroadcastBo bo : broadcastBos) {
            BroadcastVo broadcastVo = new BroadcastVo();
            BeanUtils.copyProperties(bo, broadcastVo);
            broadcastVo.setInforid(bo.getId());
            broadcastVo.setReadNum(bo.getVisitNum());
            broadcastVo.setCommentNum(bo.getCommnetNum());
            broadcastVo.setThumpsubNum(bo.getThumpsubNum());
            vos.add(broadcastVo);
        }
        asyncController.updateGrouopRecom(module, className,Constant.INFOR_RADIO);
        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            asyncController.updateUserReadHis(userBo.getId(), module, className, Constant.INFOR_RADIO);
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ret", 0);
        map.put("radioCounts", counts);
        map.put("radioList", vos);
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("获取指定广播资讯信息")
    @ApiImplicitParam(name = "radioid", value = "广播资讯id", required = true,paramType = "query", dataType = "string")
    @RequestMapping(value = "/radio-infor", method = {RequestMethod.GET, RequestMethod.POST})
    public String radioInfors(String radioid, HttpServletRequest request, HttpServletResponse response){
        BroadcastBo broadcastBo = inforService.findBroadById(radioid);
        BroadcastVo broadcastVo = null;
        if (broadcastBo != null) {
            updateInforNum(radioid, Constant.INFOR_RADIO, 1, Constant.VISIT_NUM);
            broadcastVo = new BroadcastVo();
            BeanUtils.copyProperties(broadcastBo, broadcastVo);
            broadcastVo.setInforid(broadcastBo.getId());
            broadcastVo.setReadNum(broadcastBo.getVisitNum());
            broadcastVo.setCommentNum(broadcastBo.getCommnetNum());
            broadcastVo.setThumpsubNum(broadcastBo.getThumpsubNum());
            asyncController.updateGrouprHistroy(radioid, broadcastBo.getModule(), broadcastBo.getClassName(),Constant.INFOR_RADIO);
            UserBo userBo = getUserLogin(request);
            String userid = userBo == null ? "" : userBo.getId();
            asyncController.addUserReadhis(userid, radioid, Constant.INFOR_RADIO, broadcastBo.getModule(), broadcastBo.getClassName());
            updateRadioHis(radioid, broadcastBo, userid);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("radio", broadcastVo);
        return JSONObject.fromObject(map).toString();
    }

    @ApiOperation("列表播放广播接口")
    @ApiImplicitParam(name = "inforid", value = "广播资讯id", required = true,paramType = "query", dataType = "string")
    @RequestMapping(value = "/radio-play", method = {RequestMethod.GET, RequestMethod.POST})
    public String radioPlay(String inforid, HttpServletRequest request, HttpServletResponse response){
        updateInforNum(inforid, Constant.INFOR_RADIO, 1, Constant.VISIT_NUM);
        BroadcastBo broadcastBo = inforService.findBroadById(inforid);
        if (broadcastBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.INFOR_IS_NULL.getIndex(),
                    ERRORCODE.INFOR_IS_NULL.getReason());
        }
        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            updateRadioHis(inforid, broadcastBo, userBo.getId());
            asyncController.addUserReadhis(userBo.getId(), inforid, Constant.INFOR_RADIO, broadcastBo.getModule(), broadcastBo
                    .getClassName());
        } else {
            updateRadioHis(inforid, broadcastBo, "");
        }
        return Constant.COM_RESP;
    }
    
    private void updateRadioHis(String radioid, BroadcastBo broadcastBo, String loginUserid){
        if (broadcastBo == null) {
            broadcastBo = inforService.findBroadById(radioid);
            if (broadcastBo == null) {
                return;
            }
            asyncController.updateGrouprHistroy(radioid, broadcastBo.getModule(), broadcastBo.getClassName(),Constant.INFOR_RADIO);
        }
        if (!StringUtils.isEmpty(loginUserid)) {
            asyncController.updateUserReadHis(loginUserid,broadcastBo.getModule(),
                    broadcastBo.getClassName(),Constant.INFOR_RADIO);
        }
    }


    @ApiOperation("列表播视频播接口")
    @ApiImplicitParam(name = "inforid", value = "视频资讯id", required = true,paramType = "query", dataType = "string")
    @RequestMapping(value = "/video-play", method = {RequestMethod.GET, RequestMethod.POST})
    public String videoPlay(String inforid, HttpServletRequest request, HttpServletResponse response){
        updateInforNum(inforid, Constant.INFOR_VIDEO, 1, Constant.VISIT_NUM);
        VideoBo videoBo = inforService.findVideoById(inforid);
        if (videoBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.INFOR_IS_NULL.getIndex(),
                    ERRORCODE.INFOR_IS_NULL.getReason());
        }
        UserBo userBo = getUserLogin(request);
        asyncController.updateGrouprHistroy(inforid, videoBo.getModule(), videoBo.getClassName(),Constant.INFOR_VIDEO);
        if (userBo != null) {
            asyncController.addUserReadhis(userBo.getId(), inforid, Constant.INFOR_VIDEO, videoBo.getModule(), videoBo.getClassName());
            asyncController.updateUserReadHis(userBo.getId(), videoBo.getModule(), videoBo.getClassName(), Constant.INFOR_VIDEO);
        }
        return Constant.COM_RESP;
    }



    @ApiOperation("获取视频资讯指定分类下列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "module", value = "视频资讯分类", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "className", value = "视频二级分类",paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "page", value = "页码",paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "每页条数",paramType = "query", dataType = "int")})
    @RequestMapping(value = "/video-list",method = {RequestMethod.GET, RequestMethod.POST})
    public String videoInfors(String module, String className, int page,  int limit,
                              HttpServletRequest request, HttpServletResponse response){
        List<VideoBo> videoBos = inforService.selectClassNamePage(module,className, page, limit);

        long counts = inforService.findVideoByClassCount(module,className);

        List<VideoVo> videoVos = new ArrayList<>();
        for (VideoBo bo : videoBos) {
            VideoVo videoVo = new VideoVo();
            BeanUtils.copyProperties(bo, videoVo);
            videoVo.setReadNum(bo.getVisitNum());
            //缩略图
            if (StringUtils.isEmpty(bo.getPoster())){
                String url = bo.getUrl();
                if (url.indexOf(',') > -1) {
                    url = url.substring(0, url.indexOf(','));
                }
                String picName = FFmpegUtil.inforTransfer(url, Constant.INFOR_PICTURE_PATH, bo.getId());
                if (StringUtils.isEmpty(picName)){
                    picName = FFmpegUtil.inforTransfer(url,  Constant.INFOR_PICTURE_PATH, bo.getId());
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
                } else {
                    inforService.updateVideoPicById(bo.getId(), "noPic");
                }
            } else if (bo.getPoster().equals("noPic")) {
                videoVo.setPicture("");
            } else {
                videoVo.setPicture(bo.getPoster());
            }
            videoVo.setInforid(bo.getId());
            videoVos.add(videoVo);
        }
        asyncController.updateGrouopRecom(module, className,Constant.INFOR_VIDEO);
        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            asyncController.updateUserReadHis(userBo.getId(), module, className, Constant.INFOR_VIDEO);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("videoCounts", counts);
        map.put("videoList", videoVos);
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("获取视频指定分类下合集信息")
    @ApiImplicitParam(name = "module", value = "视频资讯分类", required = true, paramType = "query",dataType = "string")
    @RequestMapping(value = "/video-classes", method = {RequestMethod.GET, RequestMethod.POST})
    public String videoClasses(String module, HttpServletRequest request, HttpServletResponse response){
        List<VideoBo> videoBos = inforService.selectVideoClassByGroups(module);
        UserBo userBo =getUserLogin(request);
        String userid = userBo != null ? userBo.getId() : "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ret", 0);
        List<VideoClassVo> classVos = new ArrayList<>();
        addVideo(videoBos, classVos, userid);
        jsonObject.put("videoClasses", classVos);
        return jsonObject.toString();
    }

    @ApiOperation("获取视频详细信息")
    @ApiImplicitParam(name = "videoid", value = "视频id", required = true, paramType = "query",dataType = "string")
    @RequestMapping(value = "/video-infor", method = {RequestMethod.GET, RequestMethod.POST})
    public String videoInfors(@RequestParam String videoid,
                              HttpServletRequest request, HttpServletResponse response){
        VideoBo videoBo = inforService.findVideoById(videoid);
        if (videoBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.INFOR_IS_NULL.getIndex(),
                    ERRORCODE.INFOR_IS_NULL.getReason());
        }
        updateInforNum(videoid, Constant.INFOR_VIDEO, 1, Constant.VISIT_NUM);
        VideoVo videoVo = new VideoVo();
        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(videoid, userBo.getId());
            videoVo.setSelfSub(thumbsupBo != null);
            asyncController.updateUserReadHis(userBo.getId(),videoBo.getModule(), videoBo.getClassName(), Constant.INFOR_VIDEO);
            asyncController.addUserReadhis(userBo.getId(), videoid, Constant.INFOR_VIDEO, videoBo.getModule(), videoBo.getClassName());
        }
        asyncController.updateGrouprHistroy(videoid, videoBo.getModule(), videoBo.getClassName(),Constant.INFOR_VIDEO);
        videoVo.setInforid(videoid);
        BeanUtils.copyProperties( videoBo, videoVo);
        videoVo.setThumpsubNum(videoBo.getThumpsubNum());
        videoVo.setCommentNum(videoBo.getCommnetNum());
        videoVo.setReadNum(videoBo.getVisitNum());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("video", videoVo);
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("获取资讯订阅和未订阅分类信息，需要登录")
    @ApiImplicitParam(name = "type", value = "资讯分类，1健康，2 安防，3广播，4视频，5时政，6养老", required = true, paramType = "query",
            dataType = "int")
    @RequestMapping(value = "/recommend-groups",method = {RequestMethod.GET, RequestMethod.POST})
    public String recommendGroups(int type, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        InforSubscriptionBo mySub = inforService.findMySubs(userBo.getId());
        if (mySub == null) {
            mySub = new InforSubscriptionBo();
            addCacheToSub(mySub, cache);
            mySub.setUserid(userBo.getId());
            inforService.insertSub(mySub);
        }
        HashSet<String> mySubs = null;
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
            case Constant.INFOR_DAILY:
                keys = Constant.DAILY_NAME;
                mySubs = mySub.getDailys();
                break;
            case Constant.INFOR_YANGLAO:
                keys = Constant.YANGLAO_NAME;
                mySubs = mySub.getYanglaos();
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


    @ApiOperation("更新订阅的资讯分类信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupNames", value = "资讯分类，多个以逗号隔开", required = true, paramType = "query",
                    dataType = "string"),
            @ApiImplicitParam(name = "type", value = "资讯分类，1健康，2 安防，3广播，4视频，5时政，6养老", required = true, paramType = "query",
                    dataType = "int")})
    @RequestMapping(value = "/update-groups", method = {RequestMethod.GET, RequestMethod.POST})
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
            case Constant.INFOR_DAILY:
                keys = Constant.DAILY_NAME;
                mySub.setDailys(mySubs);
                break;
            case Constant.INFOR_YANGLAO:
                keys = Constant.YANGLAO_NAME;
                mySub.setYanglaos(mySubs);
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

    @ApiOperation("健康资讯信息详情")
    @ApiImplicitParam(name = "inforid", value = "资讯id", required = true, paramType = "query",dataType = "string")
    @RequestMapping(value = "/news-infor", method = {RequestMethod.GET, RequestMethod.POST})
    public String infor(String inforid, HttpServletRequest request, HttpServletResponse response){

        InforBo inforBo = inforService.findById(inforid);
        if (inforBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.INFOR_IS_NULL.getIndex(),
                    ERRORCODE.INFOR_IS_NULL.getReason());
        }

        InforVo inforVo = new InforVo();
        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(inforBo.getId(), userBo.getId());
            inforVo.setSelfSub(thumbsupBo != null);
            asyncController.updateUserReadHis(userBo.getId(),inforBo.getClassName(),"", Constant.INFOR_HEALTH);
            asyncController.addUserReadhis(userBo.getId(), inforid, Constant.INFOR_HEALTH, inforBo.getModule(),
                    inforBo.getClassName());
        }
        updateInforNum(inforid, Constant.INFOR_HEALTH, 1, Constant.VISIT_NUM);
        asyncController.updateInforHistroy(inforid, inforBo.getClassName(), Constant.INFOR_HEALTH);
        inforVo.setInforid(inforBo.getId());
        BeanUtils.copyProperties(inforBo, inforVo);
        inforVo.setThumpsubNum(inforBo.getThumpsubNum());
        inforVo.setCommentNum(inforBo.getCommnetNum());
        inforVo.setReadNum(inforBo.getVisitNum());
        inforVo.setImageUrls(inforBo.getImageUrls());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVo", inforVo);
        return JSONObject.fromObject(map).toString();
    }

    @ApiOperation("安防资讯信息详情")
    @ApiImplicitParam(name = "inforid", value = "资讯id", required = true, paramType = "query",dataType = "string")
    @RequestMapping(value = "/security-infor", method = {RequestMethod.GET, RequestMethod.POST})
    public String securitys(String inforid, HttpServletRequest request, HttpServletResponse response){
        SecurityBo securityBo = inforService.findSecurityById(inforid);
        if (securityBo == null) {
            return CommonUtil.toErrorResult(
                    ERRORCODE.INFOR_IS_NULL.getIndex(),
                    ERRORCODE.INFOR_IS_NULL.getReason());
        }
        updateInforNum(inforid, Constant.INFOR_SECRITY, 1, Constant.VISIT_NUM);
        SecurityVo securityVo = new SecurityVo();
        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(inforid, userBo.getId());
            securityVo.setSelfSub(thumbsupBo != null);
            asyncController.updateUserReadHis(userBo.getId(),securityBo.getNewsType(),"",Constant.INFOR_SECRITY);
            asyncController.addUserReadhis(userBo.getId(), inforid, Constant.INFOR_SECRITY, securityBo.getNewsType(), "");
        }
        asyncController.updateInforHistroy(inforid, securityBo.getNewsType(), Constant.INFOR_SECRITY);
        securityVo.setInforid(securityBo.getId());
        BeanUtils.copyProperties(securityBo, securityVo);
        securityVo.setThumpsubNum(securityBo.getThumpsubNum());
        securityVo.setCommentNum(securityBo.getCommnetNum());
        securityVo.setReadNum(securityBo.getVisitNum());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("securityVo", securityVo);
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("获取安全防范指定分类下资讯信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newsType", value = "安防分类", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "inforTime", value = "分页时最后一条时间，若为空表示第一页开始",paramType = "query",dataType = "string"),
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true,paramType = "query", dataType =
                    "int")})
    @RequestMapping(value = "/security-list", method = {RequestMethod.GET, RequestMethod.POST})
    public String securityList(@RequestParam String newsType,
                              @RequestParam(required = false)String inforTime,
                              @RequestParam int limit,
                              HttpServletRequest request, HttpServletResponse response){
        List<SecurityBo> securityBos = inforService.findSecurityByType(newsType, inforTime, limit);

        LinkedList<SecurityVo> vos = new LinkedList<>();
        for (SecurityBo securityBo : securityBos) {
            SecurityVo securityVo = new SecurityVo();
            BeanUtils.copyProperties(securityBo, securityVo);
            securityVo.setInforid(securityBo.getId());
            securityVo.setNewsType(newsType);
            securityVo.setText("");
            securityVo.setReadNum(securityBo.getVisitNum());
            securityVo.setCommentNum(securityBo.getCommnetNum());
            securityVo.setThumpsubNum(securityBo.getThumpsubNum());
            vos.add(securityVo);
        }
        UserBo userBo =  getUserLogin(request);
        if (userBo != null) {
            asyncController.updateUserReadHis(userBo.getId(), newsType,"", Constant.INFOR_SECRITY);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("securityVoList", vos);
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("资讯评论或回复评论，需要登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inforid", value = "资讯id", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "countent", value = "评论内容",paramType = "query",dataType = "string"),
            @ApiImplicitParam(name = "parentid", value = "父评论id,为空表示评资讯", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "inforType", value = "1健康，2 安防，3广播，4视频, 5时政，6养老", required = true,paramType =
                    "query", dataType = "int")})
    @RequestMapping(value = "/add-comment", method = {RequestMethod.GET, RequestMethod.POST})
    public String addComment(@RequestParam String inforid, @RequestParam String countent,
                             String parentid, int inforType,
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
        commentBo.setSubType(inforType);
        commentService.insert(commentBo);
        updateInforNum(inforid, inforType, 1, Constant.COMMENT_NUM);
        userService.addUserLevel(userBo.getId(),1, Constant.LEVEL_COMMENT, 0);
        infotHostAsync(inforid, inforType);
        CommentVo commentVo = comentBo2Vo(commentBo);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("commentVo", commentVo);
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 阅读点赞评论等数据更新
     * @param inforid
     * @param inforType  资讯类型
     * @param num
     * @param numType 更新数据类型， 阅读、点赞等
     */
    private void updateInforNum(String inforid, int inforType, int num, int numType){
        RLock lock = redisServer.getRLock(inforid.concat(String.valueOf(numType)));
        try {
            lock.lock(2, TimeUnit.SECONDS);
            switch (inforType){
                case Constant.INFOR_HEALTH:
                    inforService.updateInforNum(inforid, numType, num);
                    break;
                case Constant.INFOR_SECRITY:
                    inforService.updateSecurityNum(inforid, numType, num);
                    break;
                case Constant.INFOR_RADIO:
                    inforService.updateRadioNum(inforid, numType, num);
                    break;
                case Constant.INFOR_VIDEO:
                    inforService.updateVideoNum(inforid, numType, num);
                    break;
                case Constant.INFOR_DAILY:
                    inforService.updateDailynewsByType(inforid, numType, num);
                    break;
                case Constant.INFOR_YANGLAO:
                    inforService.updateYanglaoByType(inforid, numType, num);
                    break;
                default:
                    break;
            }
        } finally {
            lock.unlock();
        }
    }

    @ApiOperation("获取评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inforid", value = "资讯id", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "page", value = "分页页码",paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true,paramType = "query", dataType =
                    "int")})
    @RequestMapping(value = "/get-comments", method = {RequestMethod.GET, RequestMethod.POST})
    public String getComment(@RequestParam String inforid, int page, int limit,
                             HttpServletRequest request, HttpServletResponse response){
        List<CommentBo> commentBos = commentService.selectCommentByType(Constant.INFOR_TYPE, inforid,
                page,limit);
        List<CommentVo> commentVos = new ArrayList<>();
        for (CommentBo commentBo : commentBos) {
            commentVos.add(comentBo2Vo(commentBo));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("commentVoList", commentVos);
        return JSONObject.fromObject(map).toString();
    }

    @ApiOperation("删除评论，需要登录，只能删除自己的")
    @ApiImplicitParam(name = "commnetId", value = "评论id", required = true, paramType = "query",dataType = "string")
    @RequestMapping(value = "/delete-comment", method = {RequestMethod.GET, RequestMethod.POST})
    public String delelteComment(String commnetId,
                             HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        CommentBo commentBo = commentService.findById(commnetId);
        if (commentBo != null) {
            if (commentBo.getCreateuid().equals(userBo.getId())){
                commentService.delete(commnetId);
                String inforid = commentBo.getTargetid();
                updateInforNum(inforid, commentBo.getSubType(), -1, Constant.COMMENT_NUM);
            } else {
                return CommonUtil.toErrorResult(ERRORCODE.NOTE_NOT_MASTER.getIndex(),
                        ERRORCODE.NOTE_NOT_MASTER.getReason());
            }
        }
        return Constant.COM_RESP;
    }


    @ApiOperation("资讯或评论点赞，需要登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetid", value = "资讯或评论id", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "type", value = "0 资讯点赞，1评论点赞", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "inforType", value = "1健康，2 安防，3广播，4视频，5时政，6养老", required = true,paramType = "query", dataType =
                    "int")})
    @RequestMapping(value = "/thumbsup",  method = {RequestMethod.GET, RequestMethod.POST})
    public String inforThumbsup(@RequestParam String targetid, @RequestParam int type, int inforType,
            HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        ThumbsupBo thumbsupBo = thumbsupService.findHaveOwenidAndVisitorid(targetid, userBo.getId());
        String inforid = targetid;
        if (null == thumbsupBo) {
            thumbsupBo = new ThumbsupBo();
            if (type == 0) {
                thumbsupBo.setType(Constant.INFOR_TYPE);
                updateInforNum(inforid, inforType, 1, Constant.THUMPSUB_NUM);
            } else if (type == 1) {
                thumbsupBo.setType(Constant.INFOR_COM_TYPE);
                CommentBo commentBo = commentService.findById(targetid);
                if (commentBo == null) {
                    return CommonUtil.toErrorResult(ERRORCODE.COMMENT_IS_NULL.getIndex(),
                            ERRORCODE.COMMENT_IS_NULL.getReason());
                }
                commentService.updateThumpsubNum(commentBo.getId(), 1);
                inforid = commentBo.getTargetid();
            } else {
                return CommonUtil.toErrorResult(
                        ERRORCODE.TYPE_ERROR.getIndex(), ERRORCODE.TYPE_ERROR.getReason());
            }
            thumbsupBo.setOwner_id(targetid);
            thumbsupBo.setImage(userBo.getHeadPictureName());
            thumbsupBo.setVisitor_id(userBo.getId());
            thumbsupBo.setCreateuid(userBo.getId());
            thumbsupService.insert(thumbsupBo);
        } else {
            if (thumbsupBo.getDeleted() == Constant.DELETED) {
                thumbsupService.udateDeleteById(thumbsupBo.getId());
                if (thumbsupBo.getType() == Constant.INFOR_TYPE) {
                    updateInforNum(inforid, inforType, 1, Constant.THUMPSUB_NUM);
                } else {
                    commentService.updateThumpsubNum(thumbsupBo.getOwner_id(), 1);
                }
            }
        }
        infotHostAsync(inforid, inforType);
        return Constant.COM_RESP;
    }

    /**
     * 热度信息更新
     * @param inforid
     * @param inforType
     */
    private void infotHostAsync(String inforid, int inforType){
        //根据每条资讯id加锁
        String module = "";
        switch (inforType){
            case Constant.INFOR_HEALTH:
                InforBo inforBo = inforService.findById(inforid);
                module = inforBo != null ? inforBo.getClassName() : "";
                break;
            case Constant.INFOR_SECRITY:
                SecurityBo securityBo = inforService.findSecurityById(inforid);
                module = securityBo != null ? securityBo.getNewsType() : "";
                break;
            case Constant.INFOR_RADIO:
                BroadcastBo broadcastBo = inforService.findBroadById(inforid);
                module = broadcastBo != null ? broadcastBo.getModule() : "";
                break;
            case Constant.INFOR_VIDEO:
                VideoBo videoBo = inforService.findVideoById(inforid);
                module = videoBo != null ? videoBo.getModule() : "";
                break;
            case Constant.INFOR_DAILY:
                DailynewsBo dailynewsBo = inforService.findByDailynewsId(inforid);
                module = dailynewsBo != null ? dailynewsBo.getClassName() : "";
                break;
            case Constant.INFOR_YANGLAO:
                YanglaoBo yanglaoBo = inforService.findByYanglaoId(inforid);
                module = yanglaoBo != null ? yanglaoBo.getClassName() : "";
                break;
            default:
                break;
        }
        if (!"".equals(module)){
            asyncController.updateInforHistroy(inforid, module, inforType);
        }
    }

    @ApiOperation("资讯或评论取消点赞，需要登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetid", value = "资讯或评论id", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "type", value = "0 资讯，1评论", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "inforType", value = "1健康，2 安防，3广播，4视频，5时政，6养老", required = true,paramType = "query", dataType =
                    "int")})
    @RequestMapping(value = "/cancal-thumbsup", method = {RequestMethod.GET, RequestMethod.POST})
    public String cancelThumbsup(@RequestParam String targetid, @RequestParam int type, int inforType,
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
            if (type == 0) {
                updateInforNum(targetid, inforType, -1, Constant.THUMPSUB_NUM);
            }else if (type == 1) {
                CommentBo commentBo = commentService.findById(targetid);
                if (commentBo != null) {
                    inforService.updateThumpsub(commentBo.getTargetid(), -1);
                }
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
    @ApiOperation("资讯主页推荐top50")
    @GetMapping("/home-health")
    public String homeHealth(HttpServletRequest request, HttpServletResponse response){
        List<InforRecomBo> recomBos = inforRecomService.findRecomByType(Constant.INFOR_HEALTH, 50);
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
    @ApiOperation("资讯主页推荐top4,带图片")
    @GetMapping("/home-top4")
    public String homeHealthTop(HttpServletRequest request, HttpServletResponse response){
        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
        List<InforVo> top4 = null;
        if (cache.containsKey("top4")){
            top4 = (List<InforVo>) cache.get("top4");
        } else {
            top4 = new ArrayList<>();
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
            cache.put("top4",top4);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVoList", top4);
        return JSONObject.fromObject(map).toString();
    }


    private void bo2vo(InforBo inforBo, InforVo inforVo){
        inforVo.setThumpsubNum(inforBo.getThumpsubNum());
        inforVo.setCommentNum(inforBo.getCommnetNum());
        inforVo.setReadNum(inforBo.getVisitNum());
        inforVo.setInforid(inforBo.getId());
        inforVo.setClassName(inforBo.getClassName());
        inforVo.setImageUrls(inforBo.getImageUrls());
        inforVo.setSource(inforBo.getSource());
        inforVo.setTitle(inforBo.getTitle());
        inforVo.setTime(inforBo.getTime());
        inforVo.setSourceUrl(inforBo.getSourceUrl());
        inforVo.setInforid(inforBo.getId());
        inforVo.setNum(inforBo.getNum());
    }

    /**
     * 健康个性推荐
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("健康资讯个性推荐")
    @GetMapping("/user-health")
    public String userHealth(HttpServletRequest request, HttpServletResponse response){

        UserBo userBo = getUserLogin(request);
        List<InforVo> inforVos = new ArrayList<>();
        int num = 0;
        List<InforRecomBo> recomBos = null;
        if (userBo != null) {
            InforUserReadBo readBo = inforRecomService.findUserReadByUserid(userBo.getId());
            if (readBo == null) {
                readBo = new InforUserReadBo();
                readBo.setUserid(userBo.getId());
                inforRecomService.addUserRead(readBo);
            } else {
                LinkedHashSet<String> set = readBo.getHealths();
                if (set.size() > 0) {
                    recomBos = inforRecomService.findRecomByTypeAndModule(Constant.INFOR_HEALTH, set);
                }
            }
        }
        if (recomBos == null) {
            recomBos = inforRecomService.findRecomByType(Constant.INFOR_HEALTH, 50);
        } else if (recomBos.size() < 50) {
            List<InforRecomBo> recoms = inforRecomService.findRecomByType(Constant.INFOR_HEALTH,
                    50 - recomBos.size());
            recomBos.addAll(recoms);
        }
        if (recomBos != null){
            List<String> ids = new ArrayList<>();
            for (InforRecomBo recomBo : recomBos) {
                ids.add(recomBo.getInforid());
            }
            List<InforBo> inforBos = inforService.findHealthByIds(ids);
            for (InforBo inforBo : inforBos) {
                InforVo inforVo = new InforVo();
                bo2vo(inforBo, inforVo);
                num++;
                inforVos.add(inforVo);
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
    @ApiOperation("安防资讯个性推荐")
    @GetMapping("/user-securitys")
    public String userSecritys(HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        List<SecurityVo> inforVos = new ArrayList<>();
        int num = 0;
        List<InforRecomBo> recomBos = null;
        if (userBo != null){
            InforUserReadBo readBo = inforRecomService.findUserReadByUserid(userBo.getId());
            if (readBo == null) {
                readBo = new InforUserReadBo();
                readBo.setUserid(userBo.getId());
                inforRecomService.addUserRead(readBo);
            } else {
                LinkedHashSet<String> set = readBo.getSecuritys();
                if (set.size() > 0) {
                    recomBos = inforRecomService.findRecomByTypeAndModule(Constant.INFOR_SECRITY, set);
                }
            }
        }
        if (recomBos == null) {
            recomBos = inforRecomService.findRecomByType(Constant.INFOR_SECRITY, 50);
        } else if (recomBos.size() < 50) {
            List<InforRecomBo> recoms = inforRecomService.findRecomByType(Constant.INFOR_SECRITY,
                    50 - recomBos.size());
            recomBos.addAll(recoms);
        }
        if (recomBos != null){
            List<String> ids = new ArrayList<>();
            for (InforRecomBo recomBo : recomBos) {
                ids.add(recomBo.getInforid());
            }
            List<SecurityBo> inforBos = inforService.findSecurityByIds(ids);
            for (SecurityBo securityBo : inforBos) {
                SecurityVo inforVo = new SecurityVo();
                BeanUtils.copyProperties(securityBo,inforVo);
                inforVo.setText("");
                inforVo.setInforid(securityBo.getId());
                inforVo.setReadNum(securityBo.getVisitNum());
                inforVo.setCommentNum(securityBo.getCommnetNum());
                inforVo.setThumpsubNum(securityBo.getThumpsubNum());
                num++;
                inforVos.add(inforVo);
            }
        }
        if (num < 50) {
            List<SecurityBo> securityBos = inforService.findSecurityByLimit(50 - num);
            for (SecurityBo securityBo : securityBos) {
                SecurityVo securityVo = new SecurityVo();
                BeanUtils.copyProperties(securityBo,securityVo);
                securityVo.setText("");
                securityVo.setInforid(securityBo.getId());
                securityVo.setReadNum(securityBo.getVisitNum());
                securityVo.setCommentNum(securityBo.getCommnetNum());
                securityVo.setThumpsubNum(securityBo.getThumpsubNum());
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
    @ApiOperation("广播资讯个性推荐")
    @GetMapping("/user-radios")
    public String userRadios(HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        int num = 0;
        List<InforGroupRecomBo> myRecomBos = null;
        LinkedHashSet<String> set = null;
        if (userBo != null) {
            InforUserReadBo readBo = inforRecomService.findUserReadByUserid(userBo.getId());
            if (readBo == null) {
                readBo = new InforUserReadBo();
                readBo.setUserid(userBo.getId());
                inforRecomService.addUserRead(readBo);
            } else {
                set = readBo.getVideos();
                if (set.size() > 0) {
                    myRecomBos = inforRecomService.findInforGroupByModule(Constant.INFOR_RADIO, set);
                }
            }
        }
        if (myRecomBos == null) {
            myRecomBos = inforRecomService.findInforGroupWithoutModule(Constant.INFOR_RADIO, null, 50);
        } else if (myRecomBos.size() < 50) {
            List<InforGroupRecomBo> recomBos = inforRecomService.findInforGroupWithoutModule(Constant.INFOR_RADIO, set,
                    50 - myRecomBos.size());
            myRecomBos.addAll(recomBos);
        }

        LinkedHashSet<String> modules = new LinkedHashSet<>();
        LinkedHashSet<String> classNames = new LinkedHashSet<>();
        List<RadioClassVo> classVos = new ArrayList<>();
        if (myRecomBos != null) {
            for (InforGroupRecomBo recomBo : myRecomBos) {
                modules.add(recomBo.getModule());
                classNames.add(recomBo.getClassName());
                num ++;
            }
            List<BroadcastBo> radioBos = inforService.selectRadioClassByGroups(modules, classNames);
            addRadios(radioBos, classVos);
        }
        if  (num < 50) {
            List<BroadcastBo> radioBos = inforService.findRadioByLimit(modules, classNames, 50-num);
            addRadios(radioBos, classVos);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ret", 0);
        jsonObject.put("radioClasses", classVos);
        return jsonObject.toString();
    }


    /**
     * 广播个性推荐
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("视频资讯个性推荐")
    @GetMapping("/user-videos")
    public String userVideos(HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        int num = 0;
        List<InforGroupRecomBo> myRecomBos = null;
        LinkedHashSet<String> set = null;
        String userid = "";
        if (userBo != null) {
            InforUserReadBo readBo = inforRecomService.findUserReadByUserid(userBo.getId());
            if (readBo == null) {
                readBo = new InforUserReadBo();
                readBo.setUserid(userBo.getId());
                inforRecomService.addUserRead(readBo);
            } else {
                set = readBo.getVideos();
                if (set.size() > 0) {
                    myRecomBos = inforRecomService.findInforGroupByModule(Constant.INFOR_VIDEO, set);
                }
            }
            userid = userBo.getId();
        }
        if (myRecomBos == null) {
            myRecomBos = inforRecomService.findInforGroupWithoutModule(Constant.INFOR_VIDEO, null, 50);
        } else if (myRecomBos.size() < 50) {
            List<InforGroupRecomBo> recomBos = inforRecomService.findInforGroupWithoutModule(Constant.INFOR_VIDEO, set,
                    50 - myRecomBos.size());
            myRecomBos.addAll(recomBos);
        }
        LinkedList<String> modules = new LinkedList<>();
        LinkedList<String> classNames = new LinkedList<>();
        List<VideoClassVo> classVos = new ArrayList<>();
        if (myRecomBos != null) {
            for (InforGroupRecomBo recomBo : myRecomBos) {
                String module = recomBo.getModule();
                String className = recomBo.getClassName();
                if (StringUtils.isEmpty(module) || StringUtils.isEmpty(className)) {
                    continue;
                }
                VideoBo bo = inforService.findVideoByFirst(module, className);
                if (bo != null) {
                    VideoClassVo classVo = new VideoClassVo();
                    classVo.setInforid(bo.getFirstId());
                    classVo.setModule(bo.getModule());
                    classVo.setTitle(bo.getClassName());
                    classVo.setTotalVisit(bo.getVisitNum());
                    classVo.setSource(bo.getSource());
                    classVo.setShareNum(bo.getFirstShare());
                    classVo.setThumpsubNum(bo.getFirstThump());
                    classVo.setCommentNum(bo.getFirstComment());
                    classVo.setUrl(bo.getFirstUrl());
                    if (!"".equals(userid)) {
                        ThumbsupBo thumbsupBo = thumbsupService.findHaveOwenidAndVisitorid(bo.getFirstId(),userid);
                        classVo.setSelfSub(thumbsupBo != null);
                    }
                    classVos.add(classVo);
                    num ++;
                }
                modules.add(module);
                classNames.add(className);
            }
        }
        if  (num < 50) {
            List<VideoBo> videoBos = inforService.findVideoByLimit(modules, classNames, 50-num);
            addVideo(videoBos, classVos, userid);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ret", 0);
        jsonObject.put("videoClasses", classVos);
        return jsonObject.toString();
    }

    private void addVideo(List<VideoBo> videoBos, List<VideoClassVo> classVos, String userid){
        if (videoBos != null) {
            for (VideoBo bo : videoBos) {
                VideoClassVo classVo = new VideoClassVo();
                classVo.setInforid(bo.getFirstId());
                classVo.setModule(bo.getModule());
                classVo.setTitle(bo.getClassName());
                classVo.setTotalVisit(bo.getVisitNum());
                classVo.setSource(bo.getSource());
                classVo.setShareNum(bo.getFirstShare());
                classVo.setThumpsubNum(bo.getFirstThump());
                classVo.setCommentNum(bo.getFirstComment());
                classVo.setUrl(bo.getFirstUrl());
                if (!"".equals(userid)) {
                    ThumbsupBo thumbsupBo = thumbsupService.findHaveOwenidAndVisitorid(bo.getFirstId(),userid);
                    classVo.setSelfSub(thumbsupBo != null);
                }
                classVos.add(classVo);
            }
        }
    }

    /**
     * 
     * @param subBo
     * @param cache
     */
    private void addCacheToSub(InforSubscriptionBo subBo, RMapCache<String, Object> cache){
        subBo.setSubscriptions((LinkedHashSet<String>) cache.get(Constant.HEALTH_NAME));
        subBo.setSecuritys((LinkedHashSet<String>) cache.get(Constant.SECRITY_NAME));
        subBo.setRadios((LinkedHashSet<String>) cache.get(Constant.RADIO_NAME));
        subBo.setVideos((LinkedHashSet<String>) cache.get(Constant.VIDEO_NAME));
        subBo.setDailys((LinkedHashSet<String>) cache.get(Constant.DAILY_NAME));
        subBo.setYanglaos((LinkedHashSet<String>) cache.get(Constant.YANGLAO_NAME));
    }


    private void initCache(RMapCache<String, Object> cache ){
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
        HashSet<String> dailyTypes = new LinkedHashSet<>();
        List<DailynewsBo> dailynewsBos =  inforService.selectDailynewsGroups();
        for (DailynewsBo videoBo : dailynewsBos) {
            dailyTypes.add(videoBo.getId());
        }
        HashSet<String> yanglaoTypes = new LinkedHashSet<>();
        List<YanglaoBo> yanglaoBos =  inforService.selectYanglaoGroups();
        for (YanglaoBo videoBo : yanglaoBos) {
            yanglaoTypes.add(videoBo.getId());
        }
        cache.clear();
        cache.put(Constant.SECRITY_NAME, securityTypes, 0, TimeUnit.MINUTES);
        cache.put(Constant.HEALTH_NAME, groupTypes, 0, TimeUnit.MINUTES);
        cache.put(Constant.RADIO_NAME, broadTypes, 0, TimeUnit.MINUTES);
        cache.put(Constant.VIDEO_NAME, videoTypes, 0, TimeUnit.MINUTES);
        cache.put(Constant.DAILY_NAME, dailyTypes, 0, TimeUnit.MINUTES);
        cache.put(Constant.YANGLAO_NAME, yanglaoTypes, 0, TimeUnit.MINUTES);
    }





    @ApiOperation("收藏单条咨询信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inforid", value = "资讯id", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "inforType", value = "1健康，2 安防，3广播，4视频，5时政，6养老", required = true,paramType = "query", dataType =
                    "int")})
    @RequestMapping(value = "/collect-infor", method = {RequestMethod.GET, RequestMethod.POST})
    public String collectInfor(@RequestParam String inforid, @RequestParam int inforType,
                               HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        CollectBo collectBo = collectService.findByUseridAndTargetid(userBo.getId(), inforid);
        if (collectBo == null) {
            collectBo = new CollectBo();
            collectBo.setUserid(userBo.getId());
            collectBo.setType(Constant.COLLET_URL);
            collectBo.setSub_type(Constant.INFOR_TYPE);
            collectBo.setTargetid(inforid);
        } else {
            return CommonUtil.toErrorResult(ERRORCODE.COLLECT_EXIST.getIndex(),
                    ERRORCODE.COLLECT_EXIST.getReason());
        }
        collectBo.setSourceType(inforType);
        switch (inforType){
            case Constant.INFOR_HEALTH:
                InforBo inforBo = inforService.findById(inforid);
                collectBo.setTitle(inforBo.getTitle());
                collectBo.setSource(inforBo.getModule());
                if (!CommonUtil.isEmpty(inforBo.getImageUrls())) {
                    collectBo.setTargetPic(inforBo.getImageUrls().getFirst());
                }
                break;
            case Constant.INFOR_SECRITY:
                SecurityBo securityBo = inforService.findSecurityById(inforid);
                collectBo.setTitle(securityBo.getTitle());
                collectBo.setSource(securityBo.getNewsType());
                break;
            case Constant.INFOR_RADIO:
                BroadcastBo broadcastBo = inforService.findBroadById(inforid);
                collectBo.setTitle(broadcastBo.getTitle());
                collectBo.setSource(broadcastBo.getModule());
                collectBo.setPath(broadcastBo.getBroadcast_url());
                break;
            case Constant.INFOR_VIDEO:
                VideoBo videoBo = inforService.findVideoById(inforid);
                collectBo.setTitle(videoBo.getTitle());
                collectBo.setSource(videoBo.getModule());
                collectBo.setVideo(videoBo.getUrl());
                collectBo.setTargetPic(videoBo.getPoster());
                break;
            case Constant.INFOR_DAILY:
                DailynewsBo dailynewsBo = inforService.findByDailynewsId(inforid);
                collectBo.setTitle(dailynewsBo.getTitle());
                collectBo.setSource(dailynewsBo.getClassName());
                if (!CommonUtil.isEmpty(dailynewsBo.getImageUrls())) {
                    collectBo.setTargetPic(dailynewsBo.getImageUrls().getFirst());
                }
                break;
            case Constant.INFOR_YANGLAO:
                YanglaoBo yanglaoBo = inforService.findByYanglaoId(inforid);
                collectBo.setTitle(yanglaoBo.getTitle());
                collectBo.setSource(yanglaoBo.getClassName());
                if (!CommonUtil.isEmpty(yanglaoBo.getImageUrls())) {
                    collectBo.setTargetPic(yanglaoBo.getImageUrls().getFirst());
                }
                break;
            default:
                break;
        }
        collectService.insert(collectBo);
        updateInforNum(inforid, inforType, 1, Constant.COLLECT_NUM);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("col-time", CommonUtil.time2str(collectBo.getCreateTime()));
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("收藏广播或视频资讯合集")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "module", value = "广播或视频大分类", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "className", value = "广播或视频二级分类", required = true, paramType = "query",
                    dataType = "string"),
            @ApiImplicitParam(name = "inforid", value = "合集首条资讯id", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "inforType", value = "1健康，2 安防，3广播，4视频，5时政，6养老", required = true,paramType = "query", dataType =
                    "int")})
    @RequestMapping(value = "/collect-classes", method = {RequestMethod.GET, RequestMethod.POST})
    public String collectClasses(String module, String className, String inforid, int inforType,
                               HttpServletRequest request, HttpServletResponse response){

        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        String userid = userBo.getId();
        CollectBo collectBo = collectService.findInforClasses(userid, module, className, inforType);
        if (collectBo == null) {
            collectBo = new CollectBo();
            collectBo.setUserid(userid);
            collectBo.setType(Constant.COLLET_URL);
            collectBo.setSub_type(Constant.INFOR_TYPE);
            collectBo.setFirstid(inforid);
            collectBo.setSourceType(inforType);
            collectBo.setModule(module);
            collectBo.setClassName(className);
            if (inforType == Constant.INFOR_VIDEO) {
                VideoBo videoBo = inforService.findVideoById(inforid);
                collectBo.setVideo(videoBo.getUrl());
                collectBo.setTargetPic(videoBo.getPoster());
            }
        } else {
            return CommonUtil.toErrorResult(ERRORCODE.COLLECT_EXIST.getIndex(),
                    ERRORCODE.COLLECT_EXIST.getReason());
        }
        collectService.insert(collectBo);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("col-time", CommonUtil.time2str(collectBo.getCreateTime()));
        return JSONObject.fromObject(map).toString();
    }

    /**
     * 转发到我的动态
     */
    @ApiOperation("转发到我的动态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inforid", value = "资讯id", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "inforType", value = "1健康，2 安防，3广播，4视频，5时政，6养老", required = true,paramType = "query", dataType =
                    "int"),
            @ApiImplicitParam(name = "view", value = "转发评论", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "landmark", value = "转发时地标描述", required = true, paramType = "query", dataType =
                    "string")})
    @RequestMapping(value = "/forward-dynamic", method = {RequestMethod.GET, RequestMethod.POST})
    public String forwardDynamic(@RequestParam String inforid, @RequestParam int inforType, String view,
                                 String landmark, HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        DynamicBo dynamicBo = new DynamicBo();
        dynamicBo.setLandmark(landmark);
        switch (inforType){
            case Constant.INFOR_HEALTH:
                InforBo inforBo = inforService.findById(inforid);
                dynamicBo.setTitle(inforBo.getTitle());
                dynamicBo.setSourceName(inforBo.getModule());
                if (inforBo.getImageUrls() != null) {
                    dynamicBo.setPhotos(new LinkedHashSet<>(inforBo.getImageUrls()));
                }
                break;
            case Constant.INFOR_SECRITY:
                SecurityBo securityBo = inforService.findSecurityById(inforid);
                dynamicBo.setTitle(securityBo.getTitle());
                dynamicBo.setSourceName(securityBo.getNewsType());
                break;
            case Constant.INFOR_RADIO:
                BroadcastBo broadcastBo = inforService.findBroadById(inforid);
                dynamicBo.setTitle(broadcastBo.getTitle());
                dynamicBo.setSourceName(broadcastBo.getModule());
                break;
            case Constant.INFOR_VIDEO:
                VideoBo videoBo = inforService.findVideoById(inforid);
                dynamicBo.setTitle(videoBo.getTitle());
                dynamicBo.setSourceName(videoBo.getModule());
                if (videoBo.getPoster() != null) {
                    dynamicBo.setVideoPic(videoBo.getPoster());
                }
                dynamicBo.setPicType("video");
                dynamicBo.setVideo(videoBo.getUrl());
                break;
            case Constant.INFOR_DAILY:
                DailynewsBo dailynewsBo = inforService.findByDailynewsId(inforid);
                dynamicBo.setTitle(dailynewsBo.getTitle());
                dynamicBo.setSourceName(dailynewsBo.getClassName());
                if (dailynewsBo.getImageUrls() != null) {
                    dynamicBo.setPhotos(new LinkedHashSet<>(dailynewsBo.getImageUrls()));
                }
                break;
            case Constant.INFOR_YANGLAO:
                YanglaoBo yanglaoBo = inforService.findByYanglaoId(inforid);
                dynamicBo.setTitle(yanglaoBo.getTitle());
                dynamicBo.setSourceName(yanglaoBo.getClassName());
                if (yanglaoBo.getImageUrls() != null) {
                    dynamicBo.setPhotos(new LinkedHashSet<>(yanglaoBo.getImageUrls()));
                }
                break;
            default:
                break;
        }

        dynamicBo.setCreateuid(userBo.getId());
        dynamicBo.setView(view);
        dynamicBo.setMsgid(inforid);
        dynamicBo.setType(Constant.INFOR_TYPE);
        dynamicService.addDynamic(dynamicBo);
        updateInforNum(inforid, inforType, 1, Constant.SHARE_NUM);
        updateDynamicNums(userBo.getId(), 1,dynamicService, redisServer);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("dynamicid", dynamicBo.getId());
        return JSONObject.fromObject(map).toString();
    }


    /**
     * 转发到我的动态
     */
    @ApiOperation("转发指定的圈子")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "circleid", value = "转发圈子id", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "inforid", value = "资讯id", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "inforType", value = "1健康，2 安防，3广播，4视频，5时政，6养老", required = true,paramType = "query", dataType =
                    "int")})
    @RequestMapping(value = "/forward-circle", method = {RequestMethod.GET, RequestMethod.POST})
    public String forwardCircle(String circleid, String inforid, int inforType,
                                HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        CircleBo circleBo = circleService.selectById(circleid);
        if (circleBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(),
                    ERRORCODE.CIRCLE_IS_NULL.getReason());
        }
        NoteBo noteBo = new NoteBo();
        noteBo.setSourceid(inforid);
        noteBo.setNoteType(1);
        noteBo.setInforType(inforType);
        noteBo.setForward(1);
        noteBo.setCreateuid(userBo.getId());
        noteBo.setCircleId(circleid);
        noteService.insert(noteBo);
        updateInforNum(inforid, inforType, 1, Constant.SHARE_NUM);
        return Constant.COM_RESP;
    }


    @ApiOperation("根据类型获取最后5条历史阅读信息")
    @ApiImplicitParam(name = "inforType", value = "1健康，2 安防，3广播，4视频,5时政，6养老, 0查询所有数据", required = true, paramType =
            "query", dataType = "int")
    @RequestMapping(value = "/last-read", method = {RequestMethod.GET, RequestMethod.POST})
    public String lastReadByType(int inforType,
                                HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        String userid = userBo.getId();
        List<UserReadHisBo> hisBos = inforService.findByUserAndInfor(userid, inforType);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ret", 0);
        List<InforAllVo> inforVos = new ArrayList<>();
        for (UserReadHisBo hisBo : hisBos)  {
            int type = hisBo.getInforType();
            InforAllVo inforVo = new InforAllVo();
            String inforid = hisBo.getInforid();
            switch (type){
                case Constant.INFOR_HEALTH:
                    InforBo inforBo = inforService.findById(inforid);
                    if (inforBo == null) {
                        continue;
                    }
                    BeanUtils.copyProperties(inforBo, inforVo);
                    ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(inforid, userid);
                    inforVo.setSelfSub(thumbsupBo != null);
                    inforVo.setInforid(hisBo.getInforid());
                    inforVo.setThumpsubNum(inforBo.getThumpsubNum());
                    inforVo.setCommentNum(inforBo.getCommnetNum());
                    inforVo.setReadNum(inforBo.getVisitNum());
                    inforVo.setText("");
                    inforVo.setInforType(type);
                    inforVos.add(inforVo);
                    break;
                case Constant.INFOR_SECRITY:
                    SecurityBo securityBo = inforService.findSecurityById(inforid);
                    if (securityBo == null) {
                        continue;
                    }
                    BeanUtils.copyProperties(securityBo, inforVo);
                    thumbsupBo = thumbsupService.getByVidAndVisitorid(inforid, userid);
                    inforVo.setSelfSub(thumbsupBo != null);
                    asyncController.updateUserReadHis(userid,securityBo.getNewsType(),"",Constant.INFOR_SECRITY);
                    inforVo.setInforid(hisBo.getInforid());
                    inforVo.setThumpsubNum(securityBo.getThumpsubNum());
                    inforVo.setCommentNum(securityBo.getCommnetNum());
                    inforVo.setReadNum(securityBo.getVisitNum());
                    inforVo.setText("");
                    inforVo.setInforType(type);
                    inforVos.add(inforVo);
                    break;
                case Constant.INFOR_RADIO:
                    BroadcastBo broadcastBo = inforService.findBroadById(inforid);
                    if (broadcastBo == null) {
                        continue;
                    }
                    BeanUtils.copyProperties(broadcastBo, inforVo);
                    inforVo.setInforid(broadcastBo.getId());
                    inforVo.setReadNum(broadcastBo.getVisitNum());
                    inforVo.setCommentNum(broadcastBo.getCommnetNum());
                    inforVo.setThumpsubNum(broadcastBo.getThumpsubNum());
                    inforVo.setIntro("");
                    inforVo.setInforType(type);
                    inforVos.add(inforVo);
                    break;
                case Constant.INFOR_VIDEO:
                    VideoBo videoBo = inforService.findVideoById(inforid);
                    if (videoBo != null) {
                        BeanUtils.copyProperties(videoBo, inforVo);
                        thumbsupBo = thumbsupService.getByVidAndVisitorid(inforid, userid);
                        inforVo.setSelfSub(thumbsupBo != null);
                        asyncController.updateUserReadHis(userid,videoBo.getModule(), videoBo.getClassName(), Constant.INFOR_VIDEO);
                        inforVo.setInforid(hisBo.getInforid());
                        inforVo.setThumpsubNum(videoBo.getThumpsubNum());
                        inforVo.setCommentNum(videoBo.getCommnetNum());
                        inforVo.setReadNum(videoBo.getVisitNum());
                        inforVo.setInforType(type);
                        inforVos.add(inforVo);
                    }
                    break;
                case Constant.INFOR_DAILY:
                    DailynewsBo dailynewsBo = inforService.findByDailynewsId(inforid);
                    if (dailynewsBo == null) {
                        continue;
                    }
                    BeanUtils.copyProperties(dailynewsBo, inforVo);
                    thumbsupBo = thumbsupService.getByVidAndVisitorid(inforid, userid);
                    inforVo.setSelfSub(thumbsupBo != null);
                    inforVo.setInforid(inforid);
                    inforVo.setThumpsubNum(dailynewsBo.getThumpsubNum());
                    inforVo.setCommentNum(dailynewsBo.getCommnetNum());
                    inforVo.setReadNum(dailynewsBo.getVisitNum());
                    inforVo.setText("");
                    inforVo.setInforType(type);
                    inforVos.add(inforVo);
                    break;
                case Constant.INFOR_YANGLAO:
                    YanglaoBo yanglaoBo = inforService.findByYanglaoId(inforid);
                    if (yanglaoBo == null) {
                        continue;
                    }
                    BeanUtils.copyProperties(yanglaoBo, inforVo);
                    thumbsupBo = thumbsupService.getByVidAndVisitorid(inforid, userid);
                    inforVo.setSelfSub(thumbsupBo != null);
                    inforVo.setInforid(inforid);
                    inforVo.setThumpsubNum(yanglaoBo.getThumpsubNum());
                    inforVo.setCommentNum(yanglaoBo.getCommnetNum());
                    inforVo.setReadNum(yanglaoBo.getVisitNum());
                    inforVo.setText("");
                    inforVo.setInforType(type);
                    inforVos.add(inforVo);
                    break;
                default:
                    break;
            }
            map.put("inforVos", inforVos);
        }
        return JSONObject.fromObject(map).toString();
    }

    @ApiOperation("根据标题分类搜索接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "关键字",  paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "inforType", value = "1健康，2 安防，3广播，4视频，5时政，6养老",  paramType = "query", dataType =
                    "int"),
            @ApiImplicitParam(name = "page", value = "分页页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "分页条数", paramType = "query", dataType = "int")})
    @RequestMapping(value = "/search", method = {RequestMethod.GET, RequestMethod.POST})
    public String search(String keyword,  int inforType, int page, int limit, HttpServletRequest request,
                         HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);
        String userid = userBo == null ? "" : userBo.getId();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ret", 0);
        map.put("inforType", inforType);
        asyncController.saveKeyword(keyword, inforType);
        switch (inforType){
            case Constant.INFOR_HEALTH:
                List<InforBo> inforBos = inforService.findInforByTitleRegex(keyword, page, limit);
                List<InforVo> inforVos = new ArrayList<>();
                for (InforBo inforBo : inforBos) {
                    InforVo inforVo = new InforVo();
                    BeanUtils.copyProperties(inforBo, inforVo);
                    if (!"".equals(userid)) {
                        ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(inforBo.getId(), userid);
                        inforVo.setSelfSub(thumbsupBo != null);
                    }
                    inforVo.setInforid(inforBo.getId());
                    inforVo.setText("");
                    inforVo.setThumpsubNum(inforBo.getThumpsubNum());
                    inforVo.setCommentNum(inforBo.getCommnetNum());
                    inforVo.setReadNum(inforBo.getVisitNum());
                    inforVos.add(inforVo);
                }
                map.put("inforVos", inforVos);
                break;
            case Constant.INFOR_SECRITY:
                List<SecurityBo> securityBos = inforService.findSecurByTitleRegex(keyword, page, limit);
                LinkedList<SecurityVo> vos = new LinkedList<>();
                for (SecurityBo securityBo : securityBos) {
                    SecurityVo securityVo = new SecurityVo();
                    BeanUtils.copyProperties(securityBo, securityVo);
                    if (!"".equals(userid)) {
                        ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(securityBo.getId(), userid);
                        securityVo.setSelfSub(thumbsupBo != null);
                    }
                    securityVo.setInforid(securityBo.getId());
                    securityVo.setThumpsubNum(securityBo.getThumpsubNum());
                    securityVo.setCommentNum(securityBo.getCommnetNum());
                    securityVo.setReadNum(securityBo.getVisitNum());
                    securityVo.setText("");
                    vos.add(securityVo);
                }
                map.put("securityVos", vos);
                break;
            case Constant.INFOR_RADIO:
                List<BroadcastBo> broadcastBos = inforService.findRadioByTitleRegex(keyword, page, limit);
                List<BroadcastVo> radioVos = new ArrayList<>();
                for (BroadcastBo bo : broadcastBos) {
                    BroadcastVo broadcastVo = new BroadcastVo();
                    BeanUtils.copyProperties(bo, broadcastVo);
                    if (!"".equals(userid)) {
                        ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(bo.getId(), userid);
                        broadcastVo.setSelfSub(thumbsupBo != null);
                    }
                    broadcastVo.setInforid(bo.getId());
                    broadcastVo.setReadNum(bo.getVisitNum());
                    broadcastVo.setCommentNum(bo.getCommnetNum());
                    broadcastVo.setThumpsubNum(bo.getThumpsubNum());
                    radioVos.add(broadcastVo);
                }
                map.put("radioVos", radioVos);
                break;
            case Constant.INFOR_VIDEO:
                List<VideoBo> videoBos = inforService.findVideoByTitleRegex(keyword, page, limit);
                List<VideoVo> videoVos = new ArrayList<>();
                for (VideoBo bo : videoBos) {
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(bo, videoVo);
                    videoVo.setReadNum(bo.getVisitNum());
                    if (!"".equals(userid)) {
                        ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(bo.getId(), userid);
                        videoVo.setSelfSub(thumbsupBo != null);
                    }
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
                        } else {
                            inforService.updateVideoPicById(bo.getId(), "noPic");
                        }
                    } else if (bo.getPoster().equals("noPic")) {
                        videoVo.setPicture("");
                    } else {
                        videoVo.setPicture(bo.getPoster());
                    }
                    videoVo.setInforid(bo.getId());
                    videoVo.setThumpsubNum(bo.getThumpsubNum());
                    videoVo.setCommentNum(bo.getCommnetNum());
                    videoVo.setReadNum(bo.getVisitNum());
                    videoVos.add(videoVo);
                }
                map.put("videoVos", videoVos);
                break;
            case Constant.INFOR_DAILY:
                List<DailynewsBo> dailynewsBos = inforService.findDailynewsKeyword(keyword, page, limit);
                inforVos = new ArrayList<>();
                for (DailynewsBo inforBo : dailynewsBos) {
                    InforVo inforVo = new InforVo();
                    BeanUtils.copyProperties(inforBo, inforVo);
                    if (!"".equals(userid)) {
                        ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(inforBo.getId(), userid);
                        inforVo.setSelfSub(thumbsupBo != null);
                    }
                    inforVo.setInforid(inforBo.getId());
                    inforVo.setText("");
                    inforVo.setThumpsubNum(inforBo.getThumpsubNum());
                    inforVo.setCommentNum(inforBo.getCommnetNum());
                    inforVo.setReadNum(inforBo.getVisitNum());
                    inforVos.add(inforVo);
                }
                map.put("inforVos", inforVos);
                break;
            case Constant.INFOR_YANGLAO:
                List<YanglaoBo> yanglaoBos = inforService.findYanglaosKeyword(keyword, page, limit);
                inforVos = new ArrayList<>();
                for (YanglaoBo inforBo : yanglaoBos) {
                    InforVo inforVo = new InforVo();
                    BeanUtils.copyProperties(inforBo, inforVo);
                    if (!"".equals(userid)) {
                        ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(inforBo.getId(), userid);
                        inforVo.setSelfSub(thumbsupBo != null);
                    }
                    inforVo.setInforid(inforBo.getId());
                    inforVo.setText("");
                    inforVo.setThumpsubNum(inforBo.getThumpsubNum());
                    inforVo.setCommentNum(inforBo.getCommnetNum());
                    inforVo.setReadNum(inforBo.getVisitNum());
                    inforVos.add(inforVo);
                }
                map.put("inforVos", inforVos);
                break;
            default:
                break;
        }
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("根据具体的广播或视频信息跳转到列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inforid", value = "资讯id",  paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "inforType", value = "3广播，4视频",  paramType = "query", dataType =
                    "int")})
    @RequestMapping(value = "/get-list", method = {RequestMethod.GET, RequestMethod.POST})
    public String search(String inforid,  int inforType, HttpServletRequest request,
                         HttpServletResponse response) {

        UserBo userBo = getUserLogin(request);
        String userid = userBo == null ? "" : userBo.getId();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ret", 0);
        if (inforType == Constant.INFOR_RADIO) {
            BroadcastBo broadcastBo = inforService.findBroadById(inforid);
            List<BroadcastVo> radioVos = new LinkedList<>();
            List<BroadcastBo> broadcastBos = inforService.findBroadByClassName(broadcastBo.getModule(), broadcastBo
                    .getClassName());
            boolean isGet = false;
            int i = 0;
            for (BroadcastBo bo : broadcastBos) {
                if (bo.getId().equals(inforid)) {
                    isGet = true;
                }
                i++;
                BroadcastVo broadcastVo = new BroadcastVo();
                BeanUtils.copyProperties(bo, broadcastVo);
                if (!"".equals(userid)) {
                    ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(bo.getId(), userid);
                    broadcastVo.setSelfSub(thumbsupBo != null);
                }
                broadcastVo.setInforid(bo.getId());
                broadcastVo.setReadNum(bo.getVisitNum());
                broadcastVo.setCommentNum(bo.getCommnetNum());
                broadcastVo.setThumpsubNum(bo.getThumpsubNum());
                radioVos.add(broadcastVo);
                if (i == Constant.PAGE_LIMIT) {
                    if (isGet) {
                        break;
                    }
                    i = 0;
                    radioVos.clear();
                }
            }
            map.put("radioVos", radioVos);
        } else if (inforType == Constant.INFOR_VIDEO) {
            VideoBo videoBo = inforService.findVideoById(inforid);
            List<VideoBo> videoBos = inforService.selectClassNamePage(videoBo.getModule(), videoBo.getClassName());
            List<VideoVo> videoVos = new LinkedList<>();
            boolean isGet = false;
            int i = 0;
            for (VideoBo bo : videoBos ) {
                VideoVo videoVo = new VideoVo();
                i++;
                if (bo.getId().equals(inforid)) {
                   isGet = true;
                }
                BeanUtils.copyProperties(bo, videoVo);
                videoVo.setReadNum(bo.getVisitNum());
                if (!"".equals(userid)) {
                    ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(bo.getId(), userid);
                    videoVo.setSelfSub(thumbsupBo != null);
                }
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
                    } else {
                        inforService.updateVideoPicById(bo.getId(), "noPic");
                    }
                } else if (bo.getPoster().equals("noPic")) {
                    videoVo.setPicture("");
                } else {
                    videoVo.setPicture(bo.getPoster());
                }
                videoVo.setInforid(bo.getId());
                videoVo.setThumpsubNum(bo.getThumpsubNum());
                videoVo.setCommentNum(bo.getCommnetNum());
                videoVo.setReadNum(bo.getVisitNum());
                videoVos.add(videoVo);
                if (i == Constant.PAGE_LIMIT) {
                    if (isGet) {
                        break;
                    }
                    i = 0;
                    videoVos.clear();
                }
            }
            map.put("videoVos", videoVos);
        }
        return JSONObject.fromObject(map).toString();
    }

    @ApiOperation("资讯热门搜索关键词")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inforType", value = "1健康，2 安防，3广播，4视频, 5时政，6养老",  paramType = "query",
                    dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "显示关键词数量", paramType = "query", dataType = "int")})
    @RequestMapping(value = "/hot-search-words", method = {RequestMethod.GET, RequestMethod.POST})
    public String searchHotHis(int inforType, int limit, HttpServletRequest request,
                         HttpServletResponse response) {

        List<SearchBo> searchBos = searchService.findInforByTimes(2, inforType, limit);
        List<String> words = new ArrayList<>();
        searchBos.forEach(searchBo -> words.add(searchBo.getKeyword()));
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("hotWords", words);
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("时政资讯信息详情")
    @ApiImplicitParam(name = "inforid", value = "资讯id", required = true, paramType = "query",dataType = "string")
    @RequestMapping(value = "/daily-infor", method = {RequestMethod.GET, RequestMethod.POST})
    public String dailyNews(String inforid, HttpServletRequest request, HttpServletResponse response){

        DailynewsBo inforBo = inforService.findByDailynewsId(inforid);
        if (inforBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.INFOR_IS_NULL.getIndex(),
                    ERRORCODE.INFOR_IS_NULL.getReason());
        }
        InforVo inforVo = new InforVo();
        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(inforBo.getId(), userBo.getId());
            inforVo.setSelfSub(thumbsupBo != null);
            asyncController.updateUserReadHis(userBo.getId(),inforBo.getClassName(),"", Constant.INFOR_DAILY);
            asyncController.addUserReadhis(userBo.getId(), inforid, Constant.INFOR_DAILY, inforBo.getModule(),
                    inforBo.getClassName());
        }
        updateInforNum(inforid, Constant.INFOR_DAILY, 1, Constant.VISIT_NUM);
        asyncController.updateInforHistroy(inforid, inforBo.getClassName(), Constant.INFOR_DAILY);
        BeanUtils.copyProperties(inforBo, inforVo);
        inforVo.setInforid(inforBo.getId());
        inforVo.setThumpsubNum(inforBo.getThumpsubNum());
        inforVo.setCommentNum(inforBo.getCommnetNum());
        inforVo.setReadNum(inforBo.getVisitNum());
        inforVo.setImageUrls(inforBo.getImageUrls());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVo", inforVo);
        return JSONObject.fromObject(map).toString();
    }

    @ApiOperation("获取时政指定分类下列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "className", value = "时政资讯分类", required = true, dataType =
                    "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "分页页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "分页条数", required = true, dataType = "int", paramType = "query")})
    @RequestMapping(value = "/daily-list", method = {RequestMethod.GET, RequestMethod.POST})
    public String dailyList(String className, int page,  int limit,
                            HttpServletRequest request, HttpServletResponse response){
        List<DailynewsBo> dailynewsBos = inforService.findDailynewsByClassName(className, page, limit);
        List<InforVo> vos = new ArrayList<>();
        for (DailynewsBo bo : dailynewsBos) {
            InforVo inforVo = new InforVo();
            BeanUtils.copyProperties(bo, inforVo);
            inforVo.setInforid(bo.getId());
            inforVo.setReadNum(bo.getVisitNum());
            inforVo.setCommentNum(bo.getCommnetNum());
            inforVo.setThumpsubNum(bo.getThumpsubNum());
            inforVo.setText("");
            vos.add(inforVo);
        }
        UserBo userBo =  getUserLogin(request);
        if (userBo != null) {
            asyncController.updateUserReadHis(userBo.getId(), className, "", Constant.INFOR_DAILY);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVoList", vos);
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("养老资讯信息详情")
    @ApiImplicitParam(name = "inforid", value = "资讯id", required = true, paramType = "query",dataType = "string")
    @RequestMapping(value = "/yanglao-infor", method = {RequestMethod.GET, RequestMethod.POST})
    public String dayanglaoNews(String inforid, HttpServletRequest request, HttpServletResponse response){

        YanglaoBo inforBo = inforService.findByYanglaoId(inforid);
        if (inforBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.INFOR_IS_NULL.getIndex(),
                    ERRORCODE.INFOR_IS_NULL.getReason());
        }

        InforVo inforVo = new InforVo();
        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(inforBo.getId(), userBo.getId());
            inforVo.setSelfSub(thumbsupBo != null);
            asyncController.updateUserReadHis(userBo.getId(),inforBo.getClassName(),"", Constant.INFOR_DAILY);
            asyncController.addUserReadhis(userBo.getId(), inforid, Constant.INFOR_DAILY, inforBo.getModule(),
                    inforBo.getClassName());
        }
        updateInforNum(inforid, Constant.INFOR_DAILY, 1, Constant.VISIT_NUM);
        asyncController.updateInforHistroy(inforid, inforBo.getClassName(), Constant.INFOR_DAILY);
        BeanUtils.copyProperties(inforBo, inforVo);
        inforVo.setInforid(inforBo.getId());
        inforVo.setThumpsubNum(inforBo.getThumpsubNum());
        inforVo.setCommentNum(inforBo.getCommnetNum());
        inforVo.setReadNum(inforBo.getVisitNum());
        inforVo.setImageUrls(inforBo.getImageUrls());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVo", inforVo);
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("获取养老指定分类下列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "className", value = "养老资讯分类", required = true, dataType =
                    "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "分页页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "分页条数", required = true, dataType = "int", paramType = "query")})
    @RequestMapping(value = "/yanglao-list", method = {RequestMethod.GET, RequestMethod.POST})
    public String yanglaoList(String className, int page,  int limit,
                            HttpServletRequest request, HttpServletResponse response){
        List<YanglaoBo> dailynewsBos = inforService.findYanglaoByClassName(className, page, limit);
        List<InforVo> vos = new ArrayList<>();
        for (YanglaoBo bo : dailynewsBos) {
            InforVo inforVo = new InforVo();
            BeanUtils.copyProperties(bo, inforVo);
            inforVo.setInforid(bo.getId());
            inforVo.setReadNum(bo.getVisitNum());
            inforVo.setCommentNum(bo.getCommnetNum());
            inforVo.setThumpsubNum(bo.getThumpsubNum());
            inforVo.setText("");
            vos.add(inforVo);
        }
        UserBo userBo =  getUserLogin(request);
        if (userBo != null) {
            asyncController.updateUserReadHis(userBo.getId(), className, "", Constant.INFOR_DAILY);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVoList", vos);
        return JSONObject.fromObject(map).toString();
    }


}
