package com.lad.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
@SuppressWarnings("all")
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
		List<WaiterBo> list = marriageService.getNewPublish(type,page,limit,userBo.getId());
		if(list.size()==0){
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_NEWPUBLISH_NULL.getIndex(), ERRORCODE.MARRIAGE_NEWPUBLISH_NULL.getReason());
		}
		Map map = new  HashMap<>();
		map.put("ret", 0);
		map.put("result", list);
		return JSONObject.fromObject(map).toString();
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
		// 过滤字段
		/*for (Map map : list) {
			WaiterBo object = (WaiterBo)map.get("waiter");
			String[] params = {"createTime","deleted","waiterId","updateTime","updateuid","createuid","cares"};
			map.put("waiter", CommonUtil.fieldFilter(object, false, params));
			System.out.println(CommonUtil.fieldFilter(object, false, params));
		}*/
		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("result", list);
		return JSONObject.fromObject(map).toString();
	}
	
	

	
	@ApiOperation("查询发布详情")
	@GetMapping("/publishe-desc-search")
	public String getPublishDescById(String waiterId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		Map<String,Object> map = new HashMap<>();
		WaiterBo waiter = marriageService.findWaiterById(waiterId);
		if(waiter == null){
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_PUBLISH_NULL.getIndex(),ERRORCODE.MARRIAGE_PUBLISH_NULL.getReason());
		}
		// 过滤字段
//		String[] params = {"createTime","createuid","updateTime","updateuid","age","deleted","cares","pass"};
//		map.put("waiter",CommonUtil.fastJsonfieldFilter(waiter, false, params));

		map.put("waiter", waiter);
		RequireBo require = marriageService.findRequireById(waiterId);

		if(require != null){
			// 过滤字段
//			String[] params2 = {"createTime","deleted","waiterId","updateTime","updateuid","createuid"};
//			map.put("require", CommonUtil.fastJsonfieldFilter(require, false, params2));
			map.put("require", require);
		}else{
			map.put("require", ERRORCODE.MARRIAGE_QUIRE_NULL.getReason());
		}
		map.put("ret", 0);
		return JSON.toJSONString(map);
	}
	
	@ApiOperation("不再推荐")
	@PostMapping("/pass")
	public String addPass(String waiterId,String passId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		// 获取当前用户的黑名单列表
		List<String> list = null;
		if(waiterId != null && waiterId!=""){
			list = getPassList(waiterId);
		}else{
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_PUBLISH_NULL.getIndex(),ERRORCODE.MARRIAGE_PUBLISH_NULL.getReason());
		}
		if(list == null){
			list =new ArrayList<String>();
			list.add(passId);
		}
		
		if(list!=null&&!(list.contains(passId))){
			// 将用户添加到黑名单
			list.add(passId);
		}
		
		// 更新数据库
		Map<String, Object> params = new HashMap<>();
		params.put("pass", list);
		WriteResult result = marriageService.updateByParams(waiterId, params , WaiterBo.class);
		
		if(result.isUpdateOfExisting()){
			return Constant.COM_RESP;
		}
		return Constant.COM_FAIL_RESP;
	}
	
	@ApiOperation("添加关注")
	@PostMapping("/care-insert")
	public String addCare(String waiterId,String careId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		Map<String,List> map = marriageService.getCareMap(waiterId);
		if(map==null){
			map = new HashMap<String,List>();
			
		}
		// 设置时间
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String time = format.format(date);
		
		// 如果原map中包含今天的关注,则添加到今天的关注
		List<String> list = new ArrayList<>();
		for (String key : map.keySet()) {
			if(time.equals(key)){
				list=map.get(key);
				if(list.contains(careId)){
					return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_HAS_CARE.getIndex(), ERRORCODE.MARRIAGE_HAS_CARE.getReason());
				}

				list.add(careId);
				WriteResult updateCare = marriageService.updateCare(waiterId,map);
				
				if(updateCare.isUpdateOfExisting()){
					return Constant.COM_RESP;
				}
				return Constant.COM_FAIL_RESP;	
			}
		}
		
		list.add(careId);
		map.put(time, list);
		WriteResult updateCare = marriageService.updateCare(waiterId,map);
		if(updateCare.isUpdateOfExisting()){
			return Constant.COM_RESP;
		}
		return Constant.COM_FAIL_RESP;
	}
	
	@ApiOperation("移除关注")
	@PostMapping("/care-delete")
	public String deleteCare(String waiterId,String careId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		Map<String,List> map = marriageService.getCareMap(waiterId);
		
		for (Entry<String, List> entity : map.entrySet()) {
			List list = entity.getValue();
			if(list.contains(careId)){
				list.remove(careId);	
				if(list.size()==0){
					map.remove(entity.getKey());
				}
				break;
			}
		}
		WriteResult updateCare = marriageService.updateCare(waiterId,map);
		if(updateCare.isUpdateOfExisting()){
			return Constant.COM_RESP;
		}
		return Constant.COM_FAIL_RESP;	
		/*List<String> list = new ArrayList<String>();
		if(waiterId != null && waiterId!=""){
			list = getList(waiterId,"cares");
		}else{
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_PUBLISH_NULL.getIndex(),ERRORCODE.MARRIAGE_PUBLISH_NULL.getReason());
		}
		
		if(list==null){
			list = new ArrayList<>();
		}
		
		if(list.contains(careId)){
			list.remove(careId);
		}
		
		
		
		Map<String, Object> params = new HashMap<>();
		params.put("cares", list);
		WriteResult result = marriageService.updateByParams(waiterId, params , WaiterBo.class);
		
		if(result.isUpdateOfExisting()){
			return Constant.COM_RESP;
		}*/
		
	}
	
	@ApiOperation("查询关注列表")
	@GetMapping("/care-search")
	public String getCares(String waiterId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		Map map = new HashMap<>();
		Map<String, List> careMap = marriageService.getCareMap(waiterId);
		
		for (String key : careMap.keySet()) {
			List list = careMap.get(key);
			List list2 = new ArrayList<>(); 
			for (Object Object : list) {
				WaiterBo waiter = marriageService.findWaiterById(Object.toString());
				String[] params2 = {"createTime","deleted","waiterId","updateTime","updateuid","createuid","pass"};
				String jsonfieldFilter = CommonUtil.fastJsonfieldFilter(waiter, false, params2);
				list2.add(jsonfieldFilter);
			}
			map.put(key, list2);
		}
		
		/*List<String> list = null;
		if(waiterId != null && waiterId!=""){
			list = getList(waiterId,"cares");
		}else{
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_PUBLISH_NULL.getIndex(),ERRORCODE.MARRIAGE_PUBLISH_NULL.getReason());
		}
		
		
		
		List cares = new ArrayList<>();
		if(list!=null){
			for (String caresId : list) {
				WaiterBo care = marriageService.findWaiterById(caresId);
				String[] params2 = {"createTime","deleted","waiterId","updateTime","updateuid","createuid"};
				cares.add(CommonUtil.fastJsonfieldFilter(care, false, params2));
			}
		}*/
		
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString().replace("\\", "");
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
	public String updateWaiter(@RequestParam String wv,String id,HttpServletRequest request, HttpServletResponse response){

		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (wv == null) {
//			基础资料错误
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_WAITER_NULL.getIndex(),ERRORCODE.MARRIAGE_WAITER_NULL.getReason());
		}
 
		
		WriteResult result = update(wv,id,WaiterBo.class);
		if(result.isUpdateOfExisting()){
			return Constant.COM_RESP;
		}
		return Constant.COM_FAIL_RESP;
	}
	
	@ApiOperation("修改要求")
	@PostMapping("/require-update")
	public String updateRequire(@RequestParam String rv,String id,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (rv == null) {
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_REQUIRE_NULL.getIndex(),ERRORCODE.MARRIAGE_REQUIRE_NULL.getReason());
		}
		
        /*RequireVo requireVo = null;
        try {
        	JSONObject fromObject = JSONObject.fromObject(rv);
        	requireVo = (RequireVo) JSONObject.toBean(fromObject, RequireVo.class);
        } catch (Exception e) {
            return e.toString();
        }*/
		
		WriteResult result = update(rv,id,RequireBo.class);
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
		
		List<String> result = new ArrayList<>();
		for (WaiterBo waiterBo : list) {
			String[] params = {"createTime","deleted","waiterId","updateTime","updateuid","createuid","cares"};
			result.add(CommonUtil.fastJsonfieldFilter(waiterBo, false, params));
		}
		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("publishes", result);
		return JSONObject.fromObject(map).toString();
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
        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        	waiterVo.setBirthday(format.parse(fromObject.get("birthday").toString()));
        	fromObject = JSONObject.fromObject(rv);
        	requireVo = (RequireVo) JSONObject.toBean(fromObject, RequireVo.class);
        } catch (Exception e) {
        	e.printStackTrace();
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
        if(image == null ){
        	List<String> list  = new  ArrayList<>();
        	wb.setImages(list);
        }else{
        	 wb.setImages(image);
        }
       
           
        // 设置兴趣
        List<String> hobbys = waiterVo.getHobbys();
        if(hobbys == null ){
        	List<String> list  = new  ArrayList<>();
        	wb.setHobbys(list);
        }else{
        	wb.setHobbys(hobbys);
        }        
        
        // 设置关心的人,初始为空
        Map<String,List> cares = new HashMap<String,List>();    
        wb.setCares(cares);
        // 设置不再推荐的人,初始为空
        List<String> pass = new ArrayList<>();
        wb.setPass(pass);
        
       
        String waiterId = marriageService.insertPublish(wb);
        
        // 设置要求的实体参数
        RequireBo rb = new RequireBo();
        BeanUtils.copyProperties(requireVo, rb);
        
        
        List<String> requirehobbys = requireVo.getHobbys();
        if(requirehobbys == null ){
        	List<String> list  = new  ArrayList<>();
        	rb.setHobbys(list);
        }else{
        	rb.setHobbys(requirehobbys);
        }
        
        List<String> jobs = requireVo.getJob();
        if(jobs == null ){
        	List<String> list  = new  ArrayList<>();
        	rb.setJob(list);
        }else{
        	rb.setJob(jobs);
        }
        
        rb.setSex(1-waiterVo.getSex());
        rb.setWaiterId(waiterId);
        // 插入需求,并返回需求id
        marriageService.insertPublish(rb);
        
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("showid", wb.getId());
        return JSONObject.fromObject(map).toString();
    }
    
    	
    private WriteResult update(String obj,String id,Class clazz){	
    	JSONObject fromObject = JSONObject.fromObject(obj);

    	Iterator<Map.Entry<String, Object>> iterator = fromObject.entrySet().iterator();
		Map<String, Object> params = new LinkedHashMap<>();
		

		String[] split = clazz.toString().split("\\.");

		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			
			
			if (entry.getValue() != null && !("birthday".equals(entry.getValue()))) {
				params.put(entry.getKey(), entry.getValue());
			}
		}

		// 处理时间
		if("WaiterBo".equals(split[split.length-1])){
			String birthdayStr = fromObject.getString("birthday");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if(birthdayStr!=null){
				try {
					Date parse = format.parse(birthdayStr);
					params.put("birthday", parse);
					params.put("age", CommonUtil.getAge(parse));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		

		
		
		WriteResult result  = null;
		if(params.size()>0){
			result = marriageService.updateByParams(id, params,clazz);
		}
		return result;
    	
    }
    
    private List<String> getPassList(String waiterId){
    	List<String> list = new ArrayList<String>();
    	list = marriageService.getPassList(waiterId);    	
    	return list;
    }
    
    
}
