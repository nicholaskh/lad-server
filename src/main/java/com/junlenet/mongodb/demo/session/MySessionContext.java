package com.junlenet.mongodb.demo.session;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

public class MySessionContext {
	private static ConcurrentHashMap<String, HttpSession> mymap = new ConcurrentHashMap<String, HttpSession>();

    public static synchronized void AddSession(HttpSession session) {
        if (session != null) {
            mymap.put(session.getId(), session);
        }
    }

    public static synchronized void DelSession(HttpSession session) {
        if (session != null) {
            mymap.remove(session.getId());
        }
    }

    public static synchronized HttpSession getSession(String session_id) {
        if (session_id == null)
        return null;
        return (HttpSession) mymap.get(session_id);
    }
}