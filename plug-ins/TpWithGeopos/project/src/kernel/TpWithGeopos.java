package kernel;

import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("BaM")
public class TpWithGeopos implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		if(client.getAccount().getGmLvl() < 2)
			return;
		
		packet = packet.substring(3);

		if(packet.isEmpty())
			return;
		
		int x = Integer.parseInt(packet.split(",")[0]);
		int y = Integer.parseInt(packet.split(",")[1]);
		int cont = client.getPlayer().getCurMap().getSubArea().getArea().getContinent().getId();

		Maps map = World.database.getMapData().loadMapByPos(x, y, cont);
		
		if(map == null)
			return;	
		
		int cell = map.getRandomFreeCell();
		
		if(map.getCases().get(cell) == null)
			return;
		if(client.getPlayer().getFight() != null)
			return;
		
		client.getPlayer().teleport(map.getId(), cell);
	}
}