package core;

import java.util.HashMap;
import java.util.Map;

public class Research {

    private Map<String, Integer> progress;
    private Map<String, ResearchResult> results;

    public Research() {
        this.progress = new HashMap<>();
        this.results = new HashMap<>();
    }

    public void addProgress(String field, int amount) {
        progress.put(field, progress.getOrDefault(field, 0) + amount);
    }

    public int getProgress(String field) {
        return progress.getOrDefault(field, 0);
    }

    public void recordResult(String field, ResearchResult result) {
        results.put(field, result);
    }

    public ResearchResult getLastResult(String field) {
        return results.get(field);
    }

    public Map<String, Integer> getAllProgress() {
        return new HashMap<>(progress);
    }

    public Map<String, ResearchResult> getAllResults() {
        return new HashMap<>(results);
    }

    @Override
    public String toString() {
        return "Research{" +
                "progress=" + progress +
                ", results=" + results +
                '}';
    }

}
