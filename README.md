# Snake Game
> A Snake game developed using Java.


## Try the game
The working executable of the game can be found in the included `.rar` archive. Simply extract the archive and run the executable to start the game.


## Technologies Used
- Java
- JavaFX for GUI
- Serialization for saving and loading game progress
- Java Concurrent API for pausing and resuming game
- Enum for game difficulties
- Java 8 Time API for calculating game duration
- Singleton pattern for creating only one instance of the game


## Key Features
- Multiple game difficulties (easy, medium, hard)
- Pause and resume functionality
- Save and load game progress
- Display game duration and points
- Speed up the snake as points increase
- Display "game over" message when snake hits the wall or itself


# Classes and Methods
```
The main class Snake extends Thread and implements Serializable. It includes methods to initialize the snake and apple, move the snake, speed up the snake, and calculate game duration. The class uses the Singleton pattern to ensure only one instance of the game is created.

The game implements the pause and resume functionality using the Java Concurrent API and a ReentrantLock object.

The Difficulties and Directions enums are used to specify the game difficulty and the direction in which the snake should move.

The game uses multiple helper classes like Point, CheckBox...Machine for various game-related calculations and operations.
```


# Demo
![](https://github.com/lwantPizza/SimpleSnake/blob/main/images/gameplay.gif?raw=true)
