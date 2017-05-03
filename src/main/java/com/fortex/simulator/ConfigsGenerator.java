/**<p>Description</p>
 * @author Ivan Huo
 */
package com.fortex.simulator;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Administrator
 *
 */
public class ConfigsGenerator {
	private static String dataFile = System.getProperty("user.dir") + File.separator + "configs" + File.separator
			+ "clientConfigs.xlsx";
	private static String templateFile = System.getProperty("user.dir") + File.separator + "configs" + File.separator
			+ "serverTemplateTrade.cfg";
	private static String targetFilePath = System.getProperty("user.dir") + File.separator + "configs" + File.separator;

	public static void main(String[] args) throws Exception {
		
		// 构造 XSSFWorkbook 对象，strPath 传入文件路径
		XSSFWorkbook xwb = new XSSFWorkbook(dataFile);
		// 读取第一章表格内容
		XSSFSheet sheet = xwb.getSheetAt(0);
		// 定义 row、cell
		XSSFRow row;		
		// 循环输出表格中的内容
		String senderId = "";
		String targetId = "";
		String username = "";
		String password = "";
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i);			
			senderId = row.getCell(0).toString();
			targetId = row.getCell(1).toString();
			username = row.getCell(2).toString();
			password = row.getCell(3).toString();
			genServerFile(senderId,targetId,username,password);
			
		}
		System.out.println("Generate Files finished...");
	}
	
	 

	public static void genServerFile(String senderId,String targetId, String username, String password) throws Exception {
		BufferedWriter bw = null;
		try {
			FileWriter fw = new FileWriter(targetFilePath + "servers" + username + ".cfg", true);
			bw = new BufferedWriter(fw);
			bw.write("[default]\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Properties prop = new Properties();
		InputStream in = new BufferedInputStream(new FileInputStream(templateFile));
		prop.load(in);
		FileOutputStream oFile = new FileOutputStream(targetFilePath + "servers" + username + ".cfg", true);// true表示追加打开
		Iterator<String> it = prop.stringPropertyNames().iterator();
		while (it.hasNext()) {
			String key = it.next();
			prop.setProperty(key, prop.getProperty(key));
		} 
		in.close();
		/**
		prop.setProperty("SenderCompID", senderId);
		prop.setProperty("TargetCompID", targetId);
		prop.setProperty("Username", username);
		prop.setProperty("Password", password);
		**/
		prop.store(oFile, "New Server File");
		oFile.close();
		
		
		try {
			FileWriter fw = new FileWriter(targetFilePath + "servers" + username + ".cfg", true);
			bw = new BufferedWriter(fw);
			bw.write("[session]\n");
			bw.write("SenderCompID=" + senderId + "\n");
			bw.write("TargetCompID=" + targetId + "\n");
			bw.write("Username=" + username + "\n");
			bw.write("Password=" + password + "\n");
			bw.write("StartTime=00:00:00\n");
			bw.write("EndTime=00:00:00");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
