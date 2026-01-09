package factions;

import core.Faction;
import core.Traits.TraitDefinition;

public class Humans extends Faction {

    public Humans(String name) {
        super(name, "data/humans.properties");
        initializeTraits();
    }

    @Override
    protected void initializeTraits() {
        traits.setTrait("actionPoints", 7, new TraitDefinition("Ambitious", "+7 Action Points", "Action Points"));
        traits.setTrait("populationGrowthModifier", 8, new TraitDefinition("Fertile", "+8% Population Growth", "Population Growth"));
        traits.setTrait("resourceConsumption_food", -1, new TraitDefinition("Disciplined", "-1 Food Consumption", "Food Consumption"));
        traits.setTrait("maxConcurrentBuildings", 1, new TraitDefinition("Builders", "+1 Maximum Concurrent Buildings", "Maximum Concurrent Buildings"));
        traits.setTrait("maxBuildingType_Farm", 1, new TraitDefinition("Agricultural", "+1 Maximum Farms", "Maximum Farms"));
    }
}