package org.genial.ark.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Document {


    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(org.genial.ark.domain.Document.class);


    public static final String BEGIN_EXO = "\\begin{exo}";
    public static final String END_EXO = "\\end{exo}";

    private ArrayList<Exercice> exercices = new ArrayList<>();



    private String beforeExercicesContent;

    private String afterExercicesContent;

    public Document(String inputPath){
        parse(inputPath);
    }


    private void parse(String inputPath) {
        logger.info("Parsing input file");
        try {
            //the file to be opened for reading
            FileInputStream fis=new FileInputStream(inputPath);
            Scanner sc=new Scanner(fis);    //file to be scanned
            //returns true if there is another line to read
            StringBuilder currentExerciceContent = new StringBuilder();
            int state = 0;
            int lineNum =0; // index of the line currently being read
            /*
            state 0 : started parsing file, initial configuration before exercices
            state 1 : has seen a \begin{exo} line but no \end{exo} line yet
            state 2 : has seen a \end{exo} line last at the previous iteration of the loop
             */
            while(sc.hasNextLine())
            {
                String currentLine = sc.nextLine();
                //BEGIN EXO
                if(currentLine.equals(BEGIN_EXO)){
                    if(state == 2 || state == 0){
                        state = 1; // CHANGING TO STATE 1 BECAUSE WE ARE INSIDE AN EXERCISE
                        currentExerciceContent.append(currentLine).append("\n");
                    } else{
                        logger.error("Error, malformed document, encountered " + BEGIN_EXO + " at line " + lineNum + " but an exercise was already opened and not closed");
                        System.exit(-1);
                    }
                }

                //END EXO
                else if(currentLine.equals(END_EXO)){
                    if(state == 1){
                        state = 2; // CHANGING TO STATE 2 BECAUSE WE ARE NOT INSIDE AN EXERCISE BUT WE READ AT LEAT ONE
                        currentExerciceContent.append(currentLine).append("\n");
                        Exercice exercice = new Exercice(currentExerciceContent.toString());
                        this.exercices.add(exercice); // REGISTERING EXERCISE INSIDE THE DOCUMENT
                        currentExerciceContent = new StringBuilder(); // RESET FOR NEXT EXERCISE
                    } else {
                        logger.error("Error, malformed document, encountered " + END_EXO + " at line " + lineNum + " but no exercise was open");
                        System.exit(-1);
                    }
                }

                // ANY OTHER LINE
                else{
                    // WE ARE INSIDE AN EXERCISE
                    if(state == 1){
                        currentExerciceContent.append(currentLine).append("\n");
                    } else if (state == 0){ // WE HAVE NOT ENCOUNTERED AN EXERCISE YET
                        this.beforeExercicesContent += currentLine + "\n";
                    } else if( state == 2){ // WE HAVE ENCOUNTERED AT LEAST AN EXERCISE BUT ARE NOT INSIDE OF ONE
                        this.afterExercicesContent += currentLine + "\n";
                    }
                }
                lineNum += 1;
            }
            sc.close();
        } catch(IOException e) {
            logger.error("Exception while parsing file " + inputPath + " : " + e.getMessage());
            System.exit(-1);
        }
    }



}
