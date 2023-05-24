package org.genial.ark.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

public class QuestionBlock implements ContentExercise{


    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(QuestionBlock.class);



    private ArrayList<Question> questionsArrayList = new ArrayList<>();

    public QuestionBlock(String content){
        parse(content);
    }

    private int[] shuffle(){
        int[] questionOrder = IntStream.range(0, this.questionsArrayList.size()).toArray();

        for(int i =0; i < questionOrder.length ; i ++){
            int indexSwapA =  (int)(Math.random() * questionOrder.length);
            int indexSwapB = (int)(Math.random() * questionOrder.length);

            // SWAPPING EXERCISES A INDEX A AND B
            // WE SWAP ONLY IF WE DREW TWO DIFFRENT INDEX AND IF NONE OF THEM SHOULD BE FIXED
            if(indexSwapA != indexSwapB && !this.questionsArrayList.get(questionOrder[indexSwapA]).isFixed() && !this.questionsArrayList.get(questionOrder[indexSwapB]).isFixed()   ){
                int tmp =  questionOrder[indexSwapA]; // TEMPORARILY SAVING EXERCISE A
                questionOrder[indexSwapA] = questionOrder[indexSwapB]; // COPYING EXERCISE B INTO EXERCISE A
                questionOrder[indexSwapB] = tmp; // COPYING EXERCISE A INTO EXERCISE B
            }
        }
        return  questionOrder;
    }


    private void parse(String content){
        logger.info("Parsing question block");
        Scanner sc = new Scanner(content);
        int i =0;
        int state =0;
        /*
        state = 0 not seen an item yet
        state = 1 seen an item
         */
        StringBuilder currentString = new StringBuilder();
        boolean currentQuestionIsFixed = false;
        while(sc.hasNextLine()){
            String currentLine = sc.nextLine();
            if(i == 0 || !sc.hasNextLine()){
                currentString.append(currentLine).append("\n");
                questionsArrayList.add(new Question(currentString.toString(),true));
                currentString = new StringBuilder();
            }

            else if(currentLine.trim().startsWith("\\item")){
                if(state != 0){
                    questionsArrayList.add(new Question(currentString.toString(),currentQuestionIsFixed));
                    currentQuestionIsFixed = false;
                    currentString = new StringBuilder();
                    currentString.append(currentLine).append("\n");
                    if(currentLine.trim().endsWith("%fixed")){
                        currentQuestionIsFixed = true;
                    }
                } else{
                    if(currentLine.trim().endsWith("%fixed")){
                        currentQuestionIsFixed = true;
                    }
                    currentString.append(currentLine).append("\n");
                    state = 1;
                }
            }

            else{
                currentString.append(currentLine).append("\n");
            }

            i +=1;
        }
    }

    @Override
    public String toString(){
        int[] questionOrder = shuffle();
        StringBuilder toString = new StringBuilder();
        for(int i = 0; i < questionOrder.length; i ++){
            toString.append(questionsArrayList.get(questionOrder[i]));
        }
        return  toString.toString();
    }

}
