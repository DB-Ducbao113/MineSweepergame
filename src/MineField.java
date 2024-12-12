import java.util.ArrayList;
import java.util.Random;

public class MineField {
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
}