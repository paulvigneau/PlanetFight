# Planet Fight

## Presentation

This project has been developed during the 3rd year of Bachelor at the University of Bordeaux, France by [Paul Vigneau](https://github.com/paulvigneau) and [Nicolas Desclaux](https://github.com/Ndesclaux).

This program is coded in Java 1.8 and uses the JavaFX library.

## Rules

The game begins with a Universe composed of several planets. One planet is controlled by the player, some planets by an AI (depending of the parameters of the game) and the other planets are neutrals. The goal is to control all the opponents planets by sending a percentage of one's ships to other planets.

## Functionalities

- Planets:
  - Each planet has a random size, position and number of ships.
  - Controlled planets are colored with the player/AI's color and the neutral ones are grey. Main player's color is **blue**.
  - Controlled planets begin the game with 100 ships.
  - A planet can send a percentage of its ships to another planet.
  - If a planet received allied ships, its ships count increases with the squadron size.
  - If a planet received opponent ships, its ships count decreases with the squadron size.
  - When been attacked, if the ships count of a planet becomes negative, the opponent is the new owner of the planet.
  - The number of ships of controlled planets is increased by 1 every second. Neutral planets don't increase.
  - A planet can be sick, its ship production is not constant.
- Ships:
  - A ship can only be launched from a planet and its destination is a planet too.
  - When a ship reaches its target, it disappears.
  - A ship has a speed, force, production cost and shape. There are three types of ships.
  - A ship has a destination that can change during its flight.
  - A ship goes to its destination without touching other planets.
- Artificial Intelligence:
  - The AI pattern is simple, le "player" takes a decision randomly between 4 choices:
    1. Attack the planet with the lower number of ships.
    2. Attack an opponent's planet that can be owned and send the exact number of ships.
    3. Attack a neutral planet and sent the exact number of ships.
    4. Attack/rescue a random planet.
- Save/Load:
  - There is a "save" button in the right panel to save game. There is also a "restore" button to load the previously saved game.
  - The save is located in the file `./save.ser`
- Parameters
  - There is a side panel at the right of the screen that permit to manage some parameters of the game.
  - There is a slider to select what percentage of ships the player want to send.
  - There is a part that indicates the type of ships that is selected with details.
  - Two buttons "-" and "+" to decrease or increase the number of players (AI) in the game. Can be from 2 to 10 (the default number of planets but can be changed in the code).
  - The buttons "save" and "restore".

## Controls

- Drag and drop from one of your planet to another planet to send a percentage of ships.
- Click on some of your planets with the CTRL key down to select multi planets. Drag and drop from any of them to send ships from the selection.
- Up and down arrows, slider in side panel or mouse wheel can increase/decrease the percentage of ships to send.
- Use the mouse wheel + CTRL key down to select a squadron that is flying (the selected squadron is highlighted). Click on a planet to change the target of the squadron.
- Use the digit keys to choose the ship type (from 1 to 3).

## Possible improvements

- Add some images and textures to make more beautiful the game.
- Add the possibility to save more that one game and to choose a precise one.
