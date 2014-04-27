package fr.edofus.plugin.kernel;

import fr.edofus.ancestra.evolutive.tool.plugin.Plugin;

public class Main extends Plugin {

	public void onEnable() {
		String packet = this.getWorld().valueOfPacket(TpWithGeopos.class);
		try {
			this.getWorld().addPacketPlugins(packet, TpWithGeopos.class.newInstance());
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {}
	}
	
	public void onDisable() {
		String packet = this.getWorld().valueOfPacket(TpWithGeopos.class);
		this.getWorld().getPacketPlugins().remove(packet);
	}
}