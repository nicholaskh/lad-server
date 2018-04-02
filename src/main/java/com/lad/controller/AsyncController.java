package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.IMUtil;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述：异步方法类
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/2
 */
@EnableAsync
@Component
public class AsyncController {

    private static Logger logger = LogManager.getLogger(ChatroomController.class);

    @Autowired
    public ICircleService circleService;

    @Autowired
    public IUserService userService;

    @Autowired
    public ILocationService locationService;

    @Autowired
    public INoteService noteService;

    @Autowired
    public IFeedbackService feedbackService;

    @Autowired
    public ISearchService searchService;

    @Autowired
    public RedisServer redisServer;

    @Autowired
    public IFriendsService friendsService;

    @Autowired
    public IReasonService reasonService;

    @Autowired
    public IPartyService partyService;

    @Autowired
    public IDynamicService dynamicService;

    @Autowired
    public ICollectService collectService;

    @Autowired
    public IThumbsupService thumbsupService;

    @Autowired
    public ICommentService commentService;

    @Autowired
    public IChatroomService chatroomService;
    @Autowired
    public IInforService inforService;

    @Autowired
    public IMessageService messageService;

    @Autowired
    public IInforRecomService inforRecomService;



    /**
     * 更新用户的聊天室的方法
     * @param userid
     * @param chatroomid
     */
    public void updateUserChatroom(String userid, String chatroomid, boolean isAdd){
        RLock lock = redisServer.getRLock(userid.concat("chatroom"));
        try {
            lock.lock(3, TimeUnit.SECONDS);
            UserBo userBo = userService.getUser(userid);
            if (userBo == null) {
                return;
            }
            HashSet<String> chatroom = userBo.getChatrooms();
            LinkedList<String> chatroomTops = userBo.getChatroomsTop();
            HashSet<String> showRooms = userBo.getShowChatrooms();
            if (isAdd) {
                userBo.setChatrooms(chatroom);
                //个人聊天室中没有当前聊天室，则添加到个人的聊天室
                if (!chatroom.contains(chatroomid)) {
                    chatroom.add(chatroomid);
                }
                showRooms.add(chatroomid);
            } else {
                if (chatroom.contains(chatroomid)) {
                    chatroom.remove(chatroomid);
                    userBo.setChatrooms(chatroom);
                }
                if (chatroomTops.contains(chatroomid)) {
                    chatroomTops.remove(chatroomid);
                    userBo.setChatroomsTop(chatroomTops);
                }
                showRooms.remove(chatroomid);
            }
            userBo.setShowChatrooms(showRooms);
            userService.updateChatrooms(userBo);
        } finally {
            lock.unlock();
        }
        chatroomService.deleteChatroomUser(userid, chatroomid);
    }



    /**
     * 更新用户阅读数据信息
     * @param noticeid
     * @param userid
     */
    @Async
    public void updateNoticeRead(HashSet<String> users, String noticeid, String userid){
        RLock lock = redisServer.getRLock(noticeid);
        try {
            lock.lock(3, TimeUnit.SECONDS);
            CircleNoticeBo noticeBo = circleService.findNoticeById(noticeid);
            LinkedHashSet<String> readUser = noticeBo.getReadUsers();
            LinkedHashSet<String> unReadUser = noticeBo.getUnReadUsers();
            //若有新圈子成员加入
            if (users.size() != (readUser.size() + unReadUser.size())) {
                users.removeAll(readUser);
                unReadUser.addAll(users);
            }
            if (users.contains(userid)) {
                unReadUser.remove(userid);
                readUser.add(userid);
                circleService.updateNoticeRead(noticeid, readUser, unReadUser);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 批量更新用户阅读数据信息
     * @param noticeid
     * @param userid
     */
    @Async
    public void updateNoticeRead(String noticeid, String userid){
        RLock lock = redisServer.getRLock(noticeid);
        try {
            lock.lock(3, TimeUnit.SECONDS);
            CircleNoticeBo noticeBo = circleService.findNoticeById(noticeid);
            LinkedHashSet<String> readUser = noticeBo.getReadUsers();
            LinkedHashSet<String> unReadUser = noticeBo.getUnReadUsers();
            //若有新圈子成员加入
            readUser.add(userid);
            unReadUser.remove(userid);
            circleService.updateNoticeRead(noticeid, readUser, unReadUser);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 删除群聊中的用户聊天昵称
     * @param userid
     * @param chatroomid
     */
    @Async
    public void deleteNickname(String userid, String chatroomid){
        chatroomService.deleteChatroomUser(userid, chatroomid);
    }


    // 向群中某人被邀请加入群聊通知
    @Async
    public void addRoomInfo(UserBo userBo, String chatroomid, List<String> imIds, List<String> imNames,
                             Object[] otherNameAndId){
        if(imIds.size() > 0 && otherNameAndId[0] != null){
            JSONObject json = new JSONObject();
            json.put("masterId", userBo.getId());
            json.put("masterName", userBo.getUserName());
            json.put("hitIds", imIds);
            json.put("hitNames", imNames);
            json.put("otherIds", otherNameAndId[1]);
            json.put("otherNames", otherNameAndId[0]);

            String res = IMUtil.notifyInChatRoom(Constant.SOME_ONE_BE_INVITED_OT_CHAT_ROOM, chatroomid, json.toString());
            if(!IMUtil.FINISH.equals(res)){
                logger.error("failed notifyInChatRoom Constant.SOME_ONE_BE_INVITED_OT_CHAT_ROOM, %s",res);
            }
        }
    }

    /**
     * 往群聊发提示消息
     * @param roomType
     * @param chatroomid
     * @param pushInfo
     */
    @Async
    public void pushRoomInfo(int roomType, String chatroomid, String pushInfo){
        String res = IMUtil.notifyInChatRoom(roomType, chatroomid, pushInfo);
        if(!IMUtil.FINISH.equals(res)){
            logger.error("failed notifyInChatRoom chatroomid: %s , pushInfo: %s, res: %s",chatroomid, pushInfo, res);
        }
    }


    /**
     * 往群聊指定用户发提示消息
     * @param roomType
     * @param chatroomid
     * @param pushInfo
     */
    @Async
    public void pushRoomUsersInfo(int roomType, String chatroomid, String pushInfo, String... userids){
        String res = IMUtil.notifyInChatRoom(roomType, chatroomid, pushInfo);
        if(!IMUtil.FINISH.equals(res)){
            logger.error("failed notifyInChatRoom chatroomid: %s , pushInfo: %s, res: %s",chatroomid, pushInfo, res);
        }
    }


    @Async
    public void updateChatroomNameAndUser(String chatroomid, String name, boolean isNameSet,LinkedHashSet<String>
            set) {
        // 如果群聊没有修改过名称，自动修改名称
        RLock lock = redisServer.getRLock(chatroomid.concat("users"));
        try {
            lock.lock(2, TimeUnit.SECONDS);
            chatroomService.updateNameAndUsers(chatroomid, name, isNameSet, set);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 修改用户群聊，保证用户群聊同步
     */
    @Async
    public void updateUserChatroooms(String userid, String chatroomid){
        RLock lock = redisServer.getRLock(userid.concat("chatroom"));
        try {
            lock.lock(3, TimeUnit.SECONDS);
            UserBo userBo = userService.getUser(userid);
            HashSet<String> chatroom = userBo.getChatrooms();
            HashSet<String> showRooms = userBo.getShowChatrooms();
            showRooms.add(chatroomid);
            userBo.setChatrooms(chatroom);
            //个人聊天室中没有当前聊天室，则添加到个人的聊天室
            if (!chatroom.contains(chatroomid)) {
                chatroom.add(chatroomid);
                userBo.setShowChatrooms(showRooms);
            }
            userService.updateChatrooms(userBo);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 删除用户
     * @param chatroomid
     * @param users
     */
    @Async
    public void updateChatroomUser(String chatroomid, LinkedHashSet<String> users){
        RLock lock = redisServer.getRLock(chatroomid.concat("users"));
        try {
            lock.lock(2, TimeUnit.SECONDS);
            chatroomService.updateUsers(chatroomid, users);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 更新聊天室
     * @param userBo
     * @param removes
     * @param removeTops
     */
    @Async
    public void updateUserRoom(UserBo userBo, HashSet<String> removes, LinkedList<String> removeTops){
        HashSet<String> chatrooms = userBo.getChatrooms();
        LinkedList<String> chatroomsTop = userBo.getChatroomsTop();
        chatroomsTop.removeAll(removeTops);
        chatrooms.removeAll(removes);
        userBo.setChatroomsTop(chatroomsTop);
        userBo.setChatrooms(chatrooms);
        userService.updateChatrooms(userBo);
    }





    /**
     * 用户更新自己分类访问记录
     * @param module
     * @param type
     */
    @Async
    public void updateUserReadHis(String userid, String module, String className, int type){
        String currenDate = CommonUtil.getCurrentDate(new Date());
        InforUserReadHisBo readHisBo = inforRecomService.findByReadHis(userid, type, module, className);
        if (readHisBo != null) {
            //当前类的最后一次浏览时间
            if (!readHisBo.getLastDate().equals(currenDate)) {
                inforRecomService.updateUserReadHis(readHisBo.getId(), currenDate);
            }
        } else {
            readHisBo = new InforUserReadHisBo();
            readHisBo.setLastDate(currenDate);
            readHisBo.setModule(module);
            readHisBo.setClassName(className);
            readHisBo.setType(type);
            readHisBo.setUserid(userid);
            inforRecomService.addUserReadHis(readHisBo);
        }
        boolean isNew = false;
        InforUserReadBo readBo = inforRecomService.findUserReadByUserid(userid);
        if (readBo == null) {
            readBo = new InforUserReadBo();
            readBo.setUserid(userid);
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
                updateUserReadAll(userid, readBo);
            }
        }
    }

    /**
     * 删除180天前的浏览分类
     * @param readBo
     */
    @Async
    public void updateUserReadAll(String userid, InforUserReadBo readBo){
        Date currenDate = CommonUtil.getZeroDate(new Date());
        Date halfTime = CommonUtil.getHalfYearTime(currenDate);
        String halfStr = CommonUtil.getCurrentDate(halfTime);

        List<InforUserReadHisBo> readHisBos = inforRecomService.findUserReadHisBeforeHalf(userid, halfStr);
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
    public void updateInforHistroy(String inforid, String module, int type){
        Date currenDate = CommonUtil.getZeroDate(new Date());
        String dateStr = CommonUtil.getCurrentDate(new Date());

        Date halfTime = CommonUtil.getHalfYearTime(currenDate);
        String halgStr = CommonUtil.getCurrentDate(halfTime);

        RLock lock = redisServer.getRLock(inforid);
        List<InforHistoryBo> historyBos = null;
        List<String> ids = new ArrayList<>();
        try {
            lock.lock(5, TimeUnit.SECONDS);
            InforHistoryBo historyBo = inforRecomService.findTodayHis(inforid, dateStr);
            if (historyBo == null) {
                historyBo = new InforHistoryBo();
                historyBo.setDayNum(1);
                historyBo.setInforid(inforid);
                historyBo.setModule(module);
                historyBo.setType(type);
                historyBo.setReadDate(dateStr);
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
                inforRecomService.updateRecomByInforid(recomBo.getId(), 1, 1);
                historyBos = inforRecomService.findHalfYearHis(inforid, halgStr);
                if (historyBos == null || historyBos.isEmpty()){
                    return;
                } else {
                    int disNum = 0;
                    for (InforHistoryBo history : historyBos) {
                        disNum += history.getDayNum();
                        ids.add(history.getId());
                    }
                    inforRecomService.updateRecomByInforid(recomBo.getId(), -disNum, 1);
                }
            }
        } finally {
            lock.unlock();
        }
        if (!ids.isEmpty()){
            inforRecomService.updateZeroHis(ids);
        }
    }




    /**
     * 更新
     * @param inforid
     * @param module
     * @param type
     */
    @Async
    public void updateGrouprHistroy(String inforid, String module, String className, int type){
        Date currenDate = CommonUtil.getZeroDate(new Date());
        Date halfTime = CommonUtil.getHalfYearTime(currenDate);
        String dateStr = CommonUtil.getCurrentDate(new Date());
        String halfStr = CommonUtil.getCurrentDate(halfTime);
        List<InforHistoryBo> historyBos = null;
        List<String> ids = new ArrayList<>();
        RLock lock = redisServer.getRLock(inforid);
        try {
            lock.lock(5, TimeUnit.SECONDS);
            InforHistoryBo historyBo = inforRecomService.findTodayHis(inforid, dateStr);
            if (historyBo == null) {
                historyBo = new InforHistoryBo();
                historyBo.setDayNum(1);
                historyBo.setInforid(inforid);
                historyBo.setModule(module);
                historyBo.setClassName(className);
                historyBo.setType(type);
                historyBo.setReadDate(dateStr);
                inforRecomService.addInfoHis(historyBo);
            } else {
                inforRecomService.updateHisDayNum(historyBo.getId(),1);
            }
            InforGroupRecomBo groupRecomBo = inforRecomService.findInforGroup(module,className, type);
            if (groupRecomBo == null) {
                groupRecomBo = new InforGroupRecomBo();
                groupRecomBo.setModule(module);
                groupRecomBo.setType(type);
                groupRecomBo.setHalfyearNum(1);
                groupRecomBo.setTotalNum(1);
                groupRecomBo.setClassName(className);
                inforRecomService.addInforGroup(groupRecomBo);
            } else {
                inforRecomService.updateInforGroup(groupRecomBo.getId(), 1, 1);
                historyBos = inforRecomService.findHalfYearHis(inforid, halfStr);
                if (historyBos == null || historyBos.isEmpty()){
                    return;
                } else {
                    int disNum = 0;
                    for (InforHistoryBo history : historyBos) {
                        disNum += history.getDayNum();
                        ids.add(history.getId());
                    }
                    inforRecomService.updateInforGroup(groupRecomBo.getId(), -disNum, 1);
                }
            }
        } finally {
            lock.unlock();
        }
        if (!ids.isEmpty()){
            inforRecomService.updateZeroHis(ids);
        }

    }

    /**
     * 更新资讯集合访问
     * @param module
     * @param className
     * @param type
     */
    @Async
    public void updateGrouopRecom(String module, String className, int type){
        InforGroupRecomBo groupRecomBo = inforRecomService.findInforGroup(module,className, type);
        if (groupRecomBo == null) {
            groupRecomBo = new InforGroupRecomBo();
            groupRecomBo.setModule(module);
            groupRecomBo.setType(type);
            groupRecomBo.setHalfyearNum(1);
            groupRecomBo.setTotalNum(1);
            groupRecomBo.setClassName(className);
            inforRecomService.addInforGroup(groupRecomBo);
        } else {
            inforRecomService.updateInforGroup(groupRecomBo.getId(), 1, 1);
        }
    }

    /**
     * 更新个人阅读记录
     * @param userid
     * @param inforid
     * @param inforType
     * @param module
     * @param className
     */
    @Async
    public void addUserReadhis(String userid, String inforid, int inforType, String module, String className){
        UserReadHisBo hisBo = inforService.findByInforid(userid, inforid);
        if (hisBo == null) {
            hisBo = new UserReadHisBo();
            hisBo.setUserid(userid);
            hisBo.setInforid(inforid);
            hisBo.setInforType(inforType);
            hisBo.setModule(module);
            hisBo.setClassName(className);
            hisBo.setLastTime(new Date());
            inforService.addUserReadHis(hisBo);
        } else {
            inforService.updateUserReadHis(hisBo.getId());
        }
        List<UserReadHisBo> userReadHisBos = inforService.findByUserAndInfor(userid, inforType);
        int size = userReadHisBos.size();
        if (size > 5) {
            List<String> removeids = new LinkedList<>();
            for (int i = 5; i < size; i++) {
                removeids.add(userReadHisBos.get(i).getId());
            }
            inforService.deleteUserReadHis(removeids);
        }
    }


    /**
     * 搜索记录添加
     * @param keyword
     */
    @Async
    public void saveKeyword(String keyword, int inforType) {
        RLock lock = redisServer.getRLock("inforkeyword" + inforType);
        try {
            lock.lock(2, TimeUnit.SECONDS);
            SearchBo searchBo = searchService.findInforByKeyword(keyword, 2, inforType);
            if (searchBo == null) {
                searchBo = new SearchBo();
                searchBo.setKeyword(keyword);
                searchBo.setType(2);
                searchBo.setTimes(1);
                searchBo.setInforType(inforType);
                searchService.insert(searchBo);
            } else {
                searchService.update(searchBo.getId());
            }
        } finally {
            lock.unlock();
        }
    }
}
