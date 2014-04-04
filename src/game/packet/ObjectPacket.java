package game.packet;

import java.util.Map.Entry;

import objects.Objet;
import objects.Objet.ObjTemplate;
import objects.job.JobStat;
import client.Player;

import common.ConditionParser;
import common.Constants;
import common.SocketManager;
import common.World;

import game.GameClient;
import game.packet.handler.Packet;

public class ObjectPacket {
	
	@Packet("Od")
	public static void delete(GameClient client, String packet)
	{
		String[] infos = packet.substring(2).split("\\|");
		try
		{
			int guid = Integer.parseInt(infos[0]);
			int qua = 1;
			try
			{
				qua = Integer.parseInt(infos[1]);
			}catch(Exception e){};
			Objet obj = World.data.getObjet(guid);
			if(obj == null || !client.getPlayer().hasItemGuid(guid) || qua <= 0 || client.getPlayer().get_fight() != null || client.getPlayer().is_away())
			{
				SocketManager.GAME_SEND_DELETE_OBJECT_FAILED_PACKET(client);
				return;
			}
			int newQua = obj.getQuantity()-qua;
			if(newQua <=0)
			{
				client.getPlayer().removeItem(guid);
				World.data.removeItem(guid);
				World.database.getItemData().delete(obj);
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(client.getPlayer(), guid);
			}else
			{
				obj.setQuantity(newQua);
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(client.getPlayer(), obj);
			}
			SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
			SocketManager.GAME_SEND_Ow_PACKET(client.getPlayer());
		}catch(Exception e)
		{
			SocketManager.GAME_SEND_DELETE_OBJECT_FAILED_PACKET(client);
		}
	}
	
	@Packet("OD")
	public static void drop(GameClient client, String packet)
	{
		int guid = -1;
		int qua = -1;
		try
		{
			guid = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			qua = Integer.parseInt(packet.split("\\|")[1]);
		}catch(Exception e){};
		if(guid == -1 || qua <= 0 || !client.getPlayer().hasItemGuid(guid) || client.getPlayer().get_fight() != null || client.getPlayer().is_away())return;
		Objet obj = World.data.getObjet(guid);
		
		client.getPlayer().set_curCell(client.getPlayer().get_curCell());
		int cellPosition = Constants.getNearCellidUnused(client.getPlayer());
		if(cellPosition < 0)
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1145");
			return;
		}
		if(obj.getPosition() != Constants.ITEM_POS_NO_EQUIPED)
		{
			obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
			SocketManager.GAME_SEND_OBJET_MOVE_PACKET(client.getPlayer(),obj);
			if(obj.getPosition() == Constants.ITEM_POS_ARME 		||
				obj.getPosition() == Constants.ITEM_POS_COIFFE 		||
				obj.getPosition() == Constants.ITEM_POS_FAMILIER 	||
				obj.getPosition() == Constants.ITEM_POS_CAPE		||
				obj.getPosition() == Constants.ITEM_POS_BOUCLIER	||
				obj.getPosition() == Constants.ITEM_POS_NO_EQUIPED)
					SocketManager.GAME_SEND_ON_EQUIP_ITEM(client.getPlayer().get_curCarte(), client.getPlayer());
		}
		if(qua >= obj.getQuantity())
		{
			client.getPlayer().removeItem(guid);
			client.getPlayer().get_curCarte().getCase(client.getPlayer().get_curCell().getID()+cellPosition).addDroppedItem(obj);
			obj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
			SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(client.getPlayer(), guid);
		}else
		{
			obj.setQuantity(obj.getQuantity() - qua);
			Objet obj2 = Objet.getCloneObjet(obj, qua);
			obj2.setPosition(Constants.ITEM_POS_NO_EQUIPED);
			client.getPlayer().get_curCarte().getCase(client.getPlayer().get_curCell().getID()+cellPosition).addDroppedItem(obj2);
			SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(client.getPlayer(), obj);
		}
		SocketManager.GAME_SEND_Ow_PACKET(client.getPlayer());
		SocketManager.GAME_SEND_GDO_PACKET_TO_MAP(client.getPlayer().get_curCarte(),'+',client.getPlayer().get_curCarte().getCase(client.getPlayer().get_curCell().getID()+cellPosition).getID(),obj.getTemplate().getID(),0);
		SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
	}

	@Packet("OM")
	public static synchronized void move(GameClient client, String packet)
	{
		String[] infos = packet.substring(2).split(""+(char)0x0A)[0].split("\\|");
		try
		{
			int qua;
			int guid = Integer.parseInt(infos[0]);
			int pos = Integer.parseInt(infos[1]);
			try
			{
				qua = Integer.parseInt(infos[2]);
			}catch(Exception e)
			{
				qua = 1;
			}
			Objet obj = World.data.getObjet(guid);
			
			if(!client.getPlayer().hasItemGuid(guid) || obj == null)
				return;
			
			if(client.getPlayer().get_fight() != null)
			{
				if(client.getPlayer().get_fight().get_state() > 2)
				{
					return;
				}
			}
			if(!Constants.isValidPlaceForItem(obj.getTemplate(),pos) && pos != Constants.ITEM_POS_NO_EQUIPED)
			{
				return;
			}
			if(!obj.getTemplate().getConditions().equalsIgnoreCase("") && !ConditionParser.validConditions(client.getPlayer(),obj.getTemplate().getConditions()))
			{
				SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "119|43");
				return;
			}
			if(obj.getTemplate().getLevel() > client.getPlayer().get_lvl())
			{
				SocketManager.GAME_SEND_OAEL_PACKET(client);
				return;
			}
			//On ne peut �quiper 2 items de panoplies identiques, ou 2 Dofus identiques
			if(pos != Constants.ITEM_POS_NO_EQUIPED && (obj.getTemplate().getPanopID() != -1 || obj.getTemplate().getType() == Constants.ITEM_TYPE_DOFUS )&& client.getPlayer().hasEquiped(obj.getTemplate().getID()))
				return;
			
			Objet exObj = client.getPlayer().getObjetByPos(pos);//Objet a l'ancienne position
			if(exObj != null)//S'il y avait d�ja un objet sur cette place on d�s�quipe
			{
				Objet obj2;
				if((obj2 = client.getPlayer().getSimilarItem(exObj)) != null)//On le poss�de deja
				{
					obj2.setQuantity(obj2.getQuantity()+exObj.getQuantity());
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(client.getPlayer(), obj2);
					World.data.removeItem(exObj.getGuid());
					client.getPlayer().removeItem(exObj.getGuid());
					SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(client.getPlayer(), exObj.getGuid());
				}
				else//On ne le poss�de pas
				{
					exObj.setPosition(Constants.ITEM_POS_NO_EQUIPED);
					SocketManager.GAME_SEND_OBJET_MOVE_PACKET(client.getPlayer(),exObj);
				}
				if(client.getPlayer().getObjetByPos(Constants.ITEM_POS_ARME) == null)
					SocketManager.GAME_SEND_OT_PACKET(client, -1);
				
				//Si objet de panoplie
				if(exObj.getTemplate().getPanopID() > 0)SocketManager.GAME_SEND_OS_PACKET(client.getPlayer(),exObj.getTemplate().getPanopID());
			}else//getNumbEquipedItemOfPanoplie(exObj.getTemplate().getPanopID()
			{
				Objet obj2;
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
						World.data.removeItem(obj.getGuid());
						client.getPlayer().removeItem(obj.getGuid());
						SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(client.getPlayer(), obj.getGuid());
					}
				}
				else//Pas d'objets similaires
				{
					obj.setPosition(pos);
					SocketManager.GAME_SEND_OBJET_MOVE_PACKET(client.getPlayer(),obj);
					if(obj.getQuantity() > 1)
					{
						if(qua > obj.getQuantity()) qua = obj.getQuantity();
						
						if(obj.getQuantity() - qua > 0)//Si il en reste
						{
							int newItemQua = obj.getQuantity()-qua;
							Objet newItem = Objet.getCloneObjet(obj,newItemQua);
							client.getPlayer().addObjet(newItem,false);
							World.data.addObjet(newItem,true);
							obj.setQuantity(qua);
							SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(client.getPlayer(), obj);
						}
					}
				}
			}
			SocketManager.GAME_SEND_Ow_PACKET(client.getPlayer());
			client.getPlayer().refreshStats();
			if(client.getPlayer().getGroup() != null)
			{
				SocketManager.GAME_SEND_PM_MOD_PACKET_TO_GROUP(client.getPlayer().getGroup(),client.getPlayer());
			}
			SocketManager.GAME_SEND_STATS_PACKET(client.getPlayer());
			if( pos == Constants.ITEM_POS_ARME 		||
				pos == Constants.ITEM_POS_COIFFE 	||
				pos == Constants.ITEM_POS_FAMILIER 	||
				pos == Constants.ITEM_POS_CAPE		||
				pos == Constants.ITEM_POS_BOUCLIER	||
				pos == Constants.ITEM_POS_NO_EQUIPED)
				SocketManager.GAME_SEND_ON_EQUIP_ITEM(client.getPlayer().get_curCarte(), client.getPlayer());
		
			//Si familier
			if(pos == Constants.ITEM_POS_FAMILIER && client.getPlayer().isOnMount())client.getPlayer().toogleOnMount();
			//Verif pour les outils de m�tier
			if(pos == Constants.ITEM_POS_NO_EQUIPED && client.getPlayer().getObjetByPos(Constants.ITEM_POS_ARME) == null)
				SocketManager.GAME_SEND_OT_PACKET(client, -1);
			
			if(pos == Constants.ITEM_POS_ARME && client.getPlayer().getObjetByPos(Constants.ITEM_POS_ARME) != null)
			{
				int ID = client.getPlayer().getObjetByPos(Constants.ITEM_POS_ARME).getTemplate().getID();
				for(Entry<Integer, JobStat> e : client.getPlayer().getMetiers().entrySet())
				{
					if(e.getValue().getTemplate().isValidTool(ID))
						SocketManager.GAME_SEND_OT_PACKET(client,e.getValue().getTemplate().getId());
				}
			}
			//Si objet de panoplie
			if(obj.getTemplate().getPanopID() > 0)SocketManager.GAME_SEND_OS_PACKET(client.getPlayer(),obj.getTemplate().getPanopID());
			//Si en combat
			if(client.getPlayer().get_fight() != null)
			{
				SocketManager.GAME_SEND_ON_EQUIP_ITEM_FIGHT(client.getPlayer(), client.getPlayer().get_fight().getFighterByPerso(client.getPlayer()), client.getPlayer().get_fight());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			SocketManager.GAME_SEND_DELETE_OBJECT_FAILED_PACKET(client);
		}
	}
	
	@Packet("OU")
	public static void use(GameClient client, String packet)
	{
		int guid = -1;
		int targetGuid = -1;
		short cellID = -1;
		Player Target = null;
		try
		{
			String[] infos = packet.substring(2).split("\\|");
			guid = Integer.parseInt(infos[0]);
			try
			{
				targetGuid = Integer.parseInt(infos[1]);
			}catch(Exception e){targetGuid = -1;};
			try
			{
				cellID = Short.parseShort(infos[2]);
			}catch(Exception e){cellID = -1;};
		}catch(Exception e){return;};
		//Si le joueur n'a pas l'objet
		if(World.data.getPersonnage(targetGuid) != null)
		{
			Target = World.data.getPersonnage(targetGuid);
		}
		if(!client.getPlayer().hasItemGuid(guid) || client.getPlayer().get_fight() != null || client.getPlayer().is_away())return;
		if(Target != null && (Target.get_fight() != null || Target.is_away()))return;
		Objet obj = World.data.getObjet(guid);
		if(obj == null) return;
		ObjTemplate T = obj.getTemplate();
		if(!obj.getTemplate().getConditions().equalsIgnoreCase("") && !ConditionParser.validConditions(client.getPlayer(),obj.getTemplate().getConditions()))
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "119|43");
			return;
		}
		T.applyAction(client.getPlayer(), Target, guid, cellID);
	}
}