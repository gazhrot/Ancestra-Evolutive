package org.ancestra.evolutive.login;

import java.util.*;

import org.ancestra.evolutive.login.packet.PacketHandler;
import org.ancestra.evolutive.object.Account;
import org.apache.mina.core.session.IoSession;

public class LoginClient {
	
	private long id;
	private IoSession ioSession;
	private String key;
	private Status status;
	private Account account;
	
	public static Map<Long, LoginClient> clients = new HashMap<Long, LoginClient>();
	
	public LoginClient(IoSession ioSession, String key) {
		setId(ioSession.getId());
		setIoSession(ioSession);
		setKey(key);
		
		clients.put(this.id, this);
	}
	
	public void send(Object object) {
		this.ioSession.write(object);
	}
	
	void parser(String packet) {
		PacketHandler.parser(this, packet);
	}
	
	public void kick() {
		if(LoginServer.clients.containsKey(this.getAccount().getName()))
			LoginServer.clients.remove(this.getAccount().getName());
		ioSession.close(true);
	}
	
	public long getId() {
		return id;
	}
	
	void setId(long l) {
		this.id = l;
	}
	
	public IoSession getIoSession() {
		return ioSession;
	}
	
	void setIoSession(IoSession ioSession) {
		this.ioSession = ioSession;
	}
	
	public String getKey() {
		return key;
	}
	
	void setKey(String key) {
		this.key = key;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}
	
	public enum Status {
		WAIT_VERSION, 
		WAIT_PASSWORD, 
		WAIT_ACCOUNT, 
		WAIT_NICKNAME, 
		SERVER;
	}
}
