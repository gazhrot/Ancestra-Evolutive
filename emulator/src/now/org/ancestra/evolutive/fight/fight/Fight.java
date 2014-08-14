package org.ancestra.evolutive.fight.fight;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.client.other.Group;
import org.ancestra.evolutive.common.*;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.entity.Fightable;
import org.ancestra.evolutive.entity.collector.Collector;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.ordreJeu.OrdreJeu;
import org.ancestra.evolutive.fight.spell.LaunchedSpell;
import org.ancestra.evolutive.fight.spell.SpellEffect;
import org.ancestra.evolutive.fight.spell.SpellStats;
import org.ancestra.evolutive.fight.team.PlayerTeam;
import org.ancestra.evolutive.fight.team.Team;
import org.ancestra.evolutive.fight.trap.Glyphe;
import org.ancestra.evolutive.fight.trap.Piege;
import org.ancestra.evolutive.game.GameAction;
import org.ancestra.evolutive.game.GameClient;
import org.ancestra.evolutive.map.Case;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.object.*;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Fight {
    private static final Random r = new Random();
    public enum FightType{
		DEFY(0),PVP(1),PVM(4),PERCO(5);
		public final int id;
		private FightType(int id){
			this.id = id;
		}
	}
    public enum FightState{
        INITIATION(1),PLACEMENT(2),ACTIVE(3),FINISHED(4);
        public final int id;
        private FightState(int id){
            this.id = id;
        }
    }

    private class TurnTimer extends ScheduledAction {
        public TurnTimer(int timeDelay) {
            super(timeDelay);
        }

        @Override
        public void applyAction() {
            endTurn();
        }

        public void restartTimer() {
            this.ticCount = this.initialTic;
        }
    }


    private final int id;
    private final Maps map, oldMap;
    protected final String GJKPacket;

    protected Logger logger =(Logger) LoggerFactory.getLogger(Fight.class);
    protected Team team0,team1;
    private final ConcurrentHashMap<Integer,Player> spectator = new ConcurrentHashMap<>();
    private FightState state;
    private int _guildID = -1;
    private final FightType type;
    private boolean specOk;
	protected long startTime = 0;
	private int _curFighterPA, _curFighterPM, _curFighterUsedPA;
    private Fighter currentFighter;
	private String _curAction = "";
	private CopyOnWriteArrayList<Glyphe> glyphes = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<Piege> traps = new CopyOnWriteArrayList<>();
	private Collector _perco;
	
	private List<Fighter> _captureur = new CopyOnWriteArrayList<>();	
	private boolean isCapturable = false;
	private int captWinner = -1;
	private SoulStone pierrePleine;
	//protector
	private Fighter protector;

    private TurnTimer turnTimer = new TurnTimer(Constants.TIME_BY_TURN);

    public Fight(FightType type, int id, Maps oldMap){
        logger.debug("Lancement de la fight {}",id);
        state = FightState.INITIATION;
        this.type = type;
        this.id = id;
        this.map = oldMap.copy();
        this.oldMap = oldMap;
        GJKPacket = "GJK2|" //bouton annule
                + (this.type == FightType.PVP?"1":"0")
                + "|1|0|" + (this.type == FightType.PVP?"0":"45000") + "|" + type;

        state = FightState.PLACEMENT;
        GlobalThread.registerAction(new ExecuteOnceAction(45000) {
            @Override
            public void applyAction() {
                startFight();
            }
        });
    }

    /**
     * Action a effectuer lorsqu un fighter a perdu
     * @param looser fighter ayant perdu
     */
    protected abstract void onFighterLoose(Fighter looser);

    /**
     * Action a effectuer lorsqu un fighter a gagner
     * @param winner fighter ayant gagner
     */
    protected abstract void onFighterWin(Fighter winner);

    /**
     * Paquet GE qui doit etre envoye en fin de combat
     * @param winner team ayant gagne
     * @param looser team ayant perdu
     * @return paquet GE a envoyer a tous
     */
    protected abstract String getGE(Team winner,Team looser);

    /**
     * OrdreJeu permettant de connaitre l ordre de passage
     * @return ordre jeu pour la fight
     */
    public abstract OrdreJeu getOrdreJeu();

    /**
     * Permet de verifier si la personne peut bien rejoindre l equipe
     * Les verifications sur les teams fermees sont deja faites
     * @param fighter player voulant rentre
     * @param team team voulant etre integrer
     * @return true si l integration est possible
     * false sinon
     */
    public abstract boolean canJoinTeam(Player fighter,Team team);

    /**
     * Permet de recup?rer les info necessaires pour l afficher dans la liste des fights
     * @return
     */
    public abstract String getFightInfos();

    //region Getters and setters
	public Maps getMap() {
		return map;
	}

    public Couple<Team, Team> getTeams(){
        return new Couple<>(team0,team1);
    }

    public FightState getState(){
        return this.state;
    }

    public FightType getFightType(){
        return type;
    }

    public int getType() {
        return type.id;
    }

    public int getId() {
        return id;
    }

    public Maps getOldMap() {
        return oldMap;
    }

    public ArrayList<Fighter> getAllFighters(){
        ArrayList<Fighter> fighters = new ArrayList<>();
        fighters.addAll(team0.getTeam().values());
        fighters.addAll(team1.getTeam().values());
        return fighters;
    }

	public List<Piege> getTraps() {
		return traps;
	}

	public List<Glyphe> getGlyphes() {
		return glyphes;
	}

	public ArrayList<Fighter> getFighters(int teams){
        ArrayList<Fighter> fighters = new ArrayList<>();

        if(teams - 4 >= 0)
        {
            for(Entry<Integer,Player> entry : spectator.entrySet())
            {
                fighters.add(new Fighter(Fight.this,entry.getValue(),null));
            }
            teams -= 4;
        }
        if(teams -2 >= 0)
        {
            for(Entry<Integer,Fighter> entry : team1.getTeam().entrySet())
            {
                fighters.add(entry.getValue());
            }
            teams -= 2;
        }
        if(teams -1 >=0)
        {
            for(Entry<Integer,Fighter> entry : team0.getTeam().entrySet())
            {
                fighters.add(entry.getValue());
            }
        }
        return fighters;
    }

    // region Current fighter info
    public int get_curFighterPM() {
        return _curFighterPM;
    }

    public void set_curFighterPM(int _curFighterPM) {
        Fight.this._curFighterPM = _curFighterPM;
    }

    public int get_curFighterPA() {
        return _curFighterPA;
    }

    public void set_curFighterPA(int _curFighterPA) {
        Fight.this._curFighterPA = _curFighterPA;
    }
    //endregion

    public String get_curAction() {
        return _curAction;
    }

    public void set_curAction(String _curAction) {
        this._curAction = _curAction;
    }

    //endregion

	public synchronized void changePlace(Player perso,int cell) {
        if(this.state != FightState.PLACEMENT || isOccuped(cell)) return;
		Fighter fighter = perso.getFighter();
        if(perso.isReady() || !fighter.getTeam().groupCellContains(cell))return;
        fighter.setPosition(map.getCases().get(cell));
        this.send("GIC|" + fighter.getId() + ";" + cell + ";1");
	}

	public void onReadyChange() {
        for(Fighter fighter : team0.getTeam().values()){
            if(!fighter.isReady())
                return;
        }
        for(Fighter fighter : team1.getTeam().values()){
            if(!fighter.isReady())
                return;
        }
        switch (this.state) {
            case PLACEMENT :
                startFight();
                break;
            case ACTIVE:
                startTurn();
                break;
        }
	}

    /**
     * Lance le combat et pr?viens chacun des combattants
     */
	private void startFight(){
		if(state != FightState.PLACEMENT)return;
        this.state = FightState.ACTIVE;
        this.oldMap.onFightStart(this);
        this.send(map.getGDFPacket());
        this.startTime = System.currentTimeMillis();
        for (Fighter fighter : team0.getTeam().values()) {
            fighter.initBuffStats();
        }
        for (Fighter fighter : team1.getTeam().values()) {
            fighter.initBuffStats();
        }
        /*if(type == Constants.FIGHT_TYPE_PVT) {
			_perco.set_inFight((byte)2);
			//On actualise la guilde+Message d'attaque FIXME
			for(Player z : World.data.getGuild(_guildID).getMembers())
			{
				if(z == null) continue;
				if(z.isOnline())
				{
					SocketManager.GAME_SEND_gITM_PACKET(z, Collector.parsetoGuild(z.getGuild().getId()));
					Collector.parseAttaque(z, _guildID);
					Collector.parseDefense(z, _guildID);
					SocketManager.GAME_SEND_MESSAGE(z, "Un de vos percepteurs est rentre en combat.", Server.config.getMotdColor());
				}
			}
		}*/
        getOrdreJeu().init();
		this.send(generateGICPacket());
		this.send("GS");
        this.send(getOrdreJeu().generateGTLPaquet());

		SocketManager.GAME_SEND_GTM_PACKET_TO_FIGHT(Fight.this, 7);
		GlobalThread.registerAction(this.turnTimer);
        startTurn();
	}

	private synchronized void startTurn(){
        if(state != FightState.ACTIVE)return;
        if(verifIfFightEnded()) return;
        set_curAction("");
		this.currentFighter = getOrdreJeu().getNextFighter();

        set_curFighterPA(this.currentFighter.getPA());
        set_curFighterPM(this.currentFighter.getPM());
        _curFighterUsedPA = 0;

		/*if(currentFighter.hasLeft()) {
			currentFighter.decreaseTurnRemaining();
			if(currentFighter.getTurnRemainingBeforeExpulsion() == 0) {
				if(currentFighter.getPersonnage() != null) {
					this.leftFight(currentFighter.getPersonnage());
					Player player = currentFighter.getPersonnage();
					if(player.getGroup() != null)
						player.getGroup().leave(player);
					player.resetVars();
					player.setClone(true);
					World.data.unloadPerso(player.getId());
				} else {
					onFighterDie(currentFighter);
					currentFighter.setLeft(true);
				}
			} else {
				SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(this, 7, "0162;" + currentFighter.getName() + "~" + currentFighter.getTurnRemainingBeforeExpulsion());
				endTurn();
				return;
			}
		}*/
		
		currentFighter.applyBeginningTurnBuff(Fight.this);
        currentFighter.actualiseLaunchedSort();
		currentFighter.get_chatiValue().clear();
		for(Glyphe g : glyphes) {
			if(g.get_caster() == currentFighter){
				if(g.decrementDuration() == 0){
					glyphes.remove(g);
					g.desapear();
					continue;//Continue pour pas que le joueur active le glyphe s'il ?tait dessus
				}
			}
			int dist = Pathfinding.getDistanceBetween(map,currentFighter.getCell().getId(), g.get_cell().getId());
			if(dist <= g.get_size() && g.getSpell() != 476) {
				g.onTraped(currentFighter);
			}
		}
        if(currentFighter.hasBuff(Constants.EFFECT_PASS_TURN))//Si il doit passer son tour
		{
			endTurn();
			return;
		}
		logger.trace("Debut du tour de Fighter ID= " + currentFighter.getId());
        for(Fighter fighter : team0.getTeam().values()){
            fighter.onStartTurn(currentFighter);
        }
        for(Fighter fighter : team1.getTeam().values()){
            fighter.onStartTurn(currentFighter);
        }
        this.send("GTS" + currentFighter.getId() + "|" + Constants.TIME_BY_TURN);
        turnTimer.restartTimer();
    }

	public void endTurn() {
		try {
			if(state != FightState.ACTIVE)return;
			currentFighter.setCanPlay(false);
			
			if(!get_curAction().equals("") && currentFighter.getPersonnage() != null)
				return;
            try {
                this.send("GTF" + currentFighter.getId());

                //Si empoisonn? (Cr?er une fonction applyEndTurnbuff si d'autres effets existent)
                for(SpellEffect SE : currentFighter.getBuffsByEffectID(131)) {
                    int pas = SE.getValue();
                    int val = -1;
                    try {
                        val = Integer.parseInt(SE.getArgs().split(";")[1]);
                    } catch(Exception e){};
                    if(val == -1)continue;

                    int nbr = (int) Math.floor((double)_curFighterUsedPA/(double)pas);
                    int dgt = val * nbr;
                    //Si poison paralysant
                    if(SE.getSpell() == 200) {
                        int inte = SE.getCaster().getTotalStats().getEffect(Constants.STATS_ADD_INTE);
                        if(inte < 0)inte = 0;
                        int pdom = SE.getCaster().getTotalStats().getEffect(Constants.STATS_ADD_PERDOM);
                        if(pdom < 0)pdom = 0;
                        //on applique le boost
                        dgt = (((100+inte+pdom)/100) * dgt);
                    }
                    if(currentFighter.hasBuff(184)) {
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 105, currentFighter.getId()+"", currentFighter.getId()+","+currentFighter.getBuff(184).getValue());
                        dgt = dgt-currentFighter.getBuff(184).getValue();//R?duction physique
                    }
                    if(currentFighter.hasBuff(105)) {
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 105, currentFighter.getId()+"", currentFighter.getId()+","+currentFighter.getBuff(105).getValue());
                        dgt = dgt-currentFighter.getBuff(105).getValue();//Immu
                    }
                    if(dgt <= 0)continue;

                    if(dgt>currentFighter.getPDV())dgt = currentFighter.getPDV();//va mourrir
                    currentFighter.removePDV(dgt);
                    dgt = -(dgt);
                    SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 100, SE.getCaster().getId()+"", currentFighter.getId()+","+dgt);
                }
                ArrayList<Glyphe> glyphs = new ArrayList<Glyphe>();//Copie du tableau
                glyphs.addAll(glyphes);
                for(Glyphe g : glyphs) {
                    if(state != FightState.ACTIVE)return;
                    //Si dans le glyphe
                    int dist = Pathfinding.getDistanceBetween(map,currentFighter.get_fightCell(false).getId() , g.get_cell().getId());
                    if(dist <= g.get_size() && g.getSpell() == 476)//476 a effet en fin de tour
                    {
                        //Alors le joueur est dans le glyphe
                        g.onTraped(currentFighter);
                    }
                }

                //reset des valeurs
                _curFighterUsedPA = 0;
                set_curFighterPA(currentFighter.getTotalStats().getEffect(Constants.STATS_ADD_PA));
                set_curFighterPM(currentFighter.getTotalStats().getEffect(Constants.STATS_ADD_PM));
                currentFighter.refreshfightBuff();

                SocketManager.GAME_SEND_GTM_PACKET_TO_FIGHT(Fight.this, 7);
                this.send("GTR"+currentFighter.getId());
                logger.debug("Fin du tour de Fighter ID= " + currentFighter.getId());
            } catch(NullPointerException e) {
                e.printStackTrace();
                endTurn();
            }

		} catch(Exception e) {
			e.printStackTrace();
			endTurn();
		}
			
	}

    public Team getTeamByFlagId(int teamId){
        if(team0.getFlag().getId() == teamId)
            return team0;
        if(team1.getFlag().getId() == teamId)
            return team1;
        return null;
    }

    public void joinFight(Player player,Team team){
        final String errorMessage = "GA;903;"+player.getId()+";f";
        if(team == null || team.isClosed()) {
            send(errorMessage);
        }
        if(team.isRestrictedToGroup()) {
            Group g = ((Player) team.getTeam().get(player.getId()).getFightable()).getGroup();
            if(g!=null){
                if(!g.getPlayers().contains(player)){
                    send(errorMessage);
                    return;
                }
            }
        }
        if(!canJoinTeam(player,team)) {
            send(errorMessage);
            return;
        }
        player.send(this.GJKPacket);
        oldMap.removeEntity(player);
        player.getCell().removeCreature(player);
        addPlayer(team, player);
        getOrdreJeu().addFighter(player.getFighter());
        this.send("GA;950;" + player.getId() + ";" + player.getId() + ",8,0");
        this.send("GA;950;" + player.getId() + ";" + player.getId() + ",3,0" );
        player.send("ILF0");
        team.getFlag().onFighterJoin(player.getFighter());
        this.send(map.getGmMessage());
        /**if(_perco != null){
         for(Player z : World.data.getGuild(_guildID).getMembers()){
         if(z.isOnline()){
         Collector.parseAttaque(z, _guildID);
         Collector.parseDefense(z, _guildID);
         }
         }
         }*/
    }

    public void onSpectatorBehaviourChange(PlayerTeam playerTeam){
        specOk = team0.areSpectatorAllowed() && team1.areSpectatorAllowed();
        if(!specOk){
            for(Player player : spectator.values()){
                player.send("GV");
                player.setSitted(false);
                player.setFight(null);
                player.setAway(false);
                map.removeEntity(player);
            }
            spectator.clear();
        }
        this.oldMap.send("Go" + (specOk?"+S":"-S") + playerTeam.initiateur.getId());
        this.send(specOk?"Im93":"Im40");
    }



	public boolean fighterDeplace(Fighter f, GameAction GA) {
		String path = GA.getArgs();
		if(path.equals("")) {
			logger.debug("Echec du deplacement: chemin vide");
			return false;
		}
        if(currentFighter == null || f != currentFighter || state != FightState.ACTIVE)return false;
		logger.trace("Tentative de deplacement de Fighter ID= "+f.getId()+" a partir de la case "+f.get_fightCell(false).getId());
        logger.debug("Path: "+path);
		if(!get_curAction().equals("") ){
            logger.debug("Echec du deplacement: il y deja une action en cours");
			return false;
		}
		ArrayList<Fighter> tacle = Pathfinding.getEnemyFighterArround(f.get_fightCell(false).getId(), map, Fight.this);
		if(tacle != null && !f.isState(6))//Tentative de Tacle : Si stabilisation alors pas de tacle possible
		{
			for(Fighter T : tacle)//Les stabilis?s ne taclent pas
			{ 
				if(T.isState(6)) 
				{ 
					tacle.remove(T); 
				} 
			}
			if(!tacle.isEmpty())//Si tous les tacleur ne sont pas stabilis?s
			{
				logger.trace("Le personnage est a cote de ("+tacle.size()+") ennemi(s)");// ("+tacle.getName()+","+tacle.get_fightCell().getID()+") => Tentative de tacle:");
				int chance = Formulas.getTacleChance(f, tacle);
				int rand = Formulas.getRandomValue(0, 99);
				if(rand > chance)
				{
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7,GA.getId(), "104",currentFighter.getId()+";", "");//Joueur tacl?
					int pertePA = get_curFighterPA()*chance/100;
					
					if(pertePA  < 0)pertePA = -pertePA;
					if(get_curFighterPM() < 0)set_curFighterPM(0); // -_curFighterPM :: 0 c'est plus simple :)
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7,GA.getId(),"129", f.getId()+"", f.getId()+",-"+get_curFighterPM());
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7,GA.getId(),"102", f.getId()+"", f.getId()+",-"+pertePA);
					
					set_curFighterPM(0);
					set_curFighterPA(get_curFighterPA() - pertePA);
					logger.trace("Echec du deplacement: fighter tacle");
					return false;
				}
			}
		}
		
		//*
		AtomicReference<String> pathRef = new AtomicReference<String>(path);
		int nStep = Pathfinding.isValidPath(map, f.get_fightCell(false).getId(), pathRef, Fight.this);
		String newPath = pathRef.get();
		if( nStep > get_curFighterPM() || nStep == -1000){
			logger.trace("Fighter ID= "+currentFighter.getId()+" a demander un chemin inaccessible ou trop loin");
			return false;
		}
        this._curFighterPM-= nStep;

		int nextCellID = CryptManager.cellCode_To_ID(newPath.substring(newPath.length() - 2));
		//les monstres n'ont pas de GAS//GAF
		if(currentFighter.getPersonnage() != null)
			SocketManager.GAME_SEND_GAS_PACKET_TO_FIGHT(Fight.this,7,currentFighter.getId());

        //Si le joueur n'est pas invisible
        if(!currentFighter.isHide()) {
	        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, GA.getId(), "1", currentFighter.getId()+"", "a"+CryptManager.cellID_To_Code(f.get_fightCell(false).getId())+newPath);
        } else//Si le joueur est planqu? x)
        {
        	if(currentFighter.getPersonnage() != null)
        	{
        		//On envoie le path qu'au joueur qui se d?place
        		GameClient out = currentFighter.getPersonnage().getAccount().getGameClient();
        		SocketManager.GAME_SEND_GA_PACKET(out,  GA.getId()+"", "1", currentFighter.getId()+"", "a"+CryptManager.cellID_To_Code(f.get_fightCell(false).getId())+newPath);
        	}
        }
       
        //Si port?
        Fighter po = currentFighter.get_holdedBy();
        if(po != null
        && currentFighter.isState(Constants.ETAT_PORTE)
        && po.isState(Constants.ETAT_PORTEUR))
        {
        	Console.instance.println("Porteur: "+po.getName());
        	Console.instance.println("NextCellID "+nextCellID);
        	Console.instance.println("Cell du Porteur "+po.get_fightCell(false).getId());
        	
        	//si le joueur va bouger
       		if(nextCellID != po.get_fightCell(false).getId())
       		{
       			//on retire les ?tats
       			po.setState(Constants.ETAT_PORTEUR, 0);
       			currentFighter.setState(Constants.ETAT_PORTE,0);
       			//on retire d? lie les 2 fighters
       			po.set_isHolding(null);
       			currentFighter.set_holdedBy(null);
       			//La nouvelle case sera d?finie plus tard dans le code
       			//On envoie les packets
       			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 950, po.getId()+"", po.getId()+","+Constants.ETAT_PORTEUR+",0");
    			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 950, currentFighter.getId()+"", currentFighter.getId()+","+Constants.ETAT_PORTE+",0");
       		}
      	}
        
		currentFighter.get_fightCell(false).getFighters().clear();
		if(Server.config.isDebug()) Log.addToLog("Fighter ID= "+f.getId()+" se deplace de la case "+currentFighter.get_fightCell(false).getId()+" vers "+CryptManager.cellCode_To_ID(newPath.substring(newPath.length() - 2)));
        currentFighter.setFightCell(map.getCases().get(nextCellID));
        currentFighter.get_fightCell(false).addFighter(currentFighter);
        if(po != null) po.get_fightCell(false).addFighter(po);// m?me erreur que tant?t, bug ou plus de fighter sur la case
       if(nStep < 0) 
       {
    	   if(Server.config.isDebug()) Log.addToLog("Fighter ID= "+f.getId()+" nStep negatives, reconversion");
    	   nStep = nStep*(-1);
       }
        set_curAction("GA;129;"+currentFighter.getId()+";"+currentFighter.getId()+",-"+nStep);
        
        //Si porteur
        po = currentFighter.get_isHolding();
        if(po != null
        && currentFighter.isState(Constants.ETAT_PORTEUR)
        && po.isState(Constants.ETAT_PORTE))
        {
       		//on d?place le port? sur la case
        	po.setFightCell(currentFighter.get_fightCell(false));
        	if(Server.config.isDebug()) Log.addToLog(po.getName()+" se deplace vers la case "+nextCellID);
      	}
        if(f.getPersonnage() == null)
        {
        	try {
    			Thread.sleep(900+300*nStep);//Estimation de la dur?e du d?placement
    		} catch (InterruptedException e) {};
        	SocketManager.GAME_SEND_GAMEACTION_TO_FIGHT(Fight.this,7,get_curAction());
        	
    		set_curAction("");
    		ArrayList<Piege> P = new ArrayList<Piege>();
    		P.addAll(traps);
    		for(Piege p : P)
    		{
    			Fighter F = currentFighter;
    			int dist = Pathfinding.getDistanceBetween(map,p.get_cell().getId(),F.get_fightCell(false).getId());
    			//on active le piege
    			if(dist <= p.get_size())p.onTraped(F);
    		}
    		return true;
        }
        //*/
        f.getPersonnage().getAccount().getGameClient().addAction(GA);
        return true;
    }

	public void onGK(Player perso) //TODO AAHH
	{
		if(get_curAction().equals("")|| currentFighter.getId() != perso.getId() || state != FightState.ACTIVE)return;
		logger.debug("Fin du deplacement de Fighter ID= "+perso.getId());
		SocketManager.GAME_SEND_GAMEACTION_TO_FIGHT(Fight.this,7,get_curAction());
		SocketManager.GAME_SEND_GAF_PACKET_TO_FIGHT(Fight.this,7,2,currentFighter.getId());


		for(Piege p : traps)
		{
			Fighter F = perso.getFighter();
			int dist = Pathfinding.getDistanceBetween(map,p.get_cell().getId(),F.get_fightCell(false).getId());
			//on active le piege
			if(dist <= p.get_size())p.onTraped(F);
			if(state != FightState.ACTIVE)break;
		}
		set_curAction("");
	}

	public void playerPass(Player _perso) {
		Fighter f = _perso.getFighter();
		if(f == null)return;
		if(!f.canPlay())return;
		if(!get_curAction().equals("")) return;//TODO
		endTurn();
	}

	public int tryCastSpell(Fighter fighter,SpellStats Spell, int caseID)
	{
		if(!get_curAction().equals(""))return 10;
		if(Spell == null)return 10;
		
		Case Cell = map.getCases().get(caseID);
		set_curAction("casting");
		
		if(CanCastSpell(fighter,Spell,Cell, -1))
		{
			if(fighter.getPersonnage() != null)
				SocketManager.GAME_SEND_STATS_PACKET(fighter.getPersonnage());
			
			if(Server.config.isDebug()) Log.addToLog(fighter.getName()+" tentative de lancer le sort "+Spell.getSpellID()+" sur la case "+caseID);
			set_curFighterPA(get_curFighterPA() - Spell.getPACost());
			_curFighterUsedPA += Spell.getPACost();
			SocketManager.GAME_SEND_GAS_PACKET_TO_FIGHT(Fight.this, 7, fighter.getId());
			boolean isEc = Spell.getTauxEC() != 0 && Formulas.getRandomValue(1, Spell.getTauxEC()) == Spell.getTauxEC();
			if(isEc)
			{
				if(Server.config.isDebug()) Log.addToLog(fighter.getName()+" Echec critique sur le sort "+Spell.getSpellID());
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 302, fighter.getId()+"", Spell.getSpellID()+"");
			}else
			{
				boolean isCC = fighter.testIfCC(Spell.getTauxCC());
				String sort = Spell.getSpellID()+","+caseID+","+Spell.getSpriteID()+","+Spell.getLevel()+","+Spell.getSpriteInfos();
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 300, fighter.getId()+"", sort);
				if(isCC)
				{
					if(Server.config.isDebug()) Log.addToLog(fighter.getName()+" Coup critique sur le sort "+Spell.getSpellID());
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 301, fighter.getId()+"", sort);
				}
				//Si le joueur est invi, on montre la case
				if(fighter.isHide()) {
					showCaseToAll(fighter.getId(), fighter.get_fightCell(false).getId());
				}
				//on applique les effets de l'arme
				Spell.applySpellEffectToFight(Fight.this,fighter,Cell,isCC);
				
			}
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 102,fighter.getId()+"",fighter.getId()+",-"+Spell.getPACost());
			SocketManager.GAME_SEND_GAF_PACKET_TO_FIGHT(Fight.this, 7, 56, fighter.getId());
			//Refresh des Stats
			//refreshCurPlayerInfos();
			fighter.addLaunchedSort(Cell.getFirstFighter(),Spell);
			
			if((isEc && Spell.isEcEndTurn()))
			{
				set_curAction("");
				if(fighter.getMob() != null || fighter.isInvocation())//Mob, Invoque
				{
					return 5;
				}else
				{
					endTurn();
					return 5;
				}
			} else if (fighter.getMob() != null) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			verifIfFightEnded();
		}else if (fighter.getMob() != null || fighter.isInvocation())
		{
			return 10;
		}
		set_curAction("");
		return 0;
	}

	public boolean CanCastSpell(Fighter fighter, SpellStats spell, Case cell, int launchCase){
		int ValidlaunchCase;
		if(launchCase <= -1)
		{
			ValidlaunchCase = fighter.get_fightCell(false).getId();
		}else
		{
			ValidlaunchCase = launchCase;
		}
		
		Fighter f = currentFighter;
		Player perso = fighter.getPersonnage();
		//Si le sort n'est pas existant
		if(spell == null)
		{
			if(Server.config.isDebug()) Log.addToLog("Sort non existant");
			if(perso != null)
			{
				SocketManager.GAME_SEND_Im_PACKET(perso, "1169");
			}
			return false;
		}
		//Si ce n'est pas au joueur de jouer
		if (f == null || f.getId() != fighter.getId())
		{
			if(Server.config.isDebug()) Log.addToLog("Ce n'est pas au joueur. Doit jouer :("+f.getId()+"). Fautif :("+fighter.getId()+")");
			if(perso != null)
			{
				SocketManager.GAME_SEND_Im_PACKET(perso, "1175");
			}
			return false;	
		}
		//Si le joueur n'a pas assez de PA
		if(get_curFighterPA() < spell.getPACost())
		{
			if(Server.config.isDebug()) Log.addToLog("Le joueur n'a pas assez de PA ("+get_curFighterPA()+"/"+spell.getPACost()+")");
			if(perso != null)
			{
				SocketManager.GAME_SEND_Im_PACKET(perso, "1170;" + get_curFighterPA() + "~" + spell.getPACost());
			}
			return false;
		}
		//Si la cellule vis?e n'existe pas
		if(cell == null)
		{
			if(Server.config.isDebug()) Log.addToLog("La cellule visee n'existe pas");
			if(perso != null)
			{
				SocketManager.GAME_SEND_Im_PACKET(perso, "1172");
			}
			return false;
		}
		//Si la cellule vis?e n'est pas align?e avec le joueur alors que le sort le demande
		if(spell.isLineLaunch() && !Pathfinding.casesAreInSameLine(map, ValidlaunchCase, cell.getId(), 'z'))
		{
			if(Server.config.isDebug()) Log.addToLog("Le sort demande un lancer en ligne, or la case n'est pas alignee avec le joueur");
			if(perso != null)
			{
				SocketManager.GAME_SEND_Im_PACKET(perso, "1173");
			}
			return false;
		}
		//Si le sort demande une ligne de vue et que la case demand?e n'en fait pas partie
		if(spell.hasLDV() && !Pathfinding.checkLoS(map, ValidlaunchCase, cell.getId(), fighter))
		{
			if(Server.config.isDebug()) Log.addToLog("Le sort demande une ligne de vue, mais la case visee n'est pas visible pour le joueur");
			if(perso != null)
			{
				SocketManager.GAME_SEND_Im_PACKET(perso, "1174");
			}
			return false;
		}
		// Pour peur si la personne pouss?e a la ligne de vue vers la case
		char dir = Pathfinding.getDirBetweenTwoCase(ValidlaunchCase, cell.getId(), map, true);
		if(spell.getSpellID() == 67)
			if(!Pathfinding.checkLoS(map, Pathfinding.GetCaseIDFromDirrection(ValidlaunchCase, dir, map, true), cell.getId(), null, true, getAllFighters())) {
				if(Server.config.isDebug()) 
					Log.addToLog("Le sort demande une ligne de vue, mais la case visee n'est pas visible pour le joueur");
				if(perso != null)
					SocketManager.GAME_SEND_Im_PACKET(perso, "1174");
				return false;
			}
		
		int dist = Pathfinding.getDistanceBetween(map, ValidlaunchCase, cell.getId());
		int MaxPO = spell.getMaxPO();
		if(spell.isModifPO()) {
			MaxPO += fighter.getTotalStats().getEffect(Constants.STATS_ADD_PO);
			MaxPO = MaxPO <= 0 ? 1 : MaxPO;
		}
		//V?rification Port?e mini / maxi
		if(dist < spell.getMinPO() || dist > MaxPO)
		{
			if(Server.config.isDebug()) Log.addToLog("La case est trop proche ou trop eloignee Min: "+spell.getMinPO()+" Max: "+spell.getMaxPO()+" Dist: "+dist);
			if(perso != null)
			{
				SocketManager.GAME_SEND_Im_PACKET(perso, "1171;" + spell.getMinPO() + "~" + spell.getMaxPO() + "~" + dist);
			}
			return false;
		}
		//v?rification cooldown
		if(!LaunchedSpell.cooldownGood(fighter,spell.getSpellID()))
		{
			return false;
		}
		//v?rification nombre de lancer par tour
		int nbLancer = spell.getMaxLaunchbyTurn();
		if(nbLancer - LaunchedSpell.getNbLaunch(fighter, spell.getSpellID()) <= 0 && nbLancer > 0)
		{
			return false;
		}
		//v?rification nombre de lancer par cible
		Fighter target = cell.getFirstFighter();
		int nbLancerT = spell.getMaxLaunchbyByTarget();
		if(nbLancerT - LaunchedSpell.getNbLaunchTarget(fighter, target, spell.getSpellID()) <= 0 && nbLancerT > 0)
		{
			return false;
		}
		return true;
	}
	
	/*public String GetGE(int win) {
		long time = System.currentTimeMillis() - startTime;
		int type = Constants.FIGHT_TYPE_CHALLENGE;// toujours 0
		if(this.type == Constants.FIGHT_TYPE_AGRESSION)//Sauf si gain d'honneur
			type = this.type;
		
		StringBuilder Packet = new StringBuilder();
        Packet.append("GE").append(time).append("|").append(0)//Variable inutilis?e repr?sentant l'id du lanceur du combat
                .append("|").append(type).append("|");
        Collection<Fighter> winner;
        Collection<Fighter> loser;
        if(win == 1) {
            winner = cleanTeam(team1.getTeam().values());
            loser = cleanTeam(team0.getTeam().values());
        }
        else {
        	winner = cleanTeam(team0.getTeam().values());
        	loser = cleanTeam(team1.getTeam().values());
        }
        updateTraque(winner, loser);

        //DROP SYSTEM
        	//Calcul de la PP de groupe
	    int minkamas = 0,maxkamas = 0;
        int groupPP = 0;
        	//Calcul des drops possibles
	        Map<Integer,Integer> possibleDrops = new TreeMap<>();
	        for(Fighter F : loser){
	        	if(F.getFighterType() != Fighter.FighterType.CREATURE)continue;
	        	minkamas += ((Mob)F.getFightable()).getGrade().getTemplate().getMinKamas();
	        	maxkamas += ((Mob)F.getFightable()).getGrade().getTemplate().getMaxKamas();
	        	for(Drop D : ((Mob)F.getFightable()).getGrade().getTemplate().getDrops()){
	        		if(D.getMinProsp() <= groupPP){
	        			//On augmente le taux en fonction de la PP
	        			int taux = (int)((groupPP * D.getTaux(((Mob)F.getFightable()).getGrade().getGrade())*Server.config.getRateDrop())/100);
	        			//possibleDrops.add(new Drop(D.getItemId(),0,taux));
                        possibleDrops.put(D.getItemId(),taux);
	        		}
	        	}
	        }
	        ArrayList<Fighter> Temp = new ArrayList<>();


	        logger.debug("DROP: PP ="+groupPP);
	        logger.debug("DROP: nbr="+possibleDrops.size());

	    //FIN DROP SYSTEM
	    //XP SYSTEM
	        long totalXP = 0;
	        for(Fighter F : loser){
	        	try{
                    totalXP += ((Mob)F.getFightable()).getGrade().getXp();
                } catch (Exception e) {
                    continue;
                }
	        }
	        logger.debug("TEAM1: xpTotal="+totalXP);
	    //FIN XP SYSTEM
		//Capture d'?mes
	        boolean mobCapturable = true;
	        for(Fighter F : loser) {
	        	try {
	        		mobCapturable &= ((Mob)F.getFightable()).getGrade().getTemplate().isCapturable();
	        	}catch (Exception e) {
					mobCapturable = false;
					break;
				}
	        }
	        isCapturable |= mobCapturable;
	        
	        if(isCapturable){
		        boolean isFirst = true;
		        int maxLvl = 0;
		        String pierreStats = "";

		        
		        for(Fighter F : loser)	//Cr?ation de la pierre et verifie si le groupe peut ?tre captur?
		        {
		        	if(!isFirst)
		        		pierreStats += "|";
		        	
		        	pierreStats += ((Mob)F.getMob()).getGrade().getTemplate().getId() + "," + F.getLvl();//Converti l'ID du monstre en Hex et l'ajoute au stats de la futur pierre d'?me
		        	
		        	isFirst = false;
		        	
		        	if(F.getLvl() > maxLvl)	//Trouve le monstre au plus haut lvl du groupe (pour la puissance de la pierre)
		        		maxLvl = F.getLvl();
		        }
		        pierrePleine = new SoulStone(World.data.getNewObjectGuid(), 1, 7010, -1, pierreStats);	//Cr?e la pierre d'?me
		        
		        for(Fighter F : winner)	//R?cup?re les captureur
		        {
		        	if(!F.isInvocation() && F.isState(Constants.ETAT_CAPT_AME))
		        	{
		        		_captureur.add(F);
		        	}
		        }
		        if(_captureur.size() > 0 && !World.data.isArenaMap(getMap().getId()))	//S'il y a des captureurs
	    		{
	    			for (int i = 0; i < _captureur.size(); i++)
	    			{
	    				try
	    				{
			        		Fighter f = _captureur.get(Formulas.getRandomValue(0, _captureur.size()-1));	//R?cup?re un captureur au hasard dans la liste
			        		if(!(f.getPersonnage().getObjectByPos(ObjectPosition.ARME).getTemplate().getType() == ObjectType.PIERRE_AME))
		    				{
			    				_captureur.remove(f);
		    					continue;
		    				}
			    			Couple<Integer,Integer> pierreJoueur = Formulas.decompPierreAme(f.getPersonnage().getObjectByPos(ObjectPosition.ARME));//R?cup?re les stats de la pierre ?quipp?
			    			
			    			if(pierreJoueur.second < maxLvl)	//Si la pierre est trop faible
			    			{
			    				_captureur.remove(f);
		    					continue;
		    				}
			    			
			    			int captChance = Formulas.totalCaptChance(pierreJoueur.first, f.getPersonnage());
			    			
			    			if(Formulas.getRandomValue(1, 100) <= captChance)	//Si le joueur obtiens la capture
			    			{
			    				//Retire la pierre vide au personnage et lui envoie ce changement
			    				int pierreVide = f.getPersonnage().getObjectByPos(ObjectPosition.ARME).getId();
			    				f.getPersonnage().deleteItem(pierreVide);
			    				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(f.getPersonnage(), pierreVide);
			    				
			    				captWinner = f.getId();
			    				break;
			    			}
		    			}
	    				catch(NullPointerException e)
	    				{
	    					continue;
	    				}
	    			}
	    		}
	        }
	    //Fin Capture
	    for(Fighter i : winner) {
            if(i.hasLeft()) continue;//Si il abandonne, il ne gagne pas d'xp
        	if(type == Constants.FIGHT_TYPE_CHALLENGE) {
        		if(i.getFighterType() == Fighter.FighterType.CREATURE)continue;
                if(i.getFighterType() ==Fighter.FighterType.PLAYER)
                    ((Player)i.getFightable()).refreshMapAfterFight();
        		long winxp 	= Formulas.getXpWinPvm2(i,winner,loser,totalXP);
        		AtomicReference<Long> XP = new AtomicReference<>();
        		XP.set(winxp);
        		
        		long guildxp = Formulas.getGuildXpWin(i,XP);
        		long mountxp = 0;

        		if(i.getFighterType() != Fighter.FighterType.PLAYER && ((Player)i.getFightable()).isOnMount()){
        			mountxp = Formulas.getMountXpWin(i,XP);
        			i.getPersonnage().getMount().addExperience(mountxp);
        			SocketManager.GAME_SEND_Re_PACKET(i.getPersonnage(),"+",i.getPersonnage().getMount());
        		}
        		int winKamas= Formulas.getKamasWin(i,winner,minkamas,maxkamas);
        		String drops = "";
        		//Drop system
        		Map<Integer,Integer> itemWon = new TreeMap<Integer,Integer>();
        		
        		for(Entry<Integer,Integer> tauxByItem : possibleDrops.entrySet()){
        			int t = (int)(tauxByItem.getValue()*100);//Permet de gerer des taux>0.01
        			int jet = Formulas.getRandomValue(0, 100*100);
        			if(jet < t){
        				ObjectTemplate OT = World.data.getObjectTemplate(tauxByItem.getKey());
        				if(OT == null)continue;
        				//on ajoute a la liste
        				itemWon.put(OT.getId(),(itemWon.get(OT.getId())==null?0:itemWon.get(OT.getId()))+1);

        			}
        		}
        		if(i.getId() == captWinner && pierrePleine != null)	//S'il ? captur? le groupe
        		{
        			if(drops.length() >0)drops += ",";
        			drops += pierrePleine.getTemplate().getId()+"~"+1;
        			if(i.getPersonnage().addObject(pierrePleine, false))
        				World.data.addObject(pierrePleine, true);
        		}
        		for(Entry<Integer,Integer> entry : itemWon.entrySet())
        		{
        			ObjectTemplate OT = World.data.getObjectTemplate(entry.getKey());
        			if(OT == null)continue;
        			if(drops.length() >0)drops += ",";
        			drops += entry.getKey()+"~"+entry.getValue();
        			Object obj = OT.createNewItem(entry.getValue(), false);
        			if(i.getPersonnage().addObject(obj, true))
        				World.data.addObject(obj, true);
        		}
        		//fin drop system
        		winxp = XP.get();
        		if(winxp != 0 && i.getFighterType() == Fighter.FighterType.PLAYER)
        			i.getPersonnage().addXp(winxp);
        		if(winKamas != 0 && i.getFighterType() == Fighter.FighterType.PLAYER)
        			i.getPersonnage().addKamas(winKamas);
        		if(guildxp > 0 && i.getPersonnage().getGuildMember() != null)
        			i.getPersonnage().getGuildMember().giveXpToGuild(guildxp);

        		Packet.append("2;").append(i.getId()).append(";").append(i.getName()).append(";").append(i.getLvl()).append(";").append((i.isDead() ?  "1" : "0" )).append(";");
        		Packet.append(i.xpString(";")).append(";");
        		Packet.append((winxp == 0?"":winxp)).append(";");
        		Packet.append((guildxp == 0?"":guildxp)).append(";");
        		Packet.append((mountxp == 0?"":mountxp)).append(";");
        		Packet.append(drops).append(";");//Drop
        		Packet.append((winKamas == 0?"":winKamas)).append("|");
        	}else
        	{
        		if(i.getFighterType() != Fighter.FighterType.PLAYER)
        			continue;
        		// Si c'est un neutre, on ne gagne pas de points
        		int winH = 0;
        		int winD = 0;
        		if(type == Constants.FIGHT_TYPE_AGRESSION)
        		{
	        		/*if(_init1.getPersonnage().getAlignement() != Alignement.NEUTRE && _init0.getPersonnage().getAlignement() != Alignement.NEUTRE)
	    			{
	        			if(_init1.getPersonnage().getAccount().getCurIp().compareTo(_init0.getPersonnage().getAccount().getCurIp()) != 0 || Server.config.isMulePvp())
	        			{
	            			winH = Formulas.calculHonorWin(winner,loser,i);
	        			}
	        			if(i.getPersonnage().getDeshonor() > 0) winD = -1;
	    			}*/
        		/*}
        		Player P = i.getPersonnage();
        		if(P.getHonor()+winH<0)winH = -P.getHonor();
        		P.addHonor(winH);
        		P.setDeshonor(P.getDeshonor()+winD);
        		Packet.append("2;").append(i.getId()).append(";").append(i.getName()).append(";").append(i.getLvl()).append(";").append((i.isDead() ?  "1" : "0" )).append(";");
        		Packet.append((P.getAlignement()!=Alignement.NEUTRE?World.data.getExpLevel(P.getGrade()).pvp:0)).append(";");
        		Packet.append(P.getHonor()).append(";");
        		int maxHonor = World.data.getExpLevel(P.getGrade()+1).pvp;
        		if(maxHonor == -1)maxHonor = World.data.getExpLevel(P.getGrade()).pvp;
        		Packet.append((P.getAlignement()!=Alignement.NEUTRE?maxHonor:0)).append(";");
        		Packet.append(winH).append(";");
        		Packet.append(P.getGrade()).append(";");
        		Packet.append(P.getDeshonor()).append(";");
        		Packet.append(winD);
        		Packet.append(";;0;0;0;0;0|");
        		
        	/**	if(type != Constants.FIGHT_TYPE_CHALLENGE) { What the fuck baby ? Why here *-*
        			final Fighter F = i;
        			
        			if(F.hasLeft())continue;
					if(F.getPersonnage() == null)continue;
					if(F.isInvocation())continue;
					if(!F.getPersonnage().isOnline())continue;
					
					F.getPersonnage().getWaiter().addNext(new Runnable() {
						public void run() {
							int EnergyLoos = Formulas.getLoosEnergy(F.getLvl(), type==1, type==5);
							int Energy = F.getPersonnage().get_energy() - EnergyLoos;
							if(Energy < 0) Energy = 0;
							F.getPersonnage().set_energy(Energy);
							if(Energy == 0) {
								F.getPersonnage().set_PDV(1);
								F.getPersonnage().set_Ghosts();
							} else {
								F.getPersonnage().warpToSavePos();
								F.getPersonnage().set_PDV(1);
							}
							if(F.getPersonnage().isOnline())
								SocketManager.GAME_SEND_Im_PACKET(F.getPersonnage(), "034;"+EnergyLoos);
							F.getPersonnage().refreshMapAfterFight();
						}
					}, 1000);
				} **/
        	/*}
		}
		for(Fighter i : loser)
		{
			if(this.type != Constants.FIGHT_TYPE_AGRESSION)
			{
				if(i.getPDV() == 0 || i.hasLeft())
				{
					Packet.append("0;").append(i.getId()).append(";").append(i.getName()).append(";").append(i.getLvl()).append(";1").append(";").append(i.xpString(";")).append(";;;;|");
				}else
				{
					Packet.append("0;").append(i.getId()).append(";").append(i.getName()).append(";").append(i.getLvl()).append(";0").append(";").append(i.xpString(";")).append(";;;;|");
				}
			}else
        	{
				if(i.getFighterType() != Fighter.FighterType.PLAYER)
					continue;
        		// Si c'est un neutre, on ne gagne pas de points
        		int winH = 0;
        		int winD = 0;
        		/*if(_init1.getPersonnage().getAlignement() != Alignement.NEUTRE && _init0.getPersonnage().getAlignement() != Alignement.NEUTRE)
    			{
        			if(_init1.getPersonnage().getAccount().getCurIp().compareTo(_init0.getPersonnage().getAccount().getCurIp()) != 0 || Server.config.isMulePvp())
            		{
            			winH = Formulas.calculHonorWin(winner,loser,i);
        			}
    			}*/
        		
        		/*Player P = i.getPersonnage();
        		if(P.getHonor()+winH<0)winH = -P.getHonor();
        		P.addHonor(winH);
        		if(P.getDeshonor()-winD<0) winD = 0;
        		P.setDeshonor(P.getDeshonor()-winD);
        		Packet.append("0;").append(i.getId()).append(";").append(i.getName()).append(";").append(i.getLvl()).append(";").append((i.isDead() ?  "1" : "0" )).append(";");
        		Packet.append((P.getAlignement()!=Alignement.NEUTRE?World.data.getExpLevel(P.getGrade()).pvp:0)).append(";");
        		Packet.append(P.getHonor()).append(";");
        		int maxHonor = World.data.getExpLevel(P.getGrade()+1).pvp;
        		if(maxHonor == -1)maxHonor = World.data.getExpLevel(P.getGrade()).pvp;
        		Packet.append((P.getAlignement()!=Alignement.NEUTRE?maxHonor:0)).append(";");
        		Packet.append(winH).append(";");
        		Packet.append(P.getGrade()).append(";");
        		Packet.append(P.getDeshonor()).append(";");
        		Packet.append(winD);
        		Packet.append(";;0;0;0;0;0|");
        	}
		}
		if(World.data.getCollector(map) != null && this.type == 4)//On a un percepteur ONLY PVM ?
		{
			Collector p = World.data.getCollector(map);
			long winxp 	= (int)Math.floor(Formulas.getXpWinPerco(p,winner,loser,totalXP)/100);
			long winkamas 	= (int)Math.floor(Formulas.getKamasWinPerco(minkamas,maxkamas)/100);
			p.setXp(p.getXp()+winxp);
			p.setKamas(p.getKamas()+winkamas);
			Packet.append("5;").append(p.getId()).append(";").append(p.getFirstNameId()).append(",").append(p.getLastNameId()).append(";").append(World.data.getGuild(p.get_guildID()).getLevel()).append(";0;");
			Guild G = World.data.getGuild(p.get_guildID());
			Packet.append(G.getLevel()).append(";");
			Packet.append(G.getExperience()).append(";");
			Packet.append(World.data.getGuildXpMax(G.getLevel())).append(";");
			Packet.append(";");//XpGagner
			Packet.append(winxp).append(";");//XpGuilde
			Packet.append(";");//Monture
			
			String drops = "";

    		Map<Integer,Integer> itemWon = new TreeMap<Integer,Integer>();
    		
    		for(Entry<Integer,Integer> tauxByItem: possibleDrops.entrySet())
    		{
    			int t = (int)(tauxByItem.getValue()*100);//Permet de gerer des taux>0.01
    			int jet = Formulas.getRandomValue(0, 100*100);
    			if(jet < t)
    			{
    				ObjectTemplate OT = World.data.getObjectTemplate(tauxByItem.getKey());
    				if(OT == null)continue;
    				//on ajoute a la liste
    				itemWon.put(OT.getId(),(itemWon.get(OT.getId())==null?0:itemWon.get(OT.getId()))+1);
    			}
    		}
    		for(Entry<Integer,Integer> entry : itemWon.entrySet())
    		{
    			ObjectTemplate OT = World.data.getObjectTemplate(entry.getKey());
    			if(OT == null)continue;
    			if(drops.length() >0)drops += ",";
    			drops += entry.getKey()+"~"+entry.getValue();
    			Object obj = OT.createNewItem(entry.getValue(), false);
    			p.addObject(obj);
    			World.data.addObject(obj, true);
    		}
    		Packet.append(drops).append(";");//Drop
    		Packet.append(winkamas).append("|");
			
			World.database.getCollectorData().update(p);
		}
        return Packet.toString();
    }*/

    private void updateTraque(Collection<Fighter> winner, Collection<Fighter> loser) {
        Player curp = null;
        for(Fighter F : winner){
        	if(F.isInvocation())continue;
        	if(winner.size() == 1) curp = F.getPersonnage();
        }
        for(Fighter F : loser){
        	if(F.isInvocation())continue;
        	if(curp != null && curp.getStalk() != null && curp.getStalk().getTraque() == F.getPersonnage()){
        		SocketManager.GAME_SEND_MESSAGE(curp, "Thomas Sacre : Contrat fini, reviens me voir pour recuperer ta recompense.", "000000");
        		curp.getStalk().setTraque(null);
        		curp.getStalk().setTime(-2);
        	}
        }
    }

    public boolean verifIfFightEnded() {
        if (state != FightState.ACTIVE) return true;
        boolean team0dead = true;
        boolean team1dead = true;
        for (Fighter entry : this.team0.getTeam().values()) {
            if (!entry.isDead()) {
                team0dead = false;
                break;
            }
        }
        for (Fighter entry : this.team1.getTeam().values()) {
            if (!entry.isDead()) {
                team1dead = false;
                break;
            }
        }
        if(team0dead) {
            onFightEnded(team1, team0);
            return true;
        }
        else {
            if(team1dead){
                onFightEnded(team0,team1);
                return true;
            }
        }
        return false;
    }

    public void onFightEnded(Team winner, Team looser){
        ArrayList<Fighter> winners = cleanTeam(winner.getTeam().values());
        ArrayList<Fighter> loosers = cleanTeam(looser.getTeam().values());
        state = FightState.FINISHED;
        GlobalThread.unregisterAction(turnTimer);
        oldMap.onFightEnd(this);
        this.send(getGE(winner,looser));
        for(Player perso: spectator.values()){
            perso.refreshMapAfterFight();
        }
        for(Fighter fighter : winners){
            this.onFighterWin(fighter);
            fighter.getFightable().setFight(null);
            fighter.getFightable().setFighter(null);
        }
        for(Fighter fighter : loosers){
            this.onFighterLoose(fighter);
            fighter.getFightable().setFight(null);
            fighter.getFightable().setFighter(null);
        }
        /*for(Entry<Integer, Fighter> entry : this.team0.getTeam().entrySet())//Team mob sauf en d?fie/aggro
        {
            Player perso = entry.getValue().getPersonnage();
            if(perso == null)continue;
            perso.setDuel(-1);
            perso.setReady(false);
            perso.setFight(null);
        }
        switch(type)//Team joueurs
        {
            case Constants.FIGHT_TYPE_CHALLENGE://D?fie
            case Constants.FIGHT_TYPE_AGRESSION://Aggro
                for(Entry<Integer, Fighter> entry : this.team1.getTeam().entrySet())
                {
                    Player perso = entry.getValue().getPersonnage();
                    if(perso == null)continue;
                    perso.setDuel(-1);
                    perso.setReady(false);
                    perso.setFight(null);
                }
            break;

        }


        for(Fighter F : winTeam){
            if(F.getPerco() != null)
            {
                //On actualise la guilde+Message d'attaque FIXME
                for(Player z : World.data.getGuild(_guildID).getMembers())
                {
                    if(z == null) continue;
                    if(z.isOnline())
                    {
                        SocketManager.GAME_SEND_gITM_PACKET(z, Collector.parsetoGuild(z.getGuild().getId()));
                        SocketManager.GAME_SEND_MESSAGE(z, "Votre percepteur remporte la victoire.", Server.config.getMotdColor());
                    }
                }
                F.getPerco().set_inFight((byte)0);
                F.getPerco().set_inFightID((byte)-1);
                for(Player z : World.data.getMap(F.getPerco().getMap().getId()).getPlayers()){
                    if(z == null) continue;
                    SocketManager.GAME_SEND_MAP_PERCO_GMS_PACKETS(z.getAccount().getGameClient(), z.getMap());
                }
            }

        }
        //Pour les perdant ont TP au point de sauvegarde
        for(Fighter F : looseTeam) {
            if (F.getPerco() != null) {
                getOldMap().getNpcs().remove(F.getPerco().getId());
                SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getOldMap(), F.getPerco().getId());
                _perco.DelPerco(F.getPerco().getId());
                World.database.getCollectorData().delete(F.getPerco());
                //On actualise la guilde+Message d'attaque FIXME
                for (Player z : World.data.getGuild(_guildID).getMembers()) {
                    if (z == null) continue;
                    if (z.isOnline()) {
                        SocketManager.GAME_SEND_gITM_PACKET(z, Collector.parsetoGuild(z.getGuild().getId()));
                        SocketManager.GAME_SEND_MESSAGE(z, "Votre percepteur est mort.", Server.config.getMotdColor());
                    }
                }
            }
            if (!F.hasLeft() || F.getFighterType() != Fighter.FighterType.PLAYER) continue;

            }
        }*/

	}

	public void onFighterDie(Fighter target) {
		SocketManager.GAME_SEND_FIGHT_PLAYER_DIE_TO_FIGHT(Fight.this,7,target.getId());
		target.get_fightCell(false).getFighters().clear();// Supprime tout causait bug si port?/porteur
		
		if(target.isState(Constants.ETAT_PORTEUR)) {
			Fighter f = target.get_isHolding();
			f.setFightCell(f.get_fightCell(false));
			f.get_fightCell(false).addFighter(f);//Le bug venait par manque de ceci, il ni avait plus de firstFighter
			f.setState(Constants.ETAT_PORTE, 0);//J'ajoute ceci quand m?me pour signaler qu'ils ne sont plus en ?tat port?/porteur
			target.setState(Constants.ETAT_PORTEUR, 0);
			f.set_holdedBy(null);
			target.set_isHolding(null);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 950, f.getId()+"", f.getId()+","+Constants.ETAT_PORTE+",0");
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 950, target.getId()+"", target.getId()+","+Constants.ETAT_PORTEUR+",0");
		}

        for(Entry<Integer,Fighter> entry : target.getTeam().getTeam().entrySet()){
            if(entry.getValue().getInvocator() == null)continue;
            if(entry.getValue().getPDV() == 0)continue;
            if(entry.getValue().isDead())continue;
            if(entry.getValue().getInvocator().getId() == target.getId())//si il a ?t? invoqu? par le joueur mort
            {
                onFighterDie(entry.getValue());
                getOrdreJeu().removeFighter(target);

                if(team0.getTeam().containsKey(entry.getValue().getId())) team0.getTeam().remove(entry.getValue().getId());
                else if (team1.getTeam().containsKey(entry.getValue().getId())) team1.getTeam().remove(entry.getValue().getId());
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 999, target.getId()+"", getOrdreJeu().generateGTLPaquet());
            }
        }

		if(target.getMob() != null) {
			//Si c'est une invocation, on la retire de la liste
			try{
				boolean isStatic = false;
				for(int id : Constants.STATIC_INVOCATIONS)if(id == target.getMob().getGrade().getTemplate().getId())isStatic = true;
				if(target.isInvocation() && !isStatic)
				{
					//Il ne peut plus jouer, et est mort on revient au joueur pr?cedent pour que le startTurn passe au suivant

					//Il peut jouer, et est mort alors on passe son tour pour que l'autre joue, puis on le supprime de l'index sans probl?mes
					if(target.canPlay() && currentFighter.getId() == target.getId()){
	    				endTurn();
					}
					
					getOrdreJeu().removeFighter(target);
					
					if(team0.getTeam().containsKey(target.getId())) team0.getTeam().remove(target.getId());
					else if (team1.getTeam().containsKey(target.getId())) team1.getTeam().remove(target.getId());
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 999, target.getId()+"", getOrdreJeu().generateGTLPaquet());
				}
			}catch(Exception e){e.printStackTrace();}
		}
		//on supprime les glyphes du joueur
		for(Glyphe g : glyphes){
			//Si c'est ce joueur qui l'a lanc?
			if(g.get_caster().getId() == target.getId()){
				SocketManager.GAME_SEND_GDZ_PACKET_TO_FIGHT(Fight.this, 7, "-", g.get_cell().getId(), g.get_size(), 4);
				SocketManager.GAME_SEND_GDC_PACKET_TO_FIGHT(Fight.this, 7, g.get_cell().getId());
				glyphes.remove(g);
			}
		}
		
		//on supprime les pieges du joueur
		ArrayList<Piege> Ps = new ArrayList<Piege>();
		Ps.addAll(traps);
		for(Piege p : Ps)
		{
			if(p.get_caster().getId() == target.getId())
			{
				p.desappear();
				traps.remove(p);
			}
		}
		verifIfFightEnded();
	}

	public int getTeamID(int guid){
		if(team0.getTeam().containsKey(guid))
			return 0;
		if(team1.getTeam().containsKey(guid))
			return 1;
		if(spectator.containsKey(guid))
			return 4;
		return -1;
	}

	public void tryCaC(Player perso, int cellID){
		Fighter caster = perso.getFighter();
		
		if(caster == null)return;
		
		if(currentFighter.getId() != caster.getId())//Si ce n'est pas a lui de jouer
			return;

		if(perso.getObjectByPos(ObjectPosition.ARME) == null)//S'il n'a pas de CaC
		{
			if(get_curFighterPA() < 4)//S'il n'a pas assez de PA
				return;


			SocketManager.GAME_SEND_GAS_PACKET_TO_FIGHT(Fight.this, 7, perso.getId());
			
			//Si le joueur est invisible
			if(caster.isHide()){
                caster.unHide(-1);
            }
			
			Fighter target = map.getCases().get(cellID).getFirstFighter();
			
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 303, perso.getId()+"", cellID+"");
			if(target != null) {
           		int dmg = Formulas.getRandomJet("1d5+0");
           		int finalDommage = Formulas.calculFinalDommage(Fight.this,caster, target,Constants.ELEMENT_NEUTRE, dmg,false,true, -1);
                finalDommage = SpellEffect.applyOnHitBuffs(finalDommage,target,caster,Fight.this);//S'il y a des buffs sp?ciaux
				if(finalDommage>target.getPDV())finalDommage = target.getPDV();//Target va mourrir
				target.removePDV(finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 100, caster.getId()+"", target.getId()+","+finalDommage);
			}
            set_curFighterPA(get_curFighterPA() - 4);
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 102,perso.getId()+"",perso.getId()+",-4");
			SocketManager.GAME_SEND_GAF_PACKET_TO_FIGHT(Fight.this, 7, 0, perso.getId());
			
			if(target.getPDV() <=0)
				onFighterDie(target);
			verifIfFightEnded();
		}else
		{
			Object arme = perso.getObjectByPos(ObjectPosition.ARME);
			
			//Pierre d'?mes = EC
			if(arme.getTemplate().getType() == ObjectType.PIERRE_AME)
			{
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 305, perso.getId()+"", "");//Echec Critique Cac
				SocketManager.GAME_SEND_GAF_PACKET_TO_FIGHT(Fight.this, 7, 0, perso.getId());//Fin de l'action
				endTurn();
			
				return;
			}
			
			int PACost = arme.getTemplate().getPaCost();
			
			if(get_curFighterPA() < PACost)//S'il n'a pas assez de PA
			{
				
				return;
			}
			SocketManager.GAME_SEND_GAS_PACKET_TO_FIGHT(Fight.this, 7, perso.getId());
			
			boolean isEc = arme.getTemplate().getTauxEC() != 0 && Formulas.getRandomValue(1, arme.getTemplate().getTauxEC()) == arme.getTemplate().getTauxEC();
			if(isEc)
			{
				if(Server.config.isDebug()) Log.addToLog(perso.getName()+" Echec critique sur le CaC ");
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 305, perso.getId()+"", "");//Echec Critique Cac
				SocketManager.GAME_SEND_GAF_PACKET_TO_FIGHT(Fight.this, 7, 0, perso.getId());//Fin de l'action
				endTurn();
			}else
			{
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 303, perso.getId()+"", cellID+"");
				boolean isCC = caster.testIfCC(arme.getTemplate().getTauxCC());
				if(isCC)
				{
					if(Server.config.isDebug()) Log.addToLog(perso.getName()+" Coup critique sur le CaC");
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 301, perso.getId()+"", "0");
				}
				
				//Si le joueur est invisible
				if(caster.isHide())caster.unHide(-1);
				
				ArrayList<SpellEffect> effets = arme.getEffects();
				if(isCC)
				{
					effets = arme.getCritEffects();
				}
				for(SpellEffect SE : effets)
				{
					if(state != FightState.ACTIVE)break;
					ArrayList<Fighter> cibles = Pathfinding.getCiblesByZoneByWeapon(Fight.this, arme.getTemplate().getType(), map.getCases().get(cellID),caster.get_fightCell(false).getId());
					SE.setTurn(0);
					SE.applyToFight(Fight.this, caster, cibles, true);
				}
				set_curFighterPA(get_curFighterPA() - PACost);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(Fight.this, 7, 102,perso.getId()+"",perso.getId()+",-"+PACost);
				SocketManager.GAME_SEND_GAF_PACKET_TO_FIGHT(Fight.this, 7, 0, perso.getId());
				verifIfFightEnded();
			}
		}
	}

	public Fighter getCurFighter(){
		return currentFighter;
	}

	public void leftFight(Player target) {
		Team playerTeam = target.getFighter().getTeam();
        Team otherTeam = playerTeam==team0?team1:team0;
        Fighter T = target.getFighter();
		if(T != null) {
            target.setDuel(-1);
            target.setFight(null);
            target.setSitted(false);
            target.setAway(false);
            playerTeam.removeFighter(T);
            this.map.removeEntity(T);
            T.getCell().removeCreature(T);
            T.getCell().removeFighter(T);
            if(getOrdreJeu() != null){
                getOrdreJeu().removeFighter(T);
            }
            if(this.currentFighter == T)
                endTurn();
            if (this.state == FightState.PLACEMENT) {
                target.refreshMapAfterFight();
                T.getTeam().getFlag().onFighterDismiss(T);
            }
            else
                this.onFighterLoose(T);
            final StringBuilder packet = new StringBuilder("GE");
            packet.append(System.currentTimeMillis()-startTime).append("|0|")
                    .append(type).append("|");
            for(Fighter f : otherTeam.getTeam().values()){
                if(f.getPDV() == 0 || f.hasLeft()){
                    packet.append("2;").append(f.getId()).append(";").append(f.getName()).append(";").append(f.getLvl()).append(";1").append(";").append(f.xpString(";")).append(";;;;|");
                }
                else{
                    packet.append("2;").append(f.getId()).append(";").append(f.getName()).append(";").append(f.getLvl()).append(";0").append(";").append(f.xpString(";")).append(";;;;|");
                }
            }
            for(Fighter f : playerTeam.getTeam().values()){
                if(f.getPDV() == 0 || f.hasLeft()){
                    packet.append("0;").append(f.getId()).append(";").append(f.getName()).append(";").append(f.getLvl()).append(";1").append(";").append(f.xpString(";")).append(";;;;|");
                }
                else{
                    packet.append("0;").append(f.getId()).append(";").append(f.getName()).append(";").append(f.getLvl()).append(";0").append(";").append(f.xpString(";")).append(";;;;|");
                }
            }
			target.send(packet.toString());
			/*            if(type == Constants.FIGHT_TYPE_AGRESSION || type == Constants.FIGHT_TYPE_PVM || type == Constants.FIGHT_TYPE_PVT)
                        {
                            int EnergyLoos = Formulas.getLoosEnergy(P.getLevel(), type ==1, type ==5);
                            int Energy = P.getEnergy() - EnergyLoos;
                            if(Energy < 0) Energy = 0;
                            P.setEnergy(Energy);
                            if(P.isOnline())
                                SocketManager.GAME_SEND_Im_PACKET(P, "034;"+EnergyLoos);

                            if(type == Constants.FIGHT_TYPE_AGRESSION)
                            {
                                int honor = P.getHonor()-500;
                                if(honor < 0) honor = 0;
                                P.setHonor(honor);
                                if(P.isOnline())
                                    SocketManager.GAME_SEND_Im_PACKET(P, "076;"+honor);
                            }

                            /**
                            try
                            {
                                Thread.sleep(1000);
                            }catch(Exception E){};
                            **/
                            /*if(Energy == 0)
                            {
                                P.setPdv(1);
                                P.setGhosts();
                            }else
                            {
                                P.warpToSavePos();
                                P.setPdv(1);
                            }
                        }

                        if(P.isOnline())
                        {/**
                            try
                            {
                                Thread.sleep(200);
                            }catch(Exception E){};**/
                            /*SocketManager.GAME_SEND_GV_PACKET(P);
                            P.refreshMapAfterFight();
                        }

                        //si c'?tait a son tour de jouer
                        if(currentFighter == null)return;
                        if(currentFighter.getId() == F.getId())
                        {
                            endTurn();
						}
					}else if(state == FightState.PLACEMENT)
					{
						boolean isValid1 = false;
						/*if(T != null) {
							if(_init0 != null &&_init0.getPersonnage() != null)
							{
								if(F.getPersonnage().getId() == _init0.getPersonnage().getId())
								{
									isValid1 = true;
								}
							}
							if(_init1 != null &&_init1.getPersonnage() != null)
							{
								if(F.getPersonnage().getId() == _init1.getPersonnage().getId())
								{
									isValid1 = true;
								}
							}
						}*/
						
						/*if(isValid1)//Celui qui fait l'action a lancer le combat et leave un autre personnage
						{
							if((T.getTeam() == F.getTeam()) && (T.getId() != F.getId()))
							{
								if(Server.config.isDebug()) Console.instance.println("EXULSION DE : "+T.getPersonnage().getName());
								SocketManager.GAME_SEND_ON_FIGHTER_KICK(Fight.this, T.getPersonnage().getId(), getTeamID(T.getId()));
								if(type == Constants.FIGHT_TYPE_AGRESSION || type == Constants.FIGHT_TYPE_CHALLENGE || type == Constants.FIGHT_TYPE_PVT) SocketManager.GAME_SEND_ON_FIGHTER_KICK(Fight.this, T.getPersonnage().getId(), getOtherTeamID(T.getId()));
								Player P = T.getPersonnage();
								P.setDuel(-1);
								P.setReady(false);
								P.setFight(null);
								P.setSitted(false);
								P.setAway(false);
								
								if(P.isOnline())
								{/**
									try
									{
										Thread.sleep(200);
									}catch(Exception E){};**/
									/*SocketManager.GAME_SEND_GV_PACKET(P);
									P.refreshMapAfterFight();
								}
								
								//On le supprime de la team
								if(team0.getTeam().containsKey(T.getId()))
								{
									T.getCell().removeFighter(T);
									team0.getTeam().remove(T.getId());
								}
								else if(team1.getTeam().containsKey(T.getId()))
								{
									T.getCell().removeFighter(T);
									team1.getTeam().remove(T.getId());
								}
								for(Player z : getOldMap().getPlayers()) FightStateAddFlag(Fight.this.getOldMap(), z);
							}
						}else if(T == null)//Il leave de son plein gr? donc (T = null)
						{
							boolean isValid2 = false;
							/*if(_init0 != null &&_init0.getPersonnage() != null)
							{
								if(F.getPersonnage().getId() == _init0.getPersonnage().getId())
								{
									isValid2 = true;
								}
							}
							if(_init1 != null &&_init1.getPersonnage() != null)
							{
								if(F.getPersonnage().getId() == _init1.getPersonnage().getId())
								{
									isValid2 = true;
								}
							}*/
							
							/*if(isValid2)//Soit il a lancer le combat => annulation du combat
							{
								for(Fighter f : Fight.this.getFighters(F.getTeam2()))
								{
									Player P = f.getPersonnage();
									P.setDuel(-1);
									P.setReady(false);
									P.setFight(null);
									P.setSitted(false);
									P.setAway(false);
									
									if(F.getPersonnage().getId() != f.getPersonnage().getId())//Celui qui a join le fight revient sur la map
									{
										if(P.isOnline())
										{ /**
											try
											{
												Thread.sleep(200);
											}catch(Exception E){};**/
											/*SocketManager.GAME_SEND_GV_PACKET(P);
											P.refreshMapAfterFight();
										}
									}else//Celui qui a fait le fight meurt + perte honor
									{
										if(type == Constants.FIGHT_TYPE_AGRESSION || type == Constants.FIGHT_TYPE_PVM || type == Constants.FIGHT_TYPE_PVT)
										{
											int EnergyLoos = Formulas.getLoosEnergy(P.getLevel(), type ==1, type ==5);
											int Energy = P.getEnergy() - EnergyLoos;
											if(Energy < 0) Energy = 0;
											P.setEnergy(Energy);
											if(P.isOnline())
												SocketManager.GAME_SEND_Im_PACKET(P, "034;"+EnergyLoos);
											
											if(type == Constants.FIGHT_TYPE_AGRESSION)
											{
												int honor = P.getHonor()-500;
												if(honor < 0) honor = 0;
												P.setHonor(honor);
												if(P.isOnline())
													SocketManager.GAME_SEND_Im_PACKET(P, "076;"+honor);
											}
											
											/**
											try
											{
												Thread.sleep(1000);
											}catch(Exception E){};**/
											/*if(Energy == 0)
											{
												P.setPdv(1);
												P.setGhosts();
											}else
											{
												P.warpToSavePos();
												P.setPdv(1);
											}
										}
										
										if(P.isOnline())
										{	/**
											try
											{
												Thread.sleep(200);
											}catch(Exception E){};**/
											/*SocketManager.GAME_SEND_GV_PACKET(P);
											P.refreshMapAfterFight();
										}
									}
								}
								if(type == Constants.FIGHT_TYPE_AGRESSION || type == Constants.FIGHT_TYPE_CHALLENGE || type == Constants.FIGHT_TYPE_PVT)
								{
									for(Fighter f : Fight.this.getFighters(F.getOtherTeam()))
									{
										if(f.getPersonnage() == null) continue;
										Player P = f.getPersonnage();
										P.setDuel(-1);
										P.setReady(false);
										P.setFight(null);
										P.setSitted(false);
										P.setAway(false);
										
										if(P.isOnline())
										{
											SocketManager.GAME_SEND_GV_PACKET(P);
											P.refreshMapAfterFight();
										}
									}
								}
								oldMap.onFightEnd(this);
                                if(type == Constants.FIGHT_TYPE_PVT)
								{
									//On actualise la guilde+Message d'attaque FIXME
									for(Player z : World.data.getGuild(_guildID).getMembers())
									{
										if(z == null) continue;
										if(z.isOnline())
										{
											SocketManager.GAME_SEND_gITM_PACKET(z, Collector.parsetoGuild(z.getGuild().getId()));
											SocketManager.GAME_SEND_MESSAGE(z, "Votre percepteur remporte la victioire.", Server.config.getMotdColor());
										}
									}
									_perco.set_inFight((byte)0);
									_perco.set_inFightID((byte)-1);
									for(Player z : World.data.getMap(_perco.getMap().getId()).getPlayers())
									{
										if(z == null) continue;
										SocketManager.GAME_SEND_MAP_PERCO_GMS_PACKETS(z.getAccount().getGameClient(), z.getMap());
									}
								}
								if(type == Constants.FIGHT_TYPE_PVM)
								{			
									Alignement alignement = Alignement.NEUTRE;
									if(team1.getTeam().size() >0){
										 alignement = team1.getTeam().get(team1.getTeam().keySet().toArray()[0]).getMob().getGrade().getTemplate().getAlignement();
									}
                                    //Si groupe non fixe
									if(!_mobGroup.isFix())World.data.getMap(map.getId()).spawnGroup(alignement, _mobGroup.getCell().getId());//Respawn d'un groupe

								}
							}else//Soit il a rejoin le combat => Left de lui seul
							{
								SocketManager.GAME_SEND_ON_FIGHTER_KICK(Fight.this, F.getPersonnage().getId(), getTeamID(F.getId()));
								if(type == Constants.FIGHT_TYPE_AGRESSION || type == Constants.FIGHT_TYPE_CHALLENGE || type == Constants.FIGHT_TYPE_PVT) SocketManager.GAME_SEND_ON_FIGHTER_KICK(Fight.this, F.getPersonnage().getId(), getOtherTeamID(F.getId()));
								Player P = F.getPersonnage();
								P.setDuel(-1);
								P.setReady(false);
								P.setFight(null);
								P.setSitted(false);
								P.setAway(false);
								
								if(type == Constants.FIGHT_TYPE_AGRESSION || type == Constants.FIGHT_TYPE_PVM || type == Constants.FIGHT_TYPE_PVT)
								{
									int EnergyLoos = Formulas.getLoosEnergy(P.getLevel(), type ==1, type ==5);
									int Energy = P.getEnergy() - EnergyLoos;
									if(Energy < 0) Energy = 0;
									P.setEnergy(Energy);
									if(P.isOnline())
										SocketManager.GAME_SEND_Im_PACKET(P, "034;"+EnergyLoos);
									
									if(type == Constants.FIGHT_TYPE_AGRESSION)
									{
										int honor = P.getHonor()-500;
										if(honor < 0) honor = 0;
										P.setHonor(honor);
										if(P.isOnline())
											SocketManager.GAME_SEND_Im_PACKET(P, "076;"+honor);
									}
									/**
									try
									{
										Thread.sleep(1000);
									}catch(Exception E){};**/
									/*if(Energy == 0)
									{
										P.setPdv(1);
										P.setGhosts();
									}else
									{
										P.warpToSavePos();
										P.setPdv(1);
									} 
								}
								
								if(P.isOnline())
								{/**
									try
									{
										Thread.sleep(200);
									}catch(Exception E){};**/
									/*SocketManager.GAME_SEND_GV_PACKET(P);
									P.refreshMapAfterFight();
								}
								
								//On le supprime de la team
								if(team0.getTeam().containsKey(F.getId()))
								{
									F.getCell().removeFighter(F);
									team0.getTeam().remove(F.getId());
								}
								else if(team1.getTeam().containsKey(F.getId()))
								{
									F.getCell().removeFighter(F);
									team1.getTeam().remove(F.getId());
								}
								for(Player z : getOldMap().getPlayers()) FightStateAddFlag(Fight.this.getOldMap(), z);
							}
						}
					}else
					{
						if(Server.config.isDebug()) Log.addToLog("Phase de combat non geree, type de combat:"+ type +" T:"+T+" F:"+F);
					}
				break;
				default:
					if(Server.config.isDebug()) Log.addToLog("Type de combat non geree, type de combat:"+ type +" T:"+T+" F:"+F);
				break;
			}
		}else//Si perso en spec
		{
			SocketManager.GAME_SEND_GV_PACKET(perso);
			spectator.remove(perso.getId());
			perso.setSitted(false);
			perso.setFight(null);
			perso.setAway(false);
		}*/
        }
	}

	public int getNextLowerFighterGuid()
	{
		int g = -1;
		for(Fighter f : getFighters(3))
		{
			if(f.getId() < g)
				g = f.getId();
		}
		g--;
		return g;
	}

	public void addFighterInTeam(Fighter f, Team team){
        team.getTeam().put(f.getId(), f);
  	}

	/*public String parseFightInfos() {
		StringBuilder infos = new StringBuilder();
		infos.append(id).append(";");
        long time = startTime + TimeZone.getDefault().getRawOffset();
        infos.append((startTime == 0?"-1":time)).append(";");
		//Team1
		infos.append("0,");//0 car toujours joueur :)
		switch(type)
		{
			/*case Constants.FIGHT_TYPE_CHALLENGE:
				infos.append("0,");
				infos.append(team0.getTeam().size()).append(";");
				//Team2
				infos.append("0,");
				infos.append("0,");
				infos.append(team1.getTeam().size()).append(";");
			break;
			
			/*case Constants.FIGHT_TYPE_AGRESSION:
				//infos.append(_init0.getPersonnage().getAlignement().getId()).append(",");
				infos.append(team0.getTeam().size()).append(";");
				//Team2
				infos.append("0,");
				//infos.append(_init1.getPersonnage().getAlignement().getId()).append(",");
				infos.append(team1.getTeam().size()).append(";");
			break;*/
			
			/*case Constants.FIGHT_TYPE_PVM:
				infos.append("0,");
				infos.append(team0.getTeam().size()).append(";");
				//Team2
				infos.append("1,");
				infos.append(team1.getTeam().get(team1.getTeam().keySet().toArray()[0]).getMob().getGrade().getTemplate().getAlignement()).append(",");
				infos.append(team1.getTeam().size()).append(";");
			break;
			
			/*case Constants.FIGHT_TYPE_PVT:
				infos.append("0,");
				infos.append(team0.getTeam().size()).append(";");
				//Team2
				infos.append("4,");
				infos.append("0,");
				infos.append(team1.getTeam().size()).append(";");
			break;*/
	//	}
//		return infos.toString();
//	}
	
	public void showCaseToAll(int guid, int cellID)
	{
		ArrayList<GameClient> PWs = new ArrayList<>();
		for(Entry<Integer,Fighter> e : team0.getTeam().entrySet())
		{
			if(e.getValue().getPersonnage() != null && e.getValue().getPersonnage().getAccount().getGameClient() != null)
				PWs.add(e.getValue().getPersonnage().getAccount().getGameClient());
		}
		for(Entry<Integer,Fighter> e : team1.getTeam().entrySet())
		{
			if(e.getValue().getPersonnage() != null && e.getValue().getPersonnage().getAccount().getGameClient() != null)
				PWs.add(e.getValue().getPersonnage().getAccount().getGameClient());
		}
		for(Entry<Integer,Player> e : spectator.entrySet())
		{
			PWs.add(e.getValue().getAccount().getGameClient());
		}
		SocketManager.GAME_SEND_FIGHT_SHOW_CASE(PWs, guid, cellID);
	}

	public void joinAsSpect(Player p)
	{
		if(!specOk  || state != FightState.ACTIVE)
		{
			SocketManager.GAME_SEND_Im_PACKET(p, "157");
			return;
		}
		p.getCell().removeCreature(p);
		SocketManager.GAME_SEND_GJK_PACKET(p, state.id, 0, 0, 1, 0, type.id);
		SocketManager.GAME_SEND_GS_PACKET(p);
		SocketManager.GAME_SEND_GTL_PACKET(p,Fight.this);
		SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(p.getMap(), p.getId());
		SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS(Fight.this, map,p);
		SocketManager.GAME_SEND_GAMETURNSTART_PACKET(p,currentFighter.getId(),Constants.TIME_BY_TURN);
		spectator.put(p.getId(), p);
		p.setFight(Fight.this);
		SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(Fight.this, 7, "036;"+p.getName());
	}

	public boolean playerDisconnect(Player player, boolean verif) {
		Fighter fighter = player.getFighter();
		
		if(fighter == null) 
			return false;
		
		if(this.getState() == FightState.INITIATION || this.getState() == FightState.FINISHED) {
			if(!verif)
				leftFight(player);
			return false;
		}
		
		if(!verif) {
			if(this.getState() != FightState.ACTIVE ) {
				player.setReady(true);
				player.getFight().onReadyChange();
			}
		}
		
		if(!verif) {
			fighter.setTurnRemaining(20);
			SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(this, 7, new StringBuilder("1182;").append(fighter.getName()).append("~").append(fighter.getTurnRemainingBeforeExpulsion()).toString());
		}
		return true;
	}

	public boolean playerReconnect(Player player) {
		Fighter fighter = player.getFighter();
		
		if(fighter == null) 
			return false;
		
		if(this.getState() == FightState.INITIATION) 
			return false;
		
		fighter.setTurnRemaining(-1);
		
		if(this.getState() == FightState.FINISHED) 
			return false;

		SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(this, 7, new StringBuilder("1184;").append(fighter.getName()).toString());

		if(this.getState() == FightState.ACTIVE) {
			SocketManager.GAME_SEND_GJK_PACKET(player, getState().id, 0, 0, 0, 0, getType());
		} else {
			if(getType() == Constants.FIGHT_TYPE_CHALLENGE)
					SocketManager.GAME_SEND_GJK_PACKET(player, 2, 1, 1, 0, 0, getType());
			else
				SocketManager.GAME_SEND_GJK_PACKET(player, 2, 0, 1, 0, 0, getType());
		}

		
		ArrayList<Fighter> all = new ArrayList<>();
		all.addAll(this.team0.getTeam().values());
		all.addAll(this.team1.getTeam().values());
		
		
		//SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_PLAYER(player, this.map, (fighter.getTeam().getId() == 0 ? _init0 : _init1).getId(), fighter);//Indication de la team
		SocketManager.GAME_SEND_STATS_PACKET(player);
		SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS(this, this.map, player);

		if(this.getState() == FightState.PLACEMENT) {
			//SocketManager.GAME_SEND_FIGHT_PLACES_PACKET(player.getAccount().getGameClient(), getMap().getPlaces(), this._st1);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, player.getId()+"", player.getId()+","+Constants.ETAT_PORTE+",0");
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, player.getId()+"", player.getId()+","+Constants.ETAT_PORTEUR+",0");
		} else {
			SocketManager.GAME_SEND_GS_PACKET(player);
			SocketManager.GAME_SEND_GTL_PACKET(player,this);
			SocketManager.GAME_SEND_GAMETURNSTART_PACKET(player, currentFighter.getId(), Constants.TIME_BY_TURN);
		
		   	for(Fighter f1 : getFighters(3)) {
		   		if(player.getAccount() == null) 
					continue;
		   		if(player.getAccount().getGameClient() == null)
		   			continue;
		   		
				for(int state : f1.getStates().keySet())
					SocketManager.GAME_SEND_GA_PACKET(player.getAccount().getGameClient(), String.valueOf(7), String.valueOf(950), String.valueOf(f1.getId()), f1.getId() + "," + state + ",1");
		   	}
		}

		for(Fighter f1: all)
			if(f1.isHide())
				player.send("GA;150;" + f1.getId() + ";" + f1.getId() + ",4");
		
		SocketManager.GAME_SEND_ILF_PACKET(player, 0);
		return true;
	}

    /**
     * Permet de savoir si une cellule est libre ou non
     * @param cell cellule a analyser
     * @return true si elle occupe
     * false sinon
     */
    public boolean isOccuped(int cell){
        return map.getCases().get(cell).getFighters().size() > 0 || map.getCases().get(cell).getCreature().size()>0;
    }



    public void send(String message){
        team0.send(message);
        team1.send(message);
        for(Player player : spectator.values()){
            player.send(message);
        }
    }

    protected ArrayList<Case> parsePlaces(int num){
        return CryptManager.parseStartCell(map, num);
    }



    /**
     * Ajoute tout les fightables dans la team
     * @param team team dans laquelle les fightables doivent etre ajoutes
     * @param fightables entitees a rajouter dans la team
     */
    protected void addPlayer(Team team,ArrayList<? extends Fightable> fightables){
        for(Fightable fightable : fightables){
            addPlayer(team,fightable);
        }
    }

    /**
     * Ajoute un joueur dans la team
     * @param team
     * @param fightable
     */
    protected void addPlayer(Team team,Fightable fightable){
        final String placesMessages = "GP" + this.map.getPlaces() + "|" + team.getId();
        Case startCell;
        do{
            startCell = team.startCells.get(r.nextInt(team.startCells.size()));
        } while(!startCell.getCreature().isEmpty());
        Fighter f = new Fighter(this,fightable,startCell,team);
        f.setFightCell(startCell);
        team.addFighter(f);
        f.send(placesMessages);
        map.registerOnMap(f);
        startCell.addFighter(f);
        startCell.addCreature(f);
    }

    /**
     * Retire tout ce qui n est pas a prendre en compte comme les invocations
     * @param fighters collection a nettoyer
     * @return collection sans les invocations ni les doubles
     */
    private ArrayList<Fighter> cleanTeam(Collection<Fighter> fighters){
        ArrayList<Fighter> newCollection = new ArrayList<>();
        for(Fighter fighter : fighters){
            if(fighter.getFighterType() != Fighter.FighterType.CLONE && !fighter.isInvocation())
                newCollection.add(fighter);
        }
        return newCollection;
    }

    //region packet

    /**
     * Permet de cr?er un paquet GIC envoyer
     * @return
     */
    private String generateGICPacket(){
        StringBuilder packet = new StringBuilder();
        packet.append("GIC|");
        for(Fighter p : team0.getTeam().values()) {
            packet.append(p.getId()).append(";").append(p.getVisibleCell().getId()).append(";1|");
        }
        for(Fighter p : team1.getTeam().values()) {
            packet.append(p.getId()).append(";").append(p.getVisibleCell().getId()).append(";1|");
        }
        return packet.toString();
    }
    //endregion

}
