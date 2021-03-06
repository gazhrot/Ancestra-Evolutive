package org.ancestra.evolutive.client;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.common.CryptManager;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.hdv.HdvEntry;
import org.ancestra.evolutive.object.Object;

import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Account {

	private int UUID;
	private String name;
	private String password;
	private String pseudo;
	private String key;
    private AccountHelper accountHelper;
	
	private String question;
	private String answer;
	private boolean banned = false;
	private int gmLvl = 0;
	
	private String lastConnection = "";
	private String lastIp = "";
	private String curIp = "";
	private GameClient gameClient;
	private Player curPlayer;
	
	private boolean logged;
	private boolean mute = false;
	private Timer muteTimer;
	private int position = -1;
	private long subscriber = 1;//Timestamp en secondes
	private long bankKamas = 0;
	private Map<Integer, Object> bankItems = new TreeMap<>();
	private ArrayList<Integer> friends = new ArrayList<>();
	private ArrayList<Integer> enemys = new ArrayList<>();
	private Map<Integer, ArrayList<HdvEntry>> hdvs;// Contient les items des HDV format : <hdvID,<cheapestID>>

	private final Logger logger;
	public Account(int UUID, String name,String password, String pseudo, String question, String answer, 
			int gmLvl, boolean banned, String lastIp, String lastConnection, String bank,
			int bankKamas, String friends, String enemys, boolean logged, long subscriber)
	{
		this.UUID = UUID;
		this.name = name;
		this.password = password;
		this.pseudo = pseudo;
		this.question = question;
		this.answer	= answer;
		this.gmLvl = gmLvl;
		this.banned	= banned;
		this.lastIp	= lastIp;
		this.lastConnection = lastConnection;
		this.bankKamas = bankKamas;
		this.hdvs = World.data.getMyObjects(this.UUID);
		this.logged = logged;
        this.logger = (Logger)LoggerFactory.getLogger("account." + name);
		for(String item: bank.split("\\|"))	{
			if(item.equals(""))
				continue;
			
			String[] infos = item.split(":");
			int guid = Integer.parseInt(infos[0]);
			Object obj = World.data.getObject(guid);
			
			if(obj == null)
				continue;
			this.bankItems.put(obj.getId(), obj);
		}
		
		for(String str : friends.split("\\;")) {
			try {
				this.friends.add(Integer.parseInt(str));
			} catch(Exception e) {}
		}

		for(String str : enemys.split("\\;")) {
			try	{
				this.enemys.add(Integer.parseInt(str));
			} catch(Exception e) {}
		}
        accountHelper = new AccountHelper(this);

    }
	
	public void updateInfos(int UUID, String name, String password, String pseudo, String question, String answer, int gmLvl, boolean banned) {
		this.UUID = UUID;
		this.name = name;
		this.password = password;
		this.pseudo = pseudo;
		this.question = question;
		this.answer = answer;
		this.gmLvl = gmLvl;
		this.banned	= banned;
	}
	
	public int getUUID() {
		return UUID;
	}

	public String getName() {
		return name;
	}
	
	public boolean isValidPass(String pass, String hash) {
		return pass.equals(CryptManager.CryptPassword(hash, this.password));
	}
	
	public String getPseudo() {
		return pseudo;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public boolean isBanned() {
		return banned;
	}
	
	public void setBanned(boolean banned) {
		this.banned = banned;
	}

    public AccountHelper getAccountHelper() {
        return accountHelper;
    }

	public int getGmLvl() {
		return gmLvl;
	}
	
	public void setGmLvl(int gmLvl) {
		this.gmLvl = gmLvl;
	}
	
	public String getLastConnection() {
		return lastConnection;
	}
	
	public void setLastConnection(String lastConnection) {
		this.lastConnection = lastConnection;
	}
	
	public String getLastIp() {
		return lastIp;
	}
	
	public void setLastIp(String lastIp) {
		this.lastIp = lastIp;
	}
	
	public String getCurIp() {
		return curIp;
	}
	
	public void setCurIp(String curIp) {
		this.curIp = curIp;
	}
	
	public GameClient getGameClient() {
		return gameClient;
	}
	
	public void setGameClient(GameClient gameClient) {
		this.gameClient = gameClient;
	}
	
	public Player getCurPlayer() {
		return curPlayer;
	}
	
	public void setCurPlayer(Player curPlayer) {
		this.curPlayer = curPlayer;
	}
	
	public boolean isOnline() {
		if(gameClient != null)
			return true;
		return false;
	}
	
	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}
	
	public boolean isMuted() {
		return mute;
	}
	
	public void mute(boolean muted, int time) {
		this.mute = muted;
		String msg = "";
		
		if(this.mute)
			msg = "Vous avez �t� mute !";
		else 
			msg = "Vous n'�tes plus mute !";
		
		SocketManager.GAME_SEND_MESSAGE(this.getCurPlayer(), msg, Server.config.getMotdColor());
		
		if(time == 0)
			return;
		if(this.muteTimer == null && time > 0) {
			this.muteTimer = new Timer(time*1000,new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					mute(false,0);
					muteTimer.stop();
				}
			});
			this.muteTimer.start();
		} else if(time ==0) {
			this.muteTimer = null;
		} else {
			if(this.muteTimer.isRunning()) 
				this.muteTimer.stop(); 
			this.muteTimer.setInitialDelay(time*1000); 
			this.muteTimer.start(); 
		}
	}
	
	public Timer getMuteTimer() {
		return muteTimer;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public long getSubscribeRemaining() {
		if(!Server.config.isUseSubscribe()) 
			return 525600L;
		long remaining = this.subscriber - System.currentTimeMillis();
		return remaining <= 0L ? 0L : remaining;
	}
		
	public long getBankKamas() {
		return bankKamas;
	}
	
	public void setBankKamas(long kamas) {
		this.bankKamas = kamas;
		World.database.getAccountData().update(this);
	}
	
	public String parseBankObjectsToDB() {
		StringBuilder str = new StringBuilder();
		if(this.bankItems.isEmpty())
			return "";
		for(Entry<Integer,Object> entry : this.bankItems.entrySet())
			str.append(entry.getValue().getId()).append("|");
		return str.toString();
	}
	
	public Map<Integer, Object> getBank() {
		return bankItems;
	}

	public void addFriend(int guid) {
		if(this.getUUID() == guid) {
			SocketManager.GAME_SEND_FA_PACKET(this.getCurPlayer(), "Ey");
			return;
		}
		if(!this.friends.contains(guid)) {
			this.friends.add(guid);
			SocketManager.GAME_SEND_FA_PACKET(this.getCurPlayer(), "K" + World.data.getCompte(guid).getPseudo() + World.data.getCompte(guid).getCurPlayer().parseToFriendList(this.getUUID()));
			World.database.getAccountData().update(this);
		} else {
			SocketManager.GAME_SEND_FA_PACKET(this.getCurPlayer(),"Ea");
		}
	}
	
	public void removeFriend(int guid) {
		if(this.friends.remove((java.lang.Object) guid))
			World.database.getAccountData().update(this);
		SocketManager.GAME_SEND_FD_PACKET(this.getCurPlayer(), "K");
	}
	
	public boolean isFriendWith(int guid) {
		return friends.contains(guid);
	}
	
	public String parseFriendToDb() {
		String str = "";
		for(int uuid: this.friends) {
			if(!str.equalsIgnoreCase(""))
				str += ";";
			str += uuid+"";
		}
		return str;
	}

	public String parseFriend() {
		StringBuilder str = new StringBuilder();
		
		if(this.friends.isEmpty())
			return "";
		
		for(int uuid: this.friends) {
			Account account = World.data.getCompte(uuid);
			
			if(account == null)
				continue;
			
			str.append("|").append(account.getPseudo());

			if(!account.isOnline())
				continue;
			
			Player player = account.getCurPlayer();
			
			if(player == null)
				continue;
			
			str.append(player.parseToFriendList(this.UUID));
		}
		return str.toString();
	}
	
	public void sendOnline() {
		for(int uuid: this.friends) {
			if(this.isFriendWith(uuid)) {
				Player player = World.data.getPlayer(uuid);
				if(player != null && player.isShowFriendConnection() && player.isOnline())
					SocketManager.GAME_SEND_FRIEND_ONLINE(this.getCurPlayer(), player);
			}
		}
	}

	public void addEnemy(String packet, int guid) {
		if(this.getUUID() == guid) {
			SocketManager.GAME_SEND_FA_PACKET(this.getCurPlayer(), "Ey");
			return;
		}
		if(!this.enemys.contains(guid))	{
			this.enemys.add(guid);
			Player player = World.data.getPlayerByName(packet);
			SocketManager.GAME_SEND_ADD_ENEMY(this.getCurPlayer(), player);
			World.database.getAccountData().update(this);
		} else {
			SocketManager.GAME_SEND_iAEA_PACKET(this.getCurPlayer());
		}
	}
	
	public void removeEnemy(int guid) {
		if(this.enemys.remove((java.lang.Object) guid))
			World.database.getAccountData().update(this);
		SocketManager.GAME_SEND_iD_COMMANDE(this.getCurPlayer(), "K");
	}
	
	public boolean isEnemyWith(int guid) {
		return enemys.contains(guid);
	}
	
	public String parseEnemyToDb() {
		String str = "";
		for(int uuid: this.enemys) {
			if(!str.equalsIgnoreCase(""))
				str += ";";
			str += uuid+"";
		}
		return str;
	}
	
	public String parseEnemy() {
		StringBuilder str = new StringBuilder();
		
		if(this.enemys.isEmpty())
			return "";
		
		for(int  uuid: this.enemys) {
			Account account = World.data.getCompte(uuid);
			
			if(account == null)
				continue;
			
			str.append("|").append(account.getPseudo());
			
			if(!account.isOnline())
				continue;
			
			Player player = account.getCurPlayer();
			
			if(player == null)
				continue;
			
			str.append(player.parseToEnemyList(this.getUUID()));
		}
		return str.toString();
	}
	
	public boolean recoverItem(int line, int amount) {
		if(this.getCurPlayer() == null)
			return false;
		if(this.getCurPlayer().getIsTradingWith() >= 0)
			return false;
		
		int hdvID = Math.abs(this.getCurPlayer().getIsTradingWith());//R�cup�re l'ID de l'HDV
		HdvEntry entry = null;
		
		for(HdvEntry tempEntry : this.hdvs.get(hdvID)) {//Boucle dans la liste d'entry de l'HDV pour trouver un entry avec le meme cheapestID que sp�cifi�
			logger.debug(tempEntry.getObject().getTemplate().getName());
            if(tempEntry.getLine() == line) {//Si la boucle trouve un Object avec le meme cheapestID, arrete la boucle
				entry = tempEntry;
				break;
			}
		}
		if(entry == null)//Si entry == null cela veut dire que la boucle s'est effectu� sans trouver d'item avec le meme cheapestID
			return false;

		this.hdvs.get(hdvID).remove(entry);//Retire l'item de la liste des Objects a vendre du compte
        logger.info("La supression de l item va etre effectuee");
		Object obj = entry.getObject();
		
		boolean b = this.getCurPlayer().addObject(obj,true);//False = Meme item dans l'inventaire donc augmente la qua
		if(!b)
			World.data.removeObject(obj.getId());
		
		World.data.getHdv(hdvID).delEntry(entry);//Retire l'item de l'HDV
		return true;		
	}
	
	public HdvEntry[] getHdvItems(int id) {
		if(this.hdvs.get(id) == null)
			return new HdvEntry[1];
		
		HdvEntry[] toReturn = new HdvEntry[20];
		for (int i = 0; i < this.hdvs.get(id).size(); i++)
			toReturn[i] = this.hdvs.get(id).get(i);
		
		return toReturn;
	}
	
	public int countHdvItems(int id) {
		if(this.hdvs.get(id) == null)
			return 0;
		return this.hdvs.get(id).size();
	}

	public Map<Integer, Player> getPlayers() {
		Map<Integer, Player> players = new HashMap<>();
		for(Entry<Integer, Player> player : World.data.getPlayers().entrySet()) {
			if(player.getValue().getAccount().getUUID() == this.getUUID()) {
				if(player.getValue().getAccount() == null ||
						player.getValue().getAccount().getGameClient() == null)
					player.getValue().setAccount(this);
				players.put(player.getKey(), player.getValue());
			}
		}
		return players;
	}

	
	public boolean createPlayer(String name, int sexe, int classe, int color1, int color2, int color3) {
		return Player.create(name, sexe, classe, color1, color2, color3, this) != null;
	}

    /**
     * Efface le personnage du joueur correspondant
     * @param id Identifiant du player a supprimer
     * @param answer Reponse a la question secrete
     *               (n'est demandee que si le personnage a un niveau => a 20)
     * @return true si le delete a fonctionner
     * false sinon
     */
	public boolean deletePlayer(int id,String answer) {
        Player playerToDelete = getPlayers().get(id);
		if(this.getPlayers().containsKey(id)) {
            if (playerToDelete.getLevel() < 20 || answer.equals(answer)) {
                World.data.deletePerso(this.getPlayers().get(id));
                this.getPlayers().remove(id);
                return true;
            }
        }
        return false;
	}
	
	public void disconnect() {
		this.logged = false;
		World.database.getAccountData().update(this);
		resetAllChars(true);

		if(this.curPlayer != null) {
			if(this.curPlayer.getFight() != null) {
				if(this.curPlayer.getFight().playerDisconnect(this.curPlayer, false)) {
					this.curPlayer.save();
					return;
				}
			}
		}
		
		if(this.gameClient != null && this.gameClient.getSession() != null && this.gameClient.getSession().isClosing())
			this.gameClient.getSession().close(true);
		
		this.curPlayer = null;
		this.gameClient = null;
	}

	public void resetAllChars(boolean save) {
		for(Player player: this.getPlayers().values()) {
			if(player.getCurExchange() != null)
				player.getCurExchange().cancel();
			if(player.getGroup() != null)
				player.getGroup().leave(player);
			
			if(player.getFight() != null) {//FIXME: Me pr�venir si c'est toujours l� (date : 23/06/2014) Locos.
				player.setOnline(false);
				if(player.getFight().playerDisconnect(player, false)) {
					player.save();
					continue;
				}
			}
				
			player.getCell().removeCreature(player);

			if(player.getMap() != null && player.isOnline())
				SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(player.getMap(), player.getId());
		
			player.setOnline(false);
			if(save)
				player.save();
			player.resetVars();
			World.data.unloadPerso(player.getId());
		}
	}

    /**
     * Envoie un message au player utilise par le compte
     * @param message message a faire parvenir
     */
    public void send(String message) {
        if(gameClient != null)
            gameClient.send(message);
    }
}
