import java.util.ArrayList;
import java.util.Random;

public class MineField implements Cloneable {
    private MineTile[][] board;
    private ArrayList<MineTile> mineList;
    private int numRows, numCols, mineCount;
    private Random random;

    public MineField(int numRows, int numCols, int mineCount) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.mineCount = mineCount;
        board = new MineTile[numRows][numCols];
        mineList = new ArrayList<>();
        random = new Random();
    }

    public void initializeBoard() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                board[r][c] = new MineTile(r, c);
            }
        }
    }

    public void setMines() {
        mineList.clear();
        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft--;
            }
        }
    }

    public boolean isMine(int r, int c) {
        return mineList.contains(board[r][c]);
    }

    public MineTile[][] getBoard() {
        return board;
    }

    public ArrayList<MineTile> getMineList() {
        return mineList;
    }

    @Override
    public MineField clone() {
        try {
            MineField cloned = (MineField) super.clone();
            cloned.board = new MineTile[numRows][numCols];
            cloned.mineList = new ArrayList<>();

            for (int r = 0; r < numRows; r++) {
                for (int c = 0; c < numCols; c++) {
                    MineTile originalTile = this.board[r][c];
                    MineTile clonedTile = new MineTile(originalTile.r, originalTile.c);
                    clonedTile.setText(originalTile.getText());
                    clonedTile.setEnabled(originalTile.isEnabled());
                    cloned.board[r][c] = clonedTile;

                    if (this.mineList.contains(originalTile)) {
                        cloned.mineList.add(clonedTile);
                    }
                }
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }

}
