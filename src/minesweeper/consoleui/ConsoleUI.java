package minesweeper.consoleui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import minesweeper.core.Field;
import minesweeper.core.GameState;
import minesweeper.core.Tile;

/**
 * Console user interface.
 */
public class ConsoleUI {
    /**
     * Playing field.
     */
    private Field field;

    /**
     * Input reader.
     */
    private BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    private Pattern pattern = Pattern.compile("[OM]([A-Z])([0-9])|[E]");

    /**
     * Reads line of text from the reader.
     *
     * @return line as a string
     */
    private String readLine() {
        try {
            return input.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Starts the game.
     *
     * @param field field of mines and clues
     */
    public void newGameStarted(Field field) {
        this.field = field;

        update();
        do {
            try {
                processInput();
            } catch (WrongFormatException e) {
                //System.out.println("Nespravny rozsah hracieho pola.");
                System.out.println(e.getMessage());
                //System.out.println(System.err);
                //e.printStackTrace();
                //throw new RuntimeException(e);
            }
            update();


            if (this.field.getState() == GameState.SOLVED) {
                System.out.println("YOU WON!");
                return;
            }
            if (this.field.getState() == GameState.FAILED) {
                System.out.println("YOU FAILED!");
                return;
            }

        } while (field.getState() == GameState.PLAYING);

    }

    /**
     * Updates user interface - prints the field.
     */
    public void update() {

        System.out.print(this.field);

        System.out.println("Remaining mines: " + field.getRemainingMineCount() + "\n");

    }


    /**
     * Processes user input.
     * Reads line from console and does the action on a playing field according to input string.
     */
    private void processInput() throws WrongFormatException {
        System.out.println(inputDescr());

        String input = readLine().trim().toUpperCase();     //trim oseka medzery na zaciatku a konci retazca; toUpperCase zabezpeci, ze vstup sa bude dat zadavat aj malymi pismenami


        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            System.out.println("Výraz NEsplňuje pravida.");
            return;
        }


        int inputRow = 0;
        int inputColumn = 0;
        String command = input.substring(0,1);

        if (!command.equals("E")) {
            inputRow = matcher.group(1).charAt(0) - 'A';
            inputColumn = Integer.parseInt(matcher.group(2));
        }

        if (!isInputInBorderOfField(inputRow, inputColumn)){
            //System.out.println("Nespravny rozsah hracieho pola.");
            throw new WrongFormatException("Nespravny rozsah hracieho pola.");
            //return;
        }

        if (input.startsWith("O")) {
            this.field.openTile(inputRow, inputColumn);
        }
        if (input.startsWith("M")) {
            this.field.markTile(inputRow, inputColumn);
        }
        if (input.startsWith("E")) {
            this.field.setState(GameState.FAILED);
        }

    }


    private String inputDescr() {
        String h;
        h = "Input: Operation Row Column \n";
        h += "Operation: M - mark, O - open, E - exit\n";
        h += "Row: A - " + (char) (field.getRowCount() + 65 - 1) + "\n";
        h += "Column: 0 - " + (field.getColumnCount() - 1) + "\n";
        h += "e.g. OB2 \n";

        return h;
    }

    void handleInput(String input) //throws WrongFormatException
     {

    }

    private boolean isInputInBorderOfField(int row, int col) {
        return (col < field.getColumnCount()) && (row < field.getRowCount());
    }
}
