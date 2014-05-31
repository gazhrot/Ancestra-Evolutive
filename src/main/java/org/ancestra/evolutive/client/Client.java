package org.ancestra.evolutive.client;

import org.apache.mina.core.session.IoSession;

public interface Client {
	public IoSession getSession();
	public Account getAccount();
    public void send(String message);
}
