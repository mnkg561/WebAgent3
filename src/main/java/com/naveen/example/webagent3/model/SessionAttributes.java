package com.naveen.example.webagent3.model;

public class SessionAttributes {
	private UserInfo userInfo;
	private long idleTimeOut = 600;
	private long lastSessionTime;
	private long maxTimeOut=3600;
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public long getIdleTimeOut() {
		return idleTimeOut;
	}
	public void setIdleTimeOut(long idleTimeOut) {
		this.idleTimeOut = idleTimeOut;
	}
	public long getLastSessionTime() {
		return lastSessionTime;
	}
	public void setLastSessionTime(long lastSessionTime) {
		this.lastSessionTime = lastSessionTime;
	}
	public long getMaxTimeOut() {
		return maxTimeOut;
	}
	public void setMaxTimeOut(long maxTimeOut) {
		this.maxTimeOut = maxTimeOut;
	}
}
