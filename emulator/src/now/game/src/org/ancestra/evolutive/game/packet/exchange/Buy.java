package org.ancestra.evolutive.game.packet.exchange;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.object.ObjectTemplate;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("EB")
public class Buy implements PacketParser {

	@SuppressWarnings("deprecation")
	@Override
	public void parse(GameClient client, String packet) {
		String[] infos = packet.substring(2).split("\\|");
		
        if(client.getPlayer().getIsTradingWith() > 0) {
            Player seller = World.data.getPlayer(client.getPlayer().getIsTradingWith());
            
            if(seller != null) {
            	int itemID = 0, qua = 0, price = 0;
            	try {
            		itemID = Integer.valueOf(infos[0]);
            		qua = Integer.valueOf(infos[1]);
        		}catch(Exception e) { return; }
        		
                if(!seller.getStores().containsKey(itemID) || qua <= 0) {
                    SocketManager.GAME_SEND_BUY_ERROR_PACKET(client);
                    return;
                }
               
                Object itemStore = World.data.getObject(itemID);
                
                if(itemStore == null) 
                	return;
             
                if(qua > itemStore.getQuantity()) 
                	qua = itemStore.getQuantity();
                
                int price1 = seller.getStores().get(itemID);
                price = seller.getStores().get(itemID)*qua;
                
                if(price > client.getPlayer().getKamas())
                	return;
                if(qua == itemStore.getQuantity()) {
                	seller.getStores().remove(itemStore.getId());
                	client.getPlayer().addObject(itemStore, true);
                }else {
                	seller.getStores().remove(itemStore.getId());
                	itemStore.setQuantity(itemStore.getQuantity()-qua);
                	World.database.getItemData().update(itemStore);
                	seller.addStoreItem(itemStore.getId(), price1);
                	
                	Object clone = Object.getClone(itemStore, qua);
                    World.database.getItemData().update(clone);
                    client.getPlayer().addObject(clone, true);
                }
	            //remove kamas
	            client.getPlayer().addKamas(-price);
	            //add seller kamas
	            seller.addKamas(price);
	            seller.save();
	            //send packets
	            SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
	            SocketManager.GAME_SEND_ITEM_LIST_PACKET_SELLER(seller, client.getPlayer());
	            SocketManager.GAME_SEND_BUY_OK_PACKET(client);
	            if(seller.getStores().isEmpty()) {
	            	if(World.data.getSeller(seller.getMap()) != null && World.data.getSeller(seller.getMap()).contains(seller.getId())) {
	        			World.data.removeSeller(seller.getId(), seller.getMap().getId());
	        			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(seller.getMap(), seller.getId());
	        			World.data.getPacketJar().get("EV").parse(client, packet);
	        		}
	            }
            }
            return;
        }
        
		try	{
			int tempID = Integer.parseInt(infos[0]);
			int qua = Integer.parseInt(infos[1]);
			
			if(qua <= 0) 
				return;
			
			ObjectTemplate template = World.data.getObjectTemplate(tempID);
			
			if(template == null) {//Si l'objet demand� n'existe pas(ne devrait pas arriv�)
				Log.addToLog(client.getPlayer().getName()+" tente d'acheter l'itemTemplate "+tempID+" qui est inexistant");
				SocketManager.GAME_SEND_BUY_ERROR_PACKET(client);
				return;
			}
			if(!client.getPlayer().getMap().getNpcs().get(client.getPlayer().getIsTradingWith()).getTemplate().haveObject(tempID)) {//Si le PNJ ne vend pas l'objet voulue
				Log.addToLog(client.getPlayer().getName()+" tente d'acheter l'itemTemplate "+tempID+" que le present PNJ ne vend pas");
				SocketManager.GAME_SEND_BUY_ERROR_PACKET(client);
				return;
			}

			int prix = template.getPrice() * qua;
			if(client.getPlayer().getKamas() < prix) {
				Log.addToLog(client.getPlayer().getName()+" tente d'acheter l'itemTemplate "+tempID+" mais n'a pas l'argent necessaire");
				SocketManager.GAME_SEND_BUY_ERROR_PACKET(client);
				return;
			}
			
			Object newObj = template.createNewItem(qua,false);
			long newKamas = client.getPlayer().getKamas() - prix;
			client.getPlayer().setKamas(newKamas);
			if(client.getPlayer().addObject(newObj,true))
				World.data.addObject(newObj,true);
			
			SocketManager.GAME_SEND_BUY_OK_PACKET(client);
			SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
			SocketManager.GAME_SEND_Ow_PACKET(client.getPlayer());
		}catch(Exception e)	{
			e.printStackTrace();
			SocketManager.GAME_SEND_BUY_ERROR_PACKET(client);
			return;
		}
	}
}