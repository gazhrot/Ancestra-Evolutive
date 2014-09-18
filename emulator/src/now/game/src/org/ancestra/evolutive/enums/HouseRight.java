package org.ancestra.evolutive.enums;

public enum HouseRight {
    G_SHOW_BLASON(2),  //Afficher blason pour membre de la guilde
    O_SHOW_BLASON(4),  //Afficher blason pour les autres
    G_NO_CODE(8),      //Entrer sans code pour la guilde
    O_CANT_OPEN(16),   //Entrer impossible pour les non-guildeux
    CG_NO_CODE(32),    //Coffre sans code pour la guilde
    CO_CANT_OPEN(64),  //Coffre impossible pour les non-guildeux
    G_TELEPORT(128),   //Guilde droit au repos
    G_CAN_SLEEP(256);  //Guilde droit a la TP  

    private final int id;

    private HouseRight(int id) {
        this.id = id;
    }
    
    public int getId() {
    	return id;
    }
}