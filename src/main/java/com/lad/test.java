package com.lad;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class test {

	@Autowired
	private MongoTemplate mongoTemplate;
	/**
     * 转换为GeoJSON对象
     */
    public  String getGeoJson(String lon,String lat) throws JSONException{
          JSONObject point = new JSONObject();
      point.put( "type", "Point");
      String coord = "["+lon+ ","+lat+ "]";
      point.put( "coordinates", coord);
      return point.toString();
    }
    
    /**
     * 插入空间数据
     * @param collection
     * @throws JSONException
     */
    public void insert(DBCollection collection) throws JSONException{
          DBObject point1 = new BasicDBObject();
          point1.put( "name", "001");
          point1.put( "geo", getGeoJson( "116.342176", "39.995376"));
          
          DBObject point2 = new BasicDBObject();
          point2.put( "name", "002");
          point2.put( "geo", getGeoJson( "116.348694", "39.990965"));
          
          DBObject point3 = new BasicDBObject();
          point3.put( "name", "003");
          point3.put( "geo", getGeoJson( "116.343318", "39.991184"));
          
          DBObject point4 = new BasicDBObject();
          point4.put( "name", "004");
          point4.put( "geo", getGeoJson( "116.359590", "39.982762"));
          List<DBObject> list = new ArrayList<DBObject>();
          list.add(point1);
          list.add(point2);
          list.add(point3);
          list.add(point4);
          collection.insert(list).getN();
    }
    
    /**
     * 创建空间索引
     * @param collection
     */
    public void makeSpatialIndexs(DBCollection collection){
          collection.createIndex( new BasicDBObject( "geo", "2dsphere"), "geospatialIdx");
    }
    
    /**
     * 查询矩形内的所有要素
     * @param collection
     */
    public void query(DBCollection collection){
          List<Double[]> box = new ArrayList<Double[]>();
          box.add( new Double[] {116.341795,39.992277}); //左上角 coordinate
          box.add( new Double[]{116.350122,39.989251}); // 右下角 coordinate
          BasicDBObject query = new BasicDBObject( "loc", new BasicDBObject("$within",new BasicDBObject("$box" , box)));
          DBCursor cur1 = collection.find(query);
           while (cur1.hasNext()) {
                BasicDBObject o = (BasicDBObject) cur1.next();
                System. out.println(o.get( "name"));
                System. out.println(o.get( "geometry"));
          }
    }	
    
    public static void main(String[] args){
    	test test = new test();
    	
//    	DBCollection DBCollection = new DBCollection();
    }
	
	
}
