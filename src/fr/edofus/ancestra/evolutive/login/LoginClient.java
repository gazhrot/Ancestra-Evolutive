package fr.edofus.ancestra.evolutive.login;


import org.apache.mina.core.session.IoSession;



import fr.edofus.ancestra.evolutive.client.Account;
import fr.edofus.ancestra.evolutive.client.Client;
import fr.edofus.ancestra.evolutive.common.Constants;
import fr.edofus.ancestra.evolutive.common.SocketManager;
import fr.edofus.ancestra.evolutive.core.Server;
import fr.edofus.ancestra.evolutive.core.World;
import fr.edofus.ancestra.evolutive.enums.EmulatorInfos;

public class LoginClient implements Client{
	private String _hashKey;
	private int _packetNum = 0;
	private String _accountName;
	private String _hashPass;
	private Account account;
	private IoSession session;
	
	public LoginClient(IoSession session) {
		this.setSession(session);
	}
	
	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}
	
	public void parsePacket(String packet) {
		switch(_packetNum)
		{
			case 1://Version
				if(!packet.equalsIgnoreCase(EmulatorInfos.CLIENT_RELESE.toString()) && !Constants.IGNORE_VERSION)
				{
					SocketManager.REALM_SEND_REQUIRED_VERSION(this);
					try {
						this.kick();
					} catch (Exception e) {}
				}
				break;
			case 2://Account Name
				_accountName = packet.toLowerCase();
				break;
			case 3://HashPass
				if(!packet.substring(0, 2).equalsIgnoreCase("#1"))
				{
					try {
						this.kick();
					} catch (Exception e) {}
				}
				_hashPass = packet;
				
				if(Account.login(_accountName,_hashPass,get_hashKey()))
				{
					account = World.data.getCompteByName(_accountName);
					
					if(account.isLogged()) {
						if(account.getLoginClient() != null)
							account.getLoginClient().kick();
						else if(account.getGameClient() != null) 
							account.getGameClient().kick();
						
						if(!account.isLogged()) {
							SocketManager.REALM_SEND_ALREADY_CONNECTED(this);
							kick();
							return;
						} else {
							World.data.getAccounts().remove(account.getUUID());
							account = World.database.getAccountData().loadByName(_accountName);
						}
					}
					
					if(account.isBanned())
					{
						SocketManager.REALM_SEND_BANNED(this);
						try {
							kick();
						} catch (Exception e) {}
						return;
					}
					if(Server.config.getPlayerLimitOnServer() != -1 && Server.config.getPlayerLimitOnServer() <= Server.config.getGameServer().getPlayerNumber())
					{
						//Seulement si joueur
						if(account.getGmLvl() == 0  && !account.isVip())
						{
							SocketManager.REALM_SEND_TOO_MANY_PLAYER_ERROR(this);
							try {
								kick();
							} catch (Exception e) {}
							return;
						}
					}
					if(World.data.getGmAccess() > account.getGmLvl())
					{
						SocketManager.REALM_SEND_TOO_MANY_PLAYER_ERROR(this);
						return;
					}
					String ip = Constants.getIp(session.getRemoteAddress().toString());
					if(Constants.IPcompareToBanIP(ip))
					{
						SocketManager.REALM_SEND_BANNED(this);
						return;
					}
					//Verification Multi compte
					if(!Server.config.isMultiAccount())
					{
						if(World.data.ipIsUsed(ip))
						{
							SocketManager.REALM_SEND_TOO_MANY_PLAYER_ERROR(this);
							try {
								kick();
							} catch (Exception e) {}
							return;
						}
					}
					account.setLoginClient(this);
					account.setCurIp(ip);
					account.setLogged(true);
					World.database.getAccountData().update(account);
					SocketManager.REALM_SEND_Ad_Ac_AH_AlK_AQ_PACKETS(this, account.getPseudo(),(account.getGmLvl()>0?(1):(0)), account.getQuestion() ); 
				}else//Si le compte n'a pas �t� reconnu
				{
					SocketManager.REALM_SEND_LOGIN_ERROR(this);
					try {
						kick();
					} catch (Exception e) {}
				}
				break;
			default: 
				if(packet.substring(0,2).equals("Af"))
				{
					_packetNum--;
					Pending.PendingSystem(account);
				}else
				if(packet.substring(0,2).equals("Ax"))
				{
					if(account == null)return;
					account = World.data.getCompteByName(_accountName);
					SocketManager.REALM_SEND_PERSO_LIST(this, account.getPlayers().size());
				}else
				if(packet.equals("AX1"))
				{
					Server.config.getGameServer().addWaitingCompte(account);
					String ip = account.getCurIp();
					SocketManager.REALM_SEND_GAME_SERVER_IP(this, account.getUUID(),ip.equals("127.0.0.1"));
				}
				break;
		}
	}
	
	public void kick() {
		account.setLogged(false);
		World.database.getAccountData().update(account);
		
		if(!session.isClosing())
			session.close(true);
		Server.config.getRealmServer().getClients().remove(session.getId());
		account.setLoginClient(null);
	}

	public String get_hashKey() {
		return _hashKey;
	}

	public void set_hashKey(String _hashKey) {
		this._hashKey = _hashKey;
	}	

	public void addPacket() {
		this._packetNum++;
	}
}
