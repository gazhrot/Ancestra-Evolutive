package org.ancestra.evolutive.map;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.monster.MobGroup;
import org.ancestra.evolutive.job.JobConstant;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InteractiveObject {
	
	private int id;
	private int state;
	private Maps map;
	private Case cell;
	private boolean interactive = true;
	private InteractiveObjectTemplate template;
	private boolean walkable;
	private Timer respawnTimer;

	public InteractiveObject(int id, final Maps map, final Case cell) {
		this.id = id;
		this.map = map;
		this.cell = cell;
		this.state = JobConstant.IOBJECT_STATE_FULL;
		int respawnTime = 10000;
		this.template = World.data.getInteractiveObjectTemplate(this.id);
		if(this.getTemplate() != null)
			respawnTime = this.getTemplate().getRespawnTime();
		
		if(this.getTemplate() == null) this.walkable = false;
		else this.walkable = this.getTemplate().isWalkable() && this.state == JobConstant.IOBJECT_STATE_FULL;
		
		respawnTimer = new Timer(respawnTime, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				respawnTimer.stop();
				state = Constants.IOBJECT_STATE_FULLING;
				interactive = true;
				SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(map, cell);
				state = Constants.IOBJECT_STATE_FULL;
			}
		});
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getState() {
		return this.state;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public Maps getMap() {
		return map;
	}

	public Case getCell() {
		return cell;
	}

	public boolean isInteractive() {
		return this.interactive;
	}
	
	public void setInteractive(boolean interactive) {
		this.interactive = interactive;
	}
	
	public void startTimer() {
		if(this.respawnTimer == null)
			return;
		this.state = Constants.IOBJECT_STATE_EMPTY2;
		this.respawnTimer.restart();
	}

	public int getUseDuration() {
		int duration = 1500;
		if(this.getTemplate() != null)
			duration = this.getTemplate().getDuration();
		return duration;
	}

	public int getUnknowValue()	{
		int unk = 4;
		if(this.getTemplate() != null)
			unk = this.getTemplate().getUnk();
		return unk;
	}

	public boolean isWalkable()	{
		return this.walkable;
	}
	
	public void setWalkable(boolean b) {
		this.walkable = b;
	}
		
	public void getActionIO(Player player, Case cell) {
		switch(this.getId()) {			
			case 542://Statue Phoenix.
				if(player.isGhosts())
					player.setAlive();
			break;
			
			case 684://Portillon donjon squelette.
				if(!player.hasItemTemplate(1570, 1)) {
					player.sendText("Vous ne possèdez pas la clef necéssaire.");
				} else {
					player.removeByTemplateID(1570, 1);
					SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 1570);
					player.setPosition((short) 2110, 118);
				}
			break;
			
			case 1330://Pierre de kwak
				int kwakere = 269;
				switch(player.getMap().getId()) {
				case 2072: 
					kwakere = 270; 
					break;
				case 2071: 
					kwakere = 269; 
					break;
				case 2067: 
					kwakere = 272; 
					break;
				case 2068: 
					kwakere = 271; 
					break;
				}	
				player.getMap().startFightVersusProtectors(player, new MobGroup(player.getMap().getNextObject(),player.getMap(), cell, kwakere+","+40+","+40));
			break;
			
			case 1679:
				player.warpToSavePos();
			break;
			
			/*case 1748://Donjon Larve
				if(player.getMap().getId() == 6692)
				{
					if(player.getMap().requiredCell.size() == 2)
						player.getMap().openDoor();
					player.getMap().requiredCell.clear();
				}
				if(player.getMap().getId() == 6720)
				{
					if(player.getMap().getCase(121).getDroppedItem(false) == null || player.getMap().getCase(136).getDroppedItem(false) == null
					|| player.getMap().getCase(151).getDroppedItem(false) == null || player.getMap().getCase(271).getDroppedItem(false) == null
					|| player.getMap().getCase(286).getDroppedItem(false) == null || player.getMap().getCase(301).getDroppedItem(false) == null)
						return;
					if(player.getMap().getCase(121).getDroppedItem(false).getTemplate().getId() == 362
					&& player.getMap().getCase(136).getDroppedItem(false).getTemplate().getId() == 363
					&& player.getMap().getCase(151).getDroppedItem(false).getTemplate().getId() == 364
					&& player.getMap().getCase(271).getDroppedItem(false).getTemplate().getId() == 362
					&& player.getMap().getCase(286).getDroppedItem(false).getTemplate().getId() == 363
					&& player.getMap().getCase(301).getDroppedItem(false).getTemplate().getId() == 364)
					{
						World.data.getCarte((short) 6904).openDoor();
					}
				}
			break;*/
			
			/*case 3000://Epï¿½e Crocoburio
				if(player.hasEquiped(1718) && player.hasEquiped(1719) && player.hasEquiped(1720)
				&& player.getTotalStats().getEffect(Constants.STATS_ADD_VITA) == 120
				&& player.getTotalStats().getEffect(Constants.STATS_ADD_SAGE) == 0
				&& player.getTotalStats().getEffect(Constants.STATS_ADD_FORC) == 60
				&& player.getTotalStats().getEffect(Constants.STATS_ADD_INTE) == 50
				&& player.getTotalStats().getEffect(Constants.STATS_ADD_AGIL) == 0 
				&& player.getTotalStats().getEffect(Constants.STATS_ADD_CHAN) == 0)
				{
					SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getMap(), 237, true);
					try { Thread.sleep(2250); } catch (InterruptedException e) {}
					player.setFullMorph(10, false, false);
				}else
				{
					SocketManager.GAME_SEND_Im_PACKET(player, "119");
				}
			break;*/
			
			case 7546://Foire au troll
			case 7547:
				player.send("GDF|"+cell.getId()+";3");
			break;
			
			case 1324:// Plot Rouge des ï¿½motes
				switch(player.getMap().getId()) {
					case 2196:
						if(player.isAway())
							return;
						
						if(player.getGuild() != null || player.getGuildMember() != null) {
							SocketManager.GAME_SEND_gC_PACKET(player, "Ea");
							return;
						}
						if(player.hasItemTemplate(1575, 1)) {
							player.removeByTemplateID(1575, 1);
							SocketManager.GAME_SEND_gn_PACKET(player);
							return;
						}
						SocketManager.GAME_SEND_Im_PACKET(player, "14");
						
					break;
					/*case 2037://Emote Faire signe
						player.addStaticEmote(2);
					break;
					case 2025://Emote Applaudir
						player.addStaticEmote(3);
					break;
					case 2039://Emote Se mettre en Colï¿½re
						player.addStaticEmote(4);
					break;
					case 2047://Emote Peur
						player.addStaticEmote(5);
					break;
					case 8254://Emote Montrer son Arme
						player.addStaticEmote(6);
					break;
					case 2099://Emote Saluer
						player.addStaticEmote(9);
					break;
					case 8539://Emote Croiser les bras
						player.addStaticEmote(14);
					break;*/
				}
			break;
			
			case 1694://Village brigandin tire ï¿½olienne
				SocketManager.GAME_SEND_GA_PACKET(player.getAccount().getGameClient(), "", "2", player.getId()+"", "4");
				player.setPosition(6848, 390);
			break;
			
			case 1695://Village brigandin tire ï¿½olienne
				SocketManager.GAME_SEND_GA_PACKET(player.getAccount().getGameClient(), "", "2", player.getId()+"", "3");
				player.setPosition(6844, 268);
			break;
			
			/*case 7045: TODO : :D
				Map map = player.getMap();
				switch(map.getId())
				{
					case 6165://Prison porte Bonta
						World.data.getCarte((short) 6164).openDoor();
					break;
					
					case 6172://Prison porte Brakmar
						World.data.getCarte((short) 6171).openDoor();
					break;	
					
					case 2034://Emote Colï¿½re 1
						if(!map.getCase(226).getCharacters().isEmpty() && !map.getCase(241).getCharacters().isEmpty()
						&& !map.getCase(269).getCharacters().isEmpty() && !map.getCase(312).getCharacters().isEmpty()
						&& !map.getCase(313).getCharacters().isEmpty() && !map.getCase(328).getCharacters().isEmpty())
						{
							map.openDoor();
						}
					break;
					
					case 2029://Emote Colï¿½re 2
						if(!map.getCase(268).getCharacters().isEmpty() && !map.getCase(283).getCharacters().isEmpty()
						&& !map.getCase(311).getCharacters().isEmpty() && !map.getCase(354).getCharacters().isEmpty()
						&& !map.getCase(355).getCharacters().isEmpty() && !map.getCase(370).getCharacters().isEmpty())
						{
							map.openDoor();
						}
					break;
					
					case 8269://Emote Montrer Arme 1
						if(!map.getCase(240).getCharacters().isEmpty() && !map.getCase(254).getCharacters().isEmpty()
						&& !map.getCase(255).getCharacters().isEmpty() && !map.getCase(269).getCharacters().isEmpty())
						{
							map.openDoor();
						}
					break;
					
					case 7288://Emote Montrer Arme 2
						if(!map.getCase(253).getCharacters().isEmpty() && !map.getCase(324).getCharacters().isEmpty()
						&& !map.getCase(370).getCharacters().isEmpty() && !map.getCase(370).getCharacters().isEmpty())
						{
							map.openDoor();
						}
					break;
					
					case 2032://Emote Faire signe 1
						if(!map.getCase(209).getCharacters().isEmpty() && !map.getCase(223).getCharacters().isEmpty()
						&& !map.getCase(237).getCharacters().isEmpty() && !map.getCase(238).getCharacters().isEmpty()
						&& !map.getCase(239).getCharacters().isEmpty() && !map.getCase(267).getCharacters().isEmpty())
						{
							map.openDoor();
						}
					break;
					
					case 2027://Emote Faire signe 2
						if(!map.getCase(297).getCharacters().isEmpty() && !map.getCase(311).getCharacters().isEmpty()
						&& !map.getCase(325).getCharacters().isEmpty() && !map.getCase(326).getCharacters().isEmpty()
						&& !map.getCase(327).getCharacters().isEmpty() && !map.getCase(355).getCharacters().isEmpty())
						{
							map.openDoor();
						}
					break;
					
					case 2017://Emote Applaudir 1
						if(!map.getCase(238).getCharacters().isEmpty() && !map.getCase(267).getCharacters().isEmpty()
						&& !map.getCase(268).getCharacters().isEmpty())
						{
							map.openDoor();
						}
					break;
					
					case 2018://Emote Applaudir 2
						if(!map.getCase(296).getCharacters().isEmpty() && !map.getCase(298).getCharacters().isEmpty()
						&& !map.getCase(311).getCharacters().isEmpty() && !map.getCase(312).getCharacters().isEmpty()
						&& !map.getCase(326).getCharacters().isEmpty() && !map.getCase(340).getCharacters().isEmpty()
						&& !map.getCase(341).getCharacters().isEmpty() && !map.getCase(354).getCharacters().isEmpty()
						&& !map.getCase(356).getCharacters().isEmpty())
						{
							map.openDoor();
						}
					break;
					
					case 8539:
						if(!map.getCase(238).getCharacters().isEmpty() && !map.getCase(240).getCharacters().isEmpty()
						&& !map.getCase(256).getCharacters().isEmpty() && !map.getCase(314).getCharacters().isEmpty()
						&& !map.getCase(323).getCharacters().isEmpty() && !map.getCase(325).getCharacters().isEmpty()
						&& !map.getCase(372).getCharacters().isEmpty() && !map.getCase(399).getCharacters().isEmpty())
						{
							map.openDoor();
						}
					break;					
				}
			break;*/
			
			default:
			break;
		}
	}
	
	public void getSignIO(Player player, int cell) {
		switch(player.getMap().getId())	{
			case 7460:
				if(this.id == 1988 && cell == 234)
					this.send(player, "dCK71_0706251229");
				if(this.id == 1986 && cell == 161)
					this.send(player, "dCK65_0706251123");
				if(this.id == 1985 && cell == 119)
					this.send(player, "dCK96_0706251201");
				if(this.id == 1986 && cell == 120)
					this.send(player, "dCK61_0802081743");
				if(this.id == 1985 && cell == 149)
					this.send(player, "dCK63_0706251124");
				if(this.id == 1986 && cell == 150)
					this.send(player, "dCK67_0706251223");
				if(this.id == 1986 && cell == 179)
					this.send(player, "dCK68_0706251126");
				if(this.id == 1985 && cell == 180)
					this.send(player, "dCK69_0706251058");
				if(this.id == 1986 && cell == 269)
					this.send(player, "dCK94_0706251138");
				if(this.id == 1985 && cell == 270)
					this.send(player, "dCK70_0706251122");
				if(this.id == 1986 && cell == 299)
					this.send(player, "dCK93_0706251135");
				if(this.id == 1986 && cell == 300)
					this.send(player, "dCK100_0706251214");
				if(this.id == 1985 && cell == 329)
					this.send(player, "dCK98_0706251211");
			break;
			
			case 7411:
				if(this.id == 1531 && cell == 230)
					this.send(player, "dCK139_0612131303");
			break;
			
			case 7543:
				if(this.id == 1528 && cell == 262)
					this.send(player, "dCK75_0603101710");
				if(this.id == 1533 && cell == 169)
					this.send(player, "dCK74_0603101709");
				if(this.id == 1528 && cell == 169)
					this.send(player, "dCK73_0706211414");
			break;
			
			case 7314:
				if(this.id == 1531 && cell == 93)
					this.send(player, "dCK78_0706221019");
				if(this.id == 1532 && cell == 256)
					this.send(player, "dCK76_0603091219");
				if(this.id == 1533 && cell == 415)
					this.send(player, "dCK77_0603091218");
			break;
			
			case 7417:
				if(this.id == 1532 && cell == 264)
					this.send(player, "dCK79_0603101711");
				if(this.id == 1528 && cell == 211)
					this.send(player, "dCK80_0510251009");
				if(this.id == 1532 && cell == 212)
					this.send(player, "dCK77_0603091218");
				if(this.id == 1529 && cell == 212)
					this.send(player, "dCK81_0510251010");
			break;
			
			case 2698:
				if(this.id == 1531 && cell == 93)
					this.send(player, "dCK51_0706211150");
				if(this.id == 1528 && cell == 109)
					this.send(player, "dCK41_0706221516");
			break;
			
			case 2814:
				if(this.id == 1533 && cell == 415)
					this.send(player, "dCK43_0706201719");
				if(this.id == 1532 && cell == 326)
					this.send(player, "dCK50_0706211149");
				if(this.id == 1529 && cell == 325)
					this.send(player, "dCK41_0706221516");
			break;
			
			case 3087:
				if(this.id == 1529 && cell == 89)
					this.send(player, "dCK41_0706221516");
			break;
			
			case 3018:
				if(this.id == 1530 && cell == 354)
					this.send(player, "dCK52_0706211152");
				if(this.id == 1532 && cell == 256)
					this.send(player, "dCK50_0706211149");
				if(this.id == 1528 && cell == 255)
					this.send(player, "dCK41_0706221516");
			break;
			
			case 3433:
				if(this.id == 1533 && cell == 282)
					this.send(player, "dCK53_0706211407");
				if(this.id == 1531 && cell == 179)
					this.send(player, "dCK50_0706211149");
				if(this.id == 1529 && cell == 178)
					this.send(player, "dCK41_0706221516");
			break;
			
			case 4493:
				if(this.id == 1533 && cell == 415)
					this.send(player, "dCK43_0706201719");
				if(this.id == 1532 && cell == 326)
					this.send(player, "dCK50_0706211149");
				if(this.id == 1529 && cell == 325)
					this.send(player, "dCK41_0706221516");
			break;
			
			case 4876:
				if(this.id == 1532 && cell == 316)
					this.send(player, "dCK54_0706211408");
				if(this.id == 1531 && cell == 283)
					this.send(player, "dCK51_0706211150");
				if(this.id == 1530 && cell == 282)
					this.send(player, "dCK52_0706211152");
			break;			
		}
	}
			
	public InteractiveObjectTemplate getTemplate() {
		return template;
	}

	public void setTemplate(InteractiveObjectTemplate template) {
		this.template = template;
	}
	
	private void send(Player player, String packet) {
		player.send(packet);
		player.setAway(true);
	}

	public static class InteractiveObjectTemplate {
		
		private int id;
		private int respawnTime;
		private int duration;
		private int unk;
		private boolean walkable;
		
		public InteractiveObjectTemplate(int id, int respawnTime, int duration, int unk, boolean walkable) {
			this.id = id;
			this.respawnTime = respawnTime;
			this.duration = duration;
			this.unk = unk;
			this.walkable = walkable;
		}
		
		public int getId() {
			return id;
		}	
		
		public boolean isWalkable() {
			return walkable;
		}
		
		public int getRespawnTime() {
			return respawnTime;
		}
		
		public int getDuration() {
			return duration;
		}
		
		public int getUnk() {
			return unk;
		}
	}	
}