package org.cyberspeed.service;

import org.cyberspeed.model.*;

import java.util.*;
import java.util.stream.Collectors;

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


    /**
     * Randomly assigns a bonus symbol to a cell in the matrix
     * And update the assignedBonus  parameter by symbol choosen randomly
     *
     * @param matrix
     * @param assignedBonus
     */
    public void assignBonusSymbol(String[][] matrix, StringBuilder assignedBonus) {
        Random random = new Random();
        int totalProbability = probabilities.getBonusSymbol().getSymbols().values().stream().mapToInt(Integer::intValue).sum();
        int randomNumber = random.nextInt(totalProbability) + 1;
        double cumulativeProbability = 0;

        for (Map.Entry<String, Integer> entry : probabilities.getBonusSymbol().getSymbols().entrySet()) {
            cumulativeProbability += entry.getValue();
            if (randomNumber <= cumulativeProbability) {
                while (true) {
                    int row = random.nextInt(this.rows);
                    int column = random.nextInt(this.columns);
                    if (matrix[row][column] != null) {
                        matrix[row][column] = entry.getKey();
                        assignedBonus.append(entry.getKey());
                        break;
                    }
                }
                break;
            }
        }
    }

    public Map<String, Set<String>> checkWinningCombinations(String[][] matrix) {
        Map<String, Set<String>> appliedWinningCombinations = new HashMap<>();

        // Count symbols
        Map<String, Integer> symbolCount = Arrays.stream(matrix)
                .flatMap(Arrays::stream) // Flatten the 2D array into a stream of symbols
                .filter(Objects::nonNull) // Filter out null symbols
                .collect(Collectors.groupingBy(s -> s, Collectors.summingInt(s -> 1)));

        this.winCombinations.entrySet().forEach(entry -> {
            String winCombinationName = entry.getKey();
            WinCombination winCombination = entry.getValue();

            if ("same_symbols".equals(winCombination.getWhen())) {
                int maxCount = this.winCombinations.get(winCombinationName).getCount();

                symbolCount.entrySet().stream()
                        .filter(symbolEntry -> symbolEntry.getValue() == maxCount)
                        .forEach(symbolEntry -> appliedWinningCombinations.compute(symbolEntry.getKey(), (key, oldValue) ->
                                (oldValue == null || oldValue.size() < maxCount) ?
                                        new HashSet<>(Collections.singleton(winCombinationName)) :
                                        oldValue
                        ));
            } else if ("linear_symbols".equals(winCombination.getWhen()) && !appliedWinningCombinations.containsKey(winCombinationName)) {
                List<String> matchingSymbols = findSymbolsInCoveredAreas(matrix, winCombination.getCoveredAreas());
                matchingSymbols.forEach(symbol -> appliedWinningCombinations
                        .computeIfAbsent(symbol, k -> new HashSet<>())
                        .add(winCombinationName));
            }
        });

        return appliedWinningCombinations;
    }

    private List<String> findSymbolsInCoveredAreas(String[][] matrix, List<List<String>> coveredAreas) {
        List<String> matchingSymbols = new ArrayList<>();

        for (List<String> area : coveredAreas) {
            String firstSymbol = null;
            boolean match = true;
            for (String coord : area) {
                int row = Integer.parseInt(coord.split(":")[0]);
                int col = Integer.parseInt(coord.split(":")[1]);
                String symbol = matrix[row][col];
                //If there is no matching index on matrix or If
                if (symbol == null || (firstSymbol != null && !symbol.equals(firstSymbol))) {
                    match = false;
                    break;
                }
                firstSymbol = symbol;
            }
            if (match) {
                matchingSymbols.add(firstSymbol);
            }
        }

        return matchingSymbols;
    }


    public double calculateReward(double betAmount, Map<String, Set<String>> appliedWinningCombinations, String appliedBonusSymbol) {
        double totalReward = 0.0;

        for (Map.Entry<String, Set<String>> entry : appliedWinningCombinations.entrySet()) {
            String symbol = entry.getKey();
            Set<String> combinations = entry.getValue();

            //Symbol Reward
            double symbolReward = this.symbols.containsKey(symbol) ? this.symbols.get(symbol).getRewardMultiplier() : 0.0;
            double symbolTotalReward = symbolReward;

            // symbol  combination reward sum
            for (String combination : combinations) {
                WinCombination winCombination = this.winCombinations.get(combination);
                if (winCombination != null) {
                    double combinationReward = winCombination.getRewardMultiplier();
                    symbolTotalReward *= combinationReward;
                }
            }

            totalReward += symbolTotalReward;
        }
        totalReward = totalReward * betAmount;
        // Apply bonus symbol operation
        if (appliedBonusSymbol != null) {
            if (appliedBonusSymbol.contains("x")) {
                int multiplier = Integer.parseInt(appliedBonusSymbol.replace("x", ""));
                totalReward *= multiplier;
            } else if (appliedBonusSymbol.contains("+")) {
                int bonusAmount = Integer.parseInt(appliedBonusSymbol.replace("+", ""));
                totalReward += bonusAmount;
            }
        }

        return totalReward;
    }
}
