package factions;

import core.Faction;

public class Humans extends Faction {

    public Humans(String name) {
        super(name, "data/humans.properties");
    }

    @Override
    protected void applyPopulationGrowth() {
        population += population / 3;
    }
}