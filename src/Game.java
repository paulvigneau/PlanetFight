import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


/**
 * Manages the progress of the application
 */
public class Game extends Application {

    /**
     * Width of the play area
     */
    private final static int WIDTH = 1200;
    /**
     * Height of the play area
     */
    private final static int HEIGHT = 800;
    /**
     * Number of players
     */
    private static int NB_PLAYERS = 2;
    /**
     * Number of planets
     */
    private final static  int NB_PLANETS = 10;

    /**
     * Width of the option panel
     */
    private final static int OPTION_WIDTH = 150;

    /**
     * Ship level
     */
    private int shipLevel = 1;

    /**
     * Determines if a planet is selected
     */
    private boolean onDrag = false;
    /**
     * Source planet
     */
    private Planet source;

    /**
     * Determines if squadron selection is allowed
     */
    private boolean selectionAllowed = false;
    /**
     * Selected squadron
     */
    private Squadron selectedSquadron = null;

    /**
     * Universe
     */
    private Universe universe;

    /**
     * Determines if winner has been showed
     */
    private boolean winnerShowed = false;

    /**
     * Get file resource
     * @param name Path
     * @return Absolute path
     */
    private static String getRessourcePathByName(String name) {
        return Game.class.getResource('/' + name).toString();
    }

    /**
     * Main function
     * @param args Args
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        /*
         * Mise en place de l'affichage de fenêtre de jeu
         */

        stage.setTitle("Planet Fight");
        stage.setResizable(false);

//        Stage
//        |  Scene
//        |  |  StackPane
//        |  |  |  HBox
//        |  |  |  |  Group
//        |  |  |  |  |  Canvas
//        |  |  |  |  VBox
//        |  |  |  |  |  Slider
//        |  |  |  |  |  Button(s)
//        |  |  |  StackPane
//        |  |  |  |  Label
//        |  |  |  |  Button(s)

        StackPane windowPane = new StackPane();
        windowPane.setMaxWidth(WIDTH + OPTION_WIDTH - 10);
        windowPane.setMaxHeight(HEIGHT - 10);
        Scene scene = new Scene(windowPane);

        HBox windowLayout = new HBox();
        windowLayout.setMaxWidth(WIDTH + OPTION_WIDTH - 10);
        windowLayout.setMaxHeight(HEIGHT - 10);

        windowPane.getChildren().add(windowLayout);

        Group root = new Group();
        windowLayout.getChildren().add(root);

        Canvas canvas = new Canvas();
        canvas.setWidth(WIDTH);
        canvas.setHeight(HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Image image = new Image(getRessourcePathByName("images/space.jpg"), WIDTH, HEIGHT, false, false);

        root.getChildren().add(canvas);


        // Layout placé à droite de la fenetre avec les options de jeu
        VBox optionLayout = new VBox();
        optionLayout.setPrefWidth(OPTION_WIDTH);
        optionLayout.setMaxHeight(HEIGHT);
        optionLayout.setAlignment(Pos.TOP_CENTER);
        windowLayout.getChildren().add(optionLayout);


        // Slider pour gerer le pourcentage de troupe à envoyer
        Slider slider = new Slider(0, 100, 50);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(25);
        slider.setMinorTickCount(24);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setPrefHeight(350);
        slider.setLayoutY(200);
        slider.setLayoutX(200);

        optionLayout.getChildren().add(slider);


        // Valeur du Slider
        Text sliderValueText = new Text((int) slider.getValue() + "%");
        sliderValueText.setFont(new Font(20));
        optionLayout.getChildren().add(sliderValueText);


        // Separator
        Separator separator1 = new Separator(Orientation.HORIZONTAL);
        separator1.setStyle("-fx-padding: 10px 5px 10px 5px;");
        optionLayout.getChildren().add(separator1);


        // Caractéristiques du vaisseau actuel
        Text shipLevelText = new Text("Niveau : " + shipLevel);
        Text shipSpeedText = new Text("Vitesse : " + Ship.getSpeedByLevel(shipLevel));
        Text shipPowerText = new Text("Puissance : " + Ship.getPowerByLevel(shipLevel));
        Text shipCostText = new Text("Coût : " + Ship.getCostByLevel(shipLevel));

        shipLevelText.setFont(new Font(20));
        shipSpeedText.setFont(new Font(20));
        shipPowerText.setFont(new Font(20));
        shipCostText.setFont(new Font(20));

        optionLayout.getChildren().addAll(shipLevelText, shipSpeedText, shipPowerText, shipCostText);


        //Separator
        Separator separator2 = new Separator(Orientation.HORIZONTAL);
        separator2.setStyle("-fx-padding: 10px 5px 10px 5px;");
        optionLayout.getChildren().add(separator2);


        // Changement du nombre de joueurs
        Text playersNbText = new Text("Nombre de joueurs : " + NB_PLAYERS);
        playersNbText.setFont(new Font(15));
        optionLayout.getChildren().add(playersNbText);

        HBox playerNbBox = new HBox();
        playerNbBox.setAlignment(Pos.CENTER);
        optionLayout.getChildren().add(playerNbBox);

        Button playerDecrementButton = new Button("-");
        Button playerIncrementButton = new Button("+");

        playerDecrementButton.setMaxWidth(Double.MAX_VALUE);
        playerIncrementButton.setMaxWidth(Double.MAX_VALUE);

        playerDecrementButton.setFont(new Font(20));
        playerIncrementButton.setFont(new Font(20));

        playerNbBox.setSpacing(10);
        playerNbBox.getChildren().addAll(playerDecrementButton, playerIncrementButton);


        //Separator
        Separator separator3 = new Separator(Orientation.HORIZONTAL);
        separator3.setStyle("-fx-padding: 10px 5px 10px 5px;");
        optionLayout.getChildren().add(separator3);


        // Bouton sauvegarder partie
        Button saveButton = new Button("Sauvegarder");
        optionLayout.getChildren().add(saveButton);

        // Bouton charger sauvegarde
        Button loadSave = new Button("Charger");
        optionLayout.getChildren().add(loadSave);


        // StackPane de l'affichage du vainqueur
        StackPane winnerPane = new StackPane();
        StackPane.setAlignment(winnerPane, Pos.CENTER);


        // Texte de l'affichage du vainqueur
        Label winnerText = new Label();
        winnerText.setFont(new Font(30));
        winnerText.setTextAlignment(TextAlignment.CENTER);
        winnerPane.getChildren().add(winnerText);
        StackPane.setAlignment(winnerText, Pos.CENTER);


        // Boutons : REJOUER et QUITTER
        Button replayButton = new Button("Rejouer");
        Button exitButton = new Button("Quitter");

        StackPane.setAlignment(replayButton, Pos.BOTTOM_CENTER);
        replayButton.setFont(new Font(30));
        replayButton.setTranslateX(100);
        replayButton.setTranslateY(-20);
        StackPane.setAlignment(exitButton, Pos.BOTTOM_CENTER);
        exitButton.setFont(new Font(30));
        exitButton.setTranslateX(-100);
        exitButton.setTranslateY(-20);

        winnerPane.getChildren().addAll(replayButton, exitButton);


        /*
         * Mise en place de la mécanique du jeu
         */

        // Création de l'univers
        universe = new Universe(WIDTH, HEIGHT, NB_PLANETS, NB_PLAYERS);


        /*
         * Listeners
         */

        root.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {

            // Change la destination de l'escadron sélectionné
            if (selectionAllowed && selectedSquadron != null) {
                Planet planetDest = null;

                // Détermine la planete cliquée
                for (Planet p : universe.planetList) {
                    if (p.getCircle().contains(event.getX(), event.getY())) {
                        planetDest = p;
                        break;
                    }
                }

                // Change la destination des vaisseaux de l'escadron sélectionné
                if (planetDest != null)
                    for (Ship s : selectedSquadron.shipList)
                        s.setDestination(planetDest);
            }

            // Détermine la planète source (uniquement le joueur 1)
            if (!onDrag) {
                for (Planet p : universe.planetList) {
                    if (p.getPlayer() != null && p.getPlayer().getIndex() == 1 && p.getCircle().contains(event.getX(), event.getY())) {
                        source = p;

                        onDrag = true;
                        break;
                    }
                }
            }
        });

        root.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {

            // Détermine la planète destination
            if (onDrag) {
                for (Planet p : universe.planetList) {
                    if (p != source && p.getCircle().contains(event.getX(), event.getY())) {
                        universe.squadronList.add(source.createShips(p, shipLevel));

                        break;
                    }
                }
            }
            onDrag = false;
        });

        // Listener sur le clavier
        scene.setOnKeyPressed(event -> {
            boolean isDigit = false;

            switch (event.getCode()) {
                case UP:
                    slider.increment();
                    break;

                case DOWN:
                    slider.decrement();
                    break;

                case CONTROL:
                    selectionAllowed = true;
                    break;

                case DIGIT1:
                case NUMPAD1:
                    shipLevel = 1;
                    isDigit = true;
                    break;

                case DIGIT2:
                case NUMPAD2:
                    shipLevel = 2;
                    isDigit = true;
                    break;

                case DIGIT3:
                case NUMPAD3:
                    shipLevel = 3;
                    isDigit = true;
                    break;

                default:
                    break;
            }

            if (isDigit) {
                shipLevelText.setText("Niveau : " + shipLevel);
                shipSpeedText.setText("Vitesse : " + Ship.getSpeedByLevel(shipLevel));
                shipPowerText.setText("Puissance : " + Ship.getPowerByLevel(shipLevel));
                shipCostText.setText("Coût : " + Ship.getCostByLevel(shipLevel));
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                selectionAllowed = false;

                // Deselection du Squadron
                if (selectedSquadron != null)
                    selectedSquadron.unselectSquadron();

                selectedSquadron = null;
            }
        });

        // Listener sur le le scroll de la souris
        scene.setOnScroll(event -> {
            if (!selectionAllowed) {
                if (event.getDeltaY() > 0)
                    slider.increment();
                else
                    slider.decrement();
            }
            else {
                if (event.getDeltaY() > 0) {
                    // Deselection de l'ancien Squadron, s'il existe
                    if (selectedSquadron != null) {
                        selectedSquadron.unselectSquadron();
                        selectedSquadron = null;
                    }

                    // Récupération du nouveau et selection
                    selectedSquadron = universe.playerList.get(0).getNextSquadron();
                    if (selectedSquadron != null)
                        selectedSquadron.selectSquadron();
                }
            }
        });

        // Listener pour l'affichage de la valeur du Slider
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int value = (int) slider.getValue();
            sliderValueText.setText(value + "%");

            universe.playerList.get(0).setPercentShips((slider.getValue() / 100));
        });

        // Listener sur les Buttons
        playerDecrementButton.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (NB_PLAYERS > 2) {
                NB_PLAYERS--;

                playersNbText.setText("Nombre de joueurs : " + NB_PLAYERS);

                // On relance une partie
                universe = new Universe(WIDTH, HEIGHT, NB_PLANETS, NB_PLAYERS);
            }
        });

        playerIncrementButton.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (NB_PLAYERS < NB_PLANETS) {
                NB_PLAYERS++;

                playersNbText.setText("Nombre de joueurs : " + NB_PLAYERS);

                // On relance une partie
                universe = new Universe(WIDTH, HEIGHT, NB_PLANETS, NB_PLAYERS);
            }
        });

        saveButton.addEventFilter(MouseEvent.MOUSE_PRESSED, event ->
                universe.saveGame()
        );

        loadSave.addEventFilter(MouseEvent.MOUSE_PRESSED, event ->
                universe.loadGame()
        );

        replayButton.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            // On recharge un nouvel Universe
            universe = new Universe(WIDTH, HEIGHT, NB_PLANETS, NB_PLAYERS);

            slider.setValue(50);

            // Suppression de l'affichage du vaiqueur de la précédente partie
            windowPane.getChildren().remove(winnerPane);
            winnerShowed = false;
        });

        exitButton.addEventFilter(MouseEvent.MOUSE_PRESSED, event ->
                stage.close()
        );


        /*
         * Game Loop
         */

        new AnimationTimer()
        {
            long previousTime = 0;

            public void handle(long currentTime)
            {
                // Exécution seulement si la partie n'est pas terminée
                if (!universe.endGame() || universe.squadronList.size() != 0) {

                    if (previousTime == 0)
                        previousTime = currentTime;

                    // Compteur de secondes
                    double secondsElapsed = ((currentTime - previousTime) / 1e9f);

                    // On nettoie l'affichage
                    root.getChildren().clear();

                    gc.drawImage(image, 0, 0);
                    root.getChildren().add(canvas);

                    for (Planet p : universe.planetList) {
                        // S'il s'est ecoule 1 sec et que la planete n'est pas neutre, on ajoute un Ship
                        if (secondsElapsed >= 1 && p.getPlayer() != null){
                            if (!p.isSick())
                                p.addShip(1);
                        }

                        // Affichage de Planet
                        p.render();
                        root.getChildren().addAll(p.getCircle(), p.getText());
                    }

                    // Fait jouer l'Ordinateur (IA naive)
                    if (secondsElapsed >=1) {
                        for (Player ai : universe.playerList)
                            if (ai.getIndex() != 1)
                                ai.AI_Play();
                    }


                    if (secondsElapsed >= 1)
                        previousTime = 0;

                    // Pour chaque Squadron, on parcourt chaque Ship
                    universe.squadronList.forEach(squadron -> {
                                for (Ship s : squadron.shipList) {

                                    // Mise a jour de la position du Ship et affichage
                                    s.updatePosition();
                                    s.render();

                                    root.getChildren().add(s.getShape());
                                }

                                // Si le Ship entre en collision avec sa destination, on le supprime
                                squadron.shipList.removeIf(Ship::isArrived);

                                // Suppression des escadrons dans Player si size() == 0
                                if (squadron.shipList.size() == 0)
                                    squadron.getPlayer().removeSquadron(squadron);
                            }
                    );
                }

                // Sinon affichage du vainqueur
                else if (!winnerShowed) {
                    Player winner = universe.getWinner();



                    if (winner.getIndex() == 1) {
                        winnerPane.setStyle("-fx-background-color: lightblue");
                        winnerText.setText("Vous avez gagné !");
                    }

                    else {
                        winnerPane.setStyle("-fx-background-color: #" + winner.getColor().toString().substring(2));
                        winnerText.setText(
                                "Vous avez perdu...\n" +
                                        "Joueur #" + winner.getIndex() + " a gagné !");
                    }

                    // Affichage du vainqueur
                    windowPane.getChildren().add(winnerPane);

                    winnerShowed = true;
                }
            }
        }.start();

        stage.setScene(scene);
        stage.show();
    }
}
