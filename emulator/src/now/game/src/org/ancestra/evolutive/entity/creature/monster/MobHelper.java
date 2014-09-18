package org.ancestra.evolutive.entity.creature.monster;

import org.ancestra.evolutive.entity.creature.Helper;

public class MobHelper extends Helper<Mob> {
    public MobHelper(Mob creature) {
        super(creature);
    }

    @Override
    public String getGmPacket() {
        StringBuilder str = new StringBuilder();
        str.append(getCreature().getFighter().getCell().getId()).append(";");
        str.append(1).append(";");
        str.append("0;");
        str.append(getCreature().getId()).append(";");
        str.append(getCreature().getGrade().getTemplate().getId()).append(";");
        str.append("-2;");
        str.append(getCreature().getGrade().getTemplate().getGfx()).append("^100;");
        str.append(getCreature().getGrade().getGrade()).append(";");
        str.append(getCreature().getGrade().getTemplate().getColors().replace(",", ";")).append(";");
        str.append("0,0,0,0;");
        str.append(getCreature().getMaxPdv()).append(";");
        str.append(getCreature().getGrade().getPa()).append(";");
        str.append(getCreature().getGrade().getPm()).append(";");
        str.append(getCreature().getFighter().getTeam().getId());
        return str.toString();
    }
}
