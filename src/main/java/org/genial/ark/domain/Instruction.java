package org.genial.ark.domain;

public class Instruction implements ContentExercise{

    private String content;

    public Instruction(String content){
        this.content = content;
    }

    @Override
    public String toString(){
        return this.content;
    }
}
