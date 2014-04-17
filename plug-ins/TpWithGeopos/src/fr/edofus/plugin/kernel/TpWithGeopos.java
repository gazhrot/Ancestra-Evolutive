package fr.edofus.plugin.kernel;

import objects.Carte;

import common.World;

import game.GameClient;
import game.packet.PacketParser;
import game.packet.Packet;

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
		
		Carte carte = World.data.getCarteByPosAndCont(x, y, cont);
		
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