package org.ancestra.evolutive.exchange;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.ancestra.evolutive.kernel.Console;
import org.ancestra.evolutive.kernel.Main;
import org.ancestra.evolutive.object.Server;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class ExchangeHandler implements IoHandler {
	
	@Override
	public void exceptionCaught(IoSession arg0, Throwable arg1)
			throws Exception {
		Console.instance.write("eSession " + arg0.getId() + " exception : " + arg1.getCause() + " : " + arg1.getMessage());
	}
	
	@Override
	public void messageReceived(IoSession arg0, Object arg1) throws Exception {
		String string = new String(((IoBuffer) arg1).array());
		Console.instance.write("eSession " + arg0.getId() + " < " + string);
		if(string.startsWith("MS")) {
			Server.get(Integer.parseInt(string.substring(2).split("\\|")[0])).send(arg1);
		} else {
			ExchangeClient.clients.get(arg0.getId()).parser(bufferToString(arg1));
		}
	}
	
	@Override
	public void messageSent(IoSession arg0, Object arg1) throws Exception {
		Console.instance.write("eSession " + arg0.getId() + " > " + bufferToString(arg1));
	}
	
	@Override
	public void sessionClosed(IoSession arg0) throws Exception {
		Console.instance.write("eSession " + arg0.getId() + " closed");
		
		ExchangeClient client = ExchangeClient.clients.get(arg0.getId());
		client.getServer().setState(0);
		
		Main.database.getAccountData().resetLogged(client.getServer().getId());
		
		ExchangeClient.clients.remove(arg0.getId());
	}
	
	@Override
	public void sessionCreated(IoSession arg0) throws Exception {
		Console.instance.write("eSession " + arg0.getId() + " created");

		new ExchangeClient(arg0.getId(), arg0);
		
    	IoBuffer ioBuffer = IoBuffer.allocate(2048);
		ioBuffer.put("SK?".getBytes());
		ioBuffer.flip();
		arg0.write(ioBuffer);
	}
	
	@Override
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
		Console.instance.write("eSession " + arg0.getId() + " idle");
	}
	
	@Override
	public void sessionOpened(IoSession arg0) throws Exception {}
	
	public String bufferToString(Object o) {
    	IoBuffer buffer = IoBuffer.allocate(2048);
    	buffer.put((IoBuffer) o);
    	buffer.flip();
    		
    	CharsetDecoder cd = Charset.forName("UTF-8").newDecoder();
    	
    	try { return buffer.getString(cd);
		} catch (CharacterCodingException e) { }
    	return "undefined";
	}
}
