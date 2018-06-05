package com.lad.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.lad.bo.CareAndPassBo;
import com.lad.bo.FriendsBo;
import com.lad.bo.RequireBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.lad.bo.TravelersBaseBo;
import com.lad.bo.TravelersRequireBo;
import com.lad.bo.UserBo;
import com.lad.bo.UserTasteBo;
import com.lad.bo.WaiterBo;
import com.lad.service.CareAndPassService;
import com.lad.service.IUserService;
import com.lad.service.SpouseService;
import com.lad.service.TravelersService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.CareResultVo;
import com.lad.vo.SpouseBaseVo;
import com.lad.vo.SpouseRequireVo;
import com.lad.vo.TravelersBaseVo;
import com.lad.vo.TravelersRequireVo;
import com.mongodb.WriteResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;

@Api("驴友接口")
@RestController
@RequestMapping("travelers")
@SuppressWarnings("all")
public class TravelersController extends BaseContorller {
	@Autowired
	private TravelersService travelersService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private CareAndPassService careAndPassService;
	
	/**
	 * 取消发布
	 * @param spouseId
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping("/delete")
	public String deletePublish(String requireId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		WriteResult result = travelersService.deletePublish(requireId);

		return Constant.COM_RESP;
	}
	
	@ApiOperation("修改基础资料")
	@PostMapping("/require-update")
	public String updateTravelers(@RequestParam String requireDate,String requireId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (requireDate == null) {
//			基础资料错误
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_WAITER_NULL.getIndex(),ERRORCODE.MARRIAGE_WAITER_NULL.getReason());
		}
 
		JSONObject fromObject = JSONObject.fromObject(requireDate);
		Set<Map.Entry<String, Object>> entitySet = fromObject.entrySet();
		Iterator<Map.Entry<String, Object>> iterator = entitySet.iterator();
		
		Map<String,Object> params = new HashMap<>();
		
		while(iterator.hasNext()){
			Entry<String, Object> next = iterator.next();
			params.put(next.getKey(), next.getValue());
		}
		
		travelersService.updateByIdAndParams(requireId, params);

		return Constant.COM_RESP;
	}
	
	/**
	 * 获取最新发布的消息
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/new-search")
	public String getNewTravelers(int page,int limit,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		Map map = new  HashMap<>();
		
		// 从后台获取数据
		List<TravelersRequireBo> list = travelersService.getNewTravelers(page,limit,userBo.getId());
		
		// 遍历数据,过滤
		List<String> list2 = new ArrayList<>();
		for (TravelersRequireBo requireBo : list) {
			String[] params2 = {"createTime","deleted","updateTime","updateuid","createuid","sex"};
			list2.add(CommonUtil.fastJsonfieldFilter(requireBo, false, params2));
		}
		map.put("ret", 0);
		map.put("result", list2);
		return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}
	
	
	@ApiOperation("添加黑名单")
	@PostMapping("/addPass")
	public String addPass(String requireId,String passId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		List<String> passRoster = careAndPassService.findTravelersPassList(requireId);

		// 如果存在这个记录
		if(passRoster!=null){
			if(!passRoster.contains(passId)){
				passRoster.add(passId);
				Map<String, List<String>> careMap = careAndPassService.findTravelersCareMap(requireId);
				for (Entry<String, List<String>> entity : careMap.entrySet()) {
					if(entity.getValue().contains(passId)){
						entity.getValue().remove(passId);
						// 多线程情况下有安全隐患
						if(entity.getValue().size()==0){
							careMap.remove(entity.getKey());
						}
						careAndPassService.updateCare(Constant.TRAVELERS, requireId, careMap);
						break;
					}
				}
				careAndPassService.updatePass(Constant.TRAVELERS, requireId, passRoster);
			}
		}else{
			CareAndPassBo care = new CareAndPassBo();
			// 设置主id
			care.setMainId(requireId);
			// 设置关注名单
			Map<String,List<String>> careRoster = new HashMap<>();
			care.setCareRoster(careRoster);
			// 设置黑名单list
			List<String> passList =new ArrayList<String>();
			passList.add(passId);
			care.setPassRoster(passList);
			// 设置创建者id
			care.setCreateuid(userBo.getId());
			// 设置为找驴友情境
			care.setSituation(Constant.TRAVELERS);
			careAndPassService.insert(care);
		}
		Map map = new HashMap<>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	
	// 移除关注
	@ApiOperation("移除关注")
	@PostMapping("/removeCare")
	public String deleteCare(String requireId,String careId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		Map<String,List<String>> careMap = careAndPassService.findTravelersCareMap(requireId);
		
		if(careMap==null){
			careMap = new HashMap<String,List<String>>();
		}
		
		for (Entry<String, List<String>> entrySet : careMap.entrySet()) {
			if(entrySet.getValue().contains(careId)){
				entrySet.getValue().remove(careId);
				if(entrySet.getValue().size()==0){
					careMap.remove(entrySet.getKey());
				}
				careAndPassService.updateCare(Constant.TRAVELERS,requireId,careMap);
				break;
			}
		}

		return Constant.COM_RESP;			
	}
	
	
	@ApiOperation("查看关注列表")
	@GetMapping("/getCare")
	public String getCare(String requireId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		// 在找驴友的逻辑中,主id即requireId
		CareAndPassBo care = careAndPassService.findTravelersCare(requireId);
		
		// 设置壮哉CareResultVo的容器
		List<CareResultVo> resultContainer = new ArrayList<>();
		if(care!=null){
			Map<String, List<String>> roster = care.getCareRoster();
			CareResultVo result = new CareResultVo();
			for (Entry<String, List<String>> entity : roster.entrySet()) {
				result.setAddTime(entity.getKey());
				// 设置list装载遍历出的实体数据
				List<String> requireContainer = new ArrayList<>();
				for (String entityValue : entity.getValue()) {
					// 根据requireI遍历出require
					TravelersRequireBo requireBo = travelersService.getRequireById(entityValue);
					requireBo.setCreateTime(null);
					requireBo.setDeleted(null);
					
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					requireBo.setAssembleTime(format.format(requireBo.getAssembleTime()));
					requireContainer.add(JSON.toJSONString(requireBo));
				}
				result.setString(requireContainer);
				resultContainer.add(result);
			}
		}
		
		
	       
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("require", resultContainer);
        return JSONObject.fromObject(map).toString();
	}
	
	
	
	@ApiOperation("添加关注")
	@PostMapping("/addCare")
	public String addCare(String requireId,String careId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		Map<String, List<String>> careRoster = careAndPassService.findTravelersCareMap(requireId);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String time = format.format(new Date());
		
		// 如果存在这个记录
		if(careRoster!=null){
			// 判断是否已关注该用户
			for (Entry<String, List<String>> entity : careRoster.entrySet()) {
				if(entity.getValue().contains(careId)){
					return "您已关注该用户";
				}
			}
			
			// 如果存在当天的添加记录
			if(careRoster.containsKey(time)){
				careRoster.get(time).add(careId);
			}else{
				List<String> careList =new ArrayList<String>();
				careList.add(careId);
				careRoster.put(time, careList);
			}
			careAndPassService.updateCare(Constant.TRAVELERS, requireId, careRoster);

		}else{
			CareAndPassBo care = new CareAndPassBo();
			// 设置主id
			care.setMainId(requireId);
			// 设置关注名单
			careRoster = new HashMap<>();
			List<String> careList =new ArrayList<String>();
			careList.add(careId);
			careRoster.put(time, careList);
			care.setCareRoster(careRoster);
			// 设置黑名单list
			List<String> passList =new ArrayList<String>();
			care.setPassRoster(passList);
			// 设置创建者id
			care.setCreateuid(userBo.getId());
			// 设置为找驴友情境
			care.setSituation(Constant.TRAVELERS);
			careAndPassService.insert(care);
		}
		Map map = new HashMap<>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	
	
	@ApiOperation("查询发布详情")
	@GetMapping("/desc-search")
	public String getPublishDescById(String requireId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		Map<String,Object> map = new HashMap<>();
		TravelersRequireBo requireBo = travelersService.getRequireById(requireId);
		
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		requireBo.setAssembleTime(format.format(requireBo.getAssembleTime()));
		requireBo.setDeleted(null);
		requireBo.setCreateTime(null);
		map.put("ret", 0);
		map.put("require", JSON.toJSONString(requireBo));
		
		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}
	
	/**
	 * 查询当前账户下的发布信息
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/travelers-search")
	public String getPublishById(HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		List<String> list = new ArrayList<>();
		
		// 用户要求
		List<TravelersRequireBo> requires = travelersService.getRequireList(userBo.getId());

		for (TravelersRequireBo travelersRequireBo : requires) {
			travelersRequireBo.setDeleted(null);
			travelersRequireBo.setCreateTime(null);
			list.add((JSON.toJSONString(travelersRequireBo)));
		}
		
		// 用户兴趣,字段过滤
		UserTasteBo hobbys = userService.findByUserId(userBo.getId());
		if(hobbys!=null){
			hobbys.setId(null);
			hobbys.setUserid(null);
			hobbys.setDeleted(null);
			hobbys.setUpdateTime(null);
			hobbys.setUpdateuid(null);
			hobbys.setCreateTime(null);
		}
		
		
		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("nickName", userBo.getUserName());
		map.put("hobbys", JSON.toJSONString(hobbys));
		map.put("result", list);
		return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}
	
	/**
	 * 添加发布
	 * @param baseDate
	 * @param requireDate
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("发布信息")
	@PostMapping("/insert")
	public String insertPublish(@RequestParam String requireDate, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);

        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        
        TravelersRequireVo requireVo = null;
        try {
        	JSONObject fromObject = JSONObject.fromObject(requireDate);
        	requireVo = (TravelersRequireVo) JSONObject.toBean(fromObject, TravelersRequireVo.class);
        } catch (Exception e) {
        	e.printStackTrace();
            return e.toString();
        }

        /*=================存储要求=======================*/
        // 设置要求的实体参数
        TravelersRequireBo requireBo = new TravelersRequireBo();
        BeanUtils.copyProperties(requireVo, requireBo);
        
        // 设置集合时间
       DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parse = null;
        try {
        	parse = format.parse(requireVo.getAssembleTime());
        	
		} catch (ParseException e) {
			e.printStackTrace();
		}
        requireBo.setAssembleTime(parse);        
        // 插入需求,并返回需求id
        requireBo.setCreateuid(userBo.getId());
        
        travelersService.insert(requireBo);
        
        Map<String, Object> map2 = new HashMap<>();
        map2.put("ret", 0);
        map2.put("requireId", requireBo.getId());
        return JSONObject.fromObject(map2).toString();
	}
	
	@GetMapping("/test")
	public void test(){
		travelersService.test();
	}
}
