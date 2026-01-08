package game;

import core.Faction;
import core.FactionRegistry;
import core.SaveManager;
import core.WeeklyChangeProcessor;
import core.WeeklyChangesLoader;
import factions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class GameMain {

    private static int weekNumber = 0;

    public static void main(String[] args) throws IOException {

        FactionRegistry registry = new FactionRegistry();

        Faction dwarfs = new Dwarfs("dwarfs");
        Faction humans = new Humans("humans");
        Faction ogres = new Ogres("ogres");

        registry.registerFaction("dwarfs", dwarfs);
        registry.registerFaction("humans", humans);
        registry.registerFaction("ogres", ogres);

        setupFactionDistances(registry);

        weekNumber = SaveManager.getCurrentWeek();

        try {
            if (weekNumber > 0) {
                SaveManager.loadGameState(registry, weekNumber);
                System.out.println("Loaded game from Week " + weekNumber);
            } else {
                for (Faction faction : registry.getAllFactions().values()) {
                    faction.loadFactionData();
                }
                System.out.println("Loaded game from config (Week 0 - New Game)");
            }
        } catch (IOException e) {
            System.out.println("Error loading game: " + e.getMessage());
            System.out.println("Initializing factions from config...");
            try {
                for (Faction faction : registry.getAllFactions().values()) {
                    faction.loadFactionData();
                }
            } catch (IOException e2) {
                System.out.println("Critical error: Cannot load faction data. " + e2.getMessage());
                System.exit(1);
            }
        }

        Scanner scanner = new Scanner(System.in);
        printMenu();

        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "weekend":
                    weekNumber++;
                    resolveWeek(registry);
                    SaveManager.saveGameState(registry, weekNumber);
                    System.out.println("Week " + weekNumber + " complete. Game saved.\n");
                    break;

                case "newgame":
                    if (confirmAction("Are you sure? This will delete all saved games.")) {
                        SaveManager.deleteAllSaves();
                        weekNumber = 0;
                        for (Faction faction : registry.getAllFactions().values()) {
                            faction.loadFactionData();
                        }
                        System.out.println("New game started at Week 0.\n");
                    }
                    break;

                case "status":
                    printFactionStatus(registry, weekNumber);
                    break;

                case "backups":
                    listBackupWeeks();
                    break;

                case "restore":
                    System.out.print("Enter week number to restore: ");
                    try {
                        int week = Integer.parseInt(scanner.nextLine().trim());
                        if (confirmAction("Restore Week " + week + "? Current saves will be overwritten.")) {
                            SaveManager.restoreWeek(registry, week);
                            weekNumber = week;
                            SaveManager.loadGameState(registry, weekNumber);
                            System.out.println("Week " + week + " restored successfully.\n");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid week number.\n");
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage() + "\n");
                    }
                    break;

                case "help":
                    printMenu();
                    break;

                case "exit":
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Unknown command. Type 'help' for options.\n");
            }
        }
    }

    private static void resolveWeek(FactionRegistry registry) {
        File weekly = WeeklyChangesLoader.getLatestWeeklyFile();
        if (weekly != null) {
            System.out.println("Processing: " + weekly.getName());
            try {
                WeeklyChangeProcessor.apply(weekly, registry);
            } catch (Exception e) {
                System.err.println("Error processing weekly changes: " + e.getMessage());
            }
        } else {
            System.out.println("No weekly changes file found.");
        }

        for (Faction faction : registry.getAllFactions().values()) {
            faction.processWeek();
        }

        System.out.println("Game state calculated.");
    }

    private static void printFactionStatus(FactionRegistry registry, int week) {
        System.out.println("\n========== Week " + week + " Status ==========");
        for (Faction faction : registry.getAllFactions().values()) {
            System.out.println("\n" + faction.getName());
            System.out.println("  Population: " + faction.getPopulation());
            System.out.println("  Action Points: " + faction.getActionPoints());
            System.out.println("  Resources: " + faction.getResources());
            System.out.println("  Armies: " + faction.getArmies().size());
            System.out.println("  Buildings: " + faction.getBuildings().size());
            System.out.println("  Might: " + faction.getMight());
        }
        System.out.println();
    }

    private static void listBackupWeeks() {
        List<Integer> weeks = SaveManager.listAvailableWeeks();
        if (weeks.isEmpty()) {
            System.out.println("No backups available.\n");
        } else {
            System.out.println("Available backup weeks:");
            for (int week : weeks) {
                System.out.println("  - Week " + week);
            }
            System.out.println();
        }
    }

    private static boolean confirmAction(String message) {
        System.out.print(message + " (yes/no): ");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("yes") || response.equals("y");
    }

    private static void printMenu() {
        System.out.println("\n========== GAME COMMANDS ==========");
        System.out.println("weekend      - Resolve the current week and save");
        System.out.println("status       - View all faction statuses");
        System.out.println("backups      - List available backup weeks");
        System.out.println("restore      - Restore from a previous week");
        System.out.println("newgame      - Start a new game (deletes saves)");
        System.out.println("help         - Show this menu");
        System.out.println("exit         - Exit the game");
        System.out.println("====================================\n");
    }

    private static void setupFactionDistances(FactionRegistry registry) {
        registry.setDistance("dwarfs", "humans", 2);
        registry.setDistance("dwarfs", "ogres", 3);
        registry.setDistance("humans", "ogres", 2);
    }

}
