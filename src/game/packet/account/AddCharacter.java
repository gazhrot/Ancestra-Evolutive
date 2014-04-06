package game.packet.account;

import common.SocketManager;
import common.World;

import core.Server;
import game.GameClient;
import game.packet.Packet;
import game.packet.PacketParser;

@Packet("AA")
public class AddCharacter implements PacketParser {

	@Override
	public void parse(GameClient client, String packet) {
		String[] infos = packet.substring(2).split("\\|");
		if(World.database.getCharacterData().exist(infos[0]))
		{
			SocketManager.GAME_SEND_NAME_ALREADY_EXIST(client);
			return;
		}
		//Validation du nom du personnage
		boolean isValid = true;
		String name = infos[0].toLowerCase();
		//Verifie d'abord si il contient des termes d�finit
		if(name.length() > 20 || name.contains("mj") || name.contains("modo") || name.contains("admin"))
		{
			isValid = false;
		}
		//Si le nom passe le test, on verifie que les caractere entrer sont correct.
		if(isValid)
		{
			int tiretCount = 0;
			char exLetterA = ' ';
			char exLetterB = ' ';
			for(char curLetter : name.toCharArray())
			{
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
					}else {
						tiretCount++;
					}
				}
			}
		}
		//Si le nom est invalide
		if(!isValid || Integer.parseInt(infos[1]) <= 0 || Integer.parseInt(infos[1]) > 12)
			SocketManager.GAME_SEND_NAME_ALREADY_EXIST(client);
		else if(client.getAccount().GET_PERSO_NUMBER() >= Server.config.getMaxPlayersPerAccount())
			SocketManager.GAME_SEND_CREATE_PERSO_FULL(client);
		else if(client.getAccount().createPerso(infos[0], Integer.parseInt(infos[2]), Integer.parseInt(infos[1]),
				Integer.parseInt(infos[3]), Integer.parseInt(infos[4]),	Integer.parseInt(infos[5]))) {
			SocketManager.GAME_SEND_CREATE_OK(client);
			SocketManager.GAME_SEND_PERSO_LIST(client, client.getAccount().get_persos());
		} else {
			SocketManager.GAME_SEND_CREATE_FAILED(client);
		}	
	}
}