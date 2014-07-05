package org.ancestra.evolutive.object;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.ConditionParser;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.fight.spell.Animation;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.job.JobStat;

public class ObjectAction {

	private String type;
	private String args;
	private String cond;
	private boolean send = true;
	
	public ObjectAction(String type, String args, String cond) {
		this.type = type;
		this.args = args;
		this.cond = cond;
	}

	public void apply(Player player, Player target, int objet, int cellid) {
		if(player == null)
			return;
		if(!player.isOnline())
			return;
		if(player.getFight() != null)
			return;
		if(player.getAccount().getGameClient() == null) 
			return;
		if(!this.cond.equalsIgnoreCase("") && !this.cond.equalsIgnoreCase("-1")&& !ConditionParser.validConditions(player, this.cond)) {
			SocketManager.GAME_SEND_Im_PACKET(player, "119");
			return;
		}
		if(player.getLevel() < World.data.getObject(objet).getTemplate().getLevel()) {
			SocketManager.GAME_SEND_Im_PACKET(player, "119");
			return;
		}
		
		Player perso = player;
		if(target != null)
			perso = target;
		if(World.data.getObject(objet) == null) {
			SocketManager.GAME_SEND_MESSAGE(perso, "Error object null. Merci de pr�venir un administrateur est d'indiquer le message.", Server.config.getMotdColor());
			return;
		}
		
		boolean isOk = true;
		int turn = 0;
		String arg = "";
		
		try {
			for(String type: this.type.split("\\;"))
			{
				if(!this.args.isEmpty())
					arg = args.split("\\|", 2)[turn];
				
				switch(Integer.parseInt(type))
				{	
					case -1: 
						isOk = true; 
						send = false;
					break;
						
					case 0://T�l�portation.
						short mapId = Short.parseShort(arg.split(",",2)[0]);
						int cellId = Integer.parseInt(arg.split(",",2)[1]);
						if(perso.getMap().getId() != 666)
							perso.setPosition(mapId, cellId);
						else
							if(perso.getCell().getId() == 268)
								perso.setPosition(mapId, cellId);
					break;
					
					case 1://T�l�portation au point de sauvegarde.
						if(perso.getMap().getId() != 666)
							perso.warpToSavePos();
					break;
					
					case 2://Don de Kamas.
						int count = Integer.parseInt(arg);
						perso.addKamas(count);
						if(perso.isOnline())
							SocketManager.GAME_SEND_STATS_PACKET(perso);
					break;
					
					case 3://Don de vie.
						boolean isOk1 = true, isOk2 = true;
						for(String arg0: arg.split(","))
						{
							int val, statId1;
							if(arg.contains(";"))
							{
								statId1 = Integer.parseInt(arg.split(";")[0]);
								val = World.data.getObject(objet).getRandomValue(World.data.getObject(objet).parseStatsString(), Integer.parseInt(arg.split(";")[0]));
							}else
							{
								statId1 = Integer.parseInt(arg0);
								val = World.data.getObject(objet).getRandomValue(World.data.getObject(objet).parseStatsString(), Integer.parseInt(arg0));
							}
							switch(statId1)
							{					
								case 110://Vie.
									if(perso.getPdv() == perso.getMaxPdv()) {
										isOk1 = false;
										continue;
									}
									if(perso.getPdv() + val > perso.getMaxPdv())
										val = perso.getMaxPdv() - perso.getPdv();
									perso.setPdv(perso.getPdv()+val);
									SocketManager.GAME_SEND_STATS_PACKET(perso);
									SocketManager.GAME_SEND_Im_PACKET(perso, "01;"+val);
									
								break;
								case 139://Energie.
									if(perso.getEnergy() == 10000) {
										isOk2 = false;
										continue;
									}
									if(perso.getEnergy() + val > 10000)
										val = 10000 - perso.getEnergy();
									perso.setEnergy(perso.getEnergy()+val);
									SocketManager.GAME_SEND_STATS_PACKET(perso);
									SocketManager.GAME_SEND_Im_PACKET(perso, "07;"+val);
								break;
								case 605://Exp�rience.
									perso.addXp(val);
									SocketManager.GAME_SEND_STATS_PACKET(perso);
									SocketManager.GAME_SEND_Im_PACKET(perso, "08;"+val);
								break;
								case 614://Exp�rience m�tier.
									JobStat job = perso.getMetierByID(Integer.parseInt(arg0.split(";")[1]));
									if(job == null) {
										isOk1 = false; 
										isOk2 = false;
										continue;
									}	
									job.addXp(perso, val);
									SocketManager.GAME_SEND_Im_PACKET(perso, "017;"+val+"~"+Integer.parseInt(arg0.split(";")[1]));
								break;
							}
						}
						if(arg.split(",").length == 1)
							if(!isOk1 || !isOk2)
								isOk = false;
						else
							if(!isOk1 && !isOk2)
								isOk = false;
						send = false;
					break;
					
					case 4://Don de Stats.
						for(String arg0: arg.split(","))
						{
							int statId = Integer.parseInt(arg0.split(";")[0]);
							int val = Integer.parseInt(arg0.split(";")[1]);
							switch(statId)
							{
								case 1://Vitalit�.
									 for(int i=0; i<val; i++)
										 perso.boostStat(11, false);
									break;
								case 2://Sagesse.
									for(int i=0; i<val; i++)
										perso.boostStat(12, false);
									break;	
								case 3://Force.
									for(int i=0; i<val; i++)
										perso.boostStat(10, false);
									break;
								case 4://Intelligence.
									for(int i=0; i<val; i++)
										perso.boostStat(15, false);
									break;
								case 5://Chance.
									for(int i=0; i<val; i++)
										perso.boostStat(13, false);
									break;
								case 6://Agilit�.
									for(int i=0; i<val; i++)
										perso.boostStat(14, false);
									break;
								case 7://Point de Sort.
									perso.setSpellPoints(perso.getSpellPoints() + val);
									break;
							}
						}
						SocketManager.GAME_SEND_STATS_PACKET(perso);
					break;
					
					case 5://F�e d'artifice.
						int id0 = Integer.parseInt(arg);
						Animation anim = World.data.getAnimation(id0);
						if(perso.getFight() != null)
							return;
						perso.changeOrientation(1);
						SocketManager.GAME_SEND_GA_PACKET_TO_MAP(perso.getMap(), "0", 228, perso.getId()+";"+cellid+","+Animation.parseToGA(anim), "");
					break;
					
					case 6://Apprendre un sort.
						id0 = Integer.parseInt(arg);
						if(World.data.getSort(id0) == null)
							return;
						if(!perso.learnSpell(id0, 1, true, true, true))
							return;
						send = false;
					break;
					
					case 7://D�sapprendre un sort.
						 id0 = Integer.parseInt(arg);
					     int oldLevel = perso.getSortStatBySortIfHas(id0).getLevel();
					     if(perso.getSortStatBySortIfHas(id0) == null)
					    	 return;
					     if(oldLevel <= 1)
					    	 return;
					     perso.unlearnSpell(id0, 1, oldLevel, true, true);
					break;
					
					case 8://D�sapprendre un sort � un percepteur.
						//TODO
						isOk = false; send = false;
					break;
					
					case 9://Oubli� un m�tier.
						int job = Integer.parseInt(arg);
					    if (job < 1) 
					    	return;
					    JobStat jobStats = perso.getMetierByID(job);
					    if (jobStats == null) 
					    	return;
					    perso.unlearnJob(jobStats.getId());
					    SocketManager.GAME_SEND_STATS_PACKET(perso);
					    World.database.getCharacterData().update(perso);
					break;
					
					case 10://EPO. TODO: 
						/*Object obj = World.data.getObject(objet);
						if(obj == null) 
							return;
						Object pets = perso.getObjetByPos(Constant.ITEM_POS_FAMILIER);
						if(pets == null)
							return;
						PetEntry MyPets = World.data.getPetsEntry(pets.getGuid());
						if(MyPets == null)
							return;
						if(obj.getTemplate().getConditions().contains(pets.getTemplate().getId()+""))
							MyPets.giveEpo(perso);*/
					break;
					
					case 11://Chang� de Sexe.
						if(perso.getSex() == 0)
							perso.setSex(1);
						else
							perso.setSex(0);
						SocketManager.GAME_SEND_ALTER_GM_PACKET(perso.getMap(), perso);
					break;
					
					case 12://Chang� de nom.
						//TODO
						isOk = false; send = false;
					break;
					
					case 13://Chang� de couleurs. 
						//TODO
						isOk = false; send = false;
					break;
					
					case 14://Apprendre un m�tier.
						job = Integer.parseInt(arg);
						if(World.data.getMetier(job) == null)
							return;
						if(perso.getMetierByID(job) != null)//M�tier d�j� appris
						{
							SocketManager.GAME_SEND_Im_PACKET(perso, "111");
							return;
						}
						if(perso.getMetierByID(2) != null && perso.getMetierByID(2).get_lvl() < 30
						|| perso.getMetierByID(11) != null && perso.getMetierByID(11).get_lvl() < 30
						|| perso.getMetierByID(13) != null && perso.getMetierByID(13).get_lvl() < 30
						|| perso.getMetierByID(14) != null && perso.getMetierByID(14).get_lvl() < 30
						|| perso.getMetierByID(15) != null && perso.getMetierByID(15).get_lvl() < 30
						|| perso.getMetierByID(16) != null && perso.getMetierByID(16).get_lvl() < 30
						|| perso.getMetierByID(17) != null && perso.getMetierByID(17).get_lvl() < 30
						|| perso.getMetierByID(18) != null && perso.getMetierByID(18).get_lvl() < 30
						|| perso.getMetierByID(19) != null && perso.getMetierByID(19).get_lvl() < 30
						|| perso.getMetierByID(20) != null && perso.getMetierByID(20).get_lvl() < 30
						|| perso.getMetierByID(24) != null && perso.getMetierByID(24).get_lvl() < 30
						|| perso.getMetierByID(25) != null && perso.getMetierByID(25).get_lvl() < 30
						|| perso.getMetierByID(26) != null && perso.getMetierByID(26).get_lvl() < 30
						|| perso.getMetierByID(27) != null && perso.getMetierByID(27).get_lvl() < 30
						|| perso.getMetierByID(28) != null && perso.getMetierByID(28).get_lvl() < 30
						|| perso.getMetierByID(31) != null && perso.getMetierByID(31).get_lvl() < 30
						|| perso.getMetierByID(36) != null && perso.getMetierByID(36).get_lvl() < 30
						|| perso.getMetierByID(41) != null && perso.getMetierByID(41).get_lvl() < 30
						|| perso.getMetierByID(56) != null && perso.getMetierByID(56).get_lvl() < 30
						|| perso.getMetierByID(58) != null && perso.getMetierByID(58).get_lvl() < 30
						|| perso.getMetierByID(60) != null && perso.getMetierByID(60).get_lvl() < 30
						|| perso.getMetierByID(65) != null && perso.getMetierByID(65).get_lvl() < 30)
						{	
							SocketManager.GAME_SEND_Im_PACKET(perso, "18;30");
							return;
						}
						if(perso.totalJobBasic() > 2)
						{
							SocketManager.GAME_SEND_Im_PACKET(perso, "19");
							return;
						}else
						{
							if(job == 27)
							{
								if(!perso.hasItemTemplate(966, 1))
									return;
								SocketManager.GAME_SEND_Im_PACKET(perso, "022;" + 966 + "~" + 1);
								perso.learnJob(World.data.getMetier(job));
							}else
							{
								perso.learnJob(World.data.getMetier(job));	
							}
						}
					break;
					
					case 15://TP au foyer.
						House house = World.database.getHouseData().load(perso);
						if(house != null){
							perso.setPosition(house.getToMapid(), house.getToCellid());
						}
					break;
					
					/*case 16://Pnj Follower.
						perso.setMascotte(1);
					break;
					
					case 17://B�n�diction.
						perso.setBenediction(World.data.getObject(objet).getTemplate().getId());
					break;
						
					case 18://Mal�diction.
						perso.setMalediction(World.data.getObject(objet).getTemplate().getId());
					break;
						
					case 19://RolePlay Buff.
						perso.setRoleplayBuff(World.data.getObject(objet).getTemplate().getId());
					break;
					
					case 20://Bonbon.
						perso.setCandy(World.data.getObject(objet).getTemplate().getId());
			  	    break;
			  	    
					case 21://Poser un objet d'�levage.
						Map map0 = perso.getMap();
						id0 = World.data.getObject(objet).getTemplate().getId();
			
						int resist = World.data.getObject(objet).getResistance(World.data.getObject(objet).parseStatsString());
						int resistMax = World.data.getObject(objet).getResistanceMax(World.data.getObject(objet).getTemplate().getStrTemplate());
						if(map0.getMountPark() == null)
							return;
						MountPark MP = map0.getMountPark();
						if(perso.get_guild() == null) {
							SocketManager.GAME_SEND_BN(perso);
							return;
						}
						if(!perso.getGuildMember().canDo(Constant.G_AMENCLOS)) {
							SocketManager.GAME_SEND_Im_PACKET(perso, "193");
							return;
						}
						if(MP.getCellOfObject().size() == 0 || !MP.getCellOfObject().contains(cellid)) {
							SocketManager.GAME_SEND_BN(perso);
							return;
						}
						if(MP.getObject().size() < MP.getMaxObject()) 
						{
							MP.addObject(cellid, id0, perso.getId(), resistMax, resist);
							SocketManager.SEND_GDO_PUT_OBJECT_MOUNT(map0, cellid + ";" + id0 + ";1;" + resist + ";" + resistMax);
						}else
						{
							SocketManager.GAME_SEND_Im_PACKET(perso, "1107");
							return;
						}
					break;
					
					case 22://Poser un prisme.
						map0 = perso.getMap();
						int cellId1 = perso.getCell().getId();
						SubArea subArea = map0.getSubArea();
						Area area = subArea.getArea();
						int alignement = perso.getAlignement();
						if(cellId1 <= 0) 
							return;
						if(alignement == 0 || alignement == 3) {
							SocketManager.GAME_SEND_MESSAGE(perso,"Vous ne possedez pas l'alignement n�cessaire pour poser un prisme.", Main.messageColor);
							return;
						}
						if(!perso.is_showWings()) {
							SocketManager.GAME_SEND_MESSAGE(perso,"Vos ailes doivent �tre activ� afin de poser un prisme.", Main.messageColor);
							return;
						}
						if(Config.containsPrismeMap(map0.getId())) {
							SocketManager.GAME_SEND_MESSAGE(perso, "Vous ne pouvez pas poser un prisme sur cette map.", Main.messageColor);
							return;
						}
						if(subArea.getAlignement() != 0 || !subArea.getConquistable()) {
							SocketManager.GAME_SEND_MESSAGE(perso, "L'alignement de cette sous-zone est en conqu�te ou n'est pas neutre !", Main.messageColor);
							return;
						}
						Prism Prisme = new Prism(World.data.getNextIDPrisme(), alignement, 1, map0.getId(), cellId1, perso.get_honor(), -1);
						subArea.setAlignement(alignement);
						subArea.setPrismId(Prisme.getId());
						for(Personnage z : World.data.getOnlinePersos()) {
							if(z == null)
								continue;
							if(z.get_align() == 0) {
								SocketManager.GAME_SEND_am_ALIGN_PACKET_TO_SUBAREA(z, subArea.getId() + "|" + alignement + "|1");
								if(area.getalignement() == 0)
									SocketManager.GAME_SEND_aM_ALIGN_PACKET_TO_AREA(z, area.get_id() + "|" + alignement);
								continue;
							}
							SocketManager.GAME_SEND_am_ALIGN_PACKET_TO_SUBAREA(z, subArea.getId() + "|" + alignement + "|0");
							if(area.getalignement() == 0)
								SocketManager.GAME_SEND_aM_ALIGN_PACKET_TO_AREA(z, area.get_id() + "|" + alignement);
						}
						if(area.getalignement() == 0) {
							area.setPrismeID(Prisme.getId());
							area.setalignement(alignement);
							Prisme.setConquestArea(area.get_id());
						}
						World.data.addPrisme(Prisme);
						SqlManager.ADD_PRISME(Prisme);
						perso.getMap().getSubArea().setAlignement(perso.getAlignement());
						SqlManager.UPDATE_SUBAREA(perso.getMap().getSubArea());
						SocketManager.GAME_SEND_PRISME_TO_MAP(map0, Prisme);
					break;
					
					case 23://Rappel Prismatique.		
						int dist = 99999, alea = 0;
						mapId = 0; cellId = 0;
						for(Prism i: World.data.AllPrisme())
						{
							if(i.getAlignement() != perso.getAlignement())
								continue;
							alea = (World.data.getMap(i.getMap()).getX() - perso.getMap().getX())*(World.data.getMap(i.getMap()).getX() - perso.getMap().getX()) + (World.data.getMap(i.getMap()).getY() - perso.getMap().getY())*(World.data.getMap(i.getMap()).getY() - perso.getMap().getY());
							if(alea < dist) {
								dist = alea;
								mapId = i.getMap();
								cellId = i.getCell();
							}
						}
						if(mapId != 0)
							perso.setPosition(mapId, cellId);
					break;*/
					
					case 24://TP Village align�.
						mapId = (short) Integer.parseInt(arg.split(",")[0]);
						cellId = Integer.parseInt(arg.split(",")[1]);
						if(World.data.getMap(mapId).getSubArea().getAlignement() == perso.getAlign())
							perso.setPosition(mapId, cellId);
					break;
					
					case 25://Spawn groupe.
						boolean inArena = arg.split(";")[0].equals("true");
						String groupData = "";
						if(inArena && !Server.config.getArenaMaps().contains(Integer.valueOf(perso.getMap().getId())))
							return;	
						if(arg.split(";")[1].equals("1")) {
							groupData = arg.split(";")[2];
						}else
						{
							SoulStone pierrePleine = (SoulStone) World.data.getObject(objet);
							groupData = pierrePleine.parseGroupData();
						}
						String condition = "MiS = "+perso.getId();
						perso.getMap().spawnNewGroup(true, perso.getCell(), groupData, condition);
					break;
					
					case 26://Ajout d'objet.
						for(String i: arg.split("\\;")) {
							Object obj = World.data.getObjectTemplate(Integer.parseInt(i.split("\\,")[0])).createNewItem(Integer.parseInt(i.split("\\,")[1]), false);
							if(perso.addObject(obj, true))
								World.data.addObject(obj,true);
						}
						SocketManager.GAME_SEND_Ow_PACKET(perso);
					break;
					
					case 27://Ajout de titre.
						perso.setTitle(Byte.valueOf(arg));
					break;
					
					case 28://Ajout de zaap.
						perso.addZaap((short) Integer.parseInt(arg));
					break;
					
					case 29://Panel d'oubli de sort.
						perso.setForgetingSpell(true);
						SocketManager.GAME_SEND_FORGETSPELL_INTERFACE('+', perso);
					break;
					
					default:
						if(Server.config.isDebug())
							System.out.println("- Action id "+type+" non implant� dans le syst�me !");
					break;
				}
				turn++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean effect = this.haveEffect(World.data.getObject(objet).getTemplate().getId(), perso);
		if(effect)
			isOk = true;
		if(isOk)
			effect = true;
		if(this.type.split("\\;").length > 1)
			isOk = true;
		if(objet != -1)	{
			if(send)
				SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + World.data.getObject(objet).getTemplate().getId());
			if(isOk && effect && World.data.getObject(objet).getTemplate().getId() != 7799)
				if(World.data.getObject(objet) != null)
					player.removeItem(objet, 1, true, true);			
		}
	}	

	private boolean haveEffect(int id, Player perso)
	{
		switch(id)
		{
			case 7799://Le Saut Sifflard
				perso.toogleOnMount();
				send = false;
				return false;
				
			case 10832://Craqueloroche
				perso.getMap().spawnNewGroup(true, perso.getCell(), "483,1,1000", "MiS = "+perso.getId());
				return true;
				
			case 10664://Abragland
				perso.getMap().spawnNewGroup(true, perso.getCell(), "47,1,1000", "MiS = "+perso.getId());
				return true;
				
			/*case 10665://Coffre de Jorbak
				perso.setCandy(10688);
				return true;
			
			case 10670://Parchemin de persimol
				perso.setBenediction(10682);
				return true;
			
			case 8435://Ballon Rouge Magique
				SocketManager.sendPacketToMap(perso.getMap(), "GA;208;" + perso.getId() + ";" + perso.getCell().getId() + ",2906,11,8,1");
				return true;
				
			case 8624://Ballon Bleu Magique
				SocketManager.sendPacketToMap(perso.getMap(), "GA;208;" + perso.getId() + ";" + perso.getCell().getId() + ",2907,11,8,1");
				return true;
				
			case 8625://Ballon Vert Magique
				SocketManager.sendPacketToMap(perso.getMap(), "GA;208;" + perso.getId() + ";" + perso.getCell().getId() + ",2908,11,8,1");
				return true;
				
			case 8430://Ballon Jaune Magique
				SocketManager.sendPacketToMap(perso.getMap(), "GA;208;" + perso.getId() + ";" + perso.getCell().getId() + ",2909,11,8,1");
				return true;
			
			case 8621://Cawotte Maudite
				perso.set_gfxID(1109);
				SocketManager.GAME_SEND_ALTER_GM_PACKET(perso.getMap(), perso);
				return true;
				
			case 8626://Nisitik Miditik
				perso.set_gfxID(1046);	
				SocketManager.GAME_SEND_ALTER_GM_PACKET(perso.getMap(), perso);
				return true;
			
			case 10833://Chapain
				perso.set_gfxID(9001);	
				SocketManager.GAME_SEND_ALTER_GM_PACKET(perso.getMap(), perso);
				return true;
			
			case 10839://Monstre Pain
				perso.getMap().spawnNewGroup(true, perso.getCurCell().getId(), "2787,1,1000", "MiS = "+perso.getId());
				return true;
				
			case 8335://Cadeau 1
				Noel.getRandomObjectOne(perso);
				return true;
			case 8336://Cadeau 2
				Noel.getRandomObjectTwo(perso);
				return true;
			case 8337://Cadeau 3
				Noel.getRandomObjectTree(perso);
				return true;
			case 8339://Cadeau 4
				Noel.getRandomObjectFour(perso);
				return true;
			case 8340://Cadeau 5
				Noel.getRandomObjectFive(perso);
				return true;*/
			case 10912://Cadeau nowel 1
				return false;
			case 10913://Cadeau nowel 2
				return false;
			case 10914://Cadeau nowel 3
				return false;
				
		}
		return false;
	}
}
