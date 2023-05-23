package org.genial.ark.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Scanner;

import static org.genial.ark.domain.Document.*;

public class ExerciseFactory {


    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(ExerciseFactory.class);
    public static final String BEGIN_INSTRUCTION = "%i";
    public static final String END_INSTRUCTION = "%endi";
    public static final String BEGIN_QUESTION_BLOCK = "%qb";
    public static final String END_QUESTION_BLOCK = "%endqb";

    public static Exercice exerciceFactory(String content){
        return parse(content);
    }

    private static Exercice parse(String content){
        logger.info("Parsing exercise");
        Scanner scanner = new Scanner(content);
        boolean isFixed =false;

        int lineNum = 0; // number of the line currently being read within the exercise context
        int state = 0;

        ArrayList<ContentExercise> contentExerciseArrayList = new ArrayList<>();
        StringBuilder contentCurrentBlock = new StringBuilder();
        /*
            state 0 : has not seen a %i or %qb yet
            state 1 : has seen a %i line but no %endi line yet
            state 2 : has seen a %qb line but no %endqb line yet
            state 3 : has just seen a %endi or a %endqb line
         */
        while (scanner.hasNextLine()) {
            String currentLine = scanner.nextLine();
            if(currentLine.trim().startsWith("%")){
                System.out.println(currentLine);
            }
            //FIRST LINE
            if(lineNum==0){
                if(currentLine.trim().startsWith(BEGIN_EXO)){
                    isFixed = currentLine.equals(BEGIN_EXO + COMMENT + FIXED); // REMEMBERING IF EXERCISE CURRENTLY BEING PARSED SHOULD BE FIXED OR NOT
                } else {
                    logger.error("Malformed exercise, an exercise should start with " + BEGIN_EXO + " but this exercise started with " + currentLine);
                    System.exit(-1);
                }
            }
            // %i
            else if (currentLine.trim().equals(BEGIN_INSTRUCTION)) {
                if(state == 1){
                    logger.error("Malformed exercise, encountered " + BEGIN_INSTRUCTION + " at line "+ lineNum + " but an instruction was already opened and not closed");
                    System.exit(-1);
                } else if(state == 2){
                    logger.error("Malformed exercise, encountered " + BEGIN_INSTRUCTION + " at line "+ lineNum + " but a question block was already opened and not closed");
                    System.exit(-1);
                } else if(state == 0 || state == 3){
                    state = 1;
                }
            // %endi
            } else if(currentLine.trim().equals(END_INSTRUCTION)){
                if(state == 0 || state == 3){
                    logger.error("Malformed exercise, encountered " + END_INSTRUCTION + " at line " + lineNum + " but no instruction block was opened");
                    System.exit(-1);
                } else if (state == 2){
                    logger.error("Malformed exercise, encountered " + END_INSTRUCTION + " at line " + lineNum + " but a questions block was already opened and not closed");
                    System.exit(-1);
                } else if (state == 1) {
                    ContentExercise contentExercise = new Instruction(contentCurrentBlock.toString());
                    contentExerciseArrayList.add(contentExercise);
                    contentCurrentBlock = new StringBuilder();
                    state = 3;
                }
            }
            // %qb
            else if(currentLine.trim().equals(BEGIN_QUESTION_BLOCK)){
                if(state == 2){
                    logger.error("Malformed exercise, encountered " + BEGIN_QUESTION_BLOCK + " at line " + lineNum + " but a question block was already opened and not closed");
                    System.exit(-1);
                } else if(state == 1){
                    logger.error("Malformed exercise, encountered " + BEGIN_QUESTION_BLOCK + " at line "+ lineNum + " but an instruction was already opened and not closed");
                    System.exit(-1);
                } else if(state == 0 || state == 3){
                    state = 2;
                }
            }
            // %endqb
            else if(currentLine.trim().equals(END_QUESTION_BLOCK)){
                if(state == 0 || state == 3){
                    logger.error("Malformed exercise, encountered " + END_QUESTION_BLOCK + " at line " + lineNum + " but no instruction block was opened");
                    System.exit(-1);
                } else if (state == 1){
                    logger.error("Malformed exercise, encountered " + END_QUESTION_BLOCK + " at line " + lineNum + " but an instruction block was already opened and not closed");
                    System.exit(-1);
                } else if (state == 2) {
                    ContentExercise contentExercise = new QuestionBlock(contentCurrentBlock.toString());
                    contentExerciseArrayList.add(contentExercise);
                    contentCurrentBlock = new StringBuilder();
                    state = 3;
                }
            }

            else if(!currentLine.trim().equals(END_EXO)){
                contentCurrentBlock.append(currentLine).append("\n");
            }

            lineNum +=1;

        }
        scanner.close();
        if(state ==0){
            logger.info("Exercise successfully parsed");
            return new PlainExercice(contentCurrentBlock.toString(), isFixed);
        } else if ( state == 1) {
            logger.error("Malformed exercise, an instruction block was opened but not closed by the end of the exercise");
            System.exit(-1);
        } else if( state == 2 ){
            logger.error("Malformed exercise, a questions block was opened but not closed by the end of the exercise");
            System.exit(-1);
        } else if( state == 3 ){
            return  new QuestionsExercise(contentExerciseArrayList,isFixed);
        }

        return  null;
    }


}
