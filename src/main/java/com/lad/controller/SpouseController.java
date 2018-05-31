package com.lad.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.lad.bo.RequireBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.lad.bo.UserBo;
import com.lad.bo.WaiterBo;
import com.lad.service.SpouseService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.RequireVo;
import com.lad.vo.SpouseBaseVo;
import com.lad.vo.SpouseRequireVo;
import com.lad.vo.WaiterVo;
import com.mongodb.WriteResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;

@Api("儿媳/女婿接口")
@RestController
@RequestMapping("spouse")
@SuppressWarnings("all")
public class SpouseController  extends BaseContorller{
	@Autowired
	private SpouseService spouseService;
	
	@ApiOperation("查询关注列表")
	@GetMapping("/care-search")
	public String getCares(String spouseId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		Map map = new HashMap<>();
		Map<String, List> careMap = spouseService.getCareMap(spouseId);
		
		for (String key : careMap.keySet()) {
			List list = careMap.get(key);
			List list2 = new ArrayList<>(); 
			for (Object Object : list) {
				SpouseBaseBo baseBo = spouseService.findBaseById(Object.toString());
				String[] params2 = {"createTime","deleted","waiterId","updateTime","updateuid","createuid","pass"};
				String jsonfieldFilter = CommonUtil.fastJsonfieldFilter(baseBo, false, params2);
				list2.add(jsonfieldFilter);
			}
			map.put(key, list2);
		}		
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString().replace("\\", "");
	}
	
	@ApiOperation("移除关注")
	@PostMapping("/care-delete")
	public String deleteCare(String spouseId,String careId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		Map<String,List> map = spouseService.getCareMap(spouseId);
		
		if(map==null){
			map = new HashMap<String,List>();
			
		}
		
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
		WriteResult updateCare = spouseService.updateCare(spouseId,map);
		return Constant.COM_RESP;			
	}

	
	@ApiOperation("添加关注")
	@PostMapping("/care-insert")
	public String addCare(String spouseId,String careId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		Map<String,List> map = spouseService.getCareMap(spouseId);
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
				spouseService.updateCare(spouseId,map);
				

				return Constant.COM_RESP;	
			}
		}
		
		list.add(careId);
		map.put(time, list);
		spouseService.updateCare(spouseId,map);

		return Constant.COM_RESP;
	}
	
	@ApiOperation("查看详情")
	@GetMapping("/desc-search")
	public String getDescById(String baseId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		Map<String,Object> map = new HashMap<>();
		SpouseBaseBo baseBo = spouseService.findBaseById(baseId);
		if(baseBo == null){
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_PUBLISH_NULL.getIndex(),ERRORCODE.MARRIAGE_PUBLISH_NULL.getReason());
		}

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		baseBo.setBirthday(format.format(baseBo.getBirthday()));
		map.put("baseDate", baseBo);
		SpouseRequireBo requireBo = spouseService.findRequireById(baseId);

		if(requireBo != null){
			map.put("require", requireBo);
		}else{
			map.put("require", ERRORCODE.MARRIAGE_QUIRE_NULL.getReason());
		}
		map.put("ret", 0);
		return JSON.toJSONString(map);
	}
	
	
	@ApiOperation("发布信息")
	@PostMapping("/insert")
	public String insertPublish(@RequestParam String baseDate, @RequestParam String requireDate, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);

        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        
        SpouseBaseVo baseVo = null;
        SpouseRequireVo requireVo = null;
        try {
        	JSONObject fromObject = JSONObject.fromObject(baseDate);
        	baseVo = (SpouseBaseVo) JSONObject.toBean(fromObject, SpouseBaseVo.class);
        	fromObject = JSONObject.fromObject(requireDate);
        	requireVo = (SpouseRequireVo) JSONObject.toBean(fromObject, SpouseRequireVo.class);
        } catch (Exception e) {
        	e.printStackTrace();
            return e.toString();
        }
                 
        // 设置基本资料的实体
        SpouseBaseBo baseBo = new SpouseBaseBo();
        
        BeanUtils.copyProperties(baseVo,baseBo);
        
        // 设置创建者 
        baseBo.setCreateuid(userBo.getId());
        // 初始化更新时间
        baseBo.setUpdateTime(new Date());
        
        // 设置昵称
        
        baseBo.setNickName(userBo.getUserName());
        
        // 初始化年龄
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date age = null;
		try {
			age = format.parse(baseVo.getBirthday());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		baseBo.setBirthday(age);
        baseBo.setAge(CommonUtil.getAge(age));
        
        List<String> list = new ArrayList();
        // 设置照片
        if(baseBo.getImages() == null){
        	baseBo.setImages(list);
        }
        // 设置兴趣
        if(baseBo.getHobbys() == null){
        	baseBo.setHobbys(list);
        }
        // 设置关注
       Map map = new HashMap<String,List>();
        baseBo.setCare(map);
        // 设置黑名单
        baseBo.setPass(list);
        
       
        String baseId = spouseService.insert(baseBo);
        
        // 设置要求的实体参数
        SpouseRequireBo requireBo = new SpouseRequireBo();
        BeanUtils.copyProperties(requireVo, requireBo);
        
        // 设置性别
        requireBo.setSex(1-baseBo.getSex());
        
        // 设置兴趣
        if(requireBo.getHobbys()==null){
        	requireBo.setHobbys(list);
        }
        
        // 设置baseId
        requireBo.setBaseId(baseId);
        
        // 插入需求,并返回需求id
        spouseService.insert(requireBo);
        
        Map<String, Object> map2 = new HashMap<>();
        map2.put("ret", 0);
        map2.put("showid", baseBo.getId());
        return JSONObject.fromObject(map2).toString();
	}
	
	 
	@GetMapping("/test")
	public void test(){
		spouseService.test();
	}
}
