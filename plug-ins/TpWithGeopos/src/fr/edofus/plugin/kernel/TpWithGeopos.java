package fr.edofus.plugin.kernel;

import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.game.GameClient;
import fr.edofus.ancestra.evolutive.objects.Carte;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.Packet;
import fr.edofus.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("BaM")
public class TpWithGeopos implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getAccount().get_gmLvl() < 2)
			return;
		
		packet = packet.substring(3);

		if(packet.isEmpty())
			return;
		
		int x = Integer.parseInt(packet.split(",")[0]);
		int y = Integer.parseInt(packet.split(",")[1]);
		int cont = client.getPlayer().get_curCarte().getSubArea().get_area().get_superArea().get_id();

		Carte carte = World.database.getMapData().loadMapByPos(x, y, cont);
		
		if(carte == null)
			return;	
		
		int cell = carte.getRandomFreeCellID();
		
		if(carte.getCase(cell) == null)
			return;
		if(client.getPlayer().get_fight() != null)
			return;
		
		client.getPlayer().teleport(carte.get_id(), cell);
	}
}