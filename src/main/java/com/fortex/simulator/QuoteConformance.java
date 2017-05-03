/**<p>Description</p>
 * @author Ivan Huo
 */
package com.fortex.simulator;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import com.fortex.quickRing.db.DBOperation;

import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MDUpdateType;
import quickfix.field.MarketDepth;
import quickfix.field.QuoteReqID;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.fix44.Logout;
import quickfix.fix44.MarketDataRequest;

/**
 * @author Administrator
 *
 */
public class QuoteConformance {
	private static final String CONFIG_FILE_PATH = "server.properties";
	private static AbstractServer starter;
	private static String dataFile = System.getProperty("user.dir") + File.separator + "configs" + File.separator
			+ "clientConfigs.xlsx.quote";
	private static String targetFilePath = System.getProperty("user.dir") + File.separator + "configs" + File.separator;

	private static final CountDownLatch shutdownLatch = new CountDownLatch(1);

	private static long TIMEOUT_MILLSEC = 600000;

	public void start(String configFile) throws Exception {

	}

	public static Properties readConfig(String filePath) throws Exception {
		Properties prop = new Properties();
		InputStream is = App.class.getResourceAsStream(CONFIG_FILE_PATH);
		prop.load(is);
		return prop;
	}

	public static void main(String args[]) throws Exception {

		
		System.out
				.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out
				.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$ Fix Client Side Demo(Market Data) $$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out
				.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ By Fortex $$$$$$$");
		System.out
				.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("\n");
		
		
		while (true) {
			String step = chooseStep();
			switch (step) {
			case "1":{
				step1();
				break;
			}
			case "2": {
				step2();
				break;
			}
			case "3": {
				step3();
				break;
			}
			case "4": {
				step4();
				break;
			}
			case "5": {
				step5();
				break;
			}
			case "6": {
				step6();
				break;
			}
			case "7": {
				step7();
				break;
			}
			case "8": {
				step8();
				break;
			}
			}
		}
	}

	private static String chooseStep() {
		System.out.println("Step1.Auto connect and logon to Fortex Fix Server");
		System.out.println("Step2.Market Data Request for EUR/USD(Full Book, MarketDepth = 0)");
		System.out.println("Step3.Unsubscribe of previous step");
		System.out.println("Step4.Market Data Request for EUR/USD(Top Book, MarketDepth = 1)");
		System.out.println("Step5.Unsubscribe of previous step");
		System.out.println("Step6.Market Data Request for EUR/USD(Report best 5 price tiers of Data, MarketDepth = 5)");
		System.out.println("Step7.Unsubscribe of previous step");
		System.out.println("Step8.Logout");
		System.out.print("Please select the step number you want:");
		String step = new Scanner(System.in).nextLine();
		return step;
	}
	
	private static void step1() throws Exception{
		Properties prop = readConfig(CONFIG_FILE_PATH);
		String starterClassName = prop.getProperty("starterQuoteClass");
		String username = "PFIXQUOTE01";
		Constructor<?> con = Class.forName(starterClassName).getConstructor(String.class);
		String serverPath = targetFilePath + "servers" + username + ".cfg";

		starter = (AbstractServer) con.newInstance(serverPath);
		starter.doExecute();
		long currentTime = System.currentTimeMillis();

		for (;;) {
			if (starter.isInitiatorLoggoned())
				break;
			if ((System.currentTimeMillis() - currentTime) > TIMEOUT_MILLSEC) {
				System.out.println(
						"Error login timeout, please check your connection configuration and reboot this application!");
				return;
			}
		}

		System.out.println("...Connected and Logoned successfully!");
	}

	private static void step2() throws InterruptedException {
		System.out.println("...Subscribing Market Data");
		quickfix.fix44.MarketDataRequest request = new quickfix.fix44.MarketDataRequest();
		String mDReqId = UUID.randomUUID().toString().substring(0, 16);

		request.set(new MDReqID(mDReqId));
		request.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
		request.set(new MarketDepth(0));
		request.set(new MDUpdateType(0));
		MarketDataRequest.NoMDEntryTypes noMdEntryTypes1 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes1.set(new MDEntryType(MDEntryType.BID));
		request.addGroup(noMdEntryTypes1);

		MarketDataRequest.NoMDEntryTypes noMdEntryTypes2 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes2.set(new MDEntryType(MDEntryType.OFFER));
		request.addGroup(noMdEntryTypes2);

		MarketDataRequest.NoRelatedSym norelatedSym = new MarketDataRequest.NoRelatedSym();
		norelatedSym.set(new Symbol("EUR/USD"));
		request.addGroup(norelatedSym);
		starter.sendMsg(request);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");
	}

	private static void step3() throws InterruptedException {
		System.out.println("...Unsubscribing");
		quickfix.fix44.MarketDataRequest request = new quickfix.fix44.MarketDataRequest();
		String mDReqId = UUID.randomUUID().toString().substring(0, 16);

		request.set(new MDReqID(mDReqId));
		request.set(new SubscriptionRequestType(SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST));
		request.set(new MarketDepth(0));
		request.set(new MDUpdateType(0));

		MarketDataRequest.NoMDEntryTypes noMdEntryTypes1 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes1.set(new MDEntryType(MDEntryType.BID));
		request.addGroup(noMdEntryTypes1);

		MarketDataRequest.NoMDEntryTypes noMdEntryTypes2 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes2.set(new MDEntryType(MDEntryType.OFFER));
		request.addGroup(noMdEntryTypes2);

		MarketDataRequest.NoRelatedSym norelatedSym = new MarketDataRequest.NoRelatedSym();
		// com.fortex.simulator.utils.FxTableModel model = entry.getValue();
		norelatedSym.set(new Symbol("EUR/USD"));
		request.addGroup(norelatedSym);
		starter.sendMsg(request);
		Thread.sleep(3000);
	}

	private static void step4() throws InterruptedException {
		System.out.println("...Subscribing Market Data");

		quickfix.fix44.MarketDataRequest request = new quickfix.fix44.MarketDataRequest();
		String mDReqId = UUID.randomUUID().toString().substring(0, 16);

		request.set(new MDReqID(mDReqId));
		request.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
		request.set(new MarketDepth(1));
		request.set(new MDUpdateType(0));
		MarketDataRequest.NoMDEntryTypes noMdEntryTypes1 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes1.set(new MDEntryType(MDEntryType.BID));
		request.addGroup(noMdEntryTypes1);

		MarketDataRequest.NoMDEntryTypes noMdEntryTypes2 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes2.set(new MDEntryType(MDEntryType.OFFER));
		request.addGroup(noMdEntryTypes2);

		MarketDataRequest.NoRelatedSym norelatedSym = new MarketDataRequest.NoRelatedSym();

		norelatedSym.set(new Symbol("EUR/USD"));
		request.addGroup(norelatedSym);
		starter.sendMsg(request);
		System.out.println("...Data received from Server. Check the status on conformance center");
		Thread.sleep(3000);
	}

	private static void step5() throws InterruptedException {
		System.out.println("...Unsubscribing");

		quickfix.fix44.MarketDataRequest request = new quickfix.fix44.MarketDataRequest();
		String mDReqId = UUID.randomUUID().toString().substring(0, 16);

		request.set(new MDReqID(mDReqId));
		request.set(new SubscriptionRequestType(SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST));
		request.set(new MarketDepth(1));
		request.set(new MDUpdateType(0));

		MarketDataRequest.NoMDEntryTypes noMdEntryTypes1 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes1.set(new MDEntryType(MDEntryType.BID));
		request.addGroup(noMdEntryTypes1);

		MarketDataRequest.NoMDEntryTypes noMdEntryTypes2 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes2.set(new MDEntryType(MDEntryType.OFFER));
		request.addGroup(noMdEntryTypes2);

		MarketDataRequest.NoRelatedSym norelatedSym = new MarketDataRequest.NoRelatedSym();

		norelatedSym.set(new Symbol("EUR/USD"));
		request.addGroup(norelatedSym);
		starter.sendMsg(request);

		Thread.sleep(3000);
	}

	private static void step6() throws InterruptedException {
		System.out.println("...Subscribing Market Data");

		quickfix.fix44.MarketDataRequest request = new quickfix.fix44.MarketDataRequest();
		String mDReqId = UUID.randomUUID().toString().substring(0, 16);

		request.set(new MDReqID(mDReqId));
		request.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
		request.set(new MarketDepth(5));
		request.set(new MDUpdateType(0));
		MarketDataRequest.NoMDEntryTypes noMdEntryTypes1 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes1.set(new MDEntryType(MDEntryType.BID));
		request.addGroup(noMdEntryTypes1);

		MarketDataRequest.NoMDEntryTypes noMdEntryTypes2 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes2.set(new MDEntryType(MDEntryType.OFFER));
		request.addGroup(noMdEntryTypes2);

		MarketDataRequest.NoRelatedSym norelatedSym = new MarketDataRequest.NoRelatedSym();

		norelatedSym.set(new Symbol("EUR/USD"));
		request.addGroup(norelatedSym);
		starter.sendMsg(request);
		System.out.println("...Data received from Server. Check the status on conformance center");
		Thread.sleep(3000);
	}

	private static void step7() throws InterruptedException {
		System.out.println("...Unsubscribing");

		quickfix.fix44.MarketDataRequest request = new quickfix.fix44.MarketDataRequest();
		String mDReqId = UUID.randomUUID().toString().substring(0, 16);

		request.set(new MDReqID(mDReqId));
		request.set(new SubscriptionRequestType(SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST));
		request.set(new MarketDepth(5));
		request.set(new MDUpdateType(0));

		MarketDataRequest.NoMDEntryTypes noMdEntryTypes1 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes1.set(new MDEntryType(MDEntryType.BID));
		request.addGroup(noMdEntryTypes1);

		MarketDataRequest.NoMDEntryTypes noMdEntryTypes2 = new MarketDataRequest.NoMDEntryTypes();
		noMdEntryTypes2.set(new MDEntryType(MDEntryType.OFFER));
		request.addGroup(noMdEntryTypes2);

		MarketDataRequest.NoRelatedSym norelatedSym = new MarketDataRequest.NoRelatedSym();
		// com.fortex.simulator.utils.FxTableModel model = entry.getValue();
		norelatedSym.set(new Symbol("EUR/USD"));
		request.addGroup(norelatedSym);
		starter.sendMsg(request);
		Thread.sleep(3000);
	}

	private static void step8() {
		Logout request = new Logout();
		starter.sendMsg(request);
	}

}
