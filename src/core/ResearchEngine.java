package core;

import java.util.Random;

public class ResearchEngine {

    private static final Random random = new Random();

    private static final int BASE_SUCCESS_CHANCE = 50;
    private static final int BASE_BREAKTHROUGH_CHANCE = 5;
    private static final int ADDITIVE_MODIFIER = 1;
    private static final int MULTIPLICATIVE_MODIFIER = 1;

    public static ResearchResult calculateOutcome(int baseChance, int additiveModifier, int multiplicativeModifier) {
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

    public static ResearchResult calculateOutcome(String field) {
        return calculateOutcome(BASE_SUCCESS_CHANCE, ADDITIVE_MODIFIER, MULTIPLICATIVE_MODIFIER);
    }

    public static ResearchResult calculateOutcome(String field, int investedAP) {
        int additiveBonus = investedAP;
        return calculateOutcome(BASE_SUCCESS_CHANCE, additiveBonus, MULTIPLICATIVE_MODIFIER);
    }

}
