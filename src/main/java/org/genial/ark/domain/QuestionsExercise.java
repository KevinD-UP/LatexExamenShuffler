package org.genial.ark.domain;

import java.util.ArrayList;

public class QuestionsExercise implements Exercice{

    private ArrayList<ContentExercise> contentExerciseArrayList;


    public boolean isFixed() {
        return fixed;
    }

    private final boolean fixed;

    public QuestionsExercise(ArrayList<ContentExercise> contentExerciseArrayList, Boolean fixed){
        this.contentExerciseArrayList = contentExerciseArrayList;
        this.fixed = fixed;
    }

    @Override
    public String toString(){
        StringBuilder toString = new StringBuilder();
        toString.append(Document.BEGIN_EXO).append("\n");
        for(ContentExercise contentExercise : contentExerciseArrayList){
            toString.append(contentExercise.toString());
        }
        toString.append(Document.END_EXO).append("\n");
        return toString.toString();
    }

}
