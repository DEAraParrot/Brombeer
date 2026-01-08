package factions;

import core.Faction;

public class Dwarfs extends Faction {

    public Dwarfs(String name) {
        super(name, "data/dwarfs.properties");
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
