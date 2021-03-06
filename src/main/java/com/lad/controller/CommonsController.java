package com.lad.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.lad.bo.OptionBo;
import com.lad.bo.UserBo;
import com.lad.service.IMarriageService;
import com.lad.service.IOldFriendService;
import com.lad.service.IShowService;
import com.lad.service.SpouseService;
import com.lad.service.TravelersService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.lad.vo.OptionVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;

/**
 * 通过改接口获取一些比较通用的数据
 *
 */
@RestController
@RequestMapping("common")
@SuppressWarnings("all")
public class CommonsController extends BaseContorller{
	@Autowired
	public IMarriageService marriageService;
	
	@Autowired
	public TravelersService travelersService;
	
	@Autowired
	private SpouseService spouseService;
	
	@Autowired
	private IOldFriendService oldFriendService;
	
    @Autowired
    private IShowService showService;
    
    @GetMapping("getHobOptions")
    public String getHobbysOptions(){
    	List<OptionBo> supOptions= marriageService.getHobbysSupOptions();
    	Map<String,Set<String>> map = new HashMap<>();
    	
    	for (OptionBo supOption : supOptions) {
    		List<OptionBo> sonOptions = marriageService.getHobbysSonOptions(supOption.getId());
    		Set<String> sonOptionSet = new LinkedHashSet<>();
    		for (OptionBo sonOption : sonOptions) {
    			sonOptionSet.add(sonOption.getValue());
			}
    		map.put(supOption.getValue(), sonOptionSet);
		}
    	Map result = new HashMap<>();
    	result.put("ret", 0);
    	result.put("result", map);
    	return JSON.toJSONString(result);
    }
	
	/**
	 * 获取当前用户发布消息的条数
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("getNums")
	public String getPublishNum(HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}	
		Map map = new HashMap<>();
		map.put("ret", 0);
		int marriageBoyNum = marriageService.findPublishNum(userBo.getId());
		map.put("marriageBoyNum", marriageBoyNum);
		int marriageGirlNum = marriageService.findPublishGirlNum(userBo.getId());
		map.put("marriageGirlNum", marriageGirlNum);
		int travelersNum = travelersService.findPublishNum(userBo.getId());
		map.put("travelersNum", travelersNum);
		int spouseNum = spouseService.findPublishNum(userBo.getId());
		map.put("spouseNum", spouseNum);
		int oldFriendNum = oldFriendService.findPublishNum(userBo.getId());
		map.put("oldFriendNum", oldFriendNum);
		int showZhaoNum = showService.findPublishZhaoNum(userBo.getId());
		map.put("showZhaoNum", showZhaoNum);
		int showJieNum = showService.findPublishJieNum(userBo.getId());
		map.put("showJieNum", showJieNum);
		return JSONObject.fromObject(map).toString();
	}
	

	@ApiOperation("查询选项")
	@GetMapping("/options-all-search")
	public String getAllOptions(HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<OptionBo> options = marriageService.getOptions();

		Map<String,List<OptionBo>> map = new HashMap<>();
		List<OptionBo> salarys = new ArrayList<>();
		List<OptionBo> job = new ArrayList<>();
		List<OptionBo> hobbys = new ArrayList<>();
		for (OptionBo optionBo : options) {
			if("salary".equals(optionBo.getField())){
				salarys.add(optionBo);
			}
			if("job".equals(optionBo.getField())){
				job.add(optionBo);
			}
			if("hobbys".equals(optionBo.getField())){
				hobbys.add(optionBo);
			}
		}
		map.put("salary", salarys);
		map.put("job", job);
		map.put("hobbys", hobbys);
		return JSONObject.fromObject(map).toString();
	}
	
	@ApiOperation("查询选项")
	@PostMapping("/options-search")
	public String getOptions(
			@RequestBody @ApiParam(name = "optionVo", value = "封装前端请求参数的实体", required = true) OptionVo ov,HttpServletRequest request, HttpServletResponse response){
		List<OptionBo> options = marriageService.getOptions(ov);
		if(options ==null){
			return "无对应选项";
		}
		String jsonString = JSON.toJSONString(options);
		return jsonString;
	}
	
	
	@GetMapping("job-options")
	public String getJobOptions(){
		List<OptionBo> jobOptions = marriageService.getJobOptions();
		return JSON.toJSONString(jobOptions);
	}
	
	@GetMapping("salary-options")
	public String getSalaryOptions(){
		List<OptionBo> salOptions = marriageService.getSalaryOptions();
		return JSON.toJSONString(salOptions);
	}
}
