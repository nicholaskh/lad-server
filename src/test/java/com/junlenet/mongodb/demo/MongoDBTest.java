package com.junlenet.mongodb.demo;

import com.alibaba.fastjson.JSON;
import com.lad.bo.*;
import com.lad.dao.*;
import com.lad.scrapybo.InforBo;
import com.lad.scrapybo.SecurityBo;
import com.lad.service.*;
import com.lad.util.Constant;
import com.lad.util.IMUtil;
import com.lad.vo.ChatroomUserVo;
import com.lad.vo.ChatroomVo;
import com.lad.vo.CircleVo;
import com.lad.vo.NoteVo;
import com.mongodb.BasicDBObject;
import com.pushd.ImAssistant;
import com.pushd.Message;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml",
		"classpath:spring-mvc.xml" })
public class MongoDBTest {

	@Autowired
	@Qualifier("mongoTemplateTwo")
	private MongoTemplate mongoTemplateTwo;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private IChatroomDao chatroomDao;

	@Autowired
	private ICircleDao circleDao;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private IUserService userService;

	@Autowired
	private INoteDao noteDao;

	@Autowired
	private IRedstarDao redstarDao;

	@Autowired
	private ICircleService circleService;

	@Autowired
	private IInforService inforService;

	@Autowired
	private ICommentService commentService;

	@Autowired
	private INoteService noteService;

	@Autowired
	private IThumbsupService thumbsupService;


	@Autowired
	private ILocationService locationService;

	@Autowired
	private IUserLevelDao userLevelDao;

	@Autowired
	private ICityDao cityDao;

	@Autowired
	private IFriendsService friendsService;

	@Autowired
	private IChatroomService chatroomService;

	@Autowired
	private ICircleTypeDao circleTypeDao;

	@Autowired
	private IDynamicService dynamicService;

	@Autowired
	private IIMTermService iMTermService;


	@Autowired
	private ITagService tagService;



	@Before
	public void setUp() {

	}

	@Test
	public void getUsers() {
		List<UserBo> userBos = userDao.getAllUser();

		for (UserBo userBo : userBos) {
			System.out.println(JSON.toJSONString(userBo));
		}

	}

	@Test
	public void addReds() {
		List<RedstarBo> redstarBos = redstarDao.findRedTotal("594d364f31f0a560fb6a4b5f");
		for (RedstarBo redstarBo : redstarBos) {
			redstarDao.updateRedWeekByUser(redstarBo.getUserid(), redstarBo.getWeekNo(), 2017);
		}
	}

	@Test
	public void getReds() {

		List<RedstarBo> redstarBos = userService.findRedUserTotal("594d364f31f0a560fb6a4b5f");
		for (RedstarBo redstarBo : redstarBos) {
			UserBo userBo = userService.getUser(redstarBo.getUserid());
			System.out.println(JSON.toJSONString(userBo));
		}


	}

	@Test
	public void addNote() {
		NoteBo noteBo = new NoteBo();
		noteBo.setCircleId("599314b131f0a579c692e5cf");
		noteBo.setCreateuid("5989cb6231f0a569e1dbfee3");
		noteBo.setSubject("redis user and hahah");
		noteBo.setContent("BSON documents may have more than one field with the same name. Most MongoDB interfaces, " + "however, represent MongoDB with a structure (e.g. a hash table) that does not support duplicate field " + "names. If you need to manipulate documents that have more than one field with the same name, see the driver documentation for your driver.\n" + "\n" + "Some documents created by internal MongoDB processes may have duplicate fields, but no MongoDB process will ever add duplicate fields to an existing user document.");
		noteBo.setVisitcount(20);
		noteBo.setCommentcount(10);
		noteBo.setThumpsubcount(12);

		LinkedList<String> photos = new LinkedList<>();
		photos.add("picture1.jsp");
		photos.add("picture2.jsp");

		noteBo.setPhotos(photos);
		noteBo.setTemp(0);
		noteDao.insert(noteBo);
	}


	@Test
	public void getNote() {


//		List<CommentBo> commentBos = commentService.selectByNoteid("5989cc2e31f0a569e1dbfee7", "", true, 10);
//
//		for (CommentBo commentBo : commentBos) {
//			System.out.println(JSON.toJSONString(commentBo));
//
//		}

//		List<NoteBo> noteBoss = noteService.finyMyNoteByComment("5989cb6231f0a569e1dbfee3","",false, 10);


//		List<NoteBo> noteBoss = noteDao.selectByVisit("594d364f31f0a560fb6a4b5f");

		List<NoteBo> noteBos = noteService.selectHotNotes("599314b131f0a579c692e5cf");
		for (NoteBo noteBo : noteBos) {
			System.out.println(JSON.toJSONString(noteBo));
		}

	}


	/**
	 * 索引创建后测试
	 */
	@Test
	public void save() {
		ChatroomBo chatroomBo = new ChatroomBo();
		chatroomBo.setName("face4");
		chatroomBo.setSeq(0);
		chatroomBo.setType(3);
		chatroomBo.setPosition(new double[]{118.639523, 32.070078});
		chatroomDao.insert(chatroomBo);
		System.out.println(chatroomBo.getId());

		chatroomBo = new ChatroomBo();
		chatroomBo.setName("talk3");
		chatroomBo.setSeq(0);
		chatroomBo.setType(2);

		chatroomDao.insert(chatroomBo);

		System.out.println(chatroomBo.getId());

		System.out.println("-----------------------");
	}

	@Test
	public void findRangeTest() {

//		DBObject searchObj = new BasicDBObject();
//		searchObj.put("_id", chatroomId);
//		DBObject search = new BasicDBObject("$geoNear",
//				new BasicDBObject("$geometry",
//						new BasicDBObject("coordinates",position)
//								.append("type", "Point"))
//						.append("$maxDistance",radius));
//		DBObject dbObject = new BasicDBObject();
//		dbObject.put("position",search);
//		DBCollection collection = mongoTemplate.getCollection(collectionName);
//		DBCursor cursor = collection.find(dbObject, searchObj);
//
//		Iterator<DBObject> its = cursor.iterator();
//		while (its.hasNext()) {
//			String obj = its.next().get("_id").toString();
//			if (obj.equals(chatroomId)){
//				return true;
//			}
//		}DBObject searchObj = new BasicDBObject();
//		searchObj.put("_id", chatroomId);
//		DBObject search = new BasicDBObject("$geoNear",
//				new BasicDBObject("$geometry",
//						new BasicDBObject("coordinates",position)
//								.append("type", "Point"))
//						.append("$maxDistance",radius));
//		DBObject dbObject = new BasicDBObject();
//		dbObject.put("position",search);
//		DBCollection collection = mongoTemplate.getCollection(collectionName);
//		DBCursor cursor = collection.find(dbObject, searchObj);
//
//		Iterator<DBObject> its = cursor.iterator();
//		while (its.hasNext()) {
//			String obj = its.next().get("_id").toString();
//			if (obj.equals(chatroomId)){
//				return true;
//			}
//		}

		double[] position = new double[]{118.788135, 32.029064};
		boolean res = chatroomDao.withInRange("59451b49d75bc2118c082c6c", position, 2000);
		System.out.println(res);
	}

	@Test
	public void circleTest() {
		List<CircleBo> circleBos = circleDao.selectUsersPre("");
		System.out.println(circleBos.size());
		for (CircleBo circleBo : circleBos) {
			System.out.println(JSON.toJSONString(circleBo));
		}
	}

	@Test
	public void myCircleTest() {
		List<CircleBo> circleBos = circleDao.findMyCircles("595514db31f0a503a5a4225d", "", false, 10);
		System.out.println(circleBos.size());
		for (CircleBo circleBo : circleBos) {
			System.out.println(JSON.toJSONString(circleBo));

//            if (!circleBo.getId().equals("594f765a31f0a567340921f3")) {
//				Query query = new Query();
//				query.addCriteria(new Criteria("deleted").is(0));
//				query.addCriteria(new Criteria("_id").is(circleBo.getId()));
//				Update update = new Update();
//				//创建者默认为群主，后续修改需要更改群主字段
//				update.set("deleted", 1);
//				mongoTemplate.updateFirst(query, update, CircleBo.class);
//			}
		}

	}


	@Test
	public void addApply() {
		CircleTypeBo circleTypeBo = new CircleTypeBo();
		circleTypeBo.setLevel(1);
		circleTypeBo.setCategory("其他");
		circleService.addCircleType(circleTypeBo);
	}


	@Test
	public void getUserapply() {

		String circleid = "594f765a31f0a567340921f3";
		CircleBo circleBo = circleService.selectById("5969c10b31f0a515641c5c82");
		System.out.println(JSON.toJSONString(circleBo));

		CircleVo circleVo = new CircleVo();
		BeanUtils.copyProperties(circleBo, circleVo);
		circleVo.setId(circleBo.getId());
		circleVo.setName(circleBo.getName());
		circleVo.setUsersSize(circleBo.getUsers().size());
		circleVo.setNotesSize(circleBo.getNoteSize());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("circleVo", circleVo);
		System.out.println(JSONObject.fromObject(map).toString());


//		UserBo userBo = userDao.getUser("595514db31f0a503a5a4225d");
//		List<String> tops = new LinkedList<>();
//		tops.add("594d364f31f0a560fb6a4b5f");
//		tops.add("5956624031f0a54643b48d6e");
//		tops.add("5955d8f131f0a54643b48d6b");
//		userDao.updateTopCircles(userBo.getId(), tops);
//		System.out.println(JSON.toJSONString(userBo));
	}


	@Test
	public void tst() {
//
//    	UserBo userBo = userDao.getUser("595514db31f0a503a5a4225d");
//		System.out.println(JSON.toJSONString(userBo));

//		UserBo userBo = userDao.getUserByPhone("15652918035");
//		userBo.setHeadPictureName("http://oojih7o1f.bkt.clouddn.com/head-594d1a7031f0a560fb6a4b4dly_avatar.jpg?v=8");
//		userBo = userDao.updateHeadPictureName(userBo);
//		System.out.println(JSON.toJSONString(userBo));
//
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is("597f12a0d78a362820808a3e"));

		InforBo inforBo = mongoTemplateTwo.findOne(query, InforBo.class);
		System.out.println(JSON.toJSONString(inforBo));

	}


	@Test
	public void infos() {
//		List<InforBo> inforBos = inforService.findAllGroups();
//
//		for (InforBo inforBo : inforBos) {
//			System.out.println(JSON.toJSONString(inforBo));
//		}
//		System.out.println("----------------------");

//		List<InforBo> infors = inforService.findClassInfos("疾病预防", "", 10);
//
//		for (InforBo inforBo : infors) {
//			System.out.println(JSON.toJSONString(inforBo));
//		}

//		List<InforBo> infors = inforDao.findByList("健康新知");

		List<SecurityBo> securityBos = inforService.findSecurityTypes();
		for (SecurityBo inforBo : securityBos) {
			System.out.println(JSON.toJSONString(inforBo));
		}


	}

	@Test
	public void test1() {

//		List<CircleBo> circleBos = mongoTemplate.findAll(CircleBo.class);
//
//		for (CircleBo circleBo : circleBos) {
//			System.out.println(JSON.toJSONString(circleBo));
//		}

//		List<CircleBo> circleBos = circleService.findByType("运动", 1, "", false, 10);
//		for (CircleBo circleBo : circleBos) {
//			System.out.println(JSON.toJSONString(circleBo));
//		}

		List<NoteBo> noteBos = noteService.finyByCreateTime("599314b131f0a579c692e5cf", "", false, 10);
		for (NoteBo noteBo : noteBos) {
			System.out.println(JSON.toJSONString(noteBo));
		}


//		List<NoteBo> noteBos = noteDao.selectCircleNotes(circleBo.getId(), "", false, 200);
//		System.out.println(noteBos.size() + ",=== " + circleBo.getNoteSize());
//
//		String circleid = "599314b131f0a579c692e5cf";
//
//		System.out.println("-----------------");
//		CircleBo circleBo = circleService.selectById(circleid);
//		System.out.println(circleBo.getNoteSize());
	}


	@Test
	public void delete() {
		List<CircleTypeBo> typeBos = mongoTemplate.findAll(CircleTypeBo.class);
		for (CircleTypeBo typeBo : typeBos) {
			mongoTemplate.remove(typeBo);
		}
	}

	@Test
	public void add() {
		String userid = "5989cb6231f0a569e1dbfee3";
		Criteria criteria = new Criteria("createuid").is(userid);
		criteria.and("deleted").is(Constant.ACTIVITY);
		criteria.and("type").is(Constant.NOTE_TYPE);
		criteria.and("ownerid").ne(userid);
		criteria.and("noteid").lt("598de11e31f0a5457d65bb2a");

		AggregationOperation match = Aggregation.match(criteria);

		LookupOperation lookup = Aggregation.lookup("note", "noteid","_id", "note" );


		GroupOperation group = Aggregation.group("noteid").first("noteid").as("noteid");
		Aggregation aggregation = Aggregation.newAggregation(match, group, lookup,
				Aggregation.sort(new Sort(new Sort.Order(Sort.Direction.DESC, "noteid"))),
				Aggregation.limit(10));

		AggregationResults<BasicDBObject> results = mongoTemplate.aggregate(aggregation, "comment",
				BasicDBObject.class);
		for (BasicDBObject object : results){
			System.out.println(object.toString());
		}


//		List<CommentBo> commentBos = mongoTemplate.findAll(CommentBo.class);
//
//		for (CommentBo commentBo : commentBos) {
//			if (commentBo.getType()== 0){
//				System.out.println(JSON.toJSONString(commentBo));
//				NoteBo noteBo = noteService.selectById(commentBo.getNoteid());
//				if (noteBo != null) {
//					Query query = new Query();
//					query.addCriteria(new Criteria("_id").is(commentBo.getId()));
//					Update update = new Update();
//					update.set("ownerid", noteBo.getCreateuid());
//					WriteResult writeResult = mongoTemplate.updateFirst(query, update, CommentBo.class);
//					System.out.println(writeResult.getN());
//				}
//			}
//		}

	}

	@Test
	public void addLocation() {

		List<UserBo> userBos = userDao.getAllUser();

		for (int i = 0; i < userBos.size(); i++) {
			UserBo userBo = userBos.get(i);
			if (!userBo.getId().equals("5989cb6231f0a569e1dbfee3")) {
				System.out.println(JSON.toJSONString(userBo));
				LocationBo locationBo = new LocationBo();
				locationBo.setUserid(userBo.getId());
				locationBo.setCreateuid(userBo.getId());
				locationBo.setPosition(new double[]{116.307629 + 0.02 * i, 40.069359 + 0.1 * i});
				locationService.insertUserPoint(locationBo);
			}
		}
	}

	@Test
	public void addhis() {
		UserBo userBo = userDao.getUserByPhone("18601944976");
		System.out.println(JSON.toJSONString(userBo));

		UserBo userBo1 = userDao.getUserByPhone("15528023770");
		System.out.println(JSON.toJSONString(userBo1));

		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorId(userBo.getId(), userBo1.getId());
		System.out.println("userbo: ===" + JSON.toJSONString(friendsBo));
		FriendsBo friendsBo1 = friendsService.getFriendByIdAndVisitorId(userBo1.getId(), userBo.getId());
		System.out.println("userbo11: ===" + JSON.toJSONString(friendsBo1));

		ChatroomBo chatroomBo = chatroomService.selectByUserIdAndFriendid(
				userBo.getId(), userBo1.getId());
		System.out.println("chatroomBo: ===" + JSON.toJSONString(chatroomBo));

		//在聊天室中，用户ID和好友ID是一对，所以互换ID能够查询到，都算同一个channel
		if (null == chatroomBo) {
			chatroomBo = chatroomService.selectByUserIdAndFriendid(
					userBo1.getId(), userBo.getId());
			System.out.println("chatroomBo111: ===" + JSON.toJSONString(chatroomBo));
		}

		String chatroomName = chatroomBo.getName();
		//首次创建聊天室，需要输入名称
		String res = IMUtil.subscribe(1,chatroomBo.getId(), userBo.getId(), userBo1.getId());

		System.out.println("res=======================" + res);

	}

	@Test
	public void insertCitys() throws Exception{

//		List<CityBo> cityBos = mongoTemplate.findAll(CityBo.class);
//		for (CityBo cityBo : cityBos) {
//			mongoTemplate.remove(cityBo);
//			Thread.sleep(50);
//		}
//
//		InputStream input = new FileInputStream("E:\\lad-app\\citys.xlsx");
//		XSSFWorkbook wb = new XSSFWorkbook(input);
//
//		XSSFSheet sheet = wb.getSheetAt(0);
//		int num = sheet.getLastRowNum();
//		String province = "";
//		String city = "";
//		for (int i = 1; i < num; i++) {
//			XSSFRow row = sheet.getRow(i);
//
//			double cellNo = row.getCell(1).getNumericCellValue();
//			String cell = row.getCell(2).getStringCellValue();
//			if (cellNo % 10000 == 0){
//				province = cell;
//				continue;
//			}
//			if (cellNo % 100 == 0) {
//				if (cell.endsWith("地区") || cell.equals("市辖区") || cell.equals("直辖县级行政区划")) {
//					continue;
//				}
//				city = cell;
//				continue;
//			}
//			if (province.equals("北京市") || province.equals("天津市")
//					|| province.equals("上海市") || province.equals("重庆市") ) {
//				city = province;
//			}
//
//			CityBo cityBo = new CityBo();
//			cityBo.setProvince(province);
//			cityBo.setCity(city);
//			cityBo.setDistrit(cell);
//			cityDao.insert(cityBo);
//			Thread.sleep(50);
//		}
	}

	@Test
	public void addCircleTye(){

		String[] types = new String[]{
				"交友","运动","生活","玩乐","车友","行业","其他"
		};
		for (String type : types) {
			  CircleTypeBo typeBo = new CircleTypeBo();
			  typeBo.setCategory(type);
			  typeBo.setLevel(1);
			  typeBo.setType(0);
			 circleTypeDao.insert(typeBo);
		}

		String[] friends = new String[]{
				"交友","50后","60后","70后","80后","90后","社区","广场舞","情感","聚会"
				,"婚姻","同城同乡","初中同学","高中同学","大学同学","研究生及以上同学","其他"
		};
		for (String type : friends) {
			CircleTypeBo typeBo = new CircleTypeBo();
			typeBo.setCategory(type);
			typeBo.setPreCateg("交友");
			typeBo.setLevel(2);
			typeBo.setType(0);
			circleTypeDao.insert(typeBo);
		}

		String[] sports = new String[]{
				"健身","快走","太极拳","跑步","乒乓球","羽毛球","郊游"
				,"游泳","爬山","暴走","台球","单车","瑜伽","篮球","足球","网球","高尔夫"
				,"滑板","滑雪","舞蹈","街舞","射箭","击剑","射击","拳击","骑马","其他"
		};
		for (String type : sports) {
			CircleTypeBo typeBo = new CircleTypeBo();
			typeBo.setCategory(type);
			typeBo.setPreCateg("运动");
			typeBo.setLevel(2);
			typeBo.setType(0);
			circleTypeDao.insert(typeBo);
		}

		String[] lifes = new String[]{
				"养生","旅游","宠物","茶文化","家居","花草","宗教"
				,"美食","母婴","美容","读书","购物交易","心理学","时尚","其他"
		};
		for (String type : lifes) {
			CircleTypeBo typeBo = new CircleTypeBo();
			typeBo.setCategory(type);
			typeBo.setPreCateg("生活");
			typeBo.setLevel(2);
			typeBo.setType(0);
			circleTypeDao.insert(typeBo);
		}

		String[] wanles = new String[]{
				"唱歌","跳舞","音乐","摄影","生肖","易经","电影","瑜伽","文玩","文学","其他"
		};
		for (String type : wanles) {
			CircleTypeBo typeBo = new CircleTypeBo();
			typeBo.setCategory(type);
			typeBo.setPreCateg("玩乐");
			typeBo.setLevel(2);
			typeBo.setType(0);
			circleTypeDao.insert(typeBo);
		}

		String[] cars = new String[]{
				"车友","宝马","奔驰","凯美瑞","君威","福克斯","科鲁兹","昂科威","哈弗","其他"
		};
		for (String type : cars) {
			CircleTypeBo typeBo = new CircleTypeBo();
			typeBo.setCategory(type);
			typeBo.setPreCateg("车友");
			typeBo.setLevel(2);
			typeBo.setType(0);
			circleTypeDao.insert(typeBo);
		}

		String[] jobs = new String[]{
				"股票","金融","互联网"
		};
		for (String type : jobs) {
			CircleTypeBo typeBo = new CircleTypeBo();
			typeBo.setCategory(type);
			typeBo.setPreCateg("行业");
			typeBo.setLevel(2);
			typeBo.setType(0);
			circleTypeDao.insert(typeBo);
		}

		CircleTypeBo typeBo = new CircleTypeBo();
		typeBo.setCategory("其他");
		typeBo.setPreCateg("其他");
		typeBo.setLevel(2);
		typeBo.setType(0);
		circleTypeDao.insert(typeBo);
	}


	@Test
	public void addUserType(){

		String[] types = new String[]{
				"运动","音乐","生活","旅行足迹"
		};
		for (String type : types) {
			CircleTypeBo typeBo = new CircleTypeBo();
			typeBo.setCategory(type);
			typeBo.setLevel(1);
			typeBo.setType(1);
			circleTypeDao.insert(typeBo);
		}

		String[] sports = new String[]{
				"健身","快走","太极拳","跑步","乒乓球","羽毛球","郊游"
				,"游泳","爬山","台球","单车","瑜伽","篮球","足球","网球","高尔夫"
				,"滑板","滑雪","舞蹈","街舞","射箭","击剑","射击","拳击","骑马"
		};
		for (String type : sports) {
			CircleTypeBo typeBo = new CircleTypeBo();
			typeBo.setCategory(type);
			typeBo.setPreCateg("运动");
			typeBo.setLevel(2);
			typeBo.setType(1);
			circleTypeDao.insert(typeBo);
		}

		String[] musics = new String[]{
				"华语经典","民谣","舞曲","戏曲","对唱","红歌","粤语","古风","神曲","摇滚","动漫","嘻哈"
				,"R&B","外语经典","张国荣","张学友","刘德华","李宗盛","罗大佑","谭咏麟","蔡琴","陈红"
				,"费翔","费玉清","李谷一","齐秦","齐豫","蒋大为","李双江","筷子兄弟","凤凰传奇"
		};
		for (String type : musics) {
			CircleTypeBo typeBo = new CircleTypeBo();
			typeBo.setCategory(type);
			typeBo.setPreCateg("音乐");
			typeBo.setLevel(2);
			typeBo.setType(1);
			circleTypeDao.insert(typeBo);
		}

		String[] lifes = new String[]{
				"养生","旅游","宠物","茶文化","家居","花草","宗教"
				,"美食","母婴","美容","读书","购物交易","心理学","时尚"
		};
		for (String type : lifes) {
			CircleTypeBo typeBo = new CircleTypeBo();
			typeBo.setCategory(type);
			typeBo.setPreCateg("生活");
			typeBo.setLevel(2);
			typeBo.setType(1);
			circleTypeDao.insert(typeBo);
		}

		String[] trips = new String[]{
				"成都","桂林","三亚","青岛","丽江","厦门","湖南","重庆"
				,"北京","上海","广州","深圳","阳朔","漓江","杭州","大理","张家界","九寨沟"
				,"鼓浪屿","香格里拉","西藏","台湾","香港","日本","北海道","济州岛","新加坡","首尔"
		};
		for (String type : trips) {
			CircleTypeBo typeBo = new CircleTypeBo();
			typeBo.setCategory(type);
			typeBo.setPreCateg("旅行足迹");
			typeBo.setLevel(2);
			typeBo.setType(1);
			circleTypeDao.insert(typeBo);
		}
	}

	@Test
	public void findNear() {


		double[] position = new double[]{116.855425465695, 40.3682890329198};

		List<CircleBo> circleBos = circleDao.findNearCircle("",position, 10000, 10);

		for (CircleBo circleBo: circleBos) {
			System.out.println(JSON.toJSONString(circleBo));
		}

		List<CircleHistoryBo> historyBos = circleService.findNearPeople("5989cbd631f0a569e1dbfee6",
				"5989cb6231f0a569e1dbfee3", position, 10000);

		for (CircleHistoryBo circleBo: historyBos) {
			System.out.println(JSON.toJSONString(circleBo));
		}

	}

	@Test
	public void  level() throws Exception{

		List<BasicDBObject> objects = cityDao.findProvince();
		JSONObject proObject = new JSONObject();
		for (BasicDBObject object : objects) {
			String province = object.get("province").toString();
			Map<String , ArrayList<String>> citys = new LinkedHashMap<>();

			JSONArray ciArr = new JSONArray();
			if (province.equals("北京市") || province.equals("天津市") || province.equals("上海市")
					|| province.equals("重庆市")) {
				List<CityBo> cityBos = cityDao.findByParams(province, "");
				for (CityBo cityBo : cityBos) {
					ciArr.add(cityBo.getDistrit());
				}
				proObject.put(province, ciArr);
			} else {
				List<BasicDBObject> citObjs = cityDao.findCitys(province);
				JSONObject disObject = new JSONObject();
				for (BasicDBObject basicDBObject : citObjs) {
					String city = basicDBObject.get("city").toString();
					List<CityBo> cityBoDis = cityDao.findByParams(province, city, "");
					JSONArray disArr= new JSONArray();
					for (CityBo cityBo : cityBoDis) {
						disArr.add(cityBo.getDistrit());
					}
					disObject.put(city, disArr);
				}
				ciArr.add(disObject);
				proObject.put(province, ciArr);
			}
		}
		System.out.println(proObject.toString());
	}

	@Test
	public void citys() throws Exception{

		List<String> userid = new ArrayList<>();
		userid.add("59c1ea8c31f0a51f8c9d2e3b");
		userid.add("59c1eb3e31f0a51f8c9d2e3f");
		userid.add("59c36f3231f0a51f8c9d2e71");
		userid.add("59c51d5531f0a54366ff2441");

		Query query = new Query();
		query.addCriteria(new Criteria("_id").in(userid));
		List<UserBo> userBos = mongoTemplate.find(query, UserBo.class);
		for (UserBo userBo : userBos) {
			System.out.println(JSON.toJSONString(userBo));
		}
	}

	@Test
	public void msg(){

//		List<NoteBo> noteBos = mongoTemplate.findAll(NoteBo.class);
//
//		for (NoteBo noteBo : noteBos) {
//
//			String userid = noteBo.getCreateuid();
//
//			DynamicMsgBo msgBo = new DynamicMsgBo();
//			msgBo.setTargetid(noteBo.getId());
//			msgBo.setDynamicType(Constant.NOTE_TYPE);
//			msgBo.setUserid(userid);
//			msgBo.setCreateuid(userid);
//			dynamicService.addDynamicMsg(msgBo);
//
//			DynamicNumBo numBo = dynamicService.findNumByUserid(userid);
//			if (numBo == null) {
//				numBo = new DynamicNumBo();
//				numBo.setUserid(userid);
//				numBo.setNumber(1);
//				dynamicService.addNum(numBo);
//			} else {
//				dynamicService.updateNumbers(numBo.getId(), 1);
//			}
//		}



		List<CommentBo> commentBos = mongoTemplate.findAll(CommentBo.class);

		for (CommentBo commentBo : commentBos) {
			int type = commentBo.getType();
			if (type == Constant.NOTE_TYPE) {
				NoteBo noteBo = noteService.selectById(commentBo.getNoteid());
				DynamicNumBo numBo = dynamicService.findNumByUserid(noteBo.getCreateuid());
				if (numBo == null) {
					numBo = new DynamicNumBo();
					numBo.setUserid(noteBo.getCreateuid());
					numBo.setNumber(1);
					dynamicService.addNum(numBo);
				} else {
					dynamicService.updateNumbers(numBo.getId(), 1);
				}
			}
		}

		List<ThumbsupBo> thumbsupBos = mongoTemplate.findAll(ThumbsupBo.class);
		for (ThumbsupBo thumbsupBo : thumbsupBos) {
			int type = thumbsupBo.getType();
			if (type == Constant.NOTE_TYPE) {
				NoteBo noteBo = noteService.selectById(thumbsupBo.getOwner_id());
				DynamicNumBo numBo = dynamicService.findNumByUserid(noteBo.getCreateuid());
				if (numBo == null) {
					numBo = new DynamicNumBo();
					numBo.setUserid(noteBo.getCreateuid());
					numBo.setNumber(1);
					dynamicService.addNum(numBo);
				} else {
					dynamicService.updateNumbers(numBo.getId(), 1);
				}
			}
		}
	}

	@Test
	public void collect(){

//
//		IMTermBo termBo = iMTermService.selectByUserid("59d4878631f0a55478a38a9b");
//
//
//		String[] ss = IMUtil.unSubscribe("59eea94631f0a53f2a0f886f",
//				termBo.getTerm(),"59d4878631f0a55478a38a9b" );
//
//		System.out.println(ss[0]);
//		System.out.println("term : " + ss[1]);
//
//		System.out.println("=======================================");
//
//		System.out.println("term has : " + ss[1]);

		List<InforBo> inforBos = inforService.findClassInfos("四季保健", "2017-09-11", 10);
		for (InforBo inforBo : inforBos) {
			System.out.println("-------------------------------------");
			System.out.println(JSON.toJSONString(inforBo));

		}

//		InforBo inforBo = inforService.findById("59ed7d71d78a36518045c54f");
//		System.out.println(JSON.toJSONString(inforBo));
//
//
//		inforBo = inforService.findById("59fc9d22d78a360e1b209c64");
//		System.out.println(JSON.toJSONString(inforBo));


	}

	@Test
	public void test222(){



		ImAssistant assistent = ImAssistant.init("180.76.138.200", 2222);
		String res = "";
		if (assistent == null) {
			System.out.println("--------------------");
			return;
		}
//		Message message = assistent.getAppKey();
//		String appKey = message.getMsg();
//		System.out.println("status1 : " +message.getStatus());
//		Message message2 = assistent.authServer(appKey);
//		System.out.println("term : " +message2.getMsg());
//		System.out.println("status2 : " +message2.getStatus());
//		assistent.setServerTerm(message2.getMsg());
//		Message message3 = assistent.addUserToChatRoom("59e5da9031f0a549b6806c8e", "59c64e9331f0a5457e5f8986");
//		System.out.println("msg : " +message3.getMsg());
//		System.out.println("status3 : " +message3.getStatus());


		Message message = assistent.getAppKey();
		if(message.getStatus() != Message.Status.success){
			System.out.println("get appkey error");
			return;
		}

		message = assistent.authServer(message.getMsg());
		if(message.getStatus() != Message.Status.success){
			System.out.println("auth server error");
			return;
		}


		assistent.setServerTerm(message.getMsg());
//        message = assistent.createChatRoom("room100", "user889");
		message = assistent.addUserToChatRoom("room100", "uuid2");
		if(message.getStatus() != Message.Status.success){
			System.out.println("create room error");
			return;
		}
		assistent.close();

		System.out.println("success");


	}


	/**
	 *
	 */
	@Test
	public void removeCircle(){
//		UserBo userBo = userService.getUserByPhone("18681371968");
//		Query query = new Query();
//		query.addCriteria(new Criteria("_id").is(userBo.getId()));
//		Update update = new Update();
//		update.set("level", 6);
//		mongoTemplate.updateFirst(query, update, UserBo.class);


		List<UserBo> userBos = userDao.getAllUser();
		for (UserBo userBo : userBos) {
			HashSet<String> chatrooms = userBo.getChatrooms();
			HashSet<String> removes = new LinkedHashSet<>();
			LinkedList<String> chatroomTops = userBo.getChatroomsTop();

			System.out.println("userid : "+userBo.getId()+", username : " + userBo.getUserName());

			System.out.println("=== size1 : " + chatrooms.size() +" ; tops "+chatroomTops.size());

			LinkedList<String> removeTops = new LinkedList<>();
			for (String chatroomid : chatrooms) {
				ChatroomBo chatroomBo = chatroomService.get(chatroomid);
				if (chatroomBo == null) {
					System.out.println("---------------------- " + chatroomid + " : " +chatrooms.contains(chatroomid));
					removes.add(chatroomid);
				}
			}
			for (String chatroomid : chatroomTops) {
				ChatroomBo chatroomBo = chatroomService.get(chatroomid);
				if (chatroomBo == null) {
					System.out.println("---------------------- " + chatroomid);
					removeTops.add(chatroomid);
				}
			}
			chatrooms.removeAll(removes);
			chatroomTops.removeAll(removeTops);
			System.out.println("=== size2 : " + chatrooms.size() +" ; tops  "+chatroomTops.size());
			userBo.setChatrooms(chatrooms);
			userBo.setChatroomsTop(chatroomTops);
			userService.updateChatrooms(userBo);
		}






//		List<FriendsBo> friendsBos = mongoTemplate.findAll(FriendsBo.class);
//		for (FriendsBo friendsBo : friendsBos) {
//			Query query = new Query();
//			query.addCriteria(new Criteria("_id").is(friendsBo.getId()));
//			UserBo friend = userService.getUser(friendsBo.getFriendid());
//			UserBo userBo = userService.getUser(friendsBo.getUserid());
//			if (friend != null && userBo != null) {
//				if (StringUtils.isEmpty(friendsBo.getBackname())){
//					Update update = new Update();
//					update.set("backname", friend.getUserName());
//					mongoTemplate.updateFirst(query, update, FriendsBo.class);
//				}
//			} else {
//				mongoTemplate.remove(query, FriendsBo.class);
//			}
//		}

	}

	@Test
	public void test3(){


		UserBo userBo = userService.getUser("59d4878631f0a55478a38a9b");

		String userid = userBo.getId();
		HashSet<String> chatrooms = userBo.getChatrooms();
		LinkedList<String> chatroomsTop = userBo.getChatroomsTop();
		List<ChatroomVo> chatroomList = new LinkedList<ChatroomVo>();
		HashSet<String> removes = new LinkedHashSet<>();
		LinkedList<String> removeTops = new LinkedList<>();
		for (String id : chatroomsTop) {
			ChatroomBo temp = chatroomService.get(id);
			if (null != temp) {
				if (temp.getType() == Constant.ROOM_SINGLE) {
					continue;
				}
				ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userid, id);
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(temp, vo);
				if (temp.getType() != 1) {
					bo2vo(chatroomUserBo.isShowNick(),temp, vo);
					vo.setUserNum(temp.getUsers().size());
				}
				vo.setDisturb(chatroomUserBo.isDisturb());
				vo.setShowNick(chatroomUserBo.isShowNick());
				vo.setTop(1);
				chatroomList.add(vo);
			} else {
				removeTops.add(id);
			}
		}
		for (String id : chatrooms) {
			ChatroomBo temp = chatroomService.get(id);
			if (null != temp) {
				if (temp.getType() == Constant.ROOM_SINGLE) {
					continue;
				}
				ChatroomUserBo chatroomUserBo = chatroomService.findChatUserByUserAndRoomid(userid, id);
				ChatroomVo vo = new ChatroomVo();
				BeanUtils.copyProperties(temp, vo);
				if (temp.getType() != 1) {
					bo2vo(chatroomUserBo.isShowNick(), temp, vo);
					vo.setUserNum(temp.getUsers().size());
				}
				chatroomList.add(vo);
				vo.setDisturb(chatroomUserBo.isDisturb());
				vo.setShowNick(chatroomUserBo.isShowNick());
			} else {
				removes.add(id);
			}
		}
		updateUserRoom(userBo, removes, removeTops);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("ChatroomList", chatroomList);

		System.out.println(JSON.toJSONString(map));
	}

	private void updateUserRoom(UserBo userBo, HashSet<String> removes, LinkedList<String> removeTops){
		HashSet<String> chatrooms = userBo.getChatrooms();
		LinkedList<String> chatroomsTop = userBo.getChatroomsTop();
		chatroomsTop.removeAll(removeTops);
		chatrooms.removeAll(removes);
		userBo.setChatroomsTop(chatroomsTop);
		userBo.setChatrooms(chatrooms);
		userService.updateChatrooms(userBo);
	}

	private void bo2vo(boolean isShowNick, ChatroomBo chatroomBo, ChatroomVo vo){
		LinkedHashSet<ChatroomUserVo> userVos = vo.getUserVos();
		List<ChatroomUserBo> chatroomUserBos = chatroomService.findByUserRoomid(chatroomBo.getId());
		for (ChatroomUserBo chatroomUser : chatroomUserBos) {
			String userid = chatroomUser.getUserid();
			UserBo chatUser = userService.getUser(userid);
			if (chatUser == null) {
				chatroomService.deleteUser(chatroomUser.getId());
				continue;
			}
			ChatroomUserVo userVo = new ChatroomUserVo();
			userVo.setUserid(chatUser.getId());
			userVo.setUserPic(chatUser.getHeadPictureName());
			if (userid.equals(chatroomBo.getMaster())) {
				userVo.setRole(2);
			}
			String nickname = isShowNick ? chatroomUser.getNickname() : chatroomUser.getUsername();
			userVo.setNickname(nickname);
			userVos.add(userVo);
		}
	}

	@Test
	public void circleTest1(){

		UserBo userBo = userService.getUser("59c26cf631f0a51f8c9d2e46");
		String chatroomid = "59f9f02131f0a539cd3fddf9";

		HashSet<String> chatroom = userBo.getChatrooms();
		LinkedList<String> chatroomTops = userBo.getChatroomsTop();
		boolean hasRoom = false;
		if (chatroom.contains(chatroomid)){
			chatroom.remove(chatroomid);
			userBo.setChatrooms(chatroom);
			hasRoom = true;
		}
		if (chatroomTops.contains(chatroomid)){
			chatroomTops.remove(chatroomid);
			userBo.setChatroomsTop(chatroomTops);
			hasRoom = true;
		}
		System.out.println(hasRoom + "; " + chatroom.size());
		if (hasRoom){
			userService.updateChatrooms(userBo);
		}
	}


	@Test
	public void myTest(){

		List<BasicDBObject> objects = commentService.selectMyNoteReply("59c37cea31f0a51f8c9d2e79","",50 );
		List<NoteVo> noteVoList = new LinkedList<>();
		for (BasicDBObject object : objects) {
			String id = object.get("noteid").toString();
			NoteBo noteBo = noteService.selectById(id);
			System.out.println("===============" + noteBo.getCreateTime());
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("noteVoList", noteVoList);
		System.out.println(JSON.toJSONString(map));

		List<NoteBo> noteBos = noteService.selectMyNotes("59c37cea31f0a51f8c9d2e79", "", false, 10);

		for (NoteBo noteBo : noteBos) {
			System.out.println("-------------" + noteBo.getCreateTime());
		}
		System.out.println(JSON.toJSONString(map));

		noteBos = noteService.finyByCreateTime("59f7cd4031f0a52007710c0d","",false,50);
		for (NoteBo noteBo : noteBos) {
			System.out.println("-------------" + noteBo.getCreateTime());
		}
	}

	private void boToVo(NoteBo noteBo, NoteVo noteVo, UserBo userBo){
		BeanUtils.copyProperties(noteBo, noteVo);
		if (userBo!= null) {
			noteVo.setSex(userBo.getSex());
			noteVo.setBirthDay(userBo.getBirthDay());
			noteVo.setHeadPictureName(userBo.getHeadPictureName());
			noteVo.setUsername(userBo.getUserName());
			noteVo.setUserLevel(userBo.getLevel());
		}
		noteVo.setPosition(noteBo.getPosition());
		noteVo.setCommontCount(noteBo.getCommentcount());
		noteVo.setVisitCount(noteBo.getVisitcount());
		noteVo.setNodeid(noteBo.getId());
		noteVo.setTransCount(noteBo.getTranscount());
		noteVo.setThumpsubCount(noteBo.getThumpsubcount());
	}


	private void removeTag(String userid, String friendid, List<String> tags){
		List<TagBo> tagBoList = tagService.getTagBoListByUseridAndFrinedid(
				userid, friendid);
		if (tagBoList != null) {
			for (TagBo tagBo : tagBoList) {
				System.out.println("==========" + tagBo.getName());
				if (!tags.contains(tagBo.getName())){
					System.out.println("-----------" + tagBo.getName());
					LinkedHashSet<String> firendsSet = tagBo.getFriendsIds();
					firendsSet.remove(friendid);
					tagService.updateTagFriends(tagBo.getId(), firendsSet);
				}
			}
		}
	}


	@Test
	public void circle(){

		CircleBo circleBo = new CircleBo();
		circleBo.setPosition(new double[]{118.788345,32.029078});
		circleBo.setProvince("北京市");
		circleBo.setCity("北京市");
		circleBo.setDistrict("昌平区");
		circleBo.setDescription("测试圈子222");
		circleBo.setTag("交友");
		circleBo.setSub_tag("80后");
		circleBo.setName("圈子222");
		HashSet<String> users = circleBo.getUsers();
		users.add("59cfa38231f0a51d1e04741d");
		circleBo.setUsers(users);
		circleBo.setCreateuid("59cfa38231f0a51d1e04741d");
		circleBo.setUsernum(1);
		circleBo.setOpen(true);

		circleDao.insert(circleBo);

		circleBo = new CircleBo();
		circleBo.setPosition(new double[]{118.788343, 32.029075});
		circleBo.setProvince("北京市");
		circleBo.setCity("北京市");
		circleBo.setDistrict("东城区");
		circleBo.setDescription("测试圈子222");
		circleBo.setTag("运动");
		circleBo.setSub_tag("滑板");
		circleBo.setName("圈子222");
		users = circleBo.getUsers();
		users.add("59cfa42831f0a51d1e047420");
		circleBo.setUsers(users);
		circleBo.setCreateuid("59cfa42831f0a51d1e047420");
		circleBo.setUsernum(1);
		circleBo.setOpen(true);

		circleDao.insert(circleBo);

	}



}
