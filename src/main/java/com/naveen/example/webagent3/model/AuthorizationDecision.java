package com.naveen.example.webagent3.model;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Component
@JsonIgnoreProperties(ignoreUnknown=true)
public class AuthorizationDecision {
	private boolean isAuthorized;
	private boolean mfaEnabled;
	public String mfaURL;

	public boolean isAuthorized() {
		return isAuthorized;
	}

	public void setAuthorized(boolean isAuthorized) {
		this.isAuthorized = isAuthorized;
	}

	public boolean isMfaEnabled() {
		return mfaEnabled;
	}

	public void setMfaEnabled(boolean mfaEnabled) {
		this.mfaEnabled = mfaEnabled;
	}

	public String getMfaURL() {
		return mfaURL;
	}

	public void setMfaURL(String mfaURL) {
		this.mfaURL = mfaURL;
	}
}
