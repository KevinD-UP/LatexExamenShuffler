package org.genial.ark.domain;

public class Question {

    private boolean isFixed;

    private String content;

    public boolean isFixed(){
        return isFixed;
    }

    public Question(String content, boolean isFixed){
        this.content = content;
        this.isFixed = isFixed;
    }

    @Override
    public String toString(){
        return content;
    }

}
