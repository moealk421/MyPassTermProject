package com.mypass;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates password strength and notifies observers of results.
 */
public class PasswordStrengthChecker {
    private List<PasswordObserver> observers = new ArrayList<>();
    private static final int MIN_LENGTH = 8;    // Minimum acceptable length
    private static final int STRONG_LENGTH = 12; // Length for bonus points
    
    public void addObserver(PasswordObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(PasswordObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Checks password and returns strength: "weak", "medium", or "strong".
     */
    public String checkPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            notifyWeakPassword("Password cannot be empty");
            return "weak";
        }
        
        int score = 0;
        List<String> warnings = new ArrayList<>();
        
        // Score based on length
        if (password.length() < MIN_LENGTH) {
            warnings.add("Password must be at least " + MIN_LENGTH + " characters long");
        } else {
            score += 2;
            if (password.length() >= STRONG_LENGTH) {
                score += 1; // Bonus for extra length
            }
        }
        
        // Score based on character variety
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9\\s].*");
        
        if (hasUppercase) score += 1;
        else warnings.add("Add uppercase letters");
        
        if (hasLowercase) score += 1;
        else warnings.add("Add lowercase letters");
        
        if (hasNumber) score += 1;
        else warnings.add("Add numbers");
        
        if (hasSpecial) score += 1;
        else warnings.add("Add special characters");
        
        // Determine overall strength
        String strength;
        if (score <= 3) {
            strength = "weak";
            notifyWeakPassword("Weak password: " + String.join(", ", warnings));
        } else if (score <= 5) {
            strength = "medium";
            if (!warnings.isEmpty()) {
                notifyWeakPassword("Password could be stronger: " + String.join(", ", warnings));
            }
        } else {
            strength = "strong";
        }
        
        notifyPasswordStrengthChange(strength);
        return strength;
    }
    
    private void notifyWeakPassword(String message) {
        for (PasswordObserver observer : observers) {
            observer.onWeakPassword(message);
        }
    }
    
    private void notifyPasswordStrengthChange(String strength) {
        for (PasswordObserver observer : observers) {
            observer.onPasswordStrengthChange(strength);
        }
    }
}
