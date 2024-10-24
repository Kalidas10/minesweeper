import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DifficultySelection extends JFrame {

    public DifficultySelection() {
        setTitle("Select Difficulty");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JButton easyButton = new JButton("Easy (8x8, 10 Mines)");
        JButton mediumButton = new JButton("Medium (16x16, 40 Mines)");
        JButton hardButton = new JButton("Hard (24x24, 99 Mines)");
        JButton backButton = new JButton("Back to User Menu");

        easyButton.setBounds(50, 30, 200, 30);
        mediumButton.setBounds(50, 70, 200, 30);
        hardButton.setBounds(50, 110, 200, 30);
        backButton.setBounds(50, 150, 200, 30);

        easyButton.addActionListener(e -> startGame(8, 10));
        mediumButton.addActionListener(e -> startGame(16, 40));
        hardButton.addActionListener(e -> startGame(24, 99));
        backButton.addActionListener(e -> backToUserMenu());

        add(easyButton);
        add(mediumButton);
        add(hardButton);
        add(backButton);

        setLocationRelativeTo(null);
    }

    private void startGame(int size, int mines) {
        new Thread(() -> {
            Main game = new Main(size, mines);
        }).start();
        dispose();
    }

    private void backToUserMenu() {
        dispose();
        new UserMenu("", "").setVisible(true); // You might want to pass the actual user ID and name here
    }
}