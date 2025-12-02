package com.mypass;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for user persistence operations.
 */
public class UserDAO {
    private DatabaseConnection dbConnection;
    
    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Create a new user in the database.
     */
    public boolean createUser(User user) {
        // Verify table exists before insert
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeQuery("SELECT 1 FROM users LIMIT 1");
        } catch (SQLException e) {
            System.err.println("Users table does not exist or is not accessible. Error: " + e.getMessage());
            try {
                DatabaseConnection.getInstance(); // Attempt reinitialization
            } catch (Exception ex) {
                System.err.println("Failed to initialize database: " + ex.getMessage());
                return false;
            }
        }
        
        String sql = "INSERT INTO users (email, password_hash, security_question_1, security_question_2, security_question_3) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            List<String> questions = user.getSecurityQuestions();
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, questions != null && questions.size() > 0 ? questions.get(0) : null);
            pstmt.setString(4, questions != null && questions.size() > 1 ? questions.get(1) : null);
            pstmt.setString(5, questions != null && questions.size() > 2 ? questions.get(2) : null);
            
            System.out.println("Attempting to insert user: " + user.getEmail());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User created successfully: " + user.getEmail());
                return true;
            } else {
                System.err.println("No rows affected when creating user: " + user.getEmail());
                return false;
            }
            
        } catch (SQLException e) {
            // Check for duplicate key error
            String errorMsg = e.getMessage();
            if (e.getErrorCode() == 23505 || e.getErrorCode() == 23000 || 
                errorMsg != null && (errorMsg.contains("PRIMARY KEY") || 
                errorMsg.contains("already exists") || errorMsg.contains("duplicate") ||
                errorMsg.contains("Unique index") || errorMsg.contains("unique constraint"))) {
                System.err.println("User already exists: " + user.getEmail());
                return false;
            }
            System.err.println("SQL Error creating user: " + errorMsg);
            System.err.println("SQL Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error creating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieve user by email address.
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT email, password_hash, security_question_1, security_question_2, security_question_3 " +
                     "FROM users WHERE email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Build security questions list
                List<String> questions = new ArrayList<>();
                questions.add(rs.getString("security_question_1"));
                questions.add(rs.getString("security_question_2"));
                questions.add(rs.getString("security_question_3"));
                
                String userEmail = rs.getString("email");
                User user = new User(
                    userEmail,
                    rs.getString("password_hash"),
                    questions
                );
                
                // Initialize vault with user email for database loading
                Vault vault = new Vault(userEmail);
                user.setVault(vault);
                return user;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Check if user exists by email.
     */
    public boolean userExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update user's password hash.
     */
    public boolean updatePassword(String email, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, passwordHash);
            pstmt.setString(2, email);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
