package org.cyberspeed.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cyberspeed.App;
import org.cyberspeed.model.Matrix;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScratchGameServiceTest {

    private static Matrix getMatrix(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new RuntimeException("File not found: MatrixConfigTest.json");
        }
        Matrix config = objectMapper.readValue(inputStream, Matrix.class);
        inputStream.close();
        return config;
    }

    public static void printMatrix(String[][] matrix) {
        Arrays.stream(matrix)
                .map(row -> String.join(" ", row))
                .forEach(System.out::println);
    }

    public static double calculatePercentage(double n, double m) {
        return round((n / m) * 100, 2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        DecimalFormat df = new DecimalFormat("#." + "0".repeat(places));
        return Double.parseDouble(df.format(value));
    }


    @Test
    @DisplayName("Test the generated Matrix is 3x3 Matrix")
    void generateMatrix_CASE2() throws IOException {
        Matrix config = getMatrix("MatrixConfigTest.json");
        ScratchGameService gameService = new ScratchGameService(config);
        String[][] generatedMatrix = gameService.generateMatrix();
        assertEquals(3, generatedMatrix.length); // Check rows
        assertEquals(3, generatedMatrix[0].length); // Check columns
    }

    @Test
    @DisplayName("Test the each of the cells in the generated 3x3 Matrix is filled with any o the A, B, C, D, E, F")
    void generateMatrix_CASE3() throws IOException {
        Matrix config = getMatrix("MatrixConfigTest.json");
        ScratchGameService gameService = new ScratchGameService(config);
        String[][] generatedMatrix = gameService.generateMatrix();
        String[] allowedLetters = {"A", "B", "C", "D", "E", "F"};
        for (String[] row : generatedMatrix) {
            for (String cell : row) {
                assertTrue(Arrays.asList(allowedLetters).contains(cell), "Cell value " + cell + " is not one of the allowed letters");
            }
        }
    }

    @Test
    @DisplayName("Test each matrix generation script generating different random matrix elements")
    void generateMatrix_CASE4() throws IOException {
        Matrix config = getMatrix("MatrixConfigTest.json");
        ScratchGameService gameService = new ScratchGameService(config);
        String[][] generatedMatrix1 = gameService.generateMatrix();
        String[][] generatedMatrix2 = gameService.generateMatrix();
        printMatrix(generatedMatrix1);
        System.out.println("=====");
        printMatrix(generatedMatrix2);
        System.out.println("===================================");
        if (Arrays.deepEquals(generatedMatrix1, generatedMatrix2)) {
            System.out.println("Both matrix contains same elements");
        } else {
            System.out.println("Both matrix contains different elements");
        }
        //assertNotEquals(generatedMatrix1, generatedMatrix2,"Matrix elements are same");
    }

    @Test
    @DisplayName("Test any bonus assigned to the generated matrix")
    void assignBonusSymbol_CASE1() throws IOException {
        Matrix config = getMatrix("MatrixConfigTest.json");
        ScratchGameService gameService = new ScratchGameService(config);
        String[][] generatedMatrix = gameService.generateMatrix();

        StringBuilder assignedBonus = new StringBuilder();
        gameService.assignBonusSymbol(generatedMatrix, assignedBonus);
        assertTrue(assignedBonus.length() > 0);
        boolean isAssigned = false;
        for (String[] row : generatedMatrix) {
            for (String cell : row) {
                if (assignedBonus.toString().equals(cell)) {
                    isAssigned = true;
                    break;
                }
            }
            if (isAssigned) {
                break;
            }
        }
        assertTrue(isAssigned, "No cell in the matrix is assigned by the bonus symbol");
        String[] allowedLetters = {"10x", "5x", "+1000", "+500", "MISS"};
    }

    @Test
    @DisplayName("Test bonus assigned  value are different for different method calls")
    void assignBonusSymbol_CASE2() throws IOException {
        Matrix config = getMatrix("MatrixConfigTest.json");
        ScratchGameService gameService = new ScratchGameService(config);
        String[][] generatedMatrix = gameService.generateMatrix();
        Map<String, Integer> bonusCount = new HashMap<>();

        int numSimulations = 1000;
        for (int i = 0; i < numSimulations; i++) {
            StringBuilder assignedBonus = new StringBuilder();
            gameService.assignBonusSymbol(generatedMatrix, assignedBonus);
            if (!bonusCount.containsKey(assignedBonus.toString())) {
                bonusCount.put(assignedBonus.toString(), 1);
            } else {
                bonusCount.put(assignedBonus.toString(), bonusCount.get(assignedBonus.toString()) + 1);
            }
        }
        for (Map.Entry<String, Integer> entry : bonusCount.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value: " + entry.getValue() + " Percentage: " + calculatePercentage(entry.getValue(), numSimulations) + "%");
        }
    }

    @Test
    @DisplayName("Test both same_symbol combination and vertically")
    void checkWinningCombinations_CASE1() throws IOException {

        Matrix config = getMatrix("MatrixConfigTest.json");
        ScratchGameService gameService = new ScratchGameService(config);
        String[][] generatedMatrix = {
                {"A", "A", "B"},
                {"A", "+1000", "B"},
                {"A", "A", "B"}
        };
        Map<String, Set<String>> appliedWinningCombinations =gameService.checkWinningCombinations(generatedMatrix);

        assertTrue(appliedWinningCombinations.get("A").contains("same_symbol_5_times"));
        assertTrue(appliedWinningCombinations.get("A").contains("same_symbols_vertically"));
        assertTrue(appliedWinningCombinations.get("B").contains("same_symbol_3_times"));
        assertTrue(appliedWinningCombinations.get("B").contains("same_symbols_vertically"));
    }

    @Test
    @DisplayName("Test both same_symbol combination and same_symbols_diagonally_left_to_right")
    void checkWinningCombinations_CASE2() throws IOException {

        Matrix config = getMatrix("MatrixConfigTest.json");
        ScratchGameService gameService = new ScratchGameService(config);
        String[][] generatedMatrix = {
                {"A", "D", "B"},
                {"C", "A", "B"},
                {"C", "F", "A"}
        };
        Map<String, Set<String>> appliedWinningCombinations =gameService.checkWinningCombinations(generatedMatrix);

        assertTrue(appliedWinningCombinations.get("A").contains("same_symbols_diagonally_left_to_right"));
        assertTrue(appliedWinningCombinations.get("A").contains("same_symbol_3_times"));
    }

    @Test
    @DisplayName("Test both same_symbol combination and horizontally")
    void checkWinningCombinations_CASE3() throws IOException {

        Matrix config = getMatrix("MatrixConfigTest.json");
        ScratchGameService gameService = new ScratchGameService(config);
        String[][] generatedMatrix = {
                {"A", "A", "A"},
                {"C", "E", "B"},
                {"C", "F", "A"}
        };
        Map<String, Set<String>> appliedWinningCombinations =gameService.checkWinningCombinations(generatedMatrix);

        assertTrue(appliedWinningCombinations.get("A").contains("same_symbols_horizontally"));
        assertTrue(appliedWinningCombinations.get("A").contains("same_symbol_4_times"));
    }

    @Test
    @DisplayName("Test both same_symbol combination and same_symbols_diagonally_right_to_left")
    void checkWinningCombinations_CASE4() throws IOException {

        Matrix config = getMatrix("MatrixConfigTest.json");
        ScratchGameService gameService = new ScratchGameService(config);
        String[][] generatedMatrix = {
                {"B", "D", "A"},
                {"C", "A", "B"},
                {"A", "F", "E"}
        };
        Map<String, Set<String>> appliedWinningCombinations =gameService.checkWinningCombinations(generatedMatrix);

        assertTrue(appliedWinningCombinations.get("A").contains("same_symbols_diagonally_right_to_left"));
        assertTrue(appliedWinningCombinations.get("A").contains("same_symbol_3_times"));
    }

    @Test
    @DisplayName("Test the reward calculation")
    void calculateReward() throws IOException {
        Matrix config = getMatrix("MatrixConfigTest.json");
        ScratchGameService gameService = new ScratchGameService(config);
        String[][] generatedMatrix = {
                {"A", "A", "B"},
                {"A", "+1000", "B"},
                {"A", "A", "B"}
        };
        int bettingAmount=100;
        String assignedBonus="+1000";
        Map<String, Set<String>> appliedWinningCombinations =gameService.checkWinningCombinations(generatedMatrix);
        double reward = gameService.calculateReward(bettingAmount, appliedWinningCombinations, assignedBonus);
        assertEquals(6600.0,reward);
    }

    @Test
    @DisplayName("If one symbols matches more than winning combinations then reward should be multiplied")
    void calculateReward_CASE2() throws IOException {
        Matrix config = getMatrix("MatrixConfigTest.json");
        ScratchGameService gameService = new ScratchGameService(config);
        String[][] generatedMatrix = {
                {"A", "A", "B"},
                {"A", "+1000", "B"},
                {"A", "A", "B"}
        };
        int bettingAmount=100;
        String assignedBonus="+1000";
        Map<String, Set<String>> appliedWinningCombinations =gameService.checkWinningCombinations(generatedMatrix);
        double reward = gameService.calculateReward(bettingAmount, appliedWinningCombinations, assignedBonus);
        assertEquals(6600.0,reward);
    }

    @Test
    @DisplayName("If the more than one symbols matches any winning combinations then reward should be summed")
    void calculateReward_CASE3() throws IOException {
        Matrix config = getMatrix("MatrixConfigTest.json");
        ScratchGameService gameService = new ScratchGameService(config);
        String[][] generatedMatrix = {
                {"A", "A", "B"},
                {"A", "+1000", "B"},
                {"A", "A", "B"}
        };
        int bettingAmount=100;
        String assignedBonus="+1000";
        Map<String, Set<String>> appliedWinningCombinations =gameService.checkWinningCombinations(generatedMatrix);
        double reward = gameService.calculateReward(bettingAmount, appliedWinningCombinations, assignedBonus);
        assertEquals(6600.0,reward);
    }


}