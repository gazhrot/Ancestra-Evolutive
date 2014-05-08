package org.ancestra.evolutive.login;


import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Client;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.enums.EmulatorInfos;
import org.apache.mina.core.session.IoSession;




public class LoginClient implements Client {
	private String key;
	private int packet = 0;
	private String name;
	private String password;
	private Account account;
	private IoSession session;
	
	public LoginClient(IoSession session) {
		this.session = session;
	}
	
	public IoSession getSession() {
		return session;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public void addPacket() {
		this.packet++;
	}
	
	public void parsePacket(String packet) {
		switch(this.packet)
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
				name = packet.toLowerCase();
				break;
			case 3://HashPass
				if(!packet.substring(0, 2).equalsIgnoreCase("#1"))
				{
					try {
						this.kick();
					} catch (Exception e) {}
				}
				this.password = packet;
				
				if(Account.login(name, this.password, this.key))
				{
					this.account = World.data.getCompteByName(name);
					
					if(this.getAccount().isLogged()) {
						if(this.getAccount().getLoginClient() != null)
							this.getAccount().getLoginClient().kick();
						else if(this.getAccount().getGameClient() != null) 
							this.getAccount().getGameClient().kick();
						
						if(!this.getAccount().isLogged()) {
							SocketManager.REALM_SEND_ALREADY_CONNECTED(this);
							kick();
							return;
						} else {
							World.data.getAccounts().remove(this.getAccount().getUUID());
							this.account = World.database.getAccountData().loadByName(name);
						}
					}
					
					if(this.getAccount().isBanned())
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
						if(this.getAccount().getGmLvl() == 0  && !this.getAccount().isVip())
						{
							SocketManager.REALM_SEND_TOO_MANY_PLAYER_ERROR(this);
							try {
								kick();
							} catch (Exception e) {}
							return;
						}
					}
					if(World.data.getGmAccess() > this.getAccount().getGmLvl())
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
					this.getAccount().setLoginClient(this);
					this.getAccount().setCurIp(ip);
					this.getAccount().setLogged(true);
					World.database.getAccountData().update(this.getAccount());
					SocketManager.REALM_SEND_Ad_Ac_AH_AlK_AQ_PACKETS(this, this.getAccount().getPseudo(),(this.getAccount().getGmLvl()>0?(1):(0)), this.getAccount().getQuestion() ); 
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
					this.packet--;
					Pending.PendingSystem(this.getAccount());
				}else
				if(packet.substring(0,2).equals("Ax"))
				{
					if(this.getAccount() == null)return;
					this.account = World.data.getCompteByName(name);
					SocketManager.REALM_SEND_PERSO_LIST(this, this.getAccount().getPlayers().size());
				}else
				if(packet.equals("AX1"))
				{
					Server.config.getGameServer().addWaitingCompte(this.getAccount());
					String ip = this.getAccount().getCurIp();
					SocketManager.REALM_SEND_GAME_SERVER_IP(this, this.getAccount().getUUID(),ip.equals("127.0.0.1"));
				}
				break;
		}
	}
	
	public void kick() {
		this.getAccount().setLogged(false);
		World.database.getAccountData().update(this.getAccount());
		
		if(!session.isClosing())
			session.close(true);
		Server.config.getRealmServer().getClients().remove(session.getId());
		this.getAccount().setLoginClient(null);
	}
}
