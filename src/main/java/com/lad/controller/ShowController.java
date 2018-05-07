package com.lad.controller;

import com.lad.bo.CircleTypeBo;
import com.lad.bo.FriendsBo;
import com.lad.bo.ShowBo;
import com.lad.bo.UserBo;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.ShowVo;
import com.lad.vo.UserBaseVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/28
 */
@Slf4j
@Api(value = "ShowController", description = "招接演出接口")
@RestController
@RequestMapping("show")
public class ShowController extends BaseContorller {


    @Autowired
    private IUserService userService;

    @Autowired
    private IFriendsService friendsService;

    @Autowired
    private IShowService showService;

    @Autowired
    private AsyncController asyncController;

    @Autowired
    private ICircleService circleService;


    @ApiOperation("发表招接演出信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "images", value = "图片信息,如果是招演类型，则为营业执照图片", paramType = "query", dataType =
                    "file"),
            @ApiImplicitParam(name = "video", value = "视频信息，与图片二选一", paramType = "query", dataType = "file")})
    @PostMapping("/insert")
    public String insert(@RequestBody @ApiParam(name = "showVo", value = "演出信息实体类", required = true)ShowVo showVo,
                         MultipartFile[] images, MultipartFile video, HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        String userid = userBo.getId();
        ShowBo showBo = new ShowBo();
        BeanUtils.copyProperties(showVo, showBo);
        showBo.setCreateuid(userid);
        if (images != null && images.length > 0) {
            if (showBo.getType() == ShowBo.NEED) {
                MultipartFile file = images[0];
                Long time = Calendar.getInstance().getTimeInMillis();
                String fileName = String.format("%s-%d-%s", userid, time, file.getOriginalFilename());
                String path = CommonUtil.upload(file, Constant.RELEASE_PICTURE_PATH, fileName, 0);
                showBo.setComPic(path);
            } else if (showVo.getType() == ShowBo.PROVIDE) {
                LinkedHashSet<String> photos = new LinkedHashSet<>();
                for (MultipartFile file : images) {
                    Long time = Calendar.getInstance().getTimeInMillis();
                    String fileName = String.format("%s-%d-%s", userid, time, file.getOriginalFilename());
                    String path = CommonUtil.upload(file, Constant.RELEASE_PICTURE_PATH, fileName, 0);
                    photos.add(path);
                }
                showBo.setPicType("pic");
                showBo.setImages(photos);
            }
            log.info("shows {} add pic   size: {} ",userid, images.length);
        }
        if (video != null) {
            Long time = Calendar.getInstance().getTimeInMillis();
            String fileName = String.format("%s-%d-%s", userid, time, video.getOriginalFilename());
            String[] paths = CommonUtil.uploadVedio(video, Constant.RELEASE_PICTURE_PATH, fileName, 0);
            showBo.setVideo(paths[0]);
            showBo.setVideoPic(paths[1]);
            showBo.setPicType("video");
            log.info("user {} shows add video path: {},  videoPic: {} ", userid, paths[0], paths[1]);
        }
        showService.insert(showBo);
        asyncController.addShowTypes(showService, showBo.getShowType(), userid);
        if (showBo.getType() == ShowBo.NEED) {
            asyncController.pushShowToCreate(showService, showBo);
        } else {
            asyncController.pushShowToCompany(showService, showBo.getShowType(), showBo.getId(),
                    userBo.getUserName(), userid);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("showid", showBo.getId());
        return JSONObject.fromObject(map).toString();
    }



    @ApiOperation("获取演出详情")
    @ApiImplicitParam(name = "showid", value = "演出id", required = true, paramType = "query",
            dataType = "string")
    @GetMapping("/show-info")
    public String detail(String showid,HttpServletRequest request, HttpServletResponse response) {
        ShowBo showBo = showService.findById(showid);
        if (showBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.SHOW_NULL.getIndex(),
                    ERRORCODE.SHOW_NULL.getReason());
        }
        ShowVo vo = new ShowVo();
        BeanUtils.copyProperties(showBo, vo);
        vo.setShowid(showBo.getId());
        UserBaseVo baseVo = new UserBaseVo();
        UserBo userBo = getUserLogin(request);
        UserBo createUser = null;
        String friendName = "";
        String userid = "";
        if (userBo != null) {
            userid = userBo.getId();
            if (userid.equals(showBo.getCreateuid())) {
                vo.setCreate(true);
                BeanUtils.copyProperties(userBo,baseVo);
            } else {
                createUser = userService.getUser(showBo.getCreateuid());
                FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userid, showBo.getCreateuid());
                if (friendsBo != null && !StringUtils.isEmpty(friendsBo.getBackname())) {
                    friendName = friendsBo.getBackname();
                }
            }
        } else {
            createUser = userService.getUser(showBo.getCreateuid());
        }
        if (createUser != null) {
            BeanUtils.copyProperties(createUser, baseVo);
            baseVo.setUserName(!"".equals(friendName) ? friendName : createUser.getUserName());
        }
        vo.setCreatUser(baseVo);
        //推荐信息获取
        List<ShowVo> showVos = new LinkedList<>();
        List<ShowBo> showBos;
        //招演商家推荐接演团队信息
        if (showBo.getType() == ShowBo.NEED) {
            showBos = showService.findByKeyword(showBo.getShowType(), userid, ShowBo.PROVIDE, 1, 3);
        } else {
            // 接演团队推荐商家信息
            showBos = showService.findByKeyword(showBo.getShowType(), userid,  ShowBo.NEED, 1, 3);
        }
        bo2vos(showBos, showVos, userBo);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("showVo", vo);
        map.put("recomShowVos", showVos);
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("修改招接演出信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "showid", value = "演出信息id", required = true, paramType = "query", dataType =
                    "string"),
            @ApiImplicitParam(name = "showVoJson",
                    value = "修改的参数和信息，json字符串，字段根据showVo来定义，不需要修改内容则不传入，可为空，文件url不需要传入",
                    paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "delImages", value = "删除的图片信息，多个以逗号给开，不删除则为空",
                    paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "images", value = "新增图片信息, 如果是招演类型，则默认为营业执照图片",
                    paramType = "query", dataType = "file"),
            @ApiImplicitParam(name = "video", value = "需要修改视频信息，与图片二选一", paramType = "query", dataType = "file")})
    @PostMapping("/update")
    public String insert(String showid, String showVoJson,String delImages, MultipartFile[] images, MultipartFile
            video, HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        String userid = userBo.getId();
        ShowBo showBo = showService.findById(showid);
        if (showBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.SHOW_NULL.getIndex(),
                    ERRORCODE.SHOW_NULL.getReason());
        }
        //创建者或者管理员才能修改
        if (!userBo.getId().equals(showBo.getCreateuid()) && !CommonUtil.getAdminUserids().contains(userBo.getId())) {
            return CommonUtil.toErrorResult(ERRORCODE.NOTE_NOT_MASTER.getIndex(),
                    ERRORCODE.NOTE_NOT_MASTER.getReason());
        }
        Map<String, Object> params = new LinkedHashMap<>();
        if (!StringUtils.isEmpty(showVoJson)) {
            JSONObject jsonObject = JSONObject.fromObject(showVoJson);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()){
                String key = (String)iterator.next();
                params.put(key, jsonObject.get(key));
            }
        }
        showBo.setCreateuid(userid);
        LinkedHashSet<String> photos = showBo.getImages();
         //需要删除的图片
        if (!StringUtils.isEmpty(delImages)) {
            String[] delArray = delImages.trim().split(",");
            for (String url : delArray) {
                photos.remove(url);
            }
        }
        //新增的图片
        if (images != null && images.length > 0) {
            if (showBo.getType() == ShowBo.NEED) {
                MultipartFile file = images[0];
                Long time = Calendar.getInstance().getTimeInMillis();
                String fileName = String.format("%s-%d-%s", userid, time, file.getOriginalFilename());
                String path = CommonUtil.upload(file, Constant.RELEASE_PICTURE_PATH, fileName, 0);
                params.put("comPic", path);
            } else if (showBo.getType() == ShowBo.PROVIDE) {
                for (MultipartFile file : images) {
                    Long time = Calendar.getInstance().getTimeInMillis();
                    String fileName = String.format("%s-%d-%s", userid, time, file.getOriginalFilename());
                    String path = CommonUtil.upload(file, Constant.RELEASE_PICTURE_PATH, fileName, 0);
                    photos.add(path);
                }
                params.put("picType", "pic");
                params.put("images", photos);
            }
        }
        if (video != null) {
            Long time = Calendar.getInstance().getTimeInMillis();
            String fileName = String.format("%s-%d-%s", userid, time, video.getOriginalFilename());
            String[] paths = CommonUtil.uploadVedio(video, Constant.RELEASE_PICTURE_PATH, fileName, 0);
            showBo.setVideo(paths[0]);
            showBo.setVideoPic(paths[1]);
            params.put("video", paths[0]);
            params.put("videoPic", paths[1]);
            params.put("picType", "video");
        }
        if (!params.isEmpty()) {
            showService.update(showid, params);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("showid", showBo.getId());
        return JSONObject.fromObject(map).toString();
    }



    @ApiOperation("获取演出列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "1招演出，2接演出", required = true, paramType = "query",
                    dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码",paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "条数",paramType = "query",dataType = "int")})
    @GetMapping("/show-list")
    public String showList(int type, int page, int limit, HttpServletRequest request, HttpServletResponse response) {

        UserBo userBo = getUserLogin(request);
        List<ShowBo> showBos = showService.findByShowType(type, page, limit);
        List<ShowVo> showVos = new LinkedList<>();
        bo2vos(showBos, showVos, userBo);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("showVos", showVos);
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("我的招接演出列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码",paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "条数",paramType = "query",dataType = "int")})
    @GetMapping("/my-shows")
    public String myShows(int page, int limit, HttpServletRequest request, HttpServletResponse
            response) {
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        List<ShowBo> showBos = showService.findByCreateuid(userBo.getId(), -1, page, limit);
        List<ShowVo> showVos = new LinkedList<>();
        List<String> showids = new LinkedList<>();
        UserBaseVo baseVo = new UserBaseVo();
        BeanUtils.copyProperties(userBo, baseVo);
        for (ShowBo showBo : showBos) {
            ShowVo showVo = new ShowVo();
            BeanUtils.copyProperties(showBo, showVo);
            //在当前失效的招演信息
            if (showBo.getType() == ShowBo.NEED && showBo.getStatus() == 0 && isTimeout(showBo.getShowTime())) {
                showVo.setStatus(1);
                showids.add(showBo.getId());
            }
            showVo.setShowid(showBo.getId());
            showVo.setCreate(true);
            showVo.setCreatUser(baseVo);
            showVos.add(showVo);
        }
        //过期招演信息更新
        if (!showids.isEmpty()) {
            showService.updateShowStatus(showids, 1);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("showVos", showVos);
        return JSONObject.fromObject(map).toString();
    }



    @ApiOperation("推荐演出列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "showid", value = "演出详情的id信息", required = true, paramType = "query",
                    dataType = "string"),
            @ApiImplicitParam(name = "page", value = "页码",paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "条数",paramType = "query",dataType = "int")})
    @GetMapping("/recom-shows")
    public String detail(String showid, int page, int limit, HttpServletRequest request, HttpServletResponse response) {

        UserBo userBo = getUserLogin(request);
        ShowBo showBo = showService.findById(showid);
        if (showBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.SHOW_NULL.getIndex(),
                    ERRORCODE.SHOW_NULL.getReason());
        }
        String userid = userBo == null ? "" : userBo.getId();
        List<ShowVo> showVos = new LinkedList<>();
        List<ShowBo> showBos = showService.findByKeyword(showBo.getShowType(), userid, -1, page, limit);
        bo2vos(showBos, showVos, userBo);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("recomShowVos", showVos);
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("删除演出")
    @ApiImplicitParam(name = "showid", value = "演出id", required = true, paramType = "query",
            dataType = "string")
    @GetMapping("/delete")
    public String delete(String showid,HttpServletRequest request, HttpServletResponse response) {
        ShowBo showBo = showService.findById(showid);
        if (showBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.SHOW_NULL.getIndex(),
                    ERRORCODE.SHOW_NULL.getReason());
        }
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        //创建者或者管理员才能删除
        if (userBo.getId().equals(showBo.getCreateuid()) || CommonUtil.getAdminUserids().contains(userBo.getId())) {
            showService.delete(showid);
            return Constant.COM_RESP;
        } else {
            return CommonUtil.toErrorResult(ERRORCODE.NOTE_NOT_MASTER.getIndex(),
                    ERRORCODE.NOTE_NOT_MASTER.getReason());
        }
    }


    @ApiOperation("查找所有演出类型")
    @GetMapping("/show-types")
    public String showTypes() {
        List<CircleTypeBo> circleTypeBos = circleService.selectByLevel(1, CircleTypeBo.SHOW_TYPE);
        LinkedHashSet<String> showTypes = new LinkedHashSet<>();
        if (circleTypeBos != null){
            circleTypeBos.forEach( circleTypeBo -> showTypes.add(circleTypeBo.getCategory()));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("showTypes", showTypes);
        return JSONObject.fromObject(map).toString();
    }


    /**
     *
     * @param showBos
     * @param showVos
     * @param loginUser
     */
    private void bo2vos(List<ShowBo> showBos, List<ShowVo> showVos, UserBo loginUser){
        String userid = loginUser != null ? loginUser.getId() : "";
        //过期的商家发布信息
        List<String> showids = new LinkedList<>();
        for (ShowBo showBo : showBos) {
            //已过期的不再添加
            if (showBo.getType() == ShowBo.NEED && isTimeout(showBo.getShowTime())) {
                showids.add(showBo.getId());
                continue;
            }
            ShowVo showVo = new ShowVo();
            BeanUtils.copyProperties(showBo, showVo);
            showVo.setShowid(showBo.getId());
            UserBaseVo baseVo = new UserBaseVo();
            UserBo createUser = null;
            String friendName = "";
            if (loginUser == null) {
                createUser = userService.getUser(showBo.getCreateuid());
            } else {
                if (userid.equals(showBo.getCreateuid())) {
                    BeanUtils.copyProperties(loginUser, baseVo);
                    showVo.setCreate(true);
                } else {
                    createUser = userService.getUser(showBo.getCreateuid());
                    //查询是否是好友关系
                    FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userid, showBo.getCreateuid());
                    if (friendsBo != null && !StringUtils.isEmpty(friendsBo.getBackname())) {
                        friendName = friendsBo.getBackname();
                    }
                }
            }
            if (createUser != null) {
                BeanUtils.copyProperties(createUser, baseVo);
                baseVo.setUserName(!"".equals(friendName) ? friendName : createUser.getUserName());
            }
            showVo.setCreatUser(baseVo);
            showVos.add(showVo);
        }
        //过期招演信息更新
        if (!showids.isEmpty()) {
            showService.updateShowStatus(showids, 1);
        }
    }


    /**
     * 判断是否超时
     * @param timeStr
     * @return
     */
    private boolean isTimeout(String timeStr){
        //已超时的不在推送
        Date time = CommonUtil.getDate(timeStr,"yyyy-MM-dd HH:mm:ss");
        if (time != null) {
            return System.currentTimeMillis() > time.getTime();
        }
        return false;
    }

}
