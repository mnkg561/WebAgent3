package com.naveen.example.webagent3.model;

import java.io.Serializable;

public class UserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String distinguishedName;
	private String cn;
	private String givenName;
	private String sAMAccountName;
	private String objectGUID;
	
	public String getsAMAccountName() {
		return sAMAccountName;
	}
	public void setsAMAccountName(String sAMAccountName) {
		this.sAMAccountName = sAMAccountName;
	}
	public String getObjectGUID() {
		return objectGUID;
	}
	public void setObjectGUID(String objectGUID) {
		this.objectGUID = objectGUID;
	}
	public String getDistinguishedName() {
		return distinguishedName;
	}
	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}
	public String getCn() {
		return cn;
	}
	public void setCn(String cn) {
		this.cn = cn;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

}
