package com.fortex.simulator;

 
import com.fortex.log.FixServerLogFactory;
import com.fortex.quickRing.handler.MessageHandler;
import com.fortex.simulator.utils.CfgSetting;

import quickfix.Application;
import quickfix.DefaultMessageFactory;
import quickfix.FileStoreFactory;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RingBufferSocketInitiator;
import quickfix.ScreenLogFactory;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.ThreadedSocketInitiator;
import quickfix.fix44.Message;


public abstract class AbstractServer  {
	private static enum CONNECTION_TYPE{INITIATOR,ACCEPTOR};
	
	protected Initiator initiator;
	
	protected CfgSetting cfg;
	
	public AbstractServer(String initiatorFilePath   ) throws Exception{
		this.initialize(initiatorFilePath, CONNECTION_TYPE.INITIATOR);
	}
	
	/**
	 * <p>Description:Initialize the server</p> 
	 * @author Ivan Huo
	 * @date 2016-08-02	
	 * @param confFilePath
	 * @param serverType
	 * @throws Exception
	 */
	private void initialize(String confFilePath,  CONNECTION_TYPE serverType) throws Exception{
		//SessionSettings settings = new SessionSettings(AbstractServer.class.getResourceAsStream(confFilePath));
		SessionSettings settings = new SessionSettings(confFilePath);
		cfg = new CfgSetting(confFilePath); 
		//LogFactory logFactory = new ScreenLogFactory( true,false,false);
		LogFactory logFactory = new FixServerLogFactory(settings);
		//LogFactory logFactory = new ScreenLogFactory(settings);
		MessageFactory messageFactory = new DefaultMessageFactory();
		MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings); 
		initiator = new SocketInitiator(getInitiatorApplication(), messageStoreFactory, settings, logFactory  ,       messageFactory);
	//	initiator = new RingBufferSocketInitiator(getInitiatorApplication(), messageStoreFactory, settings, logFactory,messageFactory, new MessageHandler[]{new MessageHandler()}, 16384);		
		
	}
	
 	protected abstract Application getInitiatorApplication();
	
 	protected abstract Application getAcceptorApplication();
	
 	public abstract void doExecute();
	
 
	
	/**
	 * <p>Description:Check if the initiator is logged on status</p> 
	 * @author Ivan Huo
	 * @date 2016-08-04	
	 * @return
	 */
	public boolean isInitiatorLoggoned(){
		return this.initiator.isLoggedOn();
	}
	
	
	public Initiator getInitiator() {
		return initiator;
	}

	public void setInitiator(Initiator initiator) {
		this.initiator = initiator;
	}

	public CfgSetting getCfg() {
		return cfg;
	}

	public void setCfg(CfgSetting cfg) {
		this.cfg = cfg;
	}
	
	public abstract void sendMsg(Message msg);
}
