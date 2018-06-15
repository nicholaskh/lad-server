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
import com.lad.bo.FriendsBo;
import com.lad.bo.OldFriendRequireBo;
import com.lad.bo.TravelersRequireBo;
import com.lad.bo.UserBo;
import com.lad.bo.UserTasteBo;
import com.lad.service.IFriendsService;
import com.lad.service.IOldFriendService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.ERRORCODE;
import com.lad.vo.OldFriendRequireVo;
import com.lad.vo.ShowResultVo;
import com.mongodb.WriteResult;
import com.mongodb.diagnostics.logging.Logger;

import net.sf.json.JSONObject;

@RestController
@RequestMapping("oldFriend")
@SuppressWarnings("all")
public class OldFriendController extends BaseContorller {
	@Autowired
	private IOldFriendService oldFriendService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IFriendsService friendsService;

	@GetMapping("/recommend")
	public String recommend(String requireId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		Map map = new HashMap<>();
		// 通过基础id获取需求
		OldFriendRequireBo require = oldFriendService.getByRequireId(userBo.getId(), requireId);
		if (require == null) {
			return CommonUtil.toErrorResult(ERRORCODE.REQUIREID_NOMATCH.getIndex(),
					ERRORCODE.REQUIREID_NOMATCH.getReason());
		}

		List<Map> recommend = oldFriendService.getRecommend(require);

		List result = new ArrayList<>();
		if (recommend.size() >= 1) {
			
			Map resultOne = new HashMap<>();
			for (Map recMap : recommend) {
				OldFriendRequireBo resultBo = (OldFriendRequireBo) recMap.get("requireBo");
				ShowResultVo showResultVo = getShowResultVo(resultBo);
				resultOne.put("match", recMap.get("match"));
				resultOne.put("baseData", showResultVo);
				OldFriendRequireVo resultVo = new OldFriendRequireVo();
				BeanUtils.copyProperties(resultBo, resultVo);
				resultOne.put("require", resultVo);
				result.add(resultOne);
			}
			map.put("ret", 0);
			map.put("recommend", result);
		} else {
			map.put("ret", -1);
			map.put("message", "未找到匹配者");
		}

		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}

	@GetMapping("/getNew")
	public String getNew(int page, int limit, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		List<OldFriendRequireBo> list = oldFriendService.findNewPublish(page, limit, userBo.getId());

		Map map = new HashMap<>();
		if (list.size() >= 1) {
			List resultList = new ArrayList();
			for (OldFriendRequireBo requireBo : list) {
				UserBo user = userService.getUser(requireBo.getCreateuid());
				ShowResultVo showResult = new ShowResultVo();

				showResult.setId(requireBo.getId());

				// 设置昵称
				if (user.getUserName() != null) {
					showResult.setNickName(user.getUserName());
				} else {
					showResult.setNickName("");
				}

				// 设置头像
				if (user.getHeadPictureName() != null) {
					showResult.setHeadPicture(user.getHeadPictureName());
				} else {
					showResult.setHeadPicture("");
				}

				// 设置兴趣
				UserTasteBo hobbys = userService.findByUserId(user.getId());
				if (hobbys != null) {
					hobbys.setId(null);
					hobbys.setUserid(null);
					hobbys.setDeleted(null);
					hobbys.setUpdateTime(null);
					hobbys.setUpdateuid(null);
					hobbys.setCreateTime(null);
				}
				showResult.setHobbys(JSON.toJSONString(hobbys));
				if (user.getBirthDay() != null) {
					String birthDay = user.getBirthDay();
					DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
					try {
						Date parse = format.parse(birthDay);
						showResult.setAge(CommonUtil.getAge(parse));
					} catch (Exception e) {

						showResult.setAge("");
					}
				} else {
					showResult.setAge("");
				}

				if (user.getSex() != null) {
					showResult.setSex(user.getSex());
				} else {
					showResult.setSex("");
				}
				if (user.getCity() != null) {
					showResult.setAddress(user.getCity());
				} else {
					showResult.setAddress("");
				}
				/*
				 * if(requireBo.getImages()!=null){
				 * showResult.setImages(requireBo.getImages()); }else{ List
				 * imageList = new ArrayList<>();
				 * showResult.setImages(imageList); }
				 */

				resultList.add(showResult);
			}
			map.put("ret", 0);
			map.put("result", resultList);
		} else {
			map.put("ret", -1);
			map.put("message", "无符合条件的数据");
		}

		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}

	@GetMapping("/search")
	public String search(String keyWord, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		System.out.println(keyWord);
		keyWord = "相师";
		List<UserBo> list = oldFriendService.findListByKeyword(keyWord, page, limit, userBo.getId());

		Map map = new HashMap<>();
		if (list.size() >= 1) {
			List resultList = new ArrayList();
			for (UserBo user : list) {
				// UserBo user = userService.getUser(requireBo.getCreateuid());
				OldFriendRequireBo requireBo = oldFriendService.getRequireByCreateUid(user.getId());
				ShowResultVo showResult = new ShowResultVo();
				if (requireBo == null) {
					continue;
				}
				showResult.setId(requireBo.getId());

				// 设置昵称
				if (user.getUserName() != null) {
					showResult.setNickName(user.getUserName());
				} else {
					showResult.setNickName("");
				}

				// 设置头像
				if (user.getHeadPictureName() != null) {
					showResult.setHeadPicture(user.getHeadPictureName());
				} else {
					showResult.setHeadPicture("");
				}

				// 设置兴趣
				UserTasteBo hobbys = userService.findByUserId(user.getId());
				if (hobbys != null) {
					hobbys.setId(null);
					hobbys.setUserid(null);
					hobbys.setDeleted(null);
					hobbys.setUpdateTime(null);
					hobbys.setUpdateuid(null);
					hobbys.setCreateTime(null);
				}
				showResult.setHobbys(JSON.toJSONString(hobbys));
				if (userBo.getBirthDay() != null) {
					String birthDay = userBo.getBirthDay();
					DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
					try {
						Date parse = format.parse(birthDay);
						showResult.setAge(CommonUtil.getAge(parse));
					} catch (Exception e) {
						showResult.setAge("");
					}
				}

				if (user.getSex() != null) {
					showResult.setSex(user.getSex());
				} else {
					showResult.setSex("");
				}
				if (user.getCity() != null) {
					showResult.setAddress(user.getCity());
				} else {
					showResult.setAddress("");
				}
				/*
				 * if(requireBo.getImages()!=null){
				 * showResult.setImages(requireBo.getImages()); }else{ List
				 * imageList = new ArrayList<>();
				 * showResult.setImages(imageList); }
				 */

				resultList.add(showResult);
			}
			map.put("ret", 0);
			map.put("result", resultList);
		} else {
			map.put("ret", -1);
			map.put("message", "无符合条件的数据");
		}

		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}

	@GetMapping("/getDesc")
	public String getDesc(String requireId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		OldFriendRequireBo requireBo = oldFriendService.getByRequireId(requireId);
		if (requireBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.No_SUCH_PUBLISH.getIndex(),
					ERRORCODE.No_SUCH_PUBLISH.getReason());
		}

		UserBo user = userService.getUser(requireBo.getCreateuid());

		Map map = new HashMap<>();
		if (user != null) {
			ShowResultVo result = new ShowResultVo();
			if (user.getUserName() != null) {
				result.setNickName(user.getUserName());
			} else {
				result.setNickName("");
			}
			if (user.getHeadPictureName() != null) {
				result.setHeadPicture(user.getHeadPictureName());
			} else {
				result.setHeadPicture("");
			}
			if (user.getSex() != null) {
				result.setSex(user.getSex());
			} else {
				result.setSex("");
			}

			if (user.getBirthDay() != null) {
				String birthDay = user.getBirthDay();
				DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
				try {
					Date parse = format.parse(birthDay);
					result.setAge(CommonUtil.getAge(parse));
				} catch (Exception e) {
					result.setAge("");
				}
			}
			if (user.getCity() != null) {
				result.setAddress(user.getCity());
			} else {
				result.setAddress("");
			}

			if (!(user.getId().equals(userBo.getId()))) {
				result.setUid(user.getId());
				FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userBo.getId(), user.getId());
				if (friendsBo != null) {
					result.setFriend(true);
				} else {
					result.setFriend(false);
				}
			}

			UserTasteBo hobbys = userService.findByUserId(user.getId());
			if (hobbys != null) {
				hobbys.setCreateTime(null);
				hobbys.setDeleted(null);
				hobbys.setId(null);
				hobbys.setUserid(null);
				result.setHobbys(hobbys);
			} else {
				hobbys = new UserTasteBo();
				hobbys.setDeleted(null);
				hobbys.setCreateTime(null);
				result.setHobbys(hobbys);
			}
			map.put("ret", 0);
			map.put("baseData", result);
		} else {
			map.put("ret", -1);
			map.put("baseData", "未查找到用户的相关数据");
		}

		OldFriendRequireVo requireVo = new OldFriendRequireVo();
		BeanUtils.copyProperties(requireBo, requireVo);
		map.put("requireData", requireVo);

		return JSON.toJSONString(map);
	}

	@PostMapping("/updateRequire")
	public String updateRequire(@RequestParam String requireData, String requireId, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		// 校验require与uid是否匹配
		OldFriendRequireBo oldRequireBo = oldFriendService.getByRequireId(userBo.getId(), requireId);
		if (oldRequireBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.REQUIREID_NOMATCH.getIndex(),
					ERRORCODE.REQUIREID_NOMATCH.getReason());
		}

		Iterator<Map.Entry<String, Object>> entitySet = JSONObject.fromObject(requireData).entrySet().iterator();

		Map<String, Object> params = new HashMap<>();
		while (entitySet.hasNext()) {
			Map.Entry<String, Object> entity = entitySet.next();

			if ("sex".equals(entity.getKey()) && !(entity.getValue().equals(oldRequireBo.getSex()))) {
				params.put(entity.getKey(), entity.getValue());
			}

			if ("age".equals(entity.getKey()) && !(entity.getValue().equals(oldRequireBo.getAge()))) {

				params.put(entity.getKey(), entity.getValue());
			}
			if ("address".equals(entity.getKey()) && !(entity.getValue().equals(oldRequireBo.getAddress()))) {
				params.put(entity.getKey(), entity.getValue());
			}
			if ("hobbys".equals(entity.getKey())) {
				List newlist = (List) entity.getValue();
				if (newlist.size() == oldRequireBo.getHobbys().size() && newlist.containsAll(oldRequireBo.getHobbys())
						&& oldRequireBo.getHobbys().containsAll(newlist)) {
					continue;
				}
				params.put(entity.getKey(), entity.getValue());
			}
			if ("images".equals(entity.getKey())) {
				List newlist = (List) entity.getValue();
				if (newlist.size() == oldRequireBo.getImages().size() && newlist.containsAll(oldRequireBo.getImages())
						&& oldRequireBo.getImages().containsAll(newlist)) {
					continue;
				}
				params.put(entity.getKey(), entity.getValue());
			}
		}

		if (params.size() <= 0) {
			return CommonUtil.toErrorResult(ERRORCODE.UPDATE_NO_CHANGE.getIndex(),
					ERRORCODE.UPDATE_NO_CHANGE.getReason());
		}
		WriteResult result = oldFriendService.updateByParams(params, requireId);

		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("uid", userBo.getId());
		map.put("requireId", requireId);
		/*
		 * if (result.isUpdateOfExisting()) { map.put("ret", 0); map.put("uid",
		 * userBo.getId()); map.put("requireId", requireId); } else {
		 * map.put("ret", -1); map.put("message", "修改失败"); }
		 */
		return JSON.toJSONString(map);
	}

	@GetMapping("/getRequire")
	public String getRequire(String requireId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		OldFriendRequireBo requireBo = oldFriendService.getByRequireId(userBo.getId(), requireId);

		Map map = new HashMap<>();
		if (requireBo != null) {
			OldFriendRequireVo requireVo = new OldFriendRequireVo();
			try {
				BeanUtils.copyProperties(requireBo, requireVo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			map.put("ret", 0);
			map.put("requireData", requireVo);

		} else {
			map.put("ret", -1);
			map.put("message", "数据请求失败,请检查你的参数是否正确");
		}
		return JSON.toJSONString(map);
	}

	@PostMapping("/delete")
	public String delete(String requireId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		Map map = new HashMap<>();
		try {
			WriteResult result = oldFriendService.deleteByRequireId(userBo.getId(), requireId);

			map.put("ret", 0);
			map.put("message", "删除成功");
		} catch (Exception e) {
		
			map.put("ret", -1);
			map.put("message", e.toString());
		}

		return JSONObject.fromObject(map).toString();
	}

	@PostMapping("/add")
	public String insert(@RequestParam String requireData, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		JSONObject fromObject = JSONObject.fromObject(requireData);
		OldFriendRequireBo requireBo = (OldFriendRequireBo) JSONObject.toBean(fromObject, OldFriendRequireBo.class);
		if (requireBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.PARAMS_ERROR.getIndex(), ERRORCODE.PARAMS_ERROR.getReason());
		}

		long count = oldFriendService.getRequireCount(userBo.getId());
		if (count >= 1) {
			return CommonUtil.toErrorResult(ERRORCODE.PUBLISHNUM_BEYOND.getIndex(),
					ERRORCODE.PUBLISHNUM_BEYOND.getReason());
		}
		if (requireBo.isAgree() == false) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_AGREEMENT_FALSE.getIndex(),
					ERRORCODE.USER_AGREEMENT_FALSE.getReason());
		}
		requireBo.setCreateuid(userBo.getId());
		requireBo.setUpdateTime(new Date());
		requireBo.setUpdateuid(userBo.getId());

		String requireId = oldFriendService.insert(requireBo);
		Map map = new HashMap<>();
		if (requireId != null) {
			map.put("ret", 0);
			map.put("uid", userBo.getId());
			map.put("requireId", requireId);
		} else {
			map.put("ret", -1);
			map.put("message", ERRORCODE.INSERT_FAILD.getReason());
		}
		return JSONObject.fromObject(map).toString();
	}

	@GetMapping("/init")
	public String init(HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		String requireId = oldFriendService.getInitData(userBo.getId());

		Map map = new HashMap<>();
		if (requireId != null) {
			map.put("ret", 0);
			map.put("uid", userBo.getId());
			map.put("requireId", requireId);
		} else {
			map.put("ret", -1);
			map.put("message", ERRORCODE.No_SUCH_PUBLISH.getReason());
		}

		return JSONObject.fromObject(map).toString();
	}

	/*
	 * @Autowired private IUserService userService;
	 * 
	 * @PostMapping("/insert") public String insert(@RequestParam String
	 * baseData,HttpServletRequest request, HttpServletResponse response){
	 * UserBo userBo = getUserLogin(request); if (userBo == null) { return
	 * CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),ERRORCODE.
	 * ACCOUNT_OFF_LINE.getReason()); } JSONObject fromObject =
	 * JSONObject.fromObject(baseData); try {
	 * BeanUtils.copyProperties(fromObject, userBo); } catch (Exception e) {
	 * e.printStackTrace(); } System.out.println(userBo);
	 * 
	 * return null; }
	 */
	@GetMapping("/getBase")
	public String getBase(String requireId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		OldFriendRequireBo requireBo = oldFriendService.getByRequireId(userBo.getId(), requireId);
		if (requireBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.No_SUCH_PUBLISH.getIndex(),
					ERRORCODE.No_SUCH_PUBLISH.getReason());
		}

		ShowResultVo result = new ShowResultVo();
		if (userBo.getUserName() != null) {
			result.setNickName(userBo.getUserName());
		} else {
			result.setNickName("");
		}
		if (userBo.getHeadPictureName() != null) {
			result.setHeadPicture(userBo.getHeadPictureName());
		} else {
			result.setHeadPicture("");
		}
		if (userBo.getSex() != null) {
			result.setSex(userBo.getSex());
		} else {
			result.setSex("");
		}

		if (userBo.getBirthDay() != null) {
			String birthDay = userBo.getBirthDay();
			DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
			try {
				Date parse = format.parse(birthDay);
				result.setAge(CommonUtil.getAge(parse));
			} catch (Exception e) {
				result.setAge("");
			}
		}
		if (userBo.getCity() != null) {
			result.setAddress(userBo.getCity());
		} else {
			result.setAddress("");
		}

		UserTasteBo hobbys = userService.findByUserId(userBo.getId());
		if (hobbys != null) {
			hobbys.setCreateTime(null);
			hobbys.setDeleted(null);
			hobbys.setId(null);
			hobbys.setUserid(null);
			result.setHobbys(hobbys);
		} else {
			hobbys = new UserTasteBo();
			hobbys.setCreateTime(null);
			hobbys.setDeleted(null);
			result.setHobbys(hobbys);
		}
		if (requireBo.getImages() != null) {
			result.setImages(requireBo.getImages());
		} else {
			List list = new ArrayList<>();
			result.setImages(list);
		}

		result.setId(requireBo.getId());

		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("baseData", result);

		return JSON.toJSONString(map);
	}

	private ShowResultVo getShowResultVo(OldFriendRequireBo requireBo) {
		// 根据requireId
		UserBo user = userService.getUser(requireBo.getCreateuid());
		ShowResultVo showResult = new ShowResultVo();

		// 设置头像
		if (user.getHeadPictureName() != null) {
			showResult.setHeadPicture(user.getHeadPictureName());
		} else {
			showResult.setHeadPicture(null);
		}
		// 设置昵称
		if (user.getUserName() != null) {
			showResult.setNickName(user.getUserName());
		} else {
			showResult.setNickName(null);
		}

		// 设置性别
		if (user.getSex() != null) {
			showResult.setSex(user.getSex());
		} else {
			showResult.setSex(null);
		}

		// 设置年龄
		if (user.getBirthDay() != null) {
			String birthDay = user.getBirthDay();
			DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
			try {
				Date parse = format.parse(birthDay);
				showResult.setAge(CommonUtil.getAge(parse));
			} catch (Exception e) {
				showResult.setAge("");
			}
		} else {
			showResult.setAge("");
		}

		// 设置居住地
		if (user.getCity() != null) {
			showResult.setAddress(user.getCity());
		} else {
			showResult.setAddress(null);
		}

		// 设置兴趣
		UserTasteBo hobbys = userService.findByUserId(user.getId());
		if (hobbys != null) {
			hobbys.setId(null);
			hobbys.setUserid(null);
			hobbys.setDeleted(null);
			hobbys.setUpdateTime(null);
			hobbys.setUpdateuid(null);
			hobbys.setCreateTime(null);
			hobbys.setCreateuid(null);
		}
		showResult.setHobbys(JSON.toJSONString(hobbys));
		/*
		 * 设置照片 List images = new ArrayList(); if(requireBo.getImages()!=null){
		 * images = requireBo.getImages(); }
		 * showResult.setImages(requireBo.getImages());
		 */
		return showResult;
	}

}
