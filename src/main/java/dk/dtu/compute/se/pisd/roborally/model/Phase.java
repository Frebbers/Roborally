/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model;

/**
 * Enum for the different phases of the game.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public enum Phase {
    INITIALISATION, PROGRAMMING, ACTIVATION, PLAYER_INTERACTION;

    /**
     * Convert a string to a phase.
     * @param input the string to be converted.
     * @return the phase represented by the string, or null if the string does not represent a phase.
     * @author Frederik Bode Hendrichsen s224804
     */
    public static Phase fromString(String input){
        return switch (input) {
            case "INITIALISATION" -> Phase.INITIALISATION;
            case "PROGRAMMING" -> Phase.PROGRAMMING;
            case "ACTIVATION" -> Phase.ACTIVATION;
            case "PLAYER_INTERACTION" -> Phase.PLAYER_INTERACTION;
            default -> null;
        };
    }
}

