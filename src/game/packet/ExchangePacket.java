package game.packet;

import objects.Carte;
import objects.Dragodinde;
import objects.Exchange;
import objects.HDV;
import objects.NPC_tmpl;
import objects.Objet;
import objects.Percepteur;
import objects.Trunk;
import objects.Carte.MountPark;
import objects.HDV.HdvEntry;
import objects.Objet.ObjTemplate;
import client.Player;

import common.Constants;
import common.Formulas;
import common.SocketManager;
import common.World;

import core.Log;
import core.Server;
import game.GameClient;
import game.packet.handler.Packet;

public class ExchangePacket {
	
	@Packet("EA")
	public static void accept(GameClient client, String packet) {
		if(client.getPlayer().get_isTradingWith() == 0)
			return;
		
		Player target = World.data.getPersonnage(client.getPlayer().get_isTradingWith());
		
		if(target == null)
			return;
		
		SocketManager.GAME_SEND_EXCHANGE_CONFIRM_OK(client, 1);
		SocketManager.GAME_SEND_EXCHANGE_CONFIRM_OK(target.get_compte().getGameClient(), 1);
		
		Exchange echg = new Exchange(target, client.getPlayer());
		client.getPlayer().setCurExchange(echg);
		client.getPlayer().set_isTradingWith(target.get_GUID());
		target.setCurExchange(echg);
		target.set_isTradingWith(client.getPlayer().get_GUID());
	}
	
	@Packet("EB")
	public static void buy(GameClient client, String packet) {
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
	        			finish(client, "");
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
			
			if(template == null) {//Si l'objet demandï¿½ n'existe pas(ne devrait pas arrivï¿½)
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
	
	@Packet("EH")
	public static void bigStore(GameClient client, String packet) {
		if(client.getPlayer().get_isTradingWith() > 0 || client.getPlayer().get_fight() != null || client.getPlayer().is_away())
			return;
		
		switch(packet.charAt(2))
		{
			case 'B': //Confirmation d'achat
				String[] info = packet.substring(3).split("\\|");//ligneID|amount|price
				HDV curHdv = World.data.getHdv(Math.abs(client.getPlayer().get_isTradingWith()));
				int ligneID = Integer.parseInt(info[0]);
				byte amount = Byte.parseByte(info[1]);
				
				if(curHdv.buyItem(ligneID,amount,Integer.parseInt(info[2]),client.getPlayer())) {
					SocketManager.GAME_SEND_EHm_PACKET(client.getPlayer(),"-",ligneID+"");//Enleve la ligne
					
					if(curHdv.getLigne(ligneID) != null && !curHdv.getLigne(ligneID).isEmpty())
						SocketManager.GAME_SEND_EHm_PACKET(client.getPlayer(), "+", curHdv.getLigne(ligneID).parseToEHm());//Rï¿½ajoute la ligne si elle n'est pas vide

					client.getPlayer().refreshStats();
					SocketManager.GAME_SEND_Ow_PACKET(client.getPlayer());
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(),"068");//Envoie le message "Lot achetï¿½"
				}else {
					SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(),"172");//Envoie un message d'erreur d'achat
				}
			break;
			case 'l'://Demande listage d'un template (les prix)
				int template = Integer.parseInt(packet.substring(3));
				try	{
					SocketManager.GAME_SEND_EHl(client.getPlayer(), World.data.getHdv(Math.abs(client.getPlayer().get_isTradingWith())), template);
				}catch(NullPointerException e) {//Si erreur il y a, retire le template de la liste chez le client
					SocketManager.GAME_SEND_EHM_PACKET(client.getPlayer(), "-", template+"");
				}	
			break;
			case 'P'://Demande des prix moyen
				template = Integer.parseInt(packet.substring(3));
				SocketManager.GAME_SEND_EHP_PACKET(client.getPlayer(), template);
			break;			
			case 'T'://Demande des template de la catï¿½gorie
				int categ = Integer.parseInt(packet.substring(3));
				String allTemplate = World.data.getHdv(Math.abs(client.getPlayer().get_isTradingWith())).parseTemplate(categ);
				SocketManager.GAME_SEND_EHL_PACKET(client.getPlayer(), categ, allTemplate);
			break;			
		}
	}
	
	@Packet("EK")
	public static void ready(GameClient client, String packet) {
		if(client.getPlayer().getCurJobAction() != null) {
			if(!client.getPlayer().getCurJobAction().isCraft())
				return;
			client.getPlayer().getCurJobAction().startCraft(client.getPlayer());
		}
		if(client.getPlayer().get_curExchange() == null)
			return;
		client.getPlayer().get_curExchange().toogleOK(client.getPlayer().get_GUID());
	}
	
	@Packet("EL")
	public static void putLastCraft(GameClient client, String packet) {
		if(client.getPlayer().getCurJobAction() != null)
			client.getPlayer().getCurJobAction().putLastCraftIngredients();
	}
	
	@Packet("EM")
	public static void moveItemOrKmas(GameClient client, String packet) {
		//Store
		if(client.getPlayer().get_isTradingWith() == client.getPlayer().get_GUID())
		{
			switch(packet.charAt(2))
			{
			case 'O'://Objets
				if(packet.charAt(3) == '+')
				{
					String[] infos = packet.substring(4).split("\\|");
					try
					{
						
						int guid = Integer.parseInt(infos[0]);
						int qua  = Integer.parseInt(infos[1]);
						int price  = Integer.parseInt(infos[2]);
						
						Objet obj = World.data.getObjet(guid);
						if(obj == null)return;
						
						if(qua > obj.getQuantity() || qua <= 0)
							qua = obj.getQuantity();
						
						client.getPlayer().addinStore(obj.getGuid(), price, qua);
						
					}catch(NumberFormatException e){};
				}else
				{
					String[] infos = packet.substring(4).split("\\|");
					try
					{
						int guid = Integer.parseInt(infos[0]);
						int qua  = Integer.parseInt(infos[1]);
						
						if(qua <= 0)return;
						
						Objet obj = World.data.getObjet(guid);
						if(obj == null)return;
						if(qua > obj.getQuantity())
                            qua = obj.getQuantity();
						
						client.getPlayer().removeFromStore(obj.getGuid(), qua);
					}catch(NumberFormatException e){};
				}
			break;
			}
			return;
		}
		//Percepteur
		if(client.getPlayer().get_isOnPercepteurID() != 0)
		{
			Percepteur perco = World.data.getPerco(client.getPlayer().get_isOnPercepteurID());
			if(perco == null || perco.get_inFight() > 0)return;
			switch(packet.charAt(2))
			{
			case 'G'://Kamas
				if(packet.charAt(3) == '-') //On retire
				{
					long P_Kamas = Integer.parseInt(packet.substring(4));
					long P_Retrait = perco.getKamas()-P_Kamas;
					if(P_Retrait < 0)
					{
						P_Retrait = 0;
						P_Kamas = perco.getKamas();
					}
					perco.setKamas(P_Retrait);
					client.getPlayer().addKamas(P_Kamas);
					SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
					SocketManager.GAME_SEND_EsK_PACKET(client.getPlayer(),"G"+perco.getKamas());
				}
			break;
			case 'O'://Objets
				if(packet.charAt(3) == '-') //On retire
				{
					String[] infos = packet.substring(4).split("\\|");
					int guid = 0;
					int qua = 0;
					try
					{
						guid = Integer.parseInt(infos[0]);
						qua  = Integer.parseInt(infos[1]);
					}catch(NumberFormatException e){};
					
					if(guid <= 0 || qua <= 0) return;
					
					Objet obj = World.data.getObjet(guid);
					if(obj == null)return;
                    else if(obj.getQuantity() < qua)
                        qua = obj.getQuantity();
					if(perco.HaveObjet(guid))
						perco.removeFromPercepteur(client.getPlayer(), guid, qua);

					perco.LogObjetDrop(guid, obj);
				}
			break;
			}
			client.getPlayer().get_guild().addXp(perco.getXp());
			perco.LogXpDrop(perco.getXp());
			perco.setXp(0);
			World.database.getGuildData().update(client.getPlayer().get_guild());
			return;
		}
		//HDV
		if(client.getPlayer().get_isTradingWith() < 0)
		{
			switch(packet.charAt(3))
			{
				case '-'://Retirer un objet de l'HDV
					int cheapestID = Integer.parseInt(packet.substring(4).split("\\|")[0]);
					int count = Integer.parseInt(packet.substring(4).split("\\|")[1]);
					if(count <= 0)return;
					
					client.getPlayer().get_compte().recoverItem(cheapestID,count);//Retire l'objet de la liste de vente du compte
					SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(client,'-',"",cheapestID+"");
				break;
				case '+'://Mettre un objet en vente
					int itmID = Integer.parseInt(packet.substring(4).split("\\|")[0]);
					byte amount = Byte.parseByte(packet.substring(4).split("\\|")[1]);
					int price = Integer.parseInt(packet.substring(4).split("\\|")[2]);
					if(amount <= 0 || price <= 0)return;
					
					HDV curHdv = World.data.getHdv(Math.abs(client.getPlayer().get_isTradingWith()));
					int taxe = (int)(price * (curHdv.getTaxe()/100));
					
					
					if(!client.getPlayer().hasItemGuid(itmID))//Vï¿½rifie si le personnage a bien l'item spï¿½cifiï¿½ et l'argent pour payer la taxe
						return;
					if(client.getPlayer().get_compte().countHdvItems(curHdv.getHdvID()) >= curHdv.getMaxItemCompte())
					{
						SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "058");
						return;
					}
					if(client.getPlayer().get_kamas() < taxe)
					{
						SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "176");
						return;
					}
					
					client.getPlayer().addKamas(taxe *-1);//Retire le montant de la taxe au personnage
					
					SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());//Met a jour les kamas du client
					
					Objet obj = World.data.getObjet(itmID);//Rï¿½cupï¿½re l'item
					if(amount > obj.getQuantity())//S'il veut mettre plus de cette objet en vente que ce qu'il possï¿½de
						return;
					
					int rAmount = (int)(Math.pow(10,amount)/10);
					int newQua = (obj.getQuantity()-rAmount);
					
					if(newQua <= 0)//Si c'est plusieurs objets ensemble enleve seulement la quantitï¿½ de mise en vente
					{
						client.getPlayer().removeItem(itmID);//Enlï¿½ve l'item de l'inventaire du personnage
						SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(client.getPlayer(),itmID);//Envoie un packet au client pour retirer l'item de son inventaire
					}
					else
					{
						obj.setQuantity(obj.getQuantity() - rAmount);
						SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(client.getPlayer(),obj);
						
						Objet newObj = Objet.getCloneObjet(obj, rAmount);
						World.data.addObjet(newObj, true);
						obj = newObj;
					}
					
					HdvEntry toAdd = new HdvEntry(price,amount,client.getPlayer().get_compte().get_GUID(),obj);
					curHdv.addEntry(toAdd);	//Ajoute l'entry dans l'HDV
					
					SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(client,'+',"",toAdd.parseToEmK());	//Envoie un packet pour ajouter l'item dans la fenetre de l'HDV du client
				break;
			}
			return;
		}
		//Job
		if(client.getPlayer().getCurJobAction() != null)
		{
			//Si pas action de craft, on s'arrete la
			if(!client.getPlayer().getCurJobAction().isCraft())return;
			if(packet.charAt(2) == 'O')//Ajout d'objet
			{
				if(packet.charAt(3) == '+')
				{
					//FIXME gerer les packets du genre  EMO+173|5+171|5+172|5 (split sur '+' ?:/)
					String[] infos = packet.substring(4).split("\\|");
					try
					{
						int guid = Integer.parseInt(infos[0]);
						int qua  = Integer.parseInt(infos[1]);
						if(qua <= 0)return;
						if(!client.getPlayer().hasItemGuid(guid))return;
						Objet obj = World.data.getObjet(guid);
						if(obj == null)return;
						if(obj.getQuantity()<qua)
							qua = obj.getQuantity();
							client.getPlayer().getCurJobAction().modifIngredient(client.getPlayer(),guid,qua);
					}catch(NumberFormatException e){};
				}else
				{
					String[] infos = packet.substring(4).split("\\|");
					try
					{
						int guid = Integer.parseInt(infos[0]);
						int qua  = Integer.parseInt(infos[1]);
						if(qua <= 0)return;
						Objet obj = World.data.getObjet(guid);
						if(obj == null)return;
                        else if(obj.getQuantity() < qua)
                            qua = obj.getQuantity();
						client.getPlayer().getCurJobAction().modifIngredient(client.getPlayer(),guid,-qua);
					}catch(NumberFormatException e){};
				}
				
			}else
			if(packet.charAt(2) == 'R')
			{
				try {
					int c = Integer.parseInt(packet.substring(3));
					client.getPlayer().getCurJobAction().getJobCraft().setAction(c, client.getPlayer());
				}catch(Exception e) {};
			}else
			if(packet.charAt(2) == 'r')
				if(client.getPlayer().getCurJobAction() != null)
					if(client.getPlayer().getCurJobAction().getJobCraft() != null)
						client.getPlayer().getCurJobAction().broken = true;
			return;
		}
		//Banque
		if(client.getPlayer().isInBank())
		{
			if(client.getPlayer().get_curExchange() != null)return;
			switch(packet.charAt(2))
			{
				case 'G'://Kamas
					long kamas = 0;
					try
					{
							kamas = Integer.parseInt(packet.substring(3));
					}catch(Exception e){};
					if(kamas == 0)return;
					
					if(kamas > 0)//Si On ajoute des kamas a la banque
					{
						if(client.getPlayer().get_kamas() < kamas)kamas = client.getPlayer().get_kamas();

						client.getPlayer().setBankKamas(client.getPlayer().getBankKamas()+kamas);//On ajoute les kamas a la banque
						client.getPlayer().set_kamas(client.getPlayer().get_kamas()-kamas);//On retire les kamas du personnage
						SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
						SocketManager.GAME_SEND_EsK_PACKET(client.getPlayer(),"G"+client.getPlayer().getBankKamas());
					}else
					{
						kamas = -kamas;//On repasse en positif
						if(client.getPlayer().getBankKamas() < kamas)kamas = client.getPlayer().getBankKamas();
                        else if(kamas <= 0) // si - -, Ã§a donne +
                            return;
						client.getPlayer().setBankKamas(client.getPlayer().getBankKamas()-kamas);//On retire les kamas de la banque
						client.getPlayer().set_kamas(client.getPlayer().get_kamas()+kamas);//On ajoute les kamas du personnage
						SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
						SocketManager.GAME_SEND_EsK_PACKET(client.getPlayer(),"G"+client.getPlayer().getBankKamas());
					}
				break;
				
				case 'O'://Objet
					int guid = 0;
					int qua = 0;
					try
					{
						guid = Integer.parseInt(packet.substring(4).split("\\|")[0]);
						qua = Integer.parseInt(packet.substring(4).split("\\|")[1]);
					}catch(Exception e){};
					if(guid == 0 || qua <= 0)return;
					if(World.data.getObjet(guid) == null
                            || World.data.getObjet(guid).getQuantity() < qua)
                        return;
					switch(packet.charAt(3))
					{
						case '+'://Ajouter a la banque
							client.getPlayer().addInBank(guid,qua);
						break;
						
						case '-'://Retirer de la banque
							client.getPlayer().removeFromBank(guid,qua);
						break;
					}
				break;
			}
			return;
		}
		//Coffre
	    if(client.getPlayer().getInTrunk() != null)
        {
                if(client.getPlayer().get_curExchange() != null)
                	return;
                
                Trunk t = client.getPlayer().getInTrunk();
                
                if(t == null) 
                	return;
              
                switch(packet.charAt(2))
                {
                	case 'G'://Kamas
                    	long kamas = 0;
                    	try {
                    		kamas = Integer.parseInt(packet.substring(3));
                        } catch(Exception e) {}
                    	
                        if(kamas == 0)
                        	return;
                               
                        if(kamas > 0)//Si On ajoute des kamas au coffre
                        {
                            if(client.getPlayer().get_kamas() < kamas)kamas = client.getPlayer().get_kamas();
                            t.set_kamas(t.get_kamas() + kamas);//On ajoute les kamas au coffre
                            client.getPlayer().set_kamas(client.getPlayer().get_kamas()-kamas);//On retire les kamas du personnage
                            SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
                        }else // On retire des kamas au coffre
                        {
                        	kamas = -kamas;//On repasse en positif
                            if(kamas <= 0) // - - = +
                                return;
                        	if(t.get_kamas() < kamas)kamas = t.get_kamas();
                        	t.set_kamas(t.get_kamas()-kamas);//On retire les kamas de la banque
                         	client.getPlayer().set_kamas(client.getPlayer().get_kamas()+kamas);//On ajoute les kamas du personnage
                         	SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
                        }
                        for(Player P : World.data.getOnlinePersos())
                        	if(P.getInTrunk() != null && client.getPlayer().getInTrunk().get_id() == P.getInTrunk().get_id())
                        		SocketManager.GAME_SEND_EsK_PACKET(P,"G"+t.get_kamas());
                        World.database.getTrunkData().update(t);
                    break;
              	
                	case 'O'://Objet
                		int guid = 0;
                		int qua = 0;
                		try {
                			guid = Integer.parseInt(packet.substring(4).split("\\|")[0]);
                			qua = Integer.parseInt(packet.substring(4).split("\\|")[1]);
                		} catch(Exception e) {}
                		
                		if(guid == 0 || qua <= 0)
                			return;
                        if(World.data.getObjet(guid) == null || World.data.getObjet(guid).getQuantity() < qua)
                            return;
                		switch(packet.charAt(3))
                		{
                			case '+'://Ajouter a la banque
                				t.addInTrunk(guid, qua, client.getPlayer());
                			break;        
                			case '-'://Retirer de la banque
                				t.removeFromTrunk(guid,qua, client.getPlayer());
                			break;
                		}
                	break;
                }
                return;
        }
		if(client.getPlayer().get_curExchange() == null)
			return;
		switch(packet.charAt(2))
		{
			case 'O'://Objet ?
				if(packet.charAt(3) == '+')	{
					String[] infos = packet.substring(4).split("\\|");
					try	{
						int guid = Integer.parseInt(infos[0]);
						int qua  = Integer.parseInt(infos[1]);
						int quaInExch = client.getPlayer().get_curExchange().getQuaItem(guid, client.getPlayer().get_GUID());
						
						if(!client.getPlayer().hasItemGuid(guid))
							return;
						
						Objet obj = World.data.getObjet(guid);
						
						if(obj == null)
							return;
						if(qua > obj.getQuantity() - quaInExch)
							qua = obj.getQuantity() - quaInExch;
						if(qua <= 0)
							return;
						
						client.getPlayer().get_curExchange().addItem(guid, qua, client.getPlayer().get_GUID());
					} catch(NumberFormatException e) {}
				}else {
					try	{
						String[] infos = packet.substring(4).split("\\|");
						int guid = Integer.parseInt(infos[0]);
						int qua  = Integer.parseInt(infos[1]);
						
						if(qua <= 0)
							return;
						if(!client.getPlayer().hasItemGuid(guid))
							return;
						
						Objet obj = World.data.getObjet(guid);
						
						if(obj == null)
							return;
						if(qua > client.getPlayer().get_curExchange().getQuaItem(guid, client.getPlayer().get_GUID()))
							return;
						
						client.getPlayer().get_curExchange().removeItem(guid,qua, client.getPlayer().get_GUID());
					} catch(NumberFormatException e) {}
				}
			break;
			case 'G'://Kamas
				try	{
					long numb = Integer.parseInt(packet.substring(3));
					if(client.getPlayer().get_kamas() < numb)
						numb = client.getPlayer().get_kamas();
                    else if(numb <= 0)
                        return;
					client.getPlayer().get_curExchange().setKamas(client.getPlayer().get_GUID(), numb);
				} catch(NumberFormatException e) {}
			break;
		}
	}
	
	@Packet("Eq")
	public static void askOfflineExchange(GameClient client, String packet) {
		if(client.getPlayer().get_isTradingWith() > 0 || client.getPlayer().get_fight() != null || client.getPlayer().is_away())
			return;
        if(client.getPlayer().parseStoreItemsList().isEmpty()) {
        	SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "123");
        	return;
        }
        if(World.data.isMarchandMap(client.getPlayer().get_curCarte().get_id())) {
        	SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "113");
        	return;
        }
        if (client.getPlayer().get_curCarte().get_id() == 33 || client.getPlayer().get_curCarte().get_id() == 38 || client.getPlayer().get_curCarte().get_id() == 4601 || client.getPlayer().get_curCarte().get_id() == 8036 || client.getPlayer().get_curCarte().get_id() == 10301) {
			if (client.getPlayer().get_curCarte().getStoreCount() >= 25) {
				SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "125;25");
				return;
			}
        }else if(client.getPlayer().get_curCarte().getStoreCount() >= 6) {
        	SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "125;6");
			return;
        }
        //Calcul et envoie du packet pour la taxe
        long Apayer = client.getPlayer().storeAllBuy() / 1000;
        if(Apayer < 0) {
	       	SocketManager.GAME_SEND_MESSAGE(client.getPlayer(), "Erreur de mode marchand, la somme est négatif.", Server.config.getMotdColor());
	       	return;
	    }
        SocketManager.GAME_SEND_Eq_PACKET(client.getPlayer(), Apayer);
	}
	
	@Packet("EQ")
	public static void offlineExchange(GameClient client, String packet) {
		if(World.data.isMarchandMap(client.getPlayer().get_curCarte().get_id())) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "113");
			return;
		}
		if (client.getPlayer().get_curCarte().get_id() == 33 || client.getPlayer().get_curCarte().get_id() == 38 || client.getPlayer().get_curCarte().get_id() == 4601 || client.getPlayer().get_curCarte().get_id() == 4259 || client.getPlayer().get_curCarte().get_id() == 8036 || client.getPlayer().get_curCarte().get_id() == 10301) {
			if (client.getPlayer().get_curCarte().getStoreCount() >= 25) {
				SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "125;25");
				return;
			}
		}else if(client.getPlayer().get_curCarte().getStoreCount() >= 6) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "125;6");
			return;
		}
		long Apayer2 = client.getPlayer().storeAllBuy() / 1000;
		if(client.getPlayer().get_kamas() < Apayer2) {
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "176");
			return;
		}
		if(Apayer2 < 0) {
		    SocketManager.GAME_SEND_MESSAGE(client.getPlayer(), "Erreur de mode marchand, la somme est négatif.", Server.config.getMotdColor());
		    return;
		}
		int orientation = Formulas.getRandomValue(1, 3);
		client.getPlayer().set_kamas(client.getPlayer().get_kamas() - Apayer2);
		client.getPlayer().set_orientation(orientation);
        Carte map = client.getPlayer().get_curCarte();
        client.getPlayer().set_showSeller(true);
        World.data.addSeller(client.getPlayer().get_GUID(), client.getPlayer().get_curCarte().get_id());
        client.kick();
        for(Player z : map.getPersos())
        	if(z != null && z.isOnline())
        		SocketManager.GAME_SEND_MERCHANT_LIST(z, z.get_curCarte().get_id());
	}
	
	@Packet("Er")
	public static void mountpark(GameClient client, String packet)
	{
		//Si dans un enclos
		if(client.getPlayer().getInMountPark() != null)
		{
			MountPark MP = client.getPlayer().getInMountPark();
			
			if(client.getPlayer().get_isTradingWith() > 0 || client.getPlayer().get_fight() != null)
				return;
			
			char c = packet.charAt(2);
			packet = packet.substring(3);
			int guid = -1;
			
			try {
				guid = Integer.parseInt(packet);
			} catch(Exception e) {}
			
			switch(c)
			{
				case 'C'://Parcho => Etable (Stocker)
					if(guid == -1 || !client.getPlayer().hasItemGuid(guid))
						return;
					if(MP.get_size() <= MP.MountParkDATASize()) {
						SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1145");
						return;
					}
					
					Objet obj = World.data.getObjet(guid);
					int DDid = obj.getStats().getEffect(995);
					Dragodinde DD = World.data.getDragoByID(DDid);
					//FIXME mettre return au if pour ne pas crï¿½er des nouvelles dindes
					if(DD == null) {
						int color = Constants.getMountColorByParchoTemplate(obj.getTemplate().getID());
						if(color <1)
							return;
						DD = new Dragodinde(color);
					}
					//On enleve l'objet du Monde et du Perso
					client.getPlayer().removeItem(guid);
					World.data.removeItem(guid);
					//on ajoute la dinde a l'ï¿½table
					MP.addData(DD.get_id(), client.getPlayer().get_GUID());
					World.database.getMountparkData().update(MP);
					//On envoie les packet
					SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(client.getPlayer(),obj.getGuid());
					SocketManager.GAME_SEND_Ee_PACKET(client.getPlayer(), '+', DD.parse());
				break;
				case 'c'://Etable => Parcho(Echanger)
					Dragodinde DD1 = World.data.getDragoByID(guid);
					//S'il n'a pas la dinde
					if(DD1 == null || !MP.getData().containsKey(DD1.get_id()))return;
					if(MP.getData().get(DD1.get_id()) != client.getPlayer().get_GUID() && 
						World.data.getPersonnage(MP.getData().get(DD1.get_id())).get_guild() != client.getPlayer().get_guild())
						return;
					if(MP.getData().get(DD1.get_id()) != client.getPlayer().get_GUID() && 
							World.data.getPersonnage(MP.getData().get(DD1.get_id())).get_guild() == client.getPlayer().get_guild() &&
							!client.getPlayer().getGuildMember().canDo(Constants.G_OTHDINDE)) {
						//Mï¿½me guilde, pas le droit
						SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1101");
						return;
					}
					//on retire la dinde de l'ï¿½table
					MP.removeData(DD1.get_id());
					World.database.getMountparkData().update(MP);
					//On crï¿½er le parcho
					ObjTemplate T = Constants.getParchoTemplateByMountColor(DD1.get_color());
					Objet obj1 = T.createNewItem(1, false);
					//On efface les stats
					obj1.clearStats();
					//on ajoute la possibilitï¿½ de voir la dinde
					obj1.getStats().addOneStat(995, DD1.get_id());
					obj1.addTxtStat(996, client.getPlayer().get_name());
					obj1.addTxtStat(997, DD1.get_nom());
					
					//On ajoute l'objet au joueur
					World.data.addObjet(obj1, true);
					client.getPlayer().addObjet(obj1, false);//Ne seras jamais identique de toute
					
					//Packets
					SocketManager.GAME_SEND_Ow_PACKET(client.getPlayer());
					SocketManager.GAME_SEND_Ee_PACKET(client.getPlayer(),'-',DD1.get_id()+"");
				break;
				case 'g'://Equiper
					Dragodinde DD3 = World.data.getDragoByID(guid);
					//S'il n'a pas la dinde
					if(DD3 == null || !MP.getData().containsKey(DD3.get_id()) || client.getPlayer().getMount() != null)return;
					
					if(MP.getData().get(DD3.get_id()) != client.getPlayer().get_GUID() && 
							World.data.getPersonnage(MP.getData().get(DD3.get_id())).get_guild() != client.getPlayer().get_guild())
						return;
					if(MP.getData().get(DD3.get_id()) != client.getPlayer().get_GUID() && 
							World.data.getPersonnage(MP.getData().get(DD3.get_id())).get_guild() == client.getPlayer().get_guild() &&
							!client.getPlayer().getGuildMember().canDo(Constants.G_OTHDINDE)) {
						//Mï¿½me guilde, pas le droit
						SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1101");
						return;
					}
					
					MP.removeData(DD3.get_id());
					World.database.getMountparkData().update(MP);
					client.getPlayer().setMount(DD3);
					
					//Packets
					SocketManager.GAME_SEND_Re_PACKET(client.getPlayer(), "+", DD3);
					SocketManager.GAME_SEND_Ee_PACKET(client.getPlayer(),'-',DD3.get_id()+"");
					SocketManager.GAME_SEND_Rx_PACKET(client.getPlayer());
				break;
				case 'p'://Equipï¿½ => Stocker
					//Si c'est la dinde ï¿½quipï¿½
					if(client.getPlayer().getMount()!=null?client.getPlayer().getMount().get_id() == guid:false)
					{
						//Si le perso est sur la monture on le fait descendre
						if(client.getPlayer().isOnMount())client.getPlayer().toogleOnMount();
						//Si ca n'a pas rï¿½ussie, on s'arrete lï¿½ (Items dans le sac ?)
						if(client.getPlayer().isOnMount())return;
						
						Dragodinde DD2 = client.getPlayer().getMount();
						MP.addData(DD2.get_id(), client.getPlayer().get_GUID());
						World.database.getMountparkData().update(MP);
						client.getPlayer().setMount(null);
						
						//Packets
						SocketManager.GAME_SEND_Ee_PACKET(client.getPlayer(),'+',DD2.parse());
						SocketManager.GAME_SEND_Re_PACKET(client.getPlayer(), "-", null);
						SocketManager.GAME_SEND_Rx_PACKET(client.getPlayer());
					}else//Sinon...
					{	
					}
				break;
			}
		}
	}

	@Packet("ER")
	public static void request(GameClient client, String packet) {
		if(packet.substring(2,4).equals("11"))//Ouverture HDV achat
		{
			if(client.getPlayer().get_isTradingWith() < 0)//Si dï¿½jï¿½ ouvert
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
			client.getPlayer().set_isTradingWith(0 - client.getPlayer().get_curCarte().get_id());	//Rï¿½cupï¿½re l'ID de la map et rend cette valeur nï¿½gative
			return;
		}else 
		if(packet.substring(2,4).equals("10"))//Ouverture HDV vente
		{
			if(client.getPlayer().get_isTradingWith() < 0)//Si dï¿½jï¿½ ouvert
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
			client.getPlayer().set_isTradingWith(0 - client.getPlayer().get_curCarte().get_id());	//Rï¿½cupï¿½re l'ID de la map et rend cette valeur nï¿½gative
			
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

	@Packet("ES")
	public static void sell(GameClient client, String packet) {
		try {
			String[] infos = packet.substring(2).split("\\|");
			int guid = Integer.parseInt(infos[0]);
			int qua = Integer.parseInt(infos[1]);
			
			if(!client.getPlayer().hasItemGuid(guid)) {
				SocketManager.GAME_SEND_SELL_ERROR_PACKET(client);
				return;
			} else 
			if(World.data.getObjet(guid) == null || World.data.getObjet(guid).getQuantity() < qua) {
				return;
			}
			
			client.getPlayer().sellItem(guid, qua);
		} catch(Exception e) {
			SocketManager.GAME_SEND_SELL_ERROR_PACKET(client);
		}
	}

	@Packet("EV")
	public static void finish(GameClient client, String packet)
	{
		if(client.getPlayer().get_isTradingWith() == 0 &&
		   client.getPlayer().get_curExchange() == null &&
		   client.getPlayer().getCurJobAction() == null &&
		   client.getPlayer().getInMountPark() == null &&
		   !client.getPlayer().isInBank() &&
		   client.getPlayer().get_isOnPercepteurID() == 0 &&
		   client.getPlayer().getInTrunk() == null)
			return;
		
		//Si ï¿½change avec un personnage
		if(client.getPlayer().get_curExchange() != null)
		{
			client.getPlayer().get_curExchange().cancel();
			client.getPlayer().set_isTradingWith(0);
			client.getPlayer().set_away(false);
			return;
		}
		//Si mï¿½tier
		if(client.getPlayer().getCurJobAction() != null)
		{
			client.getPlayer().getCurJobAction().resetCraft();
		}
		//Si dans un enclos
		if(client.getPlayer().getInMountPark() != null)client.getPlayer().leftMountPark();
		//prop d'echange avec un joueur
		if(client.getPlayer().get_isTradingWith() > 0)
		{
			Player p = World.data.getPersonnage(client.getPlayer().get_isTradingWith());
			if(p != null)
			{
				if(p.isOnline())
				{
					SocketManager.GAME_SEND_EV_PACKET(client);
					p.set_isTradingWith(0);
				}
			}
		}
		//Si perco
		if(client.getPlayer().get_isOnPercepteurID() != 0)
		{
			Percepteur perco = World.data.getPerco(client.getPlayer().get_isOnPercepteurID());
			if(perco == null) return;
			for(Player z : World.data.getGuild(perco.get_guildID()).getMembers())
			{
				if(z.isOnline())
				{
					SocketManager.GAME_SEND_gITM_PACKET(z, Percepteur.parsetoGuild(z.get_guild().get_id()));
					String str = "";
					str += "G"+perco.get_N1()+","+perco.get_N2();
					str += "|.|"+World.data.getCarte((short)perco.get_mapID()).getX()+"|"+World.data.getCarte((short)perco.get_mapID()).getY()+"|";
					str += client.getPlayer().get_name()+"|";
					str += perco.get_LogXp()+";";
					str += perco.get_LogItems();
					SocketManager.GAME_SEND_gT_PACKET(z, str);
				}
			}
			client.getPlayer().get_curCarte().RemoveNPC(perco.getGuid());
			SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(client.getPlayer().get_curCarte(), perco.getGuid());
			perco.DelPerco(perco.getGuid());
			World.database.getCollectorData().delete(perco);
			client.getPlayer().set_isOnPercepteurID(0);
		}
		
		client.getPlayer().save();
		SocketManager.GAME_SEND_EV_PACKET(client);
		client.getPlayer().set_isTradingWith(0);
		client.getPlayer().set_away(false);
		client.getPlayer().setInBank(false);
		client.getPlayer().setInTrunk(null);
	}	
}