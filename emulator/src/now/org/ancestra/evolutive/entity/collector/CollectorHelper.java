package org.ancestra.evolutive.entity.collector;

import org.ancestra.evolutive.common.Constants;
import org.ancestra.evolutive.entity.Helper;

public class CollectorHelper extends Helper<Collector> {


    public CollectorHelper(Collector collector) {
        super(collector);
    }

    @Override
    public String getGmPacket() {
        StringBuilder sock = new StringBuilder();

        sock.append(getCreature().getCell().getId()).append(";");
        sock.append(getCreature().getOrientation()).append(";");
        sock.append("0").append(";");
        sock.append(getCreature().getId()).append(";");
        sock.append(getCreature().getFirstNameId()).append(",")
                .append(getCreature().getLastNameId()).append(";");
        sock.append("-6").append(";");
        sock.append("6000^100;");
        if(getCreature().getFight() == null){
            sock.append(getCreature().getGuild().getId()).append(";");
            sock.append(getCreature().getGuild().getName()).append(";");
            sock.append(getCreature().getGuild().getEmblem());
        }
        else {
            sock.append(getCreature().getLevel());
            sock.append("1;2;4;");//FIXME
            sock.append(getCreature().getStats().getEffect(Constants.STATS_ADD_PA)).append(";");
            sock.append(getCreature().getStats().getEffect(Constants.STATS_ADD_PM)).append(";");
            sock.append(getCreature().getStats().getEffect(Constants.STATS_ADD_RP_NEU)).append(";");
            sock.append(getCreature().getStats().getEffect(Constants.STATS_ADD_RP_TER)).append(";");
            sock.append(getCreature().getStats().getEffect(Constants.STATS_ADD_RP_FEU)).append(";");
            sock.append(getCreature().getStats().getEffect(Constants.STATS_ADD_RP_EAU)).append(";");
            sock.append(getCreature().getStats().getEffect(Constants.STATS_ADD_RP_AIR)).append(";");
            sock.append(getCreature().getStats().getEffect(Constants.STATS_ADD_AFLEE)).append(";");
            sock.append(getCreature().getStats().getEffect(Constants.STATS_ADD_MFLEE)).append(";");
        }

    return sock.toString();
    }
}
