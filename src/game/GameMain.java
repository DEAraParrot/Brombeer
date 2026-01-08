package game;

import core.Faction;
import factions.*;

import java.io.IOException;

public class GameMain {

    public static void main(String[] args) throws IOException {
        Faction dwarfs = new Dwarfs("Dwarfs");
        Faction humans = new Humans("Humans");
        Faction ogres = new Ogres("Ogres");

        dwarfs.loadFactionData();
        humans.loadFactionData();
        ogres.loadFactionData();

        dwarfs.processWeek();
        humans.processWeek();
        ogres.processWeek();

        dwarfs.saveState();
        humans.saveState();
        ogres.saveState();
    }
}