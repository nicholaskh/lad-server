package com.lad.init;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.lad.bo.ExposeBo;
import com.lad.scrapybo.InforBo;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;

public class InitProgram {
	@Autowired
	@Qualifier("mongoTemplate")
	private MongoTemplate mongoTemplate;

	@Autowired
	@Qualifier("mongoTemplateTwo")
	private MongoTemplate mongoTemplateTwo;

	private Logger logger = LoggerFactory.getLogger(InitProgram.class);

	public void init() {
		final long timeInterval = 1000 * 60 * 60 * 24;
		Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();

		for (Thread iterable_element : allStackTraces.keySet()) {
			if ("synInforDataToExp".equals(iterable_element.getName())) {
				return;
			}
		}
		Runnable runnable = new Runnable() {
			public void run() {

				Thread.currentThread().setName("synInforDataToExp");

				Query query = new Query();
				Criteria orcriteria = new Criteria();
				orcriteria.orOperator(Criteria.where("synToExp").exists(false), Criteria.where("synToExp").is(0));
				Criteria criteria = new Criteria();
				criteria.andOperator(Criteria.where("className").is("曝光台"), orcriteria);
				query.addCriteria(criteria);
				Integer limit = 1000;
				ExposeBo exposeBo = new ExposeBo();
				while (true) {
					try {
						Map<String,Object> map = new HashMap<>();
						map.put("线程执行时间", CommonUtil.date2Str("yyyy-MM-dd HH:mm:ss",new Date()));
						Long count = mongoTemplateTwo.count(query, InforBo.class);
						map.put("资讯数据库新增数据",count);
						int addNum = 0;
						List<String> addNews = new ArrayList<>();
						for (int i = 0; i * limit < count; i++) {
							query.skip(i * limit);
							query.limit(limit);

							List<InforBo> lst = mongoTemplateTwo.find(query, InforBo.class);
							for (InforBo healthBo : lst) {
								healthBo.setId(null);

								ExposeBo find = mongoTemplate.findOne(new Query(Criteria.where("title")
										.is(healthBo.getTitle()).and("deleted").is(Constant.ACTIVITY)), ExposeBo.class);
								if (find == null) {
									BeanUtils.copyProperties(healthBo, exposeBo);
									exposeBo.setSource(StringUtils.isEmpty(healthBo.getSource())?"":healthBo.getSource());
									exposeBo.setSourceUrl(StringUtils.isEmpty(healthBo.getSourceUrl())?"":healthBo.getSourceUrl());
									exposeBo.setContent(healthBo.getText());
									exposeBo.setCreateuid("5b36f1a4c4f6aee431dba56f");
									exposeBo.setImages(new LinkedHashSet(healthBo.getImageUrls()));
									exposeBo.setExposeType("其他");
									exposeBo.setPicType("pic");
									mongoTemplate.insert(exposeBo);
									addNum+=1;
									addNews.add(JSON.toJSONString(exposeBo));
								}
								Update update = new Update();
								update.set("synToExp", 1);
								mongoTemplateTwo.updateMulti(new Query(Criteria.where("title").is(healthBo.getTitle())),
										update, InforBo.class);
							}
							lst = null;
						}
						map.put("更新的数据总数",addNum);
						map.put("更新的数据详情",addNews);
						logger.info(JSON.toJSONString(map));
						Thread.sleep(timeInterval);
					} catch (Exception e) {
						try {
							PrintStream stream = new PrintStream("/opt/apps/lad-server/log/TimeTaskinfos.log");
							e.printStackTrace(stream);
						} catch (FileNotFoundException e1) {
							logger.error(e1.toString());
						}
					}
				}
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}
}
