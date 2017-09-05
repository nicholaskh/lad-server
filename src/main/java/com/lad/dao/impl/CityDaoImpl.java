package com.lad.dao.impl;

import com.lad.bo.CityBo;
import com.lad.dao.ICityDao;
import com.lad.util.Constant;
import com.mongodb.BasicDBObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/5
 */
@Repository("cityDao")
public class CityDaoImpl implements ICityDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<CityBo> findAllCitys() {
        return mongoTemplate.findAll(CityBo.class);
    }

    @Override
    public List<CityBo> findByParams(String province, String city, String distrit) {
        Query query = new Query();
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        if (StringUtils.isNotEmpty(province)) {
            query.addCriteria(new Criteria("province").is(province));
        }
        if (StringUtils.isNotEmpty(city)) {
            query.addCriteria(new Criteria("city").is(city));
        }
        if (StringUtils.isNotEmpty(distrit)) {
            query.addCriteria(new Criteria("district").is(distrit));
        }
        return mongoTemplate.find(query, CityBo.class);
    }

    @Override
    public CityBo insert(CityBo cityBo) {
        mongoTemplate.insert(cityBo);
        return cityBo;
    }

    @Override
    public List<CityBo> findByParams(String province, String distrit) {
        Query query = new Query();
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        if (StringUtils.isNotEmpty(province)) {
            query.addCriteria(new Criteria("province").is(province));
        }
        if (StringUtils.isNotEmpty(distrit)) {
            query.addCriteria(new Criteria("district").is(distrit));
        }
        return mongoTemplate.find(query, CityBo.class);
    }

    @Override
    public List<BasicDBObject> findProvince() {

        ProjectionOperation project = Aggregation.project("province");

        GroupOperation group = Aggregation.group("province").first("province").as("province");
        Aggregation aggregation = Aggregation.newAggregation(project,  group,
                Aggregation.sort(new Sort(new Sort.Order(Sort.Direction.DESC, "province"))));
        AggregationResults<BasicDBObject> results = mongoTemplate.aggregate(aggregation, "city",
                BasicDBObject.class);
        return results.getMappedResults();
    }

    @Override
    public List<BasicDBObject> findCitys(String province) {
        Criteria criteria = new Criteria("province").is(province);
        GroupOperation group = Aggregation.group("city").first("city").as("city");
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.project("city"),  group,
                Aggregation.sort(new Sort(new Sort.Order(Sort.Direction.DESC, "city"))));
        AggregationResults<BasicDBObject> results = mongoTemplate.aggregate(aggregation, "city",
                BasicDBObject.class);
        return results.getMappedResults();
    }
}
