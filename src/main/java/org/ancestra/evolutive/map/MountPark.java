package org.ancestra.evolutive.map;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.Mount;
import org.ancestra.evolutive.guild.Guild;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MountPark {
	
	private int owner;
	private int cellid = -1;
	private int price;
	private int size;
	private Guild guild;
	private Maps map;
	private InteractiveObject door;
	private ArrayList<Case> cases = new ArrayList<>();
	private Map<Integer,Integer> datas = new TreeMap<Integer,Integer>();//DragoID, IDperso
	
	public MountPark(int owner, Maps map, int cellid, int size, String datas, int guild, int price) {
		this.owner = owner;
		this.cellid = cellid;
		this.price = price;
		this.size = size;
		this.guild = World.data.getGuild(guild);
		this.map = map;
		this.door = map.getMountParkDoor();
	
		if(this.map != null)
			this.map.setMountPark(this);
		
		for(String data: datas.split("\\;")) {
			try	{
				String[] split = data.split(",");
				Mount dd = World.data.getDragoByID(Integer.parseInt(split[1]));
				if(dd == null) 
					continue;
				this.datas.put(Integer.parseInt(split[1]), Integer.parseInt(split[0]));
			} catch(Exception e) {}
		}
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public int getCellid() {
		return cellid;
	}

	public void setCellid(int cellid) {
		this.cellid = cellid;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Guild getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		this.guild = guild;
	}

	public Maps getMap() {
		return map;
	}

	public void setMap(Maps map) {
		this.map = map;
	}

	public InteractiveObject getDoor() {
		return door;
	}

	public void setDoor(InteractiveObject door) {
		this.door = door;
	}
	
	public Map<Integer, Integer> getDatas() {
		return this.datas;
	}

	public String parseData(int id, boolean isPublic) {
		if(this.datas.isEmpty())
			return "~";
		
		StringBuilder packet = new StringBuilder();
		
		for(Entry<Integer, Integer> data: this.datas.entrySet()) {
			if(packet.length() > 0)
				packet.append(";");
			if(data.getValue() == id && isPublic)
				packet.append(World.data.getDragoByID(data.getKey()).parse());
			else
				packet.append(World.data.getDragoByID(data.getKey()).parse());
		}
		return packet.toString();
	}
	
	public String parseDataToDb() {
		if(this.datas.isEmpty())
			return "";
		
		StringBuilder str = new StringBuilder();
		
		for(Entry<Integer, Integer> data: this.datas.entrySet()) {
			if(str.length() > 0)
				str.append(";");
			str.append(data.getValue()).append(",").append(World.data.getDragoByID(data.getKey()).getId());
		}
		return str.toString();
	}

	public int getObjectNumb() {
		int i = 0;
		for(Case cell: this.cases)
			if(cell.getObject() != null)
				i++;
		return i;
	}
	
	public static void remove(int GuildID) {
		for(Entry<Short, MountPark> mp : World.data.getMountPark().entrySet())//Pour chaque enclo si ils en ont plusieurs
		{
			if(mp.getValue().getGuild().getId() == GuildID)
			{
				if(!mp.getValue().getDatas().isEmpty())
				{
					for(Entry<Integer, Integer> MPdata : mp.getValue().getDatas().entrySet())
					{
						Mount d = World.data.getDragoByID(MPdata.getKey());
						World.data.removeDragodinde(MPdata.getKey());//Suppression des dindes dans le world
						World.database.getMountData().delete(d);
					}
				}
				mp.getValue().getDatas().clear();
				mp.getValue().setOwner(0);
				mp.getValue().setGuild(null);
				mp.getValue().setPrice(3000000);
				World.database.getMountparkData().update(mp.getValue());
				for(Player p : mp.getValue().getMap().getPlayers())
					SocketManager.GAME_SEND_Rp_PACKET(p, mp.getValue());
			}
		}
	}
}