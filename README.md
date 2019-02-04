# blokus
The board game Blokus that I built using Java using Eclipse as my IDE and Maven to compile and run tests.

See: https://en.wikipedia.org/wiki/Blokus for the game's rules

The project was built on Ubuntu 18.04.1 LTS in Eclipse v4.9.0 using JDK v10.0.2 and built using Maven v3.5.4

AS OF FEBRUARY 4, 2019: The game can be played by 2, 3 or 4 human players by interacting with a GUI using the keyboard.

The keyboard commands are as follows:

W,A,S,D    : Move the currently selected piece around the board

R          : Rotate the currently selected piece in increments of 90 degrees clockwise

Q          : Flip the currently selected piece vertically, reflecting it over the x-axis

E          : Flip the currently selected piece horizontally, reflecting it over the y-axis

ENTER      : Attempt to place the currently selected piece at its current position on the board

P          : Pass the current player's turns for the rest of the game (when they have no more moves that can be made)

1,2,3,4,5  : Change the classification (the size) of the currently selected piece

LEFT,RIGHT : Cycle through the pieces of the currently selected classification

TO RUN:

1: To build the project, enter the directory with the pom.xml file and type 'mvn package'

2: To play the game, type:

'java-cp target/santanjm-blokus-1.0-SNAPSHOT.jar santanjm.blokus.Blokus <# of players> <size of grid>'
  
  where '<# of players>' is how many players are in the game and '<size of grid>' is how big the game board will be
  
  3: Follow the rules of the game and play using the keyboard
