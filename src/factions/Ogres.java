package factions;

import core.Faction;

public class Ogres extends Faction {

    public Ogres(String name) {
        super(name, "data/ogres.properties");
    }

    @Override
    protected void calculateMight() {
        might = armiesPopulation * 2;
    }
}
