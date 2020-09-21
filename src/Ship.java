import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 * Ship class
 */
public class Ship {

    /**
     * Shape of the ship
     */
    private Shape shape;
    /**
     * x coordinate
     */
    private double x;
    /**
     * y coordinate
     */
    private double y;
    /**
     * Source planet of the ship
     */
    private Planet myPlanet;
    /**
     * Angle of the ship (0-359)
     */
    private double angle;
    /**
     * Ship's width
     */
    private int width;
    /**
     * Ship's height
     */
    private int height;
    /**
     * Ship's level
     */
    private int level;
    /**
     * Ship's speed
     */
    private int speed;
    /**
     * Ship's power
     */
    private int power;
    /**
     * Ship's cost
     */
    private int cost;
    /**
     * Ship's player
     */
    private Player player;

    // Nécessaire pour le déplacement des Ships par vagues
    /**
     * Time before ship start
     */
    private int offsetTime;
    /**
     * Time reference for offsetTime
     */
    private int offsetTimeRef;
    /**
     * Indicates if the ship is gone
     */
    private boolean started;

    // Nécessaire au deplacement du Ship
    /**
     * Indicates if destination has changed
     */
    private boolean newDestination;
    /**
     * Destination planet of the ship
     */
    private Planet destination;
    /**
     * Distance between the ship and his destination (x axis)
     */
    private double destWidth;
    /**
     * Distance between the ship and his destination (y axis)
     */
    private double destHeight;
    /**
     * x ratio
     */
    private double ratioX;
    /**
     * y ratio
     */
    private double ratioY;

    /**
     * Number of ships for the squadron
     */
    private int nbShipsSquadron;
    /**
     * Processed ship number
     * (useful during the init phase)
     */
    private static int actualShipSquadron;

    // Nécessaire pour le changement de destination
    /**
     * Indicates if ship is selected
     */
    private boolean selected;
    /**
     * Indicates if selected value has recently been changed
     */
    private boolean changeSelected;

    /**
     * Universe
     */
    private Universe universe;

    /**
     * Powers' table
     */
    private final static int [] POWERS = {1, 3, 2};
    /**
     * Speeds' table
     */
    private final static int [] SPEEDS = {3, 1, 4};
    /**
     * Costs' table
     */
    private final static int [] COSTS = {1, 2, 2};

    /**
     * Level 1 ship width
     */
    private final static int WIDTH_LVL_1 = 25;
    /**
     * Level 1 ship height
     */
    private final static int HEIGHT_LVL_1 = 25;
    /**
     * Level 2 ship size
     */
    private final static int SIZE_LVL_2 = 25;
    /**
     * Level 3 ship width
     */
    private final static int WIDTH_LVL_3 = 15;
    /**
     * Level 3 ship height
     */
    private final static int HEIGHT_LVL_3 = 30;

    /**
     * Ship constructor
     * @param level Level
     * @param myPlanet Source planet
     * @param destination Destination planet
     * @param player Player that creates the ship
     * @param universe Universe
     * @param nbShipsSquadron Number of ship for the squadron
     */
    public Ship(int level, Planet myPlanet, Planet destination, Player player, Universe universe, int nbShipsSquadron) {
        this.level = level;
        this.myPlanet = myPlanet;
        this.destination = destination;
        this.player = player;
        this.universe = universe;
        this.angle = 0;
        this.newDestination = true;
        this.nbShipsSquadron = nbShipsSquadron;
        this.offsetTime = 0;
        this.started = false;
        this.selected = false;
        this.changeSelected = false;

        initShip();
        initPosition();
        render();
    }

    /**
     * Ship constructor
     * @param level Level
     * @param myPlanet Source planet
     * @param destination Destination planet
     * @param player Player that creates the ship
     * @param universe Universe
     */
    public Ship (int level, Planet myPlanet, Planet destination, Player player, Universe universe) {
        this(level, myPlanet, destination, player, universe, 1);
    }

    /**
     * Initializes the shape of the ship
     */
    private void initShip(){
        switch (level){
            case 1:
                shape = new Polygon();

                width = WIDTH_LVL_1;
                height = HEIGHT_LVL_1;

                speed = SPEEDS[0];
                power = POWERS[0];
                cost = COSTS[0];
                offsetTimeRef = 45;

                break;

            case 2:
                shape = new Circle();

                width = SIZE_LVL_2;
                height = SIZE_LVL_2;

                speed = SPEEDS[1];
                power = POWERS[1];
                cost = COSTS[1];
                offsetTimeRef = 45;
                break;

            case 3:
                shape = new Polygon();

                width = WIDTH_LVL_3;
                height = HEIGHT_LVL_3;

                speed = SPEEDS[2];
                power = POWERS[2];
                cost = COSTS[2];
                offsetTimeRef = 45;
                break;

            default:
                break;
        }

        shape.setFill(Color.TRANSPARENT);
        shape.setStroke(Color.TRANSPARENT); // Par défaut le Ship est invisible
        shape.setStrokeWidth(3);
    }

    /**
     * Initializes the position of the ship all around the planet
     */
    private void initPosition() {
        if (destination == null)
            System.out.println("coucou");

        this.x = myPlanet.getX();
        this.y = myPlanet.getY();

        destWidth = destination.getX() - x;
        destHeight = destination.getY() - y;

        angle = computeAngle() - 90;

        double semiPerimeter = Math.PI * myPlanet.getRadius();
        int max = (int) semiPerimeter / 10;

        offsetTime = -1 * (actualShipSquadron / max) * offsetTimeRef;

        changeAngle((int) (((actualShipSquadron % max) + 1) / 2.0) * (actualShipSquadron % 2 == 0 ? 10 : -10));
        x = myPlanet.getX() + (myPlanet.getRadius() + height / 2.0 + 1 + shape.getStrokeWidth()) * Math.cos(Math.toRadians(angle));   // Le +1 permet d'éviter les problèmes de collision avec la planète source à la création du Ship
        y = myPlanet.getY() + (myPlanet.getRadius() + height / 2.0 + 1 + shape.getStrokeWidth()) * Math.sin(Math.toRadians(angle));

        actualShipSquadron++;
        if (actualShipSquadron == nbShipsSquadron)
            actualShipSquadron = 0;

        angle = computeAngle();
    }

    /**
     * Updates the ship position
     */
    public void updatePosition() {
        // Si offsetTime < 0, pas encore prêt pour le déplacement
        if (offsetTime < 0) {
            offsetTime += speed;
            return;
        }

        destWidth = destination.getX() - x;
        destHeight = destination.getY() - y;

        int iteration = speed;

        // Détermine si besoin d'esquiver une planete ou non
        boolean moved = false;
        for (Planet p : universe.planetList) {
            Circle atmosphere = new Circle(p.getX(), p.getY(), p.getRadius() + height / 2.0 + shape.getStrokeWidth());

            if (p != destination && atmosphere.contains(x, y)) {
                avoidPlanet(iteration, p);
                moved = true;
                break;
            }
        }

        if (!moved) {

            if (destination.getX() != x && destination.getY() != y) {
                moveForward(iteration);

                // Seulement si la destination vient de changer
                if (newDestination) {
                    angle = computeAngle();
                    newDestination = false;
                }
            }
            else if (destination.getX() == x && destination.getY() != y) { // Mouvement vertical
                if (destination.getY() > y) {
                    y += iteration;
                    if (newDestination) {
                        angle = 180;
                        newDestination = false;
                    }
                }
                else {
                    y -= iteration;
                    if (newDestination) {
                        angle = 0;
                        newDestination = false;
                    }
                }
            }
            else if (destination.getX() != x && destination.getY() == y) { // Mouvement horizontal
                if (destination.getX() > x) {
                    x += iteration;
                    if (newDestination) {
                        angle = 90;
                        newDestination = false;
                    }
                }
                else {
                    x -= iteration;
                    if (newDestination) {
                        angle = 270;
                        newDestination = false;
                    }
                }
            }
        }
    }

    /**
     * Moves the ship toward the destination planet
     * @param iteration Number of pixels
     */
    private void moveForward(int iteration) {
        // Seulement si la destination vient de changer
        if (newDestination) {
            ratioX = Math.abs((destination.getX() - x) / (destination.getY() - y));
            ratioY = Math.abs((destination.getY() - y) / (destination.getX() - x));
        }


        double newX, newY;

        if (Math.abs(destWidth) >= Math.abs(destHeight)) {
            newX = x + (destWidth > 0 ? 1 : -1) * iteration;
            newY = y + ratioY * (destHeight > 0 ? 1 : -1) * iteration;
        }
        else {
            newX = x + ratioX * (destWidth > 0 ? 1 : -1) * iteration;
            newY = y + (destHeight > 0 ? 1 : -1) * iteration;
        }

        x = newX;
        y = newY;
    }

    /**
     * Moves the ship by dodging a specified planet
     * @param iteration Number of pixels
     * @param planet Planet to avoid
     */
    private void avoidPlanet(int iteration, Planet planet) {

        // Calcul des cas particulers
        // Alignement sur l'axe des x ou des y
        if (planet.getY() == y) {
            if (y <= destination.getY())
                y += iteration;
            else
                y -= iteration;
        }

        else if (planet.getX() == x) {
            if (x <= destination.getX())
                x += iteration;
            else
                x -= iteration;
        }

        // Cas général
        else {
            double gradient = -1 * (planet.getX() - x) / (planet.getY() - y);

            double tmpAngle = computeAngle() % 180;

            if (x < planet.getX())
                tmpAngle += 180;

            double ptY = planet.getY() + (planet.getRadius() + height / 2.0 + shape.getStrokeWidth()) * Math.sin(Math.toRadians(tmpAngle - 90));

            // Permet de changer les calculs en fonction du sens du Ship
            int direction = (x < destination.getX()) ? 1 : -1;

            if (gradient <= -1) {
                gradient = 1 / gradient;

                if (y <= ptY) {
                    if (planet.getX() >= x) {
                        x -= gradient * iteration * direction;
                        y -= iteration * direction;
                    }
                    else {
                        x += gradient * iteration * direction;
                        y += iteration * direction;
                    }
                }
                else {
                    if (planet.getX() >= x) {
                        x += gradient * iteration * direction;
                        y += iteration * direction;
                    }
                    else {
                        x -= gradient * iteration * direction;
                        y -= iteration * direction;
                    }
                }
            }
            else if (gradient < 0) {

                if (y <= ptY) {
                    if (planet.getX() >= x) {
                        x += iteration * direction;
                        y += gradient * iteration * direction;
                    }
                    else {
                        x -= iteration * direction;
                        y -= gradient * iteration * direction;
                    }
                }
                else {
                    if (planet.getX() >= x) {
                        x -= iteration * direction;
                        y -= gradient * iteration * direction;
                    }
                    else {
                        x += iteration * direction;
                        y += gradient * iteration * direction;
                    }
                }
            }
            else if (gradient >= 1) {
                gradient = 1 / gradient;

                if (y <= ptY) {
                    if (planet.getX() >= x) {
                        x -= gradient * iteration * direction;
                        y -= iteration * direction;
                    }
                    else {
                        x += gradient * iteration * direction;
                        y += iteration * direction;
                    }
                }
                else {
                    if (planet.getX() >= x) {
                        x += gradient * iteration * direction;
                        y += iteration * direction;
                    }
                    else {
                        x -= gradient * iteration * direction;
                        y -= iteration * direction;
                    }

                }
            }
            else if (gradient > 0) {
                if (y <= ptY) {
                    if (planet.getX() >= x) {
                        x -= iteration * direction;
                        y -= gradient * iteration * direction;
                    }
                    else {
                        x += iteration * direction;
                        y += gradient * iteration * direction;
                    }
                }
                else {
                    if (planet.getX() >= x) {
                        x += iteration * direction;
                        y += gradient * iteration * direction;
                    }
                    else {
                        x -= iteration * direction;
                        y -= gradient * iteration * direction;
                    }
                }
            }
        }


        angle = computeAngle();
        newDestination = true;
    }

    /**
     * Rotates the ship
     * @param degree Angle
     */
    private void rotate(double degree) {
        // https://math.stackexchange.com/questions/270194/how-to-find-the-vertices-angle-after-rotation

        if (level != 2) {
            double[] points = (((Polygon) shape).getPoints().stream().mapToDouble(Number::doubleValue).toArray());

            for (int i = 0; i < points.length; i += 2) {
                double xO = points[i];
                double yO = points[i + 1];

                double xN = (xO - x) * Math.cos(Math.toRadians(degree)) - (yO - y) * Math.sin(Math.toRadians(degree)) + x;
                double yN = (xO - x) * Math.sin(Math.toRadians(degree)) + (yO - y) * Math.cos(Math.toRadians(degree)) + y;

                ((Polygon) shape).getPoints().set(i, xN);
                ((Polygon) shape).getPoints().set(i + 1, yN);
            }
        }
    }

    /**
     * Determines if the ship is arrived
     * @return State of the ship
     */
    public boolean isArrived() {
        Circle atmosphere = new Circle(destination.getX(), destination.getY(), destination.getRadius() + height / 2.0 + shape.getStrokeWidth());

        if (atmosphere.contains(x, y)) {
            if (destination.getPlayer() != player) { // Attaque
                destination.decreaseShip(power);

                if (destination.getNbShips() <= 0) {
                    destination.setPlayer(player);

                    if (destination.getNbShips() < 0)
                        destination.setNbShips(0);
                }
            }
            else                                     // Mouvement de troupes
                destination.addShip(cost);

            return true;
        }

        return false;
    }

    /**
     * Selects the ship
     */
    public void selectSquadronShip() {
        changeSelected = true;
        selected = true;
    }

    /**
     * Unselects the ship
     */
    public void unselectSquadronShip() {
        changeSelected = true;
        selected = false;
    }

    /**
     * Updates the rendering of the ship
     */
    public void render() {
        // Affichage du Ship
        if (!started && offsetTime >= 0) {
            shape.setStroke(player.getColor());
            started = true;
        }

        if (started && changeSelected) {
            if (selected)
                shape.setFill(shape.getStroke());
            else
                shape.setFill(Color.TRANSPARENT);

            changeSelected = false;
        }


        if (level == 1 || level == 3) { // Les niveaux 1 et 3 sont des triangles
            ((Polygon) shape).getPoints().clear();
            ((Polygon) shape).getPoints().addAll(
                    x, y - height/2,
                    x + width/2, y + height/2,
                    x - width/2, y + height/2
            );
        }
        else if (level == 2) { // Le niveau 2 est un cercle
            ((Circle) shape).setRadius(height / 2.0);
            ((Circle) shape).setCenterX(x);
            ((Circle) shape).setCenterY(y);
        }

        rotate(angle);
    }

    /**
     * Computes ship's orientation
     * @return Angle
     */
    private double computeAngle() {
        double w = Math.abs(destWidth);
        double h = Math.abs(destHeight);

        if (destination.getX() >= x && destination.getY() <= y)
            return Math.toDegrees(Math.atan(w / h));

        else if (destination.getX() >= x && destination.getY() >= y)
            return Math.toDegrees(Math.atan(h / w)) + 90;

        else if (destination.getX() <= x && destination.getY() >= y)
            return Math.toDegrees(Math.atan(w / h)) + 180;

        else // if (destination.getX() <= x && destination.getY() <= y)
            return Math.toDegrees(Math.atan(h / w)) + 270;

    }

    /**
     * Set angle value
     * @param angle Angle
     */
    private void setAngle(double angle) {
        this.angle = angle % 360;
        while (this.angle < 0)
            this.angle += 360;
    }

    /**
     * Changes the angle value
     * @param delta Delta
     */
    private void changeAngle(double delta) {
        setAngle(angle + delta);
    }

    /**
     * Changes the destination planet
     * @param newDest New planet
     */
    public void setDestination(Planet newDest) {
        this.destination = newDest;

        newDestination = true;
    }

    /**
     * Get ship's Shape
     * @return Shape
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Get ship's Player
     * @return Player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the speed for a specified level
     * @param level Level
     * @return Speed
     */
    public static int getSpeedByLevel(int level) {
        return SPEEDS[level - 1];
    }

    /**
     * Get the power for a specified level
     * @param level Level
     * @return Power
     */
    public static int getPowerByLevel(int level) {
        return POWERS[level - 1];
    }

    /**
     * Get the cost for a specified level
     * @param level Level
     * @return Cost
     */
    public static int getCostByLevel(int level) {
        return COSTS[level - 1];
    }

}
