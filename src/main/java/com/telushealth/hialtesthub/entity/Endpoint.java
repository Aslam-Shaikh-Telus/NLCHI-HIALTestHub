package com.telushealth.hialtesthub.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Document
@Component
public class Endpoint {

	private String ipAddress;
	private String testOrprod;
	private String hostname;
	private String username;
	private String password;

	public Endpoint() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Endpoint(String ipAddress, String testOrprod, String hostname, String username, String password) {
		super();
		this.ipAddress = ipAddress;
		this.testOrprod = testOrprod;
		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getTestOrprod() {
		return testOrprod;
	}

	public void setTestOrprod(String testOrprod) {
		this.testOrprod = testOrprod;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Endpoint [ipAddress=" + ipAddress + ", testOrprod=" + testOrprod + ", hostname=" + hostname
				+ ", username=" + username + ", password=" + password + "]";
	}

}
