package com.lad.util;

import com.pushd.ImAssistant;
import com.pushd.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class IMUtil {

	private static Logger logger = LogManager.getLogger(IMUtil.class);

	public static String FINISH = "finish";

	private static String term = "";

	/**
	 * 向某群聊中发系统通知
	 * @param type
	 * @param chatRoomId
	 * @param msg
	 * @return
	 */
	public static String notifyInChatRoom(int type, String chatRoomId, String msg){
		ImAssistant assistent = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
		if (assistent == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
		}


		String res = "";

		assistent.setServerTerm(term);
		Message message = assistent.publishNotificationInChatRoom(type, chatRoomId, msg);
		if (message.getStatus() == Message.Status.termError) {
			try {
				term = getTerm(assistent);
			} catch (Exception e) {
				term = "";
			}
			if(!"".equals(term)){
				message = assistent.publishNotificationInChatRoom(type, chatRoomId, msg);
			}

		}

		if (Message.Status.success != message.getStatus()) {
			res = CommonUtil.toErrorResult(ERRORCODE.PUSHED_ERROR.getIndex(), message.getMsg());
		}

		assistent.close();
		if (StringUtils.isEmpty(res)) res = FINISH;

		return res;
	}

	/**

	 *  在pushd中创建用户

	 * @param userId

	 * @return

	 */
	public static String createUser(String userId){
		ImAssistant assistent = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
		if (assistent == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
		}

		String res = "";

		assistent.setServerTerm(term);
		Message message = assistent.createUser(userId);
		if (message.getStatus() == Message.Status.termError) {
			try {
				term = getTerm(assistent);
			} catch (Exception e) {
				term = "";
			}
			if(!"".equals(term)){
				message = assistent.createUser(userId);
			}

		}

		if (Message.Status.success != message.getStatus()) {
			res = CommonUtil.toErrorResult(ERRORCODE.PUSHED_ERROR.getIndex(), message.getMsg());
		}

		assistent.close();
		if (StringUtils.isEmpty(res)) res = FINISH;

		return res;
	}

	/**

	 *  获取client接入pushd使用的Token

	 * @return

	 */
	public static String getToken(){
		ImAssistant assistent = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
		if (assistent == null) {
			logger.error("assistent为null");
			return null;
		}

		String res = null;

		assistent.setServerTerm(term);
		Message message = assistent.getToken();
		if (message.getStatus() == Message.Status.termError) {
			try {
				term = getTerm(assistent);
			} catch (Exception e) {
				term = "";
			}
			if(!"".equals(term)){
				message = assistent.getToken();
			}
		}

		if (Message.Status.success == message.getStatus()) {
			res = message.getMsg();
		}

		assistent.close();

		return res;
	}

	/**

	 * 重写聊天创建方法

	 * @param type 0 表示创建，1 表示添加用户

	 * @param chatroomId  roomid

	 * @param ids  ids

	 * @return 数组 第一个为返回结果信息，第二位term信息

	 */
	public static String subscribe(int type, String chatroomId, String... ids){
		ImAssistant assistent = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
		if (assistent == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
		}

		assistent.setServerTerm(term);
		Message message;

		if (type == 0) {
			message = assistent.createChatRoom(chatroomId, ids);
			logger.info("create room 1,  user {}, chatroom {},  res {}",ids, chatroomId, message.getMsg());
		} else {
			message = assistent.addUserToChatRoom(chatroomId, ids);
			logger.info("add user 1,  user {}, chatroom {},  res {}",ids, chatroomId, message.getMsg());
		}

		String res = "";

		if(message.getStatus() == Message.Status.termError){
			try {
				term = getTerm(assistent);
			} catch (Exception e) {
				term = "";
			}

			if(!"".equals(term)){
				assistent.setServerTerm(term);
				if (type == 0) {
					message = assistent.createChatRoom(chatroomId, ids);
					logger.info("create room 2,  user {}, chatroom {},  res {}",ids, chatroomId, message.getMsg());
				} else {
					message = assistent.addUserToChatRoom(chatroomId, ids);
					logger.info("add user 2,  user {}, chatroom {},  res {}",ids, chatroomId, message.getMsg());
				}
			}
		}

		if (Message.Status.success != message.getStatus()) {
			res = CommonUtil.toErrorResult(ERRORCODE.PUSHED_ERROR.getIndex(), message.getMsg());
		}

		assistent.close();
		if (StringUtils.isEmpty(res)) res = FINISH;

		return res;
	}

	/**

	 * 重写聊天删除方法

	 * @param chatroomId

	 * @param ids

	 * @return 数组 第一个为返回结果信息，第二位term信息

	 */
	public static String unSubscribe(String chatroomId, String... ids){
		ImAssistant assistent = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
		if (assistent == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
		}

		String res = "";

		assistent.setServerTerm(term);
		Message message = assistent.removeUserFromRoom(chatroomId, ids);
		logger.info("delete user 1,  user {}, chatroom {},  res {}",ids, chatroomId, message.getMsg());
		if (message.getStatus() == Message.Status.termError) {
			try {
				term = getTerm(assistent);
			} catch (Exception e) {
				term = "";
			}

			if(!"".equals(term)){
				assistent.setServerTerm(term);
				message = assistent.removeUserFromRoom(chatroomId, ids);
				logger.info("delete user 2,  user {}, chatroom {},  res {}",ids, chatroomId, message.getMsg());
			}
		}

		if (Message.Status.success != message.getStatus()) {
			res = CommonUtil.toErrorResult(ERRORCODE.PUSHED_ERROR.getIndex(), message.getMsg());
		}

		assistent.close();
		if (StringUtils.isEmpty(res)) res = FINISH;

		return res;
	}

	public static String disolveRoom(String chatroomId) {
		ImAssistant assistent = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
		if (assistent == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
		}
		String res = "";
		assistent.setServerTerm(term);
		Message message = assistent.disolveRoom(chatroomId);
		logger.info("delete room 1, chatroom {},  res {}", chatroomId, message.getMsg());
		if (message.getStatus() == Message.Status.termError) {
			try {
				term = getTerm(assistent);
			} catch (Exception e) {
				term = "";
			}
			if(!"".equals(term)){
				assistent.setServerTerm(term);
				message = assistent.disolveRoom(chatroomId);
				logger.info("delete room 2, chatroom {},  res {}", chatroomId, message.getMsg());
			}
		}

		if (Message.Status.success != message.getStatus()) {
			res = CommonUtil.toErrorResult(ERRORCODE.PUSHED_ERROR.getIndex(), message.getMsg());
		}
		assistent.close();
		if (StringUtils.isEmpty(res)) res = FINISH;
		return res;
	}

	/**

	 * 获取term

	 * @param assistent

	 * @return

	 */
	public static String getTerm(ImAssistant assistent) throws Exception{
		if(assistent == null){
			logger.error("getTerm error, ImAssistant is null");
			throw new Exception("ImAssistant is null");
		}

		Message message = assistent.authServer(Constant.PUSHD_APPKEY);
		if(message.getStatus() != Message.Status.success){
			logger.error(String.format("update term failed: %s", message.getMsg()));
			throw new Exception(message.getMsg());
		}

		logger.info(String.format("update term successfully, term is: %s", message.getMsg()));
		return message.getMsg();
	}

	// 初始化pushd term

	static {
		ImAssistant assistant  = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
		if(assistant == null){
			logger.error("init term failed，term will be init in next calling getTerm");
		}else{
			try{
				Message message = assistant.authServer(Constant.PUSHD_APPKEY);
				if(message.getStatus() != Message.Status.success){
					logger.error(String.format("update term failed: %s", message.getMsg()));
				}else{
					term = message.getMsg();
					logger.info(String.format("term init successfully, term is:%s", term));
				}
			}catch (Exception e){
				logger.error(String.format("init term failed，term will be init in next calling getTerm: %s",
						e.getMessage()));
			}finally {
				assistant.close();
			}
		}
	}

}
