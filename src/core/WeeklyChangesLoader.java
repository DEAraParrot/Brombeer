package core;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class WeeklyChangesLoader {

    public static File getLatestWeeklyFile() {
        File dir = new File("weeklychanges");
        if (!dir.exists() || !dir.isDirectory()) return null;

        File[] files = dir.listFiles((d, name) -> name.endsWith(".week"));
        if (files == null || files.length == 0) return null;

        return Arrays.stream(files)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
    }
}
