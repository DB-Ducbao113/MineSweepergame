import javax.swing.*;

public class GameControl {
    private boolean gameOver = false;
    private int tilesClicked = 0;

    public void resetGame(MineField field, JLabel textLabel) {
        gameOver = false;
        tilesClicked = 0;
        field.setMines();
        textLabel.setText("Minesweeper: " + field.getMineList().size());
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean state) {
        this.gameOver = state;
    }

    public void incrementTilesClicked() {
        tilesClicked++;
    }

    public int getTilesClicked() {
        return tilesClicked;
    }

}
