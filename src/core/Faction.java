package core;

import java.io.*;
import java.util.Properties;

public abstract class Faction {

    protected String name;

    protected int population;
    protected int food;
    protected int actionPoints;
    protected int armiesPopulation;
    protected int might;

    protected Properties config = new Properties();
    protected Properties state = new Properties();

    protected File configFile;
    protected File stateFile;

    public Faction(String name, String configPath) {
        this.name = name;
        this.configFile = new File(configPath);
        this.stateFile = new File("generated/" + name.replace(" ", "_") + "_state.properties");
    }

    public void loadFactionData() throws IOException {
        config.load(new FileInputStream(configFile));

        population = Integer.parseInt(config.getProperty("population"));
        food = Integer.parseInt(config.getProperty("food"));
        armiesPopulation = Integer.parseInt(config.getProperty("armies_population"));
    }

    public void processWeek() {
        calculateActionPoints();
        consumeFood();
        calculateMight();
        applyPopulationGrowth();
    }

    protected void calculateActionPoints() {
        actionPoints = Math.max(3, population / 1000);
    }

    protected void consumeFood() {
        int weeklyConsumption = population / 2;
        food -= weeklyConsumption;

        if (food < population) {
            population = food;
        }
    }

    protected void applyPopulationGrowth() {
        population += population / 4;
    }

    protected void calculateMight() {
        might = armiesPopulation;
    }

    public void saveState() throws IOException {
        state.setProperty("population", String.valueOf(population));
        state.setProperty("food", String.valueOf(food));
        state.setProperty("action_points", String.valueOf(actionPoints));
        state.setProperty("armies_population", String.valueOf(armiesPopulation));
        state.setProperty("might", String.valueOf(might));

        stateFile.getParentFile().mkdirs();
        state.store(new FileOutputStream(stateFile), "Generated faction state");
    }
}
