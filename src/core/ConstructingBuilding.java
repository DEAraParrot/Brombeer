package core;

public class ConstructingBuilding extends BuildingBase {

    private int constructionWeeksRemaining;
    private int weeksSinceLastProgress;
    private static final int MAX_DORMANT_WEEKS = 3;

    public ConstructingBuilding(String name, String type, int constructionWeeks) {
        super(name, type);
        this.constructionWeeksRemaining = constructionWeeks;
        this.weeksSinceLastProgress = 0;
    }

    public void addConstruction(int weeksCompleted) {
        if (constructionWeeksRemaining > 0) {
            constructionWeeksRemaining -= weeksCompleted;
            weeksSinceLastProgress = 0;
        }
    }

    public void addDormantWeek() {
        weeksSinceLastProgress++;
    }

    public boolean isConstructionFailed() {
        return weeksSinceLastProgress > MAX_DORMANT_WEEKS;
    }

    public boolean isComplete() {
        return constructionWeeksRemaining <= 0;
    }

    public int getConstructionWeeksRemaining() {
        return constructionWeeksRemaining;
    }

    public int getWeeksSinceLastProgress() {
        return weeksSinceLastProgress;
    }

    @Override
    public String toString() {
        return "ConstructingBuilding{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", weeksRemaining=" + constructionWeeksRemaining +
                '}';
    }

}
