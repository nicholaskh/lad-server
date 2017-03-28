package com.junlenet.mongodb.demo.session;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

	public static synchronized String getSessionIdFromRequest(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		String sessionId = "";
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("session_id")) {
				sessionId = cookie.getValue();
			}
		}
		return sessionId;
	}
	
	public static synchronized HttpServletResponse putSessionIdToResponse(HttpServletResponse response, HttpSession session) {
		Cookie cookie = new Cookie("session_id", session.getId());
		response.addCookie(cookie);
		return response;
	}
}