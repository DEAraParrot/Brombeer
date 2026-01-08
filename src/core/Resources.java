package core;

import java.util.HashMap;
import java.util.Map;

public class Resources {

    private Map<String, Integer> resources;

    public Resources() {
        this.resources = new HashMap<>();
        this.resources.put("food", 0);
        this.resources.put("wood", 0);
        this.resources.put("stone", 0);
    }

    public int get(String resourceType) {
        return resources.getOrDefault(resourceType, 0);
    }

    public void set(String resourceType, int amount) {
        resources.put(resourceType, Math.max(0, amount));
    }

    public void add(String resourceType, int amount) {
        set(resourceType, get(resourceType) + amount);
    }

    public void subtract(String resourceType, int amount) {
        set(resourceType, get(resourceType) - amount);
    }

    public boolean hasResources(String resourceType, int amount) {
        return get(resourceType) >= amount;
    }

    public int getFood() {
        return get("food");
    }

    public void setFood(int amount) {
        set("food", amount);
    }

    public int getWood() {
        return get("wood");
    }

    public void setWood(int amount) {
        set("wood", amount);
    }

    public int getStone() {
        return get("stone");
    }

    public void setStone(int amount) {
        set("stone", amount);
    }

    public Map<String, Integer> getAll() {
        return new HashMap<>(resources);
    }

    @Override
    public String toString() {
        return "Resources{" +
                "food=" + getFood() +
                ", wood=" + getWood() +
                ", stone=" + getStone() +
                '}';
    }

}
