package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.scrapybo.InforBo;
import com.lad.service.ICommentService;
import com.lad.service.IInforService;
import com.lad.service.IThumbsupService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.MyException;
import com.lad.vo.CommentVo;
import com.lad.vo.InforVo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.redisson.api.RMapCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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


    @RequestMapping("/group-types")
    @ResponseBody
    public String inforGroups(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        boolean isGetType = false;
        if (!session.isNew() && session.getAttribute("isLogin") != null) {
            UserBo userBo = (UserBo) session.getAttribute("userBo");
            InforSubscriptionBo mySub = inforService.findMySubs(userBo.getId());
            if (mySub != null && !mySub.getSubscriptions().isEmpty()) {
                map.put("healthTypes", mySub.getSubscriptions());
                map.put("securityTypes", mySub.getSubscriptions());
                isGetType  = true;
            }
        }
        if (!isGetType) {
            RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
            if (cache.containsKey("healthTypes")) {
                Object groupTypes = cache.get("healthTypes");
                map.put("healthTypes", groupTypes);
                map.put("securityTypes", "");
            } else {
                List<InforBo> inforBos = inforService.findAllGroups();
                int size =  inforBos.size();
                HashSet<String> groupTypes = new LinkedHashSet<>();
                for (int i = 0; i< size; i++) {
                    //聚合查询后，分类名称值被放置到id上了
                    groupTypes.add(inforBos.get(i).getClassName());
                }
                cache.put("healthTypes", groupTypes, 0, TimeUnit.MINUTES);
                map.put("healthTypes", groupTypes);
                map.put("securityTypes", "");
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
            inforVo.setText(inforBo.getText());
            inforVos.add(inforVo);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("inforVoList", inforVos);
        return JSONObject.fromObject(map).toString();
    }

    @RequestMapping("/recommend-groups")
    @ResponseBody
    public String recommendGroups(HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        InforSubscriptionBo mySub = inforService.findMySubs(userBo.getId());
        LinkedList<String> mySubs = mySub.getSubscriptions();
        map.put("mySubTypes", mySub.getSubscriptions());
        
        RMapCache<String, Object> cache = redisServer.getCacheMap(Constant.TEST_CACHE);
        List<String> groupList = new ArrayList<>();
        if (cache.containsKey("healthTypes")) {
            String groupTypes = (String)cache.get("healthTypes");
            JSONArray array = JSONArray.fromObject(groupTypes);
            int size = array.size();
            for (int i = 0; i < size; i++) {
                String groupName = (String)array.get(i);
                if (!mySubs.contains(groupName)) {
                    groupList.add(groupName);
                } 
            }
        }
        map.put("recoTypes", groupList);
        return JSONObject.fromObject(map).toString();
    }


    @RequestMapping("/update-groups")
    @ResponseBody
    public String updateGroups(String[] groupNames, HttpServletRequest request, HttpServletResponse response){
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

        InforSubscriptionBo mySub = inforService.findMySubs(userBo.getId());

        LinkedList<String> mySubs = (LinkedList<String>) Arrays.asList(groupNames);
        if (null == mySub) {
            mySub = new InforSubscriptionBo();
            mySub.setUserid(userBo.getId());
            mySub.setCreateuid(userBo.getId());
            mySub.setSubscriptions(mySubs);
            inforService.insertSub(mySub);
        } else {
            mySub.setSubscriptions(mySubs);
            inforService.updateSub(userBo.getId(), mySubs);
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
            readNumBo.setCommentNum(readNumBo.getCommentNum());
            readNumBo.setThumpsubNum(readNumBo.getThumpsubNum());
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

        InforBo inforBo = inforService.findById(inforid);
        if (inforBo == null) {
            return CommonUtil.toErrorResult(
                    ERRORCODE.INFOR_IS_NULL.getIndex(),
                    ERRORCODE.INFOR_IS_NULL.getReason());
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
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

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
                InforBo inforBo = inforService.findById(targetid);
                if (inforBo == null ) {
                    return CommonUtil.toErrorResult(
                            ERRORCODE.INFOR_IS_NULL.getIndex(),
                            ERRORCODE.INFOR_IS_NULL.getReason());
                }
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
            }
            inforService.updateThumpsub(thumbsupBo.getOwner_id(), 1);
        }
        return Constant.COM_RESP;
    }

    @RequestMapping("/cancal-thumbsup")
    @ResponseBody
    public String cancelThumbsup(@RequestParam String targetid, @RequestParam int type, HttpServletRequest request, HttpServletResponse response){
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
        return commentVo;
    }
}
