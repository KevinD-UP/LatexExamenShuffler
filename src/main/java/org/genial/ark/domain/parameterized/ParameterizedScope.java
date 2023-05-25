package org.genial.ark.domain.parameterized;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ParameterizedScope {

    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(org.genial.ark.domain.parameterized.ParameterizedDocument.class);

    private HashMap<String,Variable> variables; // ALL OF THE VARIABLES IN A SCOPE, THE KEY IS THE VARIABLE NAME

    private ParameterizedDocument parameterizedDocument; // PARAMETERIZED DOCUMENT THE SCOPE IS RELEVANT IN TO REGISTER IT S VARIABLE THERRE


    public String getValueForName(char c){
        return  this.variables.get(String.valueOf(c)).getCurrentValue();
    }

    // Returns truc is there is an entry is variables hashmap with key c
    public boolean isVariable(char c){
        String name = String.valueOf(c);
        return this.variables.containsKey(name);
    }

    public ParameterizedScope(String scopeDeclaration, ParameterizedDocument parameterizedDocument){
        this.parameterizedDocument = parameterizedDocument;
        // PUTS EVERY VARIABLE IN AN HASHMAP WHERE KEY IS VARIABLE NAME
        ArrayList<Variable> variableArrayList = parseScopeDeclaration(scopeDeclaration);
        this.variables = new HashMap<>();
        for(Variable variable : variableArrayList){
            this.variables.put(variable.getName(), variable);
        }
    }

    public ParameterizedScope(String scopeDeclaration, ParameterizedScope scope, ParameterizedDocument parameterizedDocument){
        this.parameterizedDocument = parameterizedDocument;
        // COPIES SCOPE THEN ADDS NEWLY DECLARED VARIABLE
        ArrayList<Variable> variableArrayList = parseScopeDeclaration(scopeDeclaration);
        this.variables = new HashMap<>(scope.variables);
        for(Variable variable : variableArrayList){
            // IF A VARIABLE ALREADY EXISTED IN THE OLD SCOPE IT IS REPLACES BY THE NEWEST DECLARATION
            this.variables.put(variable.getName(), variable);
        }

    }

    private ArrayList<Variable> parseScopeDeclaration(String declaration){
        // CREATE A VARIABLE FOR EACH DECLARATIN LINE AND RETURN AN ARRAYLIST OF THEM
        ArrayList<Variable> variablesArrayList = new ArrayList<>();
        Scanner sc = new Scanner(declaration);

        while(sc.hasNextLine()){
            String currentLine = sc.nextLine();
            variablesArrayList.add(parseDeclarationLine(currentLine));
        }
        return variablesArrayList;
    }

    private  Variable parseDeclarationLine(String line){
        String workingLine = line;
        workingLine = workingLine.trim(); // FIRST TRIM EG " % ..." becomes "% ..."
        workingLine = workingLine.substring(1); // REMOVING % AT THE BEGINNING
        workingLine = workingLine.trim(); // SECOND TRIM EG " x : ..." becomes "x : ..."*


        // Line format is name : allowedVal;allowedVal so we should get an array [name;"allowedVal;allowedVal]]
        String[] splitNameValues = workingLine.split(":");
        if(splitNameValues.length != 2){
            if(splitNameValues.length <2){
                logger.error("Malformed var scope declaration line \n " + line + "\n found no declaration after : ");
                System.exit(-1);
            }
            logger.error("Malformed var scope declaration line \n" + line  + "\n found more than one : character");
            System.exit(-1);
        }
        String name = splitNameValues[0].trim(); // TRIM POSSIBLE WHITESPACE
        String[] allowedValues = splitNameValues[1].trim().split(";"); // We get an array where each value is an allowedValue
        if(allowedValues[0].length() == 0){
            logger.error("Malformed variable allowed values declaration " + line );
            System.exit(-1);
        }
        ArrayList<String> allowedValuesArrayList = new ArrayList<>(Arrays.asList(allowedValues)); // Convert to arrayList
        allowedValuesArrayList.add(name); // for now it's own name is an allowed value

        Variable variable = new Variable(name,allowedValuesArrayList);
        this.parameterizedDocument.registerVariable(variable);
        return variable ;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(Map.Entry<String, Variable> entry : this.variables.entrySet()){
            stringBuilder.append("key : ").append(entry.getKey()).append(" value : ").append(entry.getValue().toString()).append("\n");
        }

        return  stringBuilder.toString();
    }

}
