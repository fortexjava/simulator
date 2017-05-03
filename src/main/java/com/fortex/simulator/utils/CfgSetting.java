/**<p>Description</p>
 * @author Ivan Huo
 */
package com.fortex.simulator.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author IvanHuo
 *
 */
public class CfgSetting {
	private final Properties prop = new Properties();
	
	public AtomicInteger CLIENT_RECEIVED = new AtomicInteger(0);
	
	public CfgSetting(String resourcePath){
		try {
			prop.load(new FileInputStream(resourcePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getValue(String key) {
		return prop.getProperty(key);
	}
	
}
