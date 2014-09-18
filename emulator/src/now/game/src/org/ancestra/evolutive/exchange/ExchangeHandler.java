package org.ancestra.evolutive.exchange;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.ancestra.evolutive.client.PlayerMigration;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.exchange.packet.PacketHandler;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class ExchangeHandler extends IoHandlerAdapter {
	
	@Override
    public void sessionCreated(IoSession arg0) throws Exception {
		ExchangeClient client = Server.config.getExchangeClient();
		client.logger.info("connection created");
		client.setSession(arg0);
    }
	
    @Override
    public void messageReceived(IoSession arg0, Object arg1) throws Exception {
    	String packet = ioBufferToString(arg1);
    	
		if(packet.startsWith("MS")) {
			IoBuffer buffer = (IoBuffer) arg1;
			int size = 4 + String.valueOf(Server.config.getServerId()).length();
			buffer.get(new byte[size], 0, size);
			
			PlayerMigration migration = (PlayerMigration) buffer.getObject();
			System.out.println(migration.objects);
			
		} else {
	    	Server.config.getExchangeClient().logger.debug(" recv < {}", packet);
	        PacketHandler.parser(packet);
		}
    }
    
    @Override
    public void messageSent(IoSession arg0, Object arg1) throws Exception {
    	String packet = ioBufferToString(arg1);
    	Server.config.getExchangeClient().logger.info(" send > {}", packet);
    }
    
    @Override
    public void sessionClosed(IoSession arg0) throws Exception {
    	ExchangeClient client = Server.config.getExchangeClient();
    	client.logger.info("connection lost with the login server");
    	client.restart();
    }
    
    @Override
    public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {
    	Server.config.getExchangeClient().logger.info("connection exception : ");
    	arg1.printStackTrace();
    }
    
	public static String ioBufferToString(Object o) {
    	IoBuffer ioBuffer = IoBuffer.allocate(2048);
    	ioBuffer.put((IoBuffer) o);
    	ioBuffer.flip();
    		
    	CharsetDecoder charsetDecoder = Charset.forName("UTF-8").newDecoder();
    	
    	try { 
    		return ioBuffer.getString(charsetDecoder);
		} catch (CharacterCodingException e) {}
    	
    	return "undefined";
	}
}
