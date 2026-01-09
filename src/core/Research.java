package core;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Research {

    private Map<String, Integer> progress;
    private Map<String, ResearchResult> results;
    private static final Random random = new Random();
    private static final int BASE_SUCCESS_CHANCE = 50;
    private static final int BASE_BREAKTHROUGH_CHANCE = 5;
    private static final int ADDITIVE_MODIFIER = 1;
    private static final int MULTIPLICATIVE_MODIFIER = 1;

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

    public ResearchResult calculateOutcome(int baseChance, int additiveModifier, int multiplicativeModifier) {
        int successChance = baseChance + (additiveModifier * multiplicativeModifier);
        successChance = Math.max(0, Math.min(100, successChance));

        int roll = random.nextInt(100) + 1;

        if (roll > successChance) {
            return ResearchResult.FAILURE;
        }

        int breakthroughChance = BASE_BREAKTHROUGH_CHANCE + (additiveModifier * multiplicativeModifier);
        breakthroughChance = Math.max(0, Math.min(100, breakthroughChance));

        int breakthroughRoll = random.nextInt(100) + 1;

        if (breakthroughRoll <= breakthroughChance) {
            return ResearchResult.BREAKTHROUGH;
        }

        return ResearchResult.DISCOVERY;
    }

    public ResearchResult calculateOutcome(String field) {
        return calculateOutcome(BASE_SUCCESS_CHANCE, ADDITIVE_MODIFIER, MULTIPLICATIVE_MODIFIER);
    }

    public ResearchResult calculateOutcome(String field, int investedAP) {
        int additiveBonus = investedAP;
        return calculateOutcome(BASE_SUCCESS_CHANCE, additiveBonus, MULTIPLICATIVE_MODIFIER);
    }

    @Override
    public String toString() {
        return "Research{" +
                "progress=" + progress +
                ", results=" + results +
                '}';
    }

}
