package org.ancestra.evolutive.entity.npc;

import org.ancestra.evolutive.entity.Helper;

/**
 * Created by Guillaume on 14/06/2014.
 */
public class NpcHelper extends Helper<Npc> {
    
    public NpcHelper(Npc creature) {
        super(creature);
    }

    @Override
    public String getGmPacket() {
        StringBuilder sock = new StringBuilder();
        sock.append(getCreature().getCell().getId()).append(";");
        sock.append(getCreature().getOrientation()).append(";");
        sock.append("0").append(";");
        sock.append(getCreature().getId()).append(";");
        sock.append(getCreature().getTemplate().getId()).append(";");
        sock.append("-4").append(";");//type = NPC

        StringBuilder taille = new StringBuilder();
        if(getCreature().getTemplate().getScaleX() == getCreature().getTemplate().getScaleY())
            taille.append(getCreature().getTemplate().getScaleY());
        else
            taille.append(getCreature().getTemplate().getScaleX()).append("x").append(getCreature().getTemplate().getScaleY());

        sock.append(getCreature().getTemplate().getGfx()).append("^").append(taille.toString()).append(";");
        sock.append(getCreature().getTemplate().getSex()).append(";");
        sock.append((getCreature().getTemplate().getColor1() != -1?Integer.toHexString(getCreature().getTemplate().getColor1()):"-1")).append(";");
        sock.append((getCreature().getTemplate().getColor2() != -1?Integer.toHexString(getCreature().getTemplate().getColor2()):"-1")).append(";");
        sock.append((getCreature().getTemplate().getColor3() != -1?Integer.toHexString(getCreature().getTemplate().getColor3()):"-1")).append(";");
        sock.append(getCreature().getTemplate().getAcces()).append(";");
        sock.append((getCreature().getTemplate().getExtraClip()!=-1?(getCreature().getTemplate().getExtraClip()):(""))).append(";");
        sock.append(getCreature().getTemplate().getCustomArtWork());
        return sock.toString();
    }
}
