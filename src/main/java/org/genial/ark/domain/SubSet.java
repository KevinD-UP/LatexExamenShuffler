package org.genial.ark.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.genial.ark.command.CommandLESSubset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SubSet implements DocumentBlock {

    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger(SubSet.class);

    private int numberToPick;

    private ArrayList<Exercice> exercices;

    public SubSet(ArrayList<Exercice> exercices, int numberToPick){
        if(numberToPick > exercices.size()){
            logger.error("SubSet wanted is bigger than the number of exercises, check your LaTeX file.");
            System.exit(-1);
        }
        this.exercices = exercices;
        this.numberToPick = numberToPick;
    }

    private int[] pick() {
        List<Integer> selectedElements = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < this.numberToPick; i++) {
            int randomIndex;
            int selectedElement;

            do {
                randomIndex = random.nextInt(this.exercices.size());
                selectedElement = randomIndex;
            } while (selectedElements.contains(selectedElement));

            selectedElements.add(selectedElement);
        }

        return selectedElements.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public String toString() {
        int[] selected = this.pick();
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < selected.length; i++) {
            res.append(this.exercices.get(selected[i]).toString());
        }
        return res.toString();
    }
}
