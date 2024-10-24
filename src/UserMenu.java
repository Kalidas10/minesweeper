import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserMenu extends JFrame {
    private String userId;
    private String name;

    public UserMenu(String userId, String name) {
        this.userId = userId;
        this.name = name;

        setTitle("User Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10));

        JButton playButton = new JButton("Play Minesweeper");
        JButton editUserButton = new JButton("Edit User");
        JButton deleteUserButton = new JButton("Delete User");
        JButton logoutButton = new JButton("Logout");

        add(new JLabel("Welcome, " + name + "!"));
        add(playButton);
        add(editUserButton);
        add(deleteUserButton);
        add(logoutButton);

        playButton.addActionListener(e -> openDifficultySelection());
        editUserButton.addActionListener(e -> editUser());
        deleteUserButton.addActionListener(e -> deleteUser());
        logoutButton.addActionListener(e -> logout());

        setLocationRelativeTo(null);
    }

    private void openDifficultySelection() {
        dispose();
        new DifficultySelection().setVisible(true);
    }

    private void editUser() {
        String newName = JOptionPane.showInputDialog(this, "Enter new name:", name);
        if (newName != null && !newName.trim().isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "UPDATE users SET name = ? WHERE user_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, newName);
                pstmt.setString(2, userId);
                pstmt.executeUpdate();

                name = newName;
                JOptionPane.showMessageDialog(this, "User information updated successfully!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating user information.");
            }
        }
    }

    private void deleteUser() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete your account?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM users WHERE user_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, userId);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "User account deleted successfully!");
                logout();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting user account.");
            }
        }
    }

    private void logout() {
        dispose();
        new LoginRegisterScreen().setVisible(true);
    }
}