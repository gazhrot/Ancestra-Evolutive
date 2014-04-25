package game.packet.exchange;

import client.Player;
import objects.Objet;
import objects.Objet.ObjTemplate;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import common.SocketManager;
import core.Log;
import core.World;

import game.GameClient;

@Packet("EB")
public class Buy implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		String[] infos = packet.substring(2).split("\\|");
		
        if(client.getPlayer().get_isTradingWith() > 0) {
            Player seller = World.data.getPersonnage(client.getPlayer().get_isTradingWith());
            
            if(seller != null) {
            	int itemID = 0, qua = 0, price = 0;
            	try {
            		itemID = Integer.valueOf(infos[0]);
            		qua = Integer.valueOf(infos[1]);
        		}catch(Exception e) { return; }
        		
                if(!seller.getStoreItems().containsKey(itemID) || qua <= 0) {
                    SocketManager.GAME_SEND_BUY_ERROR_PACKET(client);
                    return;
                }
               
                Objet itemStore = World.data.getObjet(itemID);
                
                if(itemStore == null) 
                	return;
             
                if(qua > itemStore.getQuantity()) 
                	qua = itemStore.getQuantity();
                
                int price1 = seller.getStoreItems().get(itemID);
                price = seller.getStoreItems().get(itemID)*qua;
                
                if(price > client.getPlayer().get_kamas())
                	return;
                if(qua == itemStore.getQuantity()) {
                	seller.getStoreItems().remove(itemStore.getGuid());
                	client.getPlayer().addObjet(itemStore, true);
                }else {
                	seller.getStoreItems().remove(itemStore.getGuid());
                	itemStore.setQuantity(itemStore.getQuantity()-qua);
                	World.database.getItemData().update(itemStore);
                	seller.addStoreItem(itemStore.getGuid(), price1);
                	
                	Objet clone = Objet.getCloneObjet(itemStore, qua);
                    World.database.getItemData().update(clone);
                    client.getPlayer().addObjet(clone, true);
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
	            if(seller.getStoreItems().isEmpty()) {
	            	if(World.data.getSeller(seller.get_curCarte().get_id()) != null && World.data.getSeller(seller.get_curCarte().get_id()).contains(seller.get_GUID())) {
	        			World.data.removeSeller(seller.get_GUID(), seller.get_curCarte().get_id());
	        			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(seller.get_curCarte(), seller.get_GUID());
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
			
			ObjTemplate template = World.data.getObjTemplate(tempID);
			
			if(template == null) {//Si l'objet demand� n'existe pas(ne devrait pas arriv�)
				Log.addToLog(client.getPlayer().get_name()+" tente d'acheter l'itemTemplate "+tempID+" qui est inexistant");
				SocketManager.GAME_SEND_BUY_ERROR_PACKET(client);
				return;
			}
			if(!client.getPlayer().get_curCarte().getNPC(client.getPlayer().get_isTradingWith()).get_template().haveItem(tempID)) {//Si le PNJ ne vend pas l'objet voulue
				Log.addToLog(client.getPlayer().get_name()+" tente d'acheter l'itemTemplate "+tempID+" que le present PNJ ne vend pas");
				SocketManager.GAME_SEND_BUY_ERROR_PACKET(client);
				return;
			}

			int prix = template.getPrix() * qua;
			if(client.getPlayer().get_kamas() < prix) {
				Log.addToLog(client.getPlayer().get_name()+" tente d'acheter l'itemTemplate "+tempID+" mais n'a pas l'argent necessaire");
				SocketManager.GAME_SEND_BUY_ERROR_PACKET(client);
				return;
			}
			
			Objet newObj = template.createNewItem(qua,false);
			long newKamas = client.getPlayer().get_kamas() - prix;
			client.getPlayer().set_kamas(newKamas);
			if(client.getPlayer().addObjet(newObj,true))
				World.data.addObjet(newObj,true);
			
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