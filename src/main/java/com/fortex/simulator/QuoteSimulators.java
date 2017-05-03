/**<p>Description</p>
 * @author Ivan Huo
 */ 
package com.fortex.simulator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fortex.quickRing.db.DBOperation;
import com.fortex.simulator.utils.CfgSetting;
import com.fortex.simulator.utils.CountStatic;

import quickfix.field.Account;
import quickfix.field.ClOrdID;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MDUpdateType;
import quickfix.field.MarketDepth;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.TestReqID;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.fix44.MarketDataRequest;

/**
 * @author Administrator
 *
 */
public class QuoteSimulators {
	private static final String CONFIG_FILE_PATH = "server.properties";
	private static AbstractServer starter;
	private static String dataFile = System.getProperty("user.dir") + File.separator + "configs" + File.separator
			+ "clientConfigs.xlsx.quote";
	private static String targetFilePath = System.getProperty("user.dir") + File.separator + "configs" + File.separator;
 	
	private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
	private static Integer SENDPERSEC = 9000;
	private static Integer TOTAL_USED_MILLSEC = 120000;
	private static List<Integer> totalDist=new ArrayList<Integer>();
	private static long TIMEOUT_MILLSEC=100000000;
	
	public void start(String configFile) throws Exception {
		
	}

	public static Properties readConfig(String filePath) throws Exception {
		Properties prop = new Properties();
		InputStream is = App.class.getResourceAsStream(CONFIG_FILE_PATH);
		prop.load(is);
		return prop;
	}

	public static void main(String args[]) throws Exception {

		List<AbstractServer> servers = new ArrayList();
		Properties prop = readConfig(CONFIG_FILE_PATH);
		String starterClassName = prop.getProperty("starterQuoteClass");

		XSSFWorkbook xwb = new XSSFWorkbook(dataFile);

		XSSFSheet sheet = xwb.getSheetAt(0);

		XSSFRow row;

		String username = "PFIXQUOTE01";
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$ Fix Client Side Demo(Market Data) $$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ By Fortex $$$$$$$");
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("\n");
		System.out.println("Step1.Connecting and logon to Fortex Fix Server");

			Constructor<?> con = Class.forName(starterClassName).getConstructor(String.class);
			String serverPath = targetFilePath + "servers" + username + ".cfg";

			starter = (AbstractServer) con.newInstance(serverPath);
			starter.doExecute();
			long currentTime = System.currentTimeMillis();
			
			for(;;){
				if(starter.isInitiatorLoggoned())
					break;
				if((System.currentTimeMillis() - currentTime ) > TIMEOUT_MILLSEC){
					System.out.println("Error login timeout, please check your connection configuration and reboot this application!");
					return;					
				}
			}
			servers.add(starter);
		
		
			
		
		System.out.println("...Connected and Logoned successfully!");
		// shutdownLatch.await();
		
//		System.out.println("Send test msg...");	
//		quickfix.fix44.TestRequest test = new quickfix.fix44.TestRequest();
//		test.set(new TestReqID("0987894"));
//		starter.sendMsg(test);	
		
		Map<String, com.fortex.simulator.utils.FxTableModel> symbolsOrg = DBOperation.getSymbols();
		Set<Entry<String, com.fortex.simulator.utils.FxTableModel>> set = symbolsOrg.entrySet();										
		

			System.out.print("Step2.Choose market dept to Market Data 1(Top of book), 0(Full book), or N:");
			String dept = new Scanner(System.in).nextLine();
				System.out.println("...Subscribing Market Data");
				//TOTAL_USED_MILLSEC = Integer.valueOf(inputContent) * 1000; 
				//for (AbstractServer server : servers) {
					// server = servers.get(0);
					int symbolCount=0;
				  	for (Entry<String, com.fortex.simulator.utils.FxTableModel> entry : set) {
						quickfix.fix44.MarketDataRequest request = new quickfix.fix44.MarketDataRequest();
						String mDReqId = UUID.randomUUID().toString().substring(0, 16);

						request.set(new MDReqID(mDReqId));
						request.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
						request.set(new MarketDepth(Integer.valueOf(dept)));
						request.set(new MDUpdateType(0));// Must be 0 (Full
															// Refresh),
															// required
															// if Subscription
															// RequestType <263>
															// = Snapshot +
															// Updates (1)

						MarketDataRequest.NoMDEntryTypes noMdEntryTypes1 = new MarketDataRequest.NoMDEntryTypes();
						noMdEntryTypes1.set(new MDEntryType(MDEntryType.BID));
						request.addGroup(noMdEntryTypes1);

						MarketDataRequest.NoMDEntryTypes noMdEntryTypes2 = new MarketDataRequest.NoMDEntryTypes();
						noMdEntryTypes2.set(new MDEntryType(MDEntryType.OFFER));
						request.addGroup(noMdEntryTypes2);

						MarketDataRequest.NoRelatedSym norelatedSym = new MarketDataRequest.NoRelatedSym();
						com.fortex.simulator.utils.FxTableModel model = entry.getValue();						
						norelatedSym.set(new Symbol(model.getSymbol()));						
//						norelatedSym.set(new Symbol("GBP/DKK"));
						request.addGroup(norelatedSym);						
						starter.sendMsg(request);
						symbolCount++;
						
					 	//Thread.sleep(1000);
											 						 					 										
				 }
				  	System.out.println("Total symbol count:" + symbolCount);
				//Thread.sleep(10000);
				
				
//				System.out.print("Step3.Press any key to continue unsubscribe");				
//				new Scanner(System.in).nextLine();				
//				System.out.println("...Unsubscribing"); 
//				//for (AbstractServer server : servers) {					
//					 
//					for (Entry<String, com.fortex.simulator.utils.FxTableModel> entry : set) {
//
//						quickfix.fix44.MarketDataRequest request = new quickfix.fix44.MarketDataRequest();
//						String mDReqId = UUID.randomUUID().toString().substring(0, 16);
//
//						request.set(new MDReqID(mDReqId));
//						request.set(new SubscriptionRequestType(SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST));
//						request.set(new MarketDepth(1));
//						request.set(new MDUpdateType(0));// Must be 0 (Full
//															// Refresh),
//															// required
//															// if Subscription
//															// RequestType <263>
//															// = Snapshot +
//															// Updates (1)
//
//						MarketDataRequest.NoMDEntryTypes noMdEntryTypes1 = new MarketDataRequest.NoMDEntryTypes();
//						noMdEntryTypes1.set(new MDEntryType(MDEntryType.BID));
//						request.addGroup(noMdEntryTypes1);
//
//						MarketDataRequest.NoMDEntryTypes noMdEntryTypes2 = new MarketDataRequest.NoMDEntryTypes();
//						noMdEntryTypes2.set(new MDEntryType(MDEntryType.OFFER));
//						request.addGroup(noMdEntryTypes2);
//
//						MarketDataRequest.NoRelatedSym norelatedSym = new MarketDataRequest.NoRelatedSym();
//						com.fortex.simulator.utils.FxTableModel model = entry.getValue();
//						norelatedSym.set(new Symbol(model.getSymbol()));
//						request.addGroup(norelatedSym);
//						//server.sendMsg(request);
//						starter.sendMsg(request);
//					}
//				//}
//				Thread.sleep(3000);
//				System.out.println("...Finished unsubscribe. Check the status on conformance center"); 
				
				/**		
				long currentTime = System.currentTimeMillis();	
					   
				new Thread(new Runnable() {
					public void run() {
						while (true) {
							
							long usedTime = (System.currentTimeMillis() - currentTime);
						 
							if (usedTime >= TOTAL_USED_MILLSEC) {								
								return;
							} 
							
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							int totalPerSec = 0;
							List<Integer> dlist=new ArrayList<Integer>();
							for (int i=0;i < servers.size();i++) {
								AbstractServer server = servers.get(i);
								dlist.add(server.cfg.CLIENT_RECEIVED.intValue());
								totalDist.add(server.cfg.CLIENT_RECEIVED.intValue());
								totalPerSec =  totalPerSec + server.cfg.CLIENT_RECEIVED.intValue();
								System.out.print("Client" + i + ":" + server.cfg.CLIENT_RECEIVED + " ");								
								server.cfg.CLIENT_RECEIVED.set(0); 
							}							 
							 System.out.println("\nTotal amount this sec:" + totalPerSec + "");
							 
				              int min = dlist.get(0); 
				              for (int i = 0; i < dlist.size(); i++) {          
				                      if (min > dlist.get(i)) min = dlist.get(i);           
				              }     
				              System.out.println("Min received this sec:" + min + "\n");							
						}
					}
				}).start();
			 	 	
				
			  
				new Thread(new Runnable() {
					public void run() {
						while (true) {

							long usedTime = (System.currentTimeMillis() - currentTime);

							if (usedTime >= TOTAL_USED_MILLSEC) {
								System.out.println("*Total used time:" + usedTime + " million seconds");
								System.out.println("*Total Received Info is:" + CountStatic.TOTAL_RECEIVED.intValue());
								
								 int min = totalDist.get(0); 
					              for (int i = 0; i < totalDist.size(); i++) {          
					                      if (min > totalDist.get(i)) min = totalDist.get(i);           
					              }     
					              System.out.println("Total Min received this sec:" + min + "\n");	
								
								return;
							}

						}
					}
				}).start();
**/			    
			 
		 	
	}
}
