package factions;

import core.Faction;
import core.Traits.TraitDefinition;

public class Dwarfs extends Faction {

    public Dwarfs(String name) {
        super(name, "data/dwarfs.properties");
        initializeTraits();
    }

    @Override
    protected void initializeTraits() {
        traits.setTrait("actionPoints", 7, new TraitDefinition("Industrious", "+7 Action Points", "Action Points"));
        traits.setTrait("resourceProduction_stone", 15, new TraitDefinition("Master Miners", "+15 Stone Production", "Stone Production"));
        traits.setTrait("populationConsumptionModifier", -2, new TraitDefinition("Efficient", "-2 Food Consumption", "Food Consumption"));
        traits.setTrait("maxBuildingType_Quarry", 1, new TraitDefinition("Quarry Specialist", "+1 Maximum Quarries", "Maximum Quarries"));
    }

    @Override
    protected void consumeFood() {
        int weeklyConsumption = (population + getTotalArmiesPopulation()) / 3;
        int currentFood = resources.getFood();

        if (currentFood < weeklyConsumption) {
            resources.setFood(0);
            int populationToStarve = weeklyConsumption - currentFood;
            population = Math.max(0, population - populationToStarve);
        } else {
            resources.setFood(currentFood - weeklyConsumption);
        }

        if (resources.getFood() < population && resources.getFood() > 0) {
            population = resources.getFood();
        }
    }

    @Override
    public void setMaxConcurrentBuildings(int max) {
        this.maxConcurrentBuildings = 2;
    }
}
