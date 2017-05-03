package com.fortex.simulator.client;

 
 
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import com.fortex.simulator.AbstractServer;

import quickfix.Application;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.fix44.Message;

public class QuoteClient extends AbstractServer{
	private static final CountDownLatch shutdownLatch = new CountDownLatch(1);

	public QuoteClient(String initiatorFilePath ) throws Exception {
		super(initiatorFilePath);
	
	}

	/*
	 * (non-Javadoc)
	 * @see com.fortex.simulator.AbstractServer#getInitiatorApplication()
	 */
	@Override
	public Application getInitiatorApplication() {
		return new QuoteClientApplication(this.cfg);
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
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		 
	}

	/* (non-Javadoc)
	 * @author Ivan Huo
	 * @see java.util.concurrent.Callable#call()
	 */
 

	/* (non-Javadoc)
	 * @author Ivan Huo
	 * @see com.fortex.simulator.AbstractServer#sendMsg(quickfix.fix44.Message)
	 */
	@Override
	public void sendMsg(Message msg) {
		// TODO Auto-generated method stub
		ArrayList<SessionID> sessionIds = this.getInitiator().getSessions();
		for (SessionID sessionID : sessionIds) {
			Session session = Session.lookupSession(sessionID);
			session.send(msg);
		}
	}
}
