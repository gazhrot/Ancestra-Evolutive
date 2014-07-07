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


    public final int id;
    private IdType(int id){
        this.id = id;
    }
}
