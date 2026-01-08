package core;

public class Army {

    private String id;
    private String name;
    private int population;
    private int might;
    private int mightModifier;

    public enum ArmyState {
        DEFENDING,
        ATTACKING,
        RETREATING,
        IDLE
    }

    private ArmyState state;
    private String targetFaction;
    private int travelWeeksRemaining;

    public Army(String name, int population) {
        this.name = name;
        this.id = name.toLowerCase().replace(" ", "_");
        this.population = population;
        this.might = population;
        this.mightModifier = 0;
        this.state = ArmyState.DEFENDING;
        this.targetFaction = null;
        this.travelWeeksRemaining = 0;
    }

    public void reinforce(int amount) {
        this.population += amount;
        updateMight();
    }

    public void takeCasualties(int amount) {
        this.population = Math.max(0, this.population - amount);
        updateMight();
    }

    public void updateMight() {
        this.might = population + mightModifier;
    }

    public void setMightModifier(int modifier) {
        this.mightModifier = modifier;
        updateMight();
    }

    public int getCarryingCapacity() {
        return population / 2;
    }

    public void setState(ArmyState state) {
        this.state = state;
    }

    public void setTarget(String factionName, int weeksToTravel) {
        this.targetFaction = factionName;
        this.state = ArmyState.ATTACKING;
        this.travelWeeksRemaining = weeksToTravel;
    }

    public void advanceTravelTime() {
        if (travelWeeksRemaining > 0) {
            travelWeeksRemaining--;
        }
    }

    public boolean hasReachedTarget() {
        return travelWeeksRemaining == 0 && state == ArmyState.ATTACKING;
    }

    public void retreat() {
        this.state = ArmyState.RETREATING;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public int getMight() {
        return might;
    }

    public ArmyState getState() {
        return state;
    }

    public String getTargetFaction() {
        return targetFaction;
    }

    public int getTravelWeeksRemaining() {
        return travelWeeksRemaining;
    }

    public boolean isAlive() {
        return population > 0;
    }

    @Override
    public String toString() {
        return "Army{" +
                "name='" + name + '\'' +
                ", population=" + population +
                ", might=" + might +
                ", state=" + state +
                ", target='" + targetFaction + '\'' +
                ", weeksRemaining=" + travelWeeksRemaining +
                '}';
    }

}
