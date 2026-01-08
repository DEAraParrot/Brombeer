package factions;

import core.Faction;

public class Ogres extends Faction {

    public Ogres(String name) {
        super(name, "data/ogres.properties");
    }

    @Override
    protected void calculateMight() {
        might = 0;
        for (var army : armies.values()) {
            might += army.getMight() * 2;
        }
    }
}
