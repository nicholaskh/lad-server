package com.lad.controller;

import com.lad.bo.MessageBo;
import com.lad.bo.UserBo;
import com.lad.service.IMessageService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.MessageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Api("消息接口")
@RestController
@RequestMapping("message")
public class MessageController extends BaseContorller {

	@Autowired
	private IMessageService messageService;

	@ApiOperation("未读消息查询")
	@PostMapping("/unread-message")
	public String my_message(int page, int limit,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		List<MessageBo> list = messageService.findByUserId(userBo.getId(), 0, page, limit);
		List<MessageVo> message_from_me_vo = new ArrayList<MessageVo>();
		for (MessageBo item : list) {
			MessageVo vo = new MessageVo();
			BeanUtils.copyProperties(item, vo);
			message_from_me_vo.add(vo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("messageVos", message_from_me_vo);
		return JSONObject.fromObject(map).toString();
	}


	@ApiOperation("所有消息查询")
	@PostMapping("/all-message")
	public String allMessage(int page, int limit,
							 HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<MessageBo> list = messageService.findByUserId(userBo.getId(), -1, page, limit);
		List<MessageVo> message_from_me_vo = new ArrayList<MessageVo>();
		for (MessageBo item : list) {
			MessageVo vo = new MessageVo();
			BeanUtils.copyProperties(item, vo);
			vo.setMessageid(item.getId());
			message_from_me_vo.add(vo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("messageVos", message_from_me_vo);
		return JSONObject.fromObject(map).toString();
	}


	@ApiOperation("查看消息详情")
	@ApiImplicitParam(name = "messageid", value = "消息id", dataType = "string", paramType = "query")
	@PostMapping("/get-message")
	public String allMessage(String messageid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		MessageBo messageBo = messageService.selectById(messageid);
		if (messageBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.MESSAGE_NULL.getIndex(),
					ERRORCODE.MESSAGE_NULL.getReason());
		}
		MessageVo messageVo = new MessageVo();
		BeanUtils.copyProperties(messageBo, messageVo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("messageVo", messageVo);
		messageService.updateMessage(messageid, 1);
		return JSONObject.fromObject(map).toString();
	}


	@ApiOperation("删除消息")
	@ApiImplicitParam(name = "messageid", value = "消息id", dataType = "string", paramType = "query")
	@PostMapping("/del-message")
	public String delMessage(String messageid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		messageService.deleteMessage(messageid);
		return Constant.COM_RESP;
	}

	@ApiOperation("批量删除消息")
	@ApiImplicitParam(name = "messageids", value = "消息id,多个以逗号隔开", dataType = "string", paramType = "query")
	@PostMapping("/multi-del-message")
	public String delsMessage(String messageids, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String[] idArr = CommonUtil.getIds(messageids);
		List<String> ids = new ArrayList<>();
		Collections.addAll(ids, idArr);
		messageService.deleteMessages(ids);
		return Constant.COM_RESP;
	}

	@ApiOperation("批量阅读消息")
	@ApiImplicitParam(name = "messageids", value = "消息id,多个以逗号隔开", dataType = "string", paramType = "query")
	@PostMapping("/multi-read-message")
	public String betchReadMessage(String messageids, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String[] idArr = CommonUtil.getIds(messageids);
		List<String> ids = new ArrayList<>();
		Collections.addAll(ids, idArr);
		messageService.betchUpdateMessage(ids, 1);
		return Constant.COM_RESP;
	}

}
