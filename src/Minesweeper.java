import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import java.util.LinkedList;

public class Minesweeper {
    private LinkedList<MineField> undoQueue = new LinkedList<>();

    private int undoCount = 0;
    private final int MAX_UNDO = 3;

    private JFrame frame;
    private JLabel textLabel;
    private JPanel textPanel;
    private JPanel boardPanel;
    private JPanel controlPanel;
    private JTextField mineInput;
    private JButton restartButton;

    private MineField mineField;
    private GameControl gameControl;
    private int tileSize = 70;
    private int numRows = 8, numCols = 8, mineCount = 10;

    public Minesweeper() {
        mineField = new MineField(numRows, numCols, mineCount);
        gameControl = new GameControl();
        mineField.initializeBoard();

        frame = new JFrame("Minesweeper");
        frame.setSize(numCols * tileSize, numRows * tileSize + 100);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel = new JLabel("Minesweeper: " + mineCount, JLabel.CENTER);
        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textPanel = new JPanel(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        controlPanel = new JPanel(new FlowLayout());
        mineInput = new JTextField(5);
        restartButton = new JButton("Restart");
        controlPanel.add(new JLabel("Mines:"));
        controlPanel.add(mineInput);
        controlPanel.add(restartButton);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undoMove());
        controlPanel.add(undoButton);

        frame.add(controlPanel, BorderLayout.SOUTH);

        boardPanel = new JPanel(new GridLayout(numRows, numCols));
        frame.add(boardPanel, BorderLayout.CENTER);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = mineField.getBoard()[r][c];
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameControl.isGameOver()) return;

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            handleLeftClick(tile);
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            handleRightClick(tile);
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }

        restartButton.addActionListener(e -> restartGame());

        frame.setVisible(true);
        mineField.setMines();
    }

    private void saveState() {
        if (undoQueue.size() == MAX_UNDO) {
            undoQueue.removeFirst();
        }
        undoQueue.addLast(mineField.clone());
    }

    private void handleLeftClick(MineTile tile) {
        if (!tile.isEnabled() || gameControl.isGameOver()) return;

        saveState();

        if (mineField.isMine(tile.r, tile.c)) {
            revealMines();
        } else {
            revealSafeTile(tile.r, tile.c);
        }
    }

    private void handleRightClick(MineTile tile) {
        if (gameControl.isGameOver()) return;

        saveState();

        if (tile.getText().equals("") && tile.isEnabled()) {
            tile.setText("🚩");
        } else if (tile.getText().equals("🚩")) {
            tile.setText("");
        }
    }


    private void undoMove() {
        if (undoCount >= MAX_UNDO) {
            JOptionPane.showMessageDialog(frame,
                    "You have used all " + MAX_UNDO + " Undo turns!",
                    "Undo Limit",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (undoQueue.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "No more undo turns left!",
                    "Undo Limit",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        mineField = undoQueue.removeLast();
        undoCount++;
        gameControl.setGameOver(false);
        restoreBoardState();
    }


    private void restoreBoardState() {
        boardPanel.removeAll();
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = mineField.getBoard()[r][c];

                if (!tile.isEnabled()) {
                    tile.setEnabled(false);
                    int minesFound = countSurroundingMines(r, c);
                    if (mineField.isMine(r, c)) {
                        tile.setText("💣");
                    } else if (minesFound > 0) {
                        tile.setText(String.valueOf(minesFound));
                    }
                } else {
                    tile.setEnabled(true);
                    if (tile.getText().equals("🚩")) {
                        tile.setText("🚩");
                    } else {
                        tile.setText("");
                    }
                }

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameControl.isGameOver()) return;

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            handleLeftClick(tile);
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            handleRightClick(tile);
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }



    private void revealMines() {
        for (MineTile mine : mineField.getMineList()) {
            mine.setText("💣");
        }
        gameControl.setGameOver(true);
        textLabel.setText("Game Over!");
    }

    private void revealSafeTile(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return;

        MineTile tile = mineField.getBoard()[r][c];
        if (!tile.isEnabled()) return;

        tile.setEnabled(false);
        gameControl.incrementTilesClicked();

        int minesFound = countSurroundingMines(r, c);
        if (minesFound > 0) {
            tile.setText(String.valueOf(minesFound));
        } else {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr != 0 || dc != 0) {
                        revealSafeTile(r + dr, c + dc);
                    }
                }
            }
        }
        checkWinCondition();
    }


    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return 0;

        return mineField.getMineList().contains(mineField.getBoard()[r][c]) ? 1 : 0;
    }
    int countSurroundingMines(int r, int c) {
        int count = 0;

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                count += countMine(r + dr, c + dc);
            }
        }

        return count;
    }

    private void checkWinCondition() {
        int nonMineCellsRevealed = 0;
        int totalNonMineCells = numRows * numCols - mineField.getMineList().size();

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = mineField.getBoard()[r][c];
                if (!tile.isEnabled() && !mineField.getMineList().contains(tile)) {
                    nonMineCellsRevealed++;
                }
            }
        }

        if (nonMineCellsRevealed == totalNonMineCells) {
            gameControl.setGameOver(true);
            textLabel.setText("Mines Cleared!");
        }
    }

    private void restartGame() {
        try {
            int inputMineCount = Integer.parseInt(mineInput.getText());
            mineCount = Math.min(inputMineCount, numRows * numCols - 1);
        } catch (NumberFormatException e) {
            mineCount = 10;
        }

        gameControl.resetGame(mineField, textLabel);
        mineField = new MineField(numRows, numCols, mineCount);
        mineField.initializeBoard();
        mineField.setMines();

        undoQueue.clear();
        undoCount = 0;

        boardPanel.removeAll();
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = mineField.getBoard()[r][c];
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameControl.isGameOver()) return;

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            handleLeftClick(tile);
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            handleRightClick(tile);
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }



    public static void main(String[] args) {
        new Minesweeper();
    }
}
