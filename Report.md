<H1 style="text-align: center;">Danmarks Tekniske Universitet</H1>
<H3 style="text-align: center;">Advanced Programming Project</H3>
<br>
<br>
<p align="center">
    <img src="graphics/DTU Logo.png">
</p>
<br>
<H3 style="text-align: center;">Group 7 </H3>
<H3 style="text-align: center;">Frederik Andersen (s224308), Nicolai D. Madsen (s213364)</H3>

<H2 style="text-align: center;">RoboRally</H2>
<br>
<H3 style="text-align: center;">Wednesday 8th of May </H3>


## SpaceView.java <br>
This class is responsible for displaying two important game elements.
We modified the constructor of the class to create 2 *Image View* classes for two purposes:

### 1. Displaying Background Image <br>
   **Purpose:** To ensure that the game board's background is displayed correctly. <br><br>
   **Implementation:** Modified this method to render the background image before any other elements are drawn. 
   This ensures that the background is always visible and properly set before overlaying other components.<br><br>
   **Justification:** This change was important for setting the correct visual context for the game, which helps players to understand the game layout. <br>
   
### 2. Displaying Player Image <br>
   **Purpose:** To correctly display the player image on the game board.<br><br>
   **Implementation:** Modified the *updateView* method to include logic for rendering the player image correctly based on the player's position and orientation.
   This involved checking the player's current state and fetching the appropriate image resource for display based on the id of the player.<br><br>
   **Justification:** This change was necessary to provide visual feedback of where the player is on the board, which greatly enhanced the game experience.

## Wall.java <br>
   **Purpose:** This class was implemented to represent the walls on the game board, to prevent the robot from moving through the wall. <br><br>
   **Implementation:** It contains methods to check the position and heading of the wall and an offset on the tile.
   The logic behind it is implemented in the *handleMovement* class.<br><br>
   **Justification:** This feature is essential for game mechanics, as it adds strategic elements to the game by restricting robot movement.

## WallView.java <br>
   **Purpose:** This class was implemented to visually represent the *Wall* on the game board. <br><br>
   **Implementation:** Includes logic to fetch the appropriate wall image and render it at the correct position. <br><br>
   **Justification:** The visual representation of the walls is critical for player interaction and game strategy.

## Checkpoint.java <br>
   **Purpose:** The class was implemented to represent checkpoints that the robots have to pass through in the correct order.<br><br>
   **Implementation:** It keeps track of the checkpoint order and validates whether the robot is passing through the correct checkpoint in the correct order. <br><br>
   **Justification:** This feature adds an additional layer of complexity and objectives to the game, making ot more engaging. 

## CheckpointView.java <br>
   **Purpose:** This class was implemented to visually represent the *Checkpoint* on the game board. <br><br>
   **Implementation:** It includes logic to fetch the appropriate checkpoint image based on its id and render it at the correct position. <br><br>
   **Justification:** The visual representation of the checkpoints is crucial for the player interaction and understanding of game objectives

## BoardData.java <br>
   **Purpose:** This was implemented for storing the data of the board, such as size, walls, checkpoints, antennas etc. <br><br>
   **Implementation:** It provides methods to access and manipulate data, ensuring that the game logic operates on the correct information <br><br>
   **Justification:** It serves as a central repository for the game board information, which is crucial for the games functionality

## JsonReader.java <br>
   **Purpose:** The class was implemented to facilitate loading the game board using *JSON* <br><br>
   **Implementation:** This includes methods to read the game board from a *JSON* file.
   It creates a *BoardData* object and stores information from the *JSON* file within it.<br><br>
   **Justification:** This feature gives developers and creators an easy way to create and manage different boards for gameplay.
   This is crucial to improve the overall gameplay experience, as it improves replay value.