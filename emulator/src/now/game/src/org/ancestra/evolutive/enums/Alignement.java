package org.ancestra.evolutive.enums;

public enum Alignement {
    NEUTRE(0),
    BONTARIEN(1),
    BRAKMARIEN(2),
    MERCENAIRE(3);

    /**
     * Valeur correspondante pour le client
     */
    private final int id;

    private Alignement(int id) {
        this.id = id;
    }
    
    /**
     * Permet de recuperer la valeur
     * @return l'id de l'alignement correspondant.
     */    
    public int getId() {
    	return id;
    }

    /**
     * fais la conversion nombre -> alignement
     * @param id  nombre correspondant pour le jeu
     * @return aligenement correspondant
     */
    public static Alignement getAlignement(int id) {
        switch (id) {
            case 1 :
                return BONTARIEN;
            case 2 :
                return BRAKMARIEN;
            case 3 :
                return MERCENAIRE;
            default:
                return NEUTRE;
        }
    }

    @Override
    public String toString(){
        return Integer.toString(this.id);
    }
}
