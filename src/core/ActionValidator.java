package core;

import java.util.Map;

public class ActionValidator {

    public static boolean canCreateArmy(Faction faction, int amount) {
        return amount > 0 && amount <= faction.getPopulation();
    }

    public static boolean canReinforceArmy(Faction faction, String armyId, int amount) {
        if (amount <= 0 || amount > faction.getPopulation()) {
            return false;
        }

        Army army = faction.getArmy(armyId);
        return army != null && army.isAlive();
    }

    public static boolean canQueueBuilding(Faction faction, String buildingType) {
        BuildingDefinition def = BuildingDefinition.get(buildingType);
        if (def == null) {
            return false;
        }

        if (faction.getBuildingQueue().size() >= faction.getMaxConcurrentBuildings()) {
            return false;
        }

        Map<String, Integer> cost = def.getConstructionCost();
        for (Map.Entry<String, Integer> entry : cost.entrySet()) {
            if (!faction.getResources().hasResources(entry.getKey(), entry.getValue())) {
                return false;
            }
        }

        int totalResourcesUsed = 0;
        for (Building building : faction.getBuildingQueue()) {
            totalResourcesUsed++;
        }

        return true;
    }

    public static boolean canUseActionPoints(Faction faction, int amount) {
        return faction.getUsedActionPoints() + amount <= faction.getActionPoints() && amount > 0;
    }

    public static boolean canAttack(Faction faction, String armyId, String targetFaction) {
        Army army = faction.getArmy(armyId);
        if (army == null || !army.isAlive()) {
            return false;
        }

        return true;
    }

    public static boolean canDemolishBuilding(Faction faction, String buildingId) {
        Building building = faction.getBuilding(buildingId);
        return building != null;
    }

}
