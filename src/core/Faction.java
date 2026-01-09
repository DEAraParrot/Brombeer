package core;

import java.io.*;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public abstract class Faction {

    protected String name;
    protected int population;
    protected int actionPoints;
    protected int usedActionPoints;
    protected int might;

    protected Resources resources;
    protected Map<String, Army> armies;
    protected int armyCounter;
    protected Map<String, Building> buildings;
    protected List<Building> buildingQueue;
    protected int maxConcurrentBuildings;
    protected Research research;
    protected String features;
    protected double populationSurplusModifier;
    protected Traits traits;

    protected Properties config;
    protected Properties state;

    protected File configFile;
    protected File stateFile;

    public Faction(String name, String configPath) {
        this.name = name;
        this.configFile = new File(configPath);
        this.stateFile = new File("saved/" + name.replace(" ", "_") + ".state");
        this.resources = new Resources();
        this.armies = new HashMap<>();
        this.armyCounter = 0;
        this.usedActionPoints = 0;
        this.buildings = new HashMap<>();
        this.buildingQueue = new ArrayList<>();
        this.maxConcurrentBuildings = 1;
        this.research = new Research();
        this.features = "";
        this.populationSurplusModifier = 0.1;
        this.traits = new Traits();
        this.config = new Properties();
        this.state = new Properties();
    }

    protected void initializeTraits() {
    }

    public void loadFactionData() throws IOException {
        if (configFile.exists()) {
            config.load(new FileInputStream(configFile));
            population = Integer.parseInt(config.getProperty("population", "1000"));
            resources.setFood(Integer.parseInt(config.getProperty("food", "500")));
            resources.setWood(Integer.parseInt(config.getProperty("wood", "200")));
            resources.setStone(Integer.parseInt(config.getProperty("stone", "200")));
            
            String maxConcurrentStr = config.getProperty("maxConcurrentBuildings");
            if (maxConcurrentStr != null && !maxConcurrentStr.isEmpty()) {
                maxConcurrentBuildings = Integer.parseInt(maxConcurrentStr);
            }
            
            String initialArmiesStr = config.getProperty("initialArmies", "").trim();
            if (!initialArmiesStr.isEmpty()) {
                for (String armyEntry : initialArmiesStr.split(",")) {
                    String[] parts = armyEntry.split(":");
                    if (parts.length == 2) {
                        String armyName = parts[0].trim();
                        int armyPopulation = Integer.parseInt(parts[1].trim());
                        createArmy(armyName, armyPopulation);
                    }
                }
            }
            
            String initialBuildingsStr = config.getProperty("initialBuildings", "").trim();
            if (!initialBuildingsStr.isEmpty()) {
                for (String buildingType : initialBuildingsStr.split(",")) {
                    buildingType = buildingType.trim();
                    if (!buildingType.isEmpty()) {
                        createInitialBuilding(buildingType);
                    }
                }
            }
        } else {
            throw new FileNotFoundException("Config file not found for: " + name);
        }
    }

    public void processWeek() {
        calculateActionPoints();
        consumeFood();
        applyFoodPopulationPenalty();
        processBuildings();
        applyBuildingProduction();
        applyBuildingUpkeep();
        processArmies();
        calculateMight();
        applyPopulationGrowth();
        resetWeeklyState();
    }

    protected void calculateActionPoints() {
        actionPoints = traits.calculateTotalActionPoints();
        usedActionPoints = 0;
    }

    protected int getEffectiveMaxConcurrentBuildings() {
        return maxConcurrentBuildings + traits.getMaxConcurrentBuildingsBonus();
    }

    protected void consumeFood() {
        int baseConsumption = (population + getTotalArmiesPopulation()) / 2;
        int consumptionModifier = traits.getPopulationConsumptionModifier();
        int weeklyConsumption = Math.max(0, baseConsumption + consumptionModifier);
        int currentFood = resources.getFood();

        if (currentFood < weeklyConsumption) {
            resources.setFood(0);
            int populationToStarve = weeklyConsumption - currentFood;
            population = Math.max(0, population - populationToStarve);
        } else {
            resources.setFood(currentFood - weeklyConsumption);
        }
    }

    protected void applyFoodPopulationPenalty() {
        int currentFood = resources.getFood();
        if (currentFood < population) {
            int difference = population - currentFood;
            double percentageReduction = (double) difference / population;
            population = (int) (population * (1 - percentageReduction));
        }
    }

    protected void applyPopulationGrowth() {
        int currentFood = resources.getFood();
        if (currentFood >= population) {
            int populationSurplus = currentFood - population;
            double baseGrowthMultiplier = 1.0 + (population / 4.0 / population);
            int growthModifier = traits.getPopulationGrowthModifier();
            double effectiveGrowthMultiplier = baseGrowthMultiplier + (growthModifier * 0.01);
            double surplusContribution = populationSurplus * populationSurplusModifier;
            int newPopulation = (int) ((population + surplusContribution) * effectiveGrowthMultiplier);
            population = newPopulation;
        }
    }

    protected void calculateMight() {
        might = 0;
        for (Army army : armies.values()) {
            might += army.getMight();
        }
    }

    protected void resetWeeklyState() {
        usedActionPoints = 0;
    }

    public void saveState() throws IOException {
        FactionStateFormatter.save(this, stateFile);
    }

    public void createArmy(String name, int amount) {
        if (amount <= 0 || amount > population) return;
        population -= amount;
        armyCounter++;

        Army army = new Army(name != null ? name : "army_" + armyCounter, amount);
        int mightBonus = traits.getTrait("armyMightBonus");
        if (mightBonus != 0) {
            army.setMightModifier(mightBonus);
        }
        armies.put(army.getId(), army);
    }

    public void reinforceArmy(String id, int amount) {
        Army army = armies.get(id);
        if (army == null || amount <= 0 || amount > population) return;

        population -= amount;
        army.reinforce(amount);
    }

    public void createInitialBuilding(String buildingType) {
        BuildingDefinition def = BuildingDefinition.get(buildingType);
        if (def == null) {
            throw new IllegalArgumentException("Unknown building type: " + buildingType);
        }

        Building building = def.createCompleted(buildingType + "_" + (buildings.size() + 1));
        buildings.put(building.getId(), building);
    }

    public void queueBuilding(String buildingType) {
        BuildingDefinition def = BuildingDefinition.get(buildingType);
        if (def == null) {
            throw new IllegalArgumentException("Unknown building type: " + buildingType);
        }

        int effectiveMaxConcurrent = getEffectiveMaxConcurrentBuildings();
        if (buildingQueue.size() >= effectiveMaxConcurrent) {
            throw new IllegalStateException("Building queue is full (max: " + effectiveMaxConcurrent + ")");
        }

        int buildingsOfTypeUnderConstruction = (int) buildingQueue.stream()
                .filter(b -> b.getType().equals(buildingType))
                .count();
        
        int buildingsOfTypeCompleted = (int) buildings.values().stream()
                .filter(b -> b.getType().equals(buildingType))
                .count();
        
        int totalOfType = buildingsOfTypeUnderConstruction + buildingsOfTypeCompleted;
        int effectiveMaxOfType = def.getMaxConcurrentOfType() + traits.getMaxBuildingTypeLimit(buildingType);
        
        if (totalOfType >= effectiveMaxOfType) {
            throw new IllegalStateException("Cannot build more " + buildingType + " (max: " + effectiveMaxOfType + ")");
        }

        Map<String, Integer> cost = def.getConstructionCost();
        for (Map.Entry<String, Integer> entry : cost.entrySet()) {
            resources.subtract(entry.getKey(), entry.getValue());
        }

        Building building = def.createConstructing(buildingType + "_" + (buildings.size() + buildingQueue.size() + 1));
        buildingQueue.add(building);
    }

    public void postponeBuilding(String buildingId) {
        for (Building building : buildingQueue) {
            if (building.getId().equals(buildingId)) {
                buildingQueue.remove(building);
                buildingQueue.add(building);
                return;
            }
        }
        throw new IllegalArgumentException("Building not found in queue: " + buildingId);
    }

    public void demolishBuilding(String buildingId) {
        Building building = buildings.remove(buildingId);
        if (building != null) {
            Map<String, Integer> cost = building.getConstructionCost();
            for (Map.Entry<String, Integer> entry : cost.entrySet()) {
                resources.add(entry.getKey(), (int) (entry.getValue() * 0.15));
            }
        }
    }

    public void applyResearch(String field, int investedAP) {
        if (investedAP < 0) {
            throw new IllegalArgumentException("Invested AP cannot be negative");
        }

        research.addProgress(field, investedAP);

        ResearchResult result = research.calculateOutcome(field, investedAP);
        research.recordResult(field, result);

        if (result == ResearchResult.DISCOVERY) {
            applyDiscovery(field);
        } else if (result == ResearchResult.BREAKTHROUGH) {
            applyBreakthrough(field);
        }
    }

    protected void applyDiscovery(String field) {
    }

    protected void applyBreakthrough(String field) {
    }

    protected void processBuildings() {
        List<Building> completedBuildings = new ArrayList<>();

        for (Building constructing : buildingQueue) {
            constructing.addConstruction(1);
            
            if (constructing.isComplete()) {
                BuildingDefinition def = BuildingDefinition.get(constructing.getType());
                if (def != null) {
                    Building completed = def.createCompleted(constructing.getName());
                    buildings.put(completed.getId(), completed);
                }
                completedBuildings.add(constructing);
            } else if (constructing.isConstructionFailed()) {
                completedBuildings.add(constructing);
            }
        }

        buildingQueue.removeAll(completedBuildings);
    }

    protected void applyBuildingProduction() {
        for (Building building : buildings.values()) {
            for (Map.Entry<String, Integer> entry : building.getProduction().entrySet()) {
                String resourceType = entry.getKey();
                int baseProduction = entry.getValue();
                int productionModifier = traits.getResourceProductionModifier(resourceType);
                int effectiveProduction = baseProduction + productionModifier;
                resources.add(resourceType, Math.max(0, effectiveProduction));
            }
        }
    }

    protected void applyBuildingUpkeep() {
        for (Building building : buildings.values()) {
            for (Map.Entry<String, Integer> entry : building.getUpkeep().entrySet()) {
                String resourceType = entry.getKey();
                int baseUpkeep = entry.getValue();
                int consumptionModifier = traits.getResourceConsumptionModifier(resourceType);
                int effectiveUpkeep = baseUpkeep + consumptionModifier;
                resources.subtract(resourceType, Math.max(0, effectiveUpkeep));
            }
        }
    }

    protected void processArmies() {
        for (Army army : armies.values()) {
            if (army.getState() == Army.ArmyState.ATTACKING) {
                army.advanceTravelTime();
                if (army.hasReachedTarget()) {
                    resolveCombat(army);
                }
            } else if (army.getState() == Army.ArmyState.RETREATING) {
                army.advanceTravelTime();
                if (army.getTravelWeeksRemaining() <= 0) {
                    army.setState(Army.ArmyState.DEFENDING);
                }
            }
        }
    }

    protected void resolveCombat(Army army) {
    }

    public int getTotalArmiesPopulation() {
        return armies.values().stream().mapToInt(Army::getPopulation).sum();
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public int getActionPoints() {
        return actionPoints;
    }

    public int getUsedActionPoints() {
        return usedActionPoints;
    }

    public void useActionPoints(int amount) {
        if (usedActionPoints + amount > actionPoints) {
            throw new IllegalArgumentException("Not enough action points available");
        }
        usedActionPoints += amount;
    }

    public Resources getResources() {
        return resources;
    }

    public Map<String, Army> getArmies() {
        return armies;
    }

    public Army getArmy(String id) {
        return armies.get(id);
    }

    public Map<String, Building> getBuildings() {
        return buildings;
    }

    public Building getBuilding(String id) {
        return buildings.get(id);
    }

    public Building getBuildingUnderConstruction() {
        return buildingQueue.isEmpty() ? null : buildingQueue.get(0);
    }

    public List<Building> getBuildingQueue() {
        return new ArrayList<>(buildingQueue);
    }

    public int getMaxConcurrentBuildings() {
        return maxConcurrentBuildings;
    }

    public void setMaxConcurrentBuildings(int max) {
        this.maxConcurrentBuildings = Math.max(1, max);
    }

    public Research getResearch() {
        return research;
    }

    public int getMight() {
        return might;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features != null ? features : "";
    }

    public double getPopulationSurplusModifier() {
        return populationSurplusModifier;
    }

    public void setPopulationSurplusModifier(double modifier) {
        this.populationSurplusModifier = modifier;
    }

    public Traits getTraits() {
        return traits;
    }

    public void addTrait(String traitName, int actionPointValue) {
        traits.addTrait(traitName, actionPointValue);
    }

    public void removeTrait(String traitName) {
        traits.removeTrait(traitName);
    }

    public void setTrait(String traitName, int actionPointValue) {
        traits.setTrait(traitName, actionPointValue);
    }

}
