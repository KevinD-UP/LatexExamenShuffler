package org.genial.ark.domain;

import java.util.ArrayList;

public class SubSet implements  DocumentBlock{

    private int numberToPick;

    private ArrayList<Exercice> exercices;

    public SubSet(ArrayList<Exercice> exercices, int numberToPick){
        this.exercices = exercices;
        this.numberToPick = numberToPick;
    }
}
