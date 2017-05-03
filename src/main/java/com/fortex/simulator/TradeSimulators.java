/**<p>Description</p>
 * @author Ivan Huo
 */
package com.fortex.simulator;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fortex.simulator.utils.CountStatic;

import quickfix.field.Account;
import quickfix.field.ClOrdID;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;

/**
 * @author Administrator
 *
 */
public class TradeSimulators {
	private static final String CONFIG_FILE_PATH = "server.properties";
	private static AbstractServer starter;
	private static String dataFile = System.getProperty("user.dir") + File.separator + "configs" + File.separator + "clientConfigs.xlsx.trade";
	private static String targetFilePath = System.getProperty("user.dir") + File.separator + "configs" + File.separator ;
	private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
	private static Integer TASISIZE = 10000;
	private static Integer SENDPERSEC = 1;	
	private static Integer TOTAL_USED_MILLSEC=60000;
	private static Integer serverIndex=0;
	private static long TIMEOUT_MILLSEC=10000;
	public void start(String configFile) throws Exception {
		
//		String inatiatorConfigFile = configFile;			
		//Constructor<?> con = Class.forName(starterClassName).getConstructor(String.class);
		//starter = (AbstractServer) con.newInstance(inatiatorConfigFile);
		//starter.doExecute();
	}
	
	public static Properties readConfig(String filePath) throws Exception {
		Properties prop = new Properties();
		InputStream is = App.class.getResourceAsStream(CONFIG_FILE_PATH);
		prop.load(is);
		return prop;
	}
	
	
	public static void main(String args[]) throws Exception{
			 
				List<AbstractServer> servers = new ArrayList() ;
				Properties prop = readConfig(CONFIG_FILE_PATH);
				String starterClassName = prop.getProperty("starterTradeClass");
		
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$ Fix Client Side Demo(New Order Single) $$$$$$$$$$$$$$$$$$$$$$$$$");
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ By Fortex $$$$$$$");
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				System.out.println("\n");
				System.out.println("Step1.Connecting and logon to Fortex Fix Server");

				XSSFWorkbook xwb = new XSSFWorkbook(dataFile);
		
				XSSFSheet sheet = xwb.getSheetAt(0);
		
				XSSFRow row;		
		
				String username = "";	
			 	for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
					row = sheet.getRow(i);			
					username = row.getCell(2).toString();									
					starterClassName = prop.getProperty("starterTradeClass");
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
		 		}
			 	System.out.println("...Connected and Logoned successfully!");

//				System.out.print("\nInput Send Per Sec to go on..");
//				String persec = new Scanner(System.in).nextLine();
//				
//				System.out.print("\nInput Total sec to run..");
//				String timeLast = new Scanner(System.in).nextLine();
				
				System.out.print("Step2.Press any key to continue New Order Single");
				new Scanner(System.in).nextLine();
				System.out.println("...Making New Order Single");
//				if(Integer.valueOf(persec)>0){
					long currentTime = System.currentTimeMillis();
					long usedTime = 0;
//					SENDPERSEC= Integer.valueOf(persec);	
//					TOTAL_USED_MILLSEC = Integer.valueOf(timeLast) * 1000;
					//for(int i=0,j=0,send=1;;i++){
							int j=0,send=1;
							AbstractServer server = servers.get(0);
							quickfix.fix44.NewOrderSingle order = new quickfix.fix44.NewOrderSingle();
					    	order.set(new Account(server.getCfg().getValue("Username")));
					    	order.set(new OrdType(OrdType.MARKET));    	 
					    	order.set(new Side(Side.BUY));
					    	String clOrderId= UUID.randomUUID().toString().substring(0, 16);
							order.set(new ClOrdID(clOrderId));
							order.set(new OrderQty(1));
							order.set(new Symbol("USD/JPY"));
							order.set(new TimeInForce(TimeInForce.IMMEDIATE_OR_CANCEL));
							order.set(new SecurityType(SecurityType.FOREIGN_EXCHANGE_CONTRACT));
							order.set(new TransactTime(new Date()));		
							server.sendMsg(order);
							j++;
							if(j > servers.size()-1)
								j=0;
							send++;
//							if(send>=SENDPERSEC){
//								System.out.println("Sended " + SENDPERSEC + "..");
//								Thread.sleep(1000);
//								send=1;
//							}
							
							Thread.sleep(3000);
							System.out.println("...Data received from Server. Check the status on conformance center");	
							
							System.out.print("Step3.Press any key to continue Cancel Order");
							new Scanner(System.in).nextLine();
							System.out.println("...Cancelling");
							
							quickfix.fix44.OrderCancelRequest cancel = new quickfix.fix44.OrderCancelRequest();							
							cancel.set(new ClOrdID(UUID.randomUUID().toString().substring(0, 16)));
							cancel.set(new OrigClOrdID(clOrderId));
							cancel.set(new Account(server.getCfg().getValue("Username")));
							cancel.set(new OrdType(OrdType.MARKET));
							cancel.set(new Side(Side.BUY));
							cancel.set(new OrderQty(1));
							cancel.set(new Symbol("USD/JPY"));
							cancel.set(new SecurityType(SecurityType.FOREIGN_EXCHANGE_CONTRACT));
							cancel.set(new TransactTime(new Date()));
							server.sendMsg(cancel);
							Thread.sleep(3000);
							System.out.println("...Finished cancel order. Check the status on conformance center");
							
//							usedTime = (System.currentTimeMillis() - currentTime) ;
//							if(usedTime >= TOTAL_USED_MILLSEC)
//								break;
				//	}
					 
					
					/**
					ExecutorService service = Executors.newFixedThreadPool(servers.size());
					
			        for (int i = 0; i < TASISIZE; i++) {
			        	
			            Runnable run = new Runnable() {
			                @Override
			                public void run() {
			               
			                	AbstractServer server = servers.get(serverIndex);
								quickfix.fix44.NewOrderSingle order = new quickfix.fix44.NewOrderSingle();
						    	order.set(new Account(server.getCfg().getValue("Username")));
						    	order.set(new OrdType(OrdType.MARKET));    	 
						    	order.set(new Side(Side.BUY));
								order.set(new ClOrdID(UUID.randomUUID().toString().substring(0, 16)));
								order.set(new OrderQty(1));
								order.set(new Symbol("USD/JPY"));
								order.set(new TimeInForce(TimeInForce.IMMEDIATE_OR_CANCEL));
								order.set(new SecurityType(SecurityType.FOREIGN_EXCHANGE_CONTRACT));
								order.set(new TransactTime(new Date()));		
								server.sendMsg(order);
								serverIndex++;
								if(serverIndex > servers.size()-1)
									serverIndex=0;
			                }
			            };
			          
			            service.execute(run);
			        }
					/**
					/**
					System.out.println("Total Sended Info is:" + CountStatic.TOTAL_SENDED.intValue() );
					usedTime = (System.currentTimeMillis() - currentTime) ;
					System.out.println("Total Sended Used Time is:" + usedTime + " million seconds" );
					
					new Thread(new Runnable() {
						public void run() {
							while(true){
								long usedTime = (System.currentTimeMillis() - currentTime) ;
								 
								if(CountStatic.TOTAL_SENDED.intValue() >= TASISIZE ){
									System.out.println("Total sended used time:" + usedTime +  " million seconds");														
									return;
								}								
							}
						}
					}).start();
					**/
				 
					/**
					new Thread(new Runnable() {
						public void run() {
							while(true){
								long usedTime = (System.currentTimeMillis() - currentTime) ;
								
								 
								
								 
								if(CountStatic.TOTAL_RECEIVED.intValue() >= CountStatic.TOTAL_SENDED.intValue() * 2){
									System.out.println("Total used time:" + usedTime +  " million seconds");					
									System.out.println("Total Received Info is:" + CountStatic.TOTAL_RECEIVED.intValue());
									return;
								}	
								 						
							}
						}
					}).start();
					
					
					shutdownLatch.await();
				 	**/
				}			 
	//}
}
