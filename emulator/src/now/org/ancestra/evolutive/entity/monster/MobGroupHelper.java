package org.ancestra.evolutive.entity.monster;

import org.ancestra.evolutive.entity.Helper;

import java.util.Map;

public class MobGroupHelper extends Helper<MobGroup> {
    public MobGroupHelper(MobGroup creature) {
        super(creature);
    }

    @Override
    public String getGmPacket() {
        StringBuilder id = new StringBuilder();
        StringBuilder gfx = new StringBuilder();
        StringBuilder level = new StringBuilder();
        StringBuilder color = new StringBuilder();
        StringBuilder toReturn = new StringBuilder();

        boolean isFirst = true;

        if(getCreature().getMobsGrade().isEmpty()){
            return "";
        }

        for(Map.Entry<Integer, MobGrade> entry : getCreature().getMobsGrade().entrySet()) {
            if(!isFirst) {
                id.append(",");
                gfx.append(",");
                level.append(",");
            }
            id.append(entry.getValue().getTemplate().getId());
            gfx.append(entry.getValue().getTemplate().getGfx()).append("^100");
            level.append(entry.getValue().getLevel());
            color.append(entry.getValue().getTemplate().getColors()).append(";0,0,0,0;");

            isFirst = false;
        }

        toReturn.append(getCreature().getCell().getId()).append(";").append(getCreature().getOrientation()).append(";0;").append(getCreature().getId())
                .append(";").append(id).append(";-3;").append(gfx).append(";").append(level).append(";").append(color);

        return toReturn.toString();
    }
}
