package org.ancestra.evolutive.game.packet.account;

import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;

@Packet("AA")
public class AddCharacter implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		String[] infos = packet.substring(2).split("\\|");
		
		if(World.database.getPlayerData().exist(infos[0])) {
			client.send("AAEa");
			return;
		}
	
		boolean isValid = true;
		String name = infos[0].toLowerCase();
		
		if(name.length() > 20 || name.contains("mj") || name.contains("modo") || name.contains("admin"))
			isValid = false;
		
		if(isValid) {			
			int tiretCount = 0;
			char exLetterA = ' ', exLetterB = ' ';
			
			for(char curLetter : name.toCharArray()) {
				if(!((curLetter >= 'a' && curLetter <= 'z') || curLetter == '-')) {
					isValid = false;
					break;
				}
				if(curLetter == exLetterA && curLetter == exLetterB) {
					isValid = false;
					break;
				}
				if(curLetter >= 'a' && curLetter <= 'z') {
					exLetterA = exLetterB;
					exLetterB = curLetter;
				}
				if(curLetter == '-') {
					if(tiretCount >= 1)	{
						isValid = false;
						break;
					} else {
						tiretCount++;
					}
				}
			}
		}
		
		if(!isValid || Integer.parseInt(infos[1]) <= 0 || Integer.parseInt(infos[1]) > 12)
			client.send("AAEa");
		else if(client.getAccount().getPlayers().size() >= Server.config.getMaxPlayersPerAccount())
			client.send("AAEf");
		else if(client.getAccount().createPlayer(infos[0], Integer.parseInt(infos[2]), Integer.parseInt(infos[1]),
				Integer.parseInt(infos[3]), Integer.parseInt(infos[4]),	Integer.parseInt(infos[5]))) {
			client.send("AAK");
            client.send(client.getAccount().getAccountHelper().getPlayersList());
		} else {
			client.send("AAEF");
		}	
	}
}