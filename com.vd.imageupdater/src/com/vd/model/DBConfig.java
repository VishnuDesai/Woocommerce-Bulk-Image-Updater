package com.vd.model;

public class DBConfig {
	private String host;
	private String port;
	private String user;
	private String pass;
	private String dbName;

	public DBConfig(String host, String port, String user, String pass, String dbName) {
		super();
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.dbName = dbName;
	}

	public DBConfig() {
		super();
		this.host = "localhost";
		this.port = "";
		this.user = "root";
		this.pass = "root";
		this.dbName = "test";
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

}
