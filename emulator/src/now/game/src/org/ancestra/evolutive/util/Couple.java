package org.ancestra.evolutive.util;

public class Couple<First, Second> {
    private First first;
    private Second second;

    public Couple(First first, Second second) {
         this.first = first;
         this.second = second;
    }
    
    public First getKey() {
    	return first;    	
    }
    
    public void setFirst(First first) {
    	this.first = first;
    }
    
    public Second getValue() {
    	return second;
    }
    
    public void setValue(Second second) {
    	this.second = second;
    }
}