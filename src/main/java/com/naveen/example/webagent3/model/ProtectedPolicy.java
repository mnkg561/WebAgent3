package com.naveen.example.webagent3.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown= true)
public class ProtectedPolicy {
private Boolean thisProtected;
private String protectedBy;
public Boolean getThisProtected() {
	return thisProtected;
}
public void setThisProtected(Boolean thisProtected) {
	this.thisProtected = thisProtected;
}
public String getProtectedBy() {
	return protectedBy;
}
public void setProtectedBy(String protectedBy) {
	this.protectedBy = protectedBy;
}

}
