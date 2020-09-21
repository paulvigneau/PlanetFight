import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.io.Serializable;
import java.util.Random;


/**
 * Planet class
 */
public class Planet implements Serializable {

    /**
     * x coordinate
     */
    private double x;
    /**
     * y coordinate
     */
    private double y;
    /**
     * Planet's radius
     */
    private int radius;
    /**
     * Player's planet
     * Can be null
     */
    private transient Player player;
    /**
     * Number of ships of the planet
     */
    private int nbShips;

    /**
     * If a planet is sick or not
     */
    private boolean sick;

    /**
     * Universe
     */
    private transient Universe universe;

    /**
     * Text about the number of ships
     */
    private transient Text text;
    /**
     * Circle of the planet
     */
    private transient Circle circle;

    /**
     * Planet constructor
     * @param x x coordinate
     * @param y y coordinate
     * @param radius planet's radius
     * @param player Player
     * @param universe Universe
     * @param nbShips Number of ships
     */
    public Planet(double x, double y, int radius, boolean sick, Player player, Universe universe, int nbShips) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.sick = sick;
        this.nbShips = nbShips;
        this.universe = universe;

        circle = new Circle(x, y, radius);
        setPlayer(player);
    }

    /**
     * Planet constructor
     * @param planet Planet a copier
     * @param universe Univers de la planete
     */
    public Planet(Planet planet, Universe universe) {
        this(planet.getX(), planet.getY(), planet.getRadius(), planet.sickValue(), planet.getPlayer(), universe, planet.getNbShips());
    }

    /**
     * Centers the text
     * @param text Text to show
     */
    private void centerText(Text text) {
        double W = text.getBoundsInLocal().getWidth();
        double H = text.getBoundsInLocal().getHeight();
        text.relocate(x - W / 2, y - H / 2);
    }


    /**
     * Adds a ship
     */
    public void addShip(int power) {
        nbShips += power;
    }

    /**
     * Decreases a ship
     */
    public void decreaseShip(int power) {
        nbShips -= power;
    }

    /**
     * Creates a squadron
     * @param destinationPlanet Destination planet for the new ships
     * @return Squadron
     */
    public Squadron createShips(Planet destinationPlanet, int level) {
        if (nbShips == 0)
            return null;

        Squadron ships;
        ships = new Squadron((int) ((nbShips / Ship.getCostByLevel(level)) * player.getPercentShips()), level, this, destinationPlanet, universe);

        if (ships.shipList.size() > 0)
            nbShips -= ships.shipList.size() * Ship.getCostByLevel(level);

        return ships;
    }

    /**
     * Updates the rendering of the planet
     */
    public void render() {
        circle = new Circle(x, y, radius);
        circle.setFill(player != null ? player.getColor() : Color.LIGHTGREY);

        // Affichage du nombre de Ship en reserve
        text = new Text(String.valueOf(nbShips));
        text.setFont(new Font(20));
        text.setBoundsType(TextBoundsType.VISUAL);
        centerText(text);
    }

    /**
     * Changes planet's player
     * @param p New Player
     */
    public void setPlayer(Player p) {
        // Suppression de la planète dans la liste de l'ancien joueur
        if (player != null)
            player.removePlanet(this);

        player = p;
        circle.setFill(player != null ? player.getColor() : Color.LIGHTGREY);

        // Ajout de la planète à la liste du nouveau joueur
        if (player != null)
            player.addPlanet(this);
    }

    /**
     * Get x coordinate
     * @return x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Get y coordinate
     * @return y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Get radius
     * @return Radius
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Get player
     * @return Player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get number of ships
     * @return Number of ships
     */
    public int getNbShips() {
        return nbShips;
    }

    /**
     * Set number of ships
     * @param nbShips Number of ships
     */
    public void setNbShips(int nbShips) {
        this.nbShips = nbShips;
    }

    /**
     * Get text
     * @return Text
     */
    public Text getText() {
        return text;
    }

    /**
     * Get circle
     * @return Circle
     */
    public Circle getCircle() {
        return circle;
    }

    /**
     * Get sick value
     * @return Boolean
     */
    private boolean sickValue() {
        return sick;
    }

    /**
     * Get if planet can produce a ship a this time
     * @return Boolean
     */
    public boolean isSick() {
        if (sick) {
            Random rand = new Random();
            int r = rand.nextInt(2);
            return r == 1;
        }

        return false;
    }
}
