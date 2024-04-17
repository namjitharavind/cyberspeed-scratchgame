package org.cyberspeed.service;

import org.cyberspeed.model.*;

import java.util.Map;
import java.util.Random;

public class ScratchGameService {

    private final int rows;
    private final int columns;
    private final Map<String, Symbol> symbols;
    private final ProbabilityConfig probabilities;
    private final Map<String, WinCombination> winCombinations;

    public ScratchGameService(Matrix config) {
        this.rows = config.getRows();
        this.columns = config.getColumns();
        this.symbols = config.getSymbols();
        this.probabilities = config.getProbabilities();
        this.winCombinations = config.getWinCombinations();
    }

    /**
     * This method simulates the randomness of a scratch game where different symbols
     * appear with certain probabilities in each cell of the matrix.
     *
     * @return
     */
    public String[][] generateMatrix() {
        String[][] matrix = new String[this.rows][this.columns];
        Random random = new Random();

        for (ProbabilitySymbol probability : this.probabilities.getStandardSymbols()) {
            int row = probability.getRow();
            int column = probability.getColumn();
            int totalProbability = probability.getSymbols().values().stream().mapToInt(Integer::intValue).sum();
            int randomNumber = random.nextInt(totalProbability) + 1;
            /**
             * This process ensures that symbols are selected proportionally to their probabilities,
             * as higher probabilities lead to higher cumulative probabilities, increasing the chance of selection
             */
            double cumulativeProbability = 0;

            for (Map.Entry<String, Integer> entry : probability.getSymbols().entrySet()) {
                cumulativeProbability += entry.getValue();
                if (randomNumber <= cumulativeProbability) {
                    matrix[row][column] = entry.getKey();
                    break;
                }
            }
        }
        return matrix;
    }


    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public ProbabilityConfig getProbabilities() {
        return probabilities;
    }

    public Map<String, WinCombination> getWinCombinations() {
        return winCombinations;
    }
}