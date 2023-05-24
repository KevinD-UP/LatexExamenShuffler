package org.genial.ark.domain.parameterized;

public class PlainContentBlock implements ContentBlock{

    String content;

    public PlainContentBlock(String content){
        this.content = content;
    }

    @Override
    public String toString(){
        return "Plain \n\n" ;//+ content + "\n\n";
    }

}
