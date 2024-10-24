import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginRegisterScreen extends JFrame {
    private JTextField userIdField;
    private JTextField nameField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginRegisterScreen() {
        setTitle("Login/Register");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));

        userIdField = new JTextField();
        nameField = new JTextField();
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        add(new JLabel("User ID:"));
        add(userIdField);
        add(new JLabel("Name:"));
        add(nameField);
        add(loginButton);
        add(registerButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        setLocationRelativeTo(null);
    }

    private void login() {
        String userId = userIdField.getText();
        String name = nameField.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE user_id = ? AND name = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            pstmt.setString(2, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                openUserMenu(userId, name);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database.");
        }
    }

    private void register() {
        String userId = userIdField.getText();
        String name = nameField.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if user already exists
            String checkQuery = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "User ID already exists. Please choose a different one.");
                return;
            }

            // Insert new user
            String insertQuery = "INSERT INTO users (user_id, name) VALUES (?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, userId);
            insertStmt.setString(2, name);
            insertStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration successful!");
            openUserMenu(userId, name);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database.");
        }
    }

    private void openUserMenu(String userId, String name) {
        dispose();
        new UserMenu(userId, name).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginRegisterScreen().setVisible(true);
        });
    }
}