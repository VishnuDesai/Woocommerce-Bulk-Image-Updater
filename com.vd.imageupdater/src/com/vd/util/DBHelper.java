package com.vd.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vd.model.DBConfig;

public class DBHelper {
	private static Connection conn = null;

	public DBHelper() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		if (conn == null)
			open();
		return conn;
	}

	public static void open() {
		try {
			DBConfig config = WooCommerceUtil.readDBConfig();
			String DBUrl = "jdbc:mysql://" + config.getHost();
			if (config.getPort() != null && !config.getPort().equals(""))
				DBUrl = DBUrl + ":" + config.getPort();
			DBUrl = DBUrl + "/" + config.getDbName();
			conn = DriverManager.getConnection(DBUrl, config.getUser(), config.getPass());
		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void close() {
		try {
			conn.close();
			conn = null;
		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}