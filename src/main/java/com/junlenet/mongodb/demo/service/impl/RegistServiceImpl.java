package com.junlenet.mongodb.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junlenet.mongodb.demo.dao.IRegistDao;
import com.junlenet.mongodb.demo.service.IRegistService;

@Service("registService")
public class RegistServiceImpl implements IRegistService {

	@Autowired
	private IRegistDao registDao;

	public Integer verification_send(String phone) {
		if (is_phone_repeat(phone)) {
			return -1;
		} else {
			// TODO
			// verification
			return 0;
		}
	}

	private boolean is_phone_repeat(String phone) {
		Integer id = registDao.searchPhone(phone);
		if (id == -1)
			return false;
		return true;
	}

}
