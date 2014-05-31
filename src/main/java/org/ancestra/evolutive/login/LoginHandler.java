package org.ancestra.evolutive.login;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.tool.packetfilter.PacketFilter;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class LoginHandler implements IoHandler {
	private static PacketFilter filter = new PacketFilter(5, 1, TimeUnit.SECONDS).activeSafeMode();
	
	@Override
	public void sessionCreated(IoSession arg0) throws Exception {
		if(!filter.authorizes(Constants.getIp(arg0.getRemoteAddress().toString())))
			arg0.close(true);
		else {
            String key = generateHashKey();
			LoginClient client = new LoginClient(arg0,key);
			
			if(Server.config.isPolicy())
				SocketManager.REALM_SEND_POLICY_FILE(client);
	        
			client.send("HC" + key);
			Server.config.getRealmServer().getClients().put(arg0.getId(), client);
			Console.instance.println("rSession "+arg0.getId()+" : created");
		}
	}
	
	@Override
	public void messageReceived(IoSession arg0, Object arg1) throws Exception { 
		String packet = (String) arg1;
		
		String[] toParse = packet.split("\n");
		
		for(int i=toParse.length ; i > 0 ; i--) {
			LoginClient client = Server.config.getRealmServer().getClients().get(arg0.getId());
			client.incrementPacket();
			client.parsePacket(toParse[toParse.length-i]);

			Console.instance.println("rSession "+arg0.getId()+" : recv < "+toParse[toParse.length-i]);
		}
	}
	
	@Override
	public void sessionClosed(IoSession arg0) throws Exception {
		LoginClient client = Server.config.getRealmServer().getClients().get(arg0.getId());
		client.kick();
		Server.config.getRealmServer().getClients().remove(client.getSession().getId());
	}

	@Override
	public void exceptionCaught(IoSession arg0, Throwable arg1)throws Exception {
		Console.instance.println("rSession "+arg0.getId()+" : exception "+arg1.getMessage());
	}

	@Override
	public void messageSent(IoSession arg0, Object arg1) throws Exception {
		Console.instance.println("rSession "+arg0.getId()+" : sent > "+arg1.toString());
	}

	@Override
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
		Console.instance.println("rSession "+arg0.getId()+" : disconnected ("+arg1.toString()+")");
		
		LoginClient client = Server.config.getRealmServer().getClients().get(arg0.getId());
		SocketManager.REALM_SEND_MESSAGE(client,"01|"); 
		client.kick();
		Server.config.getRealmServer().getClients().remove(client.getSession().getId());
	}

	@Override
	public void sessionOpened(IoSession arg0) throws Exception {
		arg0.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60*15*1000);
	}

    private String generateHashKey(){
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder hashkey = new StringBuilder();

        Random rand = new Random();

        for (int i=0; i<32; i++) {
            hashkey.append(alphabet.charAt(rand.nextInt(alphabet.length())));
        }
        return hashkey.toString();
    }
	
}
