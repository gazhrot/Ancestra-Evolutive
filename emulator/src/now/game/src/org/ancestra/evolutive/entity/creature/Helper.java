package org.ancestra.evolutive.entity.creature;


public abstract class Helper<T extends Creature> {
    private final T creature;

    public Helper(T creature){
        this.creature = creature;
    }

    /**
     * Retourne le packet GM de la creature;
     * Le GM|+ ou - n'est pas compris
     * @return packet GM sans le prefixe
     */
    public abstract String getGmPacket();

    /**
     * Retourne la creature
     * @return creature
     */
    protected final T getCreature(){
        return this.creature;
    }
}
