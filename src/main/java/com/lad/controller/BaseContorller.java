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
import java.util.concurrent.TimeUnit;

public abstract class BaseContorller {

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
		Object userBo = session.getAttribute("userBo");
		return userBo == null ? null : (UserBo) userBo;
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
			CircleHistoryBo circleHistoryBo = circleService.findByUserIdAndCircleId(userid,circleid);
			LocationBo locationBo = locationService.getLocationBoByUserid(userid);
			if (circleHistoryBo == null) {
				circleHistoryBo = new CircleHistoryBo();
				circleHistoryBo.setCircleid(circleid);
				circleHistoryBo.setUserid(userid);
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
	 * 添加 动态信息推送表
	 * @param userid
	 * @param tatgetid
	 * @param type
	 */
	@Async
	public void addDynamicMsgs(String userid, String tatgetid, int type, IDynamicService dynamicService){
		DynamicMsgBo msgBo = new DynamicMsgBo();
		msgBo.setUserid(userid);
		msgBo.setTargetid(tatgetid);
		msgBo.setDynamicType(type);
		dynamicService.addDynamicMsg(msgBo);
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
			dynamicService.addNum(numBo);
		} else {
			RLock lock = server.getRLock("dynamicSize");
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
		} else {
			service.updateUserNickname(chatroomUserBo.getId(), nickname);
		}
	}


}
