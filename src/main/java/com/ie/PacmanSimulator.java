package com.ie;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A simple command line based Pacman simulator
 * 
 * Details available at - https://github.com/ie/Code-Challenge-1
 * 
 * @author sundeep
 *
 */
public class PacmanSimulator {

    public enum Command {
        PLACE,
        MOVE,
        LEFT,
        RIGHT,
        REPORT;
    }
    
    public enum Direction {
        EAST,
        WEST,
        NORTH,
        SOUTH;

        Direction turnLeft(Direction d) {
            switch (d) {
                case EAST:  return Direction.NORTH; 
                case WEST:  return Direction.SOUTH;
                case NORTH:  return Direction.WEST;
                case SOUTH:  return Direction.EAST;
            }
            return null;
        }

        Direction turnRight(Direction d) {
            switch (d) {
                case EAST:  return Direction.SOUTH; 
                case WEST:  return Direction.NORTH;
                case NORTH:  return Direction.EAST;
                case SOUTH:  return Direction.WEST;
            }
            return null;
        }
    }
    
    private static final int GRID_SIZE = 5;
    private static final int MAXIMUM_COMMANDS = 30;

    public static void main(String[] args) {
        List<String> tokens = new ArrayList<String>();
        System.out.println("Enter the commands: ");
        try (Scanner in = new Scanner(new BufferedInputStream(System.in))) {
            while (in.hasNext()) {
                if (tokens.size() >= MAXIMUM_COMMANDS) {
                    System.out.println(String.format("Exceeded maximum no of commands [%d]. Aborting user input. Continuing processing...", MAXIMUM_COMMANDS));
                    break;
                }
                String token = in.nextLine();
                if (!token.isEmpty()) {
                    tokens.add(token.toUpperCase());
                }
                if ("REPORT".equals(token.toUpperCase())) {
                    break;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        runPacmanSimulator(tokens);
    }

    /**
     * Main method which triggers the processing. It does -
     * - Basic validation
     * - Sanitize inputs
     * - Triggers processing the commands
     */
    public static void runPacmanSimulator(List<String> cmds) {
        List<String> tokens = cmds.stream().map(String::trim).map(String::toUpperCase).collect(Collectors.toList());
        validateInput(tokens);
        List<String> finalCmds = sanitizeInput(tokens);

        if (finalCmds.isEmpty()) {
            System.out.println("Nothing to process. Aborting...");
            return;
        }

        System.out.println("\nList of valid commands to be processed:");
        int cnt = 1;
        for (String cmd : finalCmds) {
            System.out.print(String.format("%d.%s      ", cnt, cmd));
            cnt++;
        }
        System.out.println();System.out.println();
        
        processCommands(finalCmds);
    }

    /**
     * Executes the inputs provided. This gets fully sanitized
     * and validated input
     */
    private static void processCommands(List<String> finalCmds) {
        int xpos = 0, ypos = 0, cnt = 1; 
        Direction curDir = null;
        for (String token : finalCmds) {
            String cmd;
            if (token.contains(",")) {
                cmd = token.substring(0, token.indexOf(" "));
            } else {
                cmd = token;
            }
            switch (Command.valueOf(cmd)) {
                case PLACE:
                    String[] coords = token.substring(token.indexOf(" ")).split(",");
                    xpos = Integer.valueOf(coords[0].trim());
                    ypos = Integer.valueOf(coords[1].trim());
                    String dir = coords[2].trim();
                    curDir = Direction.valueOf(dir);
                    break;

                case MOVE:
                    boolean beyondGrid = false;
                    if ((Direction.EAST.equals(curDir) || Direction.WEST.equals(curDir)) &&
                            isBeyondGrid(xpos, curDir)) {
                        beyondGrid = true;
                    }
                    if ((Direction.NORTH.equals(curDir) || Direction.SOUTH.equals(curDir)) &&
                            isBeyondGrid(ypos, curDir)) {
                        beyondGrid = true;
                    }
                    if (beyondGrid) {
                        System.out.println(String.format("Ignoring commmand %d.%s at [%d, %d, %s] as Pacman goes outside the grid[%dx%d]", 
                                cnt, cmd, xpos, ypos, curDir, GRID_SIZE, GRID_SIZE));
                        continue;
                    }
                    switch (curDir) {
                        case EAST:
                            xpos++;
                            break;
                        case WEST:
                            xpos--;
                            break;
                        case NORTH:
                            ypos++;
                            break;
                        case SOUTH:
                            ypos--;
                            break;
                        default:
                            break;
                    }
                    break;

                case LEFT:
                    curDir = curDir.turnLeft(curDir);
                    break;

                case RIGHT:
                    curDir = curDir.turnRight(curDir);
                    break;

                case REPORT:
                    System.out.println(String.format("\nOutput: %d, %d, %s\n", xpos, ypos, curDir));
                    break;

                default:
                    break;
            }
            cnt++;
        }
    }

    /**
     * Performs basic validation 
     * - checks if PLACE and REPORT commands are present
     * - limits the no of commands that can be provided (limited to 10)
     */
    private static void validateInput(List<String> tokens) {
        if (tokens.size() > MAXIMUM_COMMANDS) {
            System.out.println(String.format("Exceeded maximum no of commands [%d]. Aborting user input. Continuing processing...", MAXIMUM_COMMANDS));
        }
        boolean hasPlace = false;
        for (String token : tokens) {
            if (token.contains("PLACE")) {
                hasPlace = true;
                break;
            }
        }
        if (!hasPlace) {
            System.out.println("No PLACE command in input. Aborting processing..");
            return; 
        }
        if (!tokens.contains("REPORT")) {
            System.out.println("No REPORT command in input. Aborting processing...");
            return;
        }
    }

    /**
     * Sanitizes the input -
     *      - checks if all command are valid, ignores invalid commands
     *      - validates PLACE command
     * Returns the final set of commands to be executed
     * 
     */
    private static List<String> sanitizeInput(List<String> tokens) {
        List<String> finalCmds = new ArrayList<String>();
        boolean isPlaced = false;
        for (String token : tokens) {
            String cmd;
            if (token.contains(",")) {
                cmd = token.substring(0, token.indexOf(" "));
            } else {
                cmd = token;
            }
            if (!isValidCommand(cmd)) {
                System.out.println("Ignoring invalid command : " + token);
                continue;
            }
            if (cmd.startsWith("PLACE")) {
                if (isValidPlaceCommand(token)) {
                    isPlaced = true;
                } else {
                    continue;
                }
            } else if (!"PLACE".equals(cmd) && !isPlaced) {
                System.out.println("Ignoring " + token + " command before a valid PLACE command");
                continue;
            } 
            finalCmds.add(token);
        }
        return finalCmds;
    }

    /**
     * Checks if an input command is valid
     * 
     */
    @SuppressWarnings("unused")
    private static boolean isValidCommand(String command) {
        try {
            Command c = Command.valueOf(command);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Checks if the PLACE command is valid
     * - checks if syntax of PLACE command is correct
     * - checks if initial coordinates of PLACE command are in the grid
     * - checks if the directions are valid
     */
    private static boolean isValidPlaceCommand(String token) {
        boolean isValid = true;
        String[] coords = token.substring(token.indexOf(" ")).split(",");
        if (coords.length != 3) {
            System.out.println("Invalid PLACE command. Usage: PLACE x,y,F");
            isValid = false;
        }
        if (!isNumeric(coords[0].trim()) || !isNumeric(coords[1].trim())) {
            System.out.println(String.format("Co-ordinates [%s, %s] are not valid. Ignoring PLACE command",
                               coords[0].trim(), coords[1].trim()));
            isValid = false;;
        } else {
            int xpos = Integer.valueOf(coords[0].trim());
            int ypos = Integer.valueOf(coords[1].trim());
            if (!isValidCoordinates(xpos, ypos)) {
                System.out.println(String.format("Co-ordinates [%d, %d] are outside the grid. Ignoring PLACE command", xpos, ypos));
                isValid = false;;
            }
        }
        String dir = coords[2].trim();
        if (!isValidDirection(dir)) {
            System.out.println(String.format("Direction [%s] is invalid. Ignoring PLACE command", dir));
            isValid = false;;
        }
        return isValid;
    }
    
    /**
     * Checks if a given direction is valid
     * 
     */
    @SuppressWarnings("unused")
    private static boolean isValidDirection(String direction) {
        try {
            Direction d = Direction.valueOf(direction);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Checks if a given set of coordinates are in the grid [5x5]
     * 
     */
    private static boolean isValidCoordinates(int xpos, int ypos) {
        if (xpos < 0 || xpos > GRID_SIZE || 
                ypos < 0 || ypos > GRID_SIZE) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether for a given position and direction, 
     * if the next MOVE command is valid
     * 
     */
    private static boolean isBeyondGrid(int pos, Direction curDir) {
        if ((Direction.EAST.equals(curDir) || Direction.NORTH.equals(curDir))
                && ++pos > GRID_SIZE) {
            return true;
        }
        if ((Direction.WEST.equals(curDir) || Direction.SOUTH.equals(curDir))
                && --pos < 0) {
            return true;
        }
        return false;
    }
    
    /**
     * Checks if given string is numeric
     * 
     */
    private static boolean isNumeric(String s) {
        return (s.chars().allMatch(Character::isDigit));
    }

}