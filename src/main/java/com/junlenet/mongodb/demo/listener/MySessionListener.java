package com.junlenet.mongodb.demo.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.junlenet.mongodb.demo.session.MySessionContext;

public class MySessionListener implements HttpSessionListener {

	public void sessionCreated(HttpSessionEvent se) {
		// TODO Auto-generated method stub

	}

	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();
        MySessionContext.DelSession(session);
	}

}
