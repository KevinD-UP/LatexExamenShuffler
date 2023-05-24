package org.genial.ark.domain.parameterized;

import java.util.ArrayList;

public class Variable {

    public String getName() {
        return name;
    }

    private String name;

    private ArrayList<String> allowedValues;

    public Variable(String name, ArrayList<String> allowedValues){
        this.name = name;
        this.allowedValues = allowedValues;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : this.allowedValues){
            stringBuilder.append(s).append(";");
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
