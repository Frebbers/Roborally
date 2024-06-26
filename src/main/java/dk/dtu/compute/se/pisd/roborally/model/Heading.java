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
 * Enum for cardinal direction a board element is facing.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public enum Heading {

    SOUTH, WEST, NORTH, EAST;

    /**
     * Go to next enum element, effectively turning the board
     * element clockwise.
     * 
     * @return the cardinal direction one step clockwise from
     * the previous.
     */
    public Heading next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    /**
     * Go to previous enum element, effectively turning the board
     * element counter-clockwise.
     * 
     * @return the cardinal direction one step counter-clockwise from
     * the previous.
     */
    public Heading prev() {
        return values()[(this.ordinal() + values().length - 1) % values().length];
    }

    /**

     Get the opposite cardinal direction.
     @return the cardinal direction opposite to the current one.*/
    public Heading opposite() {
        return values()[(this.ordinal() + 2) % values().length];}


    /**
     * Converts a string to a Heading enum.
     * @author Frederik Bode Hendrichsen s224804
     * @param input the string to be converted
     * @return the Heading enum corresponding to the input string
     * @throws IllegalArgumentException if the input string is not a valid heading
     */
    public static Heading fromString(String input) throws IllegalArgumentException{
        return switch (input) {
            case "north", "North", "NORTH" -> Heading.NORTH;
            case "south", "South", "SOUTH" -> Heading.SOUTH;
            case "east", "East", "EAST" -> Heading.EAST;
            case "west", "West", "WEST" -> Heading.WEST;
            default -> throw new IllegalArgumentException("Invalid heading");
        };
    }
}
