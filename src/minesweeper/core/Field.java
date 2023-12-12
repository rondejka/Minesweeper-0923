package minesweeper.core;

import java.util.Formatter;
import java.util.Random;

// Field represents playing field and game logic.
public class Field {

    private final Tile[][] tiles;   // Playing field tiles.
    private final int rowCount;     // Rows are indexed from 0 to (rowCount - 1).
    private final int columnCount;
    private final int mineCount;
    private GameState state = GameState.PLAYING;
    private final Random random = new Random();

    public Field(int rowCount, int columnCount, int mineCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.mineCount = mineCount;
        tiles = new Tile[rowCount][columnCount];

        //generate the field content
        generate();

    }

    public String toString() {

        Formatter f = new Formatter();
        String format = "%3s";
        if (columnCount >= 11) {
            format = "%4s";
        }

        f.format(format, "");
        for (int r = 0; r < columnCount; r++) {
            f.format(format, r);
        }
        f.format("%n");


        for (int row = 0; row < rowCount; row++) {
            f.format(format, (char)(row + 65));
            for (int col = 0; col < columnCount; col++) {
                f.format(format, tiles[row][col]);
            }
            f.format("%n");
        }
        f.format("%n");
        return f.toString();
    }


    public void openTile(int row, int column) {
        Tile tile = tiles[row][column];
        if (tile.getState() == Tile.State.CLOSED) {
            tile.setState(Tile.State.OPEN);

            if (tile instanceof Mine) {
                state = GameState.FAILED;
                return;
            }
            //else

            openAdjacentTiles(row, column);

            if (isSolved()) {
                state = GameState.SOLVED;
                return;
            }
        }
    }


    private void openAdjacentTiles(int row, int column) {
        if (tiles[row][column] instanceof Clue) {
            if (((Clue) tiles[row][column]).getValue() == 0) {
                openLeft(row, column);
                openRight(row, column);
                openUp(row, column);
                openDown(row, column);
            }
        }
    }


    private void openLeft(int row, int column) {
        if (column > 0) {
            if (tiles[row][column-1].getState() == Tile.State.CLOSED) {
                tiles[row][column-1].setState(Tile.State.OPEN);
                openAdjacentTiles(row, column-1);
            }
        }
    }

    private void openRight(int row, int column) {
        if (column < columnCount - 1) {
            if (tiles[row][column + 1].getState() == Tile.State.CLOSED) {
                tiles[row][column + 1].setState(Tile.State.OPEN);
                openAdjacentTiles(row, column + 1);
            }
        }
    }

    private void openUp(int row, int column) {
        if (row > 0) {
            if (tiles[row - 1][column].getState() == Tile.State.CLOSED) {
                tiles[row - 1][column].setState(Tile.State.OPEN);
                openAdjacentTiles(row - 1, column);
            }
        }
    }

    private void openDown(int row, int column) {
        if (row < rowCount - 1) {
            if (tiles[row + 1][column].getState() == Tile.State.CLOSED) {
                tiles[row + 1][column].setState(Tile.State.OPEN);
                openAdjacentTiles(row + 1, column);
            }
        }
    }


    public void markTile(int row, int column) {
        Tile tile = tiles[row][column];

        if (tile.getState() == Tile.State.CLOSED) {
            tile.setState(Tile.State.MARKED);
        } else if (tile.getState() == Tile.State.MARKED) {
            tile.setState(Tile.State.CLOSED);
        }
    }

    /**
     * Generates playing field.
     */
    private void generate() {
        generateMines();
        generateClues();
    }

    private void generateClues() {
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                if (tiles[row][col] == null) {
                    tiles[row][col] = new Clue(countAdjacentMines(row, col));
                }
            }
        }
    }

    private void generateMines() {

        int insertedMines = 0;
        while (insertedMines < mineCount) {
            int randomRow = random.nextInt(rowCount);
            int randomCol = random.nextInt(columnCount);
            if (tiles[randomRow][randomCol] == null) {
                tiles[randomRow][randomCol] = new Mine();
                insertedMines++;
            }
        }
    }

    /**
     * Returns true if game is solved, false otherwise.
     */
    private boolean isSolved() {

        return  ((rowCount * columnCount) - getNumberOf(Tile.State.OPEN) == mineCount);

    }

    /**
     * Returns number of adjacent mines for a tile at specified position in the field.
     */
    private int countAdjacentMines(int row, int column) {
        int count = 0;
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            int actRow = row + rowOffset;
            if (actRow >= 0 && actRow < rowCount) {
                for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
                    int actColumn = column + columnOffset;
                    if (actColumn >= 0 && actColumn < columnCount) {
                        if (tiles[actRow][actColumn] instanceof Mine) {
                            count++;
                        }
                    }
                }
            }
        }

        return count;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getMineCount() {
        return mineCount;
    }

    public GameState getState() {
        return state;
    }

    public Tile getTile(int row, int column) {
        return tiles[row][column];
    }

    public int getNumberOf(Tile.State state) {
        int count = 0;
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                if (tiles[r][c].getState() == state) {
                    count++;
                }
            }

        }
        return count;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public int getRemainingMineCount() {
        return (getMineCount() - getNumberOf(Tile.State.MARKED));
    }
}
