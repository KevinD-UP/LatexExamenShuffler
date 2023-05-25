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
    public static final String BEGIN_QUESTION_BLOCK = COMMENT + "shuffle";
    public static final String END_QUESTION_BLOCK = COMMENT + "endshuffle";


    public static Exercice exerciceFactory(String content){
        return parse(content);
    }

    private static Exercice parse(String content){
        logger.debug("Parsing exercise");
        Scanner scanner = new Scanner(content);
        boolean isFixed =false;

        int lineNum = 0; // number of the line currently being read within the exercise context
        int state = 0;

        ArrayList<ContentExercise> contentExerciseArrayList = new ArrayList<>();
        StringBuilder contentCurrentBlock = new StringBuilder();
        /*
            state 0 : has not seen a %shuffle yet
            state 1 : has seen a %shuffle line but no %endshuffle line ye
            state 2 : has just seen a %endshuffle line
         */
        while (scanner.hasNextLine()) {
            String currentLine = scanner.nextLine();
            //FIRST LINE
            if(lineNum==0){
                if(currentLine.trim().startsWith(BEGIN_EXO)){
                    isFixed = currentLine.equals(BEGIN_EXO + COMMENT + FIXED); // REMEMBERING IF EXERCISE CURRENTLY BEING PARSED SHOULD BE FIXED OR NOT
                } else {
                    logger.error("Malformed exercise, an exercise should start with " + BEGIN_EXO + " but this exercise started with " + currentLine);
                    System.exit(-1);
                }
            }
            // %shuffle
            else if (currentLine.trim().equals(BEGIN_QUESTION_BLOCK)) {
                if(state == 1){
                    logger.error("Malformed exercise, encountered " + BEGIN_QUESTION_BLOCK + " at line "+ lineNum + " but a question block was already opened and not closed");
                    System.exit(-1);
                } else if(state == 0 || state == 2){
                    if(!contentCurrentBlock.isEmpty()){
                        ContentExercise contentExercise = new Instruction(contentCurrentBlock.toString());
                        contentExerciseArrayList.add(contentExercise);
                        contentCurrentBlock = new StringBuilder();
                    }
                    state = 1;
                }
            // %endshuffle
            } else if(currentLine.trim().equals(END_QUESTION_BLOCK)){
                if(state == 0 || state == 2){
                    logger.error("Malformed exercise, encountered " + END_QUESTION_BLOCK + " at line " + lineNum + " but no question block was opened");
                    System.exit(-1);
                } else if (state == 1) {
                    ContentExercise contentExercise = new QuestionBlock(contentCurrentBlock.toString());
                    contentExerciseArrayList.add(contentExercise);
                    contentCurrentBlock = new StringBuilder();
                    state = 2;
                }
            }

            else if(!currentLine.trim().equals(END_EXO)){
                contentCurrentBlock.append(currentLine).append("\n");
            }

            lineNum +=1;

        }
        scanner.close();
        if(state ==0){
            logger.debug("Exercise successfully parsed");
            return new PlainExercice(contentCurrentBlock.toString(), isFixed);
        } else if ( state == 1) {
            logger.error("Malformed exercise, a question block was opened but not closed by the end of the exercise");
            System.exit(-1);
        } else if( state == 2 ){
            logger.debug("Exercise successfully parsed");
            return  new QuestionsExercise(contentExerciseArrayList,isFixed);
        }

        logger.error("Malformed exercise");
        System.exit(-1);

        return null;
    }
}
