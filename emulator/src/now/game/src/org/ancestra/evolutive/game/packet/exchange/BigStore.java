package org.ancestra.evolutive.game.packet.exchange;

import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.hdv.Hdv;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("EH")
public class BigStore implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getIsTradingWith() > 0 || client.getPlayer().getFight() != null || client.getPlayer().isAway())
			return;
		
		switch(packet.charAt(2))
		{
			case 'B': //Confirmation d'achat
				String[] info = packet.substring(3).split("\\|");//ligneID|amount|price
				Hdv curHdv = World.data.getHdv(Math.abs(client.getPlayer().getIsTradingWith()));
				int ligneID = Integer.parseInt(info[0]);
				byte amount = Byte.parseByte(info[1]);
				
				if(curHdv.buyItem(ligneID,amount,Integer.parseInt(info[2]),client.getPlayer())) {
					SocketManager.GAME_SEND_EHm_PACKET(client.getPlayer(),"-",ligneID+"");//Enleve la ligne
					
					if(curHdv.getLigne(ligneID) != null && !curHdv.getLigne(ligneID).isEmpty())
						SocketManager.GAME_SEND_EHm_PACKET(client.getPlayer(), "+", curHdv.getLigne(ligneID).parseToEHm());//R�ajoute la ligne si elle n'est pas vide

					client.getPlayer().refreshStats();
					SocketManager.GAME_SEND_Ow_PACKET(client.getPlayer());
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(),"068");//Envoie le message "Lot achet�"
				}else {
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(),"172");//Envoie un message d'erreur d'achat
				}
			break;
			case 'l'://Demande listage d'un template (les prix)
				int template = Integer.parseInt(packet.substring(3));
				try	{
					SocketManager.GAME_SEND_EHl(client.getPlayer(), World.data.getHdv(Math.abs(client.getPlayer().getIsTradingWith())), template);
				}catch(NullPointerException e) {//Si erreur il y a, retire le template de la liste chez le client
					SocketManager.GAME_SEND_EHM_PACKET(client.getPlayer(), "-", template+"");
				}	
			break;
			case 'P'://Demande des prix moyen
				template = Integer.parseInt(packet.substring(3));
				SocketManager.GAME_SEND_EHP_PACKET(client.getPlayer(), template);
			break;			
			case 'T'://Demande des template de la cat�gorie
				int categ = Integer.parseInt(packet.substring(3));
				String allTemplate = World.data.getHdv(Math.abs(client.getPlayer().getIsTradingWith())).parseTemplate(categ);
				SocketManager.GAME_SEND_EHL_PACKET(client.getPlayer(), categ, allTemplate);
			break;			
		}
	}
}