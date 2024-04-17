package org.cyberspeed.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cyberspeed.App;
import org.cyberspeed.model.Matrix;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Test the constructor initialized the fields correctly")
    public void testConstructor_CASE1() throws IOException {
        Matrix config = getMatrix("MatrixConfigTest.json");
        ScratchGameService gameService = new ScratchGameService(config);
        assertEquals(config.getRows(), gameService.getRows());
        assertEquals(config.getColumns(), gameService.getColumns());
        assertEquals(config.getSymbols(), gameService.getSymbols());
        assertEquals(config.getProbabilities(), gameService.getProbabilities());
        assertEquals(config.getWinCombinations(), gameService.getWinCombinations());
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
        assertNotEquals(generatedMatrix1, generatedMatrix2);
    }
}