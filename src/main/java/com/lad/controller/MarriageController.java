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
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

@Api("儿媳/女婿接口")
@RestController
@RequestMapping("marriage")
public class MarriageController extends BaseContorller{

	@Autowired
	public IMarriageService marriageService;
	
	@ApiOperation("移除关注")
	@PutMapping("/unrecommend")
	public String addUnRecommend(String waiterId,String unRecommendId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<String> list = null;
		if(waiterId != null && waiterId!=""){
			list = getCaresList(waiterId);
		}else{
			return "参数错误";
		}
		WaiterBo care = marriageService.findWaiterById(unRecommendId);
		
		if(care != null){
			list.add(unRecommendId);
		}
		
		Map<String, Object> params = new HashMap<>();
		params.put("cares", list);
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
			list = getCaresList(waiterId);
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
			list = getCaresList(waiterId);
		}else{
			return "参数错误";
		}
		WaiterBo care = marriageService.findWaiterById(careId);
		if(care != null&&list.contains(careId)){
			list.remove(careId);
		}
		System.out.println(list);
		
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
			list = getCaresList(waiterId);
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
    public String insertPublish(WaiterVo wv,RequireVo rv,HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo = getUserLogin(request);
        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        
        if (rv == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        
        // 设置要求的实体参数
        RequireBo rb = new RequireBo();
        BeanUtils.copyProperties(rv, rb);
        rb.setSex(1-wv.getSex());
        
        
        // 设置基本资料的实体
        WaiterBo wb = new WaiterBo();
        BeanUtils.copyProperties(wv,wb);
        wb.setAge(CommonUtil.getAge(wv.getBirthday()));
        wb.setCreateuid(userBo.getId());
        wb.setUpdateTime(new Date());
        
        // 设置图片地址
        List<String> image = new ArrayList<>();
        wb.setImages(image);
        
        // 设置兴趣
        List<String> hobbys = new ArrayList<>();
        wb.setHobbys(hobbys);

        // 设置关心的人,初始为空
        List<String> cares = new ArrayList<>();
        wb.setCares(cares);
        
        // 设置不关心的人,初始为空
        List<String> pass = new ArrayList<>();
        wb.setPass(pass);
        
        
        String wbid = marriageService.insertPublish(wb, rb);
        /*String userid = userBo.getId();
        ShowBo showBo = null;
        try {
            JSONObject jsonObject = JSONObject.fromObject(showVoJson);
            showBo = (ShowBo)JSONObject.toBean(jsonObject, ShowBo.class);
        } catch (Exception e) {
            return CommonUtil.toErrorResult(ERRORCODE.FORMAT_ERROR.getIndex(),
                    ERRORCODE.FORMAT_ERROR.getReason());
        }
        showBo.setCreateuid(userid);
        if (images != null && images.length > 0) {
            if (showBo.getType() == ShowBo.NEED) {
                MultipartFile file = images[0];
                Long time = Calendar.getInstance().getTimeInMillis();
                String fileName = String.format("%s-%d-%s", userid, time, file.getOriginalFilename());
                String path = CommonUtil.upload(file, Constant.RELEASE_PICTURE_PATH, fileName, 0);
                showBo.setComPic(path);
            } else if (showBo.getType() == ShowBo.PROVIDE) {
                LinkedHashSet<String> photos = new LinkedHashSet<>();
                for (MultipartFile file : images) {
                    Long time = Calendar.getInstance().getTimeInMillis();
                    String fileName = String.format("%s-%d-%s", userid, time, file.getOriginalFilename());
                    String path = CommonUtil.upload(file, Constant.RELEASE_PICTURE_PATH, fileName, 0);
                    photos.add(path);
                }
                showBo.setPicType("pic");
                showBo.setImages(photos);
            }
            log.info("shows {} add pic   size: {} ",userid, images.length);
        }
        if (video != null) {
            Long time = Calendar.getInstance().getTimeInMillis();
            String fileName = String.format("%s-%d-%s", userid, time, video.getOriginalFilename());
            String[] paths = CommonUtil.uploadVedio(video, Constant.RELEASE_PICTURE_PATH, fileName, 0);
            showBo.setVideo(paths[0]);
            showBo.setVideoPic(paths[1]);
            showBo.setPicType("video");
            log.info("user {} shows add video path: {},  videoPic: {} ", userid, paths[0], paths[1]);
        }
        showService.insert(showBo);
        asyncController.addShowTypes(showService, showBo.getShowType(), userid);
        if (showBo.getType() == ShowBo.NEED) {
            asyncController.pushShowToCreate(showService, showBo);
        } else {
            asyncController.pushShowToCompany(showService, showBo.getShowType(), showBo.getId(),
                    userBo.getUserName(), userid);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("showid", showBo.getId());
        return JSONObject.fromObject(map).toString();*/
        return null;
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
    
    private List<String> getCaresList(String waiterId){
    	List<String> list = new ArrayList();
    	list = marriageService.getCaresList(waiterId);    	
    	return list;
    }
    
}
