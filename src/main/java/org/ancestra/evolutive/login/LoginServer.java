package org.ancestra.evolutive.login;

import org.ancestra.evolutive.core.Server;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class LoginServer {
	
	public static int totalNonAbo = 0;//Total de connections non abo
	public static int totalAbo = 0;//Total de connections abo
	public static int queueID = -1;//Numero de la queue
	public static int subscribe = 1;//File des non abonnes (0) ou abonnees (1)
	
	private final Map<Long, LoginClient> clients = new HashMap<>();
	private final Logger logger = (Logger) LoggerFactory.getLogger("RealmServer");
    private final IoAcceptor acceptor;
	
	public LoginServer() {
		acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("realm-codec-filter", 
				new ProtocolCodecFilter(
				new TextLineCodecFactory(Charset.forName("UTF8"), LineDelimiter.NUL, 
				new LineDelimiter("\n\0"))));
		acceptor.setHandler(new LoginHandler());
	}
	
	public void initialize() {
		try { 
			acceptor.bind(new InetSocketAddress(Server.config.getRealmPort()));
		} catch (IOException e) {
			logger.error("Can t create realm ", e);
			System.exit(1);
		}
	}
	
	public void close() {
		 acceptor.unbind();
		 
		 for (IoSession session : acceptor.getManagedSessions().values())
			 if (session.isConnected() || !session.isClosing()) 
				 session.close(true);
	     
	     acceptor.dispose();
	}

	public Map<Long, LoginClient> getClients() {
		return clients;
	}
}
