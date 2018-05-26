package com.lad.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.lad.bo.BaseBo;
import com.lad.bo.OptionBo;
import com.lad.bo.RequireBo;
import com.lad.bo.UserBo;
import com.lad.bo.WaiterBo;
import com.lad.service.IMarriageService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.BaseVo;
import com.lad.vo.OptionVo;
import com.lad.vo.RequireVo;
import com.lad.vo.WaiterVo;
import com.mongodb.WriteResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Api("儿媳/女婿接口")
@RestController
@RequestMapping("marriage")
public class MarriageController extends BaseContorller{

	@Autowired
	public IMarriageService marriageService;
	
	@ApiOperation("查找新发布")
	@GetMapping("/newpublish-search")
	public String getNewPublic(int type,int page,int limit,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<WaiterBo> list = marriageService.getNewPublish(type,page,limit);
		return JSON.toJSONString(list);
	}
	
	/**
	 * 推荐
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("推荐")
	@GetMapping("/recommend-search")
	public String getRecommend(String waiterId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<Map> list = marriageService.getRecommend(waiterId);
		return JSONArray.fromObject(list).toString();
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
	
	@ApiOperation("查询发布详情")
	@GetMapping("/publishe-desc-search")
	public String getPublishDescById(String waiterId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		WaiterBo waiter = marriageService.findWaiterById(waiterId);
		
		if(waiter == null){
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		RequireBo require = marriageService.findRequireById(waiterId);
		
		if(require == null){
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		Map<String,BaseBo> map = new HashMap<>();
		map.put("waiter", waiter);
		map.put("require", require);
		return JSONObject.fromObject(map).toString();
	}
	
	@ApiOperation("不再推荐")
	@PutMapping("/pass")
	public String addPass(String waiterId,String passId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<String> list = null;
		if(waiterId != null && waiterId!=""){
			list = getList(waiterId,"pass");
		}else{
			return "参数错误";
		}
		WaiterBo care = marriageService.findWaiterById(passId);
		
		if(care != null){
			list.add(passId);
		}
		
		Map<String, Object> params = new HashMap<>();
		params.put("pass", list);
		WriteResult result = marriageService.updateByParams(waiterId, params , WaiterBo.class);
		
		if(result.isUpdateOfExisting()){
			return Constant.COM_RESP;
		}
		return Constant.COM_FAIL_RESP;
	}
	
	@ApiOperation("添加关注")
	@PutMapping("/care-insert")
	public String addCare(String waiterId,String careId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<String> list = null;
		if(waiterId != null && waiterId!=""){
			list = getList(waiterId,"cares");
		}else{
			return "参数错误";
		}
		WaiterBo care = marriageService.findWaiterById(careId);
		
		if(care != null){
			list.add(careId);
		}
		
		Map<String, Object> params = new HashMap<>();
		params.put("cares", list);
		WriteResult result = marriageService.updateByParams(waiterId, params , WaiterBo.class);
		
		if(result.isUpdateOfExisting()){
			return Constant.COM_RESP;
		}
		return Constant.COM_FAIL_RESP;
	}
	
	@ApiOperation("移除关注")
	@PutMapping("/care-delete")
	public String deleteCare(String waiterId,String careId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<String> list = null;
		if(waiterId != null && waiterId!=""){
			list = getList(waiterId,"cares");
		}else{
			return "参数错误";
		}
		WaiterBo care = marriageService.findWaiterById(careId);
		if(care != null&&list.contains(careId)){
			list.remove(careId);
		}
		
		Map<String, Object> params = new HashMap<>();
		params.put("cares", list);
		WriteResult result = marriageService.updateByParams(waiterId, params , WaiterBo.class);
		
		if(result.isUpdateOfExisting()){
			return Constant.COM_RESP;
		}
		return Constant.COM_FAIL_RESP;
	}
	
	@ApiOperation("查询关注列表")
	@GetMapping("/cares-search")
	public String getCares(String waiterId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<String> list = null;
		if(waiterId != null && waiterId!=""){
			list = getList(waiterId,"cares");
		}else{
			return "参数错误";
		}
		
		List<WaiterBo> cares = new ArrayList<>();
		if(list!=null){
			for (String caresId : list) {
				WaiterBo care = marriageService.findWaiterById(caresId);
				cares.add(care);
			}
		}
		
		String jsonString = JSON.toJSONString(cares);
		return jsonString;
	}
	
	@ApiOperation("取消发布")
	@DeleteMapping("/publish-delete")
	public String deletePublish(String waiterId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		WriteResult result = marriageService.deletePublish(waiterId);
		if(result.isUpdateOfExisting()){
			return Constant.COM_RESP;
		}
		return Constant.COM_FAIL_RESP;
	}	
	
	
	@ApiOperation("修改基础资料")
	@PostMapping("/waiter-update")
	public String updateWaiter(@RequestBody @ApiParam(name = "wv", value = "封装前端请求参数的实体", required = true)WaiterVo wv,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (wv == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		WriteResult result = update(wv,WaiterBo.class);
		if(result.isUpdateOfExisting()){
			return Constant.COM_RESP;
		}
		return Constant.COM_FAIL_RESP;
	}
	
	@ApiOperation("修改要求")
	@PostMapping("/require-update")
	public String updateRequire(@RequestBody @ApiParam(name = "wv", value = "封装前端请求参数的实体", required = true)RequireVo rv,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (rv == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		WriteResult result = update(rv,RequireBo.class);
		if(result.isUpdateOfExisting()){
			return Constant.COM_RESP;
		}
		return Constant.COM_FAIL_RESP;
	}
	
	@ApiOperation("查询发布")
	@GetMapping("/publishes-search")
	public String getPublishById(HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String userId = userBo.getId();
		List<WaiterBo> list = marriageService.getPublishById(userId);
		String jsonString = JSON.toJSONString(list);
		return jsonString;
	}
	
	@ApiOperation("查询选项")
	@PostMapping("/options-search")
	public String getOptions(
			@RequestBody @ApiParam(name = "optionVo", value = "封装前端请求参数的实体", required = true) OptionVo ov,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<OptionBo> options = marriageService.getOptions(ov);
		if(options ==null){
			return "无对应选项";
		}
		String jsonString = JSON.toJSONString(options);
		return jsonString;
	}


	
    @ApiOperation("发布信息")
    @PostMapping("/insert")
    public String insertPublish(@RequestParam String wv,@RequestParam String rv,HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);

        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        
        WaiterVo waiterVo = null;
        RequireVo requireVo = null;
        try {
        	JSONObject fromObject = JSONObject.fromObject(wv);
        	waiterVo = (WaiterVo) JSONObject.toBean(fromObject, WaiterVo.class);
        	fromObject = JSONObject.fromObject(rv);
        	requireVo = (RequireVo) JSONObject.toBean(fromObject, RequireVo.class);
        } catch (Exception e) {
            return e.toString();
        }
        
        
        if (requireVo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        if(waiterVo.isAgree()== false){
        	return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
         
        // 设置基本资料的实体
        WaiterBo wb = new WaiterBo();
        BeanUtils.copyProperties(waiterVo,wb);
        wb.setAge(CommonUtil.getAge(waiterVo.getBirthday()));
        wb.setCreateuid(userBo.getId());
        wb.setUpdateTime(new Date());
        wb.setDeleted(0);
        
        // 设置图片地址
        List<String> image = waiterVo.getImages();
           
        // 设置兴趣
        List<String> hobbys = waiterVo.getHobbys();

        // 设置关心的人,初始为空
        List<String> cares = new ArrayList<>();
        
        // 设置不再推荐的人,初始为空
        List<String> pass = new ArrayList<>();
                
        String waiterId = marriageService.insertPublish(wb);
        
        // 设置要求的实体参数
        RequireBo rb = new RequireBo();
        BeanUtils.copyProperties(requireVo, rb);
        rb.setSex(1-waiterVo.getSex());
        rb.setWaiterId(waiterId);
        // 插入需求,并返回需求id
        marriageService.insertPublish(rb);
        
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("showid", wb.getId());
        return JSONObject.fromObject(map).toString();
    }
    
    
    private WriteResult update(BaseVo obj,Class clazz){
		JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);
		Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
		Map<String, Object> params = new LinkedHashMap<>();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			if (entry.getValue() != null) {
				params.put(entry.getKey(), entry.getValue());
			}
		}
		WriteResult result  = null;
		if(params.size()>0){
			result = marriageService.updateByParams(obj.getId(), params,clazz);
		}
		return result;
    }
    
    private List<String> getList(String waiterId,String key){
    	List<String> list = new ArrayList<String>();
    	list = marriageService.getCaresList(waiterId,key);    	
    	return list;
    }
    
    
}
