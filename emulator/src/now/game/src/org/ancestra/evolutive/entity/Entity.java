package org.ancestra.evolutive.entity;

import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.entity.creature.Helper;
import org.slf4j.LoggerFactory;

public class Entity {
    private final int id;
    private final String name;
    protected Helper<?> helper;
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

    /**
     * Retourne le helper de l entitee
     * @return helper
     */
    public Helper<?> getHelper(){
        return this.helper;
    }

    /**
     * Envoie un message a la creature
     * @param message
     */
    public void send(String message) {
        return;
    }

    /**
     * Recupere le logger de la creature
     * @return logger de la creature
     */
    public Logger getLogger(){
        return this.logger;
    }

}
