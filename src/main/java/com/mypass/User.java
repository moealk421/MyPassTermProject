package com.mypass;

import java.util.List;

/**
 * Represents a registered user with credentials and vault.
 */
public class User {
    private String email;                    // Primary identifier
    private String passwordHash;             // BCrypt-hashed password
    private List<String> securityQuestions;  // Q&A for recovery
    private Vault vault;                     // User's vault instance

    public User(String email, String passwordHash, List<String> securityQuestions) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.securityQuestions = securityQuestions;
        this.vault = new Vault();
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<String> getSecurityQuestions() {
        return securityQuestions;
    }

    // Lazy-load vault with user email for database binding
    public Vault getVault() {
        if (vault == null) {
            vault = new Vault(email);
        } else if (vault.getUserEmail() == null) {
            vault.setUserEmail(email);
        }
        return vault;
    }
    
    public void setVault(Vault vault) {
        this.vault = vault;
    }
}
