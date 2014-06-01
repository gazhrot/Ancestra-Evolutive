package org.ancestra.evolutive.game;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.tool.packetfilter.PacketFilter;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.util.concurrent.TimeUnit;

public class GameHandler implements IoHandler {
	private static PacketFilter filter = new PacketFilter(5, 1, TimeUnit.SECONDS).activeSafeMode();
	
	@Override
	public void sessionCreated(IoSession arg0) throws Exception {
		if(!filter.authorizes(Constants.getIp(arg0.getRemoteAddress().toString())))
			arg0.close(true);
		else {
			GameClient client = new GameClient(arg0);
	
			SocketManager.GAME_SEND_HELLOGAME_PACKET(client);
			Server.config.getGameServer().getClients().put(arg0.getId(), client);
			
			if(Server.config.getGameServer().getClients().size() 
					> Server.config.getGameServer().getMaxPlayer())
				Server.config.getGameServer().updateMaxPlayer();
		}
	}
	
	@Override
	public void messageReceived(IoSession arg0, Object arg1) throws Exception {
		GameClient client = Server.config.getGameServer().getClients().get(arg0.getId());
		for(String str : ((String)arg1).split("\n")) {
            client.logger.debug(" recv < "+str);
            client.parsePacket(str);
		}
	}
	
	@Override
	public void sessionClosed(IoSession arg0) throws Exception {
		GameClient client = Server.config.getGameServer().getClients().get(arg0.getId());
		client.kick();
		Server.config.getGameServer().getClients().remove(client.getSession().getId());
	}

	@Override
	public void exceptionCaught(IoSession arg0, Throwable arg1)throws Exception {
        GameClient client = Server.config.getGameServer().getClients().get(arg0.getId());
        client.logger.error("exception ",arg1);
		Server.config.getGameServer().getClients().get(arg0.getId()).kick();
	}

	@Override
	public void messageSent(IoSession arg0, Object arg1) throws Exception {
        GameClient client = Server.config.getGameServer().getClients().get(arg0.getId());
        client.logger.debug(" send > {}", arg1);
        client.setLastPacketSent((String)arg1);
	}

	@Override
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
        GameClient client = Server.config.getGameServer().getClients().get(arg0.getId());

        client.logger.info(" disconnected for inactivity ",arg1);
		SocketManager.REALM_SEND_MESSAGE(client,"01|");
		client.kick();
		Server.config.getGameServer().getClients().remove(client.getSession().getId());
	}

	@Override
	public void sessionOpened(IoSession arg0) throws Exception {
		arg0.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60*15*1000);
	}
}
