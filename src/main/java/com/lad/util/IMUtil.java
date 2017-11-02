package com.lad.util;

import com.lad.bo.IMTermBo;
import com.lad.service.IIMTermService;
import com.pushd.ImAssistant;
import com.pushd.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IMUtil {

	private static Logger logger = LogManager.getLogger(IMUtil.class);


	public static String FINISH = "finish";


	/**
	 * 重写聊天创建方法
	 * @param type 0 表示创建，1 表示添加用户 
	 * @param chatroomId  roomid
	 * @param inTerm  inTerm
	 * @param ids  ids
	 * @return 数组 第一个为返回结果信息，第二位term信息
	 */
	public static String[] subscribe(int type, String chatroomId, String inTerm, String... ids){
		ImAssistant assistent = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
		String res = "";
		if (assistent == null) {
			res = CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
			return new String[]{res, ""};
		}
		String term = StringUtils.isEmpty(inTerm) ? getTerm(assistent) : inTerm;
		try {
			if ("timeout".equals(term)) {
				res = CommonUtil.toErrorResult(
						ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
						ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
				return new String[]{res, ""};
			}
			assistent.setServerTerm(term);
			Message message = null;
			if (type == 0) {
				message = assistent.createChatRoom(chatroomId, ids);
				logger.info("create room 1,  user {}, chatroom {},  res {}",ids, chatroomId, message.getMsg());
			} else {
				message = assistent.addUserToChatRoom(chatroomId, ids);
				logger.info("add user 1,  user {}, chatroom {},  res {}",ids, chatroomId, message.getMsg());
			}
			if (message.getStatus() == Message.Status.termError) {
				term = getTerm(assistent);
				assistent.setServerTerm(term);
				if (type == 0) {
					message = assistent.createChatRoom(chatroomId, ids);
					logger.info("create room 2,  user {}, chatroom {},  res {}",ids, chatroomId, message.getMsg());
				} else {
					message = assistent.addUserToChatRoom(chatroomId, ids);
					logger.info("add user 2,  user {}, chatroom {},  res {}",ids, chatroomId, message.getMsg());
				}
				if (Message.Status.success != message.getStatus()) {
					res = CommonUtil.toErrorResult(message.getStatus(),
							message.getMsg());
				}
			} else if (Message.Status.success != message.getStatus()) {
				res = CommonUtil.toErrorResult(message.getStatus(),
						message.getMsg());
			}
		} finally {
			assistent.close();
		}
		if (StringUtils.isEmpty(res)) {
			res = FINISH;	
		}
		return new String[]{res, term};
	}

	/**
	 * 重写聊天删除方法
	 * @param chatroomId
	 * @param ids
	 * @return 数组 第一个为返回结果信息，第二位term信息
	 */
	public static String[] unSubscribe(String chatroomId, String inTerm, String... ids){
		ImAssistant assistent = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
		String res = "";
		if (assistent == null) {
			res = CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
			return new String[]{res, ""};
		}
		String term = StringUtils.isEmpty(inTerm) ? getTerm(assistent) : inTerm;
		if ("timeout".equals(term)) {
			res = CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
			return new String[]{res, ""};
		}
		try {
			assistent.setServerTerm(term);
			Message message = assistent.removeUserFromRoom(chatroomId, ids);
			logger.info("delete user 1,  user {}, chatroom {},  res {}",ids, chatroomId, message.getMsg());
			if (message.getStatus() == Message.Status.termError) {
				term = getTerm(assistent);
				assistent.setServerTerm(term);
				message = assistent.removeUserFromRoom(chatroomId, ids);
				logger.info("delete user 2,  user {}, chatroom {},  res {}",ids, chatroomId, message.getMsg());
				if (Message.Status.success != message.getStatus()) {
					res = CommonUtil.toErrorResult(message.getStatus(),
							message.getMsg());
				}
			} else if (Message.Status.success != message.getStatus()) {
				res = CommonUtil.toErrorResult(message.getStatus(),
						message.getMsg());
			}
		} finally {
			assistent.close();
		}
		if (StringUtils.isEmpty(res)) {
			res = FINISH;
		}
		return new String[]{res, term};
	}


	/**
	 * 获取term
	 * @param assistent
	 * @return
	 */
	public static String getTerm(ImAssistant assistent){
		Message message = assistent.getAppKey();
		Message message2 = assistent.authServer(message.getMsg());
		return  message2.getMsg();
	}

	public static String unSubscribe(IIMTermService iMTermService,
			String userid, String chatroomId, String... ids) {
		ImAssistant assistent = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
		if (assistent == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
		}
		IMTermBo iMTermBo = iMTermService.selectByUserid(userid);
		if (iMTermBo == null) {
			iMTermBo = new IMTermBo();
			iMTermBo.setUserid(userid);
			iMTermBo.setTerm(getTerm(assistent));
			iMTermService.insert(iMTermBo);
		}
		assistent.setServerTerm(iMTermBo.getTerm());
		Message message3 = assistent.removeUserFromRoom(chatroomId, ids);
		//错误再次执行
		if (message3.getStatus() == Message.Status.termError) {
			Message message = assistent.getAppKey();
			String appKey = message.getMsg();
			Message message2 = assistent.authServer(appKey);
			String term = message2.getMsg();
			iMTermService.updateByUserid(userid, term);
			assistent.setServerTerm(term);
			Message message4 = assistent.removeUserFromRoom(chatroomId, ids);
			if (Message.Status.success != message4.getStatus()) {
				assistent.close();
				return CommonUtil.toErrorResult(message4.getStatus(),
						message4.getMsg());
			}
		} else if (Message.Status.success != message3.getStatus()) {
			assistent.close();
			return CommonUtil.toErrorResult(message3.getStatus(),
					message3.getMsg());
		}
		return IMUtil.FINISH;
	}

	public static String disolveRoom(IIMTermService iMTermService,
			String userid, String chatroomId) {
		ImAssistant assistent = ImAssistant.init(Constant.PUSHD_IP, Constant.PUSHD_POST);
		if (assistent == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
		}
		IMTermBo iMTermBo = iMTermService.selectByUserid(userid);
		if (iMTermBo == null) {
			iMTermBo = new IMTermBo();
			iMTermBo.setUserid(userid);
			iMTermBo.setTerm(getTerm(assistent));
			iMTermService.insert(iMTermBo);
		}
		assistent.setServerTerm(iMTermBo.getTerm());
		Message message = assistent.disolveRoom(chatroomId);
		logger.info("delete room 1, chatroom {},  res {}", chatroomId, message.getMsg());
		if (message.getStatus() == Message.Status.termError) {
			message = assistent.getAppKey();
			String appKey = message.getMsg();
			message = assistent.authServer(appKey);
			String term = message.getMsg();
			iMTermService.updateByUserid(userid, term);
			assistent.setServerTerm(term);
			message = assistent.disolveRoom(chatroomId);
			logger.info("delete room 2, chatroom {},  res {}", chatroomId, message.getMsg());
			if (Message.Status.success != message.getStatus()) {
				assistent.close();
				return CommonUtil.toErrorResult(message.getStatus(),
						message.getMsg());
			} else {
				assistent.close();
			}
		} else if (Message.Status.success != message.getStatus()) {
			assistent.close();
			return CommonUtil.toErrorResult(message.getStatus(),
					message.getMsg());
		}
		return IMUtil.FINISH;
	}

}
