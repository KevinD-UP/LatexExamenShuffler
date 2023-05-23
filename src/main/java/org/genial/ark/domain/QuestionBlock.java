package org.genial.ark.domain;

public class QuestionBlock implements ContentExercise{

    private String content;

    public QuestionBlock(String content){
        this.content = content;
    }

    @Override
    public String toString(){
        return this.content;
    }
}
