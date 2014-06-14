package org.ancestra.evolutive.entity.collector;

import org.ancestra.evolutive.entity.Creature;
import org.ancestra.evolutive.entity.Helper;

public class CollectorHelper extends Helper<Collector> {


    public CollectorHelper(Collector collector) {
        super(collector);
    }

    @Override
    public String getGmPacket() {
        StringBuilder sock = new StringBuilder();

        if(getCreature().getState() == Creature.STATE.IN_FIGHT)return "";
        sock.append(getCreature().getCell().getId()).append(";");
        sock.append(getCreature().getOrientation()).append(";");
        sock.append("0").append(";");
        sock.append(getCreature().getId()).append(";");
        sock.append(getCreature().getFirstNameId()).append(",")
                .append(getCreature().getLastNameId()).append(";");
        sock.append("-6").append(";");
        sock.append("6000^100;");
        sock.append(getCreature().getGuild().getId()).append(";");
        sock.append(getCreature().getGuild().getName()).append(";");
        sock.append(getCreature().getGuild().getEmblem());

    return sock.toString();
    }
}
