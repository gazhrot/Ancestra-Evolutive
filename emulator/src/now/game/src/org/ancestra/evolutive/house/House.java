package org.ancestra.evolutive.house;

import java.util.Map;

import java.util.TreeMap;
import java.util.Map.Entry;

import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.enums.HouseRight;
import org.ancestra.evolutive.guild.Guild;

@SuppressWarnings("deprecation")
public class House {
	
	private int id;
	private int owner;
	private String key;
	private int access;
	private int sale;
	private short mapid;
	private int cellid;
	private int toMapid;
	private int toCellid;
	private int guildId;
	private int guildRights;
	private Map<Integer, Boolean> haveRight = new TreeMap<>();

	public House(int id, short mapid, int cellid, int owner, int sale, int guildId, int access, String key, int guildRights, int toMapid, int toCellid) {
		this.id = id;
		this.mapid = mapid;
		this.cellid = cellid;
		this.owner = owner;
		this.sale = sale;
		this.access = access;
		this.key = key;
		this.toMapid = toMapid;
		this.toCellid = toCellid;
		this.guildId = guildId;
		this.guildRights = guildRights;
		this.parseIntToRight(guildRights);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getAccess() {
		return access;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	public int getSale() {
		return sale;
	}

	public void setSale(int sale) {
		this.sale = sale;
	}

	public short getMapid() {
		return mapid;
	}

	public void setMapid(short mapid) {
		this.mapid = mapid;
	}

	public int getCellid() {
		return cellid;
	}

	public void setCellid(int cellid) {
		this.cellid = cellid;
	}

	public int getToMapid() {
		return toMapid;
	}

	public void setToMapid(int toMapid) {
		this.toMapid = toMapid;
	}

	public int getToCellid() {
		return toCellid;
	}

	public void setToCellid(int toCellid) {
		this.toCellid = toCellid;
	}

	public int getGuildId() {
		return guildId;
	}

	public void setGuildId(int guildId) {
		this.guildId = guildId;
	}

	public int getGuildRights() {
		return guildRights;
	}

	public void setGuildRights(int guildRights) {
		this.guildRights = guildRights;
	}

	public Map<Integer, Boolean> getHaveRight() {
		return haveRight;
	}

	public static House getHouseByCoord(int map, int cell) {
		for(House house : World.data.getHouses().values())
			if(house.getMapid() == map && house.getCellid() == cell)
				return house;
		return World.database.getHouseData().load(map, cell);
	}
	
	public static void load(Player player, int newMapID) {
		for(Entry<Integer, House> house : World.data.getHouses().entrySet()) {
			if(house.getValue().getMapid() == newMapID)	{
				StringBuilder packet = new StringBuilder().append("P").append(house.getValue().getId()).append("|");
				
				if(house.getValue().getOwner() > 0)	{
					Account account = World.data.getCompte(house.getValue().getOwner());
					if(account == null)
						packet.append("undefined;");
					else
						packet.append(World.data.getCompte(house.getValue().getOwner()).getPseudo()).append(";");
				} else {
					packet.append(";");
				}
				
				if(house.getValue().getSale() > 0)//Si prix > 0
					packet.append("1");//Achetable
				else
					packet.append("0");//Non achetable
				
				if(house.getValue().getGuildId() > 0) {
					Guild guild = World.data.getGuild(house.getValue().getGuildId());
					if(guild != null)
					{
						String name = guild.getName();
						String emblem = guild.getEmblem();
						if(guild.getMembers().size() < 10) {//Ce n'est plus une maison de guilde
							World.database.getHouseData().update(house.getValue(), 0, 0) ;
						} else {
							//Affiche le blason pour les membre de guilde OU Affiche le blason pour les non membre de guilde
							if(player.getGuild() != null && player.getGuild().getId() == house.getValue().getGuildId() && house.getValue().canDo(HouseRight.G_SHOW_BLASON.getId()))//meme guilde
								packet.append(";").append(name).append(";").append(emblem);
							else
							if(house.getValue().canDo(HouseRight.O_SHOW_BLASON.getId()))//Pas de guilde/guilde-différente
								packet.append(";").append(name).append(";").append(emblem);
						}
					}
				}
				SocketManager.GAME_SEND_hOUSE(player, packet.toString());

				if(house.getValue().getOwner() == player.getAccount().getUUID()) {
					StringBuilder packet1 = new StringBuilder();
					packet1.append("L+|").append(house.getValue().getId()).append(";").append(house.getValue().getAccess()).append(";");
					
					if(house.getValue().getSale() <= 0)
						packet1.append("0;").append(house.getValue().getSale());
					else 
					if(house.getValue().getSale() > 0)
						packet1.append("1;").append(house.getValue().getSale());

					SocketManager.GAME_SEND_hOUSE(player, packet1.toString());
				}
			}
		}
	}

	public void open(Player player) {
		if(player.getFight() != null || player.getIsTalkingWith() != 0 || player.getIsTradingWith() != 0 || player.getCurJobAction() != null || player.getCurExchange() != null)
			return;

		House house = player.getCurHouse();
		
		if(house == null) 
			return;
		if(house.getOwner() == player.getAccount().getUUID() || (player.getGuild() != null && player.getGuild().getId() == house.getGuildId() && canDo(HouseRight.G_NO_CODE.getId())))//C'est sa maison ou même guilde + droits entrer sans pass
			House.open(player, "-", true);
		else 
		if(house.getOwner() > 0) //Une personne autre la acheter, il faut le code pour rentrer
			SocketManager.GAME_SEND_KODE(player, "CK0|8");//8 étant le nombre de chiffre du code
		else 
		if(house.getOwner() == 0) //Maison non acheter, mais achetable, on peut rentrer sans code
			House.open(player, "-", false);
		else
			return;
	}
	
	public static void open(Player player, String packet, boolean isHome) {
		House house = player.getCurHouse();
		
		if((!house.canDo(HouseRight.O_CANT_OPEN.getId()) && (packet.compareTo(house.getKey()) == 0)) || isHome) {
			player.setPosition((short) house.getToMapid(), house.getToCellid());
			closeCode(player);
		} else 
		if((packet.compareTo(house.getKey()) != 0) || house.canDo(HouseRight.O_CANT_OPEN.getId())) {
			SocketManager.GAME_SEND_KODE(player, "KE");
			SocketManager.GAME_SEND_KODE(player, "V");
		}
	}
	
	public void buyIt(Player player) {
		House h = player.getCurHouse();
		String str = "CK"+h.getId()+"|"+h.getSale();//ID + Prix
		SocketManager.GAME_SEND_hOUSE(player, str);
	}

	public static void buy(Player player) {
		House house = player.getCurHouse();

		if(AlreadyHaveHouse(player)) {
			SocketManager.GAME_SEND_Im_PACKET(player, "132;1");
			return;
		}
		
		if(player.getKamas() < house.getSale()) 
			return;

		player.setKamas(player.getKamas() - house.getSale());
		
		int tKamas = 0;
		
		for(Trunk trunk: Trunk.getTrunksByHouse(house))	{
			if(house.getOwner() > 0)
				trunk.moveTrunkToBank(World.data.getCompte(house.getOwner()));//Déplacement des items vers la banque
			
			tKamas += trunk.getKamas();
			trunk.setKamas(0);//Retrait kamas
			trunk.setKey("-");//ResetPass
			trunk.setOwner(0);//ResetOwner
			World.database.getTrunkData().update(trunk);
		}
		
		//Ajoute des kamas dans la banque du vendeur
		if(house.getOwner() > 0) {
			Account account = World.data.getCompte(house.getOwner());
			account.setBankKamas(account.getBankKamas() + house.getSale() + tKamas);
			//Petit message pour le prévenir si il est on?
			if(account.getCurPlayer() != null) {
				SocketManager.GAME_SEND_MESSAGE(account.getCurPlayer(), "Une maison vous appartenant à été vendue "+house.getSale()+" kamas.", Server.config.getMotdColor());
				account.getCurPlayer().save();
			}
			World.database.getAccountData().update(account);
		}
		
		//On save l'acheteur
		player.save();
		SocketManager.GAME_SEND_STATS_PACKET(player);
		closeBuy(player);

		//Achat de la maison
		World.database.getHouseData().update(player, house);

		//Rafraichir la map après l'achat
		for(Player z: player.getMap().getPlayers())
			House.load(z, z.getMap().getId());
	}
	
	public void sellIt(Player player) {
		House house = player.getCurHouse();
		
		if(isHouse(player, house)) {
			String str = "CK"+house.getId()+"|"+house.getSale();//ID + Prix
			SocketManager.GAME_SEND_hOUSE(player, str);
			return;
		} else {
			return;
		}
	}
	
	public static void sell(Player player, String packet) {
		House house = player.getCurHouse();
		int price = Integer.parseInt(packet);	
		if(house.isHouse(player, house)) {
			SocketManager.GAME_SEND_hOUSE(player, "V");
			SocketManager.GAME_SEND_hOUSE(player, "SK"+house.getId()+"|"+price);
				
			//Vente de la maison
			World.database.getHouseData().update(house, price);

			//Rafraichir la map après la mise en vente
			for(Player z:player.getMap().getPlayers())
				House.load(z, z.getMap().getId());				
			return;
		} else {
			return;
		}
	}

	public boolean isHouse(Player player, House house) {//Savoir si c'est sa maison
		if(house.getOwner() == player.getAccount().getUUID()) 
			return true;
		else
			return false;
	}
	
	public static void closeCode(Player player) {
		SocketManager.GAME_SEND_KODE(player, "V");
	}
	
	public static void closeBuy(Player player) {
		SocketManager.GAME_SEND_hOUSE(player, "V");
	}
	
	public void lock(Player player) {
		SocketManager.GAME_SEND_KODE(player, "CK1|8");
	}
	
	public static void lock(Player player, String packet) {
		House house = player.getCurHouse();
		
		if(house.isHouse(player, house)) {
			World.database.getHouseData().update(player, house, packet);//Change le code
			closeCode(player);
			return;
		} else {
			closeCode(player);
			return;
		}
	}
	
	public static boolean AlreadyHaveHouse(Player player) {
		for(House house : World.data.getHouses().values())
			if(house.getOwner() == player.getAccount().getUUID())
				return true;
		return (World.database.getHouseData().load(player) != null ? true : false);
	}
		
	public static void leave(Player player, String packet) {
		House house = player.getCurHouse();
		
		if(!house.isHouse(player, house)) 
			return;
		
		Player target = World.data.getPlayer(Integer.parseInt(packet));
		
		if(target == null || !target.isOnline() || target.getFight() != null || target.getMap().getId() != player.getMap().getId()) 
			return;
		
		target.setPosition(house.getMapid(), house.getCellid());
		SocketManager.GAME_SEND_Im_PACKET(target, "018;"+player.getName());
	}
	
	public static House getHouseByPlayer(Player player) {
		for(House house : World.data.getHouses().values())
			if(house.getOwner() == player.getAccount().getUUID())
				return house;
		return World.database.getHouseData().load(player);
	}
	
	public static byte onGuild(Guild guild) {
		byte i = 0;
		World.database.getHouseData().load(guild);
		for(House house : World.data.getHouses().values())
			if(house.getGuildId() == guild.getId())
				i++;
		return i;
	}
	
	public static void removeHouseGuild(Guild guild) {
		World.database.getHouseData().load(guild);
		for(House house : World.data.getHouses().values()) {
			if(house.getGuildId() == guild.getId()) {
				house.setGuildRights(0);
				house.setGuildId(0);
				World.database.getHouseData().update(house);//Supprime les maisons de guilde
			}
		}	
	}
	
	public boolean canDo(int rightValue) {	
		return haveRight.get(rightValue);
	}
		
	public void initRight() {
		for(HouseRight right : HouseRight.values()) {
			haveRight.put(right.getId(), false);
		}
	}
	
	public void parseIntToRight(int total) {
		if(haveRight.isEmpty())
			this.initRight();

		if(total == 1)
			return;

		if(haveRight.size() > 0)
			haveRight.clear();

		this.initRight();
		
		Integer[] mapKey = haveRight.keySet().toArray(new Integer[haveRight.size()]);	//Récupère les clef de map dans un tableau d'Integer

		while(total > 0) {
			for(int i = haveRight.size()-1; i < haveRight.size(); i--) {
				if(mapKey[i].intValue() <= total) {
					total ^= mapKey[i].intValue();
					haveRight.put(mapKey[i], true);
					break;
				}
			}
		}
	}
	
	public static void parseHG(Player player, String packet) {
		House house = player.getCurHouse();
		
		if(player.getGuild() == null) 
			return;
		
		if(packet != null) {
			if(packet.charAt(0) == '+')	{
				byte HouseMaxOnGuild = (byte) Math.floor(player.getGuild().getLevel()/10);
				
				if(House.onGuild(player.getGuild()) >= HouseMaxOnGuild)
					return;
				if(player.getGuild().getMembers().size() < 10)
					return;
				
				World.database.getHouseData().update(house, player.getGuild().getId(), 0);
				parseHG(player, null);
			} else 
			if(packet.charAt(0) == '-')	{
				World.database.getHouseData().update(house, 0, 0);
				parseHG(player, null);
			} else {
				World.database.getHouseData().update(house, house.getGuildId(), Integer.parseInt(packet));
				house.parseIntToRight(Integer.parseInt(packet));
			}
		}else 
		if(packet == null) {
			if(house.getGuildId() <= 0)
				SocketManager.GAME_SEND_hOUSE(player, "G"+house.getId());
			else if(house.getGuildId() > 0)
				SocketManager.GAME_SEND_hOUSE(player, "G"+house.getId()+";"+player.getGuild().getName()+";"+player.getGuild().getEmblem()+";"+house.getGuildRights());
		}
	}
	
	public static String parseHouseToGuild(Player player) {
		boolean isFirst = true;
		StringBuilder packet = new StringBuilder();
		
		for(Entry<Integer, House> house : World.data.getHouses().entrySet()) {
			if(house.getValue().getGuildId() == player.getGuild().getId() && house.getValue().getGuildRights() > 0) {
				if(isFirst) packet.append("+");
				if(!isFirst) packet.append("|");
				
				packet.append(house.getKey()).append(";");
				packet.append(World.data.getPlayer(house.getValue().getOwner()).getAccount().getPseudo()).append(";");
				packet.append(World.data.getMap((short)house.getValue().getToMapid()).getX()).append(",").append(World.data.getMap((short)house.getValue().getToMapid()).getY()).append(";");
				packet.append("0;");//TODO : Compétences ...
				packet.append(house.getValue().getGuildRights());	
				isFirst = false;
			}
		}
		return packet.toString();
	}
}