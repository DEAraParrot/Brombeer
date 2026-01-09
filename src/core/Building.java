package core;

import java.util.HashMap;
import java.util.Map;

public class Building {

    public enum BuildingStatus {
        UNDER_CONSTRUCTION,
        COMPLETED
    }

    protected String id;
    protected String name;
    protected String type;
    protected Map<String, Integer> constructionCost;
    protected BuildingStatus status;

    private Map<String, Integer> upkeep;
    private Map<String, Integer> production;
    private int constructionWeeksRemaining;
    private int weeksSinceLastProgress;
    private static final int MAX_DORMANT_WEEKS = 3;

    public Building(String name, String type) {
        this.name = name;
        this.type = type;
        this.id = type.toLowerCase() + "_" + System.nanoTime();
        this.constructionCost = new HashMap<>();
        this.upkeep = new HashMap<>();
        this.production = new HashMap<>();
        this.status = BuildingStatus.COMPLETED;
        this.constructionWeeksRemaining = 0;
        this.weeksSinceLastProgress = 0;
    }

    public Building(String name, String type, int constructionWeeks) {
        this.name = name;
        this.type = type;
        this.id = type.toLowerCase() + "_" + System.nanoTime();
        this.constructionCost = new HashMap<>();
        this.upkeep = new HashMap<>();
        this.production = new HashMap<>();
        this.status = BuildingStatus.UNDER_CONSTRUCTION;
        this.constructionWeeksRemaining = constructionWeeks;
        this.weeksSinceLastProgress = 0;
    }

    public void setConstructionCost(String resource, int amount) {
        constructionCost.put(resource, amount);
    }

    public Map<String, Integer> getConstructionCost() {
        return new HashMap<>(constructionCost);
    }

    public void setUpkeep(String resource, int amount) {
        upkeep.put(resource, amount);
    }

    public Map<String, Integer> getUpkeep() {
        return new HashMap<>(upkeep);
    }

    public void setProduction(String resource, int amount) {
        production.put(resource, amount);
    }

    public Map<String, Integer> getProduction() {
        return new HashMap<>(production);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public BuildingStatus getStatus() {
        return status;
    }

    public void addConstruction(int weeksCompleted) {
        if (constructionWeeksRemaining > 0) {
            constructionWeeksRemaining -= weeksCompleted;
            weeksSinceLastProgress = 0;
        }
    }

    public void addDormantWeek() {
        weeksSinceLastProgress++;
    }

    public boolean isConstructionFailed() {
        return weeksSinceLastProgress > MAX_DORMANT_WEEKS;
    }

    public boolean isComplete() {
        return constructionWeeksRemaining <= 0;
    }

    public int getConstructionWeeksRemaining() {
        return constructionWeeksRemaining;
    }

    public int getWeeksSinceLastProgress() {
        return weeksSinceLastProgress;
    }

    public void markAsCompleted() {
        this.status = BuildingStatus.COMPLETED;
        this.constructionWeeksRemaining = 0;
        this.weeksSinceLastProgress = 0;
    }

    @Override
    public String toString() {
        if (status == BuildingStatus.UNDER_CONSTRUCTION) {
            return "Building{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", status=" + status +
                    ", weeksRemaining=" + constructionWeeksRemaining +
                    '}';
        } else {
            return "Building{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", status=" + status +
                    '}';
        }
    }

}
