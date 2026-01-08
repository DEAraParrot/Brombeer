package factions;

import core.Faction;

public class Humans extends Faction {

    public Humans(String name) {
        super(name, "data/humans.properties");
    }

    @Override
    protected void applyPopulationGrowth() {
        int currentFood = getResources().getFood();
        if (currentFood >= population) {
            int populationSurplus = currentFood - population;
            double growthMultiplier = 1.0 + (population / 3.0 / population);
            double surplusContribution = populationSurplus * populationSurplusModifier;
            int newPopulation = (int) ((population + surplusContribution) * growthMultiplier);
            population = newPopulation;
        }
    }
}