package com.lad.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.lad.bo.TravelersBaseBo;
import com.lad.bo.TravelersRequireBo;
import com.lad.bo.UserBo;
import com.lad.service.SpouseService;
import com.lad.service.TravelersService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.lad.vo.SpouseBaseVo;
import com.lad.vo.SpouseRequireVo;
import com.lad.vo.TravelersBaseVo;
import com.lad.vo.TravelersRequireVo;

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
		List<TravelersBaseBo> baseList = travelersService.getTravelersByUserId(userBo.getId());
		
		for (TravelersBaseBo travelersBaseBo : baseList) {
			travelersBaseBo.setCreateTime(null);
			TravelersRequireVo requireVo = travelersService.getRequireByBaseId(travelersBaseBo.getId());
		}
		
		Map map = new HashMap<>();
		map.put("ret", 0);
//		map.put("publishes", spouseBo);
		return JSONObject.fromObject(map).toString();
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
	public String insertPublish(@RequestParam String baseDate, @RequestParam String requireDate, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);

        if (userBo == null) {
            return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
                    ERRORCODE.ACCOUNT_OFF_LINE.getReason());
        }
        
        TravelersBaseVo baseVo = null;
        TravelersRequireVo requireVo = null;
        try {
        	JSONObject fromObject = JSONObject.fromObject(baseDate);
        	baseVo = (TravelersBaseVo) JSONObject.toBean(fromObject, TravelersBaseVo.class);
        	fromObject = JSONObject.fromObject(requireDate);
        	requireVo = (TravelersRequireVo) JSONObject.toBean(fromObject, TravelersRequireVo.class);
        } catch (Exception e) {
        	e.printStackTrace();
            return e.toString();
        }
        
        /*=================存储基础资料=======================*/
        // 设置基本资料的实体
        TravelersBaseBo baseBo = new TravelersBaseBo();
        
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
        
        String baseId = travelersService.insert(baseBo);
        
        /*=================存储要求=======================*/
        // 设置要求的实体参数
        TravelersRequireBo requireBo = new TravelersRequireBo();
        BeanUtils.copyProperties(requireVo, requireBo);
        
        // 设置集合时间
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parse = null;
        try {
        	System.out.println(requireVo.getAssembleTime());
        	parse = format.parse(requireVo.getAssembleTime());
        	
		} catch (ParseException e) {
			e.printStackTrace();
		}
        requireBo.setAssembleTime(parse);
        
        // 设置baseId
        requireBo.setBaseId(baseId);
        
        // 插入需求,并返回需求id
        travelersService.insert(requireBo);
        
        Map<String, Object> map2 = new HashMap<>();
        map2.put("ret", 0);
        map2.put("showid", baseBo.getId());
        return JSONObject.fromObject(map2).toString();
	}
	
	@GetMapping("/test")
	public void test(){
		travelersService.test();
	}
}
