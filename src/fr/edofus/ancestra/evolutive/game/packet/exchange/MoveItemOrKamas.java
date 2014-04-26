package fr.edofus.ancestra.evolutive.game.packet.exchange;



import fr.edofus.ancestra.evolutive.client.Player;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.HDV;
import fr.edofus.ancestra.evolutive.objects.Objet;
import fr.edofus.ancestra.evolutive.objects.Percepteur;
import fr.edofus.ancestra.evolutive.objects.Trunk;
import fr.edofus.ancestra.evolutive.objects.HDV.HdvEntry;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("EM")
public class MoveItemOrKamas implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
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
					
					
					if(!client.getPlayer().hasItemGuid(itmID))//V�rifie si le personnage a bien l'item sp�cifi� et l'argent pour payer la taxe
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
					
					Objet obj = World.data.getObjet(itmID);//R�cup�re l'item
					if(amount > obj.getQuantity())//S'il veut mettre plus de cette objet en vente que ce qu'il poss�de
						return;
					
					int rAmount = (int)(Math.pow(10,amount)/10);
					int newQua = (obj.getQuantity()-rAmount);
					
					if(newQua <= 0)//Si c'est plusieurs objets ensemble enleve seulement la quantit� de mise en vente
					{
						client.getPlayer().removeItem(itmID);//Enl�ve l'item de l'inventaire du personnage
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
                        else if(kamas <= 0) // si - -, ça donne +
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
}