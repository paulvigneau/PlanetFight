import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Universe class
 */
public class Universe {

    /**
     * Width
     */
    private int width;
    /**
     * Height
     */
    private int height;

    /**
     * Number of planets
     */
    private int nbPlanets;
    /**
     * Number of players
     */
    private int nbPlayers;

    /**
     * Planets list
     */
    public ArrayList<Planet> planetList;
    /**
     * Squadrons list
     */
    public ArrayList<Squadron> squadronList;
    /**
     * Players list
     */
    public ArrayList<Player> playerList;

    /**
     * Universe constructor
     * @param width Width
     * @param height Height
     * @param nbPlanets Number of planets
     * @param nbPlayers Number of players
     */
    public Universe(int width, int height, int nbPlanets, int nbPlayers) {
        this.width = width;
        this.height = height;
        this.nbPlanets = nbPlanets;
        this.nbPlayers = nbPlayers;

        planetList = new ArrayList<>();
        squadronList = new ArrayList<>();
        playerList = new ArrayList<>();

        initPlayers();
        initPlanets();
    }

    /**
     * Initializes all the players
     */
    private void initPlayers() {
        for (int i = 1; i <= nbPlayers; i++) {
            playerList.add(new Player(i, this));
        }
    }

    /**
     * Initializes all the planets
     */
    private void initPlanets() {
        Random rand = new Random();

        // Pour chaque planète
        // On détermine ses attributs aléatoirement,
        // On regarde si elle a sa place dans le plateau de jeu
        // Si c'est possible --> ajout de la planète
        // Sinon --> on recommence l'opération sur une limite de 20 fois

        for (int i = 0; i < nbPlanets; i++) {
            int cpt = 0;
            boolean found = true;
            int radius, x, y, nbShips;
            Player player = null;
            boolean sick;

            // On effectue plusieurs fois le test afin de trouver (ou non) un place pour la Planet
            do {
                // Coordonnées, rayon et nombre de Ship aléatoires
                radius = rand.nextInt(100 - 25 + 1) + 25;
                x = rand.nextInt(width - radius * 4) + radius * 2;
                y = rand.nextInt(height - radius * 4) + radius * 2;
                sick = rand.nextInt(10) == 0;

                nbShips = rand.nextInt(100) + 1;

                // Selection du joueur (peut être null)
                if (i < nbPlayers) {
                    player = playerList.get(i);
                    nbShips = 100;
                    sick = false;
                }

                // Tests sur les autres Planets pour voir si la place est libre
                for (Planet p: planetList) {
                    double distance = Math.sqrt(Math.pow(x - p.getX(), 2) + Math.pow(y - p.getY(), 2));
                    found = distance > radius + p.getRadius() + 50;
                    if (!found)
                        break;
                }

                cpt++;
            } while  (!found && (cpt < 20));

            // Ajout de la Planet
            if (found)
                planetList.add(new Planet(x, y, radius, sick, player, this, nbShips));
        }
    }

    /**
     * Get the winner
     * @return Player
     */
    public Player getWinner() {
        // Vérification que la pertie est bien terminée
        if (endGame()) {

            // Retourne le seul Player qui a une liste de planètes > 0
            for (Player player: playerList)
                if (player.getPlanetList().size() > 0)
                    return player;
        }

        return null;
    }

    /**
     * Determines if the game is over
     * @return Boolean
     */
    public boolean endGame() {

        // Suppression des escadrons quand size() == 0
        squadronList.removeIf(squadron -> squadron.shipList.size() == 0);

        // Compte le nombre de planètes
        int playersPlaying = 0;

        for(Player player: playerList)
            if (player.getPlanetList().size() > 0)
                playersPlaying++;

        return (playersPlaying == 1);
    }

    /**
     * Saves the game
     */
    public void saveGame() {
        ObjectOutputStream oos = null;

        try {
            final FileOutputStream file = new FileOutputStream("save.ser");
            oos = new ObjectOutputStream(file);

            // Sauvegarde du nombre de joueurs
            oos.writeInt(playerList.size());

            // Serialisation des joueurs
            for (Player player : playerList) {
                oos.writeObject(player);
                // Sauvegarde de la couleur
                oos.writeDouble(player.getColor().getRed());
                oos.writeDouble(player.getColor().getGreen());
                oos.writeDouble(player.getColor().getBlue());
            }


            // Sauvegarde du nombre de planetes
            oos.writeInt(planetList.size());

            // Serialisation des planetes
            for (Planet planet : planetList) {
                oos.writeObject(planet);
                Player p;

                // Ajout de l'indice du joueur
                if ((p = planet.getPlayer()) != null)
                    oos.writeInt(p.getIndex());
                else
                    oos.writeInt(0);
            }
        }
        catch (final java.io.IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (oos != null) {
                    oos.flush();
                    oos.close();
                }
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load a saved game
     */
    public void loadGame() {
        File file = new File("save.ser");

        // Test sur l'existance du fichier save.ser
        if (file.exists()) {

            playerList.clear();
            planetList.clear();
            squadronList.clear();

            ObjectInputStream ois = null;

            try {
                final FileInputStream fichier = new FileInputStream("save.ser");
                ois = new ObjectInputStream(fichier);

                // Récupération des Players
                int number = ois.readInt();
                for (int i = 0; i < number; i++) {
                    Player player = (Player) ois.readObject();

                    double r = ois.readDouble();
                    double g = ois.readDouble();
                    double b = ois.readDouble();

                    playerList.add(new Player(player.getIndex(), Color.color(r, g, b), this));
                }

                // Récupération des Planets
                number = ois.readInt();
                for (int i = 0; i < number; i++) {
                    Planet planet = (Planet) ois.readObject();

                    planetList.add(new Planet(planet, this));

                    // Ajout du joueur
                    int index = ois.readInt();
                    if (index > 0)
                        planetList.get(i).setPlayer(playerList.get(index - 1));
                }
            }
            catch (final IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                }
                catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
