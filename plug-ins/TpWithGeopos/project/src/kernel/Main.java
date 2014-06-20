package kernel;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.tool.command.Command;
import org.ancestra.evolutive.tool.plugin.Plugin;

public class Main extends Plugin {
	
	public void onEnable() {
		this.getWorld().getPacketPlugins().put(this.getWorld().valueOfPacket(TpWithGeopos.class), new TpWithGeopos());
		this.getWorld().getPlayerCommands().put("MAPID", new Command<Player>("MAPID") {

			@Override
			public void action(Player arg0, String[] arg1) {
				// L'action de la commande..
			}
			
		});
		
	}
	
	public void onDisable() {
		
	}
}