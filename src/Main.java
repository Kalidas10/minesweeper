import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Main extends JFrame {
    private int size; // Board size
    private int mines; // Number of mines
    private JButton[][] buttons; // Button grid
    private boolean[][] minesGrid;
    private boolean[][] revealed;
    private boolean[][] flagged;
    private boolean gameOver = false;
    private int movesLeft;

    public Main(int size, int mines) {
        this.size = size;
        this.mines = mines;
        this.buttons = new JButton[size][size];
        this.minesGrid = new boolean[size][size];
        this.revealed = new boolean[size][size];
        this.flagged = new boolean[size][size];
        this.movesLeft = (size * size) - mines;

        initUI();
        initBoard();
        placeMines();
//        addBackButton();
    }

//    private void addBackButton() {
//        JButton backButton = new JButton("Back to User Menu");
//        backButton.addActionListener(e -> backToUserMenu());
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.add(backButton);
//        add(buttonPanel, BorderLayout.SOUTH);
//    }

//    private void backToUserMenu() {
//        dispose();
//        new UserMenu("", "").setVisible(true); // You might want to pass the actual user ID and name here
//    }

    private void initUI() {
        setTitle("Minesweeper");

        // Dynamically adjust window size based on grid size
        int windowSize = size * 50; // Each button will roughly be 50x50
        setSize(windowSize, windowSize);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(size, size));

        // Adjust button size and font size dynamically
        int buttonSize = Math.max(50 - (size / 4), 25);  // Adjust size for larger grids
        int fontSize = Math.max(20 - (size / 8), 12);    // Adjust font size for readability

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setPreferredSize(new Dimension(buttonSize, buttonSize));
                buttons[row][col].setFont(new Font("Arial", Font.BOLD, fontSize));
                buttons[row][col].setBackground(Color.LIGHT_GRAY);
                buttons[row][col].addMouseListener(new ButtonClickListener(row, col));
                add(buttons[row][col]);
            }
        }

        setVisible(true);
    }

    private void initBoard() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                revealed[row][col] = false;
                flagged[row][col] = false;
            }
        }
    }

    private void placeMines() {
        Random rand = new Random();
        int placedMines = 0;

        while (placedMines < mines) {
            int row = rand.nextInt(size);
            int col = rand.nextInt(size);

            if (!minesGrid[row][col]) {
                minesGrid[row][col] = true;
                placedMines++;
            }
        }
    }

    private class ButtonClickListener extends MouseAdapter {
        private int row;
        private int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (gameOver || revealed[row][col]) {
                return;
            }

            if (SwingUtilities.isRightMouseButton(e)) {
                // Handle right-click (flag/unflag)
                if (!revealed[row][col]) {
                    if (!flagged[row][col]) {
                        flagged[row][col] = true;
                        buttons[row][col].setText("F");
                        buttons[row][col].setForeground(Color.BLUE);
                        buttons[row][col].setBackground(Color.YELLOW);
                    } else {
                        flagged[row][col] = false;
                        buttons[row][col].setText("");
                        buttons[row][col].setBackground(Color.LIGHT_GRAY);
                    }
                }
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                // Handle left-click (reveal)
                if (flagged[row][col]) {
                    return; // Do nothing if the cell is flagged
                }

                if (minesGrid[row][col]) {
                    revealMines();
                    buttons[row][col].setBackground(Color.RED);
                    buttons[row][col].setText("X");
                    buttons[row][col].setForeground(Color.WHITE);
                    gameOver = true;
                    showGameOverDialog(false); // Game over, player lost
                } else {
                    revealCell(row, col);
                    if (checkWin()) {
                        gameOver = true;
                        showGameOverDialog(true); // Player won
                    }
                }
            }
        }
    }

    private void revealCell(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size || revealed[row][col] || flagged[row][col]) {
            return;
        }

        revealed[row][col] = true;
        movesLeft--;
        int mineCount = countMines(row, col);
        buttons[row][col].setEnabled(false);
        buttons[row][col].setBackground(Color.WHITE);

        if (mineCount > 0) {
            buttons[row][col].setText(String.valueOf(mineCount));
            buttons[row][col].setForeground(getNumberColor(mineCount));
        } else {
            buttons[row][col].setText("");
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    revealCell(row + i, col + j);
                }
            }
        }
    }

    private Color getNumberColor(int number) {
        switch (number) {
            case 1:
                return Color.BLUE;
            case 2:
                return new Color(0, 128, 0); // Dark Green
            case 3:
                return Color.RED;
            case 4:
                return new Color(128, 0, 128); // Purple
            case 5:
                return new Color(128, 0, 0); // Maroon
            case 6:
                return Color.CYAN;
            case 7:
                return Color.BLACK;
            case 8:
                return Color.GRAY;
            default:
                return Color.BLACK;
        }
    }

    private int countMines(int row, int col) {
        int mineCount = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;

                if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size && minesGrid[newRow][newCol]) {
                    mineCount++;
                }
            }
        }

        return mineCount;
    }

    private void revealMines() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (minesGrid[row][col]) {
                    buttons[row][col].setText("X");
                    buttons[row][col].setForeground(Color.WHITE);
                    buttons[row][col].setBackground(Color.RED);
                }
            }
        }
    }

    private boolean checkWin() {
        return movesLeft == 0;
    }

    private void showGameOverDialog(boolean won) {
        String message = won ? "Congratulations! You won! Play again?" : "Game Over! You lost. Play again?";
        int choice = JOptionPane.showOptionDialog(
                this,
                message,
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Play Again", "Exit"},
                "Play Again"
        );

        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0); // Exit the game
        }
    }

    private void restartGame() {
        this.dispose(); // Close the current game window
        DifficultySelection selectionScreen = new DifficultySelection(); // Open difficulty selection again
        selectionScreen.setVisible(true);
    }

    public static void main(String[] args) {
        // Do nothing here as the game starts from DifficultySelection
    }
}
