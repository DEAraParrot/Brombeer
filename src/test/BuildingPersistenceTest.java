package test;

import core.*;
import factions.Dwarfs;

import java.io.File;
import java.io.IOException;

public class BuildingPersistenceTest {
    public static void main(String[] args) throws IOException {
        System.out.println("=== Building Persistence Test ===\n");

        Faction dwarfs = new Dwarfs("dwarfs");
        dwarfs.loadFactionData();

        System.out.println("Initial state:");
        System.out.println("Buildings: " + dwarfs.getBuildings().size());
        System.out.println("Building Queue: " + dwarfs.getBuildingQueue().size());

        BuildingDefinition farmDef = BuildingDefinition.get("Farm");
        BuildingDefinition quarryDef = BuildingDefinition.get("Quarry");

        if (farmDef != null) {
            Building farm1 = farmDef.createCompleted("Farm");
            Building farm2 = farmDef.createCompleted("Farm");
            dwarfs.getBuildings().put(farm1.getId(), farm1);
            dwarfs.getBuildings().put(farm2.getId(), farm2);
            System.out.println("Added 2 completed farms");
        }

        System.out.println("\nAfter adding buildings:");
        System.out.println("Completed Buildings: " + dwarfs.getBuildings().size());
        for (Building b : dwarfs.getBuildings().values()) {
            System.out.println("  - " + b.getType());
        }

        System.out.println("Constructing Buildings: " + dwarfs.getBuildingQueue().size());
        for (ConstructingBuilding b : dwarfs.getBuildingQueue()) {
            System.out.println("  - " + b.getType() + " (" + b.getConstructionWeeksRemaining() + " weeks)");
        }

        File saveFile = new File("saved/test_dwarfs.week_0");
        System.out.println("\nSaving to: " + saveFile.getAbsolutePath());
        FactionStateFormatter.save(dwarfs, saveFile);

        System.out.println("\nSaved file content:");
        java.nio.file.Files.lines(saveFile.toPath()).forEach(System.out::println);

        Faction dwarfs2 = new Dwarfs("dwarfs");
        FactionStateFormatter.load(dwarfs2, saveFile);

        System.out.println("\n=== After Loading ===");
        System.out.println("Completed Buildings: " + dwarfs2.getBuildings().size());
        for (Building b : dwarfs2.getBuildings().values()) {
            System.out.println("  - " + b.getType() + " (ID: " + b.getId() + ")");
        }

        System.out.println("Constructing Buildings: " + dwarfs2.getBuildingQueue().size());
        for (ConstructingBuilding b : dwarfs2.getBuildingQueue()) {
            System.out.println("  - " + b.getType() + " (" + b.getConstructionWeeksRemaining() + " weeks)");
        }

        if (dwarfs2.getBuildings().size() == dwarfs.getBuildings().size() &&
            dwarfs2.getBuildingQueue().size() == dwarfs.getBuildingQueue().size()) {
            System.out.println("\n✓ Test PASSED: Buildings persisted correctly");
        } else {
            System.out.println("\n✗ Test FAILED: Building count mismatch");
        }

        saveFile.delete();
    }
}
