package game.packet;

import client.Player;
import client.Player.Group;

import common.SocketManager;
import common.World;

import game.GameClient;
import game.packet.handler.Packet;

public class GroupPacket {
		
	@Packet("PA")
	public static void accept(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		if(client.getPlayer().getInvitation() == 0)
			return;
		
		Player player = World.data.getPersonnage(client.getPlayer().getInvitation());
		
		if(player == null)
			return;
		
		Group group = player.getGroup();
		
		if(group == null) {
			group = new Group(player, client.getPlayer());
			SocketManager.GAME_SEND_GROUP_CREATE(client, group);
			SocketManager.GAME_SEND_PL_PACKET(client, group);
			SocketManager.GAME_SEND_GROUP_CREATE(player.get_compte().getGameClient(),group);
			SocketManager.GAME_SEND_PL_PACKET(player.get_compte().getGameClient(), group);
			player.setGroup(group);
			SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(player.get_compte().getGameClient(), group);
		}else {
			SocketManager.GAME_SEND_GROUP_CREATE(client, group);
			SocketManager.GAME_SEND_PL_PACKET(client, group);
			SocketManager.GAME_SEND_PM_ADD_PACKET_TO_GROUP(group, client.getPlayer());
			group.addPerso(client.getPlayer());
		}
		
		client.getPlayer().setGroup(group);
		SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(client, group);
		SocketManager.GAME_SEND_PR_PACKET(player);
	}
	
	@Packet("PF")
	public static void follow(GameClient client, String packet) {
		Group group = client.getPlayer().getGroup();
		
		if(group == null)
			return;
		
		int toFollow = -1;
		
		try	{
			toFollow = Integer.parseInt(packet.substring(3));
		} catch(NumberFormatException e){return;};
		
		if(toFollow == -1) 
			return;
		
		Player player = World.data.getPersonnage(toFollow);
		
		if(player == null || !player.isOnline()) 
			return;
		
		if(packet.charAt(2) == '+') {//Suivre
			if(client.getPlayer()._Follows != null)
				client.getPlayer()._Follows._Follower.remove(client.getPlayer().get_GUID());
			SocketManager.GAME_SEND_FLAG_PACKET(client.getPlayer(), player);
			SocketManager.GAME_SEND_PF(client.getPlayer(), "+"+player.get_GUID());
			client.getPlayer()._Follows = player;
			player._Follower.put(client.getPlayer().get_GUID(), client.getPlayer());
		}else 
		if(packet.charAt(2) == '-') {//Ne plus suivre
			SocketManager.GAME_SEND_DELETE_FLAG_PACKET(client.getPlayer());
			SocketManager.GAME_SEND_PF(client.getPlayer(), "-");
			client.getPlayer()._Follows = null;
			player._Follower.remove(client.getPlayer().get_GUID());
		}
	}

	@Packet("PG")
	public static void followAll(GameClient client, String packet) {
		Group group = client.getPlayer().getGroup();
		
		if(group == null)
			return;
		
		int toFollow = -1;
		try	{
			toFollow = Integer.parseInt(packet.substring(3));
		} catch(NumberFormatException e) {return;}
		
		if(toFollow == -1) 
			return;
		
		Player player = World.data.getPersonnage(toFollow);
		
		if(player == null || !player.isOnline()) 
			return;
		
		if(packet.charAt(2) == '+') {//Suivre
			for(Player T : group.getPersos()) {
				if(T.get_GUID() == player.get_GUID()) 
					continue;
				if(T._Follows != null)
					T._Follows._Follower.remove(client.getPlayer().get_GUID());
				SocketManager.GAME_SEND_FLAG_PACKET(T, player);
				SocketManager.GAME_SEND_PF(T, "+"+player.get_GUID());
				T._Follows = player;
				player._Follower.put(T.get_GUID(), T);
			}
		}else 
		if(packet.charAt(2) == '-') {//Ne plus suivre
			for(Player p : group.getPersos()) {
				if(p.get_GUID() == player.get_GUID()) 
					continue;
				SocketManager.GAME_SEND_DELETE_FLAG_PACKET(p);
				SocketManager.GAME_SEND_PF(p, "-");
				p._Follows = null;
				player._Follower.remove(p.get_GUID());
			}
		}
	}

	@Packet("PI")
	public static void invite(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		
		String name = packet.substring(2);
		Player target = World.data.getPersoByName(name);
		
		if(target == null)
			return;
		if(!target.isOnline()) {
			SocketManager.GAME_SEND_GROUP_INVITATION_ERROR(client, "n"+name);
			return;
		}
		if(target.getGroup() != null) {
			SocketManager.GAME_SEND_GROUP_INVITATION_ERROR(client, "a"+name);
			return;
		}
		if(client.getPlayer().getGroup() != null && client.getPlayer().getGroup().getPersosNumber() == 8) {
			SocketManager.GAME_SEND_GROUP_INVITATION_ERROR(client, "f");
			return;
		}
		
		target.setInvitation(client.getPlayer().get_GUID());	
		client.getPlayer().setInvitation(target.get_GUID());
		SocketManager.GAME_SEND_GROUP_INVITATION(client, client.getPlayer().get_name(),name);
		SocketManager.GAME_SEND_GROUP_INVITATION(target.get_compte().getGameClient(), client.getPlayer().get_name(),name);
	}

	@Packet("PR")
	public static void decline(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		if(client.getPlayer().getInvitation() == 0)
			return;
		
		client.getPlayer().setInvitation(0);
		SocketManager.GAME_SEND_BN(client);
		
		Player player = World.data.getPersonnage(client.getPlayer().getInvitation());
		
		if(player == null) 
			return;
		
		player.setInvitation(0);
		SocketManager.GAME_SEND_PR_PACKET(player);
	}
	
	@Packet("PV")
	public static void leave(GameClient client, String packet) {
		if(client.getPlayer() == null)
			return;
		
		Group group = client.getPlayer().getGroup();
		
		if(group == null)
			return;
		
		if(packet.length() == 2) {//Si aucun guid est sp�cifi�, alors c'est que le joueur quitte
			group.leave(client.getPlayer());
			SocketManager.GAME_SEND_PV_PACKET(client, "");
			SocketManager.GAME_SEND_IH_PACKET(client.getPlayer(), "");
		}else 
		if(group.isChief(client.getPlayer().get_GUID())) {//Sinon, c'est qu'il kick un joueur du groupe
			int guid = -1;
			
			try {
				guid = Integer.parseInt(packet.substring(2));
			} catch(NumberFormatException e) {return;}
			
			if(guid == -1)
				return;
			
			Player player = World.data.getPersonnage(guid);
			
			group.leave(player);
			SocketManager.GAME_SEND_PV_PACKET(player.get_compte().getGameClient(), String.valueOf(client.getPlayer().get_GUID()));
			SocketManager.GAME_SEND_IH_PACKET(player, "");
		}
	}
	
	@Packet("PW")
	public static void locate(GameClient client, String packet)
	{
		if(client.getPlayer() == null)
			return;
		
		Group group = client.getPlayer().getGroup();
		
		if(group == null)
			return;
		
		String str = "";
		boolean isFirst = true;
		
		for(Player GroupP : client.getPlayer().getGroup().getPersos()) {
			if(!isFirst) 
				str += "|";
			str += GroupP.get_curCarte().getX()+";"+GroupP.get_curCarte().getY()+";"+GroupP.get_curCarte().get_id()+";2;"+GroupP.get_GUID()+";"+GroupP.get_name();
			isFirst = false;
		}
		
		SocketManager.GAME_SEND_IH_PACKET(client.getPlayer(), str);
	}
}