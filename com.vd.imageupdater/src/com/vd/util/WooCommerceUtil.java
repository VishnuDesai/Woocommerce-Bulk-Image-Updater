package com.vd.util;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import com.vd.model.DBConfig;

public class WooCommerceUtil {

	public static DBConfig readDBConfig() {
		DBConfig config = new DBConfig();
		try {
			InputStream inStream = WooCommerceUtil.class.getResourceAsStream("/DBConfig.properties");
			Properties properties = new Properties();
			properties.load(inStream);
			inStream.close();
			Enumeration<Object> enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);
				if (key.contains("DBHost"))
					config.setHost(value);
				else if (key.contains("DBPort"))
					config.setPort(value);
				else if (key.contains("DBUser"))
					config.setUser(value);
				else if (key.contains("DBName"))
					config.setDbName(value);
				else if (key.contains("DBPass"))
					config.setPass(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}

	public static String getProjDir() {
		String projDir = "C:/wamp/www/";
		try {
			InputStream inStream = WooCommerceUtil.class.getResourceAsStream("/DBConfig.properties");
			Properties properties = new Properties();
			properties.load(inStream);
			inStream.close();
			Enumeration<Object> enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);
				if (key.contains("ProjDir")) {
					projDir = value;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projDir;
	}

	public static String getUploadsUrl() {
		String uploadsUrl = "";
		try {
			InputStream inStream = WooCommerceUtil.class.getResourceAsStream("/DBConfig.properties");
			Properties properties = new Properties();
			properties.load(inStream);
			inStream.close();
			Enumeration<Object> enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);
				if (key.contains("uploadsUrl")) {
					uploadsUrl = value;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uploadsUrl;
	}

}