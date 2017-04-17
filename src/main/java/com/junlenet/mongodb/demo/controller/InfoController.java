package com.junlenet.mongodb.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.junlenet.mongodb.demo.bo.InfoBo;
import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.service.IInfoService;
import com.junlenet.mongodb.demo.vo.InfoVo;

import net.sf.json.JSONObject;

@Controller
@Scope("prototype")
@RequestMapping("info")
public class InfoController extends BaseContorller {
	@Autowired
	private IInfoService infoService;

	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(String owner_id, String content, String source, HttpServletRequest request,
			HttpServletResponse response) {
		if (StringUtils.isEmpty(owner_id)) {
			return "{\"ret\":-1,\"error\":\"owner_id is null\"}";
		}
		if (StringUtils.isEmpty(content)) {
			return "{\"ret\":-1,\"error\":\"content is null\"}";
		}
		if (StringUtils.isEmpty(source)) {
			return "{\"ret\":-1,\"error\":\"source is null\"}";
		}
		InfoBo infoBo = new InfoBo();
		infoBo.setOwnerId(owner_id);
		infoBo.setContent(content);
		infoBo.setSource(source);
		infoService.insert(infoBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/my-info")
	@ResponseBody
	public String my_info(String owner_id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":-1,\"error\":\"error session\"}";
		}
		if (StringUtils.isEmpty(owner_id)) {
			return "{\"ret\":-1,\"error\":\"owner_id is null\"}";
		}
		List<InfoBo> list = infoService.selectByOwnerId(userBo.getId());
		List<InfoVo> listVo = new ArrayList<InfoVo>();
		for (InfoBo item : list) {
			InfoVo vo = new InfoVo();
			BeanUtils.copyProperties(vo, item);
			listVo.add(vo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("info", listVo);
		return JSONObject.fromObject(map).toString();
	}
}
