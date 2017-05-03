package com.fortex.simulator.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * @author Ivan Huo
 *
 */
public class ConfigSetting {
 	private static final Properties DATABASE = new Properties();
 	static{
		try {
 			DATABASE.load(new FileInputStream("config/server/database.properties"));

			/*
			SERVER.load(ConfigSetting.class.getResourceAsStream("config/server/server.properties"));
			DATABASE.load(ConfigSetting.class.getResourceAsStream("config/server/database.properties"));
			ADAPTER.load(ConfigSetting.class.getResourceAsStream("config/quote/adaptor.properties"));
			*/
		} catch (IOException e) {
			Logger.getLogger("EventError").error("load config file error.", e);
		}
	}
	
 
	public static String getDatabaseProperty(String key) {
		return DATABASE.getProperty(key);
	}
	
	 
}
