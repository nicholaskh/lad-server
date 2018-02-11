package com.lad.controller;

import com.lad.bo.*;
import com.lad.scrapybo.BroadcastBo;
import com.lad.scrapybo.InforBo;
import com.lad.scrapybo.SecurityBo;
import com.lad.scrapybo.VideoBo;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.MyException;
import com.lad.vo.CollectVo;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/collect")
public class CollectController extends BaseContorller {
	
	@Autowired
	private ICollectService collectService;
	@Autowired
	private IUserService userService;

	@Autowired
	private ICircleService circleService;

	@Autowired
	private IPartyService partyService;

	@Autowired
	private IInforService inforService;
	
	@RequestMapping("/chat")
	@ResponseBody
	public String chat(String title, String content, String userid,
			HttpServletRequest request, HttpServletResponse response){

		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo user = userService.getUser(userid);
		if (user == null) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_NULL.getIndex(),
					ERRORCODE.USER_NULL.getReason());
		}
		CollectBo chatBo = new CollectBo();
		chatBo.setCreateuid(userBo.getId());
		chatBo.setUserid(userBo.getId());
		chatBo.setContent(content);
		chatBo.setTitle(title);
		chatBo.setType(Constant.CHAT_TYPE);
		chatBo.setSourceid(userid);
		chatBo.setTargetPic(user.getHeadPictureName());
		chatBo.setSource(user.getUserName());
		chatBo = collectService.insert(chatBo);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("col-time", CommonUtil.time2str(chatBo.getCreateTime()));
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/my-chats")
	@ResponseBody
	public String myChats(@RequestParam(required = false) String start_id, @RequestParam int limit,
					   HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<CollectBo> collectBos = collectService.findChatByUserid(userBo.getId(),
				start_id, limit, Constant.CHAT_TYPE);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("col-chats", collectBos);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/add-tag")
	@ResponseBody
	public String addTag(String name,
						  HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserTagBo userTagBo = new UserTagBo();
		userTagBo.setUserid(userBo.getId());
		userTagBo.setTagName(name);
		userTagBo.setTagType(0);
		collectService.insertTag(userTagBo);
		return  Constant.COM_RESP;
	}

	@RequestMapping("/my-tags")
	@ResponseBody
	public String myTag(HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<UserTagBo> tagBos = collectService.findTagByUserid(userBo.getId(), 0);
		List<String> tagNames = new ArrayList<>();
		for (UserTagBo tagBo : tagBos) {
			tagNames.add(tagBo.getTagName());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("tags", tagNames);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/my-collects")
	@ResponseBody
	public String myCols(int page, int limit,
						 HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<CollectBo> collectBos = collectService.findAllByUserid(userBo.getId(), page, limit);
		List<CollectVo> collectVos = new LinkedList<>();
		bo2vos(collectBos, collectVos);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("collectVos", collectVos);
		return JSONObject.fromObject(map).toString();
	}


	@RequestMapping("/col-files")
	@ResponseBody
	public String colFile(String path, int fileType, String videoPic, String userid, HttpServletRequest request,
						  HttpServletResponse
			response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CollectBo chatBo = new CollectBo();
		chatBo.setCreateuid(userBo.getId());
		chatBo.setUserid(userBo.getId());
		if (fileType == Constant.COLLET_VIDEO) {
			chatBo.setTargetPic(videoPic);
			chatBo.setVideo(path);
		}
		chatBo.setSourceid(userid);
		chatBo.setPath(path);
		chatBo.setType(fileType);
		if (fileType ==  Constant.COLLET_URL) {
			chatBo.setSub_type(Constant.FILE_TYPE);
		}
		collectService.insert(chatBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("col-time", CommonUtil.time2str(chatBo.getCreateTime()));
		return JSONObject.fromObject(map).toString();
	}



	@RequestMapping("/by-tagName")
	@ResponseBody
	public String findByTag(String tagName, int page, int limit, HttpServletRequest request, HttpServletResponse
			response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		List<CollectBo> collectBos = collectService.findByTag(tagName, userBo.getId(), page, limit);
		List<CollectVo> collectVos = new LinkedList<>();
		bo2vos(collectBos, collectVos);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("collectVos", collectVos);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/by-type")
	@ResponseBody
	public String findByTag(int type, int page, int limit, HttpServletRequest request, HttpServletResponse
			response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<CollectBo> collectBos = collectService.findByUseridAndType(userBo.getId(), type, page, limit);
		List<CollectVo> collectVos = new LinkedList<>();
		bo2vos(collectBos, collectVos);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("collectVos", collectVos);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 给收藏添加分类
	 * @param tags
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/add-col-tag")
	@ResponseBody
	public String addCollectTag(String tags, String collectid,
								HttpServletRequest request, HttpServletResponse response){
		CollectBo collectBo = collectService.findById(collectid);
		if (collectBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.COLLECT_IS_NULL.getIndex(),
					ERRORCODE.COLLECT_IS_NULL.getReason());
		}
		String[] tagArr = tags.split(",");
		LinkedHashSet<String> userTags =  collectBo.getUserTags();
		for (String tag: tagArr) {
			userTags.add(tag);
		}
		collectService.updateTags(collectid, userTags);
		return  Constant.COM_RESP;
	}

	/**
	 * 给收藏添加分类
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/del-collect")
	@ResponseBody
	public String delCollect(String collectids,
								HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String[] idArr = CommonUtil.getIds(collectids);
		if (idArr.length == 1) {
			collectService.delete(idArr[0]);
		} else {
			List<String> ids = new ArrayList<>();
			for (String id : idArr) {
				ids.add(id);
			}
			collectService.delete(ids);
		}
		return Constant.COM_RESP;
	}

	private void bo2vos(List<CollectBo> collectBos, List<CollectVo> collectVos){
		for (CollectBo collectBo : collectBos) {
			CollectVo vo = new CollectVo();
			BeanUtils.copyProperties(collectBo, vo);
			vo.setCollectid(collectBo.getId());
			vo.setCollectTime(collectBo.getCreateTime());
			vo.setCollectPic(collectBo.getTargetPic());
			int type =  collectBo.getType();
			if (type == Constant.CHAT_TYPE || (Constant.COLLET_PIC <= type && type <= Constant.COLLET_VOICE)) {
				UserBo userBo = userService.getUser(collectBo.getSourceid());
				vo.setCollectUserid(collectBo.getSourceid());
				if (userBo != null) {
					vo.setCollectUserName(userBo.getUserName());
					vo.setCollectUserPic(userBo.getHeadPictureName());
				}
			} else if (collectBo.getType() == Constant.COLLET_URL) {
				String id = collectBo.getTargetid();
				if (collectBo.getSub_type() == Constant.CIRCLE_TYPE) {
					CircleBo circleBo = circleService.selectById(id);
					if (circleBo != null) {
						vo.setCollectPic(circleBo.getHeadPicture());
					}
				} else if (collectBo.getSub_type() == Constant.PARTY_TYPE) {
					PartyBo partyBo = partyService.findById(id);
					if (partyBo != null) {
						vo.setCollectPic(partyBo.getBackPic());
					}
				}  else if (collectBo.getSub_type() == Constant.INFOR_TYPE) {
					vo.setCollectPic(collectBo.getTargetPic());
					int inforType = collectBo.getSourceType();
					String inforid = collectBo.getTargetid();
					vo.setSourceType(inforType);
					switch (inforType){
						case Constant.INFOR_HEALTH:
							InforBo inforBo = inforService.findById(inforid);
							if (inforBo != null) {
								vo.setModule(inforBo.getModule());
								vo.setClassName(inforBo.getClassName());
								vo.setTitle(inforBo.getTitle());
							}
							break;
						case Constant.INFOR_SECRITY:
							SecurityBo securityBo = inforService.findSecurityById(inforid);
							if (securityBo != null) {
								vo.setModule(securityBo.getNewsType());
								vo.setTitle(securityBo.getTitle());
							}
							break;
						case Constant.INFOR_RADIO:
							BroadcastBo broadcastBo = inforService.findBroadById(inforid);
							if (broadcastBo != null) {
								vo.setModule(broadcastBo.getModule());
								vo.setClassName(broadcastBo.getClassName());
								vo.setTitle(broadcastBo.getTitle());
							}
							break;
						case Constant.INFOR_VIDEO:
							VideoBo videoBo = inforService.findVideoById(inforid);
							if (videoBo != null) {
								vo.setModule(videoBo.getModule());
								vo.setClassName(videoBo.getClassName());
								vo.setTitle(videoBo.getTitle());
							}
							break;
						default:
							break;
					}
				}
				vo.setVideo(collectBo.getVideo());
			}
			collectVos.add(vo);
		}
	}


}
