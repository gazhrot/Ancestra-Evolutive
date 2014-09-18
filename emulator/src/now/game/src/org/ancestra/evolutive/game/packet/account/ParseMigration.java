package org.ancestra.evolutive.game.packet.account;

import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.tool.plugin.packet.Packet;
import org.ancestra.evolutive.tool.plugin.packet.PacketParser;
import org.ancestra.evolutive.util.Migration;

@Packet("AM")
public class ParseMigration implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		switch(packet.charAt(2)) {
		case '-':
			try {
				Migration migration = Migration.migrations.get(client.getAccount().getUUID());
				
				if(migration == null)
					return;
				
				int player = Integer.parseInt(packet.substring(3));
				int server = migration.search(player);
				
				Server.config.getExchangeClient().send("MD" + player + "|" + server);
				
				try {
					Thread.sleep(1000);
				} catch(Exception e) {}
				
				migration.getPlayers().clear();
				Server.config.getExchangeClient().send("MP" + client.getAccount().getUUID());
			} catch(Exception e) {}
			break;
		default:
			try {
				Migration migration = Migration.migrations.get(client.getAccount().getUUID());
				
				if(migration == null)
					return;
				
				String[] split = packet.substring(2).split("\\;");
				String name = split[1];
				
				if(World.database.getPlayerData().exist(name)) {
					client.send("AAEa");
					return;
				}
				
				boolean isValid = true;
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
						if(tiretCount >= 1) {
							isValid = false;
							break;
						} else {
							tiretCount++;
						}
					}
				}
				
				if(!isValid) {
					client.send("AAEa");
					return;
				}
				
				int server = migration.search(Integer.parseInt(split[0]));
				
				Server.config.getExchangeClient().send("MO" + split[0] + "|" + server);
			} catch(Exception e) {}
			break;
		}
	}
}