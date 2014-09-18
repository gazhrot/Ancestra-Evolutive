package org.ancestra.evolutive.exchange;

import java.net.InetSocketAddress;

import org.ancestra.evolutive.core.Server;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class ExchangeClient {
	
	private IoConnector ioConnector = new NioSocketConnector();
	private IoSession ioSession;
	public ConnectFuture connectFuture;
	
	public Logger logger = (Logger) LoggerFactory.getLogger(ExchangeClient.class);
	
	public ExchangeClient() {
	
		ioConnector.setHandler(new ExchangeHandler());
	}
	
	public boolean initialize() {
		connectFuture = ioConnector.connect(new InetSocketAddress(Server.config.getExchangeIp(), Server.config.getExchangePort()));
		ioConnector.setConnectTimeoutMillis(2000);
		
		try { Thread.sleep(1000);
		} catch (InterruptedException e) { }
		
		if(!ioConnector.isActive()) {
			if(!Server.config.isRunning()) 
				return false;
			
			logger.info("try to connect to the login");
			
			this.restart();
			return ioConnector.isActive();
		}
		
		logger.info("has been connected");
		return !ioConnector.isActive();
	}
	
	public void restart() {
		if(!Server.config.isRunning()) 
			return;
		
		try {	
			Thread.sleep(1000);
		} catch(Exception e) {}
		
		logger.info("login server was not found");
		this.close();
		
		Server.config.setExchangeClient(new ExchangeClient());
		
    	while(Server.config.getExchangeClient().initialize());
	}
	
	public void close() {
		ioConnector.dispose();
		connectFuture.cancel();
		logger.info("exchange has stopped");
	}
	
	public void setSession(IoSession ioSession) {
		this.ioSession = ioSession;
	}
	
	public void send(String arg0) {
		ioSession.write(StringToIoBuffer(arg0));
	}
	
	public void send(IoBuffer arg0) {
		ioSession.write(arg0);
	}
	
	public static IoBuffer StringToIoBuffer(String arg0) {
    	IoBuffer ioBuffer = IoBuffer.allocate(2048);
		ioBuffer.put(arg0.getBytes());
		
		return ioBuffer.flip();
	}
}
