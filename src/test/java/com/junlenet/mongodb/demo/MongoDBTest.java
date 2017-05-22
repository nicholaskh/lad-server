package com.junlenet.mongodb.demo;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lad.bo.LocationBo;
import com.lad.dao.impl.LocationDaoImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml",
		"classpath:spring-mvc.xml" })
public class MongoDBTest {

	@Autowired
	LocationDaoImpl locationDao;

	@Autowired
	MongoTemplate template;

	@Before
	public void setUp() {

		// 等同db.location.ensureIndex( {position: "2dsphere"} )
		
//		template.indexOps(Location.class).ensureIndex(
//				new GeospatialIndex("position"));

		// 初始化数据db.location.ensureIndex( {GeoIndex_2d: "2d"} )

		template.save(new LocationBo("A", 0.1, -0.1));

		template.save(new LocationBo("B", 1, 1));

		template.save(new LocationBo("C", 0.5, 0.5));

		template.save(new LocationBo("D", -0.5, -0.5));

	}

	@Test
	public void findCircleNearTest() {

		List<LocationBo> locations = locationDao.findCircleNear(new Point(0, 0),
				0.75);
		for (LocationBo item : locations) {
			System.out.println(item.getUserid());
		}

		System.out.println("-----------------------");
	}

//	@Test
//	public void findBoxNearTest() {
//
//		List<Location> locations = locationDao.findBoxNear(new Point(0.2, 0.2),
//				new Point(1, 1));
//
//		System.out.println(locations);
//
//	}
//
//	public static void print(Collection<Location> locations) {
//
//		for (Location location : locations) {
//
//			System.err.println(location);
//
//		}
//
//	}

}
