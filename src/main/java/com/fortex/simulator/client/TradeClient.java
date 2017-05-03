package com.fortex.simulator.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import com.fortex.simulator.AbstractServer;
import com.fortex.simulator.utils.CountStatic;
 
import quickfix.Application;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.Account;
import quickfix.field.ClOrdID;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.fix44.Message;

public class TradeClient extends AbstractServer {
private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
	
	
	public TradeClient(String initiatorFilePath ) throws Exception {
		super(initiatorFilePath);
	
	}

	/*
	 * (non-Javadoc)
	 * @see com.fortex.simulator.AbstractServer#getInitiatorApplication()
	 */
	@Override
	public Application getInitiatorApplication() {
		return new TradeClientApplication(this.cfg);
	}

	/*
	 * (non-Javadoc)
	 * @see com.fortex.simulator.AbstractServer#getAcceptorApplication()
	 */
	@Override
	public Application getAcceptorApplication() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fortex.simulator.AbstractServer#doExecute()
	 */
	@Override
	public void doExecute() {
		try {
			this.initiator.start();			
			 //shutdownLatch.await();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendMsg(Message msg){
		ArrayList<SessionID> sessionIds = this.getInitiator().getSessions();
		for (SessionID sessionID : sessionIds) {
			Session session = Session.lookupSession(sessionID);
			session.send(msg);
		}
		/**
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		**/
	}

	public static void main(String[] args) {
		 
	}

	 
	
	
}
