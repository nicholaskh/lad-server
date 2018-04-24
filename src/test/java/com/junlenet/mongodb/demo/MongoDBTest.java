package com.junlenet.mongodb.demo;

import com.alibaba.fastjson.JSON;
import com.lad.bo.*;
import com.lad.dao.*;
import com.lad.scrapybo.*;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.IMUtil;
import com.lad.vo.*;
import com.mongodb.BasicDBObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.text.ParseException;
import java.util.*;

@WebAppConfiguration
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
	private IPartyService partyService;


	@Autowired
	private ITagService tagService;

	@Autowired
	private IReasonService reasonService;

	@Autowired
	private IInforRecomService inforRecomService;



	@Before
	public void setUp() {

	}

	@Test
	public void getUsers() {


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

		LinkedHashSet<String> dailyTypes = new LinkedHashSet<>();
		List<DailynewsBo> dailynewsBos =  inforService.selectDailynewsGroups();
		for (DailynewsBo videoBo : dailynewsBos) {
			dailyTypes.add(videoBo.getId());
		}
		LinkedHashSet<String> yanglaoTypes = new LinkedHashSet<>();
		List<YanglaoBo> yanglaoBos =  inforService.selectYanglaoGroups();
		for (YanglaoBo videoBo : yanglaoBos) {
			yanglaoTypes.add(videoBo.getId());
		}
		List<InforSubscriptionBo> subscriptionBos =  mongoTemplate.findAll(InforSubscriptionBo.class);
		for (InforSubscriptionBo videoBo : subscriptionBos) {
			videoBo.setDailys(dailyTypes);
			videoBo.setYanglaos(yanglaoTypes);
			mongoTemplate.save(videoBo);
		}
	}


	@Test
	public void getNote() {
		List<InforGroupRecomBo> myRecomBos = inforRecomService.findInforGroupWithoutModule(Constant.INFOR_RADIO,
				null, 50);

		LinkedHashSet<String> modules = new LinkedHashSet<>();
		LinkedHashSet<String> classNames = new LinkedHashSet<>();
		JSONArray array = new JSONArray();
		int num = 0;
		for (InforGroupRecomBo recomBo : myRecomBos) {
			modules.add(recomBo.getModule());
			classNames.add(recomBo.getClassName());
			System.out.println(JSON.toJSONString(recomBo));
			num ++;
		}
		List<BroadcastBo> radioBos = inforService.selectRadioClassByGroups(modules, classNames);
		addRadios(radioBos, array);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ret", 0);
		jsonObject.put("radioClasses", array);
		System.out.println(jsonObject.toString());

	}

	private void addRadios(List<BroadcastBo> broadcastBos, JSONArray array){
		if (broadcastBos == null) {
			return;
		}
		for (BroadcastBo bo : broadcastBos) {
			JSONObject object = new JSONObject();
			object.put("module", bo.getModule());
			object.put("title", bo.getClassName());
			object.put("source", bo.getSource());
			object.put("intro", bo.getIntro());
			object.put("totalVisit", bo.getVisitNum());
			array.add(object);
		}
	}


	/**
	 * 索引创建后测试
	 */
	@Test
	public void save() {
//
//		List<ReasonBo> reasonBos = mongoTemplate.findAll(ReasonBo.class);
//		List<String> lists = new ArrayList<>();
//		for (ReasonBo reasonBo : reasonBos) {
//			String res = reasonBo.getCircleid() + reasonBo.getCreateuid();
//			if (lists.contains(res)) {
//				Query query = new Query();
//				query.addCriteria(new Criteria("_id").is(reasonBo.getId()));
//				mongoTemplate.remove(query, ReasonBo.class);
//				continue;
//			}
//			lists.add(res);
//		}

//
		List<CircleBo> circleBos = mongoTemplate.findAll(CircleBo.class);
		for (CircleBo circleBo : circleBos) {
			if (circleBo.getDeleted() == 1) {
				continue;
			}
			String id = circleBo.getId();
			HashSet<String> users = circleBo.getUsers();
			for (String userid : users) {
				ReasonBo reasonBo = reasonService.findByUserAdd(userid, id);
				if (reasonBo != null) {
					if (reasonBo.getStatus() != 1) {
						System.out.println("========== userid " + userid + ", cirlceid " +id);
						reasonService.updateApply(reasonBo.getId(), 1, "");
					}
				} else {
					reasonBo = new ReasonBo();
					reasonBo.setCreateuid(userid);
					reasonBo.setStatus(1);
					reasonBo.setCircleid(id);
					reasonBo.setAddType(0);
					reasonBo.setUnReadNum(1);
					reasonService.insert(reasonBo);
					System.out.println("************** userid " + userid + ", cirlceid " +id);
				}
			}
		}
	}



	@Test
	public void circleTest() {

//		List<InforReadNumBo> readNumBos = mongoTemplate.findAll(InforReadNumBo.class);
//		for (InforReadNumBo readNumBo : readNumBos){
//			String inforid = readNumBo.getInforid();
//			inforService.updateInforNum(inforid, Constant.COMMENT_NUM, (int)readNumBo.getVisitNum());
//		}

		List<ThumbsupBo> thumbsupBos = mongoTemplate.findAll(ThumbsupBo.class);
		for (ThumbsupBo thumbsupBo : thumbsupBos) {
			if (thumbsupBo.getDeleted() == 1) {
				continue;
			}
			if (thumbsupBo.getType() == Constant.INFOR_TYPE) {
				String inforid = thumbsupBo.getOwner_id();
				if (null != inforService.findById(inforid)) {
					inforService.updateInforNum(thumbsupBo.getOwner_id(), Constant.THUMPSUB_NUM, 1);
				} else if (null != inforService.findSecurityById(inforid)) {
					inforService.updateSecurityNum(thumbsupBo.getOwner_id(), Constant.THUMPSUB_NUM, 1);
				} else if (null != inforService.findBroadById(inforid)) {
					inforService.updateRadioNum(thumbsupBo.getOwner_id(), Constant.THUMPSUB_NUM, 1);
				} else {
					inforService.updateVideoNum(thumbsupBo.getOwner_id(), Constant.THUMPSUB_NUM, 1);
				}
			}
		}

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
	public void myCircleTest() {

		List<UserLevelBo> userLevelBos = mongoTemplate.findAll(UserLevelBo.class);

		int num = 0;
		userLevelBos.forEach( userLevelBo -> {
			Query query = new Query();
			query.addCriteria(new Criteria("_id").is(userLevelBo.getId()));
			Update update = new Update();
			update.inc("onlineHours", num);
			update.inc("launchPartys", num);
			update.inc("noteNum", num);
				update.inc("commentNum", num);
				update.inc("transmitNum", num);
				update.inc("shareNum", num);
				update.inc("circleNum", num);

			mongoTemplate.updateFirst(query, update, UserLevelBo.class);
		});
	}


	@Test
	public void addApply() {
		String keyword = "队长";
		String city = "成都市";
		List<CircleBo> circleBos = circleService.findBykeyword(keyword, city, 1, 10);
		for (CircleBo circleBo : circleBos) {
			System.out.println(JSON.toJSONString(circleBo));
		}
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

		String module = "节目展演";

		String className = "CCTV空中剧院";

		long start = System.currentTimeMillis();

		List<VideoBo> videoBos = inforService.selectClassNamePage(module, className, 1, 50);

		long end = System.currentTimeMillis();

		
	}


	@Test
	public void delete() {
		List<NoteBo> noteBos = mongoTemplate.findAll(NoteBo.class);
		for (NoteBo noteBo : noteBos) {
			if (noteBo.getDeleted() == 1) {
				continue;
			}
			List<CommentBo> commentBos = commentService.selectByNoteid(noteBo.getId(), 1, 100);
			if (commentBos != null && noteBo.getCommentcount() != commentBos.size()) {
				noteService.updateCommentCount(noteBo.getId(), commentBos.size());
			}
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

		Point point = new Point(position[0],position[1]);
		Query query = new Query();
		Distance distance = new Distance(10000/1000, Metrics.KILOMETERS);

		NearQuery near =NearQuery.near(point);
		near.maxDistance(distance);
		GeoResults<CircleBo> circleBoGeoResult =  mongoTemplate.geoNear(near, CircleBo.class);

		for (GeoResult<CircleBo> geoResult : circleBoGeoResult) {
			System.out.println(JSON.toJSONString(geoResult.getContent()));
			System.out.println(geoResult.getDistance().getValue());
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

		Query query = new Query();
		query.addCriteria(new Criteria("type").is(Constant.INFOR_TYPE));
		List<CommentBo> commentBos = mongoTemplate.find(query, CommentBo.class);
		for (CommentBo commentBo : commentBos) {
			int inforType = commentBo.getSubType();
			String inforid = commentBo.getTargetid();
			boolean hasType = false;
			switch (inforType){
				case Constant.INFOR_HEALTH:
					Query query1 = new Query();
					query1.addCriteria(new Criteria("_id").is(inforid));
					Update update = new Update();
					update.set("commnetNum",0);
					mongoTemplateTwo.updateFirst(query1, update, InforBo.class);
					break;
				case Constant.INFOR_SECRITY:
					Query query2 = new Query();
					query2.addCriteria(new Criteria("_id").is(inforid));
					Update update1 = new Update();
					update1.set("commnetNum",0);
					mongoTemplateTwo.updateFirst(query2, update1, SecurityBo.class);
					break;
				case Constant.INFOR_RADIO:
					Query query3 = new Query();
					query3.addCriteria(new Criteria("_id").is(inforid));
					Update update2 = new Update();
					update2.set("commnetNum",0);
					mongoTemplateTwo.updateFirst(query3, update2, BroadcastBo.class);
					break;
				case Constant.INFOR_VIDEO:
					Query query4 = new Query();
					query4.addCriteria(new Criteria("_id").is(inforid));
					Update update3 = new Update();
					update3.set("commnetNum",0);
					mongoTemplateTwo.updateFirst(query4, update3, VideoBo.class);
					break;
				default:
					hasType = true;
					break;
			}
			if (hasType) {
				InforBo inforBo = inforService.findById(inforid);
				if (inforBo == null) {
				 	SecurityBo securityBo = inforService.findSecurityById(inforid);
				 	if (securityBo == null) {
				 		BroadcastBo broadcastBo = inforService.findBroadById(inforid);
				 		if (broadcastBo == null) {
				 			VideoBo videoBo = inforService.findVideoById(inforid);
				 			if (videoBo  == null) {
								System.out.println("--------------------------" + commentBo.getId());
							} else {
								Query query4 = new Query();
								query4.addCriteria(new Criteria("_id").is(inforid));
								Update update3 = new Update();
								update3.set("commnetNum",0);
								mongoTemplateTwo.updateFirst(query4, update3, VideoBo.class);
							}
						} else {
							Query query3 = new Query();
							query3.addCriteria(new Criteria("_id").is(inforid));
							Update update2 = new Update();
							update2.set("commnetNum",0);
							mongoTemplateTwo.updateFirst(query3, update2, BroadcastBo.class);
						}
					} else {
						Query query2 = new Query();
						query2.addCriteria(new Criteria("_id").is(inforid));
						Update update1 = new Update();
						update1.set("commnetNum",0);
						mongoTemplateTwo.updateFirst(query2, update1, SecurityBo.class);
					}
				} else {
					 Query query1 = new Query();
					 query1.addCriteria(new Criteria("_id").is(inforid));
					 Update update = new Update();
					 update.set("commnetNum",0);
					 mongoTemplateTwo.updateFirst(query1, update, InforBo.class);
				 }
			}
			System.out.println(inforType);
			System.out.println("==================".concat(commentBo.getTargetid()));
			commentService.delete(commentBo.getId());
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

		String userid = "59d98be831f0a57ce97a5219";
		boolean isNew = false;
		int type = Constant.INFOR_RADIO;

		String module = "戏曲大全";

		InforUserReadBo readBo = inforRecomService.findUserReadByUserid(userid);
		if (readBo == null) {
			readBo = new InforUserReadBo();
			readBo.setUserid(userid);
			isNew = true;
		}
		LinkedHashSet<String> sets = null;
		if (Constant.INFOR_HEALTH == type) {
			sets = readBo.getHealths();
		} else if (Constant.INFOR_SECRITY == type) {
			sets = readBo.getSecuritys();
		} else if (Constant.INFOR_RADIO == type) {
			sets= readBo.getRadios();
		} else if (Constant.INFOR_VIDEO == type) {
			sets = readBo.getVideos();
		} else {
			sets = new LinkedHashSet<>();
		}
		if (isNew) {
			sets.add(module);
			inforRecomService.addUserRead(readBo);
		} else {
			if (!sets.contains(module)){
				sets.add(module);
				inforRecomService.updateUserRead(readBo.getId(), type, sets);
				//更新其他过时分类
			}
		}
	}

	@Test
	public void test222(){

		UserBo userBo = userDao.getUserByPhone("18228148133");

		System.out.println(userBo.getId());

		FriendsBo friendsBo = friendsService.get("5ad9c15131f0a56b965fd406");
		if (friendsBo == null) {
			System.out.println("=============");
		}
		FriendsBo friend = friendsService.getFriendByIdAndVisitorIdAgree(friendsBo.getFriendid(), userBo.getId());
		if (friend == null) {
			System.out.println("=============");
		}

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
	public void circleTest1() throws  Exception{
//
//		String module = "节目展演";
//		String className = "第二届中老年模特大赛";
////		List<VideoBo> broadcastBos = inforService.selectVideoClassByGroups(module);
//		List<VideoBo> videoBos = inforService.selectClassNamePage(module,className, 1, 10);
//		for (VideoBo bo : videoBos) {
//			System.out.println(JSON.toJSONString(bo));
//		}


		List<NoteBo> noteBos = mongoTemplate.findAll(NoteBo.class);
		for (NoteBo noteBo : noteBos) {
			if (noteBo.getDeleted() == 0) {
				CircleShowBo circleShowBo = new CircleShowBo();
				circleShowBo.setCircleid(noteBo.getCircleId());
				circleShowBo.setTargetid(noteBo.getId());
				circleShowBo.setType(0);
				circleShowBo.setCreateTime(noteBo.getCreateTime());
				circleService.addCircleShow(circleShowBo);
				Thread.sleep(50);
			}
		}
		List<PartyBo> partyBos = mongoTemplate.findAll(PartyBo.class);
		for (PartyBo partyBo : partyBos) {
			if (partyBo.getDeleted() == 0) {

				CircleShowBo circleShowBo = new CircleShowBo();
				circleShowBo.setCircleid(partyBo.getCircleid());
				circleShowBo.setTargetid(partyBo.getId());
				circleShowBo.setType(1);
				circleShowBo.setCreateTime(partyBo.getCreateTime());
				circleService.addCircleShow(circleShowBo);
				Thread.sleep(50);
			}
		}

	}


	@Test
	public void myTest(){



		double px = 104.102167;
		double py = 30.644147;

		List<LocationBo> locationBoList = locationService.findCircleNear(px, py, 5000);
		System.out.println("----------" + locationBoList.size());
		for (LocationBo bo : locationBoList) {
			String userid = bo.getUserid();
			UserBo temp = userService.getUser(userid);
			System.out.println(JSON.toJSONString(bo));
			if (temp != null) {
				System.out.println("======================");
				System.out.println(JSON.toJSONString(temp.getId()));
			}
		}

//		noteBos = noteService.finyByCreateTime("59f7cd4031f0a52007710c0d","",false,50);
//		for (NoteBo noteBo : noteBos) {
//			System.out.println("-------------" + noteBo.getCreateTime());
//		}
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


	@Test
	public void removeTag(){
//		List<BroadcastBo> broadcastBos = inforService.selectBroadClassByGroups("中医养生");
//		for (BroadcastBo broadcastBo : broadcastBos) {
//			System.out.println(JSON.toJSONString(broadcastBo));
//		}


		List<FriendsBo> friendsBos = mongoTemplate.findAll(FriendsBo.class);

		for (FriendsBo friendsBo : friendsBos) {
			if (friendsBo.getApply() != 1 || friendsBo.getDeleted() == 1) {
				continue;
			}
			UserBo userBo = userService.getUser(friendsBo.getFriendid());
			if (userBo == null) {
				continue;
			}
			Update update = new Update();
			update.set("friendHeadPic", userBo.getHeadPictureName());
			update.set("username", userBo.getUserName());
			//如果当初没有创建成功
			ChatroomBo chatroomBo = chatroomService.selectByUserIdAndFriendid(
					friendsBo.getUserid(), friendsBo.getFriendid());
			if (chatroomBo == null) {
				chatroomBo = chatroomService.selectByUserIdAndFriendid(
						friendsBo.getFriendid(), friendsBo.getUserid());
			}
			if (chatroomBo != null) {
				update.set("chatroomid", chatroomBo.getId());
			}
			Query query = new Query();
			query.addCriteria(new Criteria("_id").is(friendsBo.getId()));
			mongoTemplate.updateFirst(query, update, FriendsBo.class);
		}

		

	}


	@Test
	public void circle(){

		String timestamp = "0";
		
//		String userid = "59c37cea31f0a51f8c9d2e79";
//		List<FriendsVo> voList = new LinkedList<>();
//		String timeStr = "";
//		try {
//			Date times = CommonUtil.getDate(timestamp);
//			List<FriendsBo> list = friendsService.getFriendByUserid(userid, times);
//			if (!CommonUtil.isEmpty(list)) {
//				FriendsBo first = list.get(0);
//				timeStr = CommonUtil.getDateStr(first.getUpdateTime(),"yyyy-MM-dd HH:mm:ss");
//			}
//			for (FriendsBo friendsBo : list) {
//				FriendsVo vo = new FriendsVo();
//				BeanUtils.copyProperties(friendsBo, vo);
//				String friendid = friendsBo.getFriendid();
//				List<TagBo> tagBos = tagService.getTagBoListByUseridAndFrinedid(userid, friendid);
//				List<String> tagList = new ArrayList<>();
//				for (TagBo tagBo : tagBos) {
//					tagList.add(tagBo.getName());
//				}
//				vo.setPicture(friendsBo.getFriendHeadPic());
//				vo.setTag(tagList);
//				if (StringUtils.isEmpty(friendsBo.getBackname())) {
//					UserBo friend = userService.getUser(friendid);
//					vo.setBackname(friend.getUserName());
//					vo.setUsername(friend.getUserName());
//				} else {
//					vo.setBackname(friendsBo.getBackname());
//					vo.setUsername(friendsBo.getUsername());
//				}
//					vo.setChannelId(friendsBo.getChatroomid());
//				voList.add(vo);
//			}
//		} catch (ParseException e){
//			e.printStackTrace();
//		}
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("ret", 0);
//		map.put("timestamp", StringUtils.isNotEmpty(timeStr) ? timeStr : timestamp);
//		map.put("tag", voList);


		String[] phones = new String[]{"13716057225", "17611156225"};

		List<UserBaseVo> userBaseVos = new ArrayList<>();
		List<String> phoneList = new ArrayList<>();
		String timeStr = "";
		if(null != phones) {
			Collections.addAll(phoneList, phones);
			try {
				Date times = CommonUtil.getDate(timestamp);
				List<UserBo> userBos = userService.getUserByPhoneAndTime(phoneList, times);
				if (!CommonUtil.isEmpty(userBos)) {
					UserBo first = userBos.get(0);
					timeStr = CommonUtil.getDateStr(first.getCreateTime(),"yyyy-MM-dd HH:mm:ss");
					for (UserBo userBo : userBos) {
						UserBaseVo baseVo = new UserBaseVo();
						BeanUtils.copyProperties(userBo, baseVo);
						userBaseVos.add(baseVo);
					}
				}
			} catch (ParseException e){
				e.printStackTrace();
			}
		}
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("ret", 0);
		map.put("timestamp", StringUtils.isNotEmpty(timeStr) ? timeStr : timestamp);
		map.put("userVos", userBaseVos);

		System.out.println(JSON.toJSONString(map));
	}



}
