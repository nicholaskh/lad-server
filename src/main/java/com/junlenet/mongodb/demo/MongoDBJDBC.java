package com.junlenet.mongodb.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class MongoDBJDBC {
	
	
	private static MongoTemplate mongoTemplate;
	
	public static void main(String args[]) {
		try {
			
			/**cmd  ->>  bin 目录下面
			 net start HWJ_MONGODB
			 net stop HWJ_MONGODB
			 mongodb.log 该文件先要新建好,否则报错
			 * mongod.exe --dbpath d:\Program Files\MongoDB\data\db
			 *mongod.exe --bind_ip 127.0.0.1 --logpath "d:\Program Files\MongoDB\data\dbconfig\mongodb.log" --logappend --dbpath "d:\Program Files\MongoDB\data\db" --port 27017 --serviceName "hwj_mongodb" --serviceDisplayName "hwj_mongodb" --install
			  mongod.exe --auth --logpath "d:\Program Files\MongoDB\data\dbconfig\mongodb.log" --logappend --dbpath "d:\Program Files\MongoDB\data\db" --directoryperdb --reinstall
			 *
			 * 执行:\MongoDB\Server\3.2\bin\mongo.exe
			 db.createUser({user:'root',pwd:'123456',roles:[]})
			 db.auth('root','123456')
			 help  --帮助命令
			 ---
			 */
			// 连接到 mongodb 服务
			MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase("mongo_demo");
			System.out.println("Connect to database successfully");
			//mongoDatabase.createCollection("t_member");
			String collectionName = "t_member";
			MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
			Map<String, Object> map = null;
			Document document = null;
			for(int i=1;i<10000;i++){
				map = new HashMap<String, Object>();
				map.put("name","hwj_"+i);
				map.put("age",12+i);
				map.put("phone","110_"+i);
				map.put("phone2","110_"+i);
				map.put("phone4","110_"+i);
				map.put("phone3","110_"+i);
				map.put("phone5","110_"+i);
				map.put("phone6","110_"+i);
				map.put("phone7","110_"+i);
				map.put("phone8","110_"+i);
				map.put("phone16","110_"+i);
				map.put("phone17","110_"+i);
				map.put("phone18","110_"+i);
				map.put("phone26","110_"+i);
				map.put("phone37","110_"+i);
				map.put("phone48","110_"+i);
				document = new Document(map);
				collection.insertOne(document);
			}
			/*List<Document> documents = new ArrayList<Document>();
			documents.add(document);
			collection.insertMany(documents);*/
			
			//FindIterable<Document> findIterable = collection.find();
			FindIterable<Document> findIterable = collection.find(Filters.eq("name", "hwj_12"));
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document document2 = mongoCursor.next();
				System.out.println(JSONObject.toJSONString(document2));
			}
			//mongoTemplate.save("");
			//mongoTemplate.insert(objectToSave);
			//mongoTemplate.updateFirst(query, update, entityClass);
			
			Query query = new Query();
			Criteria criteria = Criteria.where("name").is("hwj_12120");
			query.addCriteria(criteria);
			query.skip(10);
			query.limit(10);
			List<Object> list = mongoTemplate.find(query, Object.class);
			System.out.println(list);
			//mongoTemplate.findAndRemove(query, Object.class);
			//mongoClient.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
}
