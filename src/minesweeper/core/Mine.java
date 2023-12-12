package minesweeper.core;

/**
 * Mine tile.
 */
public class Mine extends Tile {

    @Override
    public String toString() {
        if(this.getState() == State.OPEN) {
            return "X";
        }else return super.toString();
    }
}
