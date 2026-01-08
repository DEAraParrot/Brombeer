package core;

import java.util.HashMap;
import java.util.Map;

public abstract class BuildingBase {

    protected String id;
    protected String name;
    protected String type;
    protected Map<String, Integer> constructionCost;

    public BuildingBase(String name, String type) {
        this.name = name;
        this.type = type;
        this.id = type.toLowerCase() + "_" + System.nanoTime();
        this.constructionCost = new HashMap<>();
    }

    public void setConstructionCost(String resource, int amount) {
        constructionCost.put(resource, amount);
    }

    public Map<String, Integer> getConstructionCost() {
        return new HashMap<>(constructionCost);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

}
