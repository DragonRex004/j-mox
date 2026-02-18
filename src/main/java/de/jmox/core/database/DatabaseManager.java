package de.jmox.core.database;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private final String URL = "jdbc:sqlite:jmox.db";
    private Connection conn;

    public void init() {
        try {
            this.conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL);";
            stmt.execute(sql);

            String checkSql = "SELECT count(*) FROM users WHERE username = 'admin'";
            try (ResultSet rs = stmt.executeQuery(checkSql)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    createUser( "admin", "admin");
                    System.out.println("[DB] Default-Admin is created.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createUser(String user, String pass) {
        try {
            String hashed = BCrypt.hashpw(pass, BCrypt.gensalt());
            String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
            try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
                pstmt.setString(1, user);
                pstmt.setString(2, hashed);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteUser(String user) {
        if(user.equals("admin")) return;
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateUserPassword(String username, String newPass) {
        String hashed = BCrypt.hashpw(newPass, BCrypt.gensalt());
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashed);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<String> getAllUsernames() {
        List<String> users = new ArrayList<>();
        String sql = "SELECT username FROM users";
        try (Connection conn = DriverManager.getConnection(URL);
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) { users.add(rs.getString("username")); }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    public boolean checkLogin(String user, String pass) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return BCrypt.checkpw(pass, rs.getString("password"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public Connection getConn() {
        return conn;
    }
}
