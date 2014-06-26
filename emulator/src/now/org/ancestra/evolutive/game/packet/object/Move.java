package org.ancestra.evolutive.game.packet.object;

import org.ancestra.evolutive.common.ConditionParser;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.job.JobStat;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.object.ObjectPosition;
import org.ancestra.evolutive.object.ObjectType;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

import java.util.Map.Entry;

@Packet("OM")
public class Move implements PacketParser {

	@Override
	public synchronized void parse(GameClient client, String packet) {
		String[] infos = packet.substring(2).split(""+(char)0x0A)[0].split("\\|");
		try	{
			int qua;
			int guid = Integer.parseInt(infos[0]);
			ObjectPosition pos = ObjectPosition.getPositionById(Integer.parseInt(infos[1]));
			try {
				qua = Integer.parseInt(infos[2]);
			} catch(Exception e)	{
				qua = 1;
			}
			Object obj = World.data.getObject(guid);
			
			if(!client.getPlayer().hasItemGuid(guid) || obj == null)
				return;
			
			if(client.getPlayer().getFight() != null)
			{
				if(client.getPlayer().getFight().get_state() > 2)
				{
					return;
				}
			}
			if(!Constants.isValidPlaceForItem(obj.getTemplate(),pos) && pos != ObjectPosition.NO_EQUIPED)
			{
				return;
			}
			if(!obj.getTemplate().getConditions().equalsIgnoreCase("") && !ConditionParser.validConditions(client.getPlayer(),obj.getTemplate().getConditions()))
			{
				SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "119|43");
				return;
			}
			if(obj.getTemplate().getLevel() > client.getPlayer().getLevel())
			{
				SocketManager.GAME_SEND_OAEL_PACKET(client);
				return;
			}
			//On ne peut �quiper 2 items de panoplies identiques, ou 2 Dofus identiques
			if(pos != ObjectPosition.NO_EQUIPED && (obj.getTemplate().getSet() != -1 || obj.getTemplate().getType() == ObjectType.DOFUS) && client.getPlayer().hasEquiped(obj.getTemplate().getId()))
				return;
			
			Object exObj = client.getPlayer().getObjectByPos(pos);//Object a l'ancienne position
			if(exObj != null)//S'il y avait d�ja un objet sur cette place on d�s�quipe
			{
				Object obj2;
				if((obj2 = client.getPlayer().getSimilarItem(exObj)) != null) {//On le poss�de deja
					obj2.setQuantity(obj2.getQuantity()+exObj.getQuantity());
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(client.getPlayer(), obj2);
					World.data.removeObject(exObj.getId());
					client.getPlayer().removeItem(exObj.getId());
					SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(client.getPlayer(), exObj.getId());
				}else {//On ne le poss�de pas
					exObj.setPosition(ObjectPosition.NO_EQUIPED);
					SocketManager.GAME_SEND_OBJET_MOVE_PACKET(client.getPlayer(),exObj);
				}
				if(client.getPlayer().getObjectByPos(ObjectPosition.ARME) == null)
					SocketManager.GAME_SEND_OT_PACKET(client, -1);
				
				//Si objet de panoplie
				if(exObj.getTemplate().getSet() > 0)SocketManager.GAME_SEND_OS_PACKET(client.getPlayer(),exObj.getTemplate().getSet());
			}else//getNumbEquipedItemOfPanoplie(exObj.getTemplate().getPanopID()
			{
				Object obj2;
				//On a un objet similaire
				if((obj2 = client.getPlayer().getSimilarItem(obj)) != null)
				{
					if(qua > obj.getQuantity()) qua = obj.getQuantity();
					
					obj2.setQuantity(obj2.getQuantity()+qua);
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(client.getPlayer(), obj2);
					
					if(obj.getQuantity() - qua > 0)//Si il en reste
					{
						obj.setQuantity(obj.getQuantity()-qua);
						SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(client.getPlayer(), obj);
					}else//Sinon on supprime
					{
						World.data.removeObject(obj.getId());
						client.getPlayer().removeItem(obj.getId());
						SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(client.getPlayer(), obj.getId());
					}
				}else {//Pas d'objets similaires
					obj.setPosition(pos);
					SocketManager.GAME_SEND_OBJET_MOVE_PACKET(client.getPlayer(),obj);
					if(obj.getQuantity() > 1)
					{
						if(qua > obj.getQuantity()) qua = obj.getQuantity();
						
						if(obj.getQuantity() - qua > 0)//Si il en reste
						{
							int newItemQua = obj.getQuantity()-qua;
							Object newItem = Object.getClone(obj,newItemQua);
							client.getPlayer().addObject(newItem,false);
							World.data.addObject(newItem,true);
							obj.setQuantity(qua);
							SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(client.getPlayer(), obj);
						}
					}
				}
			}
			SocketManager.GAME_SEND_Ow_PACKET(client.getPlayer());
			client.getPlayer().refreshStats();
			if(client.getPlayer().getGroup() != null)
				SocketManager.GAME_SEND_PM_MOD_PACKET_TO_GROUP(client.getPlayer().getGroup(),client.getPlayer());
			SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
			if( pos == ObjectPosition.ARME 		||
				pos == ObjectPosition.COIFFE 	||
				pos == ObjectPosition.FAMILIER 	||
				pos == ObjectPosition.CAPE		||
				pos == ObjectPosition.BOUCLIER	||
				pos == ObjectPosition.NO_EQUIPED)
				SocketManager.GAME_SEND_ON_EQUIP_ITEM(client.getPlayer().getMap(), client.getPlayer());
		
			//Si familier
			if(pos == ObjectPosition.FAMILIER && client.getPlayer().isOnMount())
				client.getPlayer().toogleOnMount();
			//Verif pour les outils de m�tier
			if(pos == ObjectPosition.NO_EQUIPED && client.getPlayer().getObjectByPos(ObjectPosition.ARME) == null)
				SocketManager.GAME_SEND_OT_PACKET(client, -1);
			
			if(pos == ObjectPosition.ARME && client.getPlayer().getObjectByPos(ObjectPosition.ARME) != null)
				for(Entry<Integer, JobStat> e : client.getPlayer().getMetiers().entrySet())
					if(e.getValue().getTemplate().isValidTool(client.getPlayer().getObjectByPos(ObjectPosition.ARME).getTemplate().getId()))
						SocketManager.GAME_SEND_OT_PACKET(client,e.getValue().getTemplate().getId());

			//Si objet de panoplie
			if(obj.getTemplate().getSet() > 0)
				SocketManager.GAME_SEND_OS_PACKET(client.getPlayer(),obj.getTemplate().getSet());
			if(client.getPlayer().getFight() != null)
				SocketManager.GAME_SEND_ON_EQUIP_ITEM_FIGHT(client.getPlayer(), client.getPlayer().getFight().getFighterByPerso(client.getPlayer()), client.getPlayer().getFight());
		}catch(Exception e)	{
			e.printStackTrace();
			SocketManager.GAME_SEND_DELETE_OBJECT_FAILED_PACKET(client);
		}
	}
}