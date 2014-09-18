package org.ancestra.evolutive.enums;

public enum GuildRight {
    SET_BOOST(2),			 	// Gerer les boosts
    SET_RIGHT(4), 			 	// Gerer les droits
    CAN_INVITE(8),  		 	// Inviter des joueurs  
    CAN_BAN(16),  				// Bannir un joueur
    ALL_XP(32),   				// Gerer les xps
    HIS_XP(64),   				// Gerer son xp
    SET_RANK(128),   			// Gerer les rangs
    POS_COLLECTOR(256), 		// Poser un percepteur
    GET_COLLECTOR(512), 		// Prendre les percepteurs
    /** 1024 & 2048 ? **/
    USE_PARK(4096), 			// Utiliser les enclos
    ADJUST_PARK(8192), 			// Amenager les enclos
    ADJUST_OTHER_MOUNT(16384); 	// Amenager les montures
   
    private final int id;

    private GuildRight(int id) {
        this.id = id;
    }
    
    public int getId() {
    	return id;
    }
    
    public String toString() {
    	return String.valueOf(id);
    }
}