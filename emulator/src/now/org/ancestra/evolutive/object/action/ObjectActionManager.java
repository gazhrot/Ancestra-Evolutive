package org.ancestra.evolutive.object.action;

import java.util.HashMap;
import java.util.Map;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.fight.spell.Animation;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.job.JobStat;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.object.SoulStone;

public class ObjectActionManager {

	private static ObjectActionManager instance;
	private Map<Integer, ObjectActionCallback> originCallbacks;
	private Map<Integer, ObjectActionCallback> pluginCallbacks;
	
	private ObjectActionManager() {
		this.originCallbacks = new HashMap<>();
		this.pluginCallbacks = new HashMap<>();
		
		this.originCallbacks.put(-1, new SimpleObjectActionCallback("USELESS") {
			
			@Override
			public ObjectActionResult execute(Player player, int type, String arg, Object object, int cellid) throws Exception {
				return new ObjectActionResult(true, false);				
			}
			
		});
		
		this.originCallbacks.put(0, new SimpleObjectActionCallback("TELEPORT") {
			
			@Override
			public ObjectActionResult execute(Player player, int type, String arg, Object object, int cellid)
					throws Exception {	
				String[] args = arg.split("\\,");
				
				short mapId = Short.parseShort(args[0]);
				int cellId = Integer.parseInt(args[1]);
				
				if(player.getMap().getId() != 666)
					player.setPosition(mapId, cellId);
				else
					if(player.getCell().getId() == 268)
						player.setPosition(mapId, cellId);
			
				return new ObjectActionResult();
			}
			
		});
		
		this.originCallbacks.put(1,new SimpleObjectActionCallback("SAVEPOINT") {
			
			@Override
			public ObjectActionResult execute(Player player, int type, String arg, Object object, int cellid)
					throws Exception {
				if(player.getMap().getId() != 666)
					player.warpToSavePos();
					
				return new ObjectActionResult();
			}
			
		});
		
		this.originCallbacks.put(2, new SimpleObjectActionCallback("GIVEKAMAS") {
			
			@Override
			public ObjectActionResult execute(Player player, int type, String arg, Object object, int cellid)
					throws Exception {
				int count = Integer.parseInt(arg);
				player.addKamas(count);
				
				if(player.isOnline())
					SocketManager.GAME_SEND_STATS_PACKET(player);
				
				return new ObjectActionResult();
			}
			
		});
		
		this.originCallbacks.put(3, new SimpleObjectActionCallback("GIVE") {
			
			@Override
			public ObjectActionResult execute(Player player, int type, String arg, Object obj, int cellid)
					throws Exception {
				ObjectActionResult result =	new ObjectActionResult();
				boolean isOk1 = true, isOk2 = true;
				
				for(String arg0: arg.split("\\,")) {
					int val, statId1;
					
					if(arg.contains("\\;")) {
						statId1 = Integer.parseInt(arg.split("\\;")[0]);
						val = obj.getRandomValue(obj.parseStatsString(), Integer.parseInt(arg.split("\\;")[0]));
					} else {
						statId1 = Integer.parseInt(arg0);
						val = obj.getRandomValue(obj.parseStatsString(), Integer.parseInt(arg0));
					}
					
					switch(statId1) {					
						case 110://Vie.
							if(player.getPdv() == player.getMaxPdv()) {
								isOk1 = false;
								continue;
							}
							
							if(player.getPdv() + val > player.getMaxPdv())
								val = player.getMaxPdv() - player.getPdv();
							
							player.setPdv(player.getPdv() + val);
							SocketManager.GAME_SEND_STATS_PACKET(player);
							SocketManager.GAME_SEND_Im_PACKET(player, "01;" + val);
							break;
						
						case 139://Energie.
							if(player.getEnergy() == 10000) {
								isOk2 = false;
								continue;
							}
							
							if(player.getEnergy() + val > 10000)
								val = 10000 - player.getEnergy();
							
							player.setEnergy(player.getEnergy() + val);
							SocketManager.GAME_SEND_STATS_PACKET(player);
							SocketManager.GAME_SEND_Im_PACKET(player, "07;" + val);
							break;
						
						case 605://Exp�rience.
							player.addXp(val);
							SocketManager.GAME_SEND_STATS_PACKET(player);
							SocketManager.GAME_SEND_Im_PACKET(player, "08;" + val);
							break;
						
						case 614://Exp�rience m�tier.
							JobStat job = player.getMetierByID(Integer.parseInt(arg0.split("\\;")[1]));
							
							if(job == null) {
								isOk1 = false; 
								isOk2 = false;
								continue;
							}	
							
							job.addXp(player, val);
							SocketManager.GAME_SEND_Im_PACKET(player, "017;" + val + "~" + Integer.parseInt(arg0.split("\\;")[1]));
							break;
					}
				}
				
				if(arg.split("\\,").length == 1)
					if(!isOk1 || !isOk2)
						result.setOk(false);
				else
					if(!isOk1 && !isOk2)
						result.setOk(false);
				result.setSend(false);
				
				return result;
			}
			
		});
		
		this.originCallbacks.put(4, new SimpleObjectActionCallback("GIVESTAT") {
			
			@Override
			public ObjectActionResult execute(Player player, int type, String arg,
					Object objet, int cellid) throws Exception {
				//Don de Stats.
				for(String arg0: arg.split("\\,")) {
					int statId = Integer.parseInt(arg0.split("\\;")[0]);
					int val = Integer.parseInt(arg0.split("\\;")[1]);
					switch(statId)
					{
						case 1://Vitalit�.
							 for(int i = 0; i < val; i++)
								 player.boostStat(11, false);
							 break;
						case 2://Sagesse.
							for(int i = 0; i < val; i++)
								player.boostStat(12, false);
							break;	
						case 3://Force.
							for(int i = 0; i < val; i++)
								player.boostStat(10, false);
							break;
						case 4://Intelligence.
							for(int i = 0; i < val; i++)
								player.boostStat(15, false);
							break;
						case 5://Chance.
							for(int i = 0; i < val; i++)
								player.boostStat(13, false);
							break;
						case 6://Agilit�.
							for(int i = 0; i < val; i++)
								player.boostStat(14, false);
							break;
						case 7://Point de Sort.
							player.setSpellPoints(player.getSpellPoints() + val);
							break;
					}
				}
				
				SocketManager.GAME_SEND_STATS_PACKET(player);
				return new ObjectActionResult();
			}
			
		});
		
		this.originCallbacks.put(5, new SimpleObjectActionCallback("ANIM") {
			
			@Override
			public ObjectActionResult execute(Player player, int type, String arg,
					Object objet, int cellid) throws Exception {
				Animation animation = World.data.getAnimation(Integer.parseInt(arg));
				
				if(player.getFight() != null)
					return null;
				
				player.changeOrientation(1);
				SocketManager.GAME_SEND_GA_PACKET_TO_MAP(player.getMap(), "0", 228, player.getId() + ";" + cellid + "," + Animation.parseToGA(animation), "");
				return new ObjectActionResult();
			}
			
		});
		
		this.originCallbacks.put(6, new SimpleObjectActionCallback("LEARNSPELL") {
			
			@Override
			public ObjectActionResult execute(Player player, int type, String arg,
					Object objet, int cellid) throws Exception {
				//Apprendre un sort.
				int id = Integer.parseInt(arg);
				
				if(World.data.getSort(id) == null)
					return null;
				if(!player.learnSpell(id, 1, true, true, true))
					return null;
				
				return new ObjectActionResult(true, false);
			}
			
		});
		
		this.originCallbacks.put(7, new SimpleObjectActionCallback("UNLEARNSPELL") {
			
			@Override
			public ObjectActionResult execute(Player player, int type,
					String arg, Object objet, int cellid) throws Exception {
				//D�sapprendre un sort.
				 int id0 = Integer.parseInt(arg);
			     int oldLevel = player.getSortStatBySortIfHas(id0).getLevel();
			    
			     if(player.getSortStatBySortIfHas(id0) == null)
			    	 return null;
			     if(oldLevel <= 1)
			    	 return null;
			    
			     player.unlearnSpell(id0, 1, oldLevel, true, true);
			     return new ObjectActionResult();
			}
			
		});
		
		this.originCallbacks.put(9, new SimpleObjectActionCallback("UNLEARNJOB") { 
			
			@Override
			public ObjectActionResult execute(Player player, int type, String arg, Object objet, int cellid) throws Exception {
				//Oubli� un m�tier.
				int job = Integer.parseInt(arg);
				
			    if(job < 1) 
			    	return null;
			    
			    JobStat jobStats = player.getMetierByID(job);
			   
			    if(jobStats == null) 
			    	return null;
			    
			    player.unlearnJob(jobStats.getId());
			    SocketManager.GAME_SEND_STATS_PACKET(player);
			    World.database.getCharacterData().update(player); 
			    return new ObjectActionResult();
			}
			
		});
		
		this.originCallbacks.put(11, new SimpleObjectActionCallback("CHANGESEX") {
			
			@Override
			public ObjectActionResult execute(Player player,
					int type, String arg, Object objet, int cellid) throws Exception {
				//Chang� de Sexe.
				if(player.getSex() == 0)
					player.setSex(1);
				else
					player.setSex(0);
					
				SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getMap(), player);
				return new ObjectActionResult();
			}
			
		});
				
		this.originCallbacks.put(14, new SimpleObjectActionCallback("LEARNJOB") {
	
			@Override
			public ObjectActionResult execute(Player player,
					int type, String arg, Object objet, int cellid)	throws Exception {
				//Apprendre un m�tier.
				int job = Integer.parseInt(arg);
				
				if(World.data.getMetier(job) == null)
					return null;
				if(player.getMetierByID(job) != null) {
					SocketManager.GAME_SEND_Im_PACKET(player, "111");
					return null;
				}
				
				if(player.getMetierByID(2) != null && player.getMetierByID(2).get_lvl() < 30
				|| player.getMetierByID(11) != null && player.getMetierByID(11).get_lvl() < 30
				|| player.getMetierByID(13) != null && player.getMetierByID(13).get_lvl() < 30
				|| player.getMetierByID(14) != null && player.getMetierByID(14).get_lvl() < 30
				|| player.getMetierByID(15) != null && player.getMetierByID(15).get_lvl() < 30
				|| player.getMetierByID(16) != null && player.getMetierByID(16).get_lvl() < 30
				|| player.getMetierByID(17) != null && player.getMetierByID(17).get_lvl() < 30
				|| player.getMetierByID(18) != null && player.getMetierByID(18).get_lvl() < 30
				|| player.getMetierByID(19) != null && player.getMetierByID(19).get_lvl() < 30
				|| player.getMetierByID(20) != null && player.getMetierByID(20).get_lvl() < 30
				|| player.getMetierByID(24) != null && player.getMetierByID(24).get_lvl() < 30
				|| player.getMetierByID(25) != null && player.getMetierByID(25).get_lvl() < 30
				|| player.getMetierByID(26) != null && player.getMetierByID(26).get_lvl() < 30
				|| player.getMetierByID(27) != null && player.getMetierByID(27).get_lvl() < 30
				|| player.getMetierByID(28) != null && player.getMetierByID(28).get_lvl() < 30
				|| player.getMetierByID(31) != null && player.getMetierByID(31).get_lvl() < 30
				|| player.getMetierByID(36) != null && player.getMetierByID(36).get_lvl() < 30
				|| player.getMetierByID(41) != null && player.getMetierByID(41).get_lvl() < 30
				|| player.getMetierByID(56) != null && player.getMetierByID(56).get_lvl() < 30
				|| player.getMetierByID(58) != null && player.getMetierByID(58).get_lvl() < 30
				|| player.getMetierByID(60) != null && player.getMetierByID(60).get_lvl() < 30
				|| player.getMetierByID(65) != null && player.getMetierByID(65).get_lvl() < 30) {	
					SocketManager.GAME_SEND_Im_PACKET(player, "18;30");
					return null;
				}
				
				if(player.totalJobBasic() > 2) {
					SocketManager.GAME_SEND_Im_PACKET(player, "19");
					return null;
				} else {
					if(job == 27) {
						if(!player.hasItemTemplate(966, 1))
							return null;
						SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 966 + "~" + 1);
						player.learnJob(World.data.getMetier(job));
					} else {
						player.learnJob(World.data.getMetier(job));	
					}
				}
				
				return new ObjectActionResult();
			}
			
		});
		
		this.originCallbacks.put(15, new SimpleObjectActionCallback("TELEPORTHOME") {
			
			@Override
			public ObjectActionResult execute(Player player,
					int type, String arg, Object objet, int cellid) throws Exception {
				//TP au foyer.
				House house = World.database.getHouseData().load(player);
				
				if(house != null)
					player.setPosition(house.getToMapid(), house.getToCellid());
				
				return new ObjectActionResult();
			}
			
		});
		
		this.originCallbacks.put(24, new SimpleObjectActionCallback("TPALIGNEDAREA") {
			
			@Override
			public ObjectActionResult execute(Player player,
					int type, String arg, Object objet, int cellid) throws Exception {
				//TP Village align�.
				int mapId = (short) Integer.parseInt(arg.split("\\,")[0]);
				int cellId = Integer.parseInt(arg.split("\\,")[1]);
				
				if(World.data.getMap(mapId).getSubArea().getAlignement() == player.getAlignement())
					player.setPosition(mapId, cellId);
				
				return new ObjectActionResult();
			}
			
		});
		
		this.originCallbacks.put(25, new SimpleObjectActionCallback("SPAWNGROUP") {
			
			@Override
			public ObjectActionResult execute(Player player,
					int type, String arg, Object obj, int cellid) throws Exception {
				//Spawn groupe.
				boolean inArena = arg.split("\\;")[0].equals("true");
				String groupData = "";
				
				if(inArena && !Server.config.getArenaMaps().contains(Integer.valueOf(player.getMap().getId())))
					return null;	
				if(arg.split("\\;")[1].equals("1")) {
					groupData = arg.split("\\;")[2];
				} else {
					SoulStone pierrePleine = (SoulStone) obj;
					groupData = pierrePleine.parseGroupData();
				}
				
				player.getMap().spawnNewGroup(true, player.getCell(), groupData, "MiS=" + player.getId());
				return new ObjectActionResult();
			}		
			
		});
		
		this.originCallbacks.put(26, new SimpleObjectActionCallback("ADDITEM") {
			
			@Override
			public ObjectActionResult execute(Player player,
					int type, String arg, Object objet, int cellid) throws Exception {
				//Ajout d'objet.
				for(String i: arg.split("\\;")) {
					Object obj = World.data.getObjectTemplate(Integer.parseInt(i.split("\\,")[0])).createNewItem(Integer.parseInt(i.split("\\,")[1]), false);
					if(player.addObject(obj, true))
						World.data.addObject(obj,true);
				}
				
				SocketManager.GAME_SEND_Ow_PACKET(player);
				return new ObjectActionResult();
			}
			
		});
		
		this.originCallbacks.put(27, new SimpleObjectActionCallback("ADDTITLE") {
			
			@Override
			public ObjectActionResult execute(Player player,
					int type, String arg, Object objet, int cellid)
					throws Exception {
				//Ajout de titre.
				player.setTitle(Byte.valueOf(arg));
				return new ObjectActionResult();
			}
			
		});
		
		this.originCallbacks.put(28, new SimpleObjectActionCallback("ADDZAAP") {
			
			@Override
			public ObjectActionResult execute(Player player,
					int type, String arg, Object objet, int cellid)
					throws Exception {
				//Ajout de zaap.
				player.addZaap((short) Integer.parseInt(arg));
				return new ObjectActionResult();
			}
			
		});
		
		this.originCallbacks.put(29, new SimpleObjectActionCallback("UNLEARNSPELLPANEL") {
			
			@Override
			public ObjectActionResult execute(Player player,
					int type, String arg, Object objet, int cellid)
					throws Exception {
				//Panel d'oubli de sort.
				player.setForgetingSpell(true);
				SocketManager.GAME_SEND_FORGETSPELL_INTERFACE('+', player);
				return new ObjectActionResult();
			}	
			
		});
	}
	
	public static ObjectActionManager getInstance() {	
		if(instance == null)
			instance = new ObjectActionManager();
		
		return instance;
	}
	
	public Map<Integer, ObjectActionCallback> getOriginCallbacks() {
		return originCallbacks;
	}
	
	public ObjectActionCallback getOriginCallback(int type) {
		return originCallbacks.get(type);
	}
	
	public Map<Integer, ObjectActionCallback> getPluginCallbacks() {
		return pluginCallbacks;
	}
	
	public ObjectActionCallback getPluginCallback(int type) {
		return pluginCallbacks.get(type);
	}
}
