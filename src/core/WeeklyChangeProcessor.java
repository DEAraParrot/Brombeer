package core;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class WeeklyChangeProcessor {

    public static void apply(File weeklyFile, FactionRegistry registry) throws IOException {
        List<String> lines = Files.readAllLines(weeklyFile.toPath());
        Faction currentFaction = null;

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] parts = splitCommand(line);

            if (parts[0].equals("FACTION")) {
                String factionName = parts[1];
                currentFaction = registry.getFaction(factionName);
                if (currentFaction == null) {
                    System.err.println("Warning: Faction not found: " + factionName);
                }
                continue;
            }

            if (currentFaction == null) continue;

            try {
                switch (parts[0]) {
                    case "ARMY_CREATE":
                        handleArmyCreate(currentFaction, parts);
                        break;

                    case "ARMY_ATTACK":
                        handleArmyAttack(currentFaction, parts, registry);
                        break;

                    case "ARMY_PROTECT":
                        handleArmyProtect(currentFaction, parts, registry);
                        break;

                    case "ARMY_RETREAT":
                        handleArmyRetreat(currentFaction, parts);
                        break;

                    case "BUILDING_CONSTRUCT":
                        handleBuildingConstruct(currentFaction, parts);
                        break;

                    case "BUILDING_DEMOLISH":
                        handleBuildingDemolish(currentFaction, parts);
                        break;

                    case "BUILDING_POSTPONE":
                        handleBuildingPostpone(currentFaction, parts);
                        break;

                    case "RESEARCH":
                        handleResearch(currentFaction, parts);
                        break;

                    default:
                        System.err.println("Unknown command: " + parts[0]);
                }
            } catch (Exception e) {
                System.err.println("Error processing command: " + line + " - " + e.getMessage());
            }
        }
    }

    private static String[] splitCommand(String line) {
        return line.split(",\\s*|\\s+");
    }

    private static void handleArmyCreate(Faction faction, String[] parts) throws Exception {
        if (parts.length < 3) throw new Exception("ARMY_CREATE requires name and amount");
        String armyName = parts[1];
        int amount = Integer.parseInt(parts[2]);

        if (!ActionValidator.canCreateArmy(faction, amount)) {
            throw new Exception("Cannot create army: insufficient population");
        }

        faction.createArmy(armyName, amount);
    }

    private static void handleArmyAttack(Faction faction, String[] parts, FactionRegistry registry) throws Exception {
        if (parts.length < 3) throw new Exception("ARMY_ATTACK requires army name and target faction");
        String armyId = parts[1];
        String targetFaction = parts[2];

        Army army = faction.getArmy(armyId);
        if (army == null) throw new Exception("Army not found: " + armyId);
        
        if (!registry.hasFaction(targetFaction)) throw new Exception("Target faction not found: " + targetFaction);

        if (!ActionValidator.canAttack(faction, armyId, targetFaction)) {
            throw new Exception("Cannot attack with army: " + armyId + " (army must be alive)");
        }

        int distance = registry.getDistance(faction.getName(), targetFaction);
        if (distance < 0) throw new Exception("Distance to " + targetFaction + " not set");

        army.setTarget(targetFaction, distance);
    }

    private static void handleArmyProtect(Faction faction, String[] parts, FactionRegistry registry) throws Exception {
        if (parts.length < 3) throw new Exception("ARMY_PROTECT requires army name and target");
        String armyId = parts[1];
        String target = parts[2];

        Army army = faction.getArmy(armyId);
        if (army == null) throw new Exception("Army not found: " + armyId);

        if (registry.hasFaction(target)) {
            army.setState(Army.ArmyState.DEFENDING);
        } else {
            Army targetArmy = faction.getArmy(target);
            if (targetArmy == null) throw new Exception("Target not found: " + target);
            targetArmy.reinforce(army.getPopulation());
            faction.getArmies().remove(armyId);
        }
    }

    private static void handleArmyRetreat(Faction faction, String[] parts) throws Exception {
        if (parts.length < 2) throw new Exception("ARMY_RETREAT requires army name");
        String armyId = parts[1];

        Army army = faction.getArmy(armyId);
        if (army == null) throw new Exception("Army not found: " + armyId);
        army.retreat();
    }

    private static void handleBuildingConstruct(Faction faction, String[] parts) throws Exception {
        if (parts.length < 2) throw new Exception("BUILDING_CONSTRUCT requires building name");
        String buildingName = parts[1];

        if (!ActionValidator.canQueueBuilding(faction, buildingName)) {
            throw new Exception("Cannot queue building: insufficient resources or already constructing");
        }

        faction.queueBuilding(buildingName);
    }

    private static void handleBuildingDemolish(Faction faction, String[] parts) throws Exception {
        if (parts.length < 2) throw new Exception("BUILDING_DEMOLISH requires building name");
        String buildingName = parts[1];

        if (!ActionValidator.canDemolishBuilding(faction, buildingName)) {
            throw new Exception("Cannot demolish building: building not found or not complete");
        }

        faction.demolishBuilding(buildingName);
    }

    private static void handleBuildingPostpone(Faction faction, String[] parts) throws Exception {
        if (parts.length < 2) throw new Exception("BUILDING_POSTPONE requires building ID");
        String buildingId = parts[1];

        faction.postponeBuilding(buildingId);
    }

    private static void handleResearch(Faction faction, String[] parts) throws Exception {
        if (parts.length < 2) throw new Exception("RESEARCH requires field name");
        String field = parts[1];
        int investedAP = parts.length > 2 ? Integer.parseInt(parts[2]) : 1;
        faction.applyResearch(field, investedAP);
    }
}
