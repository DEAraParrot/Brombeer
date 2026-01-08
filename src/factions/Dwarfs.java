package factions;

import core.Faction;

public class Dwarfs extends Faction {

    public Dwarfs(String name) {
        super(name, "data/dwarfs.properties");
    }

    @Override
    protected void consumeFood() {
        int reducedConsumption = population / 3;
        food -= reducedConsumption;

        if (food < population) {
            population = food;
        }
    }
}
