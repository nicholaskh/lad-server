package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.IMUtil;
import com.lad.util.JPushUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述：异步方法类
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/2
 */
@Component
public class AsyncController extends BaseContorller {

    private static Logger logger = LogManager.getLogger(ChatroomController.class);

    @Autowired
    private ICircleService circleService;

    @Autowired
    private IUserService userService;

    @Autowired
    private INoteService noteService;

    @Autowired
    private ISearchService searchService;

    @Autowired
    private RedisServer redisServer;

    @Autowired
    private IFriendsService friendsService;

    @Autowired
    private IReasonService reasonService;

    @Autowired
    private IPartyService partyService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IChatroomService chatroomService;

    @Autowired
    private IInforService inforService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IInforRecomService inforRecomService;



    /**
     * 聊天室
     * 更新用户的聊天室的方法
     * @param userid
     * @param chatroomid
     */
    @Async
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
                chatroom.add(chatroomid);
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
    }



    /**
     * 圈子
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
     * 圈子帖子
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
     * 资讯
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
     * 资讯
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
     * 资讯
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
     * 资讯
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
     * 资讯
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
     * 资讯
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
     * 搜索
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



    /**
     * 圈子信息推送
     * 推送给好友
     * @param path
     * @param accepts
     */
    @Async
    public void pushToFriends(String circleName, String path, List<String> accepts){
        for (String userid : accepts) {
            UserBo userBo = userService.getUser(userid);
            if (userBo == null) {
                continue;
            }
            List<FriendsBo> friendsBos = friendsService.getFriendByUserid(userid);
            for (FriendsBo friendsBo : friendsBos) {
                UserBo friend = userService.getUser(friendsBo.getFriendid());
                if (friend == null) {
                    continue;
                }
                String name = "";
                FriendsBo bo = friendsService.getFriendByIdAndVisitorIdAgree(friendsBo.getFriendid(), userid);
                if (bo != null) {
                    name = bo.getBackname();
                }
                if  (StringUtils.isEmpty(name)) {
                    name = userBo.getUserName();
                }
                String content = String.format("“%s”已申请加入圈子【%s】，你也快去看看吧",name, circleName);
                JPushUtil.push("圈子通知", content, path, friend.getId());
                addMessage(messageService, path, content, "圈子通知", friend.getId());
            }
        }
    }


    /**
     * 帖子更新阅读信息
     * @param circleid
     * @param num
     */
    @Async
    public void updateCircieNoteSize(String circleid, int num){
        RLock lock = redisServer.getRLock(circleid + "noteSize");
        try {
            lock.lock(2,TimeUnit.SECONDS);
            circleService.updateNotes(circleid, num);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 帖子阅读
     * 更新红人信息
     * @param userBo
     * @param noteBo
     * @param circleid
     * @param currentDate
     */
    @Async
    public void updateRedStar(UserBo userBo, NoteBo noteBo, String circleid, Date currentDate){
        RedstarBo redstarBo = commentService.findRedstarBo(userBo.getId(), circleid);
        int curretWeekNo = CommonUtil.getWeekOfYear(currentDate);
        int year = CommonUtil.getYear(currentDate);
        if (redstarBo == null) {
            redstarBo = setRedstarBo(userBo.getId(), circleid, curretWeekNo, year);
            commentService.insertRedstar(redstarBo);
        }
        //判断贴的作者是不是自己
        boolean isNotSelf = !userBo.getId().equals(noteBo.getCreateuid());
        boolean isNoteUserCurrWeek = true;
        //如果帖子作者不是自己
        if (isNotSelf) {
            //帖子作者没有红人数据信息，则添加
            RedstarBo noteRedstarBo = commentService.findRedstarBo(noteBo.getCreateuid(), circleid);
            if (noteRedstarBo == null) {
                noteRedstarBo = setRedstarBo(noteBo.getCreateuid(), circleid, curretWeekNo, year);
                commentService.insertRedstar(noteRedstarBo);
            } else {
                //判断帖子作者周榜是不是当前周，是则添加数据，不是则更新周榜数据
                isNoteUserCurrWeek = (year == noteRedstarBo.getYear() && curretWeekNo == noteRedstarBo.getWeekNo());
            }
        }
        //判断自己周榜是不是同一周，是则添加数据，不是则更新周榜数据
        boolean isCurrentWeek = (year == redstarBo.getYear() && curretWeekNo == redstarBo.getWeekNo());
        //更新自己或他人红人评论数量，需要加锁，保证数据准确
        RLock lock = redisServer.getRLock(Constant.COMOMENT_LOCK);
        try {
            lock.lock(5, TimeUnit.SECONDS);
            //更新自己的红人信息
            if (isCurrentWeek) {
                commentService.addRadstarCount(userBo.getId(), circleid);
            } else {
                commentService.updateRedWeekByUser(userBo.getId(), curretWeekNo, year);
            }
            if (isNotSelf) {
                //更新帖子作者的红人信息
                if (isNoteUserCurrWeek) {
                    commentService.addRadstarCount(noteBo.getCreateuid(), circleid);
                } else {
                    commentService.updateRedWeekByUser(noteBo.getCreateuid(), curretWeekNo, year);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private RedstarBo setRedstarBo(String userid, String circleid, int weekNo, int year){
        RedstarBo redstarBo = new RedstarBo();
        redstarBo.setUserid(userid);
        redstarBo.setCommentTotal((long) 1);
        redstarBo.setCommentWeek((long) 1);
        redstarBo.setWeekNo(weekNo);
        redstarBo.setCircleid(circleid);
        redstarBo.setYear(year);
        return redstarBo;
    }

    /**
     * 帖子点赞
     * @param commentid
     * @param num
     */
    @Async
    public void updateCommentThumbsup(String commentid, int num){
        if (num != 0){
            RLock lock = redisServer.getRLock(commentid);
            try {
                lock.lock(1, TimeUnit.SECONDS);
                commentService.updateThumpsubNum(commentid, num);
            } finally {
                lock.unlock();
            }
        }
        if (num > 0) {
            CommentBo commentBo = commentService.findById(commentid);
            if (commentBo != null &&  commentBo.getType() == Constant.NOTE_TYPE) {
                NoteBo noteBo = noteService.selectById(commentBo.getNoteid());
                if (noteBo != null) {
                    updateCircieUnReadNum(commentBo.getCreateuid(), noteBo.getCircleId());
                }
            }
        }
    }

    /**
     * 帖子 未读信息
     * @param userid
     * @param cirlceid
     */
    @Async
    public void updateCircieUnReadNum(String userid, String cirlceid){
        RLock lock = redisServer.getRLock(userid + "UnReadNumLock");
        try{
            lock.lock(2, TimeUnit.SECONDS);
            ReasonBo reasonBo = reasonService.findByUserAndCircle(userid, cirlceid, Constant.ADD_AGREE);
            if (reasonBo == null) {
                reasonBo = new ReasonBo();
                reasonBo.setCircleid(cirlceid);
                reasonBo.setCreateuid(userid);
                reasonBo.setStatus(Constant.ADD_AGREE);
                reasonBo.setUnReadNum(1);
                reasonService.insert(reasonBo);
            } else {
                reasonService.updateUnReadNum(userid, cirlceid, 1);
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * 圈子未读信息数量添加
     * @param pushUserid
     * @param circleid
     */
    @Async
    public void updateCircieNoteUnReadNum(String pushUserid, String circleid){
        CircleBo circleBo = circleService.selectById(circleid);
        if (circleBo == null) {
            return;
        }
        HashSet<String> users = circleBo.getUsers();
        if (users.contains(pushUserid)) {
            users.remove(pushUserid);
        }
        logger.info(" circle {} note unRead update, users {}", circleid, users);
        RLock lock = redisServer.getRLock(circleid + "UnReadNumLock");
        try{
            lock.lock(3, TimeUnit.SECONDS);
            reasonService.updateUnReadNum(users, circleBo.getId());
        } finally {
            lock.unlock();
        }
    }


    /**
     * 聚会
     * 推送给好友
     * @param userid
     * @param content
     * @param path
     */
    @Async
    public void pushFriends(String userid, String content, String path, HashSet<String> circleUsers){
        List<FriendsBo> friendsBos = friendsService.getFriendByUserid(userid);
        if (!CommonUtil.isEmpty(friendsBos)) {
            String[] friendids = new String[friendsBos.size()];
            int i = 0;
            for (FriendsBo friendsBo : friendsBos) {
                //如果好友同时是圈友，则不在推送
                if (circleUsers.contains(friendsBo.getId())) {
                    continue;
                }
                friendids[i++] = friendsBo.getId();
            }
            if (i > 0) {
                JPushUtil.push("聚会通知", content, path, friendids);
                addMessage(messageService, path, content, "聚会通知", userid, friendids);
            }
        }
    }

    /**
     * 聚会数据更新
     * @param partyid
     * @param num
     */
    @Async
    public void updatePartyCollectNum(String partyid, int num){
        RLock lock = redisServer.getRLock(partyid + "partyCollect");
        try {
            lock.lock(2, TimeUnit.SECONDS);
            partyService.updateCollect(partyid, num);
        } finally {
            lock.unlock();
        }
    }


    /**
     * 聚会
     * 更新红人信息
     * @param userBo
     * @param partyBo
     * @param currentDate
     */
    @Async
    public void updateRedStar(UserBo userBo, PartyBo partyBo, Date currentDate){
        String circleid = partyBo.getCircleid();
        RedstarBo redstarBo = commentService.findRedstarBo(userBo.getId(), circleid);
        int curretWeekNo = CommonUtil.getWeekOfYear(currentDate);
        int year = CommonUtil.getYear(currentDate);
        if (redstarBo == null) {
            redstarBo = new RedstarBo();
            redstarBo.setUserid(userBo.getId());
            redstarBo.setCommentTotal((long) 1);
            redstarBo.setCommentWeek((long) 1);
            redstarBo.setWeekNo(curretWeekNo);
            redstarBo.setCircleid(circleid);
            redstarBo.setYear(year);
            commentService.insertRedstar(redstarBo);
        }
        boolean isNotSelf = !userBo.getId().equals(partyBo.getCreateuid());
        boolean isNoteUserCurrWeek = true;
        //如果帖子作者不是自己
        if (isNotSelf) {
            //帖子作者没有红人数据信息，则添加
            RedstarBo noteRedstarBo = commentService.findRedstarBo(partyBo.getCreateuid(), circleid);
            if (noteRedstarBo == null) {
                noteRedstarBo = new RedstarBo();
                noteRedstarBo.setUserid(userBo.getId());
                noteRedstarBo.setCommentTotal((long) 1);
                noteRedstarBo.setCommentWeek((long) 1);
                noteRedstarBo.setWeekNo(curretWeekNo);
                noteRedstarBo.setCircleid(partyBo.getCircleid());
                noteRedstarBo.setYear(year);
                commentService.insertRedstar(noteRedstarBo);
            } else {
                //判断帖子作者周榜是不是当前周，是则添加数据，不是则更新周榜数据
                isNoteUserCurrWeek = (year == noteRedstarBo.getYear() && curretWeekNo == noteRedstarBo.getWeekNo());
            }
        }
        //判断自己周榜是不是同一周，是则添加数据，不是则更新周榜数据
        boolean isCurrentWeek = (year == redstarBo.getYear() && curretWeekNo == redstarBo.getWeekNo());
        //更新自己或他人红人评论数量，需要加锁，保证数据准确
        RLock lock = redisServer.getRLock(Constant.COMOMENT_LOCK);
        try {
            lock.lock(5, TimeUnit.SECONDS);
            //更新自己的红人信息
            if (isCurrentWeek) {
                commentService.addRadstarCount(userBo.getId(), circleid);
            } else {
                commentService.updateRedWeekByUser(userBo.getId(), curretWeekNo, year);
            }
            if (isNotSelf) {
                //更新聚会作者的红人信息
                if (isNoteUserCurrWeek) {
                    commentService.addRadstarCount(partyBo.getCreateuid(), circleid);
                } else {
                    commentService.updateRedWeekByUser(partyBo.getCreateuid(), curretWeekNo, year);
                }
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * 异步更新阅读点赞等数据
     * @param id
     * @param numType
     * @param num
     * @return
     */
    @Async
    public void updateExposeCounts(IExposeService service, String id, int numType, int num){
        RLock lock = redisServer.getRLock(id.concat(String.valueOf(numType)));
        try {
            lock.lock(2, TimeUnit.SECONDS);
            service.updateCounts(id, numType, num);
        } finally {
            lock.unlock();
        }
    }


    /**
     * 异步更新阅读点赞等数据
     * @param commentid
     * @param num
     * @return
     */
    @Async
    public void updateCommentThump(String commentid, int num){
        RLock lock = redisServer.getRLock(commentid.concat("thump"));
        try {
            lock.lock(2, TimeUnit.SECONDS);
            commentService.updateThumpsubNum(commentid, num);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 演出发布团队向商家推送演出信息
     * @param service
     * @param showType
     * @param username
     * @param userid
     */
    @Async
    public void pushShowToCompany(IShowService service, String showType,String showid, String username, String
            userid){
        //查找商家发布的招演信息
        List<ShowBo> showBos = service.findByShowType(showType, ShowBo.NEED);
        if (CommonUtil.isEmpty(showBos)) {
            return;
        }
        //先去除重复的数据
        HashSet<String> userids = new LinkedHashSet<>();
        String path = String.format("/show/show-info?showid=%s",showid);
        String content = String.format("{“%s”}刚刚发布了{%s}演出，赶紧去看看吧！",username, showType);
        HashSet<String> friendids = new LinkedHashSet<>();

        List<String> showids = new LinkedList<>();
        for (ShowBo showBo : showBos) {
            //已超时的不在推送
            Date time = CommonUtil.getDate(showBo.getShowTime(),"yyyy-MM-dd HH:mm:ss");
            if (time == null || System.currentTimeMillis() > time.getTime()) {
                showids.add(showBo.getId());
                continue;
            }
            //判断招演发布人与接演发布人是不是好友，好友的话需要在推送消息时需要显示昵称
            FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(showBo.getCreateuid(), userid);
            if (friendsBo != null && !StringUtils.isEmpty(friendsBo.getBackname())){
                //同一个演出团队一种类型只发送一个给商家
                if (!friendids.contains(friendsBo.getId())) {
                    username =  friendsBo.getBackname();
                    path = String.format("/show/show-info?showid=%s",showid);
                    content = String.format("{“%s”}刚刚发布了{“%s”}演出，赶紧去看看吧！",username, showType);
                    JPushUtil.push("演出通知", content, path, showBo.getCreateuid());
                    addMessage(messageService, path, content, "演出通知", userid, showBo.getCreateuid());
                    friendids.add(friendsBo.getId());
                }
            } else {
                userids.add(showBo.getCreateuid());
            }
        }
        //非好友统一推送信息
        if (!userids.isEmpty()) {
            String[] useridArr = userids.toArray(new String[]{});
            JPushUtil.push("演出通知", content, path, useridArr);
            addMessage(messageService, path, content, "演出通知", userid, useridArr);
        }
        //更新过期招演信息
        /*if (!showids.isEmpty()) {
            service.updateShowStatus(showids, 1);
        }*/
    }


    /**
     * 商家发布招演信息向演出团队推送
     * @param service
     * @param show
     */
    @Async
    public void pushShowToCreate(IShowService service, ShowBo show){
        String showType = show.getShowType();
        List<ShowBo> showBos = service.findByShowType(showType, ShowBo.PROVIDE);
        String path = String.format("/show/show-info?showid=%s",show.getId());
        String content = String.format("{“%s”}正在找{“%s”}演出，赶紧去看看吧！",show.getCompany(), showType);
        HashSet<String> userids = new LinkedHashSet<>();
        showBos.forEach(showBo -> userids.add(showBo.getCreateuid()));
        List<CircleBo> circleBos = circleService.selectByRegexName(showType);
        for (CircleBo circleBo : circleBos) {
            userids.addAll(circleBo.getMasters());
            userids.add(circleBo.getCreateuid());
        }
        if (!userids.isEmpty()) {
            String[] useridArr = userids.toArray(new String[]{});
            JPushUtil.push("演出通知", content, path, useridArr);
            addMessage(messageService, path, content, "演出通知", show.getCreateuid(), useridArr);
        }
    }


    /**
     * 添加演出类型或更新次数
     * @param showType
     * @param createuid
     */
    @Async
    public void addShowTypes(String showType, String createuid){
    	System.out.println("111");
        CircleTypeBo circleTypeBo = circleService.findByName(showType, 1, CircleTypeBo.SHOW_TYPE);
        if (circleTypeBo == null) {
            circleTypeBo = new CircleTypeBo();
            circleTypeBo.setLevel(1);
            circleTypeBo.setTimes(1);
            circleTypeBo.setType(CircleTypeBo.SHOW_TYPE);
            circleTypeBo.setCreateuid(createuid);
            circleTypeBo.setCategory(showType);
            circleService.addCircleType(circleTypeBo);
        } else {
            circleService.updateCircleTypeTimes(circleTypeBo.getId());
        }
    }
}
