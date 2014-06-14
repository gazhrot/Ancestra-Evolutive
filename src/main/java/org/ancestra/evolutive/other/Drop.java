package org.ancestra.evolutive.other;

public class Drop
{
	private final int itemId;
	private final int prospectionNedded;
	private final float tauxGrade1;
    private final float tauxGrade2;
    private final float tauxGrade3;
    private final float tauxGrade4;
    private final float tauxGrade5;
	
	public Drop(int itm,int p,float t1,float t2,float t3,float t4,float t5)
	{
		itemId = itm;
		prospectionNedded = p;
		tauxGrade1 = t1;
        tauxGrade2 = t2;
        tauxGrade3 = t3;
        tauxGrade4 = t4;
        tauxGrade5 = t5;
	}

	public int getItemId() {
		return itemId;
	}

	public int getMinProsp() {
		return prospectionNedded;
	}

	public float getTaux(int grade) {
		switch(grade){
            case 1 :
                return tauxGrade1;
            case 2 :
                return tauxGrade2;
            case 3 :
                return tauxGrade3;
            case 4 :
                return tauxGrade4;
            case 5 :
                return tauxGrade5;
            default:
                return tauxGrade1;
        }
	}

}