package core;

import java.util.HashMap;
import java.util.Map;

public class Building extends BuildingBase {

    private Map<String, Integer> upkeep;
    private Map<String, Integer> production;

    public Building(String name, String type) {
        super(name, type);
        this.upkeep = new HashMap<>();
        this.production = new HashMap<>();
    }

    public void setUpkeep(String resource, int amount) {
        upkeep.put(resource, amount);
    }

    public void setProduction(String resource, int amount) {
        production.put(resource, amount);
    }

    public Map<String, Integer> getUpkeep() {
        return new HashMap<>(upkeep);
    }

    public Map<String, Integer> getProduction() {
        return new HashMap<>(production);
    }

    @Override
    public String toString() {
        return "Building{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

}
