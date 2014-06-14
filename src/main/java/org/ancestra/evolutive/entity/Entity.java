package org.ancestra.evolutive.entity;


public class Entity {
    private final int id;
    private final String name;

    public Entity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Retourne l identifiant de l entitee
     * Celui ci n'est pas unique!
     * @return identifiant
     */
    public int getId() {
        return id;
    }

    /**
     * Retourne le nom de l entitee
     * @return nom de l entitee
     */
    public String getName() {
        return name;
    }
}
