package com.fortex.quickRing.handler;

import org.apache.log4j.Logger;

import com.lmax.disruptor.EventHandler;

import quickfix.mina.RingBufferStrategy.SessionMessageEvent;

public class MessageHandler implements EventHandler<SessionMessageEvent> {
	@Override
	public void onEvent(SessionMessageEvent event, long sequence, boolean endOfBatch) throws Exception {
		try {
			event.getQuickfixSession().next(event.getMessage());
		} 
		catch (Exception e) {
			if(e.getMessage().contains("Tried to send a reject"))
				event.getQuickfixSession().disconnect(e.getMessage(), true);
			Logger.getLogger("EventError").error(e.getMessage(), e);
		}
	}
	
}
