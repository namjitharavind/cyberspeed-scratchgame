package org.cyberspeed.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ProbabilityConfig {
    @JsonProperty("standard_symbols")
    private List<ProbabilitySymbol> standardSymbols;
    @JsonProperty("bonus_symbols")
    private ProbabilitySymbol bonusSymbol;

    public List<ProbabilitySymbol> getStandardSymbols() {
        return standardSymbols;
    }

    public void setStandardSymbols(List<ProbabilitySymbol> standardSymbols) {
        this.standardSymbols = standardSymbols;
    }

    public ProbabilitySymbol getBonusSymbol() {
        return bonusSymbol;
    }

    public void setBonusSymbol(ProbabilitySymbol bonusSymbol) {
        this.bonusSymbol = bonusSymbol;
    }
}
