package org.genial.ark.domain.parameterized;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ParameterizedDocument {

    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(ParameterizedDocument.class);
    public static final String VAR = "%var";
    public static final String ENDVAR = "%endvar";
    public static final String END_DEC = "%end dec";

    private ArrayList<Variable> allVariables= new ArrayList<>();

    private ArrayList<ContentBlock> contentBlocks = new ArrayList<>();

    public ParameterizedDocument(String inputPath){
        parseVariable(inputPath);
    }

    public String generateParameterizedDocument(){
        this.setAllVariables();
        StringBuilder stringBuilder = new StringBuilder();
        for(ContentBlock contentBlock : this.contentBlocks){
            stringBuilder.append(contentBlock.toString());
        }
        return stringBuilder.toString();
    }

    public void registerVariable(Variable v){
        this.allVariables.add(v);
    }

    public void setAllVariables(){
        for(Variable variable : this.allVariables){
            variable.pickValue();
        }
    }

    public String toString(){
        StringBuilder out = new StringBuilder();
        for (ContentBlock contentBlock : contentBlocks){
            out.append(contentBlock.toString());
        }

        return  out.toString();
    }

    private void parseVariable(String inputPath){
        logger.info("Parsing input file");
        try {
            //the file to be opened for reading
            FileInputStream fis=new FileInputStream(inputPath);
            Scanner sc=new Scanner(fis);    //file to be scanned
            int lineNum =0; // index of the line currently being read
            int state = 0;
            ArrayList<ParameterizedScope> scopes = new ArrayList<>();
            int currentScopeDepth = -1;
            StringBuilder currentContent = new StringBuilder();
            StringBuilder currentScopeDeclaration = new StringBuilder();
            /*
            state 0 : has not seen a %var line yet
            state 1 : has seen a %var but not %endvar yet
            state 2 : last var tag seen was %endvar
             */

            while(sc.hasNextLine())
            {
                String currentLine = sc.nextLine();
                // %var
                if(currentLine.trim().equals(VAR)){
                    state = 1;
                    ContentBlock contentBlock;
                    // FINISHING CURRENT BLOCK
                    if(currentScopeDepth == -1){ // NOT IN A SCOPE
                        contentBlock = new PlainContentBlock(currentContent.toString());
                    } else{ // IN A SCOPE
                        contentBlock = new ParametrizedContentBlock(currentContent.toString(), scopes.get(currentScopeDepth));
                    }

                    this.contentBlocks.add(contentBlock);
                    currentContent = new StringBuilder(); // RESET CURRENT CONTENT

                    //ENTERING A SCOPE
                    // PROCESSING IT
                    while(sc.hasNextLine()){
                        currentLine = sc.nextLine();
                        // END VARIABLE DECLARATION
                        if(currentLine.trim().equals(END_DEC)){
                            ParameterizedScope parameterizedScope;
                            if(currentScopeDepth != -1){
                                // SCOPE DEPTH IS NOT -1, WE USE CURRENT DEPTH SCOPE AND DECLARATION TO CREATE SCOPE
                                 parameterizedScope = new ParameterizedScope(currentScopeDeclaration.toString(), scopes.get(currentScopeDepth), this);
                            } else{
                                parameterizedScope = new ParameterizedScope(currentScopeDeclaration.toString(),this);
                            }
                            currentScopeDeclaration = new StringBuilder();
                            lineNum +=2; // %VAR AND %END DEC
                            currentScopeDepth += 1;
                            scopes.add(parameterizedScope);
                            break;
                        } else{ // ADD DECLARATION LINE TO SCOPE DEC CONTENT
                            currentScopeDeclaration.append(currentLine).append("\n");
                        }

                        lineNum +=1 ;
                    }
                }
                // %endvar
                else if(currentLine.trim().equals(ENDVAR)){
                    if(currentScopeDepth == -1 || state == 0){
                        logger.error("Malformed document, encountered " + ENDVAR + " at line "  + lineNum + " but not var scope was opened");
                        System.exit(-1);
                    }

                    ContentBlock contentBlock = new ParametrizedContentBlock(currentContent.toString(),scopes.get(currentScopeDepth));
                    contentBlocks.add(contentBlock);
                    currentContent = new StringBuilder();
                    scopes.remove(currentScopeDepth);
                    currentScopeDepth -=1;
                    state = 2;
                }

                else{
                    currentContent.append(currentLine).append("\n");
                }

                lineNum += 1;
            }
            sc.close();

            if(state == 1){
                logger.error("Malformed document, a var scope was opened but not closed by the end of the document");
                System.exit(-1);
            } else{
                ContentBlock contentBlock;
                if(currentScopeDepth == -1){
                    contentBlock = new PlainContentBlock(currentContent.toString());
                } else{
                    contentBlock = new ParametrizedContentBlock(currentContent.toString(), scopes.get(currentScopeDepth));
                }
                contentBlocks.add(contentBlock);
            }
        } catch(IOException e) {
            logger.error("Exception while parsing file " + inputPath + " : " + e.getMessage());
            System.exit(-1);
        }
    }


}
