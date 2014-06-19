package org.ancestra.evolutive.other;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.client.other.Stalk;
import org.ancestra.evolutive.common.ConditionParser;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.monster.MobGroup;
import org.ancestra.evolutive.entity.npc.NpcQuestion;
import org.ancestra.evolutive.fight.spell.Animation;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.house.House;
import org.ancestra.evolutive.job.JobStat;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.object.ObjectTemplate;
import org.ancestra.evolutive.object.SoulStone;

public class Action {

	private int id;
	private String args;
	private String condition;
	
	public Action(int id, String args, String condition) {
		this.id = id;
		this.args = args;
		this.condition = condition;
	}
	
	public int getId() {
		return id;
	}

	public void apply(Player perso, Player target, int itemID, int cellid)
	{
		if(perso == null)return;
		if(!this.condition.equalsIgnoreCase("") && !this.condition.equalsIgnoreCase("-1")&& !ConditionParser.validConditions(perso, this.condition))
		{
			SocketManager.GAME_SEND_Im_PACKET(perso, "119");
			return;
		}
		if(perso.getAccount().getGameClient() == null) return;
		GameClient out = perso.getAccount().getGameClient();	
		switch(this.id)
		{
			case -2://cr�er guilde
				if(perso.isAway())return;
				if(perso.getGuild() != null || perso.getGuildMember() != null)
				{
					SocketManager.GAME_SEND_gC_PACKET(perso, "Ea");
					return;
				}
				SocketManager.GAME_SEND_gn_PACKET(perso);
			break;
			case -1://Ouvrir banque
				//Sauvagarde du perso et des item avant.
				perso.save();
				if(perso.getDeshonor() >= 1) 
				{
					SocketManager.GAME_SEND_Im_PACKET(perso, "183");
					return;
				}
				int cost = perso.getBankCost();
				if(cost > 0)
				{
					long nKamas = perso.getKamas() - cost;
					if(nKamas <0)//Si le joueur n'a pas assez de kamas pour ouvrir la banque
					{
						SocketManager.GAME_SEND_Im_PACKET(perso, "1128;"+cost);
						return;
					}
					perso.setKamas(nKamas);
					SocketManager.GAME_SEND_STATS_PACKET(perso);
					SocketManager.GAME_SEND_Im_PACKET(perso, "020;"+cost);
				}
				SocketManager.GAME_SEND_ECK_PACKET(perso.getAccount().getGameClient(), 5, "");
				SocketManager.GAME_SEND_EL_BANK_PACKET(perso);
				perso.setAway(true);
				perso.setInBank(true);
			break;
			
			case 0://T�l�portation
				try
				{
					short newMapID = Short.parseShort(args.split(",",2)[0]);
					int newCellID = Integer.parseInt(args.split(",",2)[1]);
					
					perso.teleport(newMapID,newCellID);	
				}catch(Exception e ){return;};
			break;
			
			case 1://Discours NPC
				out = perso.getAccount().getGameClient();
				if(args.equalsIgnoreCase("DV"))
				{
					SocketManager.GAME_SEND_END_DIALOG_PACKET(out);
					perso.setIsTalkingWith(0);
				}else
				{
					int qID = -1;
					try
					{
						qID = Integer.parseInt(args);
					}catch(NumberFormatException e){};
					
					NpcQuestion  quest = World.data.getNpcQuestion(qID);
					if(quest == null)
					{
						SocketManager.GAME_SEND_END_DIALOG_PACKET(out);
						perso.setIsTalkingWith(0);
						return;
					}
					SocketManager.GAME_SEND_QUESTION_PACKET(out, quest.parseToDQPacket(perso));
				}
			break;
			
			case 4://Kamas
				try
				{
					int count = Integer.parseInt(args);
					long curKamas = perso.getKamas();
					long newKamas = curKamas + count;
					if(newKamas <0) newKamas = 0;
					perso.setKamas(newKamas);
					
					//Si en ligne (normalement oui)
					if(perso.isOnline())
						SocketManager.GAME_SEND_STATS_PACKET(perso);
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 5://objet
				try
				{
					int tID = Integer.parseInt(args.split(",")[0]);
					int count = Integer.parseInt(args.split(",")[1]);
					boolean send = true;
					if(args.split(",").length >2)send = args.split(",")[2].equals("1");
					
					//Si on ajoute
					if(count > 0)
					{
						ObjectTemplate T = World.data.getObjectTemplate(tID);
						if(T == null)return;
						Object O = T.createNewItem(count, false);
						//Si retourne true, on l'ajoute au monde
						if(perso.addObject(O, true))
							World.data.addObject(O, true);
					}else
					{
						perso.removeByTemplateID(tID,-count);
					}
					//Si en ligne (normalement oui)
					if(perso.isOnline())//on envoie le packet qui indique l'ajout//retrait d'un item
					{
						SocketManager.GAME_SEND_Ow_PACKET(perso);
						if(send)
						{
							if(count >= 0){
								SocketManager.GAME_SEND_Im_PACKET(perso, "021;"+count+"~"+tID);
							}
							else if(count < 0){
								SocketManager.GAME_SEND_Im_PACKET(perso, "022;"+-count+"~"+tID);
							}
						}
					}
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 6://Apprendre un m�tier
				try
				{
					int mID = Integer.parseInt(args);
					if(World.data.getMetier(mID) == null)return;
					// Si c'est un m�tier 'basic' :
					if(mID == 	2 || mID == 11 ||
					   mID == 13 || mID == 14 ||
					   mID == 15 || mID == 16 ||
					   mID == 17 || mID == 18 ||
					   mID == 19 || mID == 20 ||
					   mID == 24 || mID == 25 ||
					   mID == 26 || mID == 27 ||
					   mID == 28 || mID == 31 ||
					   mID == 36 || mID == 41 ||
					   mID == 56 || mID == 58 ||
					   mID == 60 || mID == 65)
					{
						if(perso.getMetierByID(mID) != null)//M�tier d�j� appris
						{
							SocketManager.GAME_SEND_Im_PACKET(perso, "111");
						}
						
						if(perso.totalJobBasic() > 2)//On compte les m�tiers d�ja acquis si c'est sup�rieur a 2 on ignore
						{
							SocketManager.GAME_SEND_Im_PACKET(perso, "19");
						}else//Si c'est < ou = � 2 on apprend
						{
							perso.learnJob(World.data.getMetier(mID));
						}
					}
					// Si c'est une specialisations 'FM' :
					if(mID == 	43 || mID == 44 ||
					   mID == 45 || mID == 46 ||
					   mID == 47 || mID == 48 ||
					   mID == 49 || mID == 50 ||
					   mID == 62 || mID == 63 ||
					   mID == 64)
					{
						//M�tier simple level 65 n�cessaire
						if(perso.getMetierByID(17) != null && perso.getMetierByID(17).get_lvl() >= 65 && mID == 43
						|| perso.getMetierByID(11) != null && perso.getMetierByID(11).get_lvl() >= 65 && mID == 44
						|| perso.getMetierByID(14) != null && perso.getMetierByID(14).get_lvl() >= 65 && mID == 45
						|| perso.getMetierByID(20) != null && perso.getMetierByID(20).get_lvl() >= 65 && mID == 46
						|| perso.getMetierByID(31) != null && perso.getMetierByID(31).get_lvl() >= 65 && mID == 47
						|| perso.getMetierByID(13) != null && perso.getMetierByID(13).get_lvl() >= 65 && mID == 48
						|| perso.getMetierByID(19) != null && perso.getMetierByID(19).get_lvl() >= 65 && mID == 49
						|| perso.getMetierByID(18) != null && perso.getMetierByID(18).get_lvl() >= 65 && mID == 50
						|| perso.getMetierByID(15) != null && perso.getMetierByID(15).get_lvl() >= 65 && mID == 62
						|| perso.getMetierByID(16) != null && perso.getMetierByID(16).get_lvl() >= 65 && mID == 63
						|| perso.getMetierByID(27) != null && perso.getMetierByID(27).get_lvl() >= 65 && mID == 64)
						{
							//On compte les specialisations d�ja acquis si c'est sup�rieur a 2 on ignore
							if(perso.getMetierByID(mID) != null)//M�tier d�j� appris
							{
								SocketManager.GAME_SEND_Im_PACKET(perso, "111");
							}
							
							if(perso.totalJobFM() > 2)//On compte les m�tiers d�ja acquis si c'est sup�rieur a 2 on ignore
							{
								SocketManager.GAME_SEND_Im_PACKET(perso, "19");
							}
							else//Si c'est < ou = � 2 on apprend
							{
								perso.learnJob(World.data.getMetier(mID));
								perso.getMetierByID(mID).addXp(perso, 582000);//Level 100 direct
							}	
						}else
						{
							SocketManager.GAME_SEND_Im_PACKET(perso, "12");
						}
					}
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 7://retour au point de sauvegarde
				perso.warpToSavePos();
			break;
			case 8://Ajouter une Stat
				try
				{
					int statID = Integer.parseInt(args.split(",",2)[0]);
					int number = Integer.parseInt(args.split(",",2)[1]);
					perso.getStats().addOneStat(statID, number);
					SocketManager.GAME_SEND_STATS_PACKET(perso);
					int messID = 0;
					switch(statID)
					{
						case Constants.STATS_ADD_INTE: messID = 14;break;
					}
					if(messID>0)
						SocketManager.GAME_SEND_Im_PACKET(perso, "0"+messID+";"+number);
				}catch(Exception e ){return;};
			break;
			case 9://Apprendre un sort
				try
				{
					int sID = Integer.parseInt(args);
					if(World.data.getSort(sID) == null)return;
					perso.learnSpell(sID,1, true,true, true);
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 10://Pain/potion/viande/poisson
				try
				{
					int min = Integer.parseInt(args.split(",",2)[0]);
					int max = Integer.parseInt(args.split(",",2)[1]);
					if(max == 0) max = min;
					int val = Formulas.getRandomValue(min, max);
					if(target != null)
					{
						if(target.getPdv() + val > target.getMaxPdv())val = target.getMaxPdv()-target.getPdv();
						target.setPdv(target.getPdv()+val);
						SocketManager.GAME_SEND_STATS_PACKET(target);
					}
					else
					{
						if(perso.getPdv() + val > perso.getMaxPdv())val = perso.getMaxPdv()-perso.getPdv();
						perso.setPdv(perso.getPdv()+val);
						SocketManager.GAME_SEND_STATS_PACKET(perso);
					}
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 11://Definir l'alignement
				try
				{
					byte newAlign = Byte.parseByte(args.split(",",2)[0]);
					boolean replace = Integer.parseInt(args.split(",",2)[1]) == 1;
					//Si le perso n'est pas neutre, et qu'on doit pas remplacer, on passe
					if(perso.getAlign() != Constants.ALIGNEMENT_NEUTRE && !replace)return;
					perso.modifAlignement(newAlign);
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			/* TODO: autres actions */
			case 12://Spawn d'un groupe de monstre
				try
				{
					boolean delObj = args.split(",")[0].equals("true");
					boolean inArena = args.split(",")[1].equals("true");

					if(inArena && !World.data.isArenaMap(perso.getMap().getId()))return;	//Si la map du personnage n'est pas class� comme �tant dans l'ar�ne

					SoulStone pierrePleine = (SoulStone) World.data.getObject(itemID);

					String groupData = pierrePleine.parseGroupData();
					String condition = "MiS = "+perso.getId();	//Condition pour que le groupe ne soit lan�able que par le personnage qui � utiliser l'objet
					perso.getMap().spawnNewGroup(true, perso.getCell(), groupData,condition);

					if(delObj)
					{
						perso.removeItem(itemID, 1, true, true);
					}
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
		    case 13: //Reset Carac
		        try
		        {
		          perso.getStats().addOneStat(125, -perso.getStats().getEffect(125));
		          perso.getStats().addOneStat(124, -perso.getStats().getEffect(124));
		          perso.getStats().addOneStat(118, -perso.getStats().getEffect(118));
		          perso.getStats().addOneStat(123, -perso.getStats().getEffect(123));
		          perso.getStats().addOneStat(119, -perso.getStats().getEffect(119));
		          perso.getStats().addOneStat(126, -perso.getStats().getEffect(126));
		          perso.addCapital((perso.getLevel() - 1) * 5 - perso.getCapital());

		          SocketManager.GAME_SEND_STATS_PACKET(perso);
		        }catch(Exception e){Log.addToLog(e.getMessage());};
		    break;
		    case 14://Ouvrir l'interface d'oublie de sort
		    	perso.setForgetingSpell(true);
				SocketManager.GAME_SEND_FORGETSPELL_INTERFACE('+', perso);
			break;
			case 15://T�l�portation donjon
				try
				{
					short newMapID = Short.parseShort(args.split(",")[0]);
					int newCellID = Integer.parseInt(args.split(",")[1]);
					int ObjetNeed = Integer.parseInt(args.split(",")[2]);
					int MapNeed = Integer.parseInt(args.split(",")[3]);
					if(ObjetNeed == 0)
					{
						//T�l�portation sans objets
						perso.teleport(newMapID,newCellID);
					}else if(ObjetNeed > 0)
					{
					if(MapNeed == 0)
					{
						//T�l�portation sans map
						perso.teleport(newMapID,newCellID);
					}else if(MapNeed > 0)
					{
					if (perso.hasItemTemplate(ObjetNeed, 1) && perso.getMap().getId() == MapNeed)
					{
						//Le perso a l'item
						//Le perso est sur la bonne map
						//On t�l�porte, on supprime apr�s
						perso.teleport(newMapID,newCellID);
						perso.removeByTemplateID(ObjetNeed, 1);
						SocketManager.GAME_SEND_Ow_PACKET(perso);
					}
					else if(perso.getMap().getId() != MapNeed)
					{
						//Le perso n'est pas sur la bonne map
						SocketManager.GAME_SEND_MESSAGE(perso, "Vous n'etes pas sur la bonne map du donjon pour etre teleporter.", "009900");
					}
					else
					{
						//Le perso ne poss�de pas l'item
						SocketManager.GAME_SEND_MESSAGE(perso, "Vous ne possedez pas la clef necessaire.", "009900");
					}
					}
					}
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 16://Ajout d'honneur HonorValue
				try
				{
					if(perso.getAlign() != 0)
					{
						int AddHonor = Integer.parseInt(args);
						int ActualHonor = perso.getHonor();
						perso.setHonor(ActualHonor+AddHonor);
					}
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 17://Xp m�tier JobID,XpValue
				try
				{
					int JobID = Integer.parseInt(args.split(",")[0]);
					int XpValue = Integer.parseInt(args.split(",")[1]);
					if(perso.getMetierByID(JobID) != null)
					{
						perso.getMetierByID(JobID).addXp(perso, XpValue);
					}
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 18://T�l�portation chez sois
				if(House.AlreadyHaveHouse(perso))//Si il a une maison
				{
					Object obj = World.data.getObject(itemID);
					if (perso.hasItemTemplate(obj.getTemplate().getId(), 1))
					{
						perso.removeByTemplateID(obj.getTemplate().getId(),1);
						House h = House.getHouseByPlayer(perso);
						if(h == null) return;
						perso.teleport((short)h.getToMapid(), h.getToCellid());
					}
				}
			break;
			case 19://T�l�portation maison de guilde (ouverture du panneau de guilde)
				SocketManager.GAME_SEND_GUILDHOUSE_PACKET(perso);
			break;
			case 20://+Points de sorts
				try
				{
					int pts = Integer.parseInt(args);
					if(pts < 1) return;
					perso.addSpellPoint(pts);
					SocketManager.GAME_SEND_STATS_PACKET(perso);
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 21://+Energie
				try
				{
					int Energy = Integer.parseInt(args);
					if(Energy < 1) return;
					
					int EnergyTotal = perso.getEnergy()+Energy;
					if(EnergyTotal > 10000) EnergyTotal = 10000;
					
					perso.setEnergy(EnergyTotal);
					SocketManager.GAME_SEND_STATS_PACKET(perso);
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 22://+Xp
				try
				{
					long XpAdd = Integer.parseInt(args);
					if(XpAdd < 1) return;
					
					long TotalXp = perso.getExperience()+XpAdd;
					perso.setExperience(TotalXp);
					SocketManager.GAME_SEND_STATS_PACKET(perso);
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 23://UnlearnJob
				try	{
					int Job = Integer.parseInt(args);
					if(Job < 1) return;
					JobStat m = perso.getMetierByID(Job);
					if(m == null) return;
					perso.unlearnJob(m.getId());
					SocketManager.GAME_SEND_STATS_PACKET(perso);
					perso.save();
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 24://SimpleMorph
				try
				{
					int morphID = Integer.parseInt(args);
					if(morphID < 0)return;
					perso.setGfx(morphID);
					SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(perso.getMap(), perso.getId());
					SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(perso.getMap(), perso);
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 25://SimpleUnMorph
				int UnMorphID = perso.getClasse().getId()*10 + perso.getSex();
				perso.setGfx(UnMorphID);
				SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(perso.getMap(), perso.getId());
				SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(perso.getMap(), perso);
			break;
			case 26://T�l�portation enclo de guilde (ouverture du panneau de guilde)
				SocketManager.GAME_SEND_GUILDENCLO_PACKET(perso);
			break;
			case 27://startFigthVersusMonstres args : monsterID,monsterLevel| ...
				String ValidMobGroup = "";
				try
		        {
					for(String MobAndLevel : args.split("\\|"))
					{
						int monsterID = -1;
						int monsterLevel = -1;
						String[] MobOrLevel = MobAndLevel.split(",");
						monsterID = Integer.parseInt(MobOrLevel[0]);
						monsterLevel = Integer.parseInt(MobOrLevel[1]);
						
						if(World.data.getMonstre(monsterID) == null || World.data.getMonstre(monsterID).getGradeByLevel(monsterLevel) == null)
						{
							if(Server.config.isDebug()) Log.addToLog("Monstre invalide : monsterID:"+monsterID+" monsterLevel:"+monsterLevel);
							continue;
						}
						ValidMobGroup += monsterID+","+monsterLevel+","+monsterLevel+";";
					}
					if(ValidMobGroup.isEmpty()) return;
					MobGroup group  = new MobGroup(perso.getMap().getNextObject(),perso.getMap(),perso.getCell(), ValidMobGroup);
					perso.getMap().startFigthVersusMonstres(perso, group);
		        }catch(Exception e){Log.addToLog(e.getMessage());};
			break;
			case 50://Traque
				if(perso.getStalk() == null)
				{
					Stalk traq = new Stalk(0, null);
					perso.setStalk(traq);
				}
				if(perso.getStalk().getTime() < System.currentTimeMillis() - 600000 || perso.getStalk().getTime() == 0)
				{
					Player tempP = null;
					int tmp = 15;
					int diff = 0;
					for(byte b = 0; b < 100; b++)
					{
					if(b == Server.config.getGameServer().getClients().size())break;
					GameClient GT = Server.config.getGameServer().getClients().get((int)b);
					Player P = GT.getPlayer();
					if(P == null || P == perso)continue;
					if(P.getAccount().getCurIp().compareTo(perso.getAccount().getCurIp()) == 0)continue;
					//SI pas s�riane ni neutre et si alignement oppos�
					if(P.getAlign() == perso.getAlign() || P.getAlign() == 0 || P.getAlign() == 3)continue;
					
					if(P.getLevel()>perso.getLevel())diff = P.getLevel() - perso.getLevel();
					if(perso.getLevel()>P.getLevel())diff = perso.getLevel() - P.getLevel();
					if(diff<tmp)tempP = P; tmp = diff;
					}
					if(tempP == null)
					{
						SocketManager.GAME_SEND_MESSAGE(perso, "Nous n'avons pas trouve de cible a ta hauteur. Reviens plus tard." , "000000");
						break;
					}
					
					
					SocketManager.GAME_SEND_MESSAGE(perso, "Vous etes desormais en chasse de "+tempP.getName()+ "", "000000");
					
					perso.getStalk().setTraque(tempP);
					perso.getStalk().setTime(System.currentTimeMillis());
					
					
					ObjectTemplate T = World.data.getObjectTemplate(10085);
					if(T == null)return;
					perso.removeByTemplateID(T.getId(),100);
					
					Object newObj = T.createNewItem(20, false);
					//On ajoute le nom du type � recherch�
					/*
					newObj.addTxtStat(962, Integer.toString(tempP.get_lvl()));
					newObj.addTxtStat(961, Integer.toString(tempP.getGrade()));
					
					int alignid = tempP.get_align();
					String align = "";
					switch(alignid)
					{
					case 0:
					align = "Neutre";
					case 1:
					align = "Bontarien";
					break;
					case 2:
					align = "Brakmarien";
					break;
					case 3:
					align = "S�riane";
					break;
					}
					newObj.addTxtStat(960, align);
					*/
					newObj.getTxtStats().put(989, tempP.getName());
					
					//Si retourne true, on l'ajoute au monde
					if(perso.addObject(newObj, true)){
						World.data.addObject(newObj, true);
			}else
			{
				perso.removeByTemplateID(T.getId(),20);
			}
			}
			else{
			SocketManager.GAME_SEND_MESSAGE(perso, "Thomas Sacre : Vous venez juste de signer un contrat, vous devez vous reposer." , "000000");
				}

			break;
			case 51://Cible sur la g�oposition
				String perr = "";
				
				perr = World.data.getObject(itemID).getTraquedName();
				if(perr == null)
				{
					break;	
				}
				Player cible = World.data.getPlayerByName(perr);
				if(cible==null)break;
				if(!cible.isOnline())
				{
					SocketManager.GAME_SEND_MESSAGE(perso, "Ce joueur n'est pas connecte." , "000000");
					break;
				}
				SocketManager.GAME_SEND_FLAG_PACKET(perso, cible);
			break;
			case 52://recompenser pour traque
				if(perso.getStalk() != null && perso.getStalk().getTime() == -2)
				{
					int xp = Formulas.getTraqueXP(perso.getLevel());
					perso.addXp(xp);
					perso.setStalk(null);//On supprime la traque
					SocketManager.GAME_SEND_MESSAGE(perso, "Vous venez de recevoir "+xp+" points d'experiences." , "000000");
				}
				else
				{
					SocketManager.GAME_SEND_MESSAGE(perso, "Thomas Sacre : Reviens me voir quand tu aura abatu un ennemi." , "000000");
				}

			break;
			case 101://Arriver sur case de mariage
				if((perso.getSex() == 0 && perso.getCell().getId() == 282) || (perso.getSex() == 1 && perso.getCell().getId() == 297))
				{
					World.data.AddMarried(perso.getSex(), perso);
				}else 
				{
					SocketManager.GAME_SEND_Im_PACKET(perso, "1102");
				}
			break;
			case 102://Marier des personnages
				World.data.PriestRequest(perso, perso.getMap(), perso.getIsTalkingWith());
			break;
			case 103://Divorce
				if(perso.getKamas() < 50000)
				{
					return;
				}else
				{
					perso.setKamas(perso.getKamas()-50000);
					Player wife = World.data.getPlayer(perso.getWife());
					wife.Divorce();
					perso.Divorce();
				}
			break;
			case 228://Faire animation Hors Combat
				try
				{
					int AnimationId = Integer.parseInt(args);
					Animation animation = World.data.getAnimation(AnimationId);
					if(perso.getFight() != null) return;
					perso.changeOrientation(1);
					SocketManager.GAME_SEND_GA_PACKET_TO_MAP(perso.getMap(), "0", 228, perso.getId()+";"+cellid+","+Animation.parseToGA(animation), "");
				}catch(Exception e){Log.addToLog(e.getMessage());};
			break;
            case 229 :
                teleportToAstrub(perso);
                break;
			default:
				Log.addToLog("Action ID="+this.id+" non implantee");
			break;
		}
	}

    private void teleportToAstrub(Player player){
        player.teleport(player.getClasse().getAstrubStartMap(),player.getClasse().getAstrubStartCell());
    }
}
