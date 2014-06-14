package org.ancestra.evolutive.entity;


import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class Entity {
    private final int id;
    private final String name;
    protected final Logger logger;

    public Entity(int id, String name) {
        this.id = id;
        this.name = name;
        this.logger = (Logger)LoggerFactory.getLogger(name);
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
