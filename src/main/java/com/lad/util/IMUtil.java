package com.lad.util;

import com.lad.bo.IMTermBo;
import com.lad.service.IIMTermService;
import com.pushd.ImAssistant;
import com.pushd.Message;
import org.apache.commons.lang3.StringUtils;

public class IMUtil {

	public static String FINISH = "finish";


	/**
	 * 重写聊天创建方法
	 * @param chatroomName name 创建时需要输入名称，后续添加时为"",不能为null
	 * @param chatroomId  roomid
	 * @param inTerm  inTerm
	 * @param ids  ids
	 * @return 数组 第一个为返回结果信息，第二位term信息
	 */
	public static String[] subscribe(String chatroomName, String chatroomId,String inTerm, String... ids){
		ImAssistant assistent = ImAssistant.init("180.76.138.200", 2222);
		String res = "";
		if (assistent == null) {
			res = CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
			return new String[]{res, ""};
		}
		String term = "";
		if (StringUtils.isEmpty(inTerm)) {
			term = getTerm(assistent);
		}
		if ("timeout".equals(term)) {
			res = CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
			return new String[]{res, ""};
		}
		assistent.setServerTerm(term);
		Message message3 = assistent.subscribe(chatroomName, chatroomId, ids);
		try {
			if (message3.getStatus() == Message.Status.termError) {
				term = getTerm(assistent);
				assistent.setServerTerm(term);
				Message message4 = assistent.subscribe(chatroomName, chatroomId,
						ids);
				if (Message.Status.success != message4.getStatus()) {
					res = CommonUtil.toErrorResult(message4.getStatus(),
							message4.getMsg());
				}
			} else if (Message.Status.success != message3.getStatus()) {
				res = CommonUtil.toErrorResult(message3.getStatus(),
						message3.getMsg());
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
		ImAssistant assistent = ImAssistant.init("180.76.138.200", 2222);
		String res = "";
		if (assistent == null) {
			res = CommonUtil.toErrorResult(
					ERRORCODE.PUSHED_CONNECT_ERROR.getIndex(),
					ERRORCODE.PUSHED_CONNECT_ERROR.getReason());
			return new String[]{res, ""};
		}
		String term = "";
		if (StringUtils.isEmpty(inTerm)) {
			term = getTerm(assistent);
		}
		assistent.setServerTerm(term);
		Message message = assistent.unSubscribe(chatroomId, ids);
		try {
			if (message.getStatus() == Message.Status.termError) {
				term = getTerm(assistent);
				assistent.setServerTerm(term);
				Message message2 = assistent.unSubscribe(chatroomId, ids);
				if (Message.Status.success != message2.getStatus()) {
					res = CommonUtil.toErrorResult(message2.getStatus(),
							message2.getMsg());
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


	public static String subscribe(IIMTermService iMTermService, String userid,
			String chatroomName, String chatroomId, String... ids) {
		ImAssistant assistent = ImAssistant.init("180.76.138.200", 2222);
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
		Message message3 = assistent.subscribe(chatroomName, chatroomId, ids);
		if (message3.getStatus() == Message.Status.termError) {
			String term = getTerm(assistent);
			iMTermService.updateByUserid(userid, term);
			assistent.setServerTerm(term);
			Message message4 = assistent.subscribe(chatroomName, chatroomId,
					ids);
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

	/**
	 * 获取term
	 * @param assistent
	 * @return
	 */
	public static String getTerm(ImAssistant assistent){
		Message message = assistent.getAppKey();
		String appKey = message.getMsg();
		Message message2 = assistent.authServer(appKey);
		return  message2.getMsg();
	}

	public static String unSubscribe(IIMTermService iMTermService,
			String userid, String chatroomId, String... ids) {
		ImAssistant assistent = ImAssistant.init("180.76.138.200", 2222);
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
		Message message3 = assistent.unSubscribe(chatroomId, ids);
		//错误再次执行
		if (message3.getStatus() == Message.Status.termError) {
			Message message = assistent.getAppKey();
			String appKey = message.getMsg();
			Message message2 = assistent.authServer(appKey);
			String term = message2.getMsg();
			iMTermService.updateByUserid(userid, term);
			assistent.setServerTerm(term);
			Message message4 = assistent.unSubscribe(chatroomId, ids);
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
		ImAssistant assistent = ImAssistant.init("180.76.138.200", 2222);
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
		Message message3 = assistent.disolveRoom(chatroomId);
		if (message3.getStatus() == Message.Status.termError) {
			Message message = assistent.getAppKey();
			String appKey = message.getMsg();
			Message message2 = assistent.authServer(appKey);
			String term = message2.getMsg();
			iMTermService.updateByUserid(userid, term);
			assistent.setServerTerm(term);
			Message message4 = assistent.disolveRoom(chatroomId);
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

}
