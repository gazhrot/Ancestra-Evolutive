package fr.edofus.ancestra.evolutive.game.packet.exchange;



import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.HDV;
import fr.edofus.ancestra.evolutive.objects.NPC_tmpl;
import fr.edofus.ancestra.evolutive.objects.Percepteur;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("ER")
public class Request implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(packet.substring(2,4).equals("11"))//Ouverture HDV achat
		{
			if(client.getPlayer().get_isTradingWith() < 0)//Si d�j� ouvert
				SocketManager.GAME_SEND_EV_PACKET(client);
			if(client.getPlayer().getDeshonor() >= 5) {
				SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "183");
				return;
			}
			
			HDV toOpen = World.data.getHdv(client.getPlayer().get_curCarte().get_id());
			
			if(toOpen == null) 
				return;
			
			String info = "1,10,100;"+
						toOpen.getStrCategories()+
						";"+toOpen.parseTaxe()+
						";"+toOpen.getLvlMax()+
						";"+toOpen.getMaxItemCompte()+
						";-1;"+
						toOpen.getSellTime();
			SocketManager.GAME_SEND_ECK_PACKET(client.getPlayer(),11,info);
			client.getPlayer().set_isTradingWith(0 - client.getPlayer().get_curCarte().get_id());	//R�cup�re l'ID de la map et rend cette valeur n�gative
			return;
		}else 
		if(packet.substring(2,4).equals("10"))//Ouverture HDV vente
		{
			if(client.getPlayer().get_isTradingWith() < 0)//Si d�j� ouvert
				SocketManager.GAME_SEND_EV_PACKET(client);
			
			if(client.getPlayer().getDeshonor() >= 5) 
			{
				SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "183");
				return;
			}
			
			HDV toOpen = World.data.getHdv(client.getPlayer().get_curCarte().get_id());
			
			if(toOpen == null) return;
			
			String info = "1,10,100;"+
						toOpen.getStrCategories()+
						";"+toOpen.parseTaxe()+
						";"+toOpen.getLvlMax()+
						";"+toOpen.getMaxItemCompte()+
						";-1;"+
						toOpen.getSellTime();
			SocketManager.GAME_SEND_ECK_PACKET(client.getPlayer(),10,info);
			client.getPlayer().set_isTradingWith(0 - client.getPlayer().get_curCarte().get_id());	//R�cup�re l'ID de la map et rend cette valeur n�gative
			
			SocketManager.GAME_SEND_HDVITEM_SELLING(client.getPlayer());
			return;
		}
		switch(packet.charAt(2))
		{
			case '0'://Si NPC
				try	{
					int npcID = Integer.parseInt(packet.substring(4));
					NPC_tmpl.NPC npc = client.getPlayer().get_curCarte().getNPC(npcID);
					
					if(npc == null)
						return;
					
					SocketManager.GAME_SEND_ECK_PACKET(client, 0, npcID+"");
					SocketManager.GAME_SEND_ITEM_VENDOR_LIST_PACKET(client,npc);
					client.getPlayer().set_isTradingWith(npcID);
				} catch(NumberFormatException e) {}
			break;
			case '1'://Si joueur
				try	{
					int guidTarget = Integer.parseInt(packet.substring(4));
					Player target = World.data.getPersonnage(guidTarget);
					if(target == null) {
						SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(client,'E');
						return;
					}
					if(target.get_curCarte()!= client.getPlayer().get_curCarte() || !target.isOnline()) {//Si les persos ne sont pas sur la meme map
						SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(client,'E');
						return;
					}
					if(target.is_away() || client.getPlayer().is_away() || target.get_isTradingWith() != 0) {
						SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(client,'O');
						return;
					}
					SocketManager.GAME_SEND_EXCHANGE_REQUEST_OK(client, client.getPlayer().get_GUID(), guidTarget,1);
					SocketManager.GAME_SEND_EXCHANGE_REQUEST_OK(target.get_compte().getGameClient(),client.getPlayer().get_GUID(), guidTarget,1);
					client.getPlayer().set_isTradingWith(guidTarget);
					target.set_isTradingWith(client.getPlayer().get_GUID());
				} catch(NumberFormatException e) {}
			break;
            case '4'://StorePlayer
            	int id = 0;
            	try	{
            		id = Integer.valueOf(packet.split("\\|")[1]);
				} catch(NumberFormatException e) {return;}
				
				if(client.getPlayer().get_isTradingWith() > 0 || client.getPlayer().get_fight() != null || client.getPlayer().is_away())
					return;
				
				Player seller = World.data.getPersonnage(id);
				
				if(seller == null) 
					return;
				
				client.getPlayer().set_isTradingWith(id);
				SocketManager.GAME_SEND_ECK_PACKET(client.getPlayer(), 4, seller.get_GUID()+"");
				SocketManager.GAME_SEND_ITEM_LIST_PACKET_SELLER(seller, client.getPlayer());
            break;
			case '6'://StoreItems
				if(client.getPlayer().get_isTradingWith() > 0 || client.getPlayer().get_fight() != null || client.getPlayer().is_away())
					return;
                
				client.getPlayer().set_isTradingWith(client.getPlayer().get_GUID());
                SocketManager.GAME_SEND_ECK_PACKET(client.getPlayer(), 6, "");
                SocketManager.GAME_SEND_ITEM_LIST_PACKET_SELLER(client.getPlayer(), client.getPlayer());
			break;
			case '8'://Si Percepteur
				try	{
					id = Integer.parseInt(packet.substring(4));
					Percepteur perco = World.data.getPerco(id);
					
					if(perco == null || perco.get_inFight() > 0 || perco.get_Exchange())
						return;
					
					perco.set_Exchange(true);
					SocketManager.GAME_SEND_ECK_PACKET(client, 8, perco.getGuid()+"");
					SocketManager.GAME_SEND_ITEM_LIST_PACKET_PERCEPTEUR(client, perco);
					client.getPlayer().set_isTradingWith(perco.getGuid());
					client.getPlayer().set_isOnPercepteurID(perco.getGuid());
				} catch(NumberFormatException e) {}
			break;
		}
	}
}