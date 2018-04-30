package com.lad.controller;

import com.lad.bo.ShowBo;
import com.lad.bo.UserBo;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.ShowVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/28
 */
@Slf4j
@Api("招接演出接口")
@RestController
@RequestMapping("show")
public class ShowController extends BaseContorller {


    @Autowired
    private IUserService userService;

    @Autowired
    private IFriendsService friendsService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IThumbsupService thumbsupService;

    @Autowired
    private IShowService showService;

    @Autowired
    private AsyncController asyncController;


    @ApiOperation("发表招接演出信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "showVo", value = "演出信息实体类", required = true),
            @ApiImplicitParam(name = "images", value = "图片信息,如果是招演类型，则为营业执照图片", paramType = "query", dataType =
                    "file"),
            @ApiImplicitParam(name = "video", value = "视频信息，与图片二选一", paramType = "query", dataType = "file")})
    @PostMapping("/insert")
    public String insert(@RequestBody ShowVo showVo, MultipartFile[] images, MultipartFile video,
                         HttpServletRequest request, HttpServletResponse response) {
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
            if (showVo.getType() == ShowBo.NEED) {
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

        if (showVo.getType() == ShowBo.NEED) {


        } else {




        }

        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("showid", showBo.getId());
        return JSONObject.fromObject(map).toString();
    }

}
