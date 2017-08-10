package com.junlenet.mongodb.demo;

import com.alibaba.fastjson.JSON;
import com.lad.bo.*;
import com.lad.dao.*;
import com.lad.scrapybo.InforBo;
import com.lad.service.ICircleService;
import com.lad.service.IUserService;
import com.lad.util.Constant;
import com.lad.vo.CircleVo;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    private IInforDao inforDao;


	@Before
	public void setUp() {

	}

    @Test
    public void getUsers(){
        List<UserBo> userBos = userDao.getAllUser();

        for (UserBo userBo:userBos) {
            System.out.println(JSON.toJSONString(userBo));
        }

    }

	@Test
	public void addReds(){
		List<RedstarBo> redstarBos = redstarDao.findRedTotal("594d364f31f0a560fb6a4b5f");
		for (RedstarBo redstarBo : redstarBos) {
			redstarDao.updateRedWeekByUser(redstarBo.getUserid(),redstarBo.getWeekNo(), 2017 );
		}
	}

	@Test
	public void getReds(){

		List<RedstarBo> redstarBos = userService.findRedUserTotal("594d364f31f0a560fb6a4b5f");
		for (RedstarBo redstarBo : redstarBos) {
			UserBo userBo = userService.getUser(redstarBo.getUserid());
			System.out.println(JSON.toJSONString(userBo));
		}


	}

	@Test
	public void addNote(){
		NoteBo noteBo = new NoteBo();
		noteBo.setCircleId("594d364f31f0a560fb6a4b5f");
		noteBo.setCreateuid("594f5bb931f0a567340921e8");
		noteBo.setSubject("redis user and hahah");
		noteBo.setContent("BSON documents may have more than one field with the same name. Most MongoDB interfaces, " +
				"however, represent MongoDB with a structure (e.g. a hash table) that does not support duplicate field " +
				"names. If you need to manipulate documents that have more than one field with the same name, see the driver documentation for your driver.\n"
				+ "\n" + "Some documents created by internal MongoDB processes may have duplicate fields, but no MongoDB process will ever add duplicate fields to an existing user document.");
		noteBo.setVisitcount(1999);
		noteBo.setCommentcount(104);
		noteBo.setThumpsubcount(18);

		LinkedList<String> photos = new LinkedList<>();
		photos.add("picture1.jsp");
		photos.add("picture2.jsp");

		noteBo.setPhotos(photos);
		noteBo.setTemp(0);
		noteDao.insert(noteBo);
	}


	@Test
	public void getNote(){

//		List<NoteBo> noteBos = noteDao.selectHotNotes("594d364f31f0a560fb6a4b5f");

		noteDao.deleteNote("5975a62531f0a5576088996e");
		
		List<NoteBo> noteBos = noteDao.selectHotNotes("594f765a31f0a567340921f3");
		for (NoteBo noteB : noteBos) {
			System.out.println(JSON.toJSONString(noteB));
		}

//		NoteBo noteBo = noteDao.selectById("596cf26431f0a515641c5c8b");
//		System.out.println(JSON.toJSONString(noteBo));

		List<NoteBo> noteBoss = noteDao.finyByCreateTime("594f765a31f0a567340921f3","", false, 10);
		for (NoteBo noteB : noteBoss) {
			System.out.println("============");
			System.out.println(JSON.toJSONString(noteB));
		}

//		List<NoteBo> noteBoss = noteDao.selectByComment("596cf2a831f0a515641c5c8e");
////
//		List<NoteBo> noteBoss = noteDao.selectByVisit("594d364f31f0a560fb6a4b5f");
//		for (NoteBo noteBo : noteBoss) {
//			System.out.println(JSON.toJSONString(noteBo));
//		}


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
		chatroomBo.setPosition(new double[]{118.639523,32.070078});
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

		double[] position = new double[]{118.788135,32.029064};
		boolean res = chatroomDao.withInRange("59451b49d75bc2118c082c6c", position, 2000);
		System.out.println(res);
	}

	@Test
	public void circleTest(){
        List<CircleBo> circleBos = circleDao.selectUsersPre("");
        System.out.println(circleBos.size());
        for (CircleBo circleBo : circleBos) {
            System.out.println(JSON.toJSONString(circleBo));
        }
    }

    @Test
    public void myCircleTest(){
        List<CircleBo> circleBos = circleDao.findMyCircles(
                "595514db31f0a503a5a4225d", "", false, 10);
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
    public void addApply(){

		HashSet<String> usersApply = new HashSet<>();

    	String circleid = "594f765a31f0a567340921f3";

    	String userid = "594daa0e31f0a560fb6a4b73";
		ReasonBo reasonBo = new ReasonBo();
		reasonBo.setCircleid(circleid);
		reasonBo.setReason("我要加入群");
		reasonBo.setCreateuid(userid);
		usersApply.add(userid);
		reasonBo.setStatus(Constant.ADD_APPLY);
		circleService.updateUsersApply(circleid, usersApply);
		circleService.insertApplyReason(reasonBo);


		userid = "594d1a7031f0a560fb6a4b4d";
		reasonBo = new ReasonBo();
		reasonBo.setCircleid(circleid);
		reasonBo.setReason("我要加入群");
		reasonBo.setCreateuid(userid);
		usersApply.add(userid);
		reasonBo.setStatus(Constant.ADD_APPLY);
		circleService.updateUsersApply(circleid, usersApply);
		circleService.insertApplyReason(reasonBo);

		userid = "594d355631f0a560fb6a4b5a";
		reasonBo = new ReasonBo();
		reasonBo.setCircleid(circleid);
		reasonBo.setReason("我要加入群");
		reasonBo.setCreateuid(userid);
		usersApply.add(userid);
		reasonBo.setStatus(Constant.ADD_APPLY);
		circleService.updateUsersApply(circleid, usersApply);
		circleService.insertApplyReason(reasonBo);

		userid = "594d324731f0a560fb6a4b50";
		reasonBo = new ReasonBo();
		reasonBo.setCircleid(circleid);
		reasonBo.setReason("我要加入群");
		reasonBo.setCreateuid(userid);
		usersApply.add(userid);
		reasonBo.setStatus(Constant.ADD_APPLY);
		circleService.updateUsersApply(circleid, usersApply);
		circleService.insertApplyReason(reasonBo);

		userid = "594dbabf31f0a560fb6a4b76";
		reasonBo = new ReasonBo();
		reasonBo.setCircleid(circleid);
		reasonBo.setReason("我要加入群");
		reasonBo.setCreateuid(userid);
		usersApply.add(userid);
		reasonBo.setStatus(Constant.ADD_APPLY);
		circleService.updateUsersApply(circleid, usersApply);
		circleService.insertApplyReason(reasonBo);
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
		circleVo.setUsersSize((long) circleBo.getUsers().size());
		circleVo.setNotesSize((long) circleBo.getNotes().size());
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
    public void addPe(){




//        CircleBo circleBo =  circleDao.selectById("594f765a31f0a567340921f3");
//		circleBo.setCreateuid("595514db31f0a503a5a4225d");
//		circleBo.setName("这个圈子是要测试的");
//		HashSet<String> users = circleBo.getUsers();
//		users.add("595514db31f0a503a5a4225d");
//		circleBo.setUsers(users);
//		circleDao.uddateName(circleBo.getId(),circleBo.getName());

		CircleBo circleBo =  circleDao.selectById("594f765a31f0a567340921f3");
		HashSet<String> users = circleBo.getUsers();
		List<UserBo> userBos = userDao.getAllUser();
		HashSet<String> usersApply = circleBo.getUsersApply();
		for (UserBo userBo : userBos) {
			if (!users.contains(userBo.getId())) {
				ReasonBo reasonBo = new ReasonBo();
				reasonBo.setCircleid(circleBo.getId());
				reasonBo.setReason("我要加入群");
				reasonBo.setCreateuid(userBo.getId());
				usersApply.add(userBo.getId());
				reasonBo.setStatus(Constant.ADD_APPLY);
				circleService.updateUsersApply(circleBo.getId(), usersApply);
				circleService.insertApplyReason(reasonBo);
			}
			System.out.println(JSON.toJSONString(userBo));
		}

    }


    @Test
    public void tst(){
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
	public void infos(){
		List<InforBo> inforBos = inforDao.selectAllInfos();

		for (InforBo inforBo : inforBos) {
			System.out.println(JSON.toJSONString(inforBo));
		}
		System.out.println("----------------------");
//		List<InforBo> infors = inforDao.findByList("健康新知");
//
//		for (InforBo inforBo : infors) {
//			System.out.println(JSON.toJSONString(inforBo));
//		}

	}



}
