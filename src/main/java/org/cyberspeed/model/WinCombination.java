package org.cyberspeed.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class WinCombination {
    @JsonProperty("reward_multiplier")
    private Double rewardMultiplier;
    private String when;
    private int count;
    private String group;
    @JsonProperty("covered_areas")
    private List<List<String>> coveredAreas;

    public WinCombination() {
    }

    public WinCombination(Double rewardMultiplier, String when, int count, String group, List<List<String>> coveredAreas) {
        this.rewardMultiplier = rewardMultiplier;
        this.when = when;
        this.count = count;
        this.group = group;
        this.coveredAreas = coveredAreas;
    }

    public Double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public void setRewardMultiplier(Double rewardMultiplier) {
        this.rewardMultiplier = rewardMultiplier;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<List<String>> getCoveredAreas() {
        return coveredAreas;
    }

    public void setCoveredAreas(List<List<String>> coveredAreas) {
        this.coveredAreas = coveredAreas;
    }
}
