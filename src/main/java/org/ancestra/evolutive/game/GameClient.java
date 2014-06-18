package org.ancestra.evolutive.game;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Client;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Commands;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.tool.packetfilter.PacketFilter;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class GameClient implements Client {
	
	private final IoSession session;
	private Account account;
	private Player player;
	private Commands command;
    private String lastPacketSent = "";

    public Logger logger = (Logger)LoggerFactory.getLogger(Client.class);


	private Map<Integer, GameAction> actions = new ConcurrentHashMap<>();
	private PacketFilter filter = new PacketFilter(10, 500, TimeUnit.MILLISECONDS);
	public long timeLastTradeMsg = 0, timeLastRecrutmentMsg = 0, timeLastAlignMsg = 0;
	
	public GameClient(IoSession session) {
		this.session = session;
        logger = (Logger)LoggerFactory.getLogger("gsession" + session.getId());
        logger.info("has been created");
	}	

	@Override
	public IoSession getSession() {
		return session;
	}

    @Override
	public Account getAccount() {
		return account;
	}

    @Override
	public void send(String message) {
        if(session.isConnected()) {
            this.session.write(message);
        }
    }

	public void setAccount(Account account) {
		this.account = account;
        logger = (Logger)LoggerFactory.getLogger("[account]" + account.getName());
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		if(this.player == null){
            this.player = player;
            logger = (Logger)LoggerFactory.getLogger("[player]" + player.getName());
        }
	}
	
	public Commands getCommand() {
		return command;
	}
	
	public void setCommand(Commands command) {
		this.command = command;
	}
	
	public Map<Integer, GameAction> getActions() {
		return actions;
	}

	public void setActions(Map<Integer, GameAction> actions) {
		this.actions = actions;
	}

	public PacketFilter getFilter() {
		return filter;
	}

	public void setFilter(PacketFilter filter) {
		this.filter = filter;
	}
	
	public void addAction(GameAction GA) {
        logger.trace("Create action id : "+GA.getId() + "action {},args {}",GA.getAction(),GA.getArgs());
        this.getActions().put(GA.getId(), GA);
	}
	
	public void removeAction(GameAction GA) {
		logger.trace("Delete action id : "+GA.getId());
		this.getActions().remove(GA.getId());
	}
	
	@SuppressWarnings("deprecation")
	public void parsePacket(String packet) throws Exception { 
		if(!verify(packet))
			return;
		
		/** Les plugins avant les packages. **/
		for(Entry<String, PacketParser> parser : World.data.getPacketPlugins().entrySet()) {
			try {
				if(parser.getKey().equals(packet.substring(0, parser.getKey().length()))) {
					parser.getValue().parse(this, packet);
					return;
				}
			} catch(Exception e) { continue; }
		}
		
		String prefix = packet.substring(0, 2);	
		PacketParser parser = World.data.getPacketJar().get(prefix);
        if(parser != null)
			parser.parse(this, packet);
		else 
			System.out.println(" <> Packet introuvable : "+ packet+" !");
	}

	public boolean verify(String packet) {
		if (!this.getFilter().authorizes(Constants.getIp(this.getSession().getRemoteAddress().toString())))
			this.kick();
		
		if(this.getPlayer() != null)
			this.getPlayer().setLastPacketTime(System.currentTimeMillis());
		
		if(packet.length() > 3 && packet.substring(0,4).equalsIgnoreCase("ping"))	{
			SocketManager.GAME_SEND_PONG(this);
			return false;
		}
		if(packet.length() > 4 && packet.substring(0,5).equalsIgnoreCase("qping")) {
			SocketManager.GAME_SEND_QPONG(this);
			return false;
		}
		return true;
	}
	
	public void closeSocket() {
		try {
			this.getSession().close(true);
		} catch (Exception e) {}
	}
	
	public void kick() {
		try {
    		if(this.getAccount() != null) {
    			if(this.getPlayer() != null)
    				this.getPlayer().save();
    			this.getAccount().disconnect();
    		}
    		this.getSession().close(true);
		} catch(Exception e1) {e1.printStackTrace();}
	}

    public String getLastPacketSent() {
        return lastPacketSent;
    }

    public void setLastPacketSent(String lastPacketSent) {
        this.lastPacketSent = lastPacketSent;
    }
}