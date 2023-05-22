package org.genial.ark.domain;

public class Exercice {

    private String content;

    public boolean isFixed() {
        return fixed;
    }

    private final boolean fixed;

    public Exercice(String content, Boolean fixed){
        this.content = content;
        this.fixed = fixed;
    }


}
