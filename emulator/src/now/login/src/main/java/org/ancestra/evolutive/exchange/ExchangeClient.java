package org.ancestra.evolutive.exchange;

import java.util.HashMap;
import java.util.Map;

import org.ancestra.evolutive.exchange.packet.PacketHandler;
import org.ancestra.evolutive.object.Server;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

public class ExchangeClient {
	
	private long id;
	private IoSession ioSession;
	private Server server;
	
	public static Map<Long, ExchangeClient> clients = new HashMap<>();
	
	public ExchangeClient(long id, IoSession ioSession) {
		this.id = id;
		this.ioSession = ioSession;
		
		clients.put(this.id, this);
	}
	
	public static ExchangeClient get(long id) {
		return clients.get(id);
	}
	
	public void send(String s) {
    	IoBuffer ioBuffer = IoBuffer.allocate(2048);
		ioBuffer.put(s.getBytes());

		this.ioSession.write(ioBuffer.flip());
	}
	
	void parser(String packet) {
		PacketHandler.parser(this, packet);
	}
	
	public void kick() {
		this.ioSession.close(true);
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public IoSession getIoSession() {
		return ioSession;
	}
	
	public void setIoSession(IoSession ioSession) {
		this.ioSession = ioSession;
	}
	
	public Server getServer() {
		return server;
	}
	
	public void setServer(Server server) {
		this.server = server;
	}
}
