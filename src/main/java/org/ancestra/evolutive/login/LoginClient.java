package org.ancestra.evolutive.login;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Client;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.CryptManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.enums.EmulatorInfos;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;

public class LoginClient implements Client {
	
	private String key;
	private int packetCount = 0;
	private String name;
	private Account account = null;
	private IoSession session;
	Logger logger = (Logger) LoggerFactory.getLogger(LoginClient.class);

	public LoginClient(IoSession session, String key) {
		this.session = session;
        this.key = key;
        logger = (Logger)LoggerFactory.getLogger("rsession" + session.getId());
	}

    @Override
	public IoSession getSession() {
		return session;
	}

    @Override
    public void send(String message) {
        if(message != null && !message.isEmpty()){
            session.write(message);
        }
    }

    @Override
	public Account getAccount() {
		return account;
	}
	
	public void incrementPacket() {
		this.packetCount++;
	}
	
	public void parsePacket(String message) {
		switch(this.packetCount)
		{
			case 1://Version
				if(!message.equalsIgnoreCase(EmulatorInfos.CLIENT_RELEASE.toString()) && !Constants.IGNORE_VERSION) {
					send("AlEv" +EmulatorInfos.CLIENT_RELEASE.toString());
                    kick();
				}
				break;
			case 2://Account Name
				name = message.toLowerCase();
				break;
			case 3://HashPass
				if(!message.substring(0, 2).equalsIgnoreCase("#1") || !checkLogin(message)){
                    kick();
				}
                break;
			default:
                if(packetCount <= 3 || this.getAccount() == null){
                    return;
                }
				if(message.substring(0,2).equals("Af")) {
					this.packetCount--;
					Pending.PendingSystem(this.getAccount());
                    return;
				}
                if(message.substring(0,2).equals("Ax")) {
                    String packet = "";
                    packet += "AxK31536000000";
                    if(account.getPlayers().size()>0)
                        packet+= "|1," + account.getPlayers().size();//ServeurID

                    send(packet);
                    return;
				}
				if(message.equals("AX1")) {
					Server.config.getGameServer().addWaitingCompte(this.getAccount());
					send(getServerAddress());
				}
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

    private boolean checkLogin(String password){
        String ip = getIp(session);
        //Verification de l'ip
        if(Constants.IPcompareToBanIP(ip)) {
            send("AlEb");
            return false;
        }

        //Verification des info
        if((account = World.data.getCompteByName(name)) == null) {
            send("AlEf");
            return false;
        }
        if(this.getAccount().isBanned()) {
            send("AlEb");
            return false;
        }
        if(!account.isValidPass(password,key)) {
            send("AlEf");
            return false;
        }

        //Verification du statut de connecte
        if(this.getAccount().isLogged()) {
            if(this.getAccount().getLoginClient() != null)
                this.getAccount().getLoginClient().kick();
            else if(this.getAccount().getGameClient() != null)
                this.getAccount().getGameClient().kick();

            if(!this.getAccount().isLogged()) {
                send("AlEc");
                return false;
            } else {
                World.data.getAccounts().remove(this.getAccount().getUUID());
                this.account = World.database.getAccountData().load(name);
            }
        }

        //Verification de la limite de joueur
        if(Server.config.getPlayerLimitOnServer() != -1
                && Server.config.getPlayerLimitOnServer() <= Server.config.getGameServer().getPlayerNumber()) {
            if (this.getAccount().getGmLvl() == 0 && !this.getAccount().isVip()) {
                send("AlEw");
                return false;
            }
            if (World.data.getGmAccess() > this.getAccount().getGmLvl()) {
                send("AlEw");
                return false;
            }
        }

        //Verification Multi compte
        if(!Server.config.isMultiAccount()) {
            if(World.data.ipIsUsed(ip)) {
                send("AlEw");
                return false;
            }
        }

        this.getAccount().setLoginClient(this);
        this.getAccount().setCurIp(ip);
        this.getAccount().setLogged(true);
        World.database.getAccountData().update(this.getAccount());


        StringBuilder packet = new StringBuilder();
        packet.append("Ad").append(account.getPseudo()).append((char)0x00);
        packet.append("Ac0").append((char)0x00);
        //AH[ID];[State];[Completion];[CanLog]
        packet.append("AH1;").append(World.data.get_state()).append(";110;1").append((char)0x00);
        packet.append("AlK").append(account.getGmLvl() > 0 ? 1 : 0).append((char)0x00);
        packet.append("AQ").append(account.getQuestion().replace(" ", "+"));
        send(packet.toString());
        return true;
    }

    private String getIp(IoSession session){
        return session.getRemoteAddress().toString().substring(1).split(":")[0];
    }

    private String getServerAddress(){
        String packet = "A";
        if(Server.config.isUseIp())
        {
            String ip = Server.config.isIpLoopBack() && getIp(session).equals("127.0.0.1")
                    ? CryptManager.CryptIP("127.0.0.1")+CryptManager.CryptPort(Server.config.getGamePort())
                    :  Server.config.getGameServerIpCrypted();
            packet += "XK"+ip+account.getUUID();
        }
        else {
            String ip = Server.config.isIpLoopBack() && getIp(session).equals("127.0.0.1")?"127.0.0.1":Server.config.getIp();
            packet += "YK"+ip+":"+Server.config.getGamePort()+";"+account.getUUID();
        }
        return packet;
    }

}
