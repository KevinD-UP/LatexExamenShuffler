package org.genial.ark.domain;


public class PlainExercice implements Exercice {

    private String content;

    public boolean isFixed() {
        return fixed;
    }

    private final boolean fixed;

    public PlainExercice(String content, Boolean fixed){
        this.content = content;
        this.fixed = fixed;
    }

    @Override
    public String toString(){return Document.BEGIN_EXO + "\n" + this.content + Document.END_EXO + "\n";}



}
