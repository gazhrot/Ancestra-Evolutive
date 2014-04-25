package game.packet.guild;

import objects.Percepteur;
import tool.plugin.packet.Packet;
import tool.plugin.packet.PacketParser;
import client.Player;

import common.Constants;
import common.Formulas;
import common.SocketManager;
import core.World;

import game.GameClient;

@Packet("gH")
public class AddCollector implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().get_guild() == null || client.getPlayer().get_fight() != null || client.getPlayer().is_away())return;
		if(!client.getPlayer().getGuildMember().canDo(Constants.G_POSPERCO))return;//Pas le droit de le poser
		if(client.getPlayer().get_guild().getMembers().size() < 10)return;//Guilde invalide
		short price = (short)(1000+10*client.getPlayer().get_guild().get_lvl());//Calcul du prix du percepteur
		if(client.getPlayer().get_kamas() < price)//Kamas insuffisants
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "182");
			return;
		}
		if(Percepteur.GetPercoGuildID(client.getPlayer().get_curCarte().get_id()) > 0)//La carte poss�de un perco
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1168;1");
			return;
		}
		if(client.getPlayer().get_curCarte().get_placesStr().length() < 5)//La map ne poss�de pas de "places"
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "113");
			return;
		}
		if(Percepteur.CountPercoGuild(client.getPlayer().get_guild().get_id()) >= client.getPlayer().get_guild().get_nbrPerco()) return;//Limite de percepteur
		short random1 = (short) (Formulas.getRandomValue(1, 39));
		short random2 = (short) (Formulas.getRandomValue(1, 71));
		//Ajout du Perco.
		int id = World.database.getCollectorData().nextId();
		Percepteur perco = new Percepteur(id, client.getPlayer().get_curCarte().get_id(), client.getPlayer().get_curCell().getID(), (byte)3, client.getPlayer().get_guild().get_id(), random1, random2, "", 0, 0);
		World.data.addPerco(perco);
		SocketManager.GAME_SEND_ADD_PERCO_TO_MAP(client.getPlayer().get_curCarte());
		World.database.getCollectorData().create(perco);
		for(Player z : client.getPlayer().get_guild().getMembers())
		{
			if(z != null && z.isOnline())
			{
				SocketManager.GAME_SEND_gITM_PACKET(z, Percepteur.parsetoGuild(z.get_guild().get_id()));
				String str = "";
				str += "S"+perco.get_N1()+","+perco.get_N2()+"|";
				str += perco.get_mapID()+"|";
				str += World.data.getCarte((short)perco.get_mapID()).getX()+"|"+World.data.getCarte((short)perco.get_mapID()).getY()+"|"+client.getPlayer().get_name();
				SocketManager.GAME_SEND_gT_PACKET(z, str);
			}
		}
	}
}