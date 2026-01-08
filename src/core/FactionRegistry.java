package core;

import java.util.HashMap;
import java.util.Map;

public class FactionRegistry {

    private Map<String, Faction> factions;
    private Map<String, Map<String, Integer>> distances;

    public FactionRegistry() {
        this.factions = new HashMap<>();
        this.distances = new HashMap<>();
    }

    public void registerFaction(String name, Faction faction) {
        factions.put(name, faction);
        distances.put(name, new HashMap<>());
    }

    public Faction getFaction(String name) {
        return factions.get(name);
    }

    public Map<String, Faction> getAllFactions() {
        return new HashMap<>(factions);
    }

    public void setDistance(String faction1, String faction2, int weeks) {
        if (!distances.containsKey(faction1)) {
            distances.put(faction1, new HashMap<>());
        }
        if (!distances.containsKey(faction2)) {
            distances.put(faction2, new HashMap<>());
        }

        distances.get(faction1).put(faction2, weeks);
        distances.get(faction2).put(faction1, weeks);
    }

    public int getDistance(String faction1, String faction2) {
        if (!distances.containsKey(faction1)) {
            return -1;
        }
        return distances.get(faction1).getOrDefault(faction2, -1);
    }

    public boolean hasFaction(String name) {
        return factions.containsKey(name);
    }

}
