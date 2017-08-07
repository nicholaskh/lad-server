package com.lad.controller;

import com.lad.bo.CommentBo;
import com.lad.bo.InforSubscriptionBo;
import com.lad.bo.ThumbsupBo;
import com.lad.bo.UserBo;
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
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 功能描述： 资讯接口
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/7/29
 */
@Controller
@RequestMapping("infor")
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
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        if (!session.isNew() && session.getAttribute("isLogin") != null) {
            UserBo userBo = (UserBo) session.getAttribute("userBo");
            InforSubscriptionBo mySub = inforService.findMySubs(userBo.getId());
            if (mySub != null && !mySub.getSubscriptions().isEmpty()) {
                map.put("groupTypes", mySub.getSubscriptions());
            } else {
                List<InforBo> inforBos = inforService.findAllGroups();
                String[] groupTypes = new String[inforBos.size()];
                for (int i = 0; i< inforBos.size(); i++) {
                    groupTypes[i] = inforBos.get(i).getClassName();
                }
                map.put("groupTypes", groupTypes);
            }
        } else {
            List<InforBo> inforBos = inforService.findAllGroups();
            String[] groupTypes = new String[inforBos.size()];
            for (int i = 0; i< inforBos.size(); i++) {
                groupTypes[i] = inforBos.get(i).getClassName();
            }
            map.put("groupTypes", groupTypes);
        }
        return JSONObject.fromObject(map).toString();
    }

    @RequestMapping("/group-infors")
    @ResponseBody
    public String groupInfors(String groupName, String inforTime, int limit,
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
            Long readNum = inforService.findReadNum(inforBo.getId());
            inforVo.setReadNum(readNum);
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
        
        List<InforBo> inforBos = inforService.findAllGroups();
        String[] groupTypes = new String[inforBos.size()];
        for (int i = 0; i< inforBos.size(); i++) {
            String groupName = inforBos.get(i).getClassName();
            if (!mySubs.contains(groupName)) {
                groupTypes[i] =groupName;
            }
        }
        map.put("recoTypes", groupTypes);
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
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }

        InforBo inforBo = inforService.findById(inforid);

        InforVo inforVo = new InforVo();
        inforVo.setInforid(inforBo.getId());
        BeanUtils.copyProperties(inforBo, inforVo);
        Long readNum = inforService.findReadNum(inforBo.getId());
        inforVo.setReadNum(readNum);
        ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(inforBo.getId(), userBo.getId());
        inforVo.setSelfSub(thumbsupBo != null);
        long thuSupNum = thumbsupService.selectByOwnerIdCount(inforBo.getId());
        inforVo.setThumpsubNum(thuSupNum);

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
