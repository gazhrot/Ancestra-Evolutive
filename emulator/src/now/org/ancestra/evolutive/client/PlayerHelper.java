package org.ancestra.evolutive.client;

import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.entity.Helper;

public class PlayerHelper extends Helper<Player> {
    public PlayerHelper(Player creature) {
        super(creature);
    }

    @Override
    public String getGmPacket() {
        StringBuilder str = new StringBuilder();
        if(getCreature().getFight() != null) return "";

        str.append(getCreature().getCell().getId()).append(";").append(getCreature().getOrientation()).append(";");
        str.append("0").append(";");//FIXME:?
        str.append(getCreature().getId()).append(";").append(getCreature().getName()).append(";").append(getCreature().getClasse().getId());
        str.append((getCreature().getTitle()>0?(","+getCreature().getTitle()+";"):(";")));
        str.append(getCreature().getGfx()).append("^").append(getCreature().getSize()).append(";");//gfxID^size
        str.append(getCreature().getSex()).append(";").append(getCreature().getAlign()).append(",");//1,0,0,4055064
        str.append("0").append(",");//FIXME(think)
        str.append((getCreature().isShowWings() ? getCreature().getGrade() : "0")).append(",");
        str.append(getCreature().getLevel() + getCreature().getId());

        if(getCreature().isShowWings() && getCreature().getDeshonor() > 0)
            str.append(",").append(getCreature().getDeshonor() > 0 ? 1 : 0).append(';');
        else
            str.append(";");

        str.append((getCreature().getColor1()==-1?"-1":Integer.toHexString(getCreature().getColor1()))).append(";");
        str.append((getCreature().getColor2()==-1?"-1":Integer.toHexString(getCreature().getColor2()))).append(";");
        str.append((getCreature().getColor3()==-1?"-1":Integer.toHexString(getCreature().getColor3()))).append(";");
        str.append(getCreature().getGMStuffString()).append(";");
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
        str.append((getCreature().isOnMount() && getCreature().getMount() != null ? getCreature().getMount().getColor() : "")).append(";");
        str.append(";");
        return str.toString();
    }
}
