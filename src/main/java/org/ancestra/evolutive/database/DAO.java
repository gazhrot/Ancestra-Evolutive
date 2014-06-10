package org.ancestra.evolutive.database;

interface DAO<T> {
    /**
     * Inscrit le nouvel objet dans la base de donnee
     * @param obj objet a créer dans la base de donnee
     * @return true si l operation a reussie
     * false si l operation a echouee
     */
	boolean create(T obj);

    /**
     * Efface l objet de la base de donnee
     * @param obj objet a effacer
     * @return true si l operation a reussie
     * false si l operation a echouee
     */
	boolean delete(T obj);

    /**
     * Rafraichit l objet dans la base de donnee
     * @param obj objet a rafraichir
     * @return true si l operation a reussi
     * false si l operation a echouee
     */
	boolean update(T obj);

    /**
     * Charge l objet designe par l id
     * @param id id de l objet desire
     * @return null si l objet n es pas trouve
     * L objet si il est trouve
     */
	T load(int id);
}