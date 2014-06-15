package org.ancestra.evolutive.game.packet.guild;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.collector.Collector;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("gH")
public class AddCollector implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getPlayer().getGuild() == null || client.getPlayer().getFight() != null || client.getPlayer().isAway())return;
		if(!client.getPlayer().getGuildMember().canDo(Constants.G_POSPERCO))return;//Pas le droit de le poser
		if(client.getPlayer().getGuild().getMembers().size() < 10)return;//Guilde invalide
		short price = (short)(1000+10*client.getPlayer().getGuild().getLevel());//Calcul du prix du percepteur
		if(client.getPlayer().getKamas() < price)//Kamas insuffisants
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "182");
			return;
		}
		if(World.data.getCollector(client.getPlayer().getMap()) != null)//La carte poss�de un perco
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "1168;1");
			return;
		}
		if(client.getPlayer().getMap().getPlaces().length() < 5)//La map ne poss�de pas de "places"
		{
			SocketManager.GAME_SEND_Im_PACKET(client.getPlayer(), "113");
			return;
		}
		if(Collector.CountPercoGuild(client.getPlayer().getGuild().getId()) >= client.getPlayer().getGuild().getNbrCollector()) return;//Limite de percepteur
		short random1 = (short) (Formulas.getRandomValue(1, 39));
		short random2 = (short) (Formulas.getRandomValue(1, 71));
		//Ajout du Perco.
		int id = World.database.getCollectorData().nextId();
		Collector perco = new Collector(id, client.getPlayer().getMap().getId(), client.getPlayer().getCell().getId(), (byte)3, client.getPlayer().getGuild().getId(), random1, random2, "", 0, 0);
		World.data.addPerco(perco);
		SocketManager.GAME_SEND_ADD_PERCO_TO_MAP(client.getPlayer().getMap());
		World.database.getCollectorData().create(perco);
		for(Player z : client.getPlayer().getGuild().getMembers())
		{
			if(z != null && z.isOnline())
			{
				SocketManager.GAME_SEND_gITM_PACKET(z, Collector.parsetoGuild(z.getGuild().getId()));
				String str = "";
				str += "S"+perco.getFirstNameId()+","+perco.getLastNameId()+"|";
				str += perco.getMap().getId()+"|";
				str += perco.getMap().getX()+"|"
                        +perco.getMap().getY()+"|"+client.getPlayer().getName();
				SocketManager.GAME_SEND_gT_PACKET(z, str);
			}
		}
	}
}