package org.ancestra.evolutive.job;

import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.common.Pathfinding;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.monster.MobGroup;
import org.ancestra.evolutive.fight.spell.SpellEffect;
import org.ancestra.evolutive.game.GameAction;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.InteractiveObject;
import org.ancestra.evolutive.object.Objet;
import org.ancestra.evolutive.object.Objet.ObjTemplate;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class JobAction {
	
	private int id;
	private int min = 1;
	private int max = 1;
	private boolean isCraft;
	private int chan = 100;
	private int time = 0;
	private int xpWin = 0;
	private long startTime;
	public Map<Integer,Integer> ingredients = new TreeMap<Integer,Integer>();
	public Map<Integer,Integer> lastCraft = new TreeMap<Integer,Integer>();
	public Player player;
	public String data = "";
	public boolean broke = false;
	public boolean broken = false;
	private int reConfigingRunes = -1;
	public boolean isRepeat = false;
	private JobStat SM;
	private JobCraft jobCraft;
	
	public JobAction(int sk, int min, int max, boolean craft, int arg, int xpWin)
	{
		this.id = sk;
		this.min = min;
		this.max = max;
		this.isCraft = craft;
		if(craft)
			this.chan = arg;
		else 
			this.time = arg;
		this.xpWin = xpWin;	
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getMin()	{
		return this.min;
	}
	
	public int getMax()	{
		return this.max;
	}
	
	public boolean isCraft() {
		return this.isCraft;
	}
	
	public int getChance() {
		return this.chan;
	}
	
	public int getTime() {
		return this.time;
	}
	
	public int getXpWin() {
		return this.xpWin;
	}
	
	public JobStat getJobStat() {
		return this.SM;
	}
	
	public JobCraft getJobCraft() {
		return this.jobCraft;
	}
	
	public void startCraft(Player P) {
		this.jobCraft = new JobCraft(this, P);
		//this.craftTimer.start();//on retarde le lancement du craft en cas de packet EMR (craft auto)
	}
	
	public void startAction(Player P, InteractiveObject IO, GameAction GA, Case cell, JobStat SM)
	{
		this.SM = SM;
		this.player = P;
		if(P.getObjetByPos(Constants.ITEM_POS_ARME) != null && SM.getTemplate().getId() == 36)
		{
			if(World.data.getMetier(36).isValidTool(P.getObjetByPos(Constants.ITEM_POS_ARME).getTemplate().getID()))
			{
				int dist = Pathfinding.getDistanceBetween(P.getCurMap(), P.getCurCell().getId(), cell.getId());
				int distItem = 0;
				switch(P.getObjetByPos(Constants.ITEM_POS_ARME).getTemplate().getID())
				{
					case 8541://1 to 1
					case 6661:
					case 596:
						distItem = 1;
						break;
					case 1866://1 to 3
						distItem = 3;
						break;
					case 1865://1 to 4
					case 1864:	
						distItem = 4;
						break;
					case 1867://1 to 5
					case 2188:
						distItem = 5;
						break;
					case 1863://1 to 6
					case 1862:	
						distItem = 6;
						break;
					case 1868://1 to 7
						distItem = 7;
						break;
					case 1861://1 to 8
					case 1860:
						distItem = 8;
						break;
					case 2366://1 to 9
						distItem = 9;
						break;		
				}
				if(distItem < dist)
				{
					SocketManager.GAME_SEND_MESSAGE(P, "Vous �tes trop loin pour pouvoir p�cher ce poisson !", Server.config.getMotdColor());	
					SocketManager.GAME_SEND_GA_PACKET(P.getAccount().getGameClient(), "", "0", "", "");
					P.setCurJobAction(null);
					P.setInAction(false);
					//SM.setOnAction(false);
					return;
				}
			}
		}
		if(!this.isCraft)
		{
			IO.setInteractive(false);
			IO.setState(JobConstant.IOBJECT_STATE_EMPTYING);
			SocketManager.GAME_SEND_GA_PACKET_TO_MAP(P.getCurMap(),""+GA.getId(), 501, P.getUUID()+"", cell.getId()+","+this.time);
			SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(P.getCurMap(),cell);
			this.startTime = System.currentTimeMillis()+this.time;//pour eviter le cheat
		}else
		{
			P.setAway(true);
			IO.setState(JobConstant.IOBJECT_STATE_EMPTYING);//FIXME trouver la bonne valeur
			P.setCurJobAction(this);
			SocketManager.GAME_SEND_ECK_PACKET(P, 3, this.min+";"+this.id);//this.min => Nbr de Case de l'interface
			SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(P.getCurMap(), cell);
		}
	}
	
	public void endAction(Player P, InteractiveObject IO, GameAction GA, Case cell)
	{
		P.setInAction(false);
		if(!this.isCraft)
		{
			//Si recue trop tot, on ignore
			if(this.startTime - System.currentTimeMillis() > 500)
				return;
			IO.setState(3);
			IO.startTimer();
			//Packet GDF (changement d'�tat de l'IO)
			SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(P.getCurMap(), cell);
			//boolean special = Formulas.getRandomValue(0, 99)==0;//Restriction de niveau ou pas ? Useless.
			//On ajoute X ressources
			int qua = (this.max>this.min?Formulas.getRandomValue(this.min, this.max):this.min);
			int tID = JobConstant.getObjectByJobSkill(this.id);
							
			ObjTemplate T = World.data.getObjTemplate(tID);
			if(T == null)
				return;
			Objet O = T.createNewItem(qua, false);
			//Si retourne true, on l'ajoute au monde
			if(P.addObjet(O, true))
				World.data.addObjet(O, true);
			SocketManager.GAME_SEND_IQ_PACKET(P,P.getUUID(),qua);
			SocketManager.GAME_SEND_Ow_PACKET(P);
			/* 
			int maxPercent = 20+(P.getMetierBySkill(this.id).get_lvl()-20);
			System.out.println("Level : "+P.getMetierBySkill(this.id).get_lvl()+" | Max : "+ maxPercent+" | "+Formulas.getRandomValue(1, maxPercent));
			if(P.getMetierBySkill(this.id).get_lvl() >= 20 && Formulas.getRandomValue(1, maxPercent) == maxPercent)*/
			if(P.getMetierBySkill(this.id).get_lvl() >= 20 && Formulas.getRandomValue(1, 100) > 89)
			{
                int[][] protectors = JobConstant.JOB_PROTECTORS;
                for(int i = 0; i < protectors.length; i++)
                {
                  	if(tID == protectors[i][1])
                   	{
                  		int monsterId = protectors[i][0];
                     	int monsterLvl = JobConstant.getProtectorLvl(P.getLevel());            
                      	P.getCurMap().startFightVersusProtectors(P, new MobGroup(P.getCurMap().getNextObject(),P.getCurMap(), cell, monsterId+","+monsterLvl+","+monsterLvl));
                        break;
                 	}
                }
			}
		}
		P.setAway(false);
	}
	
	public void modifIngredient(Player P,int guid, int qua)	{
		//on prend l'ancienne valeur
		int q = this.ingredients.get(guid)==null?0:this.ingredients.get(guid);
		//on enleve l'entr�e dans la Map
		this.ingredients.remove(guid);
		//on ajoute (ou retire, en fct du signe) X objet
		q += qua;
		if(q > 0) {
			this.ingredients.put(guid,q);
			SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(P,'O', "+", guid+"|"+q);
		}else {
			SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(P,'O', "-", guid+"");
		}
	}

	public void craft()	
	{
		if(!this.isCraft)
			return;
		boolean signed = false;//TODO
		//Si Forgemagie
		if(this.id == 1 || this.id == 113 || this.id == 115 || this.id == 116 || this.id == 117 || this.id == 118 || this.id == 119 || this.id == 120 || (this.id >= 163 && this.id <= 169))
		{
			doFmCraft();
			return;
		}
		
		Map<Integer,Integer> items = new TreeMap<Integer,Integer>();
		//on retire les items mis en ingr�dients
		for(Entry<Integer,Integer> e : this.ingredients.entrySet())
		{
			//Si le joueur n'a pas l'objet
			if(!this.player.hasItemGuid(e.getKey()))
			{
				SocketManager.GAME_SEND_Ec_PACKET(this.player,"EI");
				Log.addToSockLog("/!\\ "+this.player.getName()+" essaye de crafter avec un objet qu'il n'a pas");
				return;
			}
			//Si l'objet n'existe pas
			Objet obj = World.data.getObjet(e.getKey());
			if(obj == null)
			{
				SocketManager.GAME_SEND_Ec_PACKET(this.player,"EI");
				Log.addToSockLog("/!\\ "+this.player.getName()+" essaye de crafter avec un objet qui n'existe pas");
				return;
			}
			//Si la quantit� est trop faible
			if(obj.getQuantity() < e.getValue())
			{
				SocketManager.GAME_SEND_Ec_PACKET(this.player,"EI");
				Log.addToSockLog("/!\\ "+this.player.getName()+" essaye de crafter avec un objet dont la quantite est trop faible");
				return;
			}
			//On calcule la nouvelle quantit�
			int newQua = obj.getQuantity() - e.getValue();
			
			if(newQua < 0)
				return;//ne devrais pas arriver
			if(newQua == 0)
			{
				this.player.removeItem(e.getKey());
				World.data.removeItem(e.getKey());
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, e.getKey());
			}else
			{
				obj.setQuantity(newQua);
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, obj);
			}
			//on ajoute le couple tID/qua a la liste des ingr�dients pour la recherche
			items.put(obj.getTemplate().getID(), e.getValue());
		}
		//On retire les items a ignorer pour la recette
		//Rune de signature
		if(items.containsKey(7508))
			signed = true;
		items.remove(7508);
		//Fin des items a retirer
		SocketManager.GAME_SEND_Ow_PACKET(this.player);
		
		//On trouve le template corespondant si existant
		JobStat SM = this.player.getMetierBySkill(this.id);
		int tID = World.data.getObjectByIngredientForJob(SM.getTemplate().getListBySkill(this.id),items);
		//Recette non existante ou pas adapt� au m�tier
		if(tID == -1 || !SM.getTemplate().canCraft(this.id, tID))
		{
			SocketManager.GAME_SEND_Ec_PACKET(this.player,"EI");
			SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(),this.player.getUUID(),"-");
			this.ingredients.clear();	
			return;
		}
		
		int chan =  JobConstant.getChanceByNbrCaseByLvl(SM.get_lvl(),this.ingredients.size());
		int jet = Formulas.getRandomValue(1, 100);
		boolean success = chan >= jet;
		if(!success)//Si echec
		{
			SocketManager.GAME_SEND_Ec_PACKET(this.player,"EF");
			SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(),this.player.getUUID(),"-"+tID);
			SocketManager.GAME_SEND_Im_PACKET(this.player, "0118");
		}else
		{
			Objet newObj = World.data.getObjTemplate(tID).createNewItem(1, false);
			//Si sign� on ajoute la ligne de Stat "Fabriqu� par:"
			if(signed)newObj.addTxtStat(988, this.player.getName());
			boolean add = true;
			int guid = newObj.getGuid();
			
			for(Entry<Integer, Objet> entry : this.player.getItems().entrySet())
			{
				Objet obj = entry.getValue();
				if(obj.getTemplate().getID() == newObj.getTemplate().getID() && obj.getStats().isSameStats(newObj.getStats()) && obj.getPosition() == Constants.ITEM_POS_NO_EQUIPED)//Si meme Template et Memes Stats et Objet non �quip�
				{
					obj.setQuantity(obj.getQuantity()+newObj.getQuantity());//On ajoute QUA item a la quantit� de l'objet existant
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player,obj);
					add = false;
					guid = obj.getGuid();
				}
			}
			if(add)
			{
				this.player.getItems().put(newObj.getGuid(), newObj);
				SocketManager.GAME_SEND_OAKO_PACKET(this.player,newObj);
				World.data.addObjet(newObj, true);
			}
			//on envoie les Packets
			SocketManager.GAME_SEND_Ow_PACKET(this.player);
			SocketManager.GAME_SEND_Em_PACKET(this.player,"KO+"+guid+"|1|"+tID+"|"+newObj.parseStatsString().replace(";","#"));
			SocketManager.GAME_SEND_Ec_PACKET(this.player,"K;"+tID);
			SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(),this.player.getUUID(),"+"+tID);
		}
		
		//On donne l'xp
		int winXP =  Formulas.calculXpWinCraft(SM.get_lvl(),this.ingredients.size()) * Server.config.getRateXpJob();
		if(success)
		{
			SM.addXp(this.player,winXP);
			ArrayList<JobStat> SMs = new ArrayList<JobStat>();
			SMs.add(SM);
			SocketManager.GAME_SEND_JX_PACKET(this.player, SMs);
		}		
		this.lastCraft.clear();
		this.lastCraft.putAll(this.ingredients);
		this.ingredients.clear();
	}
		
	public void putLastCraftIngredients() {
		if (this.player == null)
			return;
		if (this.lastCraft == null)
			return;
		if (!this.ingredients.isEmpty())
			return;//OffiLike, mais possible de faire un truc plus propre en enlevant les objets pr�sent et en rajoutant ceux de la recette
		this.ingredients.clear();
		this.ingredients.putAll(this.lastCraft);
		for (Entry<Integer, Integer> e : this.ingredients.entrySet())
		{
			if (World.data.getObjet(e.getKey()) == null)
				return;
			if (World.data.getObjet(e.getKey()).getQuantity() < e.getValue())
				return;
			SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player, 'O', "+", e.getKey() + "|" + e.getValue());
		}
	}
	
	public void resetCraft()
	{
		this.ingredients.clear();
		this.lastCraft.clear();
		//this.SM.setOnAction(false);
	}
		
	/********************************************** FM SYSTEM ********************************************************/
	//TODO: Refresh des runes dans le bloc gauche du panel FM et non fouttre un clear directement.
	private void doFmCraft() {
		boolean isSigningRune = false;
		Objet objectFm = null, signingRune = null, runeOrPotion = null;
		int lvlElementRune = 0, statsID = -1, lvlQuaStatsRune = 0, statsAdd = 0, deleteID = -1, poid = 0;
		boolean bonusRune = false;
		String statsObjectFm = "-1";
		for (int idIngredient : this.ingredients.keySet()) {
			Objet ing = World.data.getObjet(idIngredient);
			if (ing == null || !this.player.hasItemGuid(idIngredient)) {
				SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
				SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getUUID(), "-");
				this.ingredients.clear();
				return;
			}
			int templateID = ing.getTemplate().getID();
			switch (templateID) {
				case 1333 :
					statsID = 99;
					lvlElementRune = ing.getTemplate().getLevel();
					runeOrPotion = ing;
					break;
				case 1335 :
					statsID = 96;
					lvlElementRune = ing.getTemplate().getLevel();
					runeOrPotion = ing;
					break;
				case 1337 :
					statsID = 98;
					lvlElementRune = ing.getTemplate().getLevel();
					runeOrPotion = ing;
					break;
				case 1338 :
					statsID = 97;
					lvlElementRune = ing.getTemplate().getLevel();
					runeOrPotion = ing;
					break;
				case 1340 :
					statsID = 97;
					lvlElementRune = ing.getTemplate().getLevel();
					runeOrPotion = ing;
					break;
				case 1341 :
					statsID = 96;
					lvlElementRune = ing.getTemplate().getLevel();
					runeOrPotion = ing;
					break;
				case 1342 :
					statsID = 98;
					lvlElementRune = ing.getTemplate().getLevel();
					runeOrPotion = ing;
					break;
				case 1343 :
					statsID = 99;
					lvlElementRune = ing.getTemplate().getLevel();
					runeOrPotion = ing;
					break;
				case 1345 :
					statsID = 99;
					lvlElementRune = ing.getTemplate().getLevel();
					runeOrPotion = ing;
					break;
				case 1346 :
					statsID = 96;
					lvlElementRune = ing.getTemplate().getLevel();
					runeOrPotion = ing;
					break;
				case 1347 :
					statsID = 98;
					lvlElementRune = ing.getTemplate().getLevel();
					runeOrPotion = ing;
					break;
				case 1348 :
					statsID = 97;
					lvlElementRune = ing.getTemplate().getLevel();
					runeOrPotion = ing;
					break;
				case 1519 :
					runeOrPotion = ing;
					statsObjectFm = "76";
					statsAdd = 1;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1521 :
					runeOrPotion = ing;
					statsObjectFm = "7c";
					statsAdd = 1;
					poid = 6;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1522 :
					runeOrPotion = ing;
					statsObjectFm = "7e";
					statsAdd = 1;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1523 :
					runeOrPotion = ing;
					statsObjectFm = "7d";
					statsAdd = 3;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1524 :
					runeOrPotion = ing;
					statsObjectFm = "77";
					statsAdd = 1;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1525 :
					runeOrPotion = ing;
					statsObjectFm = "7b";
					statsAdd = 1;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1545 :
					runeOrPotion = ing;
					statsObjectFm = "76";
					statsAdd = 3;
					poid = 3;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1546 :
					runeOrPotion = ing;
					statsObjectFm = "7c";
					statsAdd = 3;
					poid = 18;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1547 :
					runeOrPotion = ing;
					statsObjectFm = "7e";
					statsAdd = 3;
					poid = 3;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1548 :
					runeOrPotion = ing;
					statsObjectFm = "7d";
					statsAdd = 10;
					poid = 10;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1549 :
					runeOrPotion = ing;
					statsObjectFm = "77";
					statsAdd = 3;
					poid = 3;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1550 :
					runeOrPotion = ing;
					statsObjectFm = "7b";
					statsAdd = 3;
					poid = 10;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1551 :
					runeOrPotion = ing;
					statsObjectFm = "76";
					statsAdd = 10;
					poid = 10;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1552 :
					runeOrPotion = ing;
					statsObjectFm = "7c";
					statsAdd = 10;
					poid = 50;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1553 :
					runeOrPotion = ing;
					statsObjectFm = "7e";
					statsAdd = 10;
					poid = 10;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1554 :
					runeOrPotion = ing;
					statsObjectFm = "7d";
					statsAdd = 30;
					poid = 10;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1555 :
					runeOrPotion = ing;
					statsObjectFm = "77";
					statsAdd = 10;
					poid = 10;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1556 :
					runeOrPotion = ing;
					statsObjectFm = "7b";
					statsAdd = 10;
					poid = 10;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1557 :
					runeOrPotion = ing;
					statsObjectFm = "6f";
					statsAdd = 1;
					poid = 100;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 1558 :
					runeOrPotion = ing;
					statsObjectFm = "80";
					statsAdd = 1;
					poid = 90;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7433 :
					runeOrPotion = ing;
					statsObjectFm = "73";
					statsAdd = 1;
					poid = 30;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7434 :
					runeOrPotion = ing;
					statsObjectFm = "b2";
					statsAdd = 1;
					poid = 20;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7435 :
					runeOrPotion = ing;
					statsObjectFm = "70";
					statsAdd = 1;
					poid = 20;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7436 :
					runeOrPotion = ing;
					statsObjectFm = "8a";
					statsAdd = 1;
					poid = 2;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7437 :
					runeOrPotion = ing;
					statsObjectFm = "dc";
					statsAdd = 1;
					poid = 2;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7438 :
					runeOrPotion = ing;
					statsObjectFm = "75";
					statsAdd = 1;
					poid = 50;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7442 :
					runeOrPotion = ing;
					statsObjectFm = "b6";
					statsAdd = 1;
					poid = 30;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7443 :
					runeOrPotion = ing;
					statsObjectFm = "9e";
					statsAdd = 10;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7444 :
					runeOrPotion = ing;
					statsObjectFm = "9e";
					statsAdd = 30;
					poid = 1; 
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7445 :
					runeOrPotion = ing;
					statsObjectFm = "9e";
					statsAdd = 100;
					poid = 1; 
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7446 :
					runeOrPotion = ing;
					statsObjectFm = "e1";
					statsAdd = 1;
					poid = 15;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7447 :
					runeOrPotion = ing;
					statsObjectFm = "e2";
					statsAdd = 1;
					poid = 2;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7448 :
					runeOrPotion = ing;
					statsObjectFm = "ae";
					statsAdd = 10;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7449 :
					runeOrPotion = ing;
					statsObjectFm = "ae";
					statsAdd = 30;
					poid = 3;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7450 :
					runeOrPotion = ing;
					statsObjectFm = "ae";
					statsAdd = 100;
					poid = 10;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7451 :
					runeOrPotion = ing;
					statsObjectFm = "b0";
					statsAdd = 1;
					poid = 5;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7452 :
					runeOrPotion = ing;
					statsObjectFm = "f3";
					statsAdd = 1;
					poid = 4;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7453 :
					runeOrPotion = ing;
					statsObjectFm = "f2";
					statsAdd = 1;
					poid = 4;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7454 :
					runeOrPotion = ing;
					statsObjectFm = "f1";
					statsAdd = 1;
					poid = 4;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7455 :
					runeOrPotion = ing;
					statsObjectFm = "f0";
					statsAdd = 1;
					poid = 4;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7456 :
					runeOrPotion = ing;
					statsObjectFm = "f4";
					statsAdd = 1;
					poid = 4;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7457 :
					runeOrPotion = ing;
					statsObjectFm = "d5";
					statsAdd = 1;
					poid = 5;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7458 :
					runeOrPotion = ing;
					statsObjectFm = "d4";
					statsAdd = 1;
					poid = 5;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7459 :
					runeOrPotion = ing;
					statsObjectFm = "d2";
					statsAdd = 1;
					poid = 5;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7460 :
					runeOrPotion = ing;
					statsObjectFm = "d6";
					statsAdd = 1;
					poid = 5;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7560 :
					runeOrPotion = ing;
					statsObjectFm = "d3";
					statsAdd = 1;
					poid = 5;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 8379 :
					runeOrPotion = ing;
					statsObjectFm = "7d";
					statsAdd = 10;
					poid = 10;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 10662 :
					runeOrPotion = ing;
					statsObjectFm = "b0";
					statsAdd = 3;
					poid = 15;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 7508 :
					isSigningRune = true;
					signingRune = ing;
					break;
				case 11118 :
					bonusRune = true;
					runeOrPotion = ing;
					statsObjectFm = "76";
					statsAdd = 15;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 11119 :
					bonusRune = true;
					runeOrPotion = ing;
					statsObjectFm = "7c";
					statsAdd = 15;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 11120 :
					bonusRune = true;
					runeOrPotion = ing;
					statsObjectFm = "7e";
					statsAdd = 15;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 11121 :
					bonusRune = true;
					runeOrPotion = ing;
					statsObjectFm = "7d";
					statsAdd = 45;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 11122 :
					bonusRune = true;
					runeOrPotion = ing;
					statsObjectFm = "77";
					statsAdd = 15;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 11123 :
					bonusRune = true;
					runeOrPotion = ing;
					statsObjectFm = "7b";
					statsAdd = 15;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 11124 :
					bonusRune = true;
					runeOrPotion = ing;
					statsObjectFm = "b0";
					statsAdd = 10;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 11125 :
					bonusRune = true;
					runeOrPotion = ing;
					statsObjectFm = "73";
					statsAdd = 3;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 11126 :
					bonusRune = true;
					runeOrPotion = ing;
					statsObjectFm = "b2";
					statsAdd = 5;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 11127 :
					bonusRune = true;
					runeOrPotion = ing;
					statsObjectFm = "70";
					statsAdd = 5;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 11128 :
					bonusRune = true;
					runeOrPotion = ing;
					statsObjectFm = "8a";
					statsAdd = 10;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				case 11129 :
					bonusRune = true;
					runeOrPotion = ing;
					statsObjectFm = "dc";
					statsAdd = 5;
					poid = 1;
					lvlQuaStatsRune = ing.getTemplate().getLevel();
					break;
				default :
					int type = ing.getTemplate().getType();
					if ((type >= 1 && type <= 11) || (type >= 16 && type <= 22) || type == 81 || type == 102 || type == 114
					|| ing.getTemplate().getPACost() > 0) {
						objectFm = ing;
						SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK_FM(this.player.getAccount().getGameClient(), 'O',"+", objectFm.getGuid() + "|" + 1);
						deleteID = idIngredient;
						Objet newObj = Objet.getCloneObjet(objectFm, 1);
						if (objectFm.getQuantity() > 1) {
							int newQuant = objectFm.getQuantity() - 1;
							objectFm.setQuantity(newQuant);
							SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, objectFm);
							break;
						} else {
							World.data.removeItem(idIngredient);
							this.player.removeItem(idIngredient);
							SocketManager.GAME_SEND_DELETE_STATS_ITEM_FM(this.player, idIngredient);
						}
						objectFm = newObj;
					}
			}
		}
		JobStat job = this.player.getMetierBySkill(this.id);
		job.addXp(this.player, (int) (Server.config.getRateXpJob() + 9.0 / 10.0) * 10);
		if (job == null || objectFm == null || runeOrPotion == null) {
			SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
			SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getUUID(), "-");
			this.ingredients.clear();
			return;
		}
		if (deleteID != -1) {
			this.ingredients.remove(deleteID);
		}
		ObjTemplate objTemplate = objectFm.getTemplate();
		int chance = 0;
		int lvlJob = job.get_lvl();
		int objTemaplateID = objTemplate.getID();
		String statStringObj = objectFm.parseStatsString();
		if (lvlElementRune > 0 && lvlQuaStatsRune == 0) {
			chance = Formulas.calculChanceByElement(lvlJob, objTemplate.getLevel(), lvlElementRune);
			if (chance > 100 - (lvlJob / 20))
				chance = 100 - (lvlJob / 20);
			if (chance < (lvlJob / 20))
				chance = (lvlJob / 20);
		} else if (lvlQuaStatsRune > 0 && lvlElementRune == 0) {
			int currentWeightTotal = 1;
			int currentWeightStats = 1;
			if (!statStringObj.isEmpty()) {
				currentWeightTotal = currentTotalWeigthBase(statStringObj, objectFm);
				currentWeightStats = currentWeithStats(objectFm, statsObjectFm);
			}
			int currentTotalBase = WeithTotalBase(objTemaplateID);
			if (currentTotalBase < 0) {
				currentTotalBase = 0;
			}
			if (currentWeightStats < 0) {
				currentWeightStats = 0;
			}
			if (currentWeightTotal < 0) {
				currentWeightTotal = 0;
			}
			float coef = 1;
			int baseStats = Job.viewBaseStatsItem(objectFm, statsObjectFm);
			int currentStats = Job.viewActualStatsItem(objectFm, statsObjectFm);
			if (baseStats == 1 && currentStats == 1 || baseStats == 1 && currentStats == 0) {
				coef = 1.0f;
			} else if (baseStats == 2 && currentStats == 2) {
				coef = 0.50f;
			} else if (baseStats == 0 && currentStats == 0 || baseStats == 0 && currentStats == 1) {
				coef = 0.25f;
			}
			if (Job.getActualJet(objectFm, statsObjectFm) >= getStatBaseMaxs(objectFm.getTemplate(), statsObjectFm))
				coef = 0.15f;
			int diff = (int) (currentTotalBase * 1.3f) - currentWeightTotal;
			chance = Formulas.chanceFM(currentTotalBase, currentWeightTotal, currentWeightStats, poid, diff, coef);
			if (bonusRune)
				chance += 20;
			if (chance < 1)
				chance = 1;
			else if (chance > 100)
				chance = 100;
			
		}
		int aleatoryChance = Formulas.getRandomValue(1, 100);
		boolean sucess = chance >= aleatoryChance;
		if (!sucess) { // Si il n'a pas r�ussi
			if (signingRune != null) {
				int newQua = signingRune.getQuantity() - 1;
				if (newQua <= 0) {
					this.player.removeItem(signingRune.getGuid());
					World.data.removeItem(signingRune.getGuid());
					SocketManager.GAME_SEND_DELETE_STATS_ITEM_FM(this.player, signingRune.getGuid());
				} else {
					signingRune.setQuantity(newQua);
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, signingRune);
				}
			}
			if (runeOrPotion != null) {
				int newQua = runeOrPotion.getQuantity() - 1;
				if (newQua <= 0) {
					this.player.removeItem(runeOrPotion.getGuid());
					World.data.removeItem(runeOrPotion.getGuid());
					SocketManager.GAME_SEND_DELETE_STATS_ITEM_FM(this.player, runeOrPotion.getGuid());
				} else {
					runeOrPotion.setQuantity(newQua);
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, runeOrPotion);
				}
			}
				World.data.addObjet(objectFm, true);
				this.player.addObjet(objectFm);
				if (!statStringObj.isEmpty()) {
					String statsStr = objectFm.parseStringStatsEC_FM(objectFm, poid);
					objectFm.clearStats();
					objectFm.parseStringToStats(statsStr);
				}
				SocketManager.GAME_SEND_OAKO_PACKET(this.player, objectFm);
				SocketManager.GAME_SEND_Ow_PACKET(this.player);
				
				String data = objectFm.getGuid() + "|1|" + objectFm.getTemplate().getID() + "|" + objectFm.parseStatsString();
				if (!this.isRepeat)
					this.reConfigingRunes = -1;
				if (this.reConfigingRunes != 0 || this.broken)
					SocketManager.GAME_SEND_EXCHANGE_MOVE_OK_FM(this.player, 'O', "+", data);
				this.data = data;
			
			
			SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getUUID(), "-" + objTemaplateID);
			SocketManager.GAME_SEND_Ec_PACKET(this.player, "EF");
			SocketManager.GAME_SEND_Im_PACKET(this.player, "0183");
		} else {// Si r�ussite :)
			int coef = 0;
			if (lvlElementRune == 1)
				coef = 50;
			else if (lvlElementRune == 25)
				coef = 65;
			else if (lvlElementRune == 50)
				coef = 85;
			if (isSigningRune) {
				objectFm.addTxtStat(985, this.player.getName());
			}
			if (lvlElementRune > 0 && lvlQuaStatsRune == 0) {
				for (SpellEffect effect : objectFm.getEffects()) {
					if (effect.getEffectID() != 100)
						continue;
					String[] infos = effect.getArgs().split(";");
					try {
						int min = Integer.parseInt(infos[0], 16);
						int max = Integer.parseInt(infos[1], 16);
						int newMin = ((min * coef) / 100);
						int newMax = ((max * coef) / 100);
						if (newMin == 0)
							newMin = 1;
						String newRange = "1d" + (newMax - newMin + 1) + "+" + (newMin - 1);
						String newArgs = Integer.toHexString(newMin) + ";" + Integer.toHexString(newMax) + ";-1;-1;0;"
						+ newRange;
						effect.setArgs(newArgs);
						effect.setEffectID(statsID);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (lvlQuaStatsRune > 0 && lvlElementRune == 0) {
				boolean negative = false;
				int currentStats = Job.viewActualStatsItem(objectFm, statsObjectFm);
				if (currentStats == 2) {
					if (statsObjectFm.compareTo("7b") == 0) {
						statsObjectFm = "98";
						negative = true;
					}
					if (statsObjectFm.compareTo("77") == 0) {
						statsObjectFm = "9a";
						negative = true;
					}
					if (statsObjectFm.compareTo("7e") == 0) {
						statsObjectFm = "9b";
						negative = true;
					}
					if (statsObjectFm.compareTo("76") == 0) {
						statsObjectFm = "9d";
						negative = true;
					}
					if (statsObjectFm.compareTo("7c") == 0) {
						statsObjectFm = "9c";
						negative = true;
					}
					if (statsObjectFm.compareTo("7d") == 0) {
						statsObjectFm = "99";
						negative = true;
					}
				}
				if (currentStats == 1 || currentStats == 2) {
					String statsStr = objectFm.parseFMStatsString(statsObjectFm, objectFm, statsAdd, negative);
					objectFm.clearStats();
					objectFm.parseStringToStats(statsStr);
				} else {
					if (statStringObj.isEmpty()) {
						String statsStr = statsObjectFm + "#" + Integer.toHexString(statsAdd) + "#0#0#0d0+" + statsAdd;
						objectFm.clearStats();
						objectFm.parseStringToStats(statsStr);
					} else {
						String statsStr = objectFm.parseFMStatsString(statsObjectFm, objectFm, statsAdd, negative) + ","
						+ statsObjectFm + "#" + Integer.toHexString(statsAdd) + "#0#0#0d0+" + statsAdd;
						objectFm.clearStats();
						objectFm.parseStringToStats(statsStr);
					}
				}
			}
			if (signingRune != null) {
				int newQua = signingRune.getQuantity() - 1;
				if (newQua <= 0) {
					this.player.removeItem(signingRune.getGuid());
					World.data.removeItem(signingRune.getGuid());
					SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, signingRune.getGuid());
				} else {
					signingRune.setQuantity(newQua);
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, signingRune);
				}
			}
			if (runeOrPotion != null) {
				int newQua = runeOrPotion.getQuantity() - 1;
				if (newQua <= 0) {
					this.player.removeItem(runeOrPotion.getGuid());
					World.data.removeItem(runeOrPotion.getGuid());
					SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, runeOrPotion.getGuid());
				} else {
					runeOrPotion.setQuantity(newQua);
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, runeOrPotion);
				}
			}
			World.data.addObjet(objectFm, true);
			this.player.addObjet(objectFm);
			SocketManager.GAME_SEND_Ow_PACKET(this.player);
			SocketManager.GAME_SEND_OAKO_PACKET(this.player, objectFm);
			
			String data = objectFm.getGuid() + "|1|" + objectFm.getTemplate().getID() + "|" + objectFm.parseStatsString();
			if (!this.isRepeat)
				this.reConfigingRunes = -1;
			if (this.reConfigingRunes != 0 || this.broken)
				SocketManager.GAME_SEND_EXCHANGE_MOVE_OK_FM(this.player, 'O', "+", data);
			this.data = data;
			SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getUUID(), "+" + objTemaplateID);
			SocketManager.GAME_SEND_Ec_PACKET(this.player, "K;" + objTemaplateID);
		}
		this.lastCraft.clear();
		this.lastCraft.putAll(this.ingredients);
		this.lastCraft.put(objectFm.getGuid(), 1);
		this.ingredients.clear();
	}
	
	public static int getStatBaseMaxs(ObjTemplate objMod, String statsModif)
	{
		String[] split = objMod.getStrTemplate().split(",");
		for (String s : split)
		{
			String[] stats = s.split("#");
			if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) > 0)
			{
				continue;
			} else if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) == 0) 
			{
				int max = Integer.parseInt(stats[2], 16);
				if (max == 0)
					max = Integer.parseInt(stats[1], 16);
				return max;
			}
		}
		return 0;
	}

	public static int WeithTotalBase(int objTemplateID)
	{
		int weight = 0;
		int alt = 0;
		String statsTemplate = "";
		statsTemplate = World.data.getObjTemplate(objTemplateID).getStrTemplate();
		if (statsTemplate == null || statsTemplate.isEmpty())
			return 0;
		String[] split = statsTemplate.split(",");
		for (String s : split)
		{
			String[] stats = s.split("#");
			int statID = Integer.parseInt(stats[0], 16);
			boolean sig = true;
			for (int a : Constants.ARMES_EFFECT_IDS)
				if (a == statID)
					sig = false;
			if (!sig)
				continue;
			String jet = "";
			int value = 1;
			try {
				jet = stats[4];
				value = Formulas.getRandomJet(jet);
				try {
					int min = Integer.parseInt(stats[1], 16);
					int max = Integer.parseInt(stats[2], 16);
					value = min;
					if (max != 0)
						value = max;
				} catch (Exception e) {
					value = Formulas.getRandomJet(jet);
				}
			} catch (Exception e) {}
			int statX = 1;
			if (statID == 125 || statID == 158 || statID == 174)
			{
				statX = 1;
			} else if (statID == 118 || statID == 126 || statID == 119 || statID == 123)
			{
				statX = 2;
			} else if (statID == 138 || statID == 666 || statID == 226 || statID == 220)																																	// de
																																									// da�os,Trampas %
			{
				statX = 3;
			} else if (statID == 124 || statID == 176)
			{
				statX = 5;
			} else if (statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244)
												
			{
				statX = 7;
			} else if (statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214)
			
			{
				statX = 8;
			} else if (statID == 225)
			{
				statX = 15;
			} else if (statID == 178 || statID == 112)
			{
				statX = 20;
			} else if (statID == 115 || statID == 182)
			{
				statX = 30;
			} else if (statID == 117)
			{
				statX = 50;
			} else if (statID == 128)
			{
				statX = 90;
			} else if (statID == 111)
			{
				statX = 100;
			}
			weight = value * statX; 
			alt += weight;
		}
		return alt;
	}

	public static int currentWeithStats(Objet obj, String statsModif)
	{
		for (Entry<Integer, Integer> entry : obj.getStats().getEffects().entrySet())
		{
			int statID = entry.getKey();
			if (Integer.toHexString(statID).toLowerCase().compareTo(statsModif.toLowerCase()) > 0)
			{
				continue;
			} else if (Integer.toHexString(statID).toLowerCase().compareTo(statsModif.toLowerCase()) == 0) 
			{
				int statX = 1;
				int coef = 1;
				int BaseStats = Job.viewBaseStatsItem(obj, Integer.toHexString(statID));
				if (BaseStats == 2) {
					coef = 3;
				} else if (BaseStats == 0) {
					coef = 8;
				}
				if (statID == 125 || statID == 158 || statID == 174)
				{
					statX = 1;
				} else if (statID == 118 || statID == 126 || statID == 119 || statID == 123)
			
				{
					statX = 2;
				} else if (statID == 138 || statID == 666 || statID == 226 || statID == 220)																																					// da�os,Trampas
																																										// %
				{
					statX = 3;
				} else if (statID == 124 || statID == 176)
				{
					statX = 5;
				} else if (statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244)
									
				{
					statX = 7;
				} else if (statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214)
				{
					statX = 8;
				} else if (statID == 225)
				{
					statX = 15;
				} else if (statID == 178 || statID == 112)
				{
					statX = 20;
				} else if (statID == 115 || statID == 182)
				{
					statX = 30;
				} else if (statID == 117)
				{
					statX = 50;
				} else if (statID == 128)
				{
					statX = 90;
				} else if (statID == 111)
				{
					statX = 100;
				}
				int Weight = entry.getValue() * statX * coef;
				return Weight;
			}
		}
		return 0;
	}

	public static int currentTotalWeigthBase(String statsModelo, Objet obj) 
	{
		int Weigth = 0;
		int Alto = 0;
		String[] split = statsModelo.split(",");
		for (String s : split) {
			String[] stats = s.split("#");
			int statID = Integer.parseInt(stats[0], 16);
			boolean xy = false;
			for (int a : Constants.ARMES_EFFECT_IDS)
				if (a == statID)
					xy = true;
			if (xy)
				continue;
			String jet = "";
			int qua = 1;
			try {
				jet = stats[4];
				qua = Formulas.getRandomJet(jet);
				try {
					int min = Integer.parseInt(stats[1], 16);
					int max = Integer.parseInt(stats[2], 16);
					qua = min;
					if (max != 0)
						qua = max;
				} catch (Exception e) {
					qua = Formulas.getRandomJet(jet);
				}
			} catch (Exception e) {}
			int statX = 1;
			int coef = 1;
			int statsBase = Job.viewBaseStatsItem(obj, stats[0]);
			if (statsBase == 2) {
				coef = 3;
			} else if (statsBase == 0) {
				coef = 8;
			}
			if (statID == 125 || statID == 158 || statID == 174)
			{
				statX = 1;
			} else if (statID == 118 || statID == 126 || statID == 119 || statID == 123)
			{
				statX = 2;
			} else if (statID == 138 || statID == 666 || statID == 226 || statID == 220)																															// de
																																									// da�os,Trampas %
			{
				statX = 3;
			} else if (statID == 124 || statID == 176)
			{
				statX = 5;
			} else if (statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244)
			{
				statX = 7;
			} else if (statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214)
									
			{
				statX = 8;
			} else if (statID == 225)
			{
				statX = 15;
			} else if (statID == 178 || statID == 112)
			{
				statX = 20;
			} else if (statID == 115 || statID == 182)
			{
				statX = 30;
			} else if (statID == 117)
			{
				statX = 50;
			} else if (statID == 128)
			{
				statX = 90;
			} else if (statID == 111)
			{
				statX = 100;
			}
			Weigth = qua * statX * coef;
			Alto += Weigth;
		}
		return Alto;
	}	
	/****************************************************************************************************************/	
}