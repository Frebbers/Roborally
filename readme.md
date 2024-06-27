# Installation
Clone the two repositories:

```git clone https://github.com/Frebbers/Roborally.git```

```git clone https://github.com/Frebbers/roboAPI.git```

Run the main method in the RoboApiApplication class in roboAPI in IntelliJ, OR use mvn run inside the repository directory to launch with maven.

and afterwards run the main method in the StartRoboRally class in Roborally project. 

IMPORTANT: If you wish to run more than one instance of Roborally on a single machine, you must create one copy of the repository for each instance you wish to run as the client information is stored inside the properties file during runtime.

#  Project description
This software project is a part of the Advanced Programming course at DTU and is being developed by group 7. 
It is an attempt to create a version of Roborally in Java which can be run on a PC. 
The game can be played on a variety of maps with a variety of cards that affect gameplay.
Roborally is a game about planning and traversing a map full of obstacles. 
The game consists of 3 phases iterated through until a player reaches all checkpoints in the correct order.

The phases the game consists of are:
* The upgrade phase.

In the upgrade phase, players have the option of buying upgrades for their robot, 
enhancing their capabilities or giving them new options. Upgrades are
purchased with energy cubes which can be collected on the playing field.

* The programming phase

The programming phase is where players use programming cards to determine 
what the robot will do during the activation phase.

- The activation phase

During the activation phase, robots take turn applying their programming cards 	which are kept in registers.
The first robot applies the card in its first register, the second in its first register and so on.

As mentioned before, these 3 phases repeat until a robot has reached all checkpoints on the map in the correct order, at which point that robot wins the game.
