package com.lad.controller;

import com.lad.bo.*;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.CommentVo;
import com.lad.vo.ExposeVo;
import com.lad.vo.UserBaseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 * Time:2018/4/22
 */
@Api("曝光台相关接口")
@RestController
@RequestMapping("expose")
public class ExposeController extends BaseContorller {

    private final Logger logger = LogManager.getLogger(ExposeController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IFriendsService friendsService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IThumbsupService thumbsupService;

    @Autowired
    private IExposeService exposeService;

    @Autowired
    private AsyncController asyncController;


    @ApiOperation("发表曝光信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "曝光标题", required = true, paramType = "query",
                    dataType = "string"),
            @ApiImplicitParam(name = "exposeType", value = "曝光类型，多个以逗号隔开", paramType = "query",
                    dataType = "string"),
            @ApiImplicitParam(name = "content", value = "内容", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "images", value = "图片信息", paramType = "query", dataType = "file"),
            @ApiImplicitParam(name = "video", value = "视频信息，与图片二选一", paramType = "query", dataType = "file")})
    @PostMapping("/insert")
    public String insert(String title, String exposeType, String content, MultipartFile[] images, MultipartFile
            video, HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        String userid = userBo.getId();
        ExposeBo expose = new ExposeBo();
        expose.setTitle(title);
        expose.setExposeType(exposeType);
        expose.setContent(content);
        expose.setCreateuid(userid);
        if (images != null) {
            LinkedHashSet<String> photos = new LinkedHashSet<>();
            for (MultipartFile file : images) {
                Long time = Calendar.getInstance().getTimeInMillis();
                String fileName = String.format("%s-%d-%s", userid, time, file.getOriginalFilename());
                String path = CommonUtil.upload(file, Constant.EXPOSE_PICTURE_PATH, fileName, 0);
                logger.info("expose {} add pic path: {},  size: {} ",userid,  path, file.getSize());
                photos.add(path);
            }
            expose.setPicType("pic");
            expose.setImages(photos);
        }
        if (video != null) {
            Long time = Calendar.getInstance().getTimeInMillis();
            String fileName = String.format("%s-%d-%s", userid, time, video.getOriginalFilename());
            String[] paths = CommonUtil.uploadVedio(video, Constant.EXPOSE_PICTURE_PATH, fileName, 0);
            expose.setVideo(paths[0]);
            expose.setVideoPic(paths[1]);
            expose.setPicType("video");
            logger.info("user {} expose add video path: {},  videoPic: {} ", userid, paths[0], paths[1]);
        }
        exposeService.insert(expose);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("exposeid", expose.getId());
        return JSONObject.fromObject(map).toString();
    }



    @ApiOperation("修改曝光信息，如不修改，参数输入为空")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exposeid", value = "修改曝光内容的id", required = true, paramType = "query",
                    dataType = "string"),
            @ApiImplicitParam(name = "title", value = "曝光标题",  paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "exposeType", value = "曝光类型，多个以逗号隔开", paramType = "query",
                    dataType = "string"),
            @ApiImplicitParam(name = "content", value = "内容", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "delImages", value = "删除的图片信息，多个以逗号隔开", paramType = "query", dataType = "file"),
            @ApiImplicitParam(name = "images", value = "新增的图片信息", paramType = "query", dataType = "file"),
            @ApiImplicitParam(name = "video", value = "视频信息，若存在，则覆盖原视频", paramType = "query", dataType = "file")})
    @PostMapping("/update")
    public String update(String exposeid,String title, String exposeType, String content,String delImages,
                         MultipartFile[] images, MultipartFile video, HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        String userid = userBo.getId();
        ExposeBo expose = exposeService.findById(exposeid);
        if (expose == null) {
            return CommonUtil.toErrorResult(ERRORCODE.EXPOSE_MSG_NULL.getIndex(),
                    ERRORCODE.EXPOSE_MSG_NULL.getReason());
        }
        if (!(expose.getCreateuid().equals(userid) || CommonUtil.getAdminUserids().contains(userid))) {
            return CommonUtil.toErrorResult(ERRORCODE.NOTE_NOT_MASTER.getIndex(), ERRORCODE.NOTE_NOT_MASTER.getReason());
        }
        Map<String, Object> params = new HashMap<>();
        if (!StringUtils.isEmpty(title)) {
            params.put("title", title);
        }
        if (!StringUtils.isEmpty(exposeType)) {
            params.put("exposeType", exposeType);
        }
        if (!StringUtils.isEmpty(content)) {
            params.put("content", content);
        }
        LinkedHashSet<String> photos = expose.getImages() == null ? new LinkedHashSet<>() :  expose.getImages() ;
        if (!StringUtils.isEmpty(delImages)) {
            String[] dels = delImages.split(",");
            for (String url : dels) {
                photos.remove(url);
            }
        }
        if (images != null) {
            for (MultipartFile file : images) {
                Long time = Calendar.getInstance().getTimeInMillis();
                String fileName = String.format("%s-%d-%s", userid, time, file.getOriginalFilename());
                String path = CommonUtil.upload(file, Constant.EXPOSE_PICTURE_PATH, fileName, 0);
                photos.add(path);
            }
            params.put("picType", "pic");
        }
        params.put("images", photos);
        if (video != null) {
            Long time = Calendar.getInstance().getTimeInMillis();
            String fileName = String.format("%s-%d-%s", userid, time, video.getOriginalFilename());
            String[] paths = CommonUtil.uploadVedio(video, Constant.EXPOSE_PICTURE_PATH, fileName, 0);
            params.put("video", paths[0]);
            params.put("videoPic", paths[1]);
            params.put("picType", "video");
            logger.info("user {} expose add video path: {},  videoPic: {} ", userid, paths[0], paths[1]);
        }
        exposeService.updateExpose(exposeid, params);

        expose = exposeService.findById(exposeid);
        ExposeVo vo = new ExposeVo();
        BeanUtils.copyProperties(expose, vo);
        vo.setExposeid(exposeid);
        //目前是只有自己才能修改
        UserBaseVo baseVo = new UserBaseVo();
        BeanUtils.copyProperties(userBo,baseVo);
        vo.setCreateUserVo(baseVo);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("exposeVo", vo);
        return JSONObject.fromObject(map).toString();
    }


    @ApiOperation("获取曝光信息")
    @ApiImplicitParam(name = "exposeid", value = "曝光信息id", required = true, paramType = "query",
                    dataType = "string")
    @GetMapping("/expose-info")
    public String detail(String exposeid,HttpServletRequest request, HttpServletResponse response) {
        ExposeBo expose = exposeService.findById(exposeid);
        if (expose == null) {
            return CommonUtil.toErrorResult(ERRORCODE.EXPOSE_MSG_NULL.getIndex(),
                    ERRORCODE.EXPOSE_MSG_NULL.getReason());
        }
        ExposeVo vo = new ExposeVo();
        BeanUtils.copyProperties(expose, vo);
        vo.setExposeid(exposeid);
        UserBaseVo baseVo = new UserBaseVo();
        UserBo createUser = userService.getUser(expose.getCreateuid());
        if (createUser != null) {
            BeanUtils.copyProperties(createUser,baseVo);
        }
        UserBo userBo = getUserLogin(request);
        if (userBo != null) {
            String userid = userBo.getId();
            vo.setCreate(userid.equals(expose.getCreateuid()));
            FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userid, expose.getCreateuid());
            if (friendsBo != null && !StringUtils.isEmpty(friendsBo.getBackname())) {
                baseVo.setUserName(friendsBo.getBackname());
            }
            ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(exposeid, userBo.getId());
            vo.setSelfSup(thumbsupBo != null);
        }
        vo.setCreateUserVo(baseVo);
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("exposeVo", vo);
        return JSONObject.fromObject(map).toString();
    }

    @ApiOperation("删除曝光消息")
    @ApiImplicitParam(name = "exposeid", value = "消息id", required = true, dataType = "string", paramType = "query")
    @DeleteMapping("/delete")
    public String delete(String exposeid, HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(), ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        ExposeBo expose = exposeService.findById(exposeid);
        if (expose == null) {
            return CommonUtil.toErrorResult(ERRORCODE.EXPOSE_MSG_NULL.getIndex(), ERRORCODE.EXPOSE_MSG_NULL.getReason());
        }
        //添加管理员 以备用
        if (expose.getCreateuid().equals(userBo.getId()) || CommonUtil.getAdminUserids().contains(userBo.getId())) {
            exposeService.deleteById(exposeid);
            return Constant.COM_RESP;
        } else {
            return CommonUtil.toErrorResult(ERRORCODE.NOTE_NOT_MASTER.getIndex(), ERRORCODE.NOTE_NOT_MASTER.getReason());
        }
    }



    @ApiOperation("曝光消息点赞")
    @ApiImplicitParam(name = "exposeid", value = "消息id", required = true, dataType = "string", paramType = "query")
    @PostMapping("/thumbsup")
    public String thumbsup(String exposeid, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        ExposeBo expose = exposeService.findById(exposeid);
        if (expose == null) {
            return CommonUtil.toErrorResult(ERRORCODE.EXPOSE_MSG_NULL.getIndex(),
                    ERRORCODE.EXPOSE_MSG_NULL.getReason());
        }
        ThumbsupBo thumbsupBo = thumbsupService.findHaveOwenidAndVisitorid(exposeid, userBo.getId());
        boolean isThumsup = false;
        if (null == thumbsupBo) {
            thumbsupBo = new ThumbsupBo();
            thumbsupBo.setType(Constant.EXPOSE_TYPE);
            thumbsupBo.setOwner_id(exposeid);
            thumbsupBo.setImage(userBo.getHeadPictureName());
            thumbsupBo.setVisitor_id(userBo.getId());
            thumbsupBo.setCreateuid(userBo.getId());
            thumbsupService.insert(thumbsupBo);
            isThumsup = true;
        } else {
            if (thumbsupBo.getDeleted() == Constant.DELETED) {
                thumbsupService.udateDeleteById(thumbsupBo.getId());
                isThumsup = true;
            }
        }
        if (isThumsup) {
            asyncController.updateExposeCounts(exposeService, exposeid, Constant.THUMPSUB_NUM, 1);
        }
        return Constant.COM_RESP;
    }

    @ApiOperation("取消曝光消息点赞")
    @ApiImplicitParam(name = "exposeid", value = "消息id", required = true, dataType = "string", paramType = "query")
    @PostMapping("/cancal-thumbsup")
    public String cancelThumbsup(String exposeid, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        ExposeBo expose = exposeService.findById(exposeid);
        if (expose == null) {
            return CommonUtil.toErrorResult(ERRORCODE.EXPOSE_MSG_NULL.getIndex(),
                    ERRORCODE.EXPOSE_MSG_NULL.getReason());
        }
        ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(exposeid, userBo.getId());
        if (thumbsupBo != null) {
            thumbsupService.deleteById(thumbsupBo.getId());
            asyncController.updateExposeCounts(exposeService, exposeid, Constant.THUMPSUB_NUM, -1);
        }
        return Constant.COM_RESP;
    }



    /**
     * 评论帖子或者回复评论
     * @return
     */
    @ApiOperation("评论曝光消息或者回复别人的评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exposeid", value = "曝光消息id", required = true,
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "countent", value = "评论内容", required = true,
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "parentid", value = "父评论id", dataType = "string", paramType = "query")})
    @PostMapping("/add-comment")
    public String addComment(@RequestParam String exposeid, @RequestParam String countent,
                             String parentid, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        ExposeBo expose = exposeService.findById(exposeid);
        if (expose == null) {
            return CommonUtil.toErrorResult(ERRORCODE.EXPOSE_MSG_NULL.getIndex(),
                    ERRORCODE.EXPOSE_MSG_NULL.getReason());
        }
        CommentBo commentBo = new CommentBo();
        commentBo.setTargetid(exposeid);
        commentBo.setParentid(parentid);
        commentBo.setUserName(userBo.getUserName());
        commentBo.setContent(countent);
        commentBo.setType(Constant.EXPOSE_TYPE);
        commentBo.setCreateuid(userBo.getId());
        commentBo.setOwnerid(expose.getCreateuid());
        commentService.insert(commentBo);
        asyncController.updateExposeCounts(exposeService, exposeid, Constant.COMMENT_NUM, 1);
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(commentBo, commentVo);
        commentVo.setCommentId(commentBo.getId());
        commentVo.setUserid(userBo.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("commentVo", commentVo);
        return JSONObject.fromObject(map).toString();
    }



    /**
     * 删除自己的帖子评论
     * @return
     */
    @ApiOperation("删除自己的评论")
    @ApiImplicitParam(name = "commentid", value = "评论id", required = true, dataType = "string", paramType = "query")
    @DeleteMapping("/delete-self-comment")
    public String deleteComments(String commentid,HttpServletRequest request,  HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        CommentBo commentBo = commentService.findById(commentid);
        if (commentBo != null) {
            if (userBo.getId().equals(commentBo.getCreateuid()) || CommonUtil.getAdminUserids().contains(userBo.getId())) {
                commentService.delete(commentid);
                asyncController.updateExposeCounts(exposeService, commentBo.getTargetid(), Constant.COMMENT_NUM, 1);
            } else {
                return CommonUtil.toErrorResult(ERRORCODE.NOTE_NOT_MASTER.getIndex(),
                        ERRORCODE.NOTE_NOT_MASTER.getReason());
            }
        }
        return Constant.COM_RESP;
    }

    /**
     * 获取曝光信息评论
     * @return
     */
    @ApiOperation("获取曝光信息评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exposeid", value = "消息id", required = true, dataType = "string", paramType =
                    "query"),
            @ApiImplicitParam(name = "page", value = "分页页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页数量", dataType = "int", paramType = "query")})
    @PostMapping("/get-comments")
    public String getComments(String exposeid, int page, int limit,
                              HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);
        ExposeBo expose = exposeService.findById(exposeid);
        if (expose == null) {
            return CommonUtil.toErrorResult(ERRORCODE.EXPOSE_MSG_NULL.getIndex(),
                    ERRORCODE.EXPOSE_MSG_NULL.getReason());
        }
        String userid = "";
        boolean isLogin = userBo != null;
        if (isLogin){
            userid = userBo.getId();
        }
        List<CommentBo> commentBos = commentService.selectCommentByType(Constant.EXPOSE_TYPE, exposeid, page, limit);
        List<CommentVo> commentVos = new ArrayList<>();
        for (CommentBo commentBo : commentBos) {
            CommentVo commentVo = new CommentVo();
            BeanUtils.copyProperties(commentBo, commentVo);
            commentVo.setCommentId(commentBo.getId());
            commentVo.setUserid(commentBo.getCreateuid());
            ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(commentBo.getId(), userid);
            commentVo.setMyThumbsup(thumbsupBo != null);
            commentVo.setThumpsubCount(commentBo.getThumpsubNum());
            if (!StringUtils.isEmpty(commentBo.getParentid())) {
                CommentBo parent = commentService.findById(commentBo.getParentid());
                commentVo.setParentUserName(parent.getUserName());
                if (isLogin && !userid.equals(commentBo.getParentid())) {
                    FriendsBo bo = friendsService.getFriendByIdAndVisitorIdAgree(userid, commentBo.getParentid());
                    if (bo != null && !StringUtils.isEmpty(bo.getBackname())) {
                        commentVo.setParentUserName(bo.getBackname());
                    } 
                }
                commentVo.setParentUserid(parent.getCreateuid());
            }
            UserBo comUser = userService.getUser(commentBo.getCreateuid());
            if (comUser != null){
                commentVo.setUserName(commentBo.getUserName());
                if (isLogin && !userid.equals(commentBo.getCreateuid())) {
                    FriendsBo bo = friendsService.getFriendByIdAndVisitorIdAgree(userid,commentBo.getCreateuid());
                    if (bo != null && !StringUtils.isEmpty(bo.getBackname())) {
                        commentVo.setUserName(bo.getBackname());
                    }
                } 
                commentVo.setUserHeadPic(comUser.getHeadPictureName());
                commentVo.setUserBirth(comUser.getBirthDay());
                commentVo.setUserSex(comUser.getSex());
                commentVo.setUserLevel(comUser.getLevel());
            }
            commentVo.setUserid(commentBo.getCreateuid());
            commentVos.add(commentVo);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("commentVoList", commentVos);
        return JSONObject.fromObject(map).toString();
    }




    /**
     * 曝光信息列表获取
     * @return
     */
    @ApiOperation("曝光信息列表获取，title和exposeType为空表示获取所有的")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "曝光标题关键字", required = true, paramType = "query",
                    dataType = "string"),
            @ApiImplicitParam(name = "exposeType", value = "曝光类型，多个以逗号隔开，可为空", paramType = "query",
                    dataType = "string"),
            @ApiImplicitParam(name = "page", value = "分页页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页数量", dataType = "int", paramType = "query")})
    @PostMapping("/get-exposes")
    public String getComments(String title, String exposeType, int page, int limit,
                              HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);
        String userid = "";
        boolean isLogin = userBo != null;
        if (isLogin) {
            userid = userBo.getId();
        }
        List<String> types = null;
        if (!StringUtils.isEmpty(exposeType)) {
            types = new ArrayList<>();
            if (exposeType.indexOf(',') > -1) {
                String [] typeArr = exposeType.split(",");
                Collections.addAll(types, typeArr);
            } else {
                types.add(exposeType);
            }
        }
        List<ExposeBo> exposeBos = exposeService.findByRegex(title, types, page, limit);
        List<ExposeVo> exposeVos = new LinkedList<>();
        for (ExposeBo exposeBo : exposeBos){
            ExposeVo exposeVo = new ExposeVo();
            BeanUtils.copyProperties(exposeBo, exposeVo);
            exposeVo.setExposeid(exposeBo.getId());
            exposeVo.setCreate(userid.equals(exposeBo.getCreateuid()));
            if (exposeBo.getCreateuid().equals(userid)){
                UserBaseVo baseVo = new UserBaseVo();
                BeanUtils.copyProperties(userBo,baseVo);
                exposeVo.setCreateUserVo(baseVo);
            } else {
                UserBo create = userService.getUser(exposeBo.getCreateuid());
                if (create != null) {
                    UserBaseVo baseVo = new UserBaseVo();
                    if (!userid.equals("")) {
                        BeanUtils.copyProperties(create,baseVo);
                        FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userid, exposeBo
                                .getCreateuid());
                        if (friendsBo != null && !StringUtils.isEmpty(friendsBo.getBackname())){
                            baseVo.setUserName(friendsBo.getBackname());
                        }
                    }
                    exposeVo.setCreateUserVo(baseVo);
                }
            }
            exposeVos.add(exposeVo);
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ret", 0);
        map.put("exposeVoList", exposeVos);
        return JSONObject.fromObject(map).toString();
    }



    @ApiOperation("曝光评论点赞")
    @ApiImplicitParam(name = "commentid", value = "评论id", required = true, dataType = "string", paramType = "query")
    @PostMapping("/com-thumbsup")
    public String comThumbsup(String commentid, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        CommentBo commentBo = commentService.findById(commentid);
        if (commentBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.COMMENT_IS_NULL.getIndex(),
                    ERRORCODE.COMMENT_IS_NULL.getReason());
        }
        ExposeBo expose = exposeService.findById(commentBo.getTargetid());
        if (expose == null) {
            return CommonUtil.toErrorResult(ERRORCODE.EXPOSE_MSG_NULL.getIndex(),
                    ERRORCODE.EXPOSE_MSG_NULL.getReason());
        }
        ThumbsupBo thumbsupBo = thumbsupService.findHaveOwenidAndVisitorid(commentid, userBo.getId());
        boolean isThumsup = false;
        if (null == thumbsupBo) {
            thumbsupBo = new ThumbsupBo();
            thumbsupBo.setType(Constant.EXPOSE_TYPE);
            thumbsupBo.setOwner_id(commentid);
            thumbsupBo.setImage(userBo.getHeadPictureName());
            thumbsupBo.setVisitor_id(userBo.getId());
            thumbsupBo.setCreateuid(userBo.getId());
            thumbsupService.insert(thumbsupBo);
            isThumsup = true;
            asyncController.updateCommentThump(commentid, 1);
        } else {
            if (thumbsupBo.getDeleted() == Constant.DELETED) {
                thumbsupService.udateDeleteById(thumbsupBo.getId());
                asyncController.updateCommentThump(commentid, 1);
                isThumsup = true;
            }
        }
        if (isThumsup) {
            asyncController.updateExposeCounts(exposeService, commentid, Constant.THUMPSUB_NUM, 1);
        }
        return Constant.COM_RESP;
    }

    @ApiOperation("取消曝光评论点赞")
    @ApiImplicitParam(name = "commentid", value = "评论信息id", required = true, dataType = "string", paramType = "query")
    @PostMapping("/cancal-com-thumbsup")
    public String cancelComThumbsup(String commentid, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        CommentBo commentBo = commentService.findById(commentid);
        if (commentBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.COMMENT_IS_NULL.getIndex(),
                    ERRORCODE.COMMENT_IS_NULL.getReason());
        }
        ExposeBo expose = exposeService.findById(commentBo.getTargetid());
        if (expose == null) {
            return CommonUtil.toErrorResult(ERRORCODE.EXPOSE_MSG_NULL.getIndex(),
                    ERRORCODE.EXPOSE_MSG_NULL.getReason());
        }
        ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(commentid, userBo.getId());
        if (thumbsupBo != null) {
            thumbsupService.deleteById(thumbsupBo.getId());
            asyncController.updateExposeCounts(exposeService, commentid, Constant.THUMPSUB_NUM, -1);
            if (commentBo.getThumpsubNum() > 0) {
                asyncController.updateCommentThump(commentid, -1);
            }
        }
        return Constant.COM_RESP;
    }
}
