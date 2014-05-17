package org.ancestra.evolutive.guild;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;


import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.client.other.Stats;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.Collector;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.ancestra.evolutive.guild.Guild;
import org.ancestra.evolutive.house.House;

public class Guild {
	
	private int id;
	private String name = "";
	private String emblem = "";
	private int level;
	private long experience;
	private int capital = 0;
	private int nbrCollector = 0;
	private Map<Integer, SpellStats> spells = new TreeMap<>();	//<ID, Level>
	private Map<Integer, Integer> stats = new TreeMap<>();
	private Map<Integer, Integer> statsFight = new TreeMap<>();
	private Map<Integer, GuildMember> members = new TreeMap<Integer,GuildMember>();
	
	public Guild(String name, String emblem) {
		this.id = World.data.getNextHighestGuildID();
		this.name = name;
		this.emblem = emblem;
		this.level = 1;
		this.experience = 0;
		this.decompileSpells("462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0|"); 
		this.decompileStats("176;100|158;1000|124;100|"); 
	}
	
	public Guild(int id, String name, String emblem, int level, long experience, int capital, int nbrCollector, String spells, String stats)
	{
		this.id = id;
		this.name = name;
		this.emblem = emblem;
		this.level = level;
		this.experience = experience;
		this.capital = capital;
		this.nbrCollector = nbrCollector;
		this.decompileSpells(spells);
		this.decompileStats(stats);
		this.statsFight.clear();
		this.statsFight.put(Constants.STATS_ADD_FORC, this.getLevel());
		this.statsFight.put(Constants.STATS_ADD_SAGE, this.getStat(Constants.STATS_ADD_SAGE));
		this.statsFight.put(Constants.STATS_ADD_INTE, this.getLevel());
		this.statsFight.put(Constants.STATS_ADD_CHAN, this.getLevel());
		this.statsFight.put(Constants.STATS_ADD_AGIL, this.getLevel());
		this.statsFight.put(Constants.STATS_ADD_RP_NEU, (int) Math.floor(this.getLevel() / 2));
		this.statsFight.put(Constants.STATS_ADD_RP_FEU, (int) Math.floor(this.getLevel() / 2));
		this.statsFight.put(Constants.STATS_ADD_RP_EAU, (int) Math.floor(this.getLevel() / 2));
		this.statsFight.put(Constants.STATS_ADD_RP_AIR, (int) Math.floor(this.getLevel() / 2));
		this.statsFight.put(Constants.STATS_ADD_RP_TER, (int) Math.floor(this.getLevel() / 2));
		this.statsFight.put(Constants.STATS_ADD_AFLEE, (int) Math.floor(this.getLevel() / 2));
		this.statsFight.put(Constants.STATS_ADD_MFLEE, (int) Math.floor(this.getLevel() / 2));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmblem() {
		return emblem;
	}

	public void setEmblem(String emblem) {
		this.emblem = emblem;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getExperience() {
		return experience;
	}

	public void setExperience(long experience) {
		this.experience = experience;
	}

	public int getCapital() {
		return capital;
	}

	public void setCapital(int capital) {
		this.capital = capital;
	}

	public int getNbrCollector() {
		return nbrCollector;
	}

	public void setNbrCollector(int nbrCollector) {
		this.nbrCollector = nbrCollector;
	}

	public Map<Integer, SpellStats> getSpells() {
		return spells;
	}

	public Map<Integer, Integer> getStats() {
		return stats;
	}
	
	public ArrayList<Player> getMembers() {
		ArrayList<Player> players = new ArrayList<Player>();
		for(GuildMember GM: this.members.values())
			players.add(GM.getPlayer());
		return players;
	}
	
	public GuildMember getMember(int UUID) {
		return members.get(UUID);
	}

	public GuildMember addMember(int UUID, int rank, byte xpGive, long xpGave, int right) { 
		GuildMember GM = new GuildMember(this, UUID, rank, xpGave, xpGive, right);
		this.members.put(UUID, GM);
		return GM;
	}
	
	public GuildMember addNewMember(Player player) {
		GuildMember GM = new GuildMember(this, player.getUUID(), 0, 0, (byte) 0, 0);
		this.members.put(player.getUUID(), GM);
		return GM;
	}
	
	public void removeMember(Player player) {
		House house = House.get_HouseByPerso(player);
		if(house != null)
			if(House.HouseOnGuild(this.getId()) > 0)
				World.database.getHouseData().update(house, 0, 0);

		this.members.remove(player.getUUID());
		World.database.getGuildMemberData().delete(player.getGuildMember());
	}
	
	public void boostSpell(int id) {
		SpellStats SS = this.getSpells().get(id);
		if(SS != null && SS.getLevel() == 5)
			return;
		
		this.getSpells().put(id, ((SS == null) ? World.data.getSort(id).getStatsByLevel(1) : World.data.getSort(id).getStatsByLevel(SS.getLevel() + 1)));
	}
	
	public void decompileSpells(String spells) {//ID;lvl|ID;lvl|...
		int id, lvl;
		
		for(String split: spells.split("\\|")) {
			id = Integer.parseInt(split.split("\\;")[0]);
			lvl = Integer.parseInt(split.split("\\;")[1]);
			this.getSpells().put(id, World.data.getSort(id).getStatsByLevel(lvl));
		}
	}
	
	public String compileSpells() {
		if(this.getSpells().isEmpty())
			return "";
		
		StringBuilder toReturn = new StringBuilder();
		boolean isFirst = true;
		
		for(Entry<Integer, SpellStats> curSpell : this.getSpells().entrySet()) { 
			if(!isFirst)
				toReturn.append("|");
			
			toReturn.append(curSpell.getKey()).append(";").append(((curSpell.getValue() == null) ? 0 : curSpell.getValue().getLevel()));
			isFirst = false;
		}
		
		return toReturn.toString();
	}
	
	public void addStat(int id, int qte) {
		stats.put(id, stats.get(id) + qte);
	}
	
	public void upgradeStat(int id, int value) {
		int actual = stats.get(id).intValue();
		stats.put(id, (actual + value));
	}
	
	public int getStat(int id) {
		for(Entry<Integer, Integer> stat : stats.entrySet())
			if(stat.getKey() == id)
				return stat.getValue();
		return 0;
	}
	
	public String compileStats() {
		if(stats.isEmpty())
			return "";

		StringBuilder toReturn = new StringBuilder();
		boolean isFirst = true;
		
		for(Entry<Integer, Integer> curStats : stats.entrySet()) {
			if(!isFirst)
				toReturn.append("|");
			
			toReturn.append(curStats.getKey()).append(";").append(curStats.getValue());
			isFirst = false;
		}
		
		return toReturn.toString();
	}
	
	public void decompileStats(String stats) {//ID;lvl|ID;lvl|...
		int id, value;
		
		for(String split : stats.split("\\|")) {//pp pod sagesse
			id = Integer.parseInt(split.split(";")[0]);
			value = Integer.parseInt(split.split(";")[1]);
			this.stats.put(id, value);
		}
	}

	public Stats getStatsFight() {
		return new Stats(statsFight);
	}

	public void addXp(long experience) {
		this.experience += experience;
		
		while(this.experience >= World.data.getGuildXpMax(this.getLevel()) && this.getLevel() < 200)
			levelUp();
	}
	
	public void levelUp() {
		this.level++;
		this.setCapital(this.getCapital() + 5);
	}

	public String parseCollector() {
		//Percomax|NbPerco|100*level|level|perco_add_pods|perco_prospection|perco_sagesse|perco_max|perco_boost|1000+10*level|perco_spells
		StringBuilder packet = new StringBuilder();
		
		packet.append(this.getNbrCollector()).append("|");
		packet.append(Collector.CountPercoGuild(this.getId())).append("|");
		packet.append(100 * this.getLevel()).append("|").append(this.getLevel()).append("|");
		packet.append(this.getStat(158)).append("|").append(this.getStat(176)).append("|");
		packet.append(this.getStat(124)).append("|").append(this.getNbrCollector()).append("|");
		packet.append(this.getCapital()).append("|").append((1000 + (10 * this.getLevel()))).append("|").append(this.compileSpells());
		return packet.toString();
	}
	
	public String parseMembersToGM() {
		StringBuilder str = new StringBuilder();
		for(GuildMember GM : this.members.values()) {
			String online = "0";
			
			if(GM.getPlayer() != null)
				if(GM.getPlayer().isOnline())
					online = "1";
			
			if(str.length() != 0)
				str.append("|");
			
			str.append(GM.getUUID()).append(";");
			str.append(GM.getPlayer().getName()).append(";");
			str.append(GM.getPlayer().getLevel()).append(";");
			str.append(GM.getPlayer().getGfx()).append(";");
			str.append(GM.getRank()).append(";");
			str.append(GM.getXpGave()).append(";");
			str.append(GM.getXpGive()).append(";");
			str.append(GM.getRight()).append(";");
			str.append(online).append(";");
			str.append(GM.getPlayer().getAlign()).append(";");
			str.append(GM.getLastConnection());
		}
		return str.toString();
	}
}
