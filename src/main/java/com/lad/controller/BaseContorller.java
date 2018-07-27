package com.lad.controller;


import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.MyException;
import org.redisson.api.RLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;

public abstract class BaseContorller {

	protected int dayTimeMins = 24 * 60 * 60 * 1000;

	/**
	 * 定向到错误页面
	 * 
	 * @return view
	 */
	public String toErrorPage() {
		return "/error";
	}
	
	/**
	 * 定向到错误页面
	 * 
	 * @param msg 错误消息
	 * @param model ModelMap
	 * @return view
	 */
	public String toErrorPage(String msg, ModelMap model) {
		model.addAttribute("ERROR_MSG", msg);
		return this.toErrorPage();
	}


	/**
	 * 统一校验session是否存在，不存在以异常跑出
	 * @param request
	 * @throws MyException
	 */
	public UserBo checkSession(HttpServletRequest request, IUserService userService) throws MyException{
		HttpSession session = request.getSession();
		if (session.isNew()) {
			throw new MyException(CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason()));
		}
		if (session.getAttribute("isLogin") == null) {
			throw new MyException(CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason()));
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			throw new MyException(CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason()));
		}
		userBo = userService.getUser(userBo.getId());
		if (null == userBo) {
			throw new MyException(CommonUtil.toErrorResult(
					ERRORCODE.USER_NULL.getIndex(),
					ERRORCODE.USER_NULL.getReason()));
		}
		session.setAttribute("userBo", userBo);
		return userBo;
	}

	/**
	 * 获取session
	 * @param request
	 */
	public UserBo getUserLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return null;
		}
		if (session.getAttribute("isLogin") == null) {
			return null;
		}
        return (UserBo) session.getAttribute("userBo");
	}

	/**
	 * 同步session ,用户数据又修改之后,保证session与数据库同步
	 * @param request
	 */
	@Async
	public void updateUserSession(HttpServletRequest request, IUserService userService) {
		HttpSession session = request.getSession();
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (null != userBo) {
			userBo = userService.getUser(userBo.getId());
			if (userBo == null) {
				//用户不存在，注销用户session
				session.invalidate();
			} else {
				//更新用户session
				session.setAttribute("userBo", userBo);
			}
		}
	}

	/**
	 * 更新圈子访问记录信息
	 * @param userid
	 * @param circleid
	 * @param locationService
	 * @param circleService
	 */
	@Async
	public void updateHistory(String userid, String circleid,
							  ILocationService locationService, ICircleService circleService){
		try {
			// 获取个人的圈子操作历史
			CircleHistoryBo circleHistoryBo = circleService.findByUserIdAndCircleId(userid,circleid);
			// 获取个人的地址信息
			LocationBo locationBo = locationService.getLocationBoByUserid(userid);
			if (circleHistoryBo == null) {
				circleHistoryBo = new CircleHistoryBo();
				circleHistoryBo.setCircleid(circleid);
				circleHistoryBo.setUserid(userid);
				circleHistoryBo.setType(0);
				if (null != locationBo) {
					circleHistoryBo.setPosition(locationBo.getPosition());
				} else {
					circleHistoryBo.setPosition(new double[]{0, 0});
				}
				circleService.insertHistory(circleHistoryBo);
			} else {
				circleService.updateHistory(circleHistoryBo.getId(), locationBo.getPosition());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新圈子中各种访问信息或者人气等等
	 * @param circleService
	 * @param redisServer
	 * @param circleid
	 * @param num
	 * @param type
	 */
	@Async
	public void updateCircleHot(ICircleService circleService, RedisServer redisServer,
								String circleid, int num, int type){
		RLock lock = redisServer.getRLock(Constant.CHAT_LOCK);
		try {
			//3s自动解锁
			lock.lock(3, TimeUnit.SECONDS);
			circleService.updateCircleHot(circleid, num, type);
		} finally {
			lock.unlock();
		}
	}


	/**
	 * 更新动态信息数量表
	 * @param userid
	 */
	@Async
	public void updateDynamicNums(String userid, int num, IDynamicService dynamicService, RedisServer server){
		DynamicNumBo numBo = dynamicService.findNumByUserid(userid);
		if (numBo == null) {
			numBo = new DynamicNumBo();
			numBo.setUserid(userid);
			numBo.setNumber(1);
			numBo.setTotal(1);
			dynamicService.addNum(numBo);
		} else {
			RLock lock = server.getRLock(userid+"dynamicSize");
			try {
				lock.lock(2,TimeUnit.SECONDS);
				dynamicService.updateNumbers(numBo.getId(), num);
			} finally {
				lock.unlock();
			}
		}
	}


	/**
	 * 添加聊天室用户的昵称
	 * @param chatroomid
	 * @param nickname
	 */
	@Async
	public void addChatroomUser(IChatroomService service, UserBo userBo, String chatroomid, String nickname){
		ChatroomUserBo chatroomUserBo = service.findChatUserByUserAndRoomid(userBo.getId(), chatroomid);
		if (chatroomUserBo == null) {
			chatroomUserBo = new ChatroomUserBo();
			chatroomUserBo.setChatroomid(chatroomid);
			chatroomUserBo.setUserid(userBo.getId());
			chatroomUserBo.setNickname(nickname);
			chatroomUserBo.setUsername(userBo.getUserName());
			chatroomUserBo.setShowNick(false);
			chatroomUserBo.setDisturb(false);
			service.insertUser(chatroomUserBo);
		} else if (chatroomUserBo.getDeleted() == Constant.DELETED){
			service.updateUserNickname(chatroomUserBo.getId(), nickname);
		}
	}


	/**
	 * 获取聚会状态
	 * @param startTimes
	 * @param appointment
	 * @return  1 进行中， 2报名结束， 3活动结束
	 */
	public int getPartyStatus(LinkedHashSet<String> startTimes, int appointment){
		if (!CommonUtil.isEmpty(startTimes)) {
			Iterator<String> iterator = startTimes.iterator();
			String lastTime = "";
			while (iterator.hasNext()){
				lastTime = iterator.next();
			}
			if (lastTime.equals("0")) {
				return 1;
			}
			Date lastDate = CommonUtil.getDate(lastTime, "yyyy-MM-dd HH:mm");
			if (lastDate != null) {
				Date currentLastTime = CommonUtil.getLastDate(lastDate);
				//当前时间大于聚会的结束时间 聚会结束
				if (System.currentTimeMillis() >= currentLastTime.getTime()) {
					return 3;
				}
				long last = lastDate.getTime();
				//减去提前预约天数
				if (appointment > 0) {
					last = last - (appointment * dayTimeMins);
				}
				//报名时间已经结束
				if (System.currentTimeMillis() >= last) {
					return 2;
				}
			}
		}
		return 1;
	}

	@Async
	public void addMessage(IMessageService service, String path, String content, String title,
						   String createuid, String... userids){
		for (String userid : userids) {
			MessageBo messageBo = new MessageBo();
			messageBo.setContent(content);
			messageBo.setPath(path);
			messageBo.setUserid(userid);
			messageBo.setCreateuid(createuid);
			messageBo.setTitle(title);
			service.insert(messageBo);
		}
	}

	/**
	 * 消息添加到列表
	 * @param service
	 * @param path
	 * @param content
	 * @param title
	 * @param noteid
	 * @param type
	 * @param sourceid
	 * @param userid
	 */
	@Async
	public void addMessage(IMessageService service, String path, String content, String title, String noteid,
						   int type, String sourceid, String circleid, String createuid, String userid){
		MessageBo messageBo = new MessageBo();
		messageBo.setContent(content);
		messageBo.setPath(path);
		messageBo.setUserid(userid);
		messageBo.setTitle(title);
		messageBo.setTargetid(noteid);
		messageBo.setType(type);
		messageBo.setSourceid(sourceid);
		messageBo.setCircleid(circleid);
		messageBo.setCreateuid(createuid);
		service.insert(messageBo);
	}
}
