package org.ancestra.evolutive.client;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.entity.creature.Helper;

public class PlayerHelper extends Helper<Player> {
    public PlayerHelper(Player creature) {
        super(creature);
    }

    @Override
    public String getGmPacket() {
        StringBuilder str = new StringBuilder();
        boolean isFighting = getCreature().getFight() != null;

        //----GM Header----
        if(isFighting)
            str.append(getCreature().getFighter().getCell().getId());
        else
            str.append(getCreature().getCell().getId());//Id de la cellule
        str.append(";").append(getCreature().getOrientation()).append(";")//Orientation effective
                .append("0").append(";")//FIXME:?
                .append(getCreature().getId()).append(";")//id de la personne
                .append(getCreature().getName()).append(";")//nom de la personne
        //Fin du header

                .append(getCreature().getClasse().getId());//Classe
        if(!isFighting)
            str.append((getCreature().getTitle()>0?(","+getCreature().getTitle()):""));

        str.append(";").append(getCreature().getGFX()).append("^")
                .append(getCreature().getSize()).append(";")//gfxID^size
                .append(getCreature().getSex()).append(";");
        if(isFighting)
            str.append(getCreature().getLevel()).append(";");

        str.append(getCreature().getAlignement().getId()).append(",")//1,0,0,4055064
            .append("0").append(",")//FIXME(think)
            .append((getCreature().isShowWings() ? getCreature().getGrade() : "0")).append(",")
            .append(getCreature().getLevel() + getCreature().getId());

        if(getCreature().isShowWings() && getCreature().getDeshonor() > 0)
            str.append(",").append(getCreature().getDeshonor() > 0 ? 1 : 0).append(';');
        else
            str.append(";");

        str.append((getCreature().getColor1()==-1?"-1":Integer.toHexString(getCreature().getColor1()))).append(";");
        str.append((getCreature().getColor2()==-1?"-1":Integer.toHexString(getCreature().getColor2()))).append(";");
        str.append((getCreature().getColor3()==-1?"-1":Integer.toHexString(getCreature().getColor3()))).append(";");
        str.append(getCreature().getGMStuffString()).append(";");
        if(!isFighting){
            if(Server.config.isAuraSystem())
                str.append((getCreature().getLevel() > 99 ? (getCreature().getLevel() > 199 ? (2) : (1)) : (0))).append(";");
            else
                str.append("0;");
            str.append(";");//Emote
            str.append(";");//Emote timer
            if(getCreature().getGuildMember() != null && getCreature().getGuildMember().getGuild().getMembers().size()>9)
                str.append(getCreature().getGuildMember().getGuild().getName()).append(";").append(getCreature().getGuildMember().getGuild().getEmblem()).append(";");
            else
                str.append(";;");
            str.append(getCreature().getSpeed()).append(";");//Restriction
        } else {
            str.append(getCreature().getPdv()).append(";");
            str.append(getCreature().getTotalStats().getEffect(Constants.STATS_ADD_PA)).append(";");
            str.append(getCreature().getTotalStats().getEffect(Constants.STATS_ADD_PM)).append(";");
            str.append(getCreature().getTotalStats().getEffect(Constants.STATS_ADD_RP_NEU)).append(";");
            str.append(getCreature().getTotalStats().getEffect(Constants.STATS_ADD_RP_TER)).append(";");
            str.append(getCreature().getTotalStats().getEffect(Constants.STATS_ADD_RP_FEU)).append(";");
            str.append(getCreature().getTotalStats().getEffect(Constants.STATS_ADD_RP_EAU)).append(";");
            str.append(getCreature().getTotalStats().getEffect(Constants.STATS_ADD_RP_AIR)).append(";");
            str.append(getCreature().getTotalStats().getEffect(Constants.STATS_ADD_AFLEE)).append(";");
            str.append(getCreature().getTotalStats().getEffect(Constants.STATS_ADD_MFLEE)).append(";");
            str.append(getCreature().getFighter().getTeam().getId()).append(";");
        }
        str.append((getCreature().isOnMount() && getCreature().getMount() != null ? getCreature().getMount().getColor() : "")).append(";");
        str.append(";");
        return str.toString();
    }

}
