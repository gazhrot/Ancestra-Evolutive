package org.ancestra.evolutive.house;

import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.guild.Guild;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class House
{
	private int _id;
	private short _map_id;
	private int _cell_id;
	private int _owner_id;
	private int _sale;
	private int _guild_id;
	private int _guildRights;
	private int _access;
	private String _key;
	private int _mapid;
	private int _caseid;
	
	//Droits de chaques maisons
	private Map<Integer,Boolean> haveRight = new TreeMap<Integer,Boolean>();

	
	public House(int id, short map_id, int cell_id, int owner_id, int sale,
			int guild_id, int access, String key, int guildrights, int mapid, int caseid) 
	{
		_id = id;
		_map_id = map_id;
		_cell_id = cell_id;
		_owner_id = owner_id;
		_sale = sale;
		_guild_id = guild_id;
		_access = access;
		_key = key;
		_guildRights = guildrights;
		parseIntToRight(guildrights);
		_mapid = mapid;
		_caseid = caseid;
	}
	
	public int get_id()
	{
		return _id;
	}
	
	public short get_map_id()
	{
		return _map_id;
	}
	
	public int get_cell_id()
	{
		return _cell_id;
	}
	
	public int get_owner_id()
	{
		return _owner_id;
	}
	
	public void set_owner_id(int id)
	{
		_owner_id = id;
	}
	
	public int get_sale()
	{
		return _sale;
	}
	
	public void set_sale(int price)
	{
		_sale = price;
	}
	
	public int get_guild_id()
	{
		return _guild_id;
	}
	
	public void set_guild_id(int GuildID)
	{
		_guild_id = GuildID;
	}
	
	public int get_guild_rights()
	{
		return _guildRights;
	}
	
	public void set_guild_rights(int GuildRights)
	{
		_guildRights = GuildRights;
	}
	
	public int get_access()
	{
		return _access;
	}
	
	public void set_access(int access)
	{
		_access = access;
	}
	
	public String get_key()
	{
		return _key;
	}
	
	public void set_key(String key)
	{
		_key = key;
	}
	
	public int get_mapid()
	{
		return _mapid;
	}
	
	public int get_caseid()
	{
		return _caseid;
	}
	
	public static House get_house_id_by_coord(int map_id, int cell_id)
	{
		for(Entry<Integer, House> house : World.data.getHouses().entrySet())
		{
			if(house.getValue().get_map_id() == map_id && house.getValue().get_cell_id() == cell_id)
			{
				return house.getValue();
			}
		}
		return null;
	}
	
	public static void LoadHouse(Player P, int newMapID)//Affichage des maison + Blason
	{
		
		for(Entry<Integer, House> house : World.data.getHouses().entrySet())
		{
			if(house.getValue().get_map_id() == newMapID)
			{
				StringBuilder packet = new StringBuilder();
				packet.append("P").append(house.getValue().get_id()).append("|");
				if(house.getValue().get_owner_id() > 0)
				{
					Account C = World.data.getCompte(house.getValue().get_owner_id());
					if(C == null)//Ne devrait pas arriver
					{
						packet.append("undefined;");
					}else
					{
						packet.append(World.data.getCompte(house.getValue().get_owner_id()).getPseudo()).append(";");
					}
				}else
				{
					packet.append(";");
				}
				if(house.getValue().get_sale() > 0)//Si prix > 0
				{
					packet.append("1");//Achetable
				}else
				{
					packet.append("0");//Non achetable
				}
				if(house.getValue().get_guild_id() > 0) //Maison de guilde
				{
					Guild G = World.data.getGuild(house.getValue().get_guild_id());
					if(G != null)
					{
						String Gname = G.getName();
						String Gemblem = G.getEmblem();
						if(G.getMembers().size() < 10)//Ce n'est plus une maison de guilde
						{
							World.database.getHouseData().update(house.getValue(), 0, 0) ;
						}else
						{
							//Affiche le blason pour les membre de guilde OU Affiche le blason pour les non membre de guilde
							if(P.getGuild() != null && P.getGuild().getId() == house.getValue().get_guild_id() && house.getValue().canDo(Constants.H_GBLASON))//meme guilde
							{
								packet.append(";").append(Gname).append(";").append(Gemblem);
							}
							else if(house.getValue().canDo(Constants.H_OBLASON))//Pas de guilde/guilde-diff�rente
							{
								packet.append(";").append(Gname).append(";").append(Gemblem);
							}
						}
					}
				}
				SocketManager.GAME_SEND_hOUSE(P, packet.toString());

				if(house.getValue().get_owner_id() == P.getAccount().getUUID())
				{
					StringBuilder packet1 = new StringBuilder();
					packet1.append("L+|").append(house.getValue().get_id()).append(";").append(house.getValue().get_access()).append(";");
					
					if(house.getValue().get_sale() <= 0)
					{
						packet1.append("0;").append(house.getValue().get_sale());
					}
					else if(house.getValue().get_sale() > 0)
					{
						packet1.append("1;").append(house.getValue().get_sale());
					}
					SocketManager.GAME_SEND_hOUSE(P, packet1.toString());
				}
			}
		}
	}

	public void HopIn(Player P)//Entrer dans la maison
	{
		// En gros si il fait quelque chose :)
		if(P.getFight() != null ||
		   P.getIsTalkingWith() != 0 ||
		   P.getIsTradingWith() != 0 ||
		   P.getCurJobAction() != null ||
		   P.getCurExchange() != null)
		{
			return;
		}
		
		House h = P.getCurHouse();
		if(h == null) return;
		if(h.get_owner_id() == P.getAccount().getUUID() || (P.getGuild() != null && P.getGuild().getId() == h.get_guild_id() && canDo(Constants.H_GNOCODE)))//C'est sa maison ou m�me guilde + droits entrer sans pass
		{
			OpenHouse(P, "-", true);
		}
		else if(h.get_owner_id() > 0) //Une personne autre la acheter, il faut le code pour rentrer
		{
			SocketManager.GAME_SEND_KODE(P, "CK0|8");//8 �tant le nombre de chiffre du code
		}
		else if(h.get_owner_id() == 0) //Maison non acheter, mais achetable, on peut rentrer sans code
		{
			OpenHouse(P, "-", false);
		}else
		{
			return;
		}
	}
	
	public static void OpenHouse(Player P, String packet, boolean isHome)//Ouvrir une maison ;o
	{
		
		House h = P.getCurHouse();
		if((!h.canDo(Constants.H_OCANTOPEN) && (packet.compareTo(h.get_key()) == 0)) || isHome)//Si c'est chez lui ou que le mot de passe est bon
		{
			P.teleport((short)h.get_mapid(), h.get_caseid());
			closeCode(P);
		}else if((packet.compareTo(h.get_key()) != 0) || h.canDo(Constants.H_OCANTOPEN))//Mauvais code
		{
			SocketManager.GAME_SEND_KODE(P, "KE");
			SocketManager.GAME_SEND_KODE(P, "V");
		}
	}
	
	public void BuyIt(Player P)//Acheter une maison
	{
		House h = P.getCurHouse();
		String str = "CK"+h.get_id()+"|"+h.get_sale();//ID + Prix
		SocketManager.GAME_SEND_hOUSE(P, str);
	}

	public static void HouseAchat(Player P)//Acheter une maison
	{
		House h = P.getCurHouse();

		if(AlreadyHaveHouse(P))
		{
			SocketManager.GAME_SEND_Im_PACKET(P, "132;1");
			return;
		}
		//On enleve les kamas
		if(P.getKamas() < h.get_sale()) return;
		long newkamas = P.getKamas()-h.get_sale();
		P.setKamas(newkamas);
		
		int tKamas = 0;
		for(Trunk t : Trunk.getTrunksByHouse(h))
		{
			if(h.get_owner_id() > 0)
			{
				t.moveTrunktoBank(World.data.getCompte(h.get_owner_id()));//D�placement des items vers la banque
			}
			tKamas += t.get_kamas();
			t.set_kamas(0);//Retrait kamas
			t.set_key("-");//ResetPass
			t.set_owner_id(0);//ResetOwner
			World.database.getTrunkData().update(t);
		}
		
		//Ajoute des kamas dans la banque du vendeur
		if(h.get_owner_id() > 0)
		{
			Account Seller = World.data.getCompte(h.get_owner_id());
			long newbankkamas = Seller.getBankKamas()+h.get_sale()+tKamas;
			Seller.setBankKamas(newbankkamas);
			//Petit message pour le pr�venir si il est on?
			if(Seller.getCurPlayer() != null)
			{
				SocketManager.GAME_SEND_MESSAGE(Seller.getCurPlayer(), "Une maison vous appartenant � �t� vendue "+h.get_sale()+" kamas.", Server.config.getMotdColor());
				Seller.getCurPlayer().save();
			}
			World.database.getAccountData().update(Seller);
		}
		
		//On save l'acheteur
		P.save();
		SocketManager.GAME_SEND_STATS_PACKET(P);
		closeBuy(P);

		//Achat de la maison
		World.database.getHouseData().update(P, h);

		//Rafraichir la map apr�s l'achat
		for(Player z:P.getMap().getPlayers())
		{
			LoadHouse(z, z.getMap().getId());
		}
	}
	
	public void SellIt(Player P)//Vendre une maison
	{
		House h = P.getCurHouse();
		if(isHouse(P, h))
		{
			String str = "CK"+h.get_id()+"|"+h.get_sale();//ID + Prix
			SocketManager.GAME_SEND_hOUSE(P, str);
				return;
		}else
		{
			return;
		}
	}
	
	public static void SellPrice(Player P, String packet)//Vendre une maison
	{
		House h = P.getCurHouse();
		int price = Integer.parseInt(packet);	
		if(h.isHouse(P, h))
		{
			SocketManager.GAME_SEND_hOUSE(P, "V");
			SocketManager.GAME_SEND_hOUSE(P, "SK"+h.get_id()+"|"+price);
				
			//Vente de la maison
			World.database.getHouseData().update(h, price);

			//Rafraichir la map apr�s la mise en vente
			for(Player z:P.getMap().getPlayers())
			{
				LoadHouse(z, z.getMap().getId());
			}
				
			return;
		}else
		{
			return;
		}
	}

	public boolean isHouse(Player P, House h)//Savoir si c'est sa maison
	{
		if(h.get_owner_id() == P.getAccount().getUUID()) return true;
		else return false;
	}
	
	public static void closeCode(Player P)
	{
		SocketManager.GAME_SEND_KODE(P, "V");
	}
	
	public static void closeBuy(Player P)
	{
		SocketManager.GAME_SEND_hOUSE(P, "V");
	}
	
	public void Lock(Player P) 
	{
		SocketManager.GAME_SEND_KODE(P, "CK1|8");
	}
	
	public static void LockHouse(Player P, String packet) 
	{
		House h = P.getCurHouse();
		if(h.isHouse(P, h))
		{
			World.database.getHouseData().update(P, h, packet);//Change le code
			closeCode(P);
			return;
		}else
		{
			closeCode(P);
			return;
		}
	}
	
	public static String parseHouseToGuild(Player P)
	{
		boolean isFirst = true;
		StringBuilder packet = new StringBuilder();
		for(Entry<Integer, House> house : World.data.getHouses().entrySet())
		{
			if(house.getValue().get_guild_id() == P.getGuild().getId() && house.getValue().get_guild_rights() > 0)
			{
				if(isFirst) packet.append("+");
				if(!isFirst) packet.append("|");
				
				packet.append(house.getKey()).append(";");
				packet.append(World.data.getPersonnage(house.getValue().get_owner_id()).getAccount().getPseudo()).append(";");
				packet.append(World.data.getCarte((short)house.getValue().get_mapid()).getX()).append(",").append(World.data.getCarte((short)house.getValue().get_mapid()).getY()).append(";");
				packet.append("0;");//TODO : Comp�tences ...
				packet.append(house.getValue().get_guild_rights());	
				isFirst = false;
			}
		}
			return packet.toString();
	}
	
	public static boolean AlreadyHaveHouse(Player P)
	{
		for(Entry<Integer, House> house : World.data.getHouses().entrySet())
		{
			if(house.getValue().get_owner_id() == P.getAccount().getUUID())
			{
				return true;
			}
		}
		return false;
	}
	
	public static void parseHG(Player P, String packet)
	{
		House h = P.getCurHouse();
		
		if(P.getGuild() == null) return;
		
		if(packet != null)
		{
			if(packet.charAt(0) == '+')
			{
				//Ajoute en guilde
				byte HouseMaxOnGuild = (byte) Math.floor(P.getGuild().getLevel()/10);
				if(HouseOnGuild(P.getGuild().getId()) >= HouseMaxOnGuild) return;
				if(P.getGuild().getMembers().size() < 10) return;
				World.database.getHouseData().update(h, P.getGuild().getId(), 0);
				parseHG(P, null);
			}
			else if(packet.charAt(0) == '-')
			{
				//Retire de la guilde
				World.database.getHouseData().update(h, 0, 0);
				parseHG(P, null);
			}
			else
			{
				World.database.getHouseData().update(h, h.get_guild_id(), Integer.parseInt(packet));
				h.parseIntToRight(Integer.parseInt(packet));
			}
		}
		else if(packet == null)
		{
		if(h.get_guild_id() <= 0)
		{
			SocketManager.GAME_SEND_hOUSE(P, "G"+h.get_id());
		}else if(h.get_guild_id() > 0)
		{
			SocketManager.GAME_SEND_hOUSE(P, "G"+h.get_id()+";"+P.getGuild().getName()+";"+P.getGuild().getEmblem()+";"+h.get_guild_rights());
		}
		}
	}
	
	public static byte HouseOnGuild(int GuildID) 
	{
		byte i = 0;
		for(Entry<Integer, House> house : World.data.getHouses().entrySet())
		{
			if(house.getValue().get_guild_id() == GuildID)
			{
				i++;
			}
		}
		return i;
	}

	public boolean canDo(int rightValue)
	{	
		return haveRight.get(rightValue);
	}
	
	public void initRight()
	{
		haveRight.put(Constants.H_GBLASON, false);
		haveRight.put(Constants.H_OBLASON,false);
		haveRight.put(Constants.H_GNOCODE,false);
		haveRight.put(Constants.H_OCANTOPEN,false);
		haveRight.put(Constants.C_GNOCODE,false);
		haveRight.put(Constants.C_OCANTOPEN,false);
		haveRight.put(Constants.H_GREPOS,false);
		haveRight.put(Constants.H_GTELE,false);
	}
	
	public void parseIntToRight(int total)
	{
		if(haveRight.isEmpty())
		{
			initRight();
		}
		if(total == 1)
			return;

		if(haveRight.size() > 0)	//Si les droits contiennent quelque chose -> Vidage (M�me si le TreeMap supprimerais les entr�es doublon lors de l'ajout)
			haveRight.clear();

		initRight();	//Remplissage des droits

		Integer[] mapKey = haveRight.keySet().toArray(new Integer[haveRight.size()]);	//R�cup�re les clef de map dans un tableau d'Integer

		while(total > 0)
		{
			for (int i = haveRight.size()-1; i < haveRight.size(); i--)
			{
				if(mapKey[i].intValue() <= total)
				{
					total ^= mapKey[i].intValue();
					haveRight.put(mapKey[i],true);
					break;
				}
			}
		}
	}
	
	public static void Leave(Player P, String packet)
	{
		House h = P.getCurHouse();
		if(!h.isHouse(P, h)) return;
		int Pguid = Integer.parseInt(packet);
		Player Target = World.data.getPersonnage(Pguid);
		if(Target == null || !Target.isOnline() || Target.getFight() != null || Target.getMap().getId() != P.getMap().getId()) return;
		Target.teleport(h.get_map_id(), h.get_cell_id());
		SocketManager.GAME_SEND_Im_PACKET(Target, "018;"+P.getName());
	}
	
	
	public static House get_HouseByPerso(Player P)//Connaitre la MAPID + CELLID de sa maison
	{
		for(Entry<Integer, House> house : World.data.getHouses().entrySet())
		{
			if(house.getValue().get_owner_id() == P.getAccount().getUUID())
			{
				return house.getValue();
			}
		}
		return null;
	}
	
	public static void removeHouseGuild(int GuildID)
	{
		for(Entry<Integer, House> h : World.data.getHouses().entrySet())
		{
			if(h.getValue().get_guild_id() == GuildID)
			{
				h.getValue().set_guild_rights(0);
				h.getValue().set_guild_id(0);
				World.database.getHouseData().update(h.getValue());//Supprime les maisons de guilde
			}
		}
		
	}
}