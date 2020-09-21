import java.util.ArrayList;

/**
 * Squadron class
 */
public class Squadron {

    /**
     * Ships list
     */
    public ArrayList<Ship> shipList;

    /**
     * Player
     */
    private Player player;

    /**
     * Squadron constructor
     * @param nbShips Number of ships
     * @param levelShip Ships' level
     * @param source Source planet
     * @param destination Destination planet
     * @param universe Universe
     */
    public Squadron(int nbShips, int levelShip, Planet source, Planet destination, Universe universe) {
        shipList = new ArrayList<>();

        // Ajout de l'escadron au joueur
        player = source.getPlayer();
        player.addSquadron(this);

        // Création des vaisseaux
        for (int i = 0; i < nbShips; i++) {
            shipList.add(new Ship(levelShip, source, destination, source.getPlayer(), universe, nbShips));
        }
    }

    /**
     * Select all ships of the squadron
     */
    public void selectSquadron() {
        for (Ship s : shipList)
            s.selectSquadronShip();
    }

    /**
     * Unselect all ships of the squadron
     */
    public void unselectSquadron() {
        for (Ship s : shipList)
            s.unselectSquadronShip();
    }

    /**
     * Get squadron's player
     * @return Player
     */
    public Player getPlayer() {
        return player;
    }
}
