package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.MyException;
import com.lad.vo.CommentVo;
import com.lad.vo.NoteVo;
import com.lad.vo.UserBaseVo;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("note")
public class NoteController extends BaseContorller {

	private final Logger logger = RootLogger.getLogger(NoteController.class);

	@Autowired
	private INoteService noteService;
	@Autowired
	private ICircleService circleService;
	@Autowired
	private IUserService userService;

	@Autowired
	private ICommentService commentService;

	@Autowired
	private IThumbsupService thumbsupService;
	@Autowired
	private RedisServer redisServer;

	@Autowired
	private CommonsMultipartResolver multipartResolver;


	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(@RequestParam double px,
						 @RequestParam double py,
						 @RequestParam String subject,
						 @RequestParam(required = false)String landmark,
						 @RequestParam String content,
						 @RequestParam String circleid,
						 @RequestParam(required = false) MultipartFile[] pictures,
						 @RequestParam(required = false) String type,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (null == circleBo) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		NoteBo noteBo = new NoteBo();
		noteBo.setPosition(new double[] { px, py });
		noteBo.setLandmark(landmark);
		noteBo.setSubject(subject);
		noteBo.setContent(content);
		noteBo.setVisitcount(1);
		noteBo.setCreateuid(userBo.getId());
		noteBo.setCircleId(circleid);
		noteBo.setType(type);
		LinkedList<String> photos = new LinkedList<>();
		String userId =  userBo.getId();
		if (pictures != null) {
			Long time = Calendar.getInstance().getTimeInMillis();
			for (MultipartFile file : pictures) {
				String fileName = userId + "-" + time + "-"
						+ file.getOriginalFilename();
				logger.info(fileName);
				String path = CommonUtil.upload(file, Constant.NOTE_PICTURE_PATH,
						fileName, 0);
				logger.info(path);
				photos.add(path);
			}
		}
		noteBo.setPhotos(photos);
		noteService.insert(noteBo);
		HashSet<String> notes = circleBo.getNotes();
		notes.add(noteBo.getId());
		circleService.updateNotes(circleBo.getId(), notes);

		NoteVo noteVo = new NoteVo();
		boToVo(noteBo, noteVo, userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("noteVo", noteVo);
		return JSONObject.fromObject(map).toString();
	}


	@RequestMapping("/insert2")
	@ResponseBody
	public String isnert2(@RequestParam double px,
						 @RequestParam double py,
						 @RequestParam String subject,
						 @RequestParam(required = false)String landmark,
						 @RequestParam String content,
						 @RequestParam String circleid,
						 @RequestParam(required = false) MultipartFile pictures,
						 HttpServletRequest request, HttpServletResponse response) {

		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (null == circleBo) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		NoteBo noteBo = new NoteBo();
		noteBo.setPosition(new double[] { px, py });
		noteBo.setLandmark(landmark);
		noteBo.setSubject(subject);
		noteBo.setContent(content);
		noteBo.setVisitcount(1);
		noteBo.setCreateuid(userBo.getId());
		noteBo.setCircleId(circleid);
		LinkedList<String> photos = new LinkedList<>();
		String userId =  userBo.getId();

		Long time = Calendar.getInstance().getTimeInMillis();
		if (multipartResolver.isMultipart(request)){
			//转换成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
			//取得request中的所有文件名
			Iterator<String> iter = multiRequest.getFileNames();
			while (iter.hasNext()){
				MultipartFile file = multiRequest.getFile(iter.next());
				String fileName = userId + "-" + time + "-"
						+ file.getOriginalFilename();
				String path = CommonUtil.upload(file, Constant.NOTE_PICTURE_PATH,
						fileName, 0);
				photos.add(path);
			}
		}
		noteBo.setPhotos(photos);
		noteService.insert(noteBo);
		HashSet<String> notes = circleBo.getNotes();
		notes.add(noteBo.getId());
		circleService.updateNotes(circleBo.getId(), notes);

		NoteVo noteVo = new NoteVo();
		boToVo(noteBo, noteVo, userBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("noteVo", noteVo);
		return JSONObject.fromObject(map).toString();

	}


	@RequestMapping("/photo")
	@ResponseBody
	public String note_picture(@RequestParam("photos") MultipartFile[] files,
			@RequestParam(required = true) String noteid,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		String userId = userBo.getId();
		Long time = Calendar.getInstance().getTimeInMillis();

		NoteBo noteBo = noteService.selectById(noteid);
		if (null == noteBo) {
			return CommonUtil.toErrorResult(ERRORCODE.NOTE_IS_NULL.getIndex(),
					ERRORCODE.NOTE_IS_NULL.getReason());
		}
		LinkedList<String> photos = noteBo.getPhotos();
		List<String> paths = new ArrayList<>();
		for (MultipartFile file : files) {
			String fileName = userId + "-" + time + "-"
					+ file.getOriginalFilename();
			String path = CommonUtil.upload(file, Constant.NOTE_PICTURE_PATH,
					fileName, 0);
			photos.add(path);
			paths.add(path);
		}
		noteService.updatePhoto(noteid, photos);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("path", paths);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/thumbsup")
	@ResponseBody
	public String thumbsup(String noteid, HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ThumbsupBo thumbsupBo = new ThumbsupBo();
		thumbsupBo.setOwner_id(noteid);
		thumbsupBo.setVisitor_id(userBo.getId());
		thumbsupBo.setType(Constant.NOTE_TYPE);
		thumbsupService.insert(thumbsupBo);
		RLock lock = redisServer.getRLock(Constant.THUMB_LOCK);
		try {
			lock.lock(2,TimeUnit.SECONDS);
			noteService.updateThumpsubCount(noteid, 1);
		} finally {
			lock.unlock();
		}
		return Constant.COM_RESP;
	}

	@RequestMapping("/cancal-thumbsup")
	@ResponseBody
	public String cancelThumbsup(String noteid, HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(noteid, userBo.getId());
		if (thumbsupBo != null) {
			thumbsupService.deleteById(thumbsupBo.getId());
			RLock lock = redisServer.getRLock(Constant.THUMB_LOCK);
			try {
				lock.lock(2,TimeUnit.SECONDS);
				noteService.updateThumpsubCount(noteid, -1);
			} finally {
				lock.unlock();
			}
		}
		return Constant.COM_RESP;
	}

	@RequestMapping("/note-info")
	@ResponseBody
	public String noteInfo(String noteid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		NoteBo noteBo = noteService.selectById(noteid);
		if (null == noteBo) {
			return CommonUtil.toErrorResult(
					ERRORCODE.NOTE_IS_NULL.getIndex(),
					ERRORCODE.NOTE_IS_NULL.getReason());
		}

		RLock lock = redisServer.getRLock(Constant.VISIT_LOCK);
		try {
			lock.lock(3,TimeUnit.SECONDS);
			noteService.updateVisitCount(noteid);
		} finally {
			lock.unlock();
		}
		ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(userBo.getId(), noteid);
		NoteVo noteVo = new NoteVo();
		boToVo(noteBo, noteVo, userBo);
		//这个帖子自己是否点赞
		noteVo.setMyThumbsup(null != thumbsupBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("noteVo", noteVo);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 最新动态帖子
	 */
	@RequestMapping("/new-situation")
	@ResponseBody
	public String newSituation(String circleid, String start_id, boolean gt, int limit,
							   HttpServletRequest request, HttpServletResponse response) {
		try {
			checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<NoteBo> noteBos = noteService.finyByCreateTime(circleid,start_id,gt,limit);
		List<NoteVo> noteVos = new LinkedList<>();
		if (noteBos != null) {
			for (NoteBo noteBo : noteBos) {
				NoteVo note = new NoteVo();
				UserBo userBo = userService.getUser(noteBo.getCreateuid());
				boToVo(noteBo, note, userBo);
				noteVos.add(note);
			}
		}
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("noteVoList", noteVos);
        return JSONObject.fromObject(map).toString();
	}

	/**
	 * 精华帖子，字数100以上,按浏览量倒序排,取前10
	 */
    @RequestMapping("/essential-note")
    @ResponseBody
    public String bestNote(String circleid,HttpServletRequest request,
                           HttpServletResponse response) {
		try {
			checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
        List<NoteBo> noteBos = noteService.selectByVisit(circleid);
		List<NoteVo> noteVoList = new LinkedList<>();
		for (NoteBo noteBo : noteBos) {
			NoteVo noteVo = new NoteVo();
			UserBo userBo = userService.getUser(noteBo.getCreateuid());
			boToVo(noteBo, noteVo, userBo);
			noteVoList.add(noteVo);
		}
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("noteVoList", noteVoList);
        return JSONObject.fromObject(map).toString();
    }

	/**
	 * 热门详情；1周内帖子的阅读数+赞数+转发数+评论数最多的列表，取前10
	 * @return
	 */
    @RequestMapping("/hot-notes")
    @ResponseBody
    public String hotNotes(String circleid,HttpServletRequest request,
                           HttpServletResponse response) {
		try {
			checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
        List<NoteBo> noteBos = noteService.selectHotNotes(circleid);
		List<NoteVo> noteVoList = new LinkedList<>();
		for (NoteBo noteBo : noteBos) {
			NoteVo noteVo = new NoteVo();
			UserBo userBo = userService.getUser(noteBo.getCreateuid());
			boToVo(noteBo, noteVo, userBo);
			noteVoList.add(noteVo);
		}
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("noteVoList", noteVoList);
        return JSONObject.fromObject(map).toString();
    }

	/**
	 * 评论帖子或者回复评论
	 * @return
	 */
	@RequestMapping("/add-comment")
	@ResponseBody
	public String addComment(@RequestParam(required = true)String circleid,
							 @RequestParam(required = true) String noteid,
							 @RequestParam(required = true) String countent,
							 String parentid, HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		NoteBo noteBo = noteService.selectById(noteid);
		if (noteBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.NOTE_IS_NULL.getIndex(),
					ERRORCODE.NOTE_IS_NULL.getReason());
		}
		Date currentDate = new Date();
		CommentBo commentBo = new CommentBo();
		commentBo.setNoteid(noteBo.getId());
		commentBo.setParentid(parentid);
		commentBo.setUserName(userBo.getUserName());
		commentBo.setContent(countent);
		commentBo.setCreateuid(userBo.getId());
		commentBo.setCreateTime(currentDate);
		commentService.insert(commentBo);

		RedstarBo redstarBo = commentService.findRedstarBo(userBo.getId(), circleid);
		int curretWeekNo = CommonUtil.getWeekOfYear(currentDate);
		int year = CommonUtil.getYear(currentDate);
		if (redstarBo == null) {
			redstarBo = setRedstarBo(userBo.getId(), circleid, curretWeekNo, year);
			commentService.insertRedstar(redstarBo);
		} 
		//判断贴的作者是不是自己
		boolean isNotSelf = !userBo.getId().equals(noteBo.getCreateuid());
		boolean isNoteUserCurrWeek = true;
		//如果帖子作者不是自己
		if (isNotSelf) {
			//帖子作者没有红人数据信息，则添加
			RedstarBo noteRedstarBo = commentService.findRedstarBo(noteBo.getCreateuid(), circleid);
			if (noteRedstarBo == null) {
				noteRedstarBo = setRedstarBo(noteBo.getCreateuid(), circleid, curretWeekNo, year);
				commentService.insertRedstar(noteRedstarBo);
			} else {
				//判断帖子作者周榜是不是当前周，是则添加数据，不是则更新周榜数据
				isNoteUserCurrWeek = (year == noteRedstarBo.getYear() && curretWeekNo == noteRedstarBo.getWeekNo());
			}
		}
		//判断自己周榜是不是同一周，是则添加数据，不是则更新周榜数据
		boolean isCurrentWeek = (year == redstarBo.getYear() && curretWeekNo == redstarBo.getWeekNo());
		//更新自己或他人红人评论数量，需要加锁，保证数据准确
		RLock lock = redisServer.getRLock(Constant.COMOMENT_LOCK);
		try {
			lock.lock(5, TimeUnit.SECONDS);
			//更新帖子评论数
			noteService.updateCommentCount(noteid,1);
			//更新自己的红人信息
			if (isCurrentWeek) {
				commentService.addRadstarCount(userBo.getId(), circleid);
			} else {
				commentService.updateRedWeekByUser(userBo.getId(), curretWeekNo, year);
			}
			if (isNotSelf) {
				//更新帖子作者的红人信息
				if (isNoteUserCurrWeek) {
					commentService.addRadstarCount(noteBo.getCreateuid(), circleid);
				} else {
					commentService.updateRedWeekByUser(noteBo.getCreateuid(), curretWeekNo, year);
				}
			}
		} finally {
			lock.unlock();
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("commentVo", comentBo2Vo(commentBo));
		return JSONObject.fromObject(map).toString();
	}


	/**
	 * 删除自己的帖子评论
	 * @return
	 */
	@RequestMapping("/delete-self-comment")
	@ResponseBody
	public String deleteComments(String commentid,HttpServletRequest request,  HttpServletResponse response) {

		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CommentBo commentBo = commentService.findById(commentid);
		if (commentBo != null) {
			if (userBo.getId().equals(commentBo.getCreateuid())) {
				commentService.delete(commentid);
			} else {
				return CommonUtil.toErrorResult(
						ERRORCODE.NOTE_NOT_MASTER.getIndex(),
						ERRORCODE.NOTE_NOT_MASTER.getReason());
			}
		}
		return Constant.COM_RESP;
	}

	/**
	 * 获取帖子评论
	 * @return
	 */
	@RequestMapping("/get-comments")
	@ResponseBody
	public String getComments(String noteid, String start_id, boolean gt, int limit,
							  HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		NoteBo noteBo = noteService.selectById(noteid);
		if (noteBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.NOTE_IS_NULL.getIndex(),
					ERRORCODE.NOTE_IS_NULL.getReason());
		}

		List<CommentBo> commentBos = commentService.selectByNoteid(noteid, start_id, gt, limit);
		List<CommentVo> commentVos = new ArrayList<>();
		for (CommentBo commentBo : commentBos) {
			CommentVo commentVo = comentBo2Vo(commentBo);
			ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(noteid, userBo.getId());
			commentVo.setMyThumbsup(thumbsupBo != null);
			long thums = thumbsupService.selectByOwnerIdCount(noteid);
			commentVo.setThumpsubCount(thums);
			if (!StringUtils.isEmpty(commentBo.getParentid())) {
				CommentBo parent = commentService.findById(commentBo.getParentid());
				commentVo.setParentUserName(parent.getUserName());
				commentVo.setParentUserid(parent.getCreateuid());
			}
			commentVos.add(commentVo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("commentVoList", commentVos);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 获取自己的所有评论
	 * @return
	 */
	@RequestMapping("/get-self-comments")
	@ResponseBody
	public String getSelfComments(String start_id, boolean gt, int limit,
								  HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<CommentBo> commentBos = commentService.selectByUser(userBo.getId(), start_id, gt, limit);
		List<CommentVo> commentVos = new ArrayList<>();
		for (CommentBo commentBo : commentBos) {
			commentVos.add(comentBo2Vo(commentBo));
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("commentVoList", commentVos);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 获取自己被评论过的帖子
	 * @return
	 */
	@RequestMapping("/my-comment-notes")
	@ResponseBody
	public String getMyCommentNotes(String start_id, boolean gt, int limit,
								  HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<NoteBo> noteBos = noteService.finyMyNoteByComment(userBo.getId(), start_id, gt, limit);
		List<NoteVo> noteVoList = new LinkedList<>();
		for (NoteBo noteBo : noteBos) {
			NoteVo noteVo = new NoteVo();
			boToVo(noteBo, noteVo, userBo);
			noteVoList.add(noteVo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("noteVoList", noteVoList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 评论点赞
	 * @return
	 */
	@RequestMapping("/comment-thumbsup")
	@ResponseBody
	public String commentThumbsup(String commentid,HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}

		ThumbsupBo thumbsupBo = new ThumbsupBo();
		thumbsupBo.setType(Constant.NOTE_COM_TYPE);
		thumbsupBo.setOwner_id(commentid);
		thumbsupBo.setVisitor_id(userBo.getId());
		thumbsupBo.setCreateuid(userBo.getId());
		thumbsupService.insert(thumbsupBo);
		return Constant.COM_RESP;
	}


	/**
	 * 获取帖子点赞列表
	 * @return
	 */
	@RequestMapping("/get-note-thumbsups")
	@ResponseBody
	public String getNoteThumbsups(String noteid, String start_id, boolean gt, int limit,
									HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<ThumbsupBo> thumbsupBos = thumbsupService.selectByOwnerIdPaged(
				start_id, gt, limit, noteid, Constant.NOTE_TYPE);

		List<UserBaseVo> userBaseVos = new ArrayList<>();
		for (ThumbsupBo thumbsupBo : thumbsupBos) {
			UserBo user = userService.getUser(thumbsupBo.getVisitor_id());
			if (user != null) {
				UserBaseVo userBaseVo = new UserBaseVo();
				BeanUtils.copyProperties(user, userBaseVo);
				userBaseVos.add(userBaseVo);
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("userVoList", userBaseVos);
		return JSONObject.fromObject(map).toString();
	}


	/**
	 * 获取置顶帖子，置顶帖子条件，字数>=200, 图片>=3,时间倒序取前2
	 * @return
	 */
	@RequestMapping("/top-notes")
	@ResponseBody
	public String topNotes(String circleid,HttpServletRequest request,
						   HttpServletResponse response) {
		try {
			checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<NoteBo> noteBos = noteService.selectTopNotes(circleid);
		List<NoteVo> noteVoList = new LinkedList<>();
		for (NoteBo noteBo : noteBos) {
			NoteVo noteVo = new NoteVo();
			boToVo(noteBo, noteVo, userService.getUser(noteBo.getCreateuid()));
			noteVoList.add(noteVo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("noteVoList", noteVoList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 我的帖子
	 * @return
	 */
	@RequestMapping("/my-notes")
	@ResponseBody
	public String myNotes(String start_id, boolean gt, int limit, HttpServletRequest request,
						   HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<NoteBo> noteBos = noteService.selectMyNotes(userBo.getId(), start_id, gt, limit);
		List<NoteVo> noteVoList = new LinkedList<>();
		NoteVo noteVo = null;
		for (NoteBo noteBo : noteBos) {
			noteVo = new NoteVo();
			userBo = userService.getUser(noteBo.getCreateuid());
			boToVo(noteBo, noteVo, userBo);
			noteVoList.add(noteVo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("noteVoList", noteVoList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 圈主删除帖子
	 * @return
	 */
	@RequestMapping("/delete-circle-notes")
	@ResponseBody
	public String deleteNotes(@RequestParam String noteids, @RequestParam String circleid, HttpServletRequest request,
						  HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (null == circleBo) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		String[] ids = CommonUtil.getIds(noteids);
		if (circleBo.getCreateuid().equals(userBo.getId())) {
			for (String id : ids) {
				NoteBo noteBo = noteService.selectById(id);
				if (null != noteBo) {
					//圈主删除帖子
					noteService.deleteNote(id);
				}
			}
		}  else {
			return CommonUtil.toErrorResult(
					ERRORCODE.NOTE_NOT_MASTER.getIndex(),
					ERRORCODE.NOTE_NOT_MASTER.getReason());
		}
		return Constant.COM_RESP;
	}


	/**
	 * 个人删除自己的帖子
	 * @return
	 */
	@RequestMapping("/delete-my-notes")
	@ResponseBody
	public String deleteMyNotes(@RequestParam String noteids,HttpServletRequest request,
							  HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		String[] ids = CommonUtil.getIds(noteids);
		for (String id : ids) {
			NoteBo noteBo = noteService.selectById(id);
			if (null != noteBo) {
				//删除帖子
				if (noteBo.getCreateuid().equals(userBo.getId())) {
					noteService.deleteNote(id);
				}
			}
		}
		return Constant.COM_RESP;
	}

	private RedstarBo setRedstarBo(String userid, String circleid, int weekNo, int year){
		RedstarBo redstarBo = new RedstarBo();
		redstarBo.setUserid(userid);
		redstarBo.setCommentTotal((long) 1);
		redstarBo.setCommentWeek((long) 1);
		redstarBo.setWeekNo(weekNo);
		redstarBo.setCircleid(circleid);
		redstarBo.setYear(year);
		return redstarBo;
	}



	private void boToVo(NoteBo noteBo, NoteVo noteVo, UserBo userBo){
		BeanUtils.copyProperties(noteBo, noteVo);
		if (userBo!= null) {
			noteVo.setSex(userBo.getSex());
			noteVo.setBirthDay(userBo.getBirthDay());
			noteVo.setHeadPictureName(userBo.getHeadPictureName());
			noteVo.setUsername(userBo.getUserName());
		}
		noteVo.setCommontCount(noteBo.getCommentcount());
		noteVo.setVisitCount(noteBo.getVisitcount());
		noteVo.setNodeid(noteBo.getId());
		noteVo.setTransCount(noteBo.getTranscount());
		noteVo.setThumpsubCount(noteBo.getThumpsubcount());
	}

	private CommentVo comentBo2Vo(CommentBo commentBo){
		CommentVo commentVo = new CommentVo();
		BeanUtils.copyProperties(commentBo, commentVo);
		commentVo.setCommentId(commentBo.getId());
		commentVo.setUserid(commentBo.getCreateuid());
		return commentVo;
	}


}
