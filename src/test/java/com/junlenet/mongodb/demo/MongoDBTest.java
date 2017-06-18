package com.junlenet.mongodb.demo;

import com.lad.bo.ChatroomBo;
import com.lad.dao.IChatroomDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml",
		"classpath:spring-mvc.xml" })
public class MongoDBTest {

    @Autowired
    private IChatroomDao chatroomDao;

	@Before
	public void setUp() {
       


		// 等同db.location.ensureIndex( {position: "2dsphere"} )

//		template.indexOps(Location.class).ensureIndex(
//				new GeospatialIndex("position"));

		// 初始化数据db.location.ensureIndex( {GeoIndex_2d: "2d"} )

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

}
