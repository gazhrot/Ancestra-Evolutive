package org.ancestra.evolutive.game.packet.game;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.CryptManager;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.game.GameAction;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.InteractiveObject;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("GK")
public class ActionAck implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().isNeedEndFightAction()) {
			final GameClient needed = client;
			client.getPlayer().getWaiter().addNext(new Runnable() {
				@Override
				public void run() {
					needed.getPlayer().getMap().applyEndFightAction(Constants.FIGHT_TYPE_PVM, needed.getPlayer());
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
					if(client.getPlayer().getFight() == null) {
                        client.getPlayer().setEmoteActive(0);
						client.getPlayer().getCell().removeCreature(client.getPlayer());
						client.send("BN");
						String path = GA.getArgs();
						//On prend la case cibl�e
						Case nextCell = client.getPlayer().getMap().getCases().get(CryptManager.cellCode_To_ID(path.substring(path.length()-2)));
						Case targetCell = client.getPlayer().getMap().getCases().get(CryptManager.cellCode_To_ID(GA.getPacket().substring(GA.getPacket().length()-2)));
						
						//On d�finie la case et on ajoute le personnage sur la case
						client.getPlayer().setPosition(nextCell);
						client.getPlayer().setOrientation(CryptManager.getIntByHashedValue(path.charAt(path.length()-3)));
						if(!client.getPlayer().isGhosts()) client.getPlayer().setAway(false);
						
						
						InteractiveObject IO = targetCell.getInteractiveObject();
						if(IO != null) {							
							IO.getActionIO(client.getPlayer(), targetCell);
							IO.getSignIO(client.getPlayer(), targetCell.getId());
						}

						client.getPlayer().getMap().onPlayerArriveOnCell(client.getPlayer(),client.getPlayer().getCell().getId());
						
						for(GameAction action: client.getActions().values()) 
							client.getPlayer().startActionOnCell(action);
                        client.removeAction(GA);
					} else { 
						client.getPlayer().getFight().onGK(client.getPlayer());
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
					client.getPlayer().getCell().removeCreature(client.getPlayer());
					client.getPlayer().setPosition(client.getPlayer().getMap().getCases().get(newCellID));
					client.getPlayer().setOrientation(CryptManager.getIntByHashedValue(path.charAt(path.length()-3)));
					SocketManager.GAME_SEND_BN(client);
                    client.getActions().clear();
				}
			break;
			case 500://Action Sur Map
				if(isOk){
                    client.getPlayer().finishActionOnCell(GA);
                } else {
                    //Si le joueur s'arrete sur une case
                    int newCellID = -1;

                    try	{
                        newCellID = Integer.parseInt(infos[1]);
                    } catch(Exception e) {return;}

                    if(newCellID == -1)
                        return;

                    String path = GA.getArgs();
                    client.getPlayer().getCell().removeCreature(client.getPlayer());
                    client.getPlayer().setPosition(client.getPlayer().getMap().getCases().get(newCellID));
                    client.getPlayer().setOrientation(CryptManager.getIntByHashedValue(path.charAt(path.length()-3)));
                    SocketManager.GAME_SEND_BN(client);
                    client.getActions().clear();
                }
			break;
        }

	}
}