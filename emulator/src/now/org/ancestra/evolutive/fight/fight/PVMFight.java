package org.ancestra.evolutive.fight.fight;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.common.Formulas;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.ancestra.evolutive.entity.monster.Mob;
import org.ancestra.evolutive.entity.monster.MobGroup;
import org.ancestra.evolutive.fight.Fighter;
import org.ancestra.evolutive.fight.ordreJeu.ProspectionBasedOrdreJeu;
import org.ancestra.evolutive.fight.team.MobTeam;
import org.ancestra.evolutive.fight.team.PlayerTeam;
import org.ancestra.evolutive.fight.team.Team;
import org.ancestra.evolutive.guild.GuildMember;
import org.ancestra.evolutive.map.Maps;
import org.ancestra.evolutive.object.*;
import org.ancestra.evolutive.object.Object;
import org.ancestra.evolutive.other.Drop;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Guillaume on 04/08/2014.
 * Hope you'll like it!
 */
public class PVMFight extends Fight {
    private static final Random r = new Random();
    private final Player initiateur;
    private final MobGroup mobGroup;
    private final ProspectionBasedOrdreJeu ordreJeu;

    public PVMFight(int id, Maps oldMap, Player initiateur1, MobGroup mobGroup) {
        super(FightType.PVM, id, oldMap);
        logger = (Logger)LoggerFactory.getLogger("Fight."+initiateur1.getName());
        this.initiateur = initiateur1;
        this.mobGroup = mobGroup;
        sendDescriptionPacket();
        initTeam();
        ordreJeu = new ProspectionBasedOrdreJeu(team0.getTeam().values(),team1.getTeam().values());
        this.send(this.getMap().getGmMessage());
    }

    protected void sendDescriptionPacket() {
        initiateur.send(this.GJKPacket);
    }

    protected void initTeam() {
        int random = r.nextInt(2);
        team0 = new PlayerTeam(random,parsePlaces(random),this,initiateur);
        random = Math.abs(random-1);
        team1 = new MobTeam(random,parsePlaces(random),mobGroup,this);
        addPlayer(team0, initiateur);
        addPlayer(team1, mobGroup.getMobs());

        this.send("ILF0");//Desactive regeneration
        for(Fighter f : team0.getTeam().values()){
            this.send("GA;950;" + f.getId() + ";" + f.getId() + ",8,0" );
            this.send("GA;950;" + f.getId() + ";" + f.getId() + ",3,0" );
        }
        for(Fighter f : team1.getTeam().values()){
            this.send("GA;950;" + f.getId() + ";" + f.getId() + ",8,0" );
            this.send("GA;950;" + f.getId() + ";" + f.getId() + ",3,0" );
        }
    }

    @Override
    public String getGE(Team winner, Team looser){
        final StringBuilder packet = new StringBuilder("GE");
        packet.append(System.currentTimeMillis()-startTime).append("|")
                .append(initiateur.getId()).append("|0|");
        final long totalXP = totalXP();


        if(winner instanceof PlayerTeam){
            logger.trace("L equipe de joueur l emporte");
            int minkamas = 0,maxkamas = 0;
            int groupPP = getTeamProspection(winner.getTeam().values());
            //Calcul des drops possibles
            Map<Integer,Integer> possibleDrops = new TreeMap<>();
            for(Fighter F : looser.getTeam().values()){
                minkamas += ((Mob)F.getFightable()).getGrade().getTemplate().getMinKamas();
                maxkamas += ((Mob)F.getFightable()).getGrade().getTemplate().getMaxKamas();
                for(Drop D : ((Mob)F.getFightable()).getGrade().getTemplate().getDrops()){
                    if(D.getMinProsp() <= groupPP){
                        //On augmente le taux en fonction de la PP
                        int taux = (int)((groupPP * D.getTaux(((Mob)F.getFightable()).getGrade().getGrade())* Server.config.getRateDrop())/100);
                        possibleDrops.put(D.getItemId(),taux);
                    }
                }
            }

            // region Capture
            /*boolean mobCapturable = true;
            for(Fighter F : looser.team.values()) {
                try {
                    mobCapturable &= ((Mob)F.getFightable()).getGrade().getTemplate().isCapturable();
                }catch (Exception e) {
                    mobCapturable = false;
                    break;
                }
            }
            if(mobCapturable){
                boolean isFirst = true;
                int maxLvl = 0;
                String pierreStats = "";


                for(Fighter F : looser.team.values())	//Cr?ation de la pierre et verifie si le groupe peut ?tre captur?
                {
                    if(!isFirst)
                        pierreStats += "|";

                    pierreStats += ((Mob)F.getMob()).getGrade().getTemplate().getId() + "," + F.getLvl();//Converti l'ID du monstre en Hex et l'ajoute au stats de la futur pierre d'?me

                    isFirst = false;

                    if(F.getLvl() > maxLvl)	//Trouve le monstre au plus haut lvl du groupe (pour la puissance de la pierre)
                        maxLvl = F.getLvl();
                }
                SoulStone pierrePleine = new SoulStone(World.data.getNewObjectGuid(), 1, 7010, -1, pierreStats);	//Cr?e la pierre d'?me

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
                            Fighter f = _captureur.get(Formulas.getRandomValue(0, _captureur.size() - 1));	//R?cup?re un captureur au hasard dans la liste
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
            }*/

            // endregion

            for (Fighter fighter : winner.getTeam().values()) {
                if (fighter.hasLeft()) continue;
                long winxp = Formulas.getXpWinPvm2(fighter, winner.getTeam().values(), looser.getTeam().values(), totalXP);
                AtomicReference<Long> XP = new AtomicReference<>();
                XP.set(winxp);

                long guildxp = getGuildXp(fighter, winxp);
                XP.set(winxp - guildxp);
                long mountxp = 0;

                if (fighter.getFighterType() != Fighter.FighterType.PLAYER && ((Player) fighter.getFightable()).isOnMount()) {
                    mountxp = Formulas.getMountXpWin(fighter, XP);
                    fighter.getPersonnage().getMount().addExperience(mountxp);
                    SocketManager.GAME_SEND_Re_PACKET(fighter.getPersonnage(), "+", fighter.getPersonnage().getMount());
                }


                int winKamas = Formulas.getKamasWin(fighter, winner.getTeam().values(), minkamas, maxkamas);
                String drops = "";
                //Drop system
                Map<Integer, Integer> itemWon = new TreeMap<>();

                for (Map.Entry<Integer, Integer> tauxByItem : possibleDrops.entrySet()) {
                    int t = (int) (tauxByItem.getValue() * 100);//Permet de gerer des taux>0.01
                    int jet = Formulas.getRandomValue(0, 100 * 100);
                    if (jet < t) {
                        ObjectTemplate OT = World.data.getObjectTemplate(tauxByItem.getKey());
                        if (OT == null) continue;
                        //on ajoute a la liste
                        itemWon.put(OT.getId(), (itemWon.get(OT.getId()) == null ? 0 : itemWon.get(OT.getId())) + 1);

                    }
                }
                for (Map.Entry<Integer, Integer> entry : itemWon.entrySet()) {
                    ObjectTemplate OT = World.data.getObjectTemplate(entry.getKey());
                    if (OT == null) continue;
                    if (drops.length() > 0) drops += ",";
                    drops += entry.getKey() + "~" + entry.getValue();
                    Object obj = OT.createNewItem(entry.getValue(), false);
                    if (fighter.getPersonnage().addObject(obj, true))
                        World.data.addObject(obj, true);
                }
                //fin drop system
                winxp = XP.get();
                if (winxp != 0)
                    fighter.getPersonnage().addXp(winxp);
                if (winKamas != 0)
                    fighter.getPersonnage().addKamas(winKamas);
                if (guildxp > 0)
                    fighter.getPersonnage().getGuildMember().giveXpToGuild(guildxp);

                packet.append("2;").append(fighter.getId()).append(";").append(fighter.getName()).append(";").append(fighter.getLvl()).append(";").append((fighter.isDead() ? "1" : "0")).append(";");
                packet.append(fighter.xpString(";")).append(";");
                packet.append((winxp == 0 ? "" : winxp)).append(";");
                packet.append((guildxp == 0 ? "" : guildxp)).append(";");
                packet.append((mountxp == 0 ? "" : mountxp)).append(";");
                packet.append(drops).append(";");//Drop
                packet.append((winKamas == 0 ? "" : winKamas)).append("|");
            }
            for(Fighter fighter : looser.getTeam().values()){
                packet.append("0;").append(fighter.getId()).append(";").append(fighter.getName()).append(";").append(fighter.getLvl()).append(";1").append(";").append(fighter.xpString(";")).append(";;;;|");
            }
        }
        else {
            for(Fighter f : winner.getTeam().values()){
                if(f.getPDV() == 0 || f.hasLeft()){
                    packet.append("2;").append(f.getId()).append(";").append(f.getName()).append(";").append(f.getLvl()).append(";1").append(";").append(f.xpString(";")).append(";;;;|");
                }
                else{
                    packet.append("2;").append(f.getId()).append(";").append(f.getName()).append(";").append(f.getLvl()).append(";0").append(";").append(f.xpString(";")).append(";;;;|");
                }
            }
            for(Fighter f : looser.getTeam().values()){
                packet.append("0;").append(f.getId()).append(";").append(f.getName()).append(";").append(f.getLvl()).append(";1").append(";").append(f.xpString(";")).append(";;;;|");
            }
        }
        return packet.toString();
    }

    /**
     * Calcule la propsection totale du groupe
     * @param winner team winner sur laquelle l xp est calculee
     * @return la prospection totale
     */
    private int getTeamProspection(Collection<Fighter> winner) {
        int groupPP = 0;
        for(Fighter F : winner)
            groupPP += F.getTotalStats().getEffect(Constants.STATS_ADD_PROS);
        return groupPP<0?0:groupPP;
    }

    /**
     * Calcule l xp totale du groupe
     * @return xp total gagnee par le groupe
     */
    private long totalXP(){
        long totalXP = 0;
        for(Fighter F : team0.getTeam().values()){
            try {
                if(F.isDead())
                    totalXP += ((Mob) F.getFightable()).getGrade().getXp();
            } catch (ClassCastException e){
                break;
            }
        }
        for(Fighter F : team1.getTeam().values()){
            try {
                if (F.isDead())
                    totalXP += ((Mob) F.getFightable()).getGrade().getXp();
            } catch (ClassCastException e){
                break;
            }
        }
        return totalXP;
    }

    /**
     * Permet de connaitre l xp qui sera donnee a la guilde
     * @param fighter fighter auquel on s interesse
     * @param totalXp xp totale sur laquelle sera fait le calcul
     * @return xp a donner a la guilde
     */
    private long getGuildXp(Fighter fighter,long totalXp){
        if(fighter.getFightable() instanceof Player){
            Player personnage = (Player)fighter.getFightable();
            if(personnage.getGuild() != null){
                GuildMember gm = personnage.getGuildMember();
                double pXpGive = (double)gm.getXpGive()/100;
                double maxP = totalXp * pXpGive * 0.10;	//Le maximum donn� � la guilde est 10% du montant pr�lev� sur l'xp du combat
                double diff = Math.abs(personnage.getLevel()-personnage.getGuild().getLevel());
                double toGuild;
                if(diff >= 70) {
                    toGuild = maxP * 0.10;
                }
                else {
                    if(diff >= 31 && diff <= 69){
                        toGuild = maxP - ((maxP * 0.10) * (Math.floor((diff+30)/10)));
                    }
                    else {
                        if(diff >= 10 && diff <= 30){
                            toGuild = maxP - ((maxP * 0.20) * (Math.floor(diff/10))) ;
                        }
                        else {
                            toGuild = maxP;
                        }
                    }
                }
                return Math.round(toGuild);
            }
        }
        return 0;
    }


    @Override
    protected void onFighterLoose(Fighter looser) {

        if(looser.getFightable() instanceof Player){
            int EnergyLoos = 25*looser.getLvl();
            int Energy = looser.getPersonnage().getEnergy() - EnergyLoos;
            if (Energy < 0) Energy = 0;
            looser.getPersonnage().setEnergy(Energy);
            if (Energy == 0) {
                looser.getFightable().setPdv(1);
                looser.getPersonnage().setGhosts();
            } else {
                looser.getPersonnage().warpToSavePos();
                looser.getPersonnage().refreshMapAfterFight();
                looser.getPersonnage().setPdv(1);
            }
            looser.send("Im034;" + EnergyLoos);
        }
    }

    @Override
    protected void onFighterWin(Fighter winner) {
        if (winner.getFightable() instanceof Player) {
            if (winner.getPDV() <= 0)
                winner.getFightable().setPdv(1);
            else
                winner.getFightable().setPdv(winner.getFightable().getMaxPdv() * winner.getPDV() / winner.getPDVMAX());
            ((Player) winner.getFightable()).refreshMapAfterFight();
        }
    }

    @Override
    public ProspectionBasedOrdreJeu getOrdreJeu() {
        return ordreJeu;
    }

    @Override
    public boolean canJoinTeam(Player fighter, Team team) {
        return team instanceof PlayerTeam;
    }

    @Override
    public String getFightInfos() {
        StringBuilder infos = new StringBuilder();
        infos.append(this.getId()).append(";");
        long time = startTime + TimeZone.getDefault().getRawOffset();
        infos.append((startTime == 0?"-1":time)).append(";");

        infos.append(team0.getTeamType().id).append(",")
                .append("0").append(",")//Car on se fiche de l'alignement du joueur
                .append(team0.getTeam().size()).append(";");

        infos.append(team1.getTeamType().id).append(",")
                .append("0").append(",")//Car on se fiche de l'alignement des mobs?
                .append(team1.getTeam().size()).append(";");
        return infos.toString();
    }
}
