package com.mypass;

import java.util.Date;

/**
 * Vault item for storing login credentials.
 */
public class LoginItem extends VaultItem {
    private String username;   // Login username or email
    private String password;   // Login password (sensitive)
    private String url;        // Website or service URL
    private String notes;      // User notes
    
    public LoginItem(String name, String username, String password, String url) {
        super(name);
        this.username = username;
        this.password = password;
        this.url = url;
        this.notes = "";
    }
    
    // All setters update modifiedDate to track changes
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
        this.modifiedDate = new Date();
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
        this.modifiedDate = new Date();
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
        this.modifiedDate = new Date();
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        this.modifiedDate = new Date();
    }
    
    @Override
    public String getType() {
        return "Login";
    }
}
