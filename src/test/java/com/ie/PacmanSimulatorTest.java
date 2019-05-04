package com.ie;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@code com.ie.PacmanSimulator}
 *  
 * @author sundeep
 *
 */
public class PacmanSimulatorTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private Stream<String> inputs;
    
    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        inputs = Files.lines(Paths.get("src/test/resources/Pacman.txt"));
    }

    @After
    public void tearDown() throws Exception {
        inputs.close();
        System.setOut(null);
    }

    @Test
    public final void testRunPacmanSimulator() {
        inputs.filter(s -> s != null).forEach(s -> process(s));
    }
    
    private void process(String input) {
        List<String> cmds =  Arrays.asList(input.split(":::"));
        System.out.println("\n##############################################################");
        System.out.println("Test case input : " + input);
        PacmanSimulator.runPacmanSimulator(cmds);
    }

    @Test
    public final void testPositiveCase1() {
        String in = "place 0,0,north ::: move ::: report";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Output: 0, 1, NORTH"));
    }

    @Test
    public final void testPositiveCase2() {
        String in = "place 0,0,north ::: left ::: report";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Output: 0, 0, WEST"));
    }
    
    @Test
    public final void testPositiveCase3() {
        String in = "PLACE 1,2,EAST ::: MOVE ::: MOVE ::: LEFT ::: MOVE ::: REPORT";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Output: 3, 3, NORTH"));
    }

    @Test
    public final void testPositiveCase4() {
        String in = "place 1,1,east ::: move ::: move ::: left ::: right ::: move ::: move ::: left ::: place 3,4,south ::: move ::: left ::: left ::: move ::: report";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Output: 3, 4, NORTH"));
    }

    @Test
    public final void testPositiveCase5() {
        String in = "place 2 , 3 , south ::: report";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Output: 2, 3, SOUTH"));
    }

    @Test
    public final void testPositiveCase6() {
        String in = "PlAcE 3 , 1 , EaST ::: MOve ::: REPOrt";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Output: 4, 1, EAST"));
    }

    @Test
    public final void testPositiveCase7() {
        String in = "place 6,6,north ::: move ::: left ::: place 2,1, east ::: move ::: left ::: move ::: right ::: move ::: report";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Co-ordinates [6, 6] are outside the grid. Ignoring PLACE command"));
        assertTrue(outContent.toString().trim().contains("Output: 4, 2, EAST"));
    }

    @Test
    public final void testPositiveCase8() {
        String in = "test ::: place 0,0,EAst ::: move ::: left ::: invalid ::: move ::: report";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Ignoring invalid command : TEST"));
        assertTrue(outContent.toString().trim().contains("Ignoring invalid command : INVALID"));
        assertTrue(outContent.toString().trim().contains("Output: 1, 1, NORTH"));
    }

    @Test
    public final void testPositiveCase10() {
        String in = "place 4,4,north ::: move ::: move ::: left ::: left ::: move ::: move ::: report";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Ignoring commmand 3.MOVE at [4, 5, NORTH] as Pacman goes outside the grid"));
        assertTrue(outContent.toString().trim().contains("Output: 4, 3, SOUTH"));
    }

    @Test
    public final void testNegativeCase1() {
        String in = "place 6,6,north ::: move ::: left ::: report";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Co-ordinates [6, 6] are outside the grid. Ignoring PLACE command"));
    }

    @Test
    public final void testNegativeCase2() {
        String in = "place a,b,north ::: move ::: move ::: left ::: report";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Co-ordinates [A, B] are not numeric. Ignoring PLACE command"));
        assertTrue(outContent.toString().trim().contains("Ignoring MOVE command before a valid PLACE command"));
        assertTrue(outContent.toString().trim().contains("Ignoring LEFT command before a valid PLACE command"));
        assertTrue(outContent.toString().trim().contains("Ignoring REPORT command before a valid PLACE command"));
    }

    @Test
    public final void testNegativeCase3() {
        String in = "place 1 2 east ::: move ::: right ::: report";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Ignoring invalid command : PLACE 1 2 EAST"));
        assertTrue(outContent.toString().trim().contains("Ignoring MOVE command before a valid PLACE command"));
        assertTrue(outContent.toString().trim().contains("Ignoring RIGHT command before a valid PLACE command"));
        assertTrue(outContent.toString().trim().contains("Ignoring REPORT command before a valid PLACE command"));
    }

    @Test
    public final void testNegativeCase4() {
        String in = "place 1,2,NNN ::: move ::: left ::: report";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Direction [NNN] is invalid. Ignoring PLACE command"));
        assertTrue(outContent.toString().trim().contains("Ignoring MOVE command before a valid PLACE command"));
        assertTrue(outContent.toString().trim().contains("Ignoring LEFT command before a valid PLACE command"));
        assertTrue(outContent.toString().trim().contains("Ignoring REPORT command before a valid PLACE command"));
    }

    @Test
    public final void testNegativeCase5() {
        String in = "test ::: invalid ::: report";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("No PLACE command in input. Aborting processing.."));
        assertTrue(outContent.toString().trim().contains("Ignoring invalid command : TEST"));
        assertTrue(outContent.toString().trim().contains("Ignoring invalid command : INVALID"));
    }

    @Test
    public final void testNegativeCase6() {
        String in = "report :::";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("No PLACE command in input. Aborting processing.."));
        assertTrue(outContent.toString().trim().contains("Ignoring REPORT command before a valid PLACE command"));
    }

    @Test
    public final void testNegativeCase7() {
        String in = "test ::: invalid :::";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("No PLACE command in input. Aborting processing.."));
    }

    @Test
    public final void testNegativeCase8() {
        String in = "place -1,-1,east ::: mvoe ::: move ::: report";
        List<String> cmds =  Arrays.asList(in.split(":::"));
        PacmanSimulator.runPacmanSimulator(cmds);
        assertTrue(outContent.toString().trim().contains("Co-ordinates [-1, -1] are not numeric. Ignoring PLACE command"));
        assertTrue(outContent.toString().trim().contains("Ignoring MOVE command before a valid PLACE command"));
        assertTrue(outContent.toString().trim().contains("Ignoring REPORT command before a valid PLACE command"));
    }
}
