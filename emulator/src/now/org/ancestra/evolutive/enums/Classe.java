package org.ancestra.evolutive.enums;


public enum Classe {
    FECA(1,(short)10300,323,(short)7398,299),
    OSAMODAS(2,(short)10284,372,(short)7545,340),
    ENUTROF(3,(short)10299,271,(short)7442,182),
    SRAM(4,(short)10285,263,(short)7392,313),
    XELOR(5,(short)10298,300,(short)7332,327),
    ECAFLIP(6,(short)10276,296,(short)7446,313),
    ENIRIPSA(7,(short)10283,299,(short)7316,222),
    IOP(8,(short)10294,280,(short)7427,267),
    CRA(9,(short)10292,284,(short)7378,310),
    SADIDA(10,(short)10279,254,(short)7395,371),
    SACRIEUR(11,(short)10296,243,(short)7336,197),
    PANDAWA(12,(short)10289,236,(short)10289,236);


    private final int id;
    private final short inkarnamStartMap;
    private final int inkarnamStartCell;
    private final short astrubStartMap;
    private final int astrubStartCell;


    private Classe(int id, short inkarnamStartMap, int inkarnamStartCell, short astrubStartMap, int astrubStartCell) {
        this.id = id;
        this.inkarnamStartMap = inkarnamStartMap;
        this.inkarnamStartCell = inkarnamStartCell;
        this.astrubStartMap = astrubStartMap;
        this.astrubStartCell = astrubStartCell;
    }

    public int getId() {
        return id;
    }

    public short getInkarnamStartMap() {
        return inkarnamStartMap;
    }

    public int getInkarnamStartCell() {
        return inkarnamStartCell;
    }

    public short getAstrubStartMap() {
        return astrubStartMap;
    }

    public int getAstrubStartCell() {
        return astrubStartCell;
    }
}
