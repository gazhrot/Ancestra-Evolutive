package org.ancestra.evolutive.database;

interface DAO<T> {
	/**
     * Charge l objet designe par l id
     * @param id id de l objet desire
     * @return null si l objet n es pas trouve
     * L objet si il est trouve
     */
	T load(Object obj);
	
    /**
     * Rafraichit l objet dans la base de donnee
     * @param obj objet a rafraichir
     * @return true si l operation a reussi
     * false si l operation a echouee
     */
	boolean update(T obj);
}