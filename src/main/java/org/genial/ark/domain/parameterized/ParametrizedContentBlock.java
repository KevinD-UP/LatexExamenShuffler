package org.genial.ark.domain.parameterized;

public class ParametrizedContentBlock implements ContentBlock{

    private ParameterizedScope scope;

    private String content;

    public ParametrizedContentBlock(String content, ParameterizedScope scope){
        this.scope = scope;
        this.content = content;
    }

    @Override
    public String toString(){
        return "Parameterized \n\n" ;//+ content + "\n\n";
    }


}
