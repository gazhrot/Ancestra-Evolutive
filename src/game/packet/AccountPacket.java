package game.packet;

import game.GameClient;
import game.packet.handler.Packet;
import common.Constants;
import common.SocketManager;
import common.World;

import core.Server;

public class AccountPacket {
	
	@Packet("AA")
	public static void packetAA(GameClient client, String packet) {
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

	@Packet("AB")
	public static void packetAB(GameClient client, String packet) {
		try	{
			int stat = Integer.parseInt(packet.substring(2).split("/u000A")[0]);
			client.getPlayer().boostStat(stat);
		} catch(NumberFormatException e){return;};
	}
	
	@Packet("AD")
	public static void packetAD(GameClient client, String packet) {
		String[] split = packet.substring(2).split("\\|");
		int GUID = Integer.parseInt(split[0]);
		String reponse = split.length>1?split[1]:"";
		
		if(client.getAccount().get_persos().containsKey(GUID))
		{
			if(client.getAccount().get_persos().get(GUID).get_lvl() <20 ||(client.getAccount().get_persos().get(GUID).get_lvl() >=20 && reponse.equals(client.getAccount().get_reponse())))
			{
				client.getAccount().deletePerso(GUID);
				SocketManager.GAME_SEND_PERSO_LIST(client, client.getAccount().get_persos());
			}
			else
				SocketManager.GAME_SEND_DELETE_PERSO_FAILED(client);
		}else
			SocketManager.GAME_SEND_DELETE_PERSO_FAILED(client);
	}

	@Packet("Af")
	public static void packetAf(GameClient client, String packet) {
		int queue = 1;
		int position = 1;
		SocketManager.MULTI_SEND_Af_PACKET(client, position, 1, 1, "1", queue);
	}
	
	@Packet("Ai")
	public static void packetAi(GameClient client, String packet) {
		client.getAccount().setClientKey(packet.substring(2));
	}
	
	@Packet("AL")
	public static void packetAL(GameClient client, String packet) {
		SocketManager.GAME_SEND_PERSO_LIST(client, client.getAccount().get_persos());	
	}
	
	@Packet("AS")
	public static void packetAS(GameClient client, String packet) {
		int id = Integer.parseInt(packet.substring(2));
		if(client.getAccount().get_persos().get(id) != null)
		{
			client.getAccount().setGameClient(client);
			client.setPlayer(World.data.getPersonnage(id));
			if(client.getPlayer() != null) { 
				client.getPlayer().OnJoinGame();
				return;
			}
		}
		SocketManager.GAME_SEND_PERSO_SELECTION_FAILED(client);
	}
	
	@Packet("AT")
	public static void packetAT(GameClient client, String packet) {
		int guid = Integer.parseInt(packet.substring(2));
		client.setAccount(Server.config.getGameServer().getWaitingCompte(guid));
		
		if(client.getAccount() != null && client.getAccount().get_curPerso() == null) {
			String ip = Constants.getIp(client.getSession().getRemoteAddress().toString());
			client.getAccount().setRealmThread(null);
			client.getAccount().setGameClient(client);
			client.getAccount().setCurIP(ip);
			client.getAccount().setLogged(true);
			
			World.database.getAccountData().update(client.getAccount());
			Server.config.getGameServer().delWaitingCompte(client.getAccount());
			SocketManager.GAME_SEND_ATTRIBUTE_SUCCESS(client);
		} else {
			SocketManager.GAME_SEND_ATTRIBUTE_FAILED(client);
		}
	}
	
	@Packet("AV")
	public static void packetAV(GameClient client, String packet) {
		SocketManager.GAME_SEND_AV0(client);
	}
	
	@Packet("AP")
	public static void packetAP(GameClient client, String packet) {
		SocketManager.REALM_SEND_REQUIRED_APK(client);
	}
}