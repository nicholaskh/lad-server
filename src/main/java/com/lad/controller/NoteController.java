package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.MyException;
import com.lad.vo.NoteVo;
import net.sf.json.JSONObject;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("note")
public class NoteController extends BaseContorller {

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


	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(@RequestParam(required = true) double px,
			@RequestParam(required = true) double py,
			@RequestParam(required = true) String subject,
			@RequestParam(required = true) String landmark,
			@RequestParam(required = true) String content,
			@RequestParam(required = true) String circleid,
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
		noteService.insert(noteBo);
		HashSet<String> notes = circleBo.getNotes();
		notes.add(noteBo.getId());
		circleService.updateNotes(circleBo.getId(), notes);
		NoteVo noteVo = new NoteVo();
		boToVo(noteBo, noteVo);
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
		HashSet<String> photos = noteBo.getPhotos();
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
		NoteVo noteVo = new NoteVo();
		boToVo(noteBo, noteVo);
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
	public String newSituation(String circleid, String startId, boolean gt, int limit,
							   HttpServletRequest request, HttpServletResponse response) {
		try {
			checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<NoteBo> noteBos = noteService.finyByCreateTime(circleid,startId,gt,limit);
        List<NoteVo> noteVoList = bo2vo(noteBos);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("noteVoList", noteVoList);
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
        List<NoteVo> noteVoList = bo2vo(noteBos);
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
        List<NoteBo> noteBos = noteService.selectByComment(circleid);
        List<NoteVo> noteVoList = bo2vo(noteBos);
        Map<String, Object> map = new HashMap<String, Object>();
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
		map.put("commentVo", commentBo);
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
		List<NoteVo> noteVoList = bo2vo(noteBos);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("noteVoList", noteVoList);
		return JSONObject.fromObject(map).toString();
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

    private List<NoteVo> bo2vo(List<NoteBo> noteBos){
        List<NoteVo> noteVoList = new LinkedList<>();
        NoteVo noteVo;
        for (NoteBo noteBo : noteBos) {
            noteVo = new NoteVo();
           	boToVo(noteBo, noteVo);
			noteVoList.add(noteVo);
        }
		return noteVoList;
	}

	private void boToVo(NoteBo noteBo, NoteVo noteVo){
		BeanUtils.copyProperties(noteBo, noteVo);

    	UserBo userBo = userService.getUser(noteBo.getCreateuid());
		noteVo.setSex(userBo.getSex());
		noteVo.setBirthDay(userBo.getBirthDay());
		noteVo.setHeadPictureName(userBo.getHeadPictureName());
		noteVo.setUsername(userBo.getUserName());
		noteVo.setCommontCount(noteBo.getCommentcount());
		noteVo.setVisitCount(noteBo.getVisitcount());
		noteVo.setNodeid(noteBo.getId());
		noteVo.setTransCount(noteBo.getTranscount());
		noteVo.setThumpsubCount(noteBo.getThumpsubcount());
	}


}
