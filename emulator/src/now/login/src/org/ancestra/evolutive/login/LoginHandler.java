package org.ancestra.evolutive.login;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.ancestra.evolutive.kernel.Console;
import org.ancestra.evolutive.login.LoginClient.Status;
import org.ancestra.evolutive.object.Account;
import org.ancestra.evolutive.tool.packetfilter.PacketFilter;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class LoginHandler implements IoHandler {

	private static PacketFilter filter = new PacketFilter(5, 1, TimeUnit.SECONDS).activeSafeMode();
	
	@Override
	public void exceptionCaught(IoSession arg0, Throwable arg1)
			throws Exception {
		Console.instance.write("session " + arg0.getId() + " exception : " + arg1.getMessage());
	}
	
	@Override
	public void messageReceived(IoSession arg0, Object arg1) throws Exception {
		String packet = (String) arg1;
		
		String[] s = packet.split("\n");
		int i = 0;
		do {
			Console.instance.write("session " + arg0.getId() +" : recv < " + s[i]);
			LoginClient.clients.get(arg0.getId()).parser(s[i]);
			i++;
		} while(i == s.length - 1);
	}
	
	@Override
	public void messageSent(IoSession arg0, Object arg1) throws Exception {
		Console.instance.write("session " + arg0.getId() +" : sent > " + arg1.toString());
	}
	
	@Override
	public void sessionClosed(IoSession arg0) throws Exception {
		Console.instance.write("session " + arg0.getId() + " closed");
		LoginClient client = LoginClient.clients.get(arg0.getId());
		Account account = client.getAccount();
		account.setState(0);
	}
	
	@Override
	public void sessionCreated(IoSession arg0) throws Exception {
		if(!filter.authorizes(arg0.getRemoteAddress().toString().substring(1).split("\\:")[0]))
			arg0.close(true);
		else {
			Console.instance.write("session " + arg0.getId() + " created");
			
			LoginClient client = new LoginClient(arg0, genKey());
			
			client.send("HC" + client.getKey());
			client.setStatus(Status.WAIT_VERSION);
		}
	}
	
	@Override
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
		Console.instance.write("session " + arg0.getId() + " idle");
	}
	
	@Override
	public void sessionOpened(IoSession arg0) throws Exception {
		Console.instance.write("session " + arg0.getId() + " oppened");
	}
	
	public static synchronized void sendToAll(String packet) {
		for(LoginClient client : LoginClient.clients.values()) {
			IoSession ioSession = client.getIoSession();
			
			if(ioSession.isConnected() || !ioSession.isClosing())
				client.send(packet);
		}
	}
	
	public String genKey() {
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		StringBuilder hashKey = new StringBuilder();
		Random rand = new Random();

		for(int i = 0; i < 32; i++) 
			hashKey.append(alphabet.charAt(rand.nextInt(alphabet.length())));
		return hashKey.toString();
	}
}
