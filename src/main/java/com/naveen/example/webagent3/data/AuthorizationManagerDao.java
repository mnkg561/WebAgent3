package com.naveen.example.webagent3.data;

import com.naveen.example.webagent3.model.AuthorizationDecision;

public interface AuthorizationManagerDao {
	public AuthorizationDecision isAuthorized(String path, String cookieName, String cookie);
}
