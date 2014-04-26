package fr.edofus.ancestra.evolutive.game.packet.exchange;



import fr.edofus.ancestra.evolutive.common.Constants;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.Dragodinde;
import fr.edofus.ancestra.evolutive.objects.Objet;
import fr.edofus.ancestra.evolutive.objects.Carte.MountPark;
import fr.edofus.ancestra.evolutive.objects.Objet.ObjTemplate;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Er")
public class MountparkExchange implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
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
					//FIXME mettre return au if pour ne pas cr�er des nouvelles dindes
					if(DD == null) {
						int color = Constants.getMountColorByParchoTemplate(obj.getTemplate().getID());
						if(color <1)
							return;
						DD = new Dragodinde(color);
					}
					//On enleve l'objet du Monde et du Perso
					client.getPlayer().removeItem(guid);
					World.data.removeItem(guid);
					//on ajoute la dinde a l'�table
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
						//M�me guilde, pas le droit
						SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1101");
						return;
					}
					//on retire la dinde de l'�table
					MP.removeData(DD1.get_id());
					World.database.getMountparkData().update(MP);
					//On cr�er le parcho
					ObjTemplate T = Constants.getParchoTemplateByMountColor(DD1.get_color());
					Objet obj1 = T.createNewItem(1, false);
					//On efface les stats
					obj1.clearStats();
					//on ajoute la possibilit� de voir la dinde
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
						//M�me guilde, pas le droit
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
				case 'p'://Equip� => Stocker
					//Si c'est la dinde �quip�
					if(client.getPlayer().getMount()!=null?client.getPlayer().getMount().get_id() == guid:false)
					{
						//Si le perso est sur la monture on le fait descendre
						if(client.getPlayer().isOnMount())client.getPlayer().toogleOnMount();
						//Si ca n'a pas r�ussie, on s'arrete l� (Items dans le sac ?)
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
}