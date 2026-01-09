package core;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;

public class Traits {

    public static class TraitDefinition {
        public final String displayName;
        public final String description;
        public final String category;

        public TraitDefinition(String displayName, String description, String category) {
            this.displayName = displayName;
            this.description = description;
            this.category = category;
        }
    }

    private Map<String, Integer> traits;
    private Map<String, TraitDefinition> traitDefinitions;

    public static final Map<String, String> TRAIT_TYPES = new HashMap<>();

    static {
        TRAIT_TYPES.put("actionPoints", "integer");
        TRAIT_TYPES.put("maxConcurrentBuildings", "integer");
        TRAIT_TYPES.put("populationGrowthModifier", "double");
        TRAIT_TYPES.put("populationConsumptionModifier", "double");
        TRAIT_TYPES.put("maxBuildingType_", "integer");
    }

    public Traits() {
        this.traits = new HashMap<>();
        this.traitDefinitions = new HashMap<>();
    }

    public void addTrait(String traitName, int value, TraitDefinition definition) {
        traits.put(traitName, traits.getOrDefault(traitName, 0) + value);
        if (definition != null) {
            traitDefinitions.put(traitName, definition);
        }
    }

    public void addTrait(String traitName, int value) {
        traits.put(traitName, traits.getOrDefault(traitName, 0) + value);
    }

    public void removeTrait(String traitName) {
        traits.remove(traitName);
        traitDefinitions.remove(traitName);
    }

    public void setTrait(String traitName, int value) {
        if (value <= 0) {
            traits.remove(traitName);
        } else {
            traits.put(traitName, value);
        }
    }

    public void setTrait(String traitName, int value, TraitDefinition definition) {
        setTrait(traitName, value);
        if (definition != null) {
            traitDefinitions.put(traitName, definition);
        }
    }

    public void registerTraitDefinition(String traitName, TraitDefinition definition) {
        traitDefinitions.put(traitName, definition);
    }

    public TraitDefinition getTraitDefinition(String traitName) {
        return traitDefinitions.get(traitName);
    }

    public int getTrait(String traitName) {
        return traits.getOrDefault(traitName, 0);
    }

    public boolean hasTrait(String traitName) {
        return traits.containsKey(traitName);
    }

    public int calculateTotalActionPoints() {
        int total = 0;
        for (Map.Entry<String, Integer> entry : traits.entrySet()) {
            if (entry.getKey().equals("actionPoints")) {
                total += entry.getValue();
            }
        }
        return total;
    }

    public int getMaxConcurrentBuildingsBonus() {
        return getTrait("maxConcurrentBuildings");
    }

    public int getMaxBuildingTypeLimit(String buildingType) {
        return getTrait("maxBuildingType_" + buildingType);
    }

    public int getPopulationGrowthModifier() {
        return getTrait("populationGrowthModifier");
    }

    public int getPopulationConsumptionModifier() {
        return getTrait("populationConsumptionModifier");
    }

    public int getResourceProductionModifier(String resourceType) {
        return getTrait("resourceProduction_" + resourceType);
    }

    public int getResourceConsumptionModifier(String resourceType) {
        return getTrait("resourceConsumption_" + resourceType);
    }

    public Map<String, Integer> getAllTraits() {
        return new HashMap<>(traits);
    }

    public Map<String, TraitDefinition> getAllTraitDefinitions() {
        return new HashMap<>(traitDefinitions);
    }

    public Map<String, Integer> getTraitSummationByCategory() {
        Map<String, Integer> summation = new LinkedHashMap<>();
        
        for (Map.Entry<String, Integer> entry : traits.entrySet()) {
            String traitName = entry.getKey();
            int value = entry.getValue();
            TraitDefinition def = traitDefinitions.get(traitName);
            
            if (def != null) {
                summation.put(def.category, summation.getOrDefault(def.category, 0) + value);
            }
        }
        
        return summation;
    }

    @Override
    public String toString() {
        return "Traits{" +
                "traits=" + traits +
                ", totalAP=" + calculateTotalActionPoints() +
                '}';
    }

}
