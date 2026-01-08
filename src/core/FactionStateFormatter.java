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
            for (ConstructingBuilding building : faction.getBuildingQueue()) {
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
        boolean inFeaturesSection = false;
        boolean inBuildingsSection = false;
        boolean inConstructingSection = false;

        for (String line : lines) {
            String trimmed = line.trim();
            
            if (trimmed.isEmpty() || trimmed.startsWith("[") || trimmed.startsWith("--")) {
                if (inFeaturesSection && !trimmed.isEmpty()) {
                    featuresBuilder.append(trimmed).append("\n");
                }
                continue;
            }

            if (trimmed.equals("Buildings:")) {
                inBuildingsSection = true;
                inConstructingSection = false;
                inFeaturesSection = false;
                continue;
            }

            if (trimmed.equals("Constructing:")) {
                inBuildingsSection = false;
                inConstructingSection = true;
                inFeaturesSection = false;
                continue;
            }

            if (trimmed.equals("Features:")) {
                inBuildingsSection = false;
                inConstructingSection = false;
                inFeaturesSection = true;
                continue;
            }

            if (trimmed.matches("^[A-Za-z]+:$")) {
                inBuildingsSection = false;
                inConstructingSection = false;
                inFeaturesSection = false;
            }

            if (inBuildingsSection) {
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

        System.out.println("DEBUG: loadConstructingBuildings - line: '" + constructingLine + "'");
        System.out.println("DEBUG: faction.getBuildingQueue() ref: " + System.identityHashCode(faction.getBuildingQueue()));
        String[] entries = constructingLine.split(",(?![^()]*\\))");
        System.out.println("DEBUG: entries.length = " + entries.length);
        for (String entry : entries) {
            entry = entry.trim();
            System.out.println("DEBUG: Processing entry: '" + entry + "'");
            String[] parts = entry.split("\\s+");
            System.out.println("DEBUG: parts.length = " + parts.length);
            if (parts.length >= 2) {
                String buildingType = parts[0];
                int count = Integer.parseInt(parts[1]);
                System.out.println("DEBUG: buildingType=" + buildingType + ", count=" + count);
                
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
                System.out.println("DEBUG: weeksRemaining=" + weeksRemaining);
                
                BuildingDefinition def = BuildingDefinition.get(buildingType);
                System.out.println("DEBUG: def=" + def);
                if (def != null) {
                    for (int i = 0; i < count; i++) {
                        ConstructingBuilding building = def.createConstructing(weeksRemaining);
                        List<ConstructingBuilding> queue = faction.getBuildingQueue();
                        queue.add(building);
                        System.out.println("DEBUG: After add, queue size: " + queue.size());
                        System.out.println("DEBUG: faction.getBuildingQueue() size after add: " + faction.getBuildingQueue().size());
                    }
                }
            }
        }
        System.out.println("DEBUG: End of loadConstructingBuildings, final queue size: " + faction.getBuildingQueue().size());
    }

}
