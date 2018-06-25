package com.lad.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.parser.Entity;

import org.apache.commons.beanutils.BeanComparator;
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
import com.lad.bo.TravelersRequireBo;
import com.lad.bo.UserBo;
import com.lad.bo.WaiterBo;
import com.lad.service.CareAndPassService;
import com.lad.service.IFriendsService;
import com.lad.service.SpouseService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.CareResultVo;
import com.lad.vo.RequireVo;
import com.lad.vo.ShowResultVo;
import com.lad.vo.SpouseBaseVo;
import com.lad.vo.SpouseRequireVo;
import com.lad.vo.WaiterVo;
import com.mongodb.WriteResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;

@Api("老伴")
@RestController
@RequestMapping("spouse")
@SuppressWarnings("all")
public class SpouseController  extends BaseContorller{
	@Autowired
	private SpouseService spouseService;
	
	@Autowired
	private CareAndPassService careAndPassService;
	
	
	@Autowired
	private IFriendsService friendsService;
	
	
	@GetMapping("/recommend")
	public String recommend(HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		Map map = new HashMap<>();
		// 获取自身的需求的基础id
		SpouseBaseBo spouseBaseBo = spouseService.getSpouseByUserId(userBo.getId());
		if(spouseBaseBo==null){
			map.put("ret", -1);
			map.put("result", "当前账号无发布消息");
			return JSONObject.fromObject(map).toString();
		}
		String baseId = spouseBaseBo.getId();
		// 通过基础id获取需求
		SpouseRequireBo require = spouseService.findRequireById(baseId);
		
		List<Map> recommend = spouseService.getRecommend(require);
		

		
		
		map.put("ret", 0);
		
		Comparator<? super Map> c = new BeanComparator("match").reversed();
		recommend.sort(c);
		
		
		map.put("result", recommend);
		return JSONObject.fromObject(map).toString();
	}
	
	
	
	/**
	 * 
	 * @param keyWord
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/search")
	public String search(String keyWord,int page,int limit,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		SpouseBaseBo baseBo = spouseService.getSpouseByUserId(userBo.getId());

		String sex = null;
		if(baseBo!=null && baseBo.getSex()!=null){
			sex = ("男".equals(baseBo.getSex()))?"女":"男";
		}
		
		List<SpouseBaseBo> list = spouseService.findListByKeyword(keyWord,sex,page,limit,SpouseBaseBo.class);
		

		
		// 遍历数据,过滤
		List<String> list2 = new ArrayList<>();
		for (SpouseBaseBo spouseBaseBo : list) {
			String[] params2 = {"createTime","deleted","waiterId","updateTime","updateuid","createuid","pass","care"};
			list2.add(CommonUtil.fastJsonfieldFilter(spouseBaseBo, false, params2));
		}
		
		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("result", list2);
		return JSONObject.fromObject(map).toString();
	}
	
	/**
	 * 查找当前账号下的发布信息
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/spouse-search")
	public String getPublishById(HttpServletRequest request, HttpServletResponse response){

		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		Map map = new HashMap<>();
		SpouseBaseBo spouseBo = spouseService.getSpouseByUserId(userBo.getId());
		
		if(spouseBo == null){
			map.put("ret", -1);
			map.put("result", "当前账号无发布消息");
			return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
		}
		
		spouseBo.setCreateTime(null);
		spouseBo.setDeleted(null);

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		spouseBo.setBirthday(format.format(spouseBo.getBirthday()));
		map.put("baseDate", JSON.toJSONString(spouseBo));
		
		SpouseRequireBo requireBo = spouseService.findRequireById(spouseBo.getId());
		requireBo.setCreateTime(null);
		requireBo.setCreateuid(null);
		requireBo.setDeleted(null);
		requireBo.setBaseId(null);
		map.put("ret", 0);
		map.put("RequireDate", JSON.toJSONString(requireBo));		
		return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}
	
	
	/**
	 * 修改要求
	 * @param requireDate
	 * @param requireId
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("修改要求")
	@PostMapping("/require-update")
	public String updateRequire(@RequestParam String requireDate,String requireId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (requireDate == null || requireId == null) {
			return CommonUtil.toErrorResult(ERRORCODE.PARAMS_ERROR.getIndex(),ERRORCODE.PARAMS_ERROR.getReason());
		}
		
		updateByIdAndParams(requireDate, requireId,SpouseRequireBo.class);
		

		return Constant.COM_RESP;
	}


	
	/**
	 * 修改基础资料
	 * @param wv
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("修改基础资料")
	@PostMapping("/base-update")
	public String updateSpouse(@RequestParam String baseDate,String spouseId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (baseDate == null) {
//			基础资料错误
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_WAITER_NULL.getIndex(),ERRORCODE.MARRIAGE_WAITER_NULL.getReason());
		}
 
		updateByIdAndParams(baseDate, spouseId,SpouseBaseBo.class);

		return Constant.COM_RESP;
	}
	
	/**
	 * 取消发布
	 * @param spouseId
	 * @param request
	 * @param response
	 * @return
	 */
	@DeleteMapping("/publish-delete")
	public String deletePublish(String spouseId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		
		WriteResult result = spouseService.deletePublish(spouseId);

		return Constant.COM_RESP;
	}
	
	
	
	/**
	 * 获取最新发布的消息
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/new-search")
	public String getNewSpouse(int page,int limit,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		Map map = new  HashMap<>();
		SpouseBaseBo baseBo = spouseService.getSpouseByUserId(userBo.getId());

		String sex = null;
		if(baseBo!=null){
			sex = ("男".equals(baseBo.getSex()))?"女":"男";
		}
		// 从后台获取数据
		List<SpouseBaseBo> list = spouseService.getNewSpouse(sex,page,limit,userBo.getId());
		
		// 遍历数据,过滤
		List<String> list2 = new ArrayList<>();
		for (SpouseBaseBo spouseBaseBo : list) {
			String[] params2 = {"createTime","deleted","waiterId","updateTime","updateuid","createuid","pass","care"};
			list2.add(CommonUtil.fastJsonfieldFilter(spouseBaseBo, false, params2));
		}
		map.put("ret", 0);
		map.put("result", list2);
		return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}
	
	/**
	 * 查看基础信息详细资料
	 * @param spouseId
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/base-search")
	public String getBaseById(HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		SpouseBaseBo baseBo2 = spouseService.getSpouseByUserId(userBo.getId());

		
		Map<String,Object> map = new HashMap<>();
		SpouseBaseBo baseBo = spouseService.findBaseById(baseBo2.getId());
		if(baseBo == null){
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_PUBLISH_NULL.getIndex(),ERRORCODE.MARRIAGE_PUBLISH_NULL.getReason());
		}

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		baseBo.setBirthday(format.format(baseBo.getBirthday()));
		map.put("ret", 0);
		String[] params2 = {"createTime","deleted","waiterId","updateTime","updateuid","createuid","pass"};
		map.put("baseDate", CommonUtil.fastJsonfieldFilter(baseBo, false, params2));
		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}
	
	
	@ApiOperation("不再推荐")
	@PostMapping("/pass")
	public String addPass(String passId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		SpouseBaseBo baseBo = spouseService.getSpouseByUserId(userBo.getId());
		if(baseBo == null){
			Map map = new HashMap<String,String>();
			map.put("ret", -1);
			map.put("result", "当前账号无发布消息");
			return JSONObject.fromObject(map).toString();
		}
		
		List<String> passRoster = careAndPassService.findSpousePassList(baseBo.getId());

		// 如果存在这个记录
		if(passRoster!=null){
			if(!passRoster.contains(passId)){
				passRoster.add(passId);
				Map<String, List<String>> careMap = careAndPassService.findTravelersCareMap(baseBo.getId());
				for (Entry<String, List<String>> entity : careMap.entrySet()) {
					if(entity.getValue().contains(passId)){
						entity.getValue().remove(passId);
						// 多线程情况下有安全隐患
						if(entity.getValue().size()==0){
							careMap.remove(entity.getKey());
						}
						careAndPassService.updateCare(Constant.SPOUSE, baseBo.getId(), careMap);
						break;
					}
				}
				careAndPassService.updatePass(Constant.SPOUSE, baseBo.getId(), passRoster);
			}
		}else{
			CareAndPassBo care = new CareAndPassBo();
			// 设置主id
			care.setMainId(baseBo.getId());
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
			care.setSituation(Constant.SPOUSE);
			careAndPassService.insert(care);
		}
		Map map = new HashMap<>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@ApiOperation("查询关注列表")
	@GetMapping("/care-search")
	public String getCares(HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		SpouseBaseBo baseBo = spouseService.getSpouseByUserId(userBo.getId());
		if(baseBo == null){
			Map map = new HashMap<String,String>();
			map.put("ret", -1);
			map.put("result", "当前账号无发布消息");
			return JSONObject.fromObject(map).toString();
		}
			
		 // 在找老伴的逻辑中,主id为SpouseBaseId
		CareAndPassBo care = careAndPassService.findSpouseCare(baseBo.getId());
		
		
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
					SpouseBaseBo requireBo = spouseService.findBaseById(entityValue);
					requireBo.setCreateTime(null);
					requireBo.setDeleted(null);
					
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					requireBo.setBirthday(format.format(requireBo.getBirthday()));
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
	
	@ApiOperation("移除关注")
	@PostMapping("/care-delete")
	public String deleteCare(String careId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		SpouseBaseBo baseBo = spouseService.getSpouseByUserId(userBo.getId());
		if(baseBo == null){
			Map map = new HashMap<String,String>();
			map.put("ret", -1);
			map.put("result", "当前账号无发布消息");
			return JSONObject.fromObject(map).toString();
		}
		
		Map<String,List<String>> careMap = careAndPassService.findSpouseCareMap(baseBo.getId());
		
		if(careMap==null){
			careMap = new HashMap<String,List<String>>();
		}
		
		for (Entry<String, List<String>> entrySet : careMap.entrySet()) {
			if(entrySet.getValue().contains(careId)){
				entrySet.getValue().remove(careId);
				if(entrySet.getValue().size()==0){
					careMap.remove(entrySet.getKey());
				}
				careAndPassService.updateCare(Constant.SPOUSE,baseBo.getId(),careMap);
				break;
			}
		}

		return Constant.COM_RESP;
	}

	
	@ApiOperation("添加关注")
	@PostMapping("/care-insert")
	public String addCare(String careId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		SpouseBaseBo baseBo = spouseService.getSpouseByUserId(userBo.getId());
		if(baseBo == null){
			Map map = new HashMap<String,String>();
			map.put("ret", -1);
			map.put("result", "当前账号无发布消息");
			return JSONObject.fromObject(map).toString();
		}
		
		CareAndPassBo careAndPassBo = careAndPassService.findSpouseCare(baseBo.getId());
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String time = format.format(new Date());
		
		if(careAndPassBo!=null){
			Map<String, List<String>> careRoster = careAndPassBo.getCareRoster();

			
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
			careAndPassService.updateCare(Constant.SPOUSE, baseBo.getId(), careRoster);

			
		}else{
			CareAndPassBo care = new CareAndPassBo();
			// 设置主id
			care.setMainId(baseBo.getId());
			// 设置关注名单
			Map<String, List<String>> careRoster = new HashMap<>();
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
			care.setSituation(Constant.SPOUSE);
			careAndPassService.insert(care);
		}

		Map map = new HashMap<>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}
	
	@ApiOperation("查看详情")
	@GetMapping("/desc-search")
	public String getDescById(String spouseId,HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		SpouseBaseBo baseBo2 = spouseService.getSpouseByUserId(userBo.getId());
		if(baseBo2 == null){
			Map map = new HashMap<String,String>();
			map.put("ret", -1);
			map.put("result", "当前账号无发布消息");
			return JSONObject.fromObject(map).toString();
		}
		
		
		
		
		
		Map<String,Object> map = new HashMap<>();
		SpouseBaseBo baseBo = spouseService.findBaseById(spouseId);
		if(baseBo == null){
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_PUBLISH_NULL.getIndex(),ERRORCODE.MARRIAGE_PUBLISH_NULL.getReason());
		}

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		baseBo.setBirthday(format.format(baseBo.getBirthday()));
		map.put("baseDate", baseBo);
		SpouseRequireBo requireBo = spouseService.findRequireById(spouseId);

		if(requireBo != null){
			map.put("require", requireBo);
		}else{
			map.put("require", ERRORCODE.MARRIAGE_QUIRE_NULL.getReason());
		}
		
		if(!(baseBo.getCreateuid().equals(userBo.getId()))){
			ShowResultVo result = new ShowResultVo();
			result.setUid(baseBo.getCreateuid());
			FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userBo.getId(),baseBo.getCreateuid());
			if(friendsBo!=null){
				result.setFriend(true);
			}else{
				result.setFriend(false);
			}
			map.put("friend", result);
		}else{
			map.put("friend", "本人");
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
        
		int num = spouseService.getNum(userBo.getId());
		
		if(num>=1){
			return CommonUtil.toErrorResult(ERRORCODE.SPOUSE_NUM_OUTOFLIMIT.getIndex(), ERRORCODE.SPOUSE_NUM_OUTOFLIMIT.getReason());
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
//        baseBo==男 则 取女
//        baseBo==女 则 取男
        
        requireBo.setSex(("男".equals(baseBo.getSex()))?"女":"男");
        
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
	
	/**
	 * 私有方法,根据传入的Map集合参数修改后台数据
	 * @param requireDate
	 * @param requireId
	 * @param clazz
	 * @return
	 */
	private WriteResult updateByIdAndParams(String requireDate, String requireId,Class clazz) {


		Iterator<Map.Entry<String, Object>> iterator = JSONObject.fromObject(requireDate).entrySet().iterator();
		Map params = new HashMap<>();
		while(iterator.hasNext()){
			Map.Entry<String, Object> entry = iterator.next();
			
			params.put(entry.getKey(), entry.getValue());
			if("images".equals(entry.getKey())){
				List<String> images = (List<String>)entry.getValue();
				if(images.size()>4){
					params.put("images", images.subList(0, 4));
				}else {
					params.put("images", images);
				}
				continue;
			}
			
			
			
			if("birthday".equals(entry.getKey())){
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date birth = null;
				try {
					birth = format.parse(entry.getValue().toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				params.put("birthday", birth);
				params.put("age", CommonUtil.getAge(birth));
			}
			
		}
		WriteResult updateByParams = spouseService.updateByParams(requireId, params, clazz);
		return updateByParams;
	}
	 
	@GetMapping("/test")
	public void test(){
		spouseService.test();
	}
}
