package org.genial.ark.domain;

public class Exercice {

    private final String content;

    private final boolean fixed;

    public Exercice(String content, Boolean fixed){
        this.content = content;
        this.fixed = fixed;
    }

    public String getContent(){return this.content;}

    public boolean isFixed() {
        return fixed;
    }

}
