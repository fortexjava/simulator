package com.fortex.simulator.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load the Server Config Setting
 * @author Ivan Huo
 */
public class ServerSetting {
	private static final Logger logger = LoggerFactory.getLogger(ServerSetting.class);
	private static final Properties tradeProp = new Properties();
	private static final Properties quoteProp = new Properties();
	
	 
	static{
		try {
			tradeProp.load(ServerSetting.class.getClassLoader().getResourceAsStream("com/fortex/simulator/inatiatorTradeServer.cfg"));
			quoteProp.load(ServerSetting.class.getClassLoader().getResourceAsStream("com/fortex/simulator/inatiatorQuoteServer.cfg"));
		} catch (IOException e) {
			logger.error("load config file error.", e);
		}
	}
	 		
	
	
	public static String getTradeValue(String key) {
		return tradeProp.getProperty(key);
	}
	
	public static String getQuoteValue(String key) {
		return quoteProp.getProperty(key);
	}
	
	

}
