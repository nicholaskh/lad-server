package com.lad.util;

import com.lad.bo.IMTermBo;
import com.lad.service.IIMTermService;
import com.pushd.ImAssistant;
import com.pushd.Message;

public class IMUtil {

	public static String FINISH = "finish";

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
			Message message = assistent.getAppKey();
			String appKey = message.getMsg();
			Message message2 = assistent.authServer(appKey);
			String term = message2.getMsg();
			iMTermBo.setTerm(term);
			iMTermService.insert(iMTermBo);
		}
		assistent.setServerTerm(iMTermBo.getTerm());
		Message message3 = assistent.subscribe(chatroomName, chatroomId, ids);
		if (message3.getStatus() == Message.Status.termError) {
			Message message = assistent.getAppKey();
			String appKey = message.getMsg();
			Message message2 = assistent.authServer(appKey);
			String term = message2.getMsg();
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

}
