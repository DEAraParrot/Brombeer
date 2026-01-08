package core;

import java.util.HashMap;
import java.util.Map;

public class BuildingDefinition {

    private String type;
    private int constructionWeeks;
    private Map<String, Integer> constructionCost;
    private Map<String, Integer> upkeep;
    private Map<String, Integer> production;

    public static final Map<String, BuildingDefinition> BUILDINGS = new HashMap<>();

    static {
        BuildingDefinition farm = new BuildingDefinition("Farm", 2);
        farm.setConstructionCost("wood", 50);
        farm.setConstructionCost("stone", 25);
        farm.setUpkeep("food", 5);
        farm.setProduction("food", 100);
        BUILDINGS.put("Farm", farm);

        BuildingDefinition lumbermill = new BuildingDefinition("Lumbermill", 3);
        lumbermill.setConstructionCost("stone", 100);
        lumbermill.setConstructionCost("wood", 50);
        lumbermill.setUpkeep("food", 10);
        lumbermill.setProduction("wood", 80);
        BUILDINGS.put("Lumbermill", lumbermill);

        BuildingDefinition quarry = new BuildingDefinition("Quarry", 4);
        quarry.setConstructionCost("wood", 100);
        quarry.setConstructionCost("stone", 50);
        quarry.setUpkeep("food", 10);
        quarry.setProduction("stone", 60);
        BUILDINGS.put("Quarry", quarry);
    }

    public BuildingDefinition(String type, int constructionWeeks) {
        this.type = type;
        this.constructionWeeks = constructionWeeks;
        this.constructionCost = new HashMap<>();
        this.upkeep = new HashMap<>();
        this.production = new HashMap<>();
    }

    public void setConstructionCost(String resource, int amount) {
        constructionCost.put(resource, amount);
    }

    public void setUpkeep(String resource, int amount) {
        upkeep.put(resource, amount);
    }

    public void setProduction(String resource, int amount) {
        production.put(resource, amount);
    }

    public ConstructingBuilding createConstructing(String name) {
        ConstructingBuilding building = new ConstructingBuilding(name, type, constructionWeeks);

        for (Map.Entry<String, Integer> entry : constructionCost.entrySet()) {
            building.setConstructionCost(entry.getKey(), entry.getValue());
        }

        return building;
    }

    public ConstructingBuilding createConstructing(int weeksRemaining) {
        ConstructingBuilding building = new ConstructingBuilding(type, type, weeksRemaining);

        for (Map.Entry<String, Integer> entry : constructionCost.entrySet()) {
            building.setConstructionCost(entry.getKey(), entry.getValue());
        }

        return building;
    }

    public Building createCompleted(String name) {
        Building building = new Building(name, type);

        for (Map.Entry<String, Integer> entry : constructionCost.entrySet()) {
            building.setConstructionCost(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Integer> entry : upkeep.entrySet()) {
            building.setUpkeep(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Integer> entry : production.entrySet()) {
            building.setProduction(entry.getKey(), entry.getValue());
        }

        return building;
    }

    public String getType() {
        return type;
    }

    public int getConstructionWeeks() {
        return constructionWeeks;
    }

    public Map<String, Integer> getConstructionCost() {
        return new HashMap<>(constructionCost);
    }

    public Map<String, Integer> getUpkeep() {
        return new HashMap<>(upkeep);
    }

    public Map<String, Integer> getProduction() {
        return new HashMap<>(production);
    }

    public static BuildingDefinition get(String type) {
        return BUILDINGS.get(type);
    }

}
