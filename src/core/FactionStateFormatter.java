package core;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FactionStateFormatter {

    public static void save(Faction faction, File file) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(faction.getName()).append("\n");
        sb.append("Population=").append(faction.getPopulation()).append("\n");
        sb.append("ActionPoints=").append(faction.getActionPoints()).append("\n");
        sb.append("\n");

        sb.append("Traits:\n");
        Traits traits = faction.getTraits();
        if (traits.getAllTraits().isEmpty()) {
            sb.append("  None\n");
        } else {
            for (Map.Entry<String, Integer> entry : traits.getAllTraits().entrySet()) {
                String traitName = entry.getKey();
                int value = entry.getValue();
                Traits.TraitDefinition def = traits.getTraitDefinition(traitName);
                
                if (def != null) {
                    sb.append("  ").append(def.displayName).append(": ").append(def.description).append(" [").append(traitName).append("=").append(value).append("]\n");
                } else {
                    sb.append("  ").append(traitName).append(": ").append(value).append("\n");
                }
            }
            
            sb.append("\n  Trait Summation:\n");
            Map<String, Integer> summation = traits.getTraitSummationByCategory();
            if (summation.isEmpty()) {
                sb.append("    None\n");
            } else {
                for (Map.Entry<String, Integer> entry : summation.entrySet()) {
                    String category = entry.getKey();
                    int total = entry.getValue();
                    String sign = total > 0 ? "+" : "";
                    sb.append("    ").append(category).append(": ").append(sign).append(total).append("\n");
                }
            }
        }
        sb.append("\n");

        sb.append("Resources:\n");
        Resources resources = faction.getResources();
        sb.append("  food=").append(resources.getFood()).append("\n");
        sb.append("  wood=").append(resources.getWood()).append("\n");
        sb.append("  stone=").append(resources.getStone()).append("\n");
        sb.append("\n");

        sb.append("Armies:\n");
        if (faction.getArmies().isEmpty()) {
            sb.append("  None\n");
        } else {
            for (Army army : faction.getArmies().values()) {
                sb.append("  ").append(army.getName()).append(": ")
                        .append(army.getPopulation()).append(" soldiers, ")
                        .append(army.getMight()).append(" might, ")
                        .append(army.getState()).append("\n");
            }
        }
        sb.append("\n");

        sb.append("Buildings:\n");
        if (!faction.getBuildings().isEmpty()) {
            Map<String, Integer> buildingCounts = new HashMap<>();
            for (Building building : faction.getBuildings().values()) {
                String type = building.getType();
                buildingCounts.put(type, buildingCounts.getOrDefault(type, 0) + 1);
            }
            StringBuilder buildingsLine = new StringBuilder();
            for (Map.Entry<String, Integer> entry : buildingCounts.entrySet()) {
                if (buildingsLine.length() > 0) {
                    buildingsLine.append(", ");
                }
                buildingsLine.append(entry.getKey()).append(" ").append(entry.getValue());
            }
            sb.append("  ").append(buildingsLine).append("\n");
        } else {
            sb.append("  None\n");
        }
        sb.append("\n");

        sb.append("Constructing:\n");
        if (faction.getBuildingQueue().isEmpty()) {
            sb.append("  None\n");
        } else {
            Map<String, Integer> constructingCounts = new HashMap<>();
            Map<String, Integer> weeksRemaining = new HashMap<>();
            for (Building building : faction.getBuildingQueue()) {
                String type = building.getType();
                constructingCounts.put(type, constructingCounts.getOrDefault(type, 0) + 1);
                weeksRemaining.put(type, building.getConstructionWeeksRemaining());
            }
            StringBuilder constructingLine = new StringBuilder();
            for (Map.Entry<String, Integer> entry : constructingCounts.entrySet()) {
                if (constructingLine.length() > 0) {
                    constructingLine.append(", ");
                }
                constructingLine.append(entry.getKey()).append(" ").append(entry.getValue())
                        .append(" (").append(weeksRemaining.get(entry.getKey())).append(" weeks)");
            }
            sb.append("  ").append(constructingLine).append("\n");
        }
        sb.append("\n");

        sb.append("Research:\n");
        Research research = faction.getResearch();
        if (research.getAllResults().isEmpty()) {
            sb.append("  None\n");
        } else {
            for (Map.Entry<String, ResearchResult> entry : research.getAllResults().entrySet()) {
                sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue().getDisplayName()).append("\n");
            }
        }
        sb.append("\n");

        sb.append("Features:\n");
        String features = faction.getFeatures();
        if (features.isEmpty()) {
            sb.append("  [Space for manual faction features and quirks]\n");
        } else {
            for (String line : features.split("\n")) {
                sb.append("  ").append(line).append("\n");
            }
        }

        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(sb.toString());
        }
    }

    public static void load(Faction faction, File file) throws IOException {
        if (!file.exists()) {
            return;
        }

        List<String> lines = Files.readAllLines(file.toPath());
        Map<String, String> values = new HashMap<>();
        StringBuilder featuresBuilder = new StringBuilder();
        String buildingsLine = "";
        String constructingLine = "";
        String traitsLine = "";
        boolean inFeaturesSection = false;
        boolean inBuildingsSection = false;
        boolean inConstructingSection = false;
        boolean inTraitsSection = false;

        for (String line : lines) {
            String trimmed = line.trim();
            
            if (trimmed.isEmpty() || trimmed.startsWith("[") || trimmed.startsWith("--")) {
                if (inFeaturesSection && !trimmed.isEmpty()) {
                    featuresBuilder.append(trimmed).append("\n");
                }
                continue;
            }

            if (trimmed.equals("Traits:")) {
                inTraitsSection = true;
                inBuildingsSection = false;
                inConstructingSection = false;
                inFeaturesSection = false;
                continue;
            }

            if (trimmed.equals("Buildings:")) {
                inTraitsSection = false;
                inBuildingsSection = true;
                inConstructingSection = false;
                inFeaturesSection = false;
                continue;
            }

            if (trimmed.equals("Constructing:")) {
                inTraitsSection = false;
                inBuildingsSection = false;
                inConstructingSection = true;
                inFeaturesSection = false;
                continue;
            }

            if (trimmed.equals("Features:")) {
                inTraitsSection = false;
                inBuildingsSection = false;
                inConstructingSection = false;
                inFeaturesSection = true;
                continue;
            }

            if (trimmed.matches("^[A-Za-z]+:$")) {
                inTraitsSection = false;
                inBuildingsSection = false;
                inConstructingSection = false;
                inFeaturesSection = false;
            }

            if (inTraitsSection) {
                if (!trimmed.equals("None")) {
                    if (!traitsLine.isEmpty()) {
                        traitsLine += ", ";
                    }
                    traitsLine += trimmed;
                }
            } else if (inBuildingsSection) {
                if (!trimmed.equals("None")) {
                    buildingsLine = trimmed;
                }
            } else if (inConstructingSection) {
                if (!trimmed.equals("None")) {
                    constructingLine = trimmed;
                }
            } else if (inFeaturesSection) {
                String content = trimmed.replaceFirst("^\\s*", "");
                if (!content.equals("[Space for manual faction features and quirks]")) {
                    featuresBuilder.append(content).append("\n");
                }
            } else if (trimmed.contains("=")) {
                String[] parts = trimmed.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    values.put(key.toLowerCase(), value);
                }
            }
        }

        if (values.containsKey("population")) {
            int pop = Integer.parseInt(values.get("population"));
            faction.population = pop;
        }

        if (values.containsKey("food")) {
            int food = Integer.parseInt(values.get("food"));
            faction.getResources().setFood(food);
        }

        if (values.containsKey("wood")) {
            int wood = Integer.parseInt(values.get("wood"));
            faction.getResources().setWood(wood);
        }

        if (values.containsKey("stone")) {
            int stone = Integer.parseInt(values.get("stone"));
            faction.getResources().setStone(stone);
        }

        loadTraits(faction, traitsLine);
        loadBuildings(faction, buildingsLine);
        loadConstructingBuildings(faction, constructingLine);

        String features = featuresBuilder.toString().trim();
        if (!features.isEmpty()) {
            faction.setFeatures(features);
        }
    }

    private static void loadBuildings(Faction faction, String buildingsLine) {
        if (buildingsLine.isEmpty() || buildingsLine.equals("None")) {
            return;
        }

        String[] entries = buildingsLine.split(",");
        for (String entry : entries) {
            String[] parts = entry.trim().split("\\s+");
            if (parts.length >= 2) {
                String buildingType = parts[0];
                int count = Integer.parseInt(parts[1]);
                
                BuildingDefinition def = BuildingDefinition.get(buildingType);
                if (def != null) {
                    for (int i = 0; i < count; i++) {
                        Building building = def.createCompleted(buildingType);
                        faction.getBuildings().put(building.getId(), building);
                    }
                }
            }
        }
    }

    private static void loadConstructingBuildings(Faction faction, String constructingLine) {
        if (constructingLine.isEmpty() || constructingLine.equals("None")) {
            return;
        }

        String[] entries = constructingLine.split(",(?![^()]*\\))");
        for (String entry : entries) {
            entry = entry.trim();
            String[] parts = entry.split("\\s+");
            if (parts.length >= 2) {
                String buildingType = parts[0];
                int count = Integer.parseInt(parts[1]);
                
                int weeksRemaining = 0;
                int openParen = entry.indexOf('(');
                int closeParen = entry.indexOf(')');
                if (openParen >= 0 && closeParen > openParen) {
                    String weeksPart = entry.substring(openParen + 1, closeParen).trim();
                    weeksPart = weeksPart.replaceAll("\\D+", "");
                    if (!weeksPart.isEmpty()) {
                        weeksRemaining = Integer.parseInt(weeksPart);
                    }
                }
                
                BuildingDefinition def = BuildingDefinition.get(buildingType);
                if (def != null) {
                    for (int i = 0; i < count; i++) {
                        Building building = def.createConstructing(weeksRemaining);
                        faction.getBuildingQueue().add(building);
                    }
                }
            }
        }
    }

    private static void loadTraits(Faction faction, String traitsLine) {
        if (traitsLine.isEmpty() || traitsLine.equals("None")) {
            return;
        }

        String[] entries = traitsLine.split(",");
        for (String entry : entries) {
            entry = entry.trim();
            
            if (entry.contains("Trait Summation")) {
                continue;
            }
            
            int bracketStart = entry.indexOf('[');
            int bracketEnd = entry.indexOf(']');
            
            if (bracketStart >= 0 && bracketEnd > bracketStart) {
                String internalData = entry.substring(bracketStart + 1, bracketEnd);
                String[] parts = internalData.split("=");
                if (parts.length == 2) {
                    String traitName = parts[0].trim();
                    String traitValue = parts[1].trim();
                    try {
                        int value = Integer.parseInt(traitValue);
                        faction.setTrait(traitName, value);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
    }

}
