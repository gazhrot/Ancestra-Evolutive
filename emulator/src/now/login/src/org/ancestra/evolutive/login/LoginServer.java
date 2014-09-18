package org.ancestra.evolutive.login;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

import org.ancestra.evolutive.kernel.Console;
import org.ancestra.evolutive.kernel.Main;
import org.ancestra.evolutive.login.LoginHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class LoginServer {
	
	public static Map<String, LoginClient> clients = new TreeMap<String, LoginClient>();
	private static NioSocketAcceptor acceptor;
	
	public LoginServer() {
		acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new TextLineCodecFactory(Charset.forName("UTF8"), LineDelimiter.NUL, new LineDelimiter("\n\0"))));
		acceptor.setHandler(new LoginHandler());
	}
	
	public void start() {
		if(acceptor.isActive()) 
			return;
		
		try { 
			acceptor.bind(new InetSocketAddress(Main.config.getLoginPort()));
		} catch (IOException e) {
			Console.instance.write(e.toString());
			Console.instance.write("Fail to bind acceptor : " + e);
		} finally { 
			Console.instance.write(" > Login server started on port " + Main.config.getLoginPort()); 
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
		
		Console.instance.write("Login server stoped");
	}
}
