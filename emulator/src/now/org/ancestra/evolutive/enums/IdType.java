package org.ancestra.evolutive.enums;

public enum IdType {
    CREATURE(-1),
    MONSTER(-2),
    MONSTER_GROUP(-3),
    PNJ(-4),
    SELLER(-5),
    COLLECTOR(-6),
    NON_PLAYER_MUTANT(-7),
    PLAYER_MUTANT(-8),
    FENCE(-9),
    PRISM(-10);


    /**
     * Id du type
     */
    public final int id;

    /**
     * Id maximal
     * /!\ Ce sont des id negatifs il faudra donc descendre
     */
    public final int MAXIMAL_ID;

    /**
     * Id minimal
     * /!\ Ce sont des id negatifs il faudra donc monter
     */
    public final int MINIMAL_ID;
    private IdType(int id){
        this.id = id;
        MAXIMAL_ID = id*100000000;
        MINIMAL_ID =((id-1)*100000000)+1;

    }
}
