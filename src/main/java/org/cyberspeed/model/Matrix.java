package org.cyberspeed.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Matrix {
    private Integer columns;
    private Integer rows;

    private Map<String, Symbol> symbols;
    private ProbabilityConfig probabilities;
    @JsonProperty("win_combinations")
    private Map<String, WinCombination> winCombinations;

    public Matrix() {
    }

    public Matrix(Integer columns, Integer rows, Map<String, Symbol> symbols, ProbabilityConfig probabilities, Map<String, WinCombination> winCombinations) {
        this.columns = columns;
        this.rows = rows;
        this.symbols = symbols;
        this.probabilities = probabilities;
        this.winCombinations = winCombinations;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(Map<String, Symbol> symbols) {
        this.symbols = symbols;
    }

    public ProbabilityConfig getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(ProbabilityConfig probabilities) {
        this.probabilities = probabilities;
    }

    public Map<String, WinCombination> getWinCombinations() {
        return winCombinations;
    }

    public void setWinCombinations(Map<String, WinCombination> winCombinations) {
        this.winCombinations = winCombinations;
    }
}
