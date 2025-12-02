package com.mypass;

import java.util.Date;

/**
 * Abstract base class for all vault item types.
 */
public abstract class VaultItem {
    protected String id;           // UUID
    protected String name;         // User-friendly name
    protected Date createdDate;    // When created
    protected Date modifiedDate;   // Last modified
    
    public VaultItem(String name) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.createdDate = new Date();
        this.modifiedDate = new Date();
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.modifiedDate = new Date();
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public Date getModifiedDate() {
        return modifiedDate;
    }
    
    // Package-private setters for database loading
    void setId(String id) {
        this.id = id;
    }
    
    void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    
    // Return item type for DAO routing
    public abstract String getType();
}
