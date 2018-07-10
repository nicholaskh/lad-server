package com.lad.dao.impl;

import java.util.LinkedHashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.ShowBo;
import com.lad.dao.BaseDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;

import cn.jiguang.common.utils.StringUtils;

/**
 * 功能描述： Copyright: Copyright (c) 2018 Version: 1.0 Time:2018/4/26
 */
@Repository("showDao")
public class ShowDao extends BaseDao<ShowBo> {

	/**
	 * 根据关键和type 查询
	 * 
	 * @param keyword
	 * @param type
	 * @param page
	 * @param limit
	 * @return
	 */
	public List<ShowBo> findByList(String[] matchField, String keyword, String userid, int type, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("status").is(0).and("deleted").is(Constant.ACTIVITY);
		// -1表示查询所有
		if (type != -1) {
			criteria.and("type").is(type);
		}
		// 排除自己发布的信息
		if (!StringUtils.isEmpty(userid)) {
			criteria.and("createuid").ne(userid);
		}
		if (!StringUtils.isEmpty(keyword)) {
			Criteria[] criterias = new Criteria[matchField.length];
			for (int i = 0; i < matchField.length; i++) {
				criterias[i] = new Criteria(matchField[i]).regex("^.*" + keyword + ".*$");
			}
			criteria.orOperator(criterias);
		}
		query.addCriteria(criteria);
		System.out.println("============================================");
		Logger logger = Logger.getLogger(ShowDao.class);
		logger.error(query.toString());
		query.with(new Sort(Sort.Direction.DESC, "_id"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return getMongoTemplate().find(query, ShowBo.class);
	}

	/**
	 * 根据关键和type 查询
	 * 
	 * @param keyword
	 * @param type
	 * @param page
	 * @param limit
	 * @return
	 */
	public List<ShowBo> findByList(String keyword, String userid, int type, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("status").is(0).and("deleted").is(Constant.ACTIVITY);
		// -1表示查询所有
		if (type != -1) {
			criteria.and("type").is(type);
		}
		// 排除自己发布的信息
		if (!StringUtils.isEmpty(userid)) {
			criteria.and("createuid").ne(userid);
		}
		if (!StringUtils.isEmpty(keyword)) {
			criteria.orOperator(new Criteria("showType").regex("^.*" + keyword + ".*$"),
					new Criteria("title").regex("^.*" + keyword + ".*$"));
		}
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.DESC, "_id"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return getMongoTemplate().find(query, ShowBo.class);
	}

	/**
	 * 根据关键和type 查询
	 * 
	 * @param keyword
	 * @param type
	 * @return
	 */
	public long findByList(String keyword, String userid, int type) {
		Query query = new Query();
		Criteria criteria = Criteria.where("status").is(0).and("deleted").is(Constant.ACTIVITY);
		// -1表示查询所有
		if (type != -1) {
			criteria.and("type").is(type);
		}
		// 排除自己发布的信息
		if (!StringUtils.isEmpty(userid)) {
			criteria.and("createuid").ne(userid);
		}
		if (!StringUtils.isEmpty(keyword)) {
			criteria.orOperator(new Criteria("showType").regex("^.*" + keyword + ".*$"),
					new Criteria("title").regex("^.*" + keyword + ".*$"));
		}
		query.addCriteria(criteria);
		return getMongoTemplate().count(query, ShowBo.class);
	}

	/**
	 * 根据关键和type 查询
	 * 
	 * @param keyword
	 * @param type
	 * @return
	 */
	public List<ShowBo> findByShowType(String keyword, int type) {
		Query query = new Query();
		Criteria criteria = new Criteria("type").is(type).and("deleted").is(Constant.ACTIVITY).and("showType")
				.is(keyword).and("status").is(0);
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.DESC, "_id"));
		return getMongoTemplate().find(query, ShowBo.class);
	}

	/**
	 * 根据关键和type 查询
	 * 
	 * @param userid
	 * @param type
	 * @param page
	 * @param limit
	 * @return
	 */
	public List<ShowBo> findByMyShows(String userid, int type, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("createuid").is(userid).and("deleted").is(Constant.ACTIVITY);
		// -1表示查询所有
		if (type != -1) {
			criteria.and("type").is(type);
		}
		query.addCriteria(criteria);
		// 自己发布已失效也需要排序
		query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "status")));
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return getMongoTemplate().find(query, ShowBo.class);
	}

	/**
	 * 根据关键和type 查询
	 * 
	 * @param userid
	 * @param type
	 * @return
	 */
	public List<ShowBo> findByMyShows(String userid, int type) {
		Query query = new Query();
		Criteria criteria = new Criteria("createuid").is(userid).and("deleted").is(Constant.ACTIVITY).and("status")
				.is(0);
		// -1表示查询所有
		if (type != -1) {
			criteria.and("type").is(type);
		}
		query.addCriteria(criteria);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		return getMongoTemplate().find(query, ShowBo.class);
	}

	/**
	 * 根据关键和type 查询
	 * 
	 * @param showTypes
	 * @return
	 */
	public List<ShowBo> findRecomShows(String userid, LinkedHashSet<String> showTypes, int type) {
		Query query = new Query();
		Criteria criteria = new Criteria("createuid").ne(userid).and("deleted").is(Constant.ACTIVITY).and("status")
				.is(0).and("showType").in(showTypes);
		// -1表示查询所有
		if (type != -1) {
			criteria.and("type").is(type);
		}
		query.addCriteria(criteria);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		return getMongoTemplate().find(query, ShowBo.class);
	}

	/**
	 * 根据关键和type 查询
	 * 
	 * @param showTypes
	 * @return
	 */
	public List<ShowBo> findCircleRecoms(LinkedHashSet<String> showTypes) {
		Query query = new Query();
		Criteria criteria = new Criteria("deleted").is(Constant.ACTIVITY).and("status").is(0).and("showType")
				.in(showTypes).and("type").is(ShowBo.NEED);
		query.addCriteria(criteria);
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		return getMongoTemplate().find(query, ShowBo.class);
	}

	/**
	 * 根据关键和type 查询
	 * 
	 * @param circleid
	 * @param type
	 * @return
	 */
	public List<ShowBo> findByCircleid(String circleid, int status, int type) {
		Query query = new Query();
		Criteria criteria = new Criteria("type").is(type).and("circleid").is(circleid).and("deleted")
				.is(Constant.ACTIVITY);
		if (status != -1) {
			criteria.and("status").is(status);
		}
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.DESC, "_id"));
		return getMongoTemplate().find(query, ShowBo.class);
	}

	/**
	 * 根据关键和type 查询
	 * 
	 * @param showids
	 * @param status
	 * @return
	 */
	public WriteResult updateShowStatus(List<String> showids, int status) {
		Query query = new Query();
		Criteria criteria = new Criteria("_id").in(showids).and("status").is(0).and("deleted").is(Constant.ACTIVITY);
		query.addCriteria(criteria);
		Update update = new Update();
		update.set("status", status);
		return getMongoTemplate().updateMulti(query, update, ShowBo.class);
	}

	/**
	 *
	 * @param showid
	 * @param status
	 * @return
	 */
	public WriteResult updateShowStatus(String showid, int status) {
		Query query = new Query(new Criteria("_id").is(showid));
		Update update = new Update();
		update.set("status", status);
		return getMongoTemplate().updateFirst(query, update, ShowBo.class);
	}

	/**
	 * 根据关键和type 查询
	 * 
	 * @param type
	 * @return
	 */
	public List<ShowBo> findByShowType(int type, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("type").is(type).and("deleted").is(Constant.ACTIVITY).and("status").is(0);
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.DESC, "_id"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return getMongoTemplate().find(query, ShowBo.class);
	}

	/**
	 * 根据关键和type 查询
	 * 
	 * @param keyword
	 * @param type
	 * @param page
	 * @param limit
	 * @return
	 */
	public List<ShowBo> findByKeword(String keyword, int type, int page, int limit) {
		Query query = new Query();
		Criteria criteria = new Criteria("status").is(0).and("deleted").is(Constant.ACTIVITY);
		// -1表示查询所有
		if (type != -1) {
			criteria.and("type").is(type);
		}
		if (!StringUtils.isEmpty(keyword)) {
			criteria.orOperator(new Criteria("showType").regex("^.*" + keyword + ".*$"),
					new Criteria("company").regex("^.*" + keyword + ".*$"));
		}
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.DESC, "_id"));
		page = page < 1 ? 1 : page;
		query.skip((page - 1) * limit);
		query.limit(limit);
		return getMongoTemplate().find(query, ShowBo.class);
	}

	public int findPublishZhaoNum(String id) {
		return (int) getMongoTemplate().count(new Query(
				Criteria.where("type").is(ShowBo.NEED).and("deleted").is(Constant.ACTIVITY).and("createuid").is(id)),
				ShowBo.class);
	}

	public int findPublishJieNum(String id) {
		return (int) getMongoTemplate().count(new Query(
				Criteria.where("type").is(ShowBo.PROVIDE).and("deleted").is(Constant.ACTIVITY).and("createuid").is(id)),
				ShowBo.class);
	}
}
