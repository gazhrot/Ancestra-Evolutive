package fr.edofus.ancestra.evolutive.game.packet.game;

import fr.edofus.ancestra.evolutive.common.Constants;
import fr.edofus.ancestra.evolutive.common.CryptManager;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.game.GameAction;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.Carte.Case;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("GK")
public class ActionAck implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().isNeedEndFightAction()) {
			final GameClient needed = client;
			client.getPlayer().getWaiter().addNext(new Runnable() {
				public void run() {
					needed.getPlayer().get_curCarte().applyEndFightAction(Constants.FIGHT_TYPE_PVM, needed.getPlayer());
					needed.getPlayer().refreshMapAfterFight();
					needed.getPlayer().setNeedEndFightAction(false);
				}
			}, 1000);
		}
		
		int GameActionId = -1;
		String[] infos = packet.substring(3).split("\\|");
		
		try	{
			GameActionId = Integer.parseInt(infos[0]);
		} catch(Exception e) {return;}
		
		if(GameActionId == -1)
			return;
		
		GameAction GA = client.getActions().get(GameActionId);
		
		if(GA == null)
			return;
		
		boolean isOk = packet.charAt(2) == 'K';
		
		switch(GA.getAction())
		{
			case 1://Deplacement
				if(isOk) {//Hors Combat
					if(client.getPlayer().get_fight() == null) {
						client.getPlayer().get_curCell().removePlayer(client.getPlayer().get_GUID());
						SocketManager.GAME_SEND_BN(client);
						String path = GA.getArgs();
						//On prend la case cibl�e
						Case nextCell = client.getPlayer().get_curCarte().getCase(CryptManager.cellCode_To_ID(path.substring(path.length()-2)));
						Case targetCell = client.getPlayer().get_curCarte().getCase(CryptManager.cellCode_To_ID(GA.getPacket().substring(GA.getPacket().length()-2)));
						
						//On d�finie la case et on ajoute le personnage sur la case
						client.getPlayer().set_curCell(nextCell);
						client.getPlayer().set_orientation(CryptManager.getIntByHashedValue(path.charAt(path.length()-3)));
						client.getPlayer().get_curCell().addPerso(client.getPlayer());
						if(!client.getPlayer()._isGhosts) client.getPlayer().set_away(false);
						
						if(targetCell.getObject() != null) {
							//Si c'est une "borne" comme Emotes, ou Cr�ation guilde
							if(targetCell.getObject().getID() == 1324) {
								Constants.applyPlotIOAction(client.getPlayer(),client.getPlayer().get_curCarte().get_id(),targetCell.getID());
							}else if(targetCell.getObject().getID() == 542) {
								if(client.getPlayer()._isGhosts) client.getPlayer().set_Alive();
							}
						}
						client.getPlayer().get_curCarte().onPlayerArriveOnCell(client.getPlayer(),client.getPlayer().get_curCell().getID());
						
						for(GameAction action: client.getActions().values()) 
							client.getPlayer().startActionOnCell(action);
					} else { 
						client.getPlayer().get_fight().onGK(client.getPlayer());
						return;
					}	
				}else {
					//Si le joueur s'arrete sur une case
					int newCellID = -1;
					
					try	{
						newCellID = Integer.parseInt(infos[1]);
					} catch(Exception e) {return;}
					
					if(newCellID == -1)
						return;
					
					String path = GA.getArgs();
					client.getPlayer().get_curCell().removePlayer(client.getPlayer().get_GUID());
					client.getPlayer().set_curCell(client.getPlayer().get_curCarte().getCase(newCellID));
					client.getPlayer().set_orientation(CryptManager.getIntByHashedValue(path.charAt(path.length()-3)));
					client.getPlayer().get_curCell().addPerso(client.getPlayer());
					SocketManager.GAME_SEND_BN(client);
				}
			break;
			case 500://Action Sur Map
				client.getPlayer().finishActionOnCell(GA);
			break;
		}
		client.removeAction(GA);
	}
}