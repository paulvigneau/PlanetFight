import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Player class
 */
public class Player implements Serializable {

    /**
     * Percent ship to send
     */
    private transient double percentShips;
    /**
     * Player's color
     */
    private transient Color color;
    /**
     * Player's index
     */
    private int index;

    /**
     * Selected squadron index
     */
    private transient int squadronSelectedIndex;

    /**
     * Planets list (only those of the player)
     */
    private transient ArrayList<Planet> planetList;
    /**
     * Squadrons list (only those of the player)
     */
    private transient ArrayList<Squadron> squadronList;

    /**
     * Universe
     */
    private transient Universe universe;

    /**
     * Player constructor
     * @param index Index
     * @param universe Universe
     */
    public Player(int index, Universe universe) {
        this.percentShips = 0.5;
        this.index = index;
        this.squadronSelectedIndex = -1; // == aucun esquadron de sélectionné
        this.universe = universe;

        planetList = new ArrayList<>();
        squadronList = new ArrayList<>();

        initColor();
    }

    /**
     * Player constructor
     * @param index Index
     * @param color Color
     * @param universe Universe
     */
    public Player(int index, Color color, Universe universe) {
        this.percentShips = 0.5;
        this.index = index;
        this.squadronSelectedIndex = -1;
        this.universe = universe;
        this.color = Color.color(color.getRed(), color.getGreen(), color.getBlue());

        planetList = new ArrayList<>();
        squadronList = new ArrayList<>();
    }

    /**
     * Initializes the player's color
     */
    private void initColor() {
        if (index == 1)
            color = Color.BLUE;
        else if (index == 2)
            color = Color.RED;
        else {
            // Couleurs vives générées aléatoirement
            Random random = new Random();
            final int hue = random.nextInt(360);
            final float saturation = 0.9f;
            final float luminance = 1.0f;
            color = Color.hsb(hue, saturation, luminance);
        }
    }

    /**
     * Artificial Intelligence
     * (Only if the player is played by the computer)
     */
    public void AI_Play() {
        // Si aucune planete
        if (planetList.size() == 0)
            return;

        // S'il ne reste que ce joueur encore en jeu, on ne joue pas
        if (universe.endGame())
            return;

        Random rand = new Random();

        int source = rand.nextInt(planetList.size());
        int decision = rand.nextInt(4);

        Planet planetSrc = planetList.get(source);
        Planet planetDest = null;

        Planet p;
        switch (decision){
            case 0 :
                p = lowerPlanet();
                if (p != null) {
                    planetDest = p;
                    break;
                }
            case 1 :
                p = conquerableOpponentPlanet(planetSrc);
                if (p != null) {
                    planetDest = p;
                    decision = 1;
                    break;
                }
            case 2:
                p = conquerableNeutralPlanet(planetSrc);
                if (p != null) {
                    planetDest = p;
                    decision = 2;
                    break;
                }
            case 3:
                planetDest = randomPlanet(planetSrc);
                decision = 3;
                break;
            default:
                break;
        }

        if (planetSrc.getNbShips() > 20 && (decision == 0 || decision == 3)) {
            universe.squadronList.add(planetSrc.createShips(planetDest, 1));
        }
        else if (decision == 1 || decision == 2) {
            setPercentShips((double) (planetDest.getNbShips() + 10) / (double) planetSrc.getNbShips());

            universe.squadronList.add(planetSrc.createShips(planetDest, 1));

            this.percentShips = 0.5;
        }
    }

    /**
     * Choose one random planet to attack (for the AI)
     * @param PlanetSrc Source planet
     * @return Planet
     */
    private Planet randomPlanet(Planet PlanetSrc){
        Random rand = new Random();
        int dest;
        Planet planetDest;

        do {
            dest = rand.nextInt(universe.planetList.size());
            planetDest = universe.planetList.get(dest);
        } while (planetDest == PlanetSrc);

        return planetDest;
    }

    /**
     * Choose the planet which have the lower number of ships (for the AI)
     * @return Planet
     */
    private Planet lowerPlanet() {
        int min = Integer.MAX_VALUE;
        Planet planetDest = null;

        for (Planet p : universe.planetList) {
            int nbShips = p.getNbShips();

            if (nbShips <= min) {
                min = nbShips;
                planetDest = p;
            }
        }

        return planetDest;
    }

    /**
     * Choose one planet that can be conquered from all opponent planets
     * @param source Source Planet
     * @return Planet
     */
    private Planet conquerableOpponentPlanet(Planet source){
        Planet planetDest = null;

        for (Planet p : universe.planetList) {
            int nbShips = p.getNbShips();

            if(nbShips < source.getNbShips() && source.getPlayer() != p.getPlayer() && p.getPlayer() != null){
                planetDest = p;
                break;
            }
        }

        return planetDest;
    }

    /**
     * Choose one planet that can be conquered from all white planets
     * @param source Source Planet
     * @return Planet
     */
    private Planet conquerableNeutralPlanet(Planet source){
        Planet PlanetDest = null;

        for (Planet p : universe.planetList) {
            int nbShips = p.getNbShips();

            if(nbShips < source.getNbShips() && p.getPlayer() == null){
                PlanetDest = p;
                break;
            }
        }

        return PlanetDest;
    }

    /**
     * Add a new planet to the planets list
     * @param planet New planet
     */
    public void addPlanet(Planet planet) {
        planetList.add(planet);
    }

    /**
     * Remove a planet to the planets list
     * @param planet Planet to remove
     */
    public void removePlanet(Planet planet) {
        planetList.remove(planet);
    }

    /**
     * Get planets list
     * @return Planet list
     */
    public ArrayList<Planet> getPlanetList() {
        return planetList;
    }

    /**
     * Set the percent ship value
     * @param percentShips Number between 0 to 1
     */
    public void setPercentShips(double percentShips) {
        if (percentShips > 1)
            this.percentShips = 1;

        else if (percentShips < 0)
            this.percentShips = 0;

        else if (percentShips >= 0 && percentShips <= 1)
            this.percentShips = percentShips;
    }

    /**
     * Get the percent ship value
     * @return Percent number
     */
    public double getPercentShips() {
        return percentShips;
    }

    /**
     * Get the next squadron to select
     * @return Squadron
     */
    public Squadron getNextSquadron() {
        if (squadronList.size() >0){
            squadronSelectedIndex = (squadronSelectedIndex + 1) % squadronList.size();
            return squadronList.get(squadronSelectedIndex);
        }
        return null;
    }

    /**
     * Add a new squadron to the squadrons list
     * @param squadron New squadron
     */
    public void addSquadron(Squadron squadron) {
        squadronList.add(squadron);
    }

    /**
     * Remove a squadron to the squadrons list
     * @param squadron Squadron to remove
     */
    public void removeSquadron(Squadron squadron){
        squadronList.remove(squadron);
    }

    /**
     * Get color
     * @return Color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Get index
     * @return Index
     */
    public int getIndex() {
        return index;
    }
}
