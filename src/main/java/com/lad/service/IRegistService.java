package com.lad.service;

public interface IRegistService extends IBaseService{
	
	public Integer verification_send(String phone);
	
	public boolean is_phone_repeat(String phone);
	
}
