package com.fortex.simulator.client;

import java.util.concurrent.atomic.AtomicInteger;

import com.fortex.simulator.utils.CfgSetting;
import com.fortex.simulator.utils.CountStatic;
import com.fortex.simulator.utils.ServerSetting;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;

public class QuoteClientApplication implements Application {
	
	private CfgSetting cfgSetting;
	
	private static AtomicInteger RECEIVE_COUNT = new AtomicInteger(0);
	private static long RECEIVE_COUNT_TIME_PER_SECOND = 0;
	
	public QuoteClientApplication(){
		
	}
	
	public QuoteClientApplication(CfgSetting cfg){
		this.cfgSetting=cfg;
	}

	/*
	 * (non-Javadoc)
	 * @see quickfix.Application#onCreate(quickfix.SessionID)
	 */
	@Override
	public void onCreate(SessionID sessionId) {
		// TODO Auto-generated method stub
		//System.out.println("**********client on create*****************");
	}
	
	/*
	 * (non-Javadoc)
	 * @see quickfix.Application#onLogon(quickfix.SessionID)
	 */
	@Override
	public void onLogon(SessionID sessionId) {
		// TODO Auto-generated method stub
		//System.out.println("**********client on onLogon*****************");
	}
	
	/*
	 * (non-Javadoc)
	 * @see quickfix.Application#onLogout(quickfix.SessionID)
	 */
	@Override
	public void onLogout(SessionID sessionId) {
		// TODO Auto-generated method stub
		//System.out.println("**********client on onLogout*****************");

	}
	
	/*
	 * (non-Javadoc)
	 * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void toAdmin(Message msg, SessionID sessionId) {
		// TODO Auto-generated method stub
	//	System.out.println("**********client on toAdmin*****************");
		try {
			final String msgType = msg.getHeader().getString(MsgType.FIELD);
			if (MsgType.LOGON.compareTo(msgType) == 0) {
//				msg.setString(quickfix.field.Username.FIELD, ServerSetting.getQuoteValue("Username"));
//				msg.setString(quickfix.field.Password.FIELD, ServerSetting.getQuoteValue("Password"));
				msg.setString(quickfix.field.Username.FIELD, cfgSetting.getValue("Username"));
				msg.setString(quickfix.field.Password.FIELD, cfgSetting.getValue("Password"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void fromAdmin(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		// TODO Auto-generated method stub
//		System.out.println("**********client on fromAdmin*****************");
//		System.out.println(message.toString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void toApp(Message message, SessionID sessionId) throws DoNotSend {
		// TODO Auto-generated method stub
		//System.out.println("**********client on toApp: " + message.toString());
		
		//CountStatic.TOTAL_SENDED.incrementAndGet();
	}
	
	/*
	 * (non-Javadoc)
	 * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void fromApp(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		// TODO Auto-generated method stub
		//System.out.println("**********client on fromApp*****************");		
	CountStatic.TOTAL_RECEIVED.incrementAndGet();
	cfgSetting.CLIENT_RECEIVED.incrementAndGet();
		
		/** 
		RECEIVE_COUNT.incrementAndGet();
		if (RECEIVE_COUNT.intValue() == 1)
			RECEIVE_COUNT_TIME_PER_SECOND = System.currentTimeMillis();
		if(System.currentTimeMillis() - RECEIVE_COUNT_TIME_PER_SECOND > 1000) {
			System.out.println("########total message :##########" + RECEIVE_COUNT);
			RECEIVE_COUNT.set(0);
			RECEIVE_COUNT_TIME_PER_SECOND = System.currentTimeMillis();			
		}
		**/
		
	}

}
