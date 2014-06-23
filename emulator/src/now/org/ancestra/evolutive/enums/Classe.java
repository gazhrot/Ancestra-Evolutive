package org.ancestra.evolutive.enums;


public enum Classe {
    FECA(1, 10300, 323, 7398, 299),
    OSAMODAS(2, 10284, 372, 7545, 340), 
    ENUTROF(3, 10299, 271, 7442, 182), 
    SRAM(4, 10285, 263, 7392, 313), 
    XELOR(5, 10298, 300, 7332, 327), 
    ECAFLIP(6, 10276, 296, 7446, 313), 
    ENIRIPSA(7, 10283, 299, 7316, 222), 
    IOP(8, 10294, 280, 7427, 267), 
    CRA(9, 10292, 284, 7378, 310), 
    SADIDA(10, 10279, 254, 7395, 371), 
    SACRIEUR(11, 10296, 243, 7336, 197), 
    PANDAWA(12, 10289, 236, 10289, 236);

    private final int id;
    private final int inkarnamStartMap;
    private final int inkarnamStartCell;
    private final int astrubStartMap;
    private final int astrubStartCell;

    private Classe(int id, int inkarnamStartMap, int inkarnamStartCell, int astrubStartMap, int astrubStartCell) {
        this.id = id;
        this.inkarnamStartMap = inkarnamStartMap;
        this.inkarnamStartCell = inkarnamStartCell;
        this.astrubStartMap = astrubStartMap;
        this.astrubStartCell = astrubStartCell;
    }

    public int getId() {
        return id;
    }

    public int getInkarnamStartMap() {
        return inkarnamStartMap;
    }

    public int getInkarnamStartCell() {
        return inkarnamStartCell;
    }

    public int getAstrubStartMap() {
        return astrubStartMap;
    }

    public int getAstrubStartCell() {
        return astrubStartCell;
    }
}
