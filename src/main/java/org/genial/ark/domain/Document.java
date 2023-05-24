package org.genial.ark.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.collections4.iterators.PermutationIterator;

import static java.lang.Math.min;


public class Document {


    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(org.genial.ark.domain.Document.class);


    public static final String BEGIN_EXO = "\\begin{exo}";
    public static final String END_EXO = "\\end{exo}";
    public static final String COMMENT = "%";
    public static final String FIXED = "fixed";

    private final ArrayList<Exercice> exercise = new ArrayList<>();


    private String beforeExerciseContent = "";

    private String afterExercisesContent = "";

    public Document(String inputPath){
        parse(inputPath);
    }

    public int[][] generateVariations(String outputDirectory, String filename, int numberVariations){
        int [][] allVariations = this.shuffle(numberVariations);
        int n = min(numberVariations, allVariations.length);
        for(int i = 1; i <= n; i++){
            int[] exerciseOrder = allVariations[i - 1];
            String outputFileName = "./" + outputDirectory + filename + i + ".tex";
            dumpToTex(exerciseOrder, outputFileName);
        }
        return allVariations;
    }

    public int[][] generateVariationsSubset(String outputDirectory, String filename, int numberVariations, int subset){
        List<Integer> selectedElements = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < subset; i++) {
            int randomIndex;
            int selectedElement;

            do {
                randomIndex = random.nextInt(this.exercise.size());
                selectedElement = randomIndex;
            } while (selectedElements.contains(selectedElement));

            selectedElements.add(selectedElement);
        }

        int[] intArray = selectedElements.stream().mapToInt(Integer::intValue).toArray();
        int[][] allVariations = this.shuffle(intArray, numberVariations);
        int n = min(numberVariations, allVariations.length);
        for(int i = 1; i <= n; i++){
            int[] exerciseOrder = allVariations[i - 1];
            String outputFileName = "./" + outputDirectory + filename + i + ".tex";
            dumpToTex(exerciseOrder, outputFileName);
        }
        return allVariations;
    }

    public int[][] generateVariationsSubsetRange(String outputDirectory, String filename, int numberVariations, int nbExo, int range){
        List<Integer> selectedElements = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < this.exercise.size(); i += range) {
            for(int j = 0; j < nbExo; j++){
                int randomIndex;
                int selectedElement;

                do {
                    randomIndex = random.nextInt(i, min(this.exercise.size(), i + range));
                    selectedElement = randomIndex;
                } while (selectedElements.contains(selectedElement));

                selectedElements.add(selectedElement);
            }
        }

        int[] intArray = selectedElements.stream().mapToInt(Integer::intValue).toArray();

        int[][] allVariations = this.shuffle(intArray, numberVariations);
        int m = min(numberVariations, allVariations.length);
        for(int i = 1; i <= m; i++){
            int[] exerciseOrder = allVariations[i - 1];
            String outputFileName = "./" + outputDirectory + filename + i + ".tex";
            dumpToTex(exerciseOrder, outputFileName);
        }
        return allVariations;
    }



    private BigInteger factorial(int n) {
        BigInteger result = BigInteger.ONE;

        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }

        return result;
    }

    private int[][] shuffle(int numberShuffleWanted){
        logger.info("Beginning shuffling ");
        // récupérer nombre et  position exercices qui ne peuvent pas être changés 
        // récupérer nombre et position  exercices qui peuvent être changés
        ArrayList<Integer> fixedExerciseArray  = new ArrayList<Integer>();
        ArrayList<Integer> unfixedExerciseArray  = new ArrayList<Integer>();
        int currentExercisePos = 0 ;
        for(Exercice ex : this.exercise){
            if(ex.isFixed()){
                fixedExerciseArray.add(currentExercisePos);
            }else{
                unfixedExerciseArray.add(currentExercisePos);
            }
            currentExercisePos++;
        }
        
        BigInteger numberShuffleAvailable = factorial(unfixedExerciseArray.size());
        logger.info("Number of shuffle wanted " + numberShuffleWanted+ " number of shuffle available " + numberShuffleAvailable);
        if (numberShuffleAvailable.compareTo(BigInteger.valueOf(numberShuffleWanted)) < 0){
            logger.warn("Number of shuffle wanted " + numberShuffleWanted + " is greater than number of shuffle available " + numberShuffleAvailable + ", so every permutation available will be generated");
            numberShuffleWanted = numberShuffleAvailable.intValue();
        }
        // shuffle les exercices qui peuvent être ré-arrangés
        
        PermutationIterator <Integer> shuffleIterator = new PermutationIterator<Integer>(unfixedExerciseArray);
        
        // tableau de retour de permutations d'exercices
        int [][] shuffledExercicesToReturn = new int[numberShuffleWanted][this.exercise.size()];
            
        //merger les exercices qui peuvent être ré-arrangés et ceux qui ne le peuvent pas
        for (int currentlyProcessedShuffle = 0 ; currentlyProcessedShuffle < numberShuffleWanted; currentlyProcessedShuffle++  ){
        
            // toutes les cases de la permutation actuelle sont initialement marqués comme vides
            final int CASEVIDE=-1;
            for(int fillingPos = 0; fillingPos < this.exercise.size(); fillingPos++)shuffledExercicesToReturn[currentlyProcessedShuffle][fillingPos]=CASEVIDE;

            //on met les exercices qui ne peuvent pas être réordonnées
            for (int fixedExercisePos: fixedExerciseArray) shuffledExercicesToReturn[currentlyProcessedShuffle][fixedExercisePos]=fixedExercisePos;
            
            //on met les exercices qui peuvent être réordonnées dans les cases restantes
            int[] aShuffle  = shuffleIterator.next().stream().mapToInt(Integer::intValue).toArray(); // on récupère une permutation d'exercices non fixés
            
            int exercisePosition = 0 ; // position ou le prochain exercice va être écrit  
            for (int exercise: aShuffle){
                // tant que l'on se trouve sur une case d'exercice fixé
                while (shuffledExercicesToReturn[currentlyProcessedShuffle][exercisePosition] != CASEVIDE) exercisePosition++;
                // ici on se trouve sur une case vide
                shuffledExercicesToReturn[currentlyProcessedShuffle][exercisePosition]=exercise ;
            }    
        }
        return shuffledExercicesToReturn;
    }

    private int[][] shuffle(int[] tab, int numberShuffleWanted){
        int[] exerciseOrder = tab.clone();
        logger.info("Beginning shuffling ");
        // récupérer nombre et  position exercices qui ne peuvent pas être changés
        // récupérer nombre et position  exercices qui peuvent être changés
        ArrayList<Integer> fixedExerciseArray  = new ArrayList<Integer>();
        ArrayList<Integer> unfixedExerciseArray  = new ArrayList<Integer>();
        for(int exId: exerciseOrder){
            if(this.exercise.get(exId).isFixed()){
                fixedExerciseArray.add(exId);
            }else{
                unfixedExerciseArray.add(exId);
            }
        }

        BigInteger numberShuffleAvailable = factorial(unfixedExerciseArray.size());
        logger.info("Number of shuffle wanted " + numberShuffleWanted+ " number of shuffle available " + numberShuffleAvailable);
        if (numberShuffleAvailable.compareTo(BigInteger.valueOf(numberShuffleWanted)) < 0){
            logger.warn("Number of shuffle wanted " + numberShuffleWanted + " is greater than number of shuffle available " + numberShuffleAvailable + ", so every permutation available will be generated");
            numberShuffleWanted = numberShuffleAvailable.intValue();
        }
        // shuffle les exercices qui peuvent être ré-arrangés

        PermutationIterator <Integer> shuffleIterator = new PermutationIterator<Integer>(unfixedExerciseArray);

        // tableau de retour de permutations d'exercices
        int [][] shuffledExercicesToReturn = new int[numberShuffleWanted][exerciseOrder.length];

        //merger les exercices qui peuvent être ré-arrangés et ceux qui ne le peuvent pas
        for (int currentlyProcessedShuffle = 0 ; currentlyProcessedShuffle < numberShuffleWanted; currentlyProcessedShuffle++  ){

            // toutes les cases de la permutation actuelle sont initialement marqués comme vides
            final int CASEVIDE=-1;
            for(int fillingPos= 0; fillingPos < exerciseOrder.length; fillingPos++)shuffledExercicesToReturn[currentlyProcessedShuffle][fillingPos]=CASEVIDE;

            //on met les exercices qui ne peuvent pas être réordonnées
            for (int fixedExercisePos: fixedExerciseArray) shuffledExercicesToReturn[currentlyProcessedShuffle][fixedExercisePos]=fixedExercisePos;

            //on met les exercices qui peuvent être réordonnées dans les cases restantes
            int[] aShuffle  = shuffleIterator.next().stream().mapToInt(Integer::intValue).toArray(); // on récupère une permutation d'exercices non fixés

            int exercisePosition = 0 ; // position ou le prochain exercice va être écrit
            for (int exercise: aShuffle){
                // tant que l'on se trouve sur une case d'exercice fixé
                while (shuffledExercicesToReturn[currentlyProcessedShuffle][exercisePosition] != CASEVIDE) exercisePosition++;
                // ici on se trouve sur une case vide
                shuffledExercicesToReturn[currentlyProcessedShuffle][exercisePosition]=exercise ;
            }
        }
        return shuffledExercicesToReturn;
    }

    private int[] shuffle(int[] tab){
        int[] exerciseOrder = tab.clone();
        for(int i = 0; i < exerciseOrder.length ; i ++){
            int indexSwapA =  (int)(Math.random() * exerciseOrder.length);
            int indexSwapB = (int)(Math.random() * exerciseOrder.length);
            // SWAPPING EXERCISES A INDEX A AND B
            // WE SWAP ONLY IF WE DREW TWO DIFFRENT INDEX AND IF NONE OF THEM SHOULD BE FIXED
            if(indexSwapA != indexSwapB && !this.exercise.get(exerciseOrder[indexSwapA]).isFixed() && !this.exercise.get(exerciseOrder[indexSwapB]).isFixed()){
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
            Files.writeString(of, this.beforeExerciseContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            for(int index : exercisesPermutation) {
                Files.writeString(of, this.exercise.get(index).toString(), StandardOpenOption.APPEND);
            }
            Files.writeString(of, this.afterExercisesContent, StandardOpenOption.APPEND);
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
                        this.exercise.add(exercice); // REGISTERING EXERCISE INSIDE THE DOCUMENT
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
                        this.beforeExerciseContent += currentLine + "\n";
                    } else if(state == 2){ // WE HAVE ENCOUNTERED AT LEAST AN EXERCISE BUT ARE NOT INSIDE OF ONE
                        this.afterExercisesContent += currentLine + "\n";
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
