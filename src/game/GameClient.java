package game;


import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;

import tool.packetfilter.PacketFilter;
import tool.plugin.packet.PacketParser;

import client.Client;
import client.Account;
import client.Player;

import common.Commands;
import common.Constants;
import common.SocketManager;
import common.World;

import core.Console;

public class GameClient implements Client {
	
	private IoSession session;
	private Account account;
	private Player player;
	private Commands command;
	
	private Map<Integer, GameAction> actions = new TreeMap<>();
	private PacketFilter filter = new PacketFilter(10, 500, TimeUnit.MILLISECONDS);
	public long timeLastTradeMsg = 0, timeLastRecrutmentMsg = 0, timeLastAlignMsg = 0;
	
	public GameClient(IoSession session) {
		this.session = session;
	}	

	@Override
	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession ioSession) {
		this.session = ioSession;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
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
		this.getActions().put(GA.getId(), GA);
		Console.instance.println("Game > Create action id : "+GA.getId());
		Console.instance.println("Game > Packet : "+GA.getPacket());
	}
	
	public void removeAction(GameAction GA) {
		Console.instance.println("Game > Delete action id : "+GA.getId());
		this.getActions().remove(GA.getId());
	}
	
	public void parsePacket(String packet) throws Exception { 
		if(!verify(packet))
			return;
		
		/** Les plugins avant les packages. **/
		for(Entry<String, PacketParser> parser : World.data.getPluginParsers().entrySet()) {
			if(parser.getKey().equals(packet.subSequence(0, parser.getKey().length()))) {
				parser.getValue().parse(this, packet);
			}
		}
		
		String prefix = (String) packet.subSequence(0, 2);	
		PacketParser parser = World.data.getParsers().get(prefix);
		if(parser != null)
			parser.parse(this, packet);
		else
			throw new Exception(" <> Packet parser not found !");
	}

	public boolean verify(String packet) {
		if (!this.getFilter().authorizes(Constants.getIp(this.getSession().getRemoteAddress().toString())))
			this.kick();
		
		if(this.getPlayer() != null)
			this.getPlayer().refreshLastPacketTime();
		
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
    			this.getAccount().deconnexion();
    		}
		} catch(Exception e1) {e1.printStackTrace();}
	}
	
	
}