package org.genial.ark.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.IntStream;

public class Document {


    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(org.genial.ark.domain.Document.class);


    public static final String BEGIN_EXO = "\\begin{exo}";
    public static final String END_EXO = "\\end{exo}";
    public static final String COMMENT = "%";
    public static final String FIXED = "fixed";

    private final ArrayList<Exercice> exercices = new ArrayList<>();


    private String beforeExercicesContent = "";

    private String afterExercicesContent = "";

    public Document(String inputPath){
        parse(inputPath);
        shuffle();
    }

    public void generateVariations(String outputDirectory, String filename, int numberVariations){
        for(int i = 1; i <= numberVariations; i++){
            int[] exerciseOrder = this.shuffle();
            String outputFileName = "./" + outputDirectory + filename + i + ".tex";
            dumpToTex(exerciseOrder, outputFileName);
        }
    }

    public void generateVariationsSubset(String outputDirectory, String filename, int numberVariations, int subset){
        for(int i = 1; i <= numberVariations; i++){
            int[] exerciseOrder = this.shuffle();
            String outputFileName = "./" + outputDirectory + filename + i + ".tex";
            List<Integer> selectedElements = new ArrayList<>();
            Random random = new Random();
            if (subset > exerciseOrder.length) {
                logger.info("The subset that you want is greater than the number of exercises ! Every exercises will be use.");
                dumpToTex(exerciseOrder, outputFileName);
            }
            else {
                for (int j = 0; j < subset; j++) {
                    int randomIndex;
                    int selectedElement;

                    do {
                        randomIndex = random.nextInt(exerciseOrder.length);
                        selectedElement = exerciseOrder[randomIndex];
                    } while (selectedElements.contains(selectedElement));

                    selectedElements.add(selectedElement);
                    Collections.sort(selectedElements);
                }
                int[] subsetExerciseOrder = this.shuffle(selectedElements.stream().mapToInt(Integer::intValue).toArray());
                dumpToTex(subsetExerciseOrder, outputFileName);
            }
        }
    }


    private int[] shuffle(){
        int[] exerciseOrder = IntStream.range(0, this.exercices.size()).toArray();
        for(int i =0; i < exerciseOrder.length ; i ++){
            int indexSwapA =  (int)(Math.random() * exerciseOrder.length);
            int indexSwapB = (int)(Math.random() * exerciseOrder.length);
            // SWAPPING EXERCISES A INDEX A AND B
            // WE SWAP ONLY IF WE DREW TWO DIFFRENT INDEX AND IF NONE OF THEM SHOULD BE FIXED
            if(indexSwapA != indexSwapB && !this.exercices.get(exerciseOrder[indexSwapA]).isFixed() && !this.exercices.get(exerciseOrder[indexSwapB]).isFixed()   ){
                int tmp =  exerciseOrder[indexSwapA]; // TEMPORARILY SAVING EXERCISE A
                exerciseOrder[indexSwapA] = exerciseOrder[indexSwapB]; // COPYING EXERCISE B INTO EXERCISE A
                exerciseOrder[indexSwapB] = tmp; // COPYING EXERCISE A INTO EXERCISE B
            }
        }
        return  exerciseOrder;
    }

    private int[] shuffle(int[] tab){
        int[] exerciseOrder = tab.clone();
        for(int i = 0; i < exerciseOrder.length ; i ++){
            int indexSwapA =  (int)(Math.random() * exerciseOrder.length);
            int indexSwapB = (int)(Math.random() * exerciseOrder.length);
            // SWAPPING EXERCISES A INDEX A AND B
            // WE SWAP ONLY IF WE DREW TWO DIFFRENT INDEX AND IF NONE OF THEM SHOULD BE FIXED
            if(indexSwapA != indexSwapB && !this.exercices.get(exerciseOrder[indexSwapA]).isFixed() && !this.exercices.get(exerciseOrder[indexSwapB]).isFixed()   ){
                int tmp =  exerciseOrder[indexSwapA]; // TEMPORARILY SAVING EXERCISE A
                exerciseOrder[indexSwapA] = exerciseOrder[indexSwapB]; // COPYING EXERCISE B INTO EXERCISE A
                exerciseOrder[indexSwapB] = tmp; // COPYING EXERCISE A INTO EXERCISE B
            }
        }
        return exerciseOrder;
    }

    private void dumpToTex(int[] exercisesPermutation, String outputFileName) {
        try {
            Path of = Path.of(outputFileName);
            Files.createDirectories(of.getParent());
            Files.writeString(of, this.beforeExercicesContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            for(int index : exercisesPermutation) {
                Files.writeString(of, this.exercices.get(index).toString(), StandardOpenOption.APPEND);
            }
            Files.writeString(of, this.afterExercicesContent, StandardOpenOption.APPEND);
        } catch (Exception e) {
            logger.error("Exception occurred when trying to dump to Text:"+e.getMessage());
            e.printStackTrace();
        }
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
            int exerciseCount = 1; // current exercise for logging purposes
            /*
            state 0 : started parsing file, initial configuration before exercices
            state 1 : has seen a \begin{exo} line but no \end{exo} line yet
            state 2 : has seen a \end{exo} line last at the previous iteration of the loop
             */
            while(sc.hasNextLine())
            {
                String currentLine = sc.nextLine();
                //BEGIN EXO
                if(currentLine.trim().startsWith(BEGIN_EXO)){
                    if(state == 2 || state == 0){
                        state = 1; // CHANGING TO STATE 1 BECAUSE WE ARE INSIDE AN EXERCISE
                        currentExerciceContent.append(currentLine).append("\n");
                    } else{
                        logger.error("Error, malformed document, encountered " + BEGIN_EXO + " at line " + lineNum + " but an exercise was already opened and not closed");
                        System.exit(-1);
                    }
                }

                //END EXO
                else if(currentLine.trim().equals(END_EXO)){
                    if(state == 1){
                        state = 2; // CHANGING TO STATE 2 BECAUSE WE ARE NOT INSIDE AN EXERCISE BUT WE READ AT LEAST ONE
                        currentExerciceContent.append(currentLine).append("\n");
                        logger.info("Creating exercise " + exerciseCount);
                        exerciseCount += 1;
                        Exercice exercice = ExerciseFactory.exerciceFactory(currentExerciceContent.toString());
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
                    } else if(state == 2){ // WE HAVE ENCOUNTERED AT LEAST AN EXERCISE BUT ARE NOT INSIDE OF ONE
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
