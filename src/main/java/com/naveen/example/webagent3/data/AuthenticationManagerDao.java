package com.naveen.example.webagent3.data;

import java.util.Map;

public interface AuthenticationManagerDao {
	public Map<String, String> validateAndCreateSession(String cookie, String cookieType);
	

}
