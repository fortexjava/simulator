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

import quickfix.field.Account;
import quickfix.field.ClOrdID;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.fix44.Logout;

/**
 * @author Administrator
 *
 */
public class TradeConformance {
	private static final String CONFIG_FILE_PATH = "server.properties";
	private static AbstractServer starter;
	private static String dataFile = System.getProperty("user.dir") + File.separator + "configs" + File.separator
			+ "clientConfigs.xlsx.trade";
	private static String targetFilePath = System.getProperty("user.dir") + File.separator + "configs" + File.separator;
	private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
	private static Integer TASISIZE = 10000;
	private static Integer SENDPERSEC = 1;
	private static Integer TOTAL_USED_MILLSEC = 60000;
	private static Integer serverIndex = 0;
	private static long TIMEOUT_MILLSEC = 10000;
	private static String clorder = "";

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

		System.out
				.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out
				.println("$$$$$$$$$$$$$$$$$$$$$$$$$$ Fix Client Side Demo(New Order Single) $$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out
				.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ By Fortex $$$$$$$");
		System.out
				.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("\n");
		// System.out.println("Step1.Connecting and logon to Fortex Fix
		// Server");
		//
		//
		// AbstractServer server = servers.get(0);
		//
		// System.out.print("Step2.User buy 1,000,000 EUR / USD, @ Market
		// ,TIF=FOK");
		//
		//
		// System.out.print("Step3.User sell 1,000,000 EUR / USD,@ Market
		// ,TIF=FOK");
		//
		//
		// System.out.print("Step4.User buy 1,000,000 EUR / USD @ 1.5,
		// TIF=FOK");
		//
		//
		// System.out.print("Step5.User sell 1,000,000 EUR / USD @ 1.5,
		// TIF=FOK");
		//
		//
		// System.out.print("Step6.User buy 1,000,000 EUR / USD, @ Market
		// ,TIF=IOC");
		//
		//
		// System.out.print("Step7.User sell 1,000,000 EUR / USD,@ Market
		// ,TIF=IOC");
		//
		//
		// System.out.print("Step8.User buy 1,000,000 EUR / USD @ 1.5,
		// TIF=IOC");
		//
		//
		// System.out.print("Step9.User sell 1,000,000 EUR / USD @ 1.5,
		// TIF=IOC");
		//
		//
		// System.out.print("Step10.Conformance Web turn off the auto pilot
		// ,user buy 1,000,000 EUR / USD @ 1, TIF=IOC");
		//
		//
		// System.out.print("Step11.User send a cancel request, receives
		// cancellation from FTS and return it to the user and ask the user to
		// cofirm");
		//
		//
		//
		// System.out.print("Step12.Conformance Web turn on the auto pilot, user
		// sell 1,000,000 EUR / USD @ 1 TIF=IOC, but the test server throws do
		// NOT send this execution report back to the user");
		//
		//
		// System.out.print("Step13.User send a cancel request, ask the user to
		// confirm the cancel reject");
		//
		//
		//
		// System.out.print("Step14.Conformance Web turn off the trading, user
		// buy 1,000,000 EUR / USD @ 1, TIF=FOK, ask the user to cofirm the
		// order is rejected, turn on the trading for the account");
		//
		//
		// System.out.print("Step15.Conformance Web turn off the auto pilot,
		// user buy 1,000,000 EUR / USD @ 1 TIF=FOK, xRing generate an cancel
		// request itself and send it FTS, ask the user to cofirm the order is
		// cancele");
		//
		//
		// System.out.print("Step16.Logout");
		while (true) {
			String step = chooseStep();
			switch (step){ 
				case "1": step1();break;				
				case "2":step2();break;
				case "3":step3();break;
				case "4":step4();break;
				case "5": step5();break;
				case "6": step6();break;
				case "7": step7();break;
				case "8": step8();break;
				case "9": step9();break;
				case "10": step10();break;
				case "11": step11();break;
				case "12": step12();break;
				case "13": step13();break;
				case "14": step14();break;
				case "15": step15();break;
				case "16": step16();break;
			}
		}
	}

	private static String sendOrder(AbstractServer server, String symbol, char orderType, char side, char timeinforce,
			int qty, double prize) {
		quickfix.fix44.NewOrderSingle order = new quickfix.fix44.NewOrderSingle();
		order.set(new Account(server.getCfg().getValue("Username")));
		order.set(new OrdType(orderType));
		if (orderType == OrdType.LIMIT) {
			order.set(new Price(prize));
		}
		order.set(new Side(side));
		String clOrderId = UUID.randomUUID().toString().substring(0, 16);
		order.set(new ClOrdID(clOrderId));
		order.set(new OrderQty(qty));
		order.set(new Symbol(symbol));
		order.set(new TimeInForce(timeinforce));
		order.set(new SecurityType(SecurityType.FOREIGN_EXCHANGE_CONTRACT));
		order.set(new TransactTime(new Date()));
		server.sendMsg(order);
		return clOrderId;
	}

	private static void sendOrderCancel(AbstractServer server, String symbol, char orderType, char side,
			char timeinforce, int qty, String clOrderId) {
		quickfix.fix44.OrderCancelRequest cancel = new quickfix.fix44.OrderCancelRequest();
		cancel.set(new ClOrdID(UUID.randomUUID().toString().substring(0, 16)));
		cancel.set(new OrigClOrdID(clOrderId));
		cancel.set(new Account(server.getCfg().getValue("Username")));
		cancel.set(new OrdType(orderType));
		cancel.set(new Side(side));
		cancel.set(new OrderQty(qty));
		cancel.set(new Symbol(symbol));
		cancel.set(new SecurityType(SecurityType.FOREIGN_EXCHANGE_CONTRACT));
		cancel.set(new TransactTime(new Date()));
		server.sendMsg(cancel);

	}

	private static String chooseStep() {
		System.out.println("Step1.Auto Connect and logon to Fortex Fix Server");
		System.out.println("Step2.User buy 1,000,000 EUR / USD, @ Market ,TIF=FOK");
		System.out.println("Step3.User sell 1,000,000 EUR / USD,@ Market ,TIF=FOK");
		System.out.println("Step4.User buy 1,000,000 EUR / USD @ 1.5, TIF=FOK");
		System.out.println("Step5.User sell 1,000,000 EUR / USD @ 1.5, TIF=FOK");
		System.out.println("Step6.User buy 1,000,000 EUR / USD, @ Market ,TIF=IOC");
		System.out.println("Step7.User sell 1,000,000 EUR / USD,@ Market ,TIF=IOC");
		System.out.println("Step8.User buy 1,000,000 EUR / USD @ 1.5, TIF=IOC");
		System.out.println("Step9.User sell 1,000,000 EUR / USD @ 1.5, TIF=IOC");
		System.out.println("Step10.Conformance Web turn off the auto pilot ,user buy 1,000,000 EUR / USD @ 1, TIF=IOC");
		System.out.println(
				"Step11.User send a cancel request, receives cancellation from FTS and return it to the user and ask the user to cofirm");
		System.out.println(
				"Step12.Conformance Web turn on the auto pilot, user sell 1,000,000 EUR / USD @ 1 TIF=IOC, but the test server throws do NOT send this execution report back to the user");
		System.out.println("Step13.User send a cancel request, ask the user to confirm the cancel reject");
		System.out.println(
				"Step14.Conformance Web turn off the trading, user buy 1,000,000 EUR / USD @ 1, TIF=FOK, ask the user to cofirm the order is rejected, turn on the trading for the account");
		System.out.println(
				"Step15.Conformance Web turn off the auto pilot, user buy 1,000,000 EUR / USD @ 1 TIF=FOK, xRing generate an cancel request itself and send it FTS, ask the user to cofirm the order is cancele");
		System.out.println("Step16.Logout");
		System.out.print("Please select the step number you want:");
		String step = new Scanner(System.in).nextLine();
		return step;
	}

	private static void step1() throws Exception {
		Properties prop = readConfig(CONFIG_FILE_PATH);
		String starterClassName = prop.getProperty("starterTradeClass");

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

			for (;;) {
				if (starter.isInitiatorLoggoned())
					break;
				if ((System.currentTimeMillis() - currentTime) > TIMEOUT_MILLSEC) {
					System.out.println(
							"Error login timeout, please check your connection configuration and reboot this application!");
					return;
				}
			}
		}
		System.out.println("...Connected and Logoned successfully!");
	}

	private static void step2() throws InterruptedException {		
		sendOrder(starter, "EUR/USD", OrdType.MARKET, Side.BUY, TimeInForce.FILL_OR_KILL, 1000000, 0);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");
	}

	private static void step3() throws InterruptedException {
		
		sendOrder(starter, "EUR/USD", OrdType.MARKET, Side.SELL, TimeInForce.FILL_OR_KILL, 1000000, 0);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");

	}

	private static void step4() throws InterruptedException {
		
		sendOrder(starter, "EUR/USD", OrdType.LIMIT, Side.BUY, TimeInForce.FILL_OR_KILL, 1000000, 1.5);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");

	}

	private static void step5() throws InterruptedException {
		
		sendOrder(starter, "EUR/USD", OrdType.LIMIT, Side.SELL, TimeInForce.FILL_OR_KILL, 1000000, 1.5);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");

	}

	private static void step6() throws InterruptedException {
		
		sendOrder(starter, "EUR/USD", OrdType.MARKET, Side.BUY, TimeInForce.IMMEDIATE_OR_CANCEL, 1000000, 0);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");
	}

	private static void step7() throws InterruptedException {
		
		sendOrder(starter, "EUR/USD", OrdType.MARKET, Side.SELL, TimeInForce.IMMEDIATE_OR_CANCEL, 1000000, 0);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");
	}

	private static void step8() throws InterruptedException {
		
		sendOrder(starter, "EUR/USD", OrdType.LIMIT, Side.BUY, TimeInForce.IMMEDIATE_OR_CANCEL, 1000000, 1.5);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");
	}

	private static void step9() throws InterruptedException {
		
		sendOrder(starter, "EUR/USD", OrdType.LIMIT, Side.SELL, TimeInForce.IMMEDIATE_OR_CANCEL, 1000000, 1.5);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");
	}

	private static void step10() throws InterruptedException {
		
		sendOrder(starter, "EUR/USD", OrdType.LIMIT, Side.BUY, TimeInForce.IMMEDIATE_OR_CANCEL,
				1000000, 1);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");
	}

	private static void step11() throws InterruptedException {
		
		sendOrderCancel(starter, "EUR/USD", OrdType.LIMIT, Side.BUY, TimeInForce.IMMEDIATE_OR_CANCEL, 1000000, clorder);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");
	}

	private static void step12() throws InterruptedException {
		
		clorder=sendOrder(starter, "EUR/USD", OrdType.LIMIT, Side.SELL, TimeInForce.IMMEDIATE_OR_CANCEL, 1000000, 1);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");
	}

	private static void step13() throws InterruptedException {
		
		sendOrderCancel(starter, "EUR/USD", OrdType.LIMIT, Side.SELL, TimeInForce.IMMEDIATE_OR_CANCEL, 1000000,
				clorder);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");
	}

	private static void step14() throws InterruptedException {
		
		clorder = sendOrder(starter, "EUR/USD", OrdType.LIMIT, Side.BUY, TimeInForce.FILL_OR_KILL, 1000000, 1);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");
	}

	private static void step15() throws InterruptedException {
		
		sendOrder(starter, "EUR/USD", OrdType.LIMIT, Side.BUY, TimeInForce.FILL_OR_KILL, 1000000, 1);
		Thread.sleep(3000);
		System.out.println("...Data received from Server. Check the status on conformance center");
	}

	private static void step16() {
		
		Logout request = new Logout();
		starter.sendMsg(request);
	}

}
