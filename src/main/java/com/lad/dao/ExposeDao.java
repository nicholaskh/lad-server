package com.lad.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.lad.bo.ExposeBo;
import com.lad.util.CommonUtil;
import com.mongodb.WriteResult;

/**
 * 功能描述： Copyright: Copyright (c) 2018 Version: 1.0 Time:2018/4/22
 */
@Repository("exposeDao")
public class ExposeDao extends BaseDao<ExposeBo> {

	/**
	 * 根据参数匹配
	 * 
	 * @param page
	 * @param limit
	 * @return
	 */
	public List<ExposeBo> findRegexByPage(String title, List<String> exposeTypes, int page, int limit) {
		Query query = new Query();

		if (!StringUtils.isEmpty(title)) {
			Pattern pattern = Pattern.compile("^.*" + title + ".*$", Pattern.CASE_INSENSITIVE);
			query.addCriteria(new Criteria("title").regex(pattern));
		}
		if (!CommonUtil.isEmpty(exposeTypes)) {
			Criteria criteria = new Criteria();
			exposeTypes.forEach(type -> {
				Pattern pattern = Pattern.compile("^.*" + type + ".*$", Pattern.CASE_INSENSITIVE);
				criteria.and("exposeType").regex(pattern);
			});
			query.addCriteria(criteria);
		}
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return getMongoTemplate().find(query, ExposeBo.class);
	}

	/**
	 * 根据参数匹配
	 * @param params
	 * @param page
	 * @param limit
	 * @return
	 */
	public List<ExposeBo> findParamsByPage(Map<String, Object> params, int page, int limit) {
		Query query = new Query();
		if (params != null) {
			params.forEach((key, value) -> query.addCriteria(new Criteria(key).is(value)));
		}
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return getMongoTemplate().find(query, ExposeBo.class);
	}

	/**
	 * 根据参数匹配
	 * @param params
	 * @return
	 */
	public ExposeBo findByParam(Map<String, Object> params) {
		Query query = new Query();
		if (params != null) {
			params.forEach((key, value) -> query.addCriteria(new Criteria(key).is(value)));
			return getMongoTemplate().findOne(query, ExposeBo.class);
		}
		return null;
	}

	/**
	 * 根据参数匹配
	 * @param params
	 * @return
	 */
	public WriteResult updateByParam(String id, Map<String, Object> params) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(id));
		Update update = new Update();
		params.forEach((key, value) -> update.set(key, value));
		update.set("updateTime", new Date());
		return getMongoTemplate().updateFirst(query, update, ExposeBo.class);
	}
}
