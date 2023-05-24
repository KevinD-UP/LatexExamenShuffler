package org.genial.ark.domain.parameterized;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParametrizedContentBlock implements ContentBlock{

    private ParameterizedScope scope;

    private String content;



    public ParametrizedContentBlock(String content, ParameterizedScope scope){
        this.scope = scope;
        this.content = content;
    }

    private String replace(){
        char[] charArray = content.toCharArray();
        Pattern pattern = Pattern.compile("\\$([^\\$]+)\\$");
        Matcher matcher = pattern.matcher(this.content);
        // Check all occurrences
        while (matcher.find()) {
            if(matcher.hasMatch()){
                for(int i = matcher.start() +1 ; i < matcher.end(); i ++){
                    if(this.scope.isVariable(this.content.charAt(i))){
                        charArray[i] = this.scope.getValueForName(this.content.charAt(i)).charAt(0);
                    }
                }
            }
        }

        return new  String(charArray);

    }


    @Override
    public String toString(){
        String replaced = replace();
        return replaced;
    }

}
