
package com.fortex.simulator;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fortex.simulator.client.QuoteClient;
import com.fortex.simulator.client.TradeClient;
import com.fortex.simulator.utils.ServerSetting;

import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.Account;
import quickfix.field.ClOrdID;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MDUpdateType;
import quickfix.field.MarketDepth;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.Message;

public class App {
	private static final String CONFIG_FILE_PATH = "server.properties";
	private static AbstractServer starter;
	static String orgClOrdId = "";
	static Side orgSide = null;
	static OrdType orgType=null;
	static String orgMdReqId="";
	static Double price =null;
	/**
	 * <p>
	 * Description:Read Config Properties
	 * </p>
	 * 
	 * @author Ivan Huo
	 * @date 2016-08-03
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private Properties readConfig(String filePath) throws Exception {
		Properties prop = new Properties();
		InputStream is = App.class.getResourceAsStream(CONFIG_FILE_PATH);
		prop.load(is);
		return prop;
	}

	/**
	 * <p>
	 * Description:Start simulator
	 * </p>
	 * 
	 * @author Ivan Huo
	 * @date 2016-08-03
	 * @throws Exception
	 */
	private void start() throws Exception {
		Properties prop = this.readConfig(CONFIG_FILE_PATH);
		 
	
		System.out.print("Select the Starter Class(1.Trade; 2.Quote):");
		String starterClassName = null;
		String inatiatorConfigFile = null;
		@SuppressWarnings("resource")
		String inputContent = new Scanner(System.in).nextLine();
		switch (inputContent) {
		case "1":
			starterClassName = prop.getProperty("starterTradeClass");
			inatiatorConfigFile = prop.getProperty("inatiatorTradeConfigFile");
			break;
		case "2":
			starterClassName = prop.getProperty("starterQuoteClass");
			inatiatorConfigFile = prop.getProperty("inatiatorQuoteConfigFile");
			break;
		}

		Constructor<?> con = Class.forName(starterClassName).getConstructor(String.class);
		starter = (AbstractServer) con.newInstance(inatiatorConfigFile);
		starter.doExecute();
		 
	}

	public static double round(double value){
	    return Math.round(value*100)/100.0;
	}
	public static void main(String[] args) throws Exception {
		  
		new Thread(new Runnable() {
			public void run() {

 				while (true) {
					 try {
						Thread.sleep(8000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (null != starter && starter.isInitiatorLoggoned()) {
						if (starter instanceof TradeClient) {
							System.out.print("Select Test type(1.NewOrderSingle(); 2.Order Cancel Request;):");
							@SuppressWarnings("resource")
							String inputContent = new Scanner(System.in).nextLine();
							switch (inputContent) {
							case "1":
								generateTestOrderSingle();
								break;
							case "2":
								generateTestOrderCancelRequest();
								break;
							}
						} else {
							System.out.print("Select Test type(1.MarketDataRequest(); 2.Disable MarketDataRequest;):");
							try {
								String inputContent = new Scanner(System.in).nextLine();
								switch (inputContent) {
								case "1":
									generateTestMarketDataRequest();
									break;
								case "2":
									generateTestDisableMarketDataRequest();
									break;
								}
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
	 			}
			};
		}) {
		}.start();
		App app = new App();
		app.start();
		 
	}

	/**
	 * <p>
	 * Description:Test for New Order Single
	 * </p>
	 * 
	 * @author Ivan Huo
	 * @date 2016-08-04
	 */
	private static void generateTestOrderSingle() {
		System.out.println("@@@@@@@@@@@@@@ Generated test New Order Single @@@@@@@");
		
		

		System.out.print("Select Ord type(1.Market; 2.Limit):");
		String inputContent = new Scanner(System.in).nextLine();
			switch (inputContent) {
				case "1":
					orgType = new OrdType(OrdType.MARKET);					
					break;
				case "2": {
					orgType = new OrdType(OrdType.LIMIT);
								 
					System.out.print("The Limit price is:");
					price= new Scanner(System.in).nextDouble();
					
					break;
				}
			}
		System.out.print("\nSelect Side(1.Buy; 2.Sell):");
		inputContent = new Scanner(System.in).nextLine();
		switch (inputContent) {
		case "1":
			orgSide = new Side(Side.BUY);
			break;
		case "2":
			orgSide = new Side(Side.SELL);
			break;
		}
		
		System.out.print("\nThe Quantity is:");
		int quantity = new Scanner(System.in).nextInt();
		long curT1=System.currentTimeMillis();		
 
		        	quickfix.fix44.NewOrderSingle order = new quickfix.fix44.NewOrderSingle();
		        	order.set(new Account(ServerSetting.getTradeValue("Username")));
		        	order.set(orgType);
		        	if(null!=price)
		        		order.set(new Price(price));
		        	order.set(orgSide);
		    		order.set(new ClOrdID(UUID.randomUUID().toString().substring(0, 16)));
		    		order.set(new OrderQty(quantity));
		    		order.set(new Symbol("USD/JPY"));
		    		order.set(new TimeInForce(TimeInForce.IMMEDIATE_OR_CANCEL));
		    		order.set(new SecurityType(SecurityType.FOREIGN_EXCHANGE_CONTRACT));
		    		order.set(new TransactTime(new Date()));
		    		sendTradeClient(order);
 
					 		 
	}

	/**
	 * <p>
	 * Description:Test for Order Cancel Request
	 * </p>
	 * 
	 * @author Ivan Huo
	 * @date 2016-08-03
	 */
	private static void generateTestOrderCancelRequest() {
		
		if(null!=orgClOrdId){
			System.out.println("@@@@@@@@@@@@@@ Generated test Order Cancel Request @@@@@@");
			quickfix.fix44.OrderCancelRequest order = new quickfix.fix44.OrderCancelRequest();
		 		
			order.set(new ClOrdID(UUID.randomUUID().toString().substring(0, 16)));
			order.set(new OrigClOrdID(orgClOrdId));
			order.set(new Account(ServerSetting.getTradeValue("Username")));
			order.set(orgSide);
			order.set(orgType);
			order.set(new OrderQty(1));
			order.set(new Symbol("USD/JPY"));
			order.set(new SecurityType(SecurityType.FOREIGN_EXCHANGE_CONTRACT));
			order.set(new TransactTime(new Date()));
			sendTradeClient(order);
		}
		else{
			System.out.println("Org ClOrdId is empty, please make a new Order Single test first");
		}
	}

	/**
	 * <p>
	 * Description:Test for Market Data Request
	 * </p>
	 * 
	 * @author Ivan Huo
	 * @throws InterruptedException 
	 * @date 2016-08-03
	 */
	private static void generateTestMarketDataRequest() throws InterruptedException {
		System.out.println("@@@@@@@@@@@@@@ Generated test Market Data Request @@@@@@");
		quickfix.fix44.MarketDataRequest request = new quickfix.fix44.MarketDataRequest();		
		String mDReqId = UUID.randomUUID().toString().substring(0, 16);
		orgMdReqId=mDReqId;
		request.set(new MDReqID(mDReqId));
		request.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
		request.set(new MarketDepth(0));
		request.set(new MDUpdateType(0));// Must be 0 (Full Refresh), required
											// if Subscription RequestType <263>
											// = Snapshot + Updates (1)

		MarketDataRequest.NoMDEntryTypes noMdEntryTypes1 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes1.set(new MDEntryType(MDEntryType.BID));		
		request.addGroup(noMdEntryTypes1);
		
		MarketDataRequest.NoMDEntryTypes noMdEntryTypes2 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes2.set(new MDEntryType(MDEntryType.OFFER));		
		request.addGroup(noMdEntryTypes2);

	    
		MarketDataRequest.NoRelatedSym norelatedSym = new MarketDataRequest.NoRelatedSym();
		norelatedSym.set(new Symbol("USD/JPY"));
		request.addGroup(norelatedSym);
		sendQuoteClient(request);
		Thread.sleep(3000);
	}
	
	private static void generateTestDisableMarketDataRequest() throws InterruptedException{
		if(orgMdReqId!=null){
			System.out.println("@@@@@@@@@@@@@@ Generated Disable Market Data Request @@@@@@");
			quickfix.fix44.MarketDataRequest request = new quickfix.fix44.MarketDataRequest();		 	 
			request.set(new MDReqID(orgMdReqId));
			request.set(new SubscriptionRequestType(SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST));
			request.set(new MarketDepth(0));
	
			MarketDataRequest.NoMDEntryTypes noMdEntryTypes1 = new MarketDataRequest.NoMDEntryTypes();
			noMdEntryTypes1.set(new MDEntryType(MDEntryType.BID));		
			request.addGroup(noMdEntryTypes1);
			
			MarketDataRequest.NoMDEntryTypes noMdEntryTypes2 = new MarketDataRequest.NoMDEntryTypes();
			noMdEntryTypes2.set(new MDEntryType(MDEntryType.OFFER));		
			request.addGroup(noMdEntryTypes2);
	
		    
			MarketDataRequest.NoRelatedSym norelatedSym = new MarketDataRequest.NoRelatedSym();
			norelatedSym.set(new Symbol("USD/JPY"));
			request.addGroup(norelatedSym);
			sendQuoteClient(request);
		}
		else{
			System.out.println("Org Md Req Id is empty, please make a new Market Data Request First");
		}
	}
	

	/**
	 * <p>
	 * Description:Send msg by Quote Client's session
	 * </p>
	 * 
	 * @author Ivan Huo
	 * @date 2016-08-03
	 * @param msg
	 */
	private static void sendQuoteClient(Message msg) {
		if (null != starter) {
			QuoteClient client = (QuoteClient) starter;
			ArrayList<SessionID> sessionIds = client.getInitiator().getSessions();
			for (SessionID sessionID : sessionIds) {
				Session session = Session.lookupSession(sessionID);
				session.send(msg);
			}
		} else {
			System.out.println("The starter of Quote has not been initiated");
		}
	}

	/**
	 * <p>
	 * Description:Send msg by Trade Client's session
	 * </p>
	 * 
	 * @author Ivan Huo
	 * @date 2016-08-03
	 * @param msg
	 */
	private static void sendTradeClient(Message msg) {
		if (null != starter) {
			TradeClient client = (TradeClient) starter;
			ArrayList<SessionID> sessionIds = client.getInitiator().getSessions();
			for (SessionID sessionID : sessionIds) {
				Session session = Session.lookupSession(sessionID);
				session.send(msg);
			}
		} else {
			System.out.println("The starter of Trade has not been initiated");
		}
	}

}
