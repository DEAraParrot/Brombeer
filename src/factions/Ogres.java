package factions;

import core.Faction;
import core.Traits.TraitDefinition;

public class Ogres extends Faction {

    public Ogres(String name) {
        super(name, "data/ogres.properties");
        initializeTraits();
    }

    @Override
    protected void initializeTraits() {
        traits.setTrait("actionPoints", 9, new TraitDefinition("Ferocious", "+9 Action Points", "Action Points"));
        traits.setTrait("populationConsumptionModifier", 3, new TraitDefinition("Voracious", "+3 Food Consumption", "Food Consumption"));
        traits.setTrait("resourceProduction_food", 8, new TraitDefinition("Hunters", "+8 Food Production", "Food Production"));
        traits.setTrait("maxConcurrentBuildings", -1, new TraitDefinition("Narrow Minded", "-1 Maximum Concurrent Buildings", "Maximum Concurrent Buildings"));
        traits.setTrait("armyMightBonus", 100, new TraitDefinition("Narrow Minded", "+100 Might per Army", "Army Might"));
    }
}
