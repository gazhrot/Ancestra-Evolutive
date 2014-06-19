package org.ancestra.evolutive.game.packet.exchange;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.Mount;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.map.MountPark;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.object.ObjectTemplate;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("Er")
public class MountparkExchange implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		//Si dans un enclos
		if(client.getPlayer().getCurMountPark() != null)
		{
			MountPark MP = client.getPlayer().getCurMountPark();
			
			if(client.getPlayer().getIsTradingWith() > 0 || client.getPlayer().getFight() != null)
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
					if(MP.getSize() <= MP.getDatas().size()) {
						SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1145");
						return;
					}
					
					Object obj = World.data.getObject(guid);
					int DDid = obj.getStats().getEffect(995);
					Mount DD = World.data.getDragoByID(DDid);
					//FIXME mettre return au if pour ne pas cr�er des nouvelles dindes
					if(DD == null) {
						int color = Constants.getMountColorByParchoTemplate(obj.getTemplate().getId());
						if(color <1)
							return;
						DD = new Mount(color);
					}
					//On enleve l'objet du Monde et du Perso
					client.getPlayer().removeItem(guid);
					World.data.removeObject(guid);
					//on ajoute la dinde a l'�table
					MP.getDatas().put(DD.getId(), client.getPlayer().getId());
					World.database.getMountparkData().update(MP);
					//On envoie les packet
					SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(client.getPlayer(),obj.getId());
					SocketManager.GAME_SEND_Ee_PACKET(client.getPlayer(), '+', DD.parse());
				break;
				case 'c'://Etable => Parcho(Echanger)
					Mount DD1 = World.data.getDragoByID(guid);
					//S'il n'a pas la dinde
					if(DD1 == null || !MP.getDatas().containsKey(DD1.getId()))return;
					if(MP.getDatas().get(DD1.getId()) != client.getPlayer().getId() &&
						World.data.getPersonnage(MP.getDatas().get(DD1.getId())).getGuild() != client.getPlayer().getGuild())
						return;
					if(MP.getDatas().get(DD1.getId()) != client.getPlayer().getId() &&
							World.data.getPersonnage(MP.getDatas().get(DD1.getId())).getGuild() == client.getPlayer().getGuild() &&
							!client.getPlayer().getGuildMember().canDo(Constants.G_OTHDINDE)) {
						//M�me guilde, pas le droit
						SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1101");
						return;
					}
					//on retire la dinde de l'�table
					MP.getDatas().remove(DD1.getId());
					World.database.getMountparkData().update(MP);
					//On cr�er le parcho
					ObjectTemplate T = Constants.getParchoTemplateByMountColor(DD1.getColor());
					Object obj1 = T.createNewItem(1, false);
					//On efface les stats
					obj1.clearStats();
					//on ajoute la possibilit� de voir la dinde
					obj1.getStats().addOneStat(995, DD1.getId());
					obj1.getTxtStats().put(996, client.getPlayer().getName());
					obj1.getTxtStats().put(997, DD1.getName());
					
					//On ajoute l'objet au joueur
					World.data.addObject(obj1, true);
					client.getPlayer().addObject(obj1, false);//Ne seras jamais identique de toute
					
					//Packets
					SocketManager.GAME_SEND_Ow_PACKET(client.getPlayer());
					SocketManager.GAME_SEND_Ee_PACKET(client.getPlayer(),'-',DD1.getId()+"");
				break;
				case 'g'://Equiper
					Mount DD3 = World.data.getDragoByID(guid);
					//S'il n'a pas la dinde
					if(DD3 == null || !MP.getDatas().containsKey(DD3.getId()) || client.getPlayer().getMount() != null)return;
					
					if(MP.getDatas().get(DD3.getId()) != client.getPlayer().getId() &&
							World.data.getPersonnage(MP.getDatas().get(DD3.getId())).getGuild() != client.getPlayer().getGuild())
						return;
					if(MP.getDatas().get(DD3.getId()) != client.getPlayer().getId() &&
							World.data.getPersonnage(MP.getDatas().get(DD3.getId())).getGuild() == client.getPlayer().getGuild() &&
							!client.getPlayer().getGuildMember().canDo(Constants.G_OTHDINDE)) {
						//M�me guilde, pas le droit
						SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1101");
						return;
					}
					
					MP.getDatas().remove(DD3.getId());
					World.database.getMountparkData().update(MP);
					client.getPlayer().setMount(DD3);
					
					//Packets
					SocketManager.GAME_SEND_Re_PACKET(client.getPlayer(), "+", DD3);
					SocketManager.GAME_SEND_Ee_PACKET(client.getPlayer(),'-',DD3.getId()+"");
					SocketManager.GAME_SEND_Rx_PACKET(client.getPlayer());
				break;
				case 'p'://Equip� => Stocker
					//Si c'est la dinde �quip�
					if(client.getPlayer().getMount()!=null?client.getPlayer().getMount().getId() == guid:false)
					{
						//Si le perso est sur la monture on le fait descendre
						if(client.getPlayer().isOnMount())client.getPlayer().toogleOnMount();
						//Si ca n'a pas r�ussie, on s'arrete l� (Items dans le sac ?)
						if(client.getPlayer().isOnMount())return;
						
						Mount DD2 = client.getPlayer().getMount();
						MP.getDatas().put(DD2.getId(), client.getPlayer().getId());
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