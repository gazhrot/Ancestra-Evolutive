package org.ancestra.evolutive.exchange;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.ancestra.evolutive.kernel.Console;
import org.ancestra.evolutive.kernel.Main;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class ExchangeServer {

	SocketAcceptor acceptor = new NioSocketAcceptor();
	
	public ExchangeServer() {
		acceptor.setReuseAddress(true);
		acceptor.setHandler(new ExchangeHandler());
	}
	
	public void start() {
		if(acceptor.isActive()) 
			return;
		
		try { 
			acceptor.bind(new InetSocketAddress(Main.config.getExchangeIp(), Main.config.getExchangePort()));
		} catch (IOException e) {
			Console.instance.write(e.toString());
			Console.instance.write("Fail to bind acceptor : " + e);
		} finally { 
			Console.instance.write(" > Exchange server started on port " + Main.config.getExchangePort()); 
		}
	}
	
	public void stop() {
		if(!acceptor.isActive())
			return;
		
		acceptor.unbind();
		
		for(IoSession session : acceptor.getManagedSessions().values())
			if(session.isConnected() || !session.isClosing()) 
				session.close(true);		
		
		acceptor.dispose();
		
		Console.instance.write("Exchange server stoped");
	}
}
