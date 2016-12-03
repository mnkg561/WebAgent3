package com.naveen.example.webagent3.data;

import com.naveen.example.webagent3.model.ProtectedPolicy;

public interface ProtectionManagerDao {

	public ProtectedPolicy isProtected(String path);
}
