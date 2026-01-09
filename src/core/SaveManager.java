package core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveManager {

    private static final String SAVED_DIR = "saved";
    private static final String BACKUP_DIR = "backups";
    private static final Pattern WEEK_PATTERN = Pattern.compile(".*\\.week_(\\d+)");

    public static int getCurrentWeek() {
        File savedDir = new File(SAVED_DIR);
        if (!savedDir.exists() || !savedDir.isDirectory()) {
            return 0;
        }

        File[] stateFiles = savedDir.listFiles((dir, name) -> name.matches(".*\\.week_\\d+"));
        if (stateFiles == null || stateFiles.length == 0) {
            return 0;
        }

        int maxWeek = 0;
        for (File file : stateFiles) {
            Matcher matcher = WEEK_PATTERN.matcher(file.getName());
            if (matcher.matches()) {
                int week = Integer.parseInt(matcher.group(1));
                maxWeek = Math.max(maxWeek, week);
            }
        }

        return maxWeek;
    }

    public static void loadGameState(FactionRegistry registry, int week) throws IOException {
        File savedDir = new File(SAVED_DIR);
        if (!savedDir.exists()) {
            throw new FileNotFoundException("No saved games found");
        }

        for (Faction faction : registry.getAllFactions().values()) {
            String filename = faction.getName() + ".week_" + week;
            File stateFile = new File(savedDir, filename);

            if (stateFile.exists()) {
                FactionStateFormatter.load(faction, stateFile);
            } else {
                throw new FileNotFoundException("State file not found: " + filename);
            }
        }
    }

    public static void saveGameState(FactionRegistry registry, int week) throws IOException {
        archiveCurrentWeek(week - 1);

        File savedDir = new File(SAVED_DIR);
        Files.createDirectories(savedDir.toPath());

        for (Faction faction : registry.getAllFactions().values()) {
            String filename = faction.getName() + ".week_" + week;
            File stateFile = new File(savedDir, filename);
            FactionStateFormatter.save(faction, stateFile);
        }
    }

    private static void archiveCurrentWeek(int weekToArchive) throws IOException {
        if (weekToArchive <= 0) return;

        File savedDir = new File(SAVED_DIR);
        File backupDir = new File(BACKUP_DIR);

        if (!savedDir.exists()) return;

        Files.createDirectories(backupDir.toPath());

        File[] filesToArchive = savedDir.listFiles((dir, name) -> name.matches(".*\\.week_" + weekToArchive));
        if (filesToArchive == null) return;

        for (File file : filesToArchive) {
            String originalName = file.getName();
            String archivedName = "backup." + originalName;
            Path destination = Paths.get(BACKUP_DIR, archivedName);

            Files.move(file.toPath(), destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        cleanupOldBackups(weekToArchive);
    }

    private static void cleanupOldBackups(int currentWeek) throws IOException {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists() || !backupDir.isDirectory()) {
            return;
        }

        final int WEEKS_TO_KEEP = 4;
        long currentTimeMillis = System.currentTimeMillis();

        File[] backupFiles = backupDir.listFiles((dir, name) -> name.matches("backup\\..*\\.week_\\d+"));
        if (backupFiles == null) {
            return;
        }

        for (File file : backupFiles) {
            try {
                FileTime fileTime = Files.getLastModifiedTime(file.toPath());
                long fileAgeMillis = currentTimeMillis - fileTime.toMillis();
                long weeksInMillis = WEEKS_TO_KEEP * 7L * 24L * 60L * 60L * 1000L;

                if (fileAgeMillis > weeksInMillis) {
                    Files.delete(file.toPath());
                }
            } catch (IOException e) {
                System.err.println("Failed to delete old backup: " + file.getName());
            }
        }
    }

    public static void deleteAllSaves() throws IOException {
        File savedDir = new File(SAVED_DIR);
        if (!savedDir.exists() || !savedDir.isDirectory()) {
            return;
        }

        File[] files = savedDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().matches(".*\\.week_\\d+")) {
                    if (!file.delete()) {
                        throw new IOException("Failed to delete: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    public static List<Integer> listAvailableWeeks() {
        List<Integer> weeks = new ArrayList<>();
        File backupDir = new File(BACKUP_DIR);

        if (backupDir.exists() && backupDir.isDirectory()) {
            File[] files = backupDir.listFiles((dir, name) -> name.matches("backup\\..*\\.week_\\d+"));
            if (files != null) {
                Set<Integer> weekSet = new TreeSet<>();
                for (File file : files) {
                    Matcher matcher = Pattern.compile("backup\\..*\\.week_(\\d+)").matcher(file.getName());
                    if (matcher.matches()) {
                        weekSet.add(Integer.parseInt(matcher.group(1)));
                    }
                }
                weeks.addAll(weekSet);
            }
        }

        return weeks;
    }

    public static void restoreWeek(FactionRegistry registry, int week) throws IOException {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            throw new FileNotFoundException("Backup directory not found");
        }

        File[] backupFiles = backupDir.listFiles((dir, name) -> name.matches("backup\\..*\\.week_" + week));
        if (backupFiles == null || backupFiles.length == 0) {
            throw new FileNotFoundException("No backups found for week " + week);
        }

        File savedDir = new File(SAVED_DIR);
        Files.createDirectories(savedDir.toPath());

        for (File backupFile : backupFiles) {
            String restoredName = backupFile.getName().substring(7);
            File destination = new File(savedDir, restoredName);
            Files.copy(backupFile.toPath(), destination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
