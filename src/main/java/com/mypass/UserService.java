package com.mypass;

import org.mindrot.jbcrypt.BCrypt;
import java.util.*;

/**
 * Singleton service layer for user operations.
 * Handles registration, authentication, and password updates.
 */
public class UserService {
    private static UserService instance;
    private UserDAO userDAO;
    
    private UserService() {
        this.userDAO = new UserDAO();
    }
    
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    /**
     * Register new user with hashed password.
     */
    public boolean registerUser(String email, String password, List<String> securityQuestions) {
        // Check for existing user
        if (userDAO.userExists(email)) {
            return false;
        }
        
        // Hash password with BCrypt
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(email, passwordHash, securityQuestions);
        return userDAO.createUser(user);
    }
    
    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }
    
    /**
     * Verify password against stored BCrypt hash.
     */
    public boolean verifyPassword(String email, String password) {
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            System.err.println("User not found for email: " + email);
            return false;
        }
        String storedHash = user.getPasswordHash();
        if (storedHash == null) {
            System.err.println("Password hash is null for user: " + email);
            return false;
        }
        boolean matches = BCrypt.checkpw(password, storedHash);
        if (!matches) {
            System.err.println("Password mismatch for user: " + email);
        }
        return matches;
    }
    
    public boolean userExists(String email) {
        return userDAO.userExists(email);
    }
    
    /**
     * Update password with new BCrypt hash.
     */
    public boolean updatePassword(String email, String newPassword) {
        String passwordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        return userDAO.updatePassword(email, passwordHash);
    }
}
