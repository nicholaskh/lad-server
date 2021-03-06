package com.lad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.dao.IRegistDao;
import com.lad.service.IRegistService;

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

	public boolean is_phone_repeat(String phone) {
		Integer id = registDao.searchPhone(phone);
		if (id == -1)
			return false;
		return true;
	}

}
